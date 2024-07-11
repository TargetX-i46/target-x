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

    @Column(name = "seq")
    private Integer seq;

    @Column(name = "device_id")
    private String deviceId;

    @Column(name = "key_val")
    private String keyVal;

    @Column(name = "response_val")
    private String responseVal;

    public DeviceKey() {
    }

    public DeviceKey(String deviceId, Integer seq, String keyVal, String responseVal) {
        this.deviceId = deviceId;
        this.seq = seq;
        this.keyVal = keyVal;
        this.responseVal = responseVal;
    }

}
