package com.i46.management.model.service;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class IdTokenVerify {
    private static final Logger logger = LoggerFactory.getLogger(IdTokenVerify.class);

    @Value("${app.google.client.id}")
    private String googleClientId;
    public Map<String, Object> userDetails;


    public boolean validateJwtToken(@RequestBody String idToken) {
        try {
            userDetails = authenticateFirebase(idToken);
            return userDetails != null;
        } catch (IllegalArgumentException | GeneralSecurityException | IOException e) {
            return false;
        }
    }

    public Map<String,Object> authenticateFirebase(String idToken) throws IllegalArgumentException, GeneralSecurityException, IOException {

        InputStream serviceAccount = new FileInputStream("/opt/spacebox-d13d5-firebase-adminsdk-fbsvc-e626ba5335.json");
        GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
        // Build FirestoreOptions
        FirestoreOptions firestoreOptions = FirestoreOptions.newBuilder()
                .setCredentials(credentials)
                // Project ID is often inferred, but can be set explicitly
                .setProjectId("spacebox-d13d5")
                .build();

        Firestore db = firestoreOptions.getService();
        // Get a reference to the document
        DocumentReference docRef = db.collection("users").document(idToken);

        // Asynchronously retrieve the document
        ApiFuture<DocumentSnapshot> future = docRef.get();
        Map<String, Object> user = new HashMap<>();
        try {
            // block on response
            DocumentSnapshot document = future.get();
            if (document.exists()) {
                System.out.println("Document data: " + document.getData());

                user.put("appId", document.getString("uid"));
                user.put("email", document.getString("email"));
                user.put("name", document.getString("display_name"));
                user.put("provider", "firebase");
            } else {
                logger.error("No such document!");
            }
        } catch (Exception e) {
            logger.error("Error getting document: " + e);
        }

        return user;
    }

//    public Map<String, Object> authenticateUser(String idToken) throws GeneralSecurityException, IOException {
//        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance())
//                .setAudience(Collections.singletonList(googleClientId))
//                .build();
//
//        GoogleIdToken googleIdToken = verifier.verify(idToken);
//        if (googleIdToken != null) {
//            GoogleIdToken.Payload payload = googleIdToken.getPayload();
//            String appId = payload.getSubject();
//            String email = payload.getEmail();
//            String name = (String) payload.get("name");
//
//            Map<String, Object> userDetails = new HashMap<>();
//            userDetails.put("appId", appId);
//            userDetails.put("email", email);
//            userDetails.put("name", name);
//
//            return userDetails;
//        } else {
//            throw new IllegalArgumentException("Invalid ID token.");
//        }
//
//    }
}