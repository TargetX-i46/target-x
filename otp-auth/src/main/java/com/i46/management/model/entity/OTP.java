package com.i46.management.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity(name = "otp")
@Data
public class OTP {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Integer id;

    @Column(name = "storage_id")
    private Integer storageId;

    @Column(name = "key")
    private String key;

    @Column(name = "activated_at")
    private Timestamp activatedAt;


    public OTP() {
    }

    public OTP(Integer storageId, String key) {
        this.storageId = storageId;
        this.key = key;
    }
}
