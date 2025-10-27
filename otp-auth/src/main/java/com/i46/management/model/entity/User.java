package com.i46.management.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.util.UUID;

@Entity(name = "device_user")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "app_id")
    private String appId;

    @Column(name = "description")
    private String description;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "last_login")
    private Timestamp lastLogin;

    public User() {
    }

    public User(String appId, String description, Timestamp createdAt) {
        this.appId = appId;
        this.description = description;
        this.createdAt = createdAt;
        this.lastLogin = createdAt;
    }

    public User(UUID id, String description, Timestamp lastLogin) {
        this.id = id;
        this.description = description;
        this.lastLogin = lastLogin;
    }
}
