package com.i46.management.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Id;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Data
public class Token {
    @Id
    private UUID id;
    private UUID accountId;
    private Integer tokenTypeId;
    private String name;
    private String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp createdAt;
    private Integer accessTypeId;
    private List<UUID> accessUsers;

    public Token() {
    }

    public Token(UUID accountId, Integer tokenTypeId, String name, String description, Timestamp createdAt, Integer accessTypeId, List<UUID> accessUsers) {
        this.accountId = accountId;
        this.tokenTypeId = tokenTypeId;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
        this.accessTypeId = accessTypeId; //AccessType.WRITE_MANY_READ_MANY.getId();
        this.accessUsers = accessUsers;
    }

}
