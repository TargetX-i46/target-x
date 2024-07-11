package com.i46.otpauth.model;


import lombok.Data;

@Data
public class KeyRequest {
    private String deviceId;
    private String key;

    public KeyRequest(String deviceId, String key) {
        this.deviceId = deviceId;
        this.key = key;
    }
}
