package com.example.e_commerceapplication.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.e_commerceapplication.network.ApiService;
import com.example.e_commerceapplication.network.PaymentStatusResponse;
import com.example.e_commerceapplication.network.RetrofitClient;
import com.example.e_commerceapplication.network.StkPushRequest;
import com.example.e_commerceapplication.network.StkPushResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository class responsible for sending STK Push request and polling M-Pesa payment status.
 */
public class PaymentRepository {

    private final ApiService apiService;

    public PaymentRepository() {
        apiService = RetrofitClient.getApiService();
    }

    public LiveData<Resource<StkPushResponse>> initiateStkPush(String idToken, String orderRef, String phone) {
        MutableLiveData<Resource<StkPushResponse>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        StkPushRequest request = new StkPushRequest(orderRef, phone);
        String authHeader = idToken != null ? "Bearer " + idToken : "";

        apiService.initiateStkPush(authHeader, request).enqueue(new Callback<StkPushResponse>() {
            @Override
            public void onResponse(Call<StkPushResponse> call, Response<StkPushResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        result.setValue(Resource.success(response.body()));
                    } else {
                        result.setValue(Resource.error(response.body().getMessage()));
                    }
                } else {
                    result.setValue(Resource.error("STK Push failed with HTTP error code: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<StkPushResponse> call, Throwable t) {
                result.setValue(Resource.error("Network failure: " + t.getMessage()));
            }
        });

        return result;
    }

    public LiveData<Resource<PaymentStatusResponse>> checkPaymentStatus(String idToken, String orderRef) {
        MutableLiveData<Resource<PaymentStatusResponse>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        String authHeader = idToken != null ? "Bearer " + idToken : "";

        apiService.getPaymentStatus(authHeader, orderRef).enqueue(new Callback<PaymentStatusResponse>() {
            @Override
            public void onResponse(Call<PaymentStatusResponse> call, Response<PaymentStatusResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(response.body()));
                } else {
                    result.setValue(Resource.error("Status check failed with HTTP code: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<PaymentStatusResponse> call, Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage()));
            }
        });

        return result;
    }
}