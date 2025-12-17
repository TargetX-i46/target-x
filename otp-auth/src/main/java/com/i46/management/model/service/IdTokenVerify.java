package com.i46.management.model.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.cloud.FirestoreClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;


import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.concurrent.ExecutionException;

@Service
public class IdTokenVerify {
    private static final Logger logger = LoggerFactory.getLogger(IdTokenVerify.class);

    public Map<String, Object> userDetails;


    public boolean validateJwtToken(@RequestBody String idToken) {
        try {
            userDetails = authenticateFirebase(idToken);
            return userDetails != null;
        } catch (IllegalArgumentException | GeneralSecurityException | IOException | FirebaseAuthException |
                 ExecutionException | InterruptedException e) {
            return false;
        }
    }

    public Map<String, Object> authenticateFirebase(String idToken) throws IllegalArgumentException, GeneralSecurityException, IOException, FirebaseAuthException, ExecutionException, InterruptedException {
        Firestore firestore = FirestoreClient.getFirestore();
        FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdTokenAsync(idToken).get();
            // Get the user details (e.g., UID, email) from the token
        String uid = decodedToken.getUid();

        // Get a reference to the document
        DocumentReference docRef = firestore.collection("users").document(uid);

        // Asynchronously retrieve the document
        ApiFuture<DocumentSnapshot> future = docRef.get();

        // block on response
        DocumentSnapshot document = future.get();
        Map<String, Object> user = new HashMap<>();
        if (document.exists()) {
            logger.info("Document data: " + document.getData());

            user.put("appId", document.getString("uid"));
            user.put("email", document.getString("email"));
            user.put("name", document.getString("display_name"));
            user.put("provider", "firebase");
        } else {
            logger.error("No such document!");
        }


        return user;
    }

}