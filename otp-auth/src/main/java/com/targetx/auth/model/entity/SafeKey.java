package com.targetx.auth.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity(name = "safe_key")
@Data
public class SafeKey {
    @Id
    @Column(name = "device_id")
    private UUID deviceId;

    @Column(name = "disk_key")
    private String diskKey;

    @Column(name = "storage_key")
    private String storageKey;

    @Column(name = "encryption_key")
    private String encryptionKey;

    public SafeKey() {
    }

}
