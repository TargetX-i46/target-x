package com.i46.management.model.entity;

import com.i46.management.model.UserDTO;
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

    @Column(name = "provider")
    private String provider;

    @Column(name = "app_id")
    private String appId;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "description")
    private String description;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "last_login")
    private Timestamp lastLogin;

    public User() {
    }

    public User(UserDTO userDTO) {
        this.provider = userDTO.getProvider();
        this.appId = userDTO.getAppId();
        this.name = userDTO.getName();
        this.email = userDTO.getEmail();
        this.description = userDTO.getDescription();
        this.createdAt = userDTO.getCreatedAt();
        this.lastLogin = userDTO.getLastLogin();
    }

}
