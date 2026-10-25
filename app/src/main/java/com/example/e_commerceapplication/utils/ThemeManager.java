package com.example.e_commerceapplication.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import androidx.appcompat.app.AppCompatDelegate;

public class ThemeManager {
    public static final int LIGHT = 0;
    public static final int DARK = 1;
    public static final int SYSTEM = 2;

    private static final String PREFS = "shopmpesa_prefs";
    private static final String KEY_THEME = "theme_mode";

    // Read the saved choice (default = follow system)
    public static int getSavedMode(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        return sp.getInt(KEY_THEME, SYSTEM);
    }

    // Save the choice and apply it right away
    public static void setMode(Context context, int mode) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit().putInt(KEY_THEME, mode).apply();
        applyMode(mode);
    }

    // Called once when the app starts
    public static void applySavedTheme(Context context) {
        applyMode(getSavedMode(context));
    }

    private static void applyMode(int mode) {
        switch (mode) {
            case LIGHT:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case DARK:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
    }

    // Is the screen currently dark? (used by the toolbar sun/moon toggle)
    public static boolean isDarkNow(Context context) {
        int flags = context.getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;
        return flags == Configuration.UI_MODE_NIGHT_YES;
    }
}