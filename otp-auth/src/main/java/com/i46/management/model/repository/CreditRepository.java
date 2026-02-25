package com.i46.management.model.repository;


import com.i46.management.Commons;
import org.apache.commons.io.input.ReversedLinesFileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.UUID;


@Repository
public class CreditRepository {
    private static final Logger logger = LoggerFactory.getLogger(CreditRepository.class);

    @Autowired
    private Commons commons;

    private static final String CREDITS =  "/credit.csv";


    public int findCreditById(UUID id) {
        String filePath = commons.HOME_DIRECTORY + id.toString() +CREDITS;

        Path path = Path.of(filePath);
        try (ReversedLinesFileReader reader = ReversedLinesFileReader.builder()
                .setPath(path)
                .setBufferSize(4096)
                .setCharset(StandardCharsets.UTF_8)
                .get()) {

            String line;
            if ((line = reader.readLine()) != null) {
                String[] cols = line.split(",");
                return Integer.parseInt(cols[1]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }
}