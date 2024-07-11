/* Copyright (c) 2016 Jon Chambers
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE. */

package com.i46.otpauth.controller;

import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator;
import com.i46.otpauth.model.KeyRequest;
import com.i46.otpauth.model.entity.DeviceKey;
import com.i46.otpauth.model.service.DeviceKeyService;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    DeviceKeyService deviceKeyService;

    @GetMapping("/keys/available")
    public ResponseEntity<Map<String, Object>> getUnusedKeys(@RequestParam String deviceId) {
        Map<String, Object> response = new HashMap<>();
        List<DeviceKey> deviceKeys = deviceKeyService.getAll(deviceId);
        response.put("count", deviceKeys.size());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/key/validate")
    public ResponseEntity<Map<String, Object>> validateKey(@RequestBody KeyRequest keyRequest) {
        Map<String, Object> response = new HashMap<>();
        if (keyRequest.getDeviceId() == null) {
            response.put("error", "Device id is required");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        if (keyRequest.getKey() == null) {
            response.put("error", "Key is required");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        DeviceKey deviceKey = deviceKeyService.get(keyRequest.getDeviceId());
        if (deviceKey == null) {
            response.put("error", "Device id does not exist");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        if (keyRequest.getKey().equals(deviceKey.getKeyVal())) {
            DeviceKey deviceKeyNext = deviceKeyService.getNext(deviceKey.getDeviceId(), deviceKey.getSeq() +1);
            if (deviceKeyNext == null){
                response.put("key", "All keys are used. Contact system administrator");
                return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
            }else{
                deviceKey.setResponseVal(deviceKeyNext.getKeyVal());
                deviceKey = deviceKeyService.save(deviceKey);
                response.put("key", deviceKey.getResponseVal());
                return new ResponseEntity<>(response, HttpStatus.OK);
            }

        } else {
            response.put("fail", "Invalid key");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/keys")
    public ResponseEntity<Resource> generateKeys(@RequestBody KeyRequest keyRequest) throws NoSuchAlgorithmException {
        if (keyRequest.getDeviceId() == null) {
            logger.error("Device id is required");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (deviceKeyService.existsByDeviceId(keyRequest.getDeviceId())) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } else {
            TimeBasedOneTimePasswordGenerator totp = new TimeBasedOneTimePasswordGenerator(Duration.ofSeconds(30L), 6, "HmacSHA512");
            StringBuilder inputBuffer = new StringBuilder();
            HttpHeaders responseHeaders = new HttpHeaders();

            for (int i = 1; i <= 1000; i++) {
                KeyGenerator keyGenerator = KeyGenerator.getInstance(totp.getAlgorithm());

                // Key length should match the length of the HMAC output (160 bits for SHA-1, 256 bits
                // for SHA-256, and 512 bits for SHA-512). Note that while Mac#getMacLength() returns a
                // length in _bytes,_ KeyGenerator#init(int) takes a key length in _bits._
                int macLengthInBytes = Mac.getInstance(totp.getAlgorithm()).getMacLength();
                keyGenerator.init(macLengthInBytes * 8);

                Key key = keyGenerator.generateKey();

                DeviceKey deviceKey = new DeviceKey(keyRequest.getDeviceId(), i, Hex.encodeHexString(key.getEncoded()), null);

                deviceKeyService.save(deviceKey);

                inputBuffer.append(deviceKey.getKeyVal()).append(",0");
                inputBuffer.append('\n');
            }

            try {

                String inputStr = inputBuffer.toString();

                ContentDisposition contentDisposition = ContentDisposition.builder("inline")
                        .filename("device" + keyRequest.getDeviceId() +"-secret-keys.csv")
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


}