package com.i46.management.model;


import lombok.Data;

import java.util.UUID;

@Data
public class StorageDTO {
    private UUID userId;
    private Integer slot;
    private String data;
    private Integer nTimes;
    private String remarks;

    public StorageDTO(UUID userId, Integer slot, String data, Integer nTimes, String remarks) {
        this.userId = userId;
        this.slot = slot;
        this.data = data;
        this.nTimes = nTimes;
        this.remarks = remarks;
    }
}
