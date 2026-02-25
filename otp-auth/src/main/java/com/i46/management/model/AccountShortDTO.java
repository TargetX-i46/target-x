package com.i46.management.model;


import lombok.Data;

import java.sql.Timestamp;
import java.util.UUID;

@Data
public class AccountShortDTO {
    private UUID id;
    private String appId;
    private Timestamp createdAt;
    private boolean active;

    public AccountShortDTO(){

    }

    public AccountShortDTO(UUID id, String appId, Timestamp createdAt, boolean active) {
        this.id= id;
        this.appId = appId;
        this.createdAt = createdAt;
        this.active = active;
    }

    public String toCSVString(){
        return this.id +","+this.appId +"," + this.createdAt +"," + this.active +"\n";
    }

}
