package com.i46.management.model.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.springframework.beans.factory.annotation.Value;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class IdTokenVerify {
    @Value("${app.google.client.id}")
    private String googleClientId;
    public Map<String, Object> userDetails;

    public boolean validateJwtToken(@RequestBody String idToken) {
        try {
            userDetails = authenticateUser(idToken);
            return userDetails != null;
        } catch (IllegalArgumentException | GeneralSecurityException | IOException e) {
            return false;
        }
    }

    public Map<String, Object> authenticateUser(String idToken) throws GeneralSecurityException, IOException {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(googleClientId))
                .build();

        GoogleIdToken googleIdToken = verifier.verify(idToken);
        if (googleIdToken != null) {
            GoogleIdToken.Payload payload = googleIdToken.getPayload();
            String appId = payload.getSubject();
            String email = payload.getEmail();
            String name = (String) payload.get("name");

            Map<String, Object> userDetails = new HashMap<>();
            userDetails.put("appId", appId);
            userDetails.put("email", email);
            userDetails.put("name", name);

            return userDetails;
        } else {
            throw new IllegalArgumentException("Invalid ID token.");
        }
    }
}