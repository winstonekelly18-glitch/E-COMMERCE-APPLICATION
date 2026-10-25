package com.example.e_commerceapplication.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.e_commerceapplication.R;
import com.example.e_commerceapplication.repository.Resource;
import com.example.e_commerceapplication.utils.ValidationUtils;
import com.example.e_commerceapplication.viewmodels.AuthViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * Sends Firebase Authentication password reset emails.
 */
public class ForgotPasswordActivity extends AppCompatActivity {

    private TextInputLayout tilResetEmail;
    private TextInputEditText etResetEmail;
    private MaterialButton btnSendReset;
    private ProgressBar progressBar;

    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        tilResetEmail = findViewById(R.id.tilResetEmail);
        etResetEmail = findViewById(R.id.etResetEmail);
        btnSendReset = findViewById(R.id.btnSendReset);
        progressBar = findViewById(R.id.progressBar);

        btnSendReset.setOnClickListener(v -> performReset());
    }

    private void performReset() {
        String email = etResetEmail.getText() != null ? etResetEmail.getText().toString().trim() : "";
        tilResetEmail.setError(null);

        if (!ValidationUtils.isValidEmail(email)) {
            tilResetEmail.setError("Please enter a valid email address");
            return;
        }

        authViewModel.sendPasswordResetEmail(email).observe(this, resource -> {
            if (resource.status == Resource.Status.LOADING) {
                progressBar.setVisibility(View.VISIBLE);
                btnSendReset.setEnabled(false);
            } else if (resource.status == Resource.Status.SUCCESS) {
                progressBar.setVisibility(View.GONE);
                btnSendReset.setEnabled(true);
                Toast.makeText(this, "Password reset email sent. Please check your inbox.", Toast.LENGTH_LONG).show();
                finish();
            } else if (resource.status == Resource.Status.ERROR) {
                progressBar.setVisibility(View.GONE);
                btnSendReset.setEnabled(true);
                Toast.makeText(this, "Error: " + resource.message, Toast.LENGTH_LONG).show();
            }
        });
    }
}