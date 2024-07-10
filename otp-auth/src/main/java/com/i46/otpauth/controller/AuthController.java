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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    DeviceKeyService deviceKeyService;
    @GetMapping("/key")
    public ResponseEntity<Map<String, Object>> getKey(@RequestParam String deviceId) throws InvalidKeyException {
        Map<String, Object> response = new HashMap<>();
        DeviceKey deviceKey = deviceKeyService.get(deviceId);
        response.put("key", deviceKey.getKeyVal());
        TimeBasedOneTimePasswordGenerator totp = new TimeBasedOneTimePasswordGenerator(Duration.ofSeconds(30L), 6, "HmacSHA512");
        Instant now = Instant.now();
        Key originalKey = new SecretKeySpec(deviceKey.getKeyVal().getBytes(), 0, 6, "HmacSHA512");
        String otp = totp.generateOneTimePasswordString(originalKey, now);

        response.put("otp", otp);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/otp/validate")
    public ResponseEntity<Map<String, Object>> validateOtp(@RequestBody KeyRequest keyRequest) throws InvalidKeyException, NoSuchAlgorithmException {
        Map<String, Object> response = new HashMap<>();
        DeviceKey deviceKey = deviceKeyService.get(keyRequest.getDeviceId());
        TimeBasedOneTimePasswordGenerator totp = new TimeBasedOneTimePasswordGenerator(Duration.ofSeconds(30L), 6, "HmacSHA512");

        Key originalKey = new SecretKeySpec(deviceKey.getKeyVal().getBytes(), 0, 6, "HmacSHA512");
        Instant now = Instant.now();
        String otp = totp.generateOneTimePasswordString(originalKey, now);
        if (keyRequest.getPassword().equals(otp)){
            DeviceKey deviceKeyNext = newKey(keyRequest.getDeviceId());
            if (deviceKeyNext != null ){
                response.put("success", deviceKeyNext.getKeyVal());
                return new ResponseEntity<>(response, HttpStatus.CREATED);
            }else{
                response.put("error", "Not saved");
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }else{
            response.put("fail", "Invalid OTP");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/key")
    public ResponseEntity<Map<String, Object>> create(@RequestBody KeyRequest keyRequest) throws NoSuchAlgorithmException {
        Map<String, Object> response = new HashMap<>();
        DeviceKey deviceKey = newKey(keyRequest.getDeviceId());
        if (deviceKey != null ){
            response.put("success", "New key generated");
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }else{
            response.put("error", "Not saved");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    private DeviceKey newKey(String deviceId) throws NoSuchAlgorithmException {
        TimeBasedOneTimePasswordGenerator totp = new TimeBasedOneTimePasswordGenerator(Duration.ofSeconds(30L), 6, "HmacSHA512");
        KeyGenerator keyGenerator = KeyGenerator.getInstance(totp.getAlgorithm());

        // Key length should match the length of the HMAC output (160 bits for SHA-1, 256 bits
        // for SHA-256, and 512 bits for SHA-512). Note that while Mac#getMacLength() returns a
        // length in _bytes,_ KeyGenerator#init(int) takes a key length in _bits._
        int macLengthInBytes = Mac.getInstance(totp.getAlgorithm()).getMacLength();
        keyGenerator.init(macLengthInBytes * 8);

        Key key = keyGenerator.generateKey();
        DeviceKey deviceKey = deviceKeyService.get(deviceId);
        if (deviceKey == null){
            deviceKey = new DeviceKey(deviceId, Hex.encodeHexString(key.getEncoded()));
        }else{
            deviceKey.setKeyVal(Hex.encodeHexString(key.getEncoded()));
        }
        return deviceKeyService.save(deviceKey);
    }
}