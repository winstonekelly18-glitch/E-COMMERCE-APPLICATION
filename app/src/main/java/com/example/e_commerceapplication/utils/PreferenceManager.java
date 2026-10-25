package com.example.e_commerceapplication.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Helper class for persisting key-value settings in SharedPreferences.
 * Used for theme selection (Light/Dark/System), user profile cache, and app settings.
 */
public class PreferenceManager {

    private static final String PREF_NAME = "shop_mpesa_prefs";
    private static final String KEY_THEME_MODE = "theme_mode";
    private static final String KEY_BIOMETRIC_ENABLED = "biometric_enabled";

    // Theme constants
    public static final int THEME_MODE_SYSTEM = 0;
    public static final int THEME_MODE_LIGHT = 1;
    public static final int THEME_MODE_DARK = 2;

    private final SharedPreferences preferences;

    public PreferenceManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void setThemeMode(int mode) {
        preferences.edit().putInt(KEY_THEME_MODE, mode).apply();
    }

    public int getThemeMode() {
        return preferences.getInt(KEY_THEME_MODE, THEME_MODE_SYSTEM);
    }

    public void setBiometricEnabled(boolean enabled) {
        preferences.edit().putBoolean(KEY_BIOMETRIC_ENABLED, enabled).apply();
    }

    public boolean isBiometricEnabled() {
        return preferences.getBoolean(KEY_BIOMETRIC_ENABLED, false);
    }
}