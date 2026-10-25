package com.example.e_commerceapplication.network;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Retrofit REST API interface for communicating with the Python Flask backend.
 */
public interface ApiService {

    /**
     * Triggers M-Pesa STK push via backend.
     */
    @POST("api/mpesa/stkpush")
    Call<StkPushResponse> initiateStkPush(
            @Header("Authorization") String idToken,
            @Body StkPushRequest request
    );

    /**
     * Polls current payment status for an order.
     */
    @GET("api/mpesa/status/{orderRef}")
    Call<PaymentStatusResponse> getPaymentStatus(
            @Header("Authorization") String idToken,
            @Path("orderRef") String orderRef
    );
}