package com.targetx.auth.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity(name = "disk_mount_key")
@Data
public class DiskMountKey {
    @Id
    @Column(name = "deviceId")
    private UUID deviceId;

    @Column(name = "disk_key")
    private String diskKey;

    public DiskMountKey() {
    }

}
