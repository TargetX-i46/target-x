package com.i46.management.controller;

import com.i46.management.model.ContactDTO;
import com.i46.management.model.entity.Account;
import com.i46.management.model.service.AccountService;
import com.i46.management.model.service.ContactService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
public class ContactController {
    private static final Logger logger = LoggerFactory.getLogger(ContactController.class);

    @Autowired
    ContactService contactService;

    @Autowired
    private AccountService accountService;


    @PostMapping("/contact")
    public ResponseEntity<Map<String, Object>> add(@RequestBody ContactDTO contactDTO) {
        Map<String, Object> response = new HashMap<>();

        if (contactDTO.getAccountId() == null) {
            response.put("error", "Account id is required");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        if (contactDTO.getContactAccountId() == null) {
            response.put("error", "Contact account id is required");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }


        Optional<Account> accountInviter = accountService.getById(contactDTO.getAccountId());
        Optional<Account> accountInvitee = accountService.getById(contactDTO.getContactAccountId());

        if (accountInviter.isPresent() && accountInvitee.isPresent()) {
            Account accountSave = contactService.save(accountInviter.get(), contactDTO.getContactAccountId());

            if (accountSave != null) {
                Account accountContactSave = contactService.save(accountInvitee.get(), contactDTO.getAccountId());
                if (accountContactSave != null){
                    response.put("data", accountSave);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
            }
        }

        response.put("error", "An unexpected error occurred");
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);

    }


}