package com.i46.management.model.repository;


import com.i46.management.Commons;
import com.i46.management.model.entity.Credit;
import com.i46.management.model.entity.Transaction;
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
import java.util.Calendar;


@Repository
public class TransactionRepository {
    private static final Logger logger = LoggerFactory.getLogger(TransactionRepository.class);

    @Autowired
    private Commons commons;

    private static final String CREDITS =  "/credit.csv";
    private static final String TRANSACTIONS =  "/transaction.csv";


    public Transaction save(Transaction transaction, int availableCredit) {
        String newDir = commons.HOME_DIRECTORY + transaction.getAccountId();
        String transactFile = newDir +TRANSACTIONS;
        String creditFile = newDir +CREDITS;
        try {
            // Create the directory, including any necessary but nonexistent parent directories
            Path path = Paths.get(newDir);
            Files.createDirectories(path);
            logger.info("Directory created successfully at: " + path.toAbsolutePath());

            //csv credit
            Calendar cal = Calendar.getInstance();
            Timestamp timestamp = new Timestamp(cal.getTimeInMillis());
            int newBal = availableCredit -  transaction.getCredit();
            Credit credit = new Credit(transaction.getAccountId(), newBal, timestamp);
            Path filePathCredit = Paths.get(creditFile);
            Files.writeString(
                    filePathCredit,
                    credit.toCSVString(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
            logger.info("Content successfully appended to " +creditFile);


            //csv transaction
            Path filePathTransact = Paths.get(transactFile);
            Files.writeString(
                    filePathTransact,
                    transaction.toCSVString(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
            logger.info("Transaction successfully appended to " +transactFile);
        } catch (IOException e) {
            // Handle exceptions, e.g., permission denied or a file exists with the same name
            logger.error("Failed to create directory: " + newDir);
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return transaction;
    }

    public Transaction save(Transaction transaction) {
        String newDir = commons.HOME_DIRECTORY + transaction.getAccountId();
        String transactFile = newDir +TRANSACTIONS;
        try {
            // Create the directory, including any necessary but nonexistent parent directories
            Path path = Paths.get(newDir);
            Files.createDirectories(path);
            logger.info("Directory created successfully at: " + path.toAbsolutePath());

            //csv transaction
            Path filePathTransact = Paths.get(transactFile);
            Files.writeString(
                    filePathTransact,
                    transaction.toCSVString(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
            logger.info("Transaction successfully appended to " +transactFile);
        } catch (IOException e) {
            // Handle exceptions, e.g., permission denied or a file exists with the same name
            logger.error("Failed to create directory: " + newDir);
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return transaction;
    }


}