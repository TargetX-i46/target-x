package com.i46.management.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity(name = "storage")
@Data
public class Storage {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Integer id;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "slot")
    private Integer slot;

    @Column(name = "data")
    private String data;

    @Column(name = "n_times")
    private Integer nTimes;

    @Column(name = "remarks")
    private String remarks;


    public Storage() {
    }

    public Storage(UUID userId, Integer slot, String data, Integer nTimes, String remarks) {
        this.userId = userId;
        this.slot = slot;
        this.data = data;
        this.nTimes = nTimes;
        this.remarks = remarks;
    }

}
