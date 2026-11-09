package com.example.e_commerceapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.e_commerceapplication.MainActivity;
import com.example.e_commerceapplication.R;
import com.example.e_commerceapplication.repository.Resource;
import com.example.e_commerceapplication.utils.Constants;
import com.example.e_commerceapplication.viewmodels.AuthViewModel;
import com.google.firebase.auth.FirebaseUser;

/**
 * Splash screen activity that verifies login status and routes user instantly based on role.
 */
public class SplashActivity extends AppCompatActivity {

    private AuthViewModel authViewModel;
    private boolean isNavigated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        // Fast splash launch (400ms) for instant start
        new Handler(Looper.getMainLooper()).postDelayed(this::checkUserSession, 400);
    }

    private void checkUserSession() {
        if (isNavigated) return;

        try {
            FirebaseUser currentUser = authViewModel.getCurrentUser();
            if (currentUser == null) {
                navigateTo(LoginActivity.class);
            } else {
                authViewModel.getUserProfile(currentUser.getUid()).observe(this, resource -> {
                    if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                        if (Constants.ROLE_ADMIN.equalsIgnoreCase(resource.data.getRole())) {
                            navigateTo(AdminDashboardActivity.class);
                        } else {
                            navigateTo(MainActivity.class);
                        }
                    } else if (resource.status == Resource.Status.ERROR) {
                        navigateTo(MainActivity.class);
                    }
                });

                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    if (!isNavigated) {
                        navigateTo(LoginActivity.class);
                    }
                }, 1000);
            }
        } catch (Exception e) {
            navigateTo(LoginActivity.class);
        }
    }

    private void navigateTo(Class<?> targetClass) {
        if (!isNavigated) {
            isNavigated = true;
            Intent intent = new Intent(SplashActivity.this, targetClass);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        }
    }
}