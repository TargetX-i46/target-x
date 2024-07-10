package com.i46.otpauth.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity(name = "device_key")
@Data
public class DeviceKey {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "device_id")
    private String deviceId;

    @Column(name = "key_val")
    private String keyVal;

    public DeviceKey() {
    }

    public DeviceKey(String deviceId, String keyVal) {
        this.deviceId = deviceId;
        this.keyVal = keyVal;
    }
}
