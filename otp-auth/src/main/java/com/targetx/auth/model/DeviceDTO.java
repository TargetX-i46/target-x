package com.targetx.auth.model;


import lombok.Data;

@Data
public class DeviceDTO {
    private String deviceName;
    private String description;

    public DeviceDTO(String deviceName, String description) {
        this.deviceName = deviceName;
        this.description = description;
    }
}
