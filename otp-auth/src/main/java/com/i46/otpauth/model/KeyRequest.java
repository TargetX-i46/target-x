package com.i46.otpauth.model;


import lombok.Data;

@Data
public class KeyRequest {
    private String deviceId;
    private String password;

    public KeyRequest(String deviceId, String password) {
        this.deviceId = deviceId;
        this.password = password;
    }
}
