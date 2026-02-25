package com.i46.management.model.entity;

import lombok.Data;

import java.sql.Timestamp;
import java.util.UUID;

@Data
public class Transaction {
    private UUID accountId;
    private Timestamp date;
    private Integer credit;
    private UUID token;
    private String type; //positive / negative
    private String remarks;
    private String referenceNo;

    public Transaction() {
    }

    public Transaction(UUID accountId, Integer credit, UUID token, String type, String remarks, String referenceNo, Timestamp date) {
        this.accountId = accountId;
        this.credit = credit;
        this.token = token;
        this.type = type;
        this.remarks = remarks;
        this.referenceNo = referenceNo;
        this.date = date;

    }

    public String toCSVString(){
        return this.accountId +","+ this.date +","+ this.credit +"," +this.token +"," +this.type +"," + this.remarks +"," + this.referenceNo +"," +"\n";
    }

}
