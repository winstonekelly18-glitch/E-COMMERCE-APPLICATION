package com.example.e_commerceapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.e_commerceapplication.R;
import com.example.e_commerceapplication.models.User;
import com.example.e_commerceapplication.repository.Resource;
import com.example.e_commerceapplication.utils.ValidationUtils;
import com.example.e_commerceapplication.viewmodels.AuthViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseUser;

/**
 * ProfileActivity displays and allows editing of user details (Name, Phone, Delivery Address)
 * and displays role (Customer/Admin).
 */
public class ProfileActivity extends AppCompatActivity {

    private TextView tvRoleBadge;
    private TextInputLayout tilFullName, tilPhone, tilEmail, tilAddress;
    private TextInputEditText etFullName, etPhone, etEmail, etAddress;
    private MaterialButton btnSaveProfile, btnLogout;
    private ProgressBar progressBar;

    private AuthViewModel authViewModel;
    private User currentUserModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        initViews();
        loadProfileData();

        btnSaveProfile.setOnClickListener(v -> saveProfile());
        btnLogout.setOnClickListener(v -> performLogout());
    }

    private void initViews() {
        tvRoleBadge = findViewById(R.id.tvRoleBadge);
        tilFullName = findViewById(R.id.tilFullName);
        tilPhone = findViewById(R.id.tilPhone);
        tilEmail = findViewById(R.id.tilEmail);
        tilAddress = findViewById(R.id.tilAddress);

        etFullName = findViewById(R.id.etFullName);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        etAddress = findViewById(R.id.etAddress);

        btnSaveProfile = findViewById(R.id.btnSaveProfile);
        btnLogout = findViewById(R.id.btnLogout);
        progressBar = findViewById(R.id.progressBar);
    }

    private void loadProfileData() {
        FirebaseUser fbUser = authViewModel.getCurrentUser();
        if (fbUser == null) {
            finish();
            return;
        }

        authViewModel.getUserProfile(fbUser.getUid()).observe(this, resource -> {
            if (resource.status == Resource.Status.LOADING) {
                progressBar.setVisibility(View.VISIBLE);
            } else if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                progressBar.setVisibility(View.GONE);
                currentUserModel = resource.data;
                etFullName.setText(currentUserModel.getFullName());
                etPhone.setText(currentUserModel.getPhone());
                etEmail.setText(currentUserModel.getEmail());
                etAddress.setText(currentUserModel.getAddress());
                tvRoleBadge.setText("ROLE: " + (currentUserModel.getRole() != null ? currentUserModel.getRole().toUpperCase() : "CUSTOMER"));
            } else if (resource.status == Resource.Status.ERROR) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(this, "Failed to load profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveProfile() {
        if (currentUserModel == null) return;

        String name = etFullName.getText() != null ? etFullName.getText().toString().trim() : "";
        String phone = etPhone.getText() != null ? etPhone.getText().toString().trim() : "";
        String address = etAddress.getText() != null ? etAddress.getText().toString().trim() : "";

        if (name.isEmpty()) {
            tilFullName.setError("Name cannot be empty");
            return;
        }

        if (!ValidationUtils.isValidKenyanPhone(phone)) {
            tilPhone.setError("Invalid Kenyan phone number");
            return;
        }

        currentUserModel.setFullName(name);
        currentUserModel.setPhone(ValidationUtils.formatKenyanPhoneTo254(phone));
        currentUserModel.setAddress(address);

        authViewModel.updateUserProfile(currentUserModel).observe(this, resource -> {
            if (resource.status == Resource.Status.LOADING) {
                progressBar.setVisibility(View.VISIBLE);
            } else if (resource.status == Resource.Status.SUCCESS) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
            } else if (resource.status == Resource.Status.ERROR) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(this, "Failed to update profile: " + resource.message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void performLogout() {
        authViewModel.logout();
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}