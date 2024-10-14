package com.targetx.auth.model;


import lombok.Data;

import java.util.UUID;

@Data
public class KeyRequest {
    private UUID uuid;
    private String key;

    public KeyRequest(UUID uuid, String key) {
        this.uuid = uuid;
        this.key = key;
    }
}
