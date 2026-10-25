package com.example.e_commerceapplication.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.e_commerceapplication.models.User;
import com.example.e_commerceapplication.repository.AuthRepository;
import com.example.e_commerceapplication.repository.Resource;
import com.google.firebase.auth.FirebaseUser;

/**
 * ViewModel managing authentication workflow and user session state.
 */
public class AuthViewModel extends AndroidViewModel {

    private final AuthRepository authRepository;

    public AuthViewModel(@NonNull Application application) {
        super(application);
        authRepository = new AuthRepository();
    }

    public FirebaseUser getCurrentUser() {
        return authRepository.getCurrentUser();
    }

    public LiveData<Resource<FirebaseUser>> login(String email, String password) {
        return authRepository.login(email, password);
    }

    public LiveData<Resource<User>> register(String fullName, String email, String phone, String password) {
        return authRepository.register(fullName, email, phone, password);
    }

    public LiveData<Resource<User>> getUserProfile(String userId) {
        return authRepository.getUserProfile(userId);
    }

    public LiveData<Resource<Boolean>> sendPasswordResetEmail(String email) {
        return authRepository.sendPasswordResetEmail(email);
    }

    public LiveData<Resource<Boolean>> updateUserProfile(User user) {
        return authRepository.updateUserProfile(user);
    }

    public void logout() {
        authRepository.logout();
    }
}