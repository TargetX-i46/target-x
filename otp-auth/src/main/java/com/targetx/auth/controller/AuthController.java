package com.targetx.auth.controller;

import com.targetx.auth.model.KeyRequest;
import com.targetx.auth.model.DeviceDTO;
import com.targetx.auth.model.entity.Device;
import com.targetx.auth.model.entity.DeviceKey;
import com.targetx.auth.model.entity.SafeKey;
import com.targetx.auth.model.service.DeviceKeyService;
import com.targetx.auth.model.service.DeviceService;
import com.targetx.auth.model.service.SafeKeyService;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import javax.crypto.KeyGenerator;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.util.*;

@RestController
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    DeviceService deviceService;

    @Autowired
    DeviceKeyService deviceKeyService;

    @Autowired
    SafeKeyService safeKeyService;

    @Value("${targetx.keys.max}")
    private Integer MAX_KEYS;

    private final Integer keyBits = 128;

    @GetMapping("/keys/available")
    public ResponseEntity<Map<String, Object>> getUnusedKeys(@RequestParam UUID uuid) {
        Map<String, Object> response = new HashMap<>();
        DeviceKey deviceKey = deviceKeyService.get(uuid);
        if (deviceKey == null) {
            response.put("error", "Device id does not exist");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        List<DeviceKey> deviceKeys = deviceKeyService.getAllUnused(uuid);
        response.put("count", deviceKeys.size());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/keys/download")
    public ResponseEntity<Resource> getAll(@RequestParam UUID uuid) {
        Optional<Device> optDevice = deviceService.get(uuid);
        if (optDevice.isPresent()){
            Device device = optDevice.get();
            List<DeviceKey> deviceKeys = deviceKeyService.getAll(uuid);
            StringBuilder inputBuffer = new StringBuilder();
            for(DeviceKey key: deviceKeys){
                inputBuffer.append(key.getKeyVal());
                inputBuffer.append('\n');
            }

            try {

                String inputStr = inputBuffer.toString();
                HttpHeaders responseHeaders = new HttpHeaders();
                ContentDisposition contentDisposition = ContentDisposition.builder("inline")
                        .filename("device" + device.getDeviceName() + "_secret-keys.csv")
                        .build();
                responseHeaders.setContentDisposition(contentDisposition);
                InputStream stream = new ByteArrayInputStream(inputStr.getBytes(StandardCharsets.UTF_8));
                InputStreamResource resource = new InputStreamResource(stream);
                return ResponseEntity.ok()
                        .headers(responseHeaders)
                        .contentLength(stream.available())
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .body(resource);
            } catch (IOException e) {
                logger.error(e.getMessage());
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }else{
            logger.error("Device id not found");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    @GetMapping("/diskKey")
    public ResponseEntity<Map<String, Object>> getDiskKey(@RequestParam UUID uuid) {
        Map<String, Object> response = new HashMap<>();
        Optional<SafeKey> optKey = safeKeyService.get(uuid);
        if (optKey.isPresent()){
            response.put("key", optKey.get().getDiskKey());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }else{
            response.put("error", "Device id not found");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

    }

    private SafeKey generateSafeKeys(UUID deviceId, String diskKey, String currentKey) throws NoSuchAlgorithmException{
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");

        SecureRandom secureRandom = new SecureRandom();
        SafeKey safeKey = new SafeKey();
        safeKey.setDeviceId(deviceId);

        if (diskKey == null){
            keyGenerator.init(keyBits, secureRandom);
            Key keyDisk = keyGenerator.generateKey();
            safeKey.setDiskKey(Hex.encodeHexString(keyDisk.getEncoded()));
        }else{
            safeKey.setDiskKey(diskKey);
        }

        if (currentKey == null){
            Key current = keyGenerator.generateKey();
            keyGenerator.init(keyBits, secureRandom);
            safeKey.setCurrentKey(Hex.encodeHexString(current.getEncoded()));
        }else{
            safeKey.setCurrentKey(currentKey);
        }

        Key nextKey = keyGenerator.generateKey();
        keyGenerator.init(keyBits, secureRandom);
        safeKey.setNextKey(Hex.encodeHexString(nextKey.getEncoded()));
        Key encryptionKey = keyGenerator.generateKey();
        keyGenerator.init(keyBits, secureRandom);
        safeKey.setEncryptionKey(Hex.encodeHexString(encryptionKey.getEncoded()));

        return safeKey;
    }

    @PostMapping("/key/validate")
    public ResponseEntity<Map<String, Object>> validateKey(@RequestBody KeyRequest keyRequest) throws NoSuchAlgorithmException {
        Map<String, Object> response = new HashMap<>();
        if (keyRequest.getUuid() == null) {
            response.put("error", "Device id (UUID) is required");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        if (keyRequest.getKey() == null) {
            response.put("error", "Key is required");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        DeviceKey deviceKey = deviceKeyService.get(keyRequest.getUuid());
        if (deviceKey == null) {
            response.put("error", "Device does not exist");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        if (keyRequest.getKey().equals(deviceKey.getKeyVal())) {
            DeviceKey deviceKeyNext = deviceKeyService.getNext(deviceKey.getDeviceId(), deviceKey.getSeq() +1);
            if (deviceKeyNext == null){
                response.put("key", "All keys are used. Contact system administrator");
                return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
            }else{
                deviceKey.setResponseVal(deviceKeyNext.getKeyVal());
                DeviceKey deviceKeySave = deviceKeyService.save(deviceKey);
                Optional<SafeKey> safeKey = safeKeyService.get(deviceKey.getDeviceId());
                if (safeKey.isPresent()) {
                    SafeKey safeKeyResult = safeKey.get();
                    response.put("responseKey", deviceKeySave.getResponseVal());
                    response.put("currentKey", safeKeyResult.getCurrentKey());
                    response.put("nextKey", safeKeyResult.getNextKey());
                    response.put("encryptionKey", safeKeyResult.getEncryptionKey());


                    SafeKey safeKeyNext = generateSafeKeys(safeKeyResult.getDeviceId(), safeKeyResult.getNextKey(), safeKeyResult.getEncryptionKey());
                    safeKeyService.save(safeKeyNext);


                    return new ResponseEntity<>(response, HttpStatus.OK);
                }else{
                    response.put("error", "Key not found");
                    return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
                }

            }

        } else {
            response.put("fail", "Invalid key");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/keys")
    public ResponseEntity<Resource> generateKeys(@RequestBody DeviceDTO deviceDTO) throws NoSuchAlgorithmException {
        if (deviceDTO.getDeviceName() == null) {
            logger.error("Device name is required");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");

            SecureRandom secureRandom = new SecureRandom();

            StringBuilder inputBuffer = new StringBuilder();
            HttpHeaders responseHeaders = new HttpHeaders();
            Calendar cal = Calendar.getInstance();
            Timestamp timestamp = new Timestamp(cal.getTimeInMillis());

            Device device = new Device(deviceDTO.getDeviceName(), deviceDTO.getDescription(), timestamp);
            Device deviceSave = deviceService.save(device);

            SafeKey safeKey = generateSafeKeys(deviceSave.getId(), null, null);

            safeKeyService.save(safeKey);

            for (int i = 1; i <= MAX_KEYS; i++) {
                keyGenerator.init(keyBits, secureRandom);
                Key key = keyGenerator.generateKey();

                DeviceKey deviceKey = new DeviceKey(deviceSave.getId(), i, Hex.encodeHexString(key.getEncoded()), null);
                deviceKeyService.save(deviceKey);

                inputBuffer.append(deviceKey.getKeyVal());
                inputBuffer.append('\n');
            }

            try {

                String inputStr = inputBuffer.toString();

                ContentDisposition contentDisposition = ContentDisposition.builder("inline")
                        .filename("device" + deviceDTO.getDeviceName() + "_secret-keys.csv")
                        .build();
                responseHeaders.setContentDisposition(contentDisposition);
                InputStream stream = new ByteArrayInputStream(inputStr.getBytes(StandardCharsets.UTF_8));
                InputStreamResource resource = new InputStreamResource(stream);
                return ResponseEntity.ok()
                        .headers(responseHeaders)
                        .contentLength(stream.available())
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .body(resource);
            } catch (IOException e) {
                logger.error(e.getMessage());
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }

        }
}