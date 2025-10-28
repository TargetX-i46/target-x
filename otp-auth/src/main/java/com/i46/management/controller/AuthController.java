package com.i46.management.controller;

import com.i46.management.model.KeyDTO;
import com.i46.management.model.StorageDTO;
import com.i46.management.model.UserDTO;
import com.i46.management.model.entity.OTP;
import com.i46.management.model.entity.User;
import com.i46.management.model.entity.Storage;
import com.i46.management.model.service.AppService;
import com.i46.management.model.service.OTPService;
import com.i46.management.model.service.UserService;
import com.i46.management.model.service.StorageService;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import javax.crypto.KeyGenerator;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.util.*;

@RestController
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    UserService userService;

    @Autowired
    StorageService storageService;

    @Autowired
    OTPService otpService;

    private final Integer keyBits = 256;


    @Autowired
    private AppService appService;


    @GetMapping("/welcome")
    public String welcome(@AuthenticationPrincipal OAuth2User oauth2User){
        if (oauth2User != null) {
            return "Hello, " + oauth2User.getAttribute("name") + "! Your email is: " + oauth2User.getAttribute("email");
        }
        return "User not authenticated.";
    }

//    @PostMapping("/users")
//    public ResponseEntity<Map<String, Object>> newUser(@AuthenticationPrincipal OAuth2User oauth2User) {
//        Map<String, Object> response = new HashMap<>();
//        Calendar cal = Calendar.getInstance();
//        Timestamp timestamp = new Timestamp(cal.getTimeInMillis());
//
//        UserDTO userDTO = new UserDTO("google", oauth2User.getAttribute("sub"), oauth2User.getAttribute("name"),
//                oauth2User.getAttribute("email"), null, timestamp, timestamp);
//
//        if (userDTO.getAppId() == null || userDTO.getAppId().isEmpty()) {
//            response.put("error", "App ID is required");
//            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
//        }
//
//        User userExisting = userService.getByAppId(userDTO.getAppId());
//        if (userExisting != null){
//            response.put("uuid", userExisting.getId());
//            return new ResponseEntity<>(response, HttpStatus.OK);
//        }
//
//        User user = new User(userDTO);
//        User userSave = userService.save(user);
//
//        response.put("uuid", userSave.getId());
//        response.put("name", userSave.getName());
//        response.put("email", userSave.getEmail());
//        response.put("appId", userSave.getAppId());
//        response.put("token", appService.getJwtToken());
//
//        return new ResponseEntity<>(response, HttpStatus.CREATED);
//
//    }


    @PostMapping("/users")
    public ResponseEntity<Map<String, Object>> newUser(@RequestBody UserDTO userDTO) {
        Map<String, Object> response = new HashMap<>();
        if (userDTO.getAppId() == null || userDTO.getAppId().isEmpty()) {
            response.put("error", "App ID is required");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        User userExisting = userService.getByAppId(userDTO.getAppId());
        if (userExisting != null){
            response.put("uuid", userExisting.getId());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        User user = new User(userDTO);
        User userSave = userService.save(user);

        response.put("uuid", userSave.getId());
        response.put("name", userSave.getName());
        response.put("email", userSave.getEmail());
        response.put("appId", userSave.getAppId());

        return new ResponseEntity<>(response, HttpStatus.CREATED);

    }

    @PostMapping("/storage")
    public ResponseEntity<Map<String, Object>> newStorageItem(@RequestBody StorageDTO storageDTO) throws NoSuchAlgorithmException {
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

        if (storageService.existsStorageByUserIdAndSlot(storageDTO.getUserId(),storageDTO.getSlot())){
            response.put("error", "Slot taken");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }


        Storage storageSave = storageService.save(storage);

        String otp = generateOTP(storageSave.getId());

        response.put("key", otp);
        return new ResponseEntity<>(response, HttpStatus.OK);

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
    public ResponseEntity<Map<String, Object>> validateKey(@RequestBody KeyDTO keyDTO){
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

            if (otpExisting.getActivatedAt() == null){
                Calendar cal = Calendar.getInstance();
                Timestamp timestamp = new Timestamp(cal.getTimeInMillis());
                otpExisting.setActivatedAt(timestamp);
                otpService.save(otpExisting);

                response.put("data", storage.get().getData());
                return new ResponseEntity<>(response, HttpStatus.OK);

            }else{
                response.put("response", "Key already activated");
                response.put("activatedAt", otpExisting.getActivatedAt());
                response.put("remarks", storage.get().getRemarks());
                return new ResponseEntity<>(response, HttpStatus.CONFLICT);
            }

        }


    }
}