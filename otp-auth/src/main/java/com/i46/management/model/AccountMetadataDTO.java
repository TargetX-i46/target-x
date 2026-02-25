package com.i46.management.model;

import lombok.Data;


@Data
public class AccountMetadataDTO {
    private String id;
    private String name;
    private String appId;
    private String channel;
    private String provider;
    private String description;
    private String location;
    private String createdAt;
    private String createdBy;
    private String lastLogin;
    private String[] contacts;
    private boolean active;
    private String activatedAt;
    private String deactivatedAt;

    public AccountMetadataDTO(){

    }


}
