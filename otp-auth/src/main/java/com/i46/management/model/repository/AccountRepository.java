package com.i46.management.model.repository;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.*;
import com.i46.management.model.AccountShortDTO;
import com.i46.management.Commons;
import com.i46.management.model.AccountMetadataDTO;
import com.i46.management.model.entity.Account;
import com.i46.management.model.entity.Credit;
import com.i46.management.model.entity.Transaction;
import com.i46.management.model.entity.TransactionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Stream;


@Repository
public class AccountRepository {
    private static final Logger logger = LoggerFactory.getLogger(AccountRepository.class);

    private static final Random random = new Random();
    private static final int MIN_ID = 0;
    private static final int MAX_ID = 100000000;
    private static final String METADATA = "/metadata.json";
    private static final String USERS_FILE =  "users.csv";
    private static final String CREDITS =  "/credit.csv";
    private static final String TRANSACTIONS =  "/transaction.csv";

    @Autowired
    private Commons commons;

    /**
     * Generates a guaranteed unique 8-digit numeric string
     * The loop ensures no duplicates.
     */
    public String generateUnique8DigitId() {
        String id;
        do {
            int randomNumber = random.nextInt(MAX_ID - MIN_ID) + MIN_ID;
            id = String.format("%08d", randomNumber);
        } while (isAppIdExists(id)); // Check if the ID is already used

        return id;
    }

    public Account save(Account account, int initCredit) {
        account.setId(UUID.randomUUID());
        account.setAppId(generateUnique8DigitId());
        String usersFile = commons.HOME_DIRECTORY + USERS_FILE;

        String accountDir = commons.HOME_DIRECTORY + account.getId();
        String metadata = accountDir +METADATA;
        String creditFile = accountDir +CREDITS;
        String transactFile = accountDir +TRANSACTIONS;
        try {
            // Create the directory, including any necessary but nonexistent parent directories
            Path path = Paths.get(accountDir);
            Files.createDirectories(path);
            logger.info("Directory created successfully at: " + path.toAbsolutePath());

            Path filePath = Paths.get(usersFile);

            AccountShortDTO dto = new AccountShortDTO(account.getId(), account.getAppId(), account.getCreatedAt(), account.isActive());

            //csv user
            Files.writeString(
                    filePath,
                    dto.toCSVString(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
            logger.info("Content successfully appended to " + usersFile);

            //json metadata
            Path filePathMetadata = Paths.get(metadata);
            ObjectMapper mapper = new ObjectMapper();
            mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
            Files.writeString(
                    filePathMetadata,
                    mapper.writeValueAsString(account)
            );
            logger.info("Content successfully written to " +metadata);

            //csv credit
            Calendar cal = Calendar.getInstance();
            Timestamp timestamp = new Timestamp(cal.getTimeInMillis());
            Credit credit = new Credit(account.getId(), initCredit, timestamp);
            Path filePathCredit = Paths.get(creditFile);
            Files.writeString(
                    filePathCredit,
                    credit.toCSVString(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
            logger.info("Content successfully appended to " +creditFile);

            //csv transaction
            Transaction transaction = new Transaction(account.getId(), initCredit, null, TransactionType.POSITIVE.name(), "init credit", null, timestamp);
            Path filePathTransact = Paths.get(transactFile);
            Files.writeString(
                    filePathTransact,
                    transaction.toCSVString(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
            logger.info("Content successfully appended to " +transactFile);

        } catch (IOException e) {
            // Handle exceptions, e.g., permission denied or a file exists with the same name
            logger.error("Failed to create directory: " + accountDir);
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return account;
    }

    public Optional<Account> findByAppId(String appId) {
        String usersFile = commons.HOME_DIRECTORY + USERS_FILE;

        try (Stream<String> lines = Files.lines(Paths.get(usersFile))) {
            // Check if any line in the file contains the search string
            Optional<String> matchedLine = lines
                    .filter(line -> line.contains(","+appId+","))
                    .findFirst();

            if (matchedLine.isPresent()){
                String[] cols = matchedLine.get().split(",");
                UUID id = UUID.fromString(cols[0]);
                String acctDir = commons.HOME_DIRECTORY + id;
                String metadata = acctDir +METADATA;

                // Use try-with-resources to ensure the FileReader is closed automatically
                try (FileReader reader = new FileReader(metadata)) {
                    // Create a Gson instance
                    Gson gson = new Gson();

                    // Use fromJson() to parse the JSON data into a User object
                    AccountMetadataDTO user = gson.fromJson(reader, AccountMetadataDTO.class);

                    Account account = new Account(user);

                    return Optional.of(account);

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        } catch (IOException e) {
            logger.error("An error occurred while reading the file: " + e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<Account> findById(UUID id) {
        String usersFile = commons.HOME_DIRECTORY + USERS_FILE;

        // Use try-with-resources to ensure the stream is closed automatically
        try (Stream<String> lines = Files.lines(Paths.get(usersFile))) {
            // Check if any line in the file contains the search string
            boolean found = lines.anyMatch(line -> line.contains(id.toString()));
            if (found) {
                String acctDir = commons.HOME_DIRECTORY + id;
                String metadata = acctDir +METADATA;

                // Use try-with-resources to ensure the FileReader is closed automatically
                try (FileReader reader = new FileReader(metadata)) {
                    // Create a Gson instance
                    Gson gson = new Gson();

                    // Use fromJson() to parse the JSON data into a User object
                    AccountMetadataDTO user = gson.fromJson(reader, AccountMetadataDTO.class);

                    Account account = new Account(user);

                    return Optional.of(account);

                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {
                logger.info("Account not found");
            }
        } catch (IOException e) {
            logger.error("An error occurred while reading the file: " + e.getMessage());
        }
        return Optional.empty();

    }

    public boolean isAppIdExists(String appId) {
        String usersFile = commons.HOME_DIRECTORY + USERS_FILE;

        try (Stream<String> lines = Files.lines(Paths.get(usersFile))) {
            return lines.anyMatch(line -> line.contains(","+appId+","));
        } catch (IOException e) {
            logger.error("An error occurred while reading the file: " + e.getMessage());
        }
        return false;

    }
}