package com.i46.management.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {
    public FirebaseAuth firebaseAuth;

    @PostConstruct
    public void firestore() throws IOException {
        InputStream serviceAccount = new FileInputStream("/opt/spacebox-d13d5-firebase-adminsdk-fbsvc-9b31b0188a.json");
        //다운받은 비공개 키 이름

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        firebaseAuth = FirebaseAuth.getInstance(FirebaseApp.initializeApp(options));
    }

}


