package com.i46.management.model;


import lombok.Data;

import java.util.UUID;

@Data
public class TransferCreditDTO {
    private UUID sourceAccountId;
    private UUID destinationAccountId;
    private Integer credit;

    public TransferCreditDTO(){

    }

}
