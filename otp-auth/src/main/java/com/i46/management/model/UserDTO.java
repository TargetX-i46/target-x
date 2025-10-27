package com.i46.management.model;


import lombok.Data;

@Data
public class UserDTO {
    private String appId;
    private String description;

    public UserDTO(String appId, String description) {
        this.appId = appId;
        this.description = description;
    }
}
