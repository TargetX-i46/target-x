package com.i46.management.model.repository;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.i46.management.Commons;
import com.i46.management.model.entity.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.UUID;


@Repository
public class TokenRepository {
    private static final Logger logger = LoggerFactory.getLogger(TokenRepository.class);

    @Autowired
    private Commons commons;

    private static final String METADATA = "/metadata.json";


    public Token save(Token token) {
        token.setId(UUID.randomUUID());

        String tokenDir = commons.HOME_DIRECTORY + token.getAccountId() + "/" + token.getId();
        String metadata = tokenDir +METADATA;
        try {
            // Create the directory, including any necessary but nonexistent parent directories
            Path path = Paths.get(tokenDir);
            Files.createDirectories(path);
            logger.info("Directory created successfully at: " + path.toAbsolutePath());

            //json metadata
            Path filePathMetadata = Paths.get(metadata);
            ObjectMapper mapper = new ObjectMapper();
            mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
            Files.writeString(
                    filePathMetadata,
                    mapper.writeValueAsString(token),
                    StandardOpenOption.CREATE
            );
            logger.info("Content successfully written to " +metadata);


        } catch (IOException e) {
            // Handle exceptions, e.g., permission denied or a file exists with the same name
            logger.error("Failed to create directory: " + tokenDir);
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return token;
    }

}