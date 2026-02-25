package com.i46.management.model.repository;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.i46.management.Commons;
import com.i46.management.model.entity.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Repository
public class ContactRepository {
    private static final Logger logger = LoggerFactory.getLogger(ContactRepository.class);

    @Autowired
    private Commons commons;

    private static final String METADATA = "/metadata.json";

    public Account save(Account account, UUID contactAccountId) {
        String accountDir = commons.HOME_DIRECTORY + account.getId();
        String metadata = accountDir +METADATA;
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            File file = new File(metadata);
            Account accountObj = objectMapper.readValue(file, Account.class);
            accountObj.setLastLogin(new Timestamp(System.currentTimeMillis()));
            if (accountObj.getContacts() == null){
                List<UUID> contactIds = new ArrayList<>();
                contactIds.add(contactAccountId);
                accountObj.setContacts(contactIds);
            }else{
                accountObj.getContacts().add(contactAccountId);
            }

            //json metadata
            Path filePathMetadataInviter = Paths.get(metadata);
            ObjectMapper mapper = new ObjectMapper();
            mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
            Files.writeString(
                    filePathMetadataInviter,
                    mapper.writeValueAsString(accountObj));
            logger.info("New contact added to " +metadata);

            return accountObj;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}