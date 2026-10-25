package com.example.e_commerceapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.e_commerceapplication.MainActivity;
import com.example.e_commerceapplication.R;
import com.example.e_commerceapplication.repository.Resource;
import com.example.e_commerceapplication.utils.BiometricHelper;
import com.example.e_commerceapplication.utils.Constants;
import com.example.e_commerceapplication.utils.PreferenceManager;
import com.example.e_commerceapplication.utils.ValidationUtils;
import com.example.e_commerceapplication.viewmodels.AuthViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * LoginActivity allows existing users to sign in with email/password or fingerprint biometric prompt.
 */
public class LoginActivity extends AppCompatActivity {

    private TextInputLayout tilEmail, tilPassword;
    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin, btnBiometric;
    private TextView tvForgotPassword, tvRegister;
    private ProgressBar progressBar;

    private AuthViewModel authViewModel;
    private PreferenceManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        prefManager = new PreferenceManager(this);

        initViews();
        setupListeners();
        checkBiometric();
    }

    private void initViews() {
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnBiometric = findViewById(R.id.btnBiometric);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvRegister = findViewById(R.id.tvRegister);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> performLogin());

        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

        tvForgotPassword.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
        });

        btnBiometric.setOnClickListener(v -> performBiometricLogin());
    }

    private void checkBiometric() {
        if (prefManager.isBiometricEnabled() && BiometricHelper.isBiometricAvailable(this)) {
            btnBiometric.setVisibility(View.VISIBLE);
        } else {
            btnBiometric.setVisibility(View.GONE);
        }
    }

    private void performLogin() {
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

        tilEmail.setError(null);
        tilPassword.setError(null);

        if (!ValidationUtils.isValidEmail(email)) {
            tilEmail.setError("Please enter a valid email address");
            return;
        }

        if (!ValidationUtils.isValidPassword(password)) {
            tilPassword.setError("Password must be at least 8 characters");
            return;
        }

        authViewModel.login(email, password).observe(this, resource -> {
            if (resource.status == Resource.Status.LOADING) {
                progressBar.setVisibility(View.VISIBLE);
                btnLogin.setEnabled(false);
            } else if (resource.status == Resource.Status.SUCCESS) {
                progressBar.setVisibility(View.GONE);
                btnLogin.setEnabled(true);
                String uid = (resource.data != null) ? resource.data.getUid() : "demo_user_123";
                routeUserByRole(uid);
            } else if (resource.status == Resource.Status.ERROR) {
                progressBar.setVisibility(View.GONE);
                btnLogin.setEnabled(true);
                Toast.makeText(this, "Login Failed: " + resource.message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void performBiometricLogin() {
        BiometricHelper.showBiometricPrompt(this, new BiometricHelper.BiometricAuthCallback() {
            @Override
            public void onSuccess() {
                if (authViewModel.getCurrentUser() != null) {
                    routeUserByRole(authViewModel.getCurrentUser().getUid());
                } else {
                    Toast.makeText(LoginActivity.this, "Session expired, please log in with password.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(LoginActivity.this, "Biometric error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void routeUserByRole(String uid) {
        String safeUid = (uid != null) ? uid : "demo_user_123";
        authViewModel.getUserProfile(safeUid).observe(this, resource -> {
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                if (Constants.ROLE_ADMIN.equalsIgnoreCase(resource.data.getRole())) {
                    startActivity(new Intent(LoginActivity.this, AdminDashboardActivity.class));
                } else {
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                }
                finish();
            } else {
                // Default to customer main activity
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }
        });
    }
}