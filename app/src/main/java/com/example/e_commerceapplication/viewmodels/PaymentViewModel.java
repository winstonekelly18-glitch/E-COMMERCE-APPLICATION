package com.example.e_commerceapplication.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.e_commerceapplication.network.PaymentStatusResponse;
import com.example.e_commerceapplication.network.StkPushResponse;
import com.example.e_commerceapplication.repository.PaymentRepository;
import com.example.e_commerceapplication.repository.Resource;

/**
 * ViewModel managing M-Pesa STK push initiation and polling payment status.
 */
public class PaymentViewModel extends AndroidViewModel {

    private final PaymentRepository paymentRepository;

    public PaymentViewModel(@NonNull Application application) {
        super(application);
        paymentRepository = new PaymentRepository();
    }

    public LiveData<Resource<StkPushResponse>> initiateStkPush(String idToken, String orderRef, String phone) {
        return paymentRepository.initiateStkPush(idToken, orderRef, phone);
    }

    public LiveData<Resource<PaymentStatusResponse>> checkPaymentStatus(String idToken, String orderRef) {
        return paymentRepository.checkPaymentStatus(idToken, orderRef);
    }
}