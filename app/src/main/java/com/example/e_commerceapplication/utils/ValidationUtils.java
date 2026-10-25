package com.example.e_commerceapplication.utils;

import android.util.Patterns;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

/**
 * Utility functions for validating user inputs and formatting phone numbers to Safaricom Daraja 2547XXXXXXXX standard.
 */
public class ValidationUtils {

    public static boolean isValidEmail(String email) {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches();
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 8;
    }

    /**
     * Validates Kenyan mobile phone numbers starting with 07, 01, +254, or 254.
     */
    public static boolean isValidKenyanPhone(String phone) {
        if (phone == null) return false;
        String clean = phone.trim().replaceAll("\\s+", "");
        return clean.matches("^(?:\\+254|254|0)?([71]\\d{8})$");
    }

    /**
     * Converts any valid Kenyan phone number format into Safaricom Daraja format: 2547XXXXXXXX or 2541XXXXXXXX.
     */
    public static String formatKenyanPhoneTo254(String phone) {
        if (phone == null) return "";
        String clean = phone.trim().replaceAll("[^0-9]", "");
        if (clean.startsWith("0") && clean.length() == 10) {
            return "254" + clean.substring(1);
        } else if (clean.startsWith("254") && clean.length() == 12) {
            return clean;
        }
        return clean;
    }

    /**
     * Generates a unique order reference formatted like ORD-20261019-ABCD.
     */
    public static String generateOrderReference() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.US);
        String datePart = sdf.format(new Date());
        String randomPart = String.format(Locale.US, "%04d", new Random().nextInt(10000));
        return "ORD-" + datePart + "-" + randomPart;
    }
}