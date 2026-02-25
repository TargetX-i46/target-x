package com.i46.management.controller;

import com.i46.management.model.TransferCreditDTO;
import com.i46.management.model.entity.*;
import com.i46.management.model.service.CreditService;
import com.i46.management.model.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.*;

@RestController
public class CreditController {
    private static final Logger logger = LoggerFactory.getLogger(CreditController.class);

    @Autowired
    TransactionService transactionService;

    @Autowired
    private CreditService creditService;


    @PostMapping("/credit")
    public ResponseEntity<Map<String, Object>> transfer(@RequestBody TransferCreditDTO transferCreditDTO) {
        Map<String, Object> response = new HashMap<>();

        if (transferCreditDTO.getSourceAccountId() == null) {
            response.put("error", "Source account id is required");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        if (transferCreditDTO.getDestinationAccountId() == null) {
            response.put("error", "Destination account id is required");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        if (transferCreditDTO.getCredit() == null) {
            response.put("error", "Credit amount is required");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        Calendar cal = Calendar.getInstance();
        Timestamp timestamp = new Timestamp(cal.getTimeInMillis());

        //check if destination is in source's contact list


        //check if source has credits left
        int creditAvailable = creditService.getCredit(transferCreditDTO.getSourceAccountId());
        if (creditAvailable >= transferCreditDTO.getCredit()){
            //add transaction to source - negative type
            Transaction transactionSrc = new Transaction(transferCreditDTO.getSourceAccountId(), transferCreditDTO.getCredit(), null, TransactionType.NEGATIVE.name(), "minus credit for transfer token", null, timestamp);
            Transaction tSrcSave = transactionService.save(transactionSrc, creditAvailable);

            if (tSrcSave != null){
                //add transaction to destination - positive type
                Transaction transactionDest = new Transaction(transferCreditDTO.getSourceAccountId(), transferCreditDTO.getCredit(), null, TransactionType.POSITIVE.name(), "plus credit for receiving token", null, timestamp);
                Transaction tDestSave = transactionService.save(transactionDest, creditAvailable);

                if (tDestSave != null){
                    response.put("status", "Success");
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
            }

        }else{
            response.put("error", "Insufficient credit");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        response.put("error", "An unexpected error occurred");
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);

    }




}