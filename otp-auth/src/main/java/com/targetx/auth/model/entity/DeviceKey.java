package com.targetx.auth.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity(name = "device_key")
@Data
public class DeviceKey {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "device_id")
    private UUID deviceId;

    @Column(name = "seq")
    private Integer seq;

    @Column(name = "key_val")
    private String keyVal;

    @Column(name = "response_val")
    private String responseVal;

    public DeviceKey() {
    }

    public DeviceKey(UUID deviceId, Integer seq, String keyVal, String responseVal) {
        this.deviceId = deviceId;
        this.seq = seq;
        this.keyVal = keyVal;
        this.responseVal = responseVal;
    }

}
