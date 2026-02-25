package com.i46.management.controller;

import com.i46.management.model.KeyDTO;
import com.i46.management.model.StorageDTO;
import com.i46.management.model.entity.OTP;
import com.i46.management.model.entity.Storage;
import com.i46.management.model.entity.User;
import com.i46.management.model.service.IdTokenVerify;
import com.i46.management.model.service.OTPService;
import com.i46.management.model.service.StorageService;
import com.i46.management.model.service.UserService;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.KeyGenerator;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
public class TokenController {
    private static final Logger logger = LoggerFactory.getLogger(TokenController.class);
    @Autowired
    UserService userService;

    @Autowired
    StorageService storageService;

    @Autowired
    OTPService otpService;

    @Autowired
    private IdTokenVerify idTokenVerify;

    private final Integer keyBits = 256;


//    @GetMapping("/welcome")
//    public String welcome(){
//        return "User authenticated.";
//    }

    @PostMapping("/storage")
    public ResponseEntity<Map<String, Object>> newStorageItem(@RequestBody StorageDTO storageDTO) throws NoSuchAlgorithmException {
        Optional<User> user = userService.getByAppId(idTokenVerify.userDetails.get("appId").toString());
        user.ifPresent(value -> storageDTO.setUserId(value.getId()));

        Map<String, Object> response = new HashMap<>();
        if (storageDTO.getUserId()== null) {
            response.put("error", "User ID is required");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        if (storageDTO.getSlot()== null) {
            response.put("error", "Slot number is required. Ex. 1");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        if (storageDTO.getData()== null || storageDTO.getData().isEmpty()) {
            response.put("error", "Data is required. Enter the data you want to store.");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        if (storageDTO.getNTimes()== null) {
            storageDTO.setNTimes(1);
        }

        Storage storage = new Storage(storageDTO.getUserId(), storageDTO.getSlot(), storageDTO.getData(), storageDTO.getNTimes(), storageDTO.getRemarks());
        Storage storageSave;

        if (storageService.existsStorageByUserIdAndSlot(storageDTO.getUserId(),storageDTO.getSlot())){
            //response.put("error", "Slot taken");
           // return new ResponseEntity<>(response, HttpStatus.CONFLICT);
            Storage existing = storageService.findByUserIdAndSlot(storageDTO.getUserId(),storageDTO.getSlot());
            existing.setData(storageDTO.getData());
            existing.setNTimes(storageDTO.getNTimes());
            existing.setRemarks(storageDTO.getRemarks());
            storageSave = storageService.save(existing);

            otpService.deleteByStorageId(existing.getId());

            String otp = generateOTP(storageSave.getId());

            response.put("key", otp);
        }else {
            storageSave = storageService.save(storage);
            String otp = generateOTP(storageSave.getId());

            response.put("key", otp);
        }
        return new ResponseEntity<>(response, HttpStatus.CREATED);


    }


    private String generateOTP(Integer storageId) throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        SecureRandom secureRandom = new SecureRandom();

        keyGenerator.init(keyBits, secureRandom);
        Key key = keyGenerator.generateKey();

        OTP otp = new OTP(storageId, Hex.encodeHexString(key.getEncoded()));

        OTP otpSave = otpService.save(otp);
        return otpSave.getKey();

    }

    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateKey(@RequestBody KeyDTO keyDTO) {
        Optional<User> user = userService.getByAppId(idTokenVerify.userDetails.get("appId").toString());
        user.ifPresent(value -> keyDTO.setUserId(value.getId()));

        Map<String, Object> response = new HashMap<>();
        if (keyDTO.getUserId()== null) {
            response.put("error", "User ID is required");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        if (keyDTO.getKey()== null) {
            response.put("error", "Key is required");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        OTP otpExisting = otpService.getByKey(keyDTO.getKey());
        if (otpExisting == null) {
            response.put("error", "Key not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }else{
            Optional<Storage> storage = storageService.get(otpExisting.getStorageId());
            if (storage.isEmpty()) {
                response.put("error", "Storage data not found");
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            if (otpExisting.getActivatedAt() == null || otpExisting.getNTimesActivated() < storage.get().getNTimes()){
                Calendar cal = Calendar.getInstance();
                Timestamp timestamp = new Timestamp(cal.getTimeInMillis());
                otpExisting.setActivatedAt(timestamp);
                otpExisting.setNTimesActivated(otpExisting.getNTimesActivated() + 1);
                otpService.save(otpExisting);

                //new otp
                //String otp = generateOTP(storage.get().getId());

                response.put("data", storage.get().getData());
                response.put("remarks", storage.get().getRemarks());
                return new ResponseEntity<>(response, HttpStatus.OK);

            }else{
                response.put("error", "This token has already been validated.");
                response.put("activatedAt", otpExisting.getActivatedAt());
                response.put("remarks", storage.get().getRemarks());
                return new ResponseEntity<>(response, HttpStatus.CONFLICT);
            }

        }


    }
}