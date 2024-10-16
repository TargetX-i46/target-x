package com.targetx.auth.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity(name = "safe_key")
@Data
public class SafeKey {
    @Id
    @Column(name = "deviceId")
    private UUID deviceId;

    @Column(name = "disk_key")
    private String diskKey;

    @Column(name = "current_key")
    private String currentKey;

    @Column(name = "next_key")
    private String nextKey;

    @Column(name = "encryption_key")
    private String encryptionKey;

    public SafeKey() {
    }

}
