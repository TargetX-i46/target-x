package com.i46.management.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.i46.management.model.AccountDTO;
import com.i46.management.model.AccountMetadataDTO;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Data
public class Account {
    @Id
    private UUID id;
    private String provider;
    private String appId;
    private String name;
    private String channel;
    private String description;
    private String location;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Timestamp createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Timestamp lastLogin;
    private boolean active;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Timestamp activatedAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Timestamp deactivatedAt;
    private UUID createdBy;
    private List<UUID> contacts;

    public Account() {
    }

    public Account(AccountDTO accountDTO) {
        this.provider = accountDTO.getProvider();
        this.appId = accountDTO.getAppId();
        this.name = accountDTO.getName();
        this.channel = accountDTO.getChannel();
        this.description = accountDTO.getDescription();
        this.location = accountDTO.getLocation();
        this.createdAt = accountDTO.getCreatedAt();
        this.lastLogin = accountDTO.getLastLogin();
        this.active = accountDTO.isActive();
        this.activatedAt = accountDTO.getActivatedAt();
        this.deactivatedAt = accountDTO.getDeactivatedAt();
        this.createdBy = accountDTO.getCreatedBy();
        this.contacts = accountDTO.getContacts();
    }

    public Account(AccountMetadataDTO accountDTO) {
        this.id = UUID.fromString(accountDTO.getId());
        this.provider = accountDTO.getProvider();
        this.appId = accountDTO.getAppId();
        this.name = accountDTO.getName();
        this.channel = accountDTO.getChannel();
        this.description = accountDTO.getDescription();
        this.location = accountDTO.getLocation();
        if (accountDTO.getCreatedAt() != null){
            LocalDateTime localDateTime = LocalDateTime.parse(accountDTO.getCreatedAt()); // LocalDateTime.parse expects ISO format with 'T'
            this.createdAt = Timestamp.valueOf(localDateTime);
        }
        if (accountDTO.getLastLogin() != null){
            LocalDateTime localDateTime = LocalDateTime.parse(accountDTO.getLastLogin()); // LocalDateTime.parse expects ISO format with 'T'
            this.lastLogin = Timestamp.valueOf(localDateTime);
        }
        this.active = accountDTO.isActive();
        if (accountDTO.getActivatedAt() != null){
            LocalDateTime localDateTime = LocalDateTime.parse(accountDTO.getActivatedAt()); // LocalDateTime.parse expects ISO format with 'T'
            this.activatedAt = Timestamp.valueOf(localDateTime);
        }
        if (accountDTO.getDeactivatedAt() != null){
            LocalDateTime localDateTime = LocalDateTime.parse(accountDTO.getDeactivatedAt()); // LocalDateTime.parse expects ISO format with 'T'
            this.deactivatedAt = Timestamp.valueOf(localDateTime);
        }
        if (accountDTO.getCreatedBy() != null) {
            this.createdBy = UUID.fromString(accountDTO.getCreatedBy());
        }
        if (accountDTO.getContacts() != null) {
            List<UUID> result = Arrays.stream(accountDTO.getContacts()).map(UUID::fromString).toList();
            this.contacts = new ArrayList<>(result);

        }

    }



}
