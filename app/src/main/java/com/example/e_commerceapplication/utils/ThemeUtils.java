package com.example.e_commerceapplication.utils;

import android.content.Context;
import androidx.appcompat.app.AppCompatDelegate;

/**
 * Utility class for switching app theme between Light, Dark, and System Default.
 */
public class ThemeUtils {

    /**
     * Reads saved theme preference and applies it using AppCompatDelegate.
     */
    public static void applySavedTheme(Context context) {
        PreferenceManager pref = new PreferenceManager(context);
        int mode = pref.getThemeMode();
        switch (mode) {
            case PreferenceManager.THEME_MODE_LIGHT:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case PreferenceManager.THEME_MODE_DARK:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case PreferenceManager.THEME_MODE_SYSTEM:
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }

    /**
     * Updates theme preference and applies the new night mode.
     */
    public static void setTheme(Context context, int themeMode) {
        PreferenceManager pref = new PreferenceManager(context);
        pref.setThemeMode(themeMode);
        applySavedTheme(context);
    }
}