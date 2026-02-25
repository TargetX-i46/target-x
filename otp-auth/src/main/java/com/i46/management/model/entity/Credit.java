package com.i46.management.model.entity;

import lombok.Data;

import java.sql.Timestamp;
import java.util.UUID;

@Data
public class Credit {
    private UUID accountId;
    private Integer credit;
    private Timestamp transactionDate;

    public Credit() {
    }

    public Credit(UUID accountId, Integer credit, Timestamp lastUpdated) {
        this.accountId = accountId;
        this.credit = credit;
        this.transactionDate = lastUpdated;
    }

    public String toCSVString(){
        return this.accountId +","+ this.credit +"," +this.transactionDate +"\n";
    }

}
