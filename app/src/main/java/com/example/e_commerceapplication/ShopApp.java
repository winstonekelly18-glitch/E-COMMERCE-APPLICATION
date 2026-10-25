package com.example.e_commerceapplication;

import android.app.Application;
import com.example.e_commerceapplication.utils.ThemeManager;
import com.google.firebase.FirebaseApp;

public class ShopApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Load the saved theme BEFORE any screen opens
        ThemeManager.applySavedTheme(this);

        // Safely initialize FirebaseApp
        try {
            if (FirebaseApp.getApps(this).isEmpty()) {
                FirebaseApp.initializeApp(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}