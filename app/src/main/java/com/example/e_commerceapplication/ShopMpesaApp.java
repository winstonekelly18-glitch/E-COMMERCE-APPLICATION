package com.example.e_commerceapplication;

import android.app.Application;
import com.example.e_commerceapplication.utils.ThemeUtils;
import com.google.firebase.FirebaseApp;

/**
 * ShopMpesaApp is the custom Application class.
 * It initializes key application settings such as Dark/Light theme mode
 * and Firebase before any Activity or Screen is inflated.
 */
public class ShopMpesaApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Apply saved theme preference (Light, Dark, or System Default)
        ThemeUtils.applySavedTheme(this);

        // Initialize FirebaseApp safely
        try {
            if (FirebaseApp.getApps(this).isEmpty()) {
                FirebaseApp.initializeApp(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}