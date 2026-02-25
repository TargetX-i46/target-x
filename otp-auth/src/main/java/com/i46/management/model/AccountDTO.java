package com.i46.management.model;


import lombok.Data;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Data
public class AccountDTO {
    private String appId;
    private String name;
    private String channel;
    private String provider;
    private String description;
    private String location;
    private Timestamp createdAt;
    private Timestamp lastLogin;
    private boolean active;
    private Timestamp activatedAt;
    private Timestamp deactivatedAt;
    private UUID createdBy;
    private List<UUID> contacts;

    public AccountDTO(){

    }

}
