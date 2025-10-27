package com.i46.management.model;


import lombok.Data;

import java.sql.Timestamp;

@Data
public class UserDTO {
    private String provider;
    private String appId;
    private String name;
    private String email;
    private String description;
    private Timestamp createdAt;
    private Timestamp lastLogin;

    public UserDTO(){

    }

    public UserDTO(String provider, String appId, String name, String email, String description, Timestamp createdAt, Timestamp lastLogin) {
        this.provider = provider;
        this.appId = appId;
        this.name = name;
        this.email = email;
        this.description = description;
        this.createdAt = new Timestamp(System.currentTimeMillis());
        this.lastLogin = new Timestamp(System.currentTimeMillis());
    }
}
