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
import com.example.e_commerceapplication.utils.ValidationUtils;
import com.example.e_commerceapplication.viewmodels.AuthViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * Handles new user registration, validating Full Name, Email, Password, and Kenyan M-Pesa Phone number format.
 */
public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout tilFullName, tilPhone, tilEmail, tilPassword, tilConfirmPassword;
    private TextInputEditText etFullName, etPhone, etEmail, etPassword, etConfirmPassword;
    private MaterialButton btnRegister;
    private TextView tvLogin;
    private ProgressBar progressBar;

    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        initViews();
        setupListeners();
    }

    private void initViews() {
        tilFullName = findViewById(R.id.tilFullName);
        tilPhone = findViewById(R.id.tilPhone);
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);

        etFullName = findViewById(R.id.etFullName);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupListeners() {
        btnRegister.setOnClickListener(v -> performRegister());
        tvLogin.setOnClickListener(v -> finish());
    }

    private void performRegister() {
        String fullName = etFullName.getText() != null ? etFullName.getText().toString().trim() : "";
        String phone = etPhone.getText() != null ? etPhone.getText().toString().trim() : "";
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";
        String confirmPassword = etConfirmPassword.getText() != null ? etConfirmPassword.getText().toString().trim() : "";

        tilFullName.setError(null);
        tilPhone.setError(null);
        tilEmail.setError(null);
        tilPassword.setError(null);
        tilConfirmPassword.setError(null);

        if (fullName.isEmpty()) {
            tilFullName.setError("Please enter your full name");
            return;
        }

        if (!ValidationUtils.isValidKenyanPhone(phone)) {
            tilPhone.setError("Enter valid Kenyan phone (07XXXXXXXX, 01XXXXXXXX, +2547XXXXXXXX)");
            return;
        }

        if (!ValidationUtils.isValidEmail(email)) {
            tilEmail.setError("Please enter a valid email address");
            return;
        }

        if (!ValidationUtils.isValidPassword(password)) {
            tilPassword.setError("Password must be at least 8 characters");
            return;
        }

        if (!password.equals(confirmPassword)) {
            tilConfirmPassword.setError("Passwords do not match");
            return;
        }

        String formattedPhone = ValidationUtils.formatKenyanPhoneTo254(phone);

        authViewModel.register(fullName, email, formattedPhone, password).observe(this, resource -> {
            if (resource.status == Resource.Status.LOADING) {
                progressBar.setVisibility(View.VISIBLE);
                btnRegister.setEnabled(false);
            } else if (resource.status == Resource.Status.SUCCESS) {
                progressBar.setVisibility(View.GONE);
                btnRegister.setEnabled(true);
                Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                if (email.toLowerCase().contains("admin") || (resource.data != null && com.example.e_commerceapplication.utils.Constants.ROLE_ADMIN.equalsIgnoreCase(resource.data.getRole()))) {
                    startActivity(new Intent(RegisterActivity.this, AdminDashboardActivity.class));
                } else {
                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                }
                finishAffinity();
            } else if (resource.status == Resource.Status.ERROR) {
                progressBar.setVisibility(View.GONE);
                btnRegister.setEnabled(true);
                Toast.makeText(this, "Registration Failed: " + resource.message, Toast.LENGTH_LONG).show();
            }
        });
    }
}