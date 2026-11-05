package com.example.e_commerceapplication;

import android.app.Application;
import com.example.e_commerceapplication.utils.ThemeManager;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.PersistentCacheSettings;

public class ShopApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Load the saved theme BEFORE any screen opens
        ThemeManager.applySavedTheme(this);

        // Safely initialize FirebaseApp with offline disk persistence for instant loads
        try {
            if (FirebaseApp.getApps(this).isEmpty()) {
                FirebaseApp.initializeApp(this);
            }
            FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                    .setLocalCacheSettings(PersistentCacheSettings.newBuilder().build())
                    .build();
            FirebaseFirestore.getInstance().setFirestoreSettings(settings);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}