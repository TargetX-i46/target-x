package com.i46.management.model;


import lombok.Data;

import java.util.UUID;

@Data
public class ContactDTO {
    private UUID accountId;
    private UUID contactAccountId;

    public ContactDTO(){

    }

}
