package com.i46.management.model;


import lombok.Data;

import java.util.UUID;

@Data
public class KeyDTO {
    private UUID userId;
    private String key;

    public KeyDTO(UUID userId, String key) {
        this.userId = userId;
        this.key = key;
    }
}
