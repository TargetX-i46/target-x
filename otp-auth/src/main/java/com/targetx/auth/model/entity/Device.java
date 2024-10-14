package com.targetx.auth.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.util.UUID;

@Entity(name = "device")
@Data
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "device_name")
    private String deviceName;

    @Column(name = "description")
    private String description;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "last_login")
    private Timestamp lastLogin;

    public Device() {
    }

    public Device(String deviceName, String description, Timestamp createdAt) {
        this.deviceName = deviceName;
        this.description = description;
        this.createdAt = createdAt;
    }

}
