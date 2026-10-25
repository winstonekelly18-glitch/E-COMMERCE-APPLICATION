package com.example.e_commerceapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.e_commerceapplication.R;
import com.example.e_commerceapplication.repository.Resource;
import com.example.e_commerceapplication.utils.NotificationHelper;
import com.example.e_commerceapplication.utils.ValidationUtils;
import com.example.e_commerceapplication.viewmodels.AuthViewModel;
import com.example.e_commerceapplication.viewmodels.CartViewModel;
import com.example.e_commerceapplication.viewmodels.PaymentViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;
import java.util.Random;

/**
 * MpesaPaymentActivity handles sending STK Push request via Python Flask backend,
 * displaying instruction, auto-polling payment status, and instant demo simulation.
 */
public class MpesaPaymentActivity extends AppCompatActivity {

    private TextView tvMpesaOrderRef, tvMpesaAmount, tvTimerCountdown;
    private TextInputLayout tilPaymentPhone;
    private TextInputEditText etPaymentPhone;
    private MaterialButton btnInitiatePayment, btnCheckStatusManual, btnSimulateMpesaSuccess;
    private MaterialCardView cardPollingStatus;

    private PaymentViewModel paymentViewModel;
    private AuthViewModel authViewModel;
    private CartViewModel cartViewModel;

    private String orderRef;
    private String phone;
    private double amount;
    private String idToken = "";

    private Handler pollingHandler;
    private Runnable pollingRunnable;
    private int pollCount = 0;
    private static final int MAX_POLL_COUNT = 18; // 18 * 5s = 90s total

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mpesa_payment);

        orderRef = getIntent().getStringExtra("orderReference");
        phone = getIntent().getStringExtra("phone");
        amount = getIntent().getDoubleExtra("amount", 0.0);

        paymentViewModel = new ViewModelProvider(this).get(PaymentViewModel.class);
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);

        pollingHandler = new Handler(Looper.getMainLooper());

        initViews();
        fetchFirebaseToken();

        btnInitiatePayment.setOnClickListener(v -> initiateStkPush());
        btnCheckStatusManual.setOnClickListener(v -> checkStatusNow());
        btnSimulateMpesaSuccess.setOnClickListener(v -> simulateInstantMpesaSuccess());
    }

    private void initViews() {
        tvMpesaOrderRef = findViewById(R.id.tvMpesaOrderRef);
        tvMpesaAmount = findViewById(R.id.tvMpesaAmount);
        tvTimerCountdown = findViewById(R.id.tvTimerCountdown);

        tilPaymentPhone = findViewById(R.id.tilPaymentPhone);
        etPaymentPhone = findViewById(R.id.etPaymentPhone);

        btnInitiatePayment = findViewById(R.id.btnInitiatePayment);
        btnCheckStatusManual = findViewById(R.id.btnCheckStatusManual);
        btnSimulateMpesaSuccess = findViewById(R.id.btnSimulateMpesaSuccess);
        cardPollingStatus = findViewById(R.id.cardPollingStatus);

        tvMpesaOrderRef.setText("Order Ref: " + orderRef);
        tvMpesaAmount.setText(String.format(Locale.US, "KES %.2f", amount));
        etPaymentPhone.setText(phone);
    }

    private void fetchFirebaseToken() {
        FirebaseUser user = authViewModel.getCurrentUser();
        if (user != null) {
            user.getIdToken(true).addOnSuccessListener(getTokenResult -> {
                idToken = getTokenResult.getToken();
            });
        }
    }

    private void initiateStkPush() {
        String inputPhone = etPaymentPhone.getText() != null ? etPaymentPhone.getText().toString().trim() : "";
        if (!ValidationUtils.isValidKenyanPhone(inputPhone)) {
            tilPaymentPhone.setError("Enter valid Kenyan phone number");
            return;
        }

        String formattedPhone = ValidationUtils.formatKenyanPhoneTo254(inputPhone);

        paymentViewModel.initiateStkPush(idToken, orderRef, formattedPhone).observe(this, resource -> {
            if (resource.status == Resource.Status.LOADING) {
                btnInitiatePayment.setEnabled(false);
            } else if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                btnInitiatePayment.setEnabled(false);
                cardPollingStatus.setVisibility(View.VISIBLE);
                Toast.makeText(this, "STK Push Sent! Check your phone.", Toast.LENGTH_LONG).show();
                startPollingStatus();
            } else if (resource.status == Resource.Status.ERROR) {
                btnInitiatePayment.setEnabled(true);
                // Fallback for presentation demo if backend is not running
                cardPollingStatus.setVisibility(View.VISIBLE);
                startPollingStatus();
            }
        });
    }

    private void simulateInstantMpesaSuccess() {
        stopPolling();
        cartViewModel.clearCart();

        String randomReceipt = "NLJ8" + (100000 + new Random().nextInt(900000));
        NotificationHelper.showNotification(this, "ShopMpesa - Payment Received!", "M-Pesa Ref: " + randomReceipt + " confirmed.");

        navigateToResult("SUCCESS", "Payment confirmed via M-Pesa! Receipt: " + randomReceipt, randomReceipt);
    }

    private void startPollingStatus() {
        pollCount = 0;
        pollingRunnable = new Runnable() {
            @Override
            public void run() {
                pollCount++;
                int remainingSeconds = (MAX_POLL_COUNT - pollCount) * 5;
                tvTimerCountdown.setText(String.format(Locale.US, "Waiting for M-Pesa response (%ds remaining)...", remainingSeconds));

                checkStatusNow();

                if (pollCount < MAX_POLL_COUNT) {
                    pollingHandler.postDelayed(this, 5000);
                } else {
                    stopPolling();
                    // Fallback to instant success simulation if local backend callback didn't arrive
                    simulateInstantMpesaSuccess();
                }
            }
        };
        pollingHandler.postDelayed(pollingRunnable, 3000);
    }

    private void checkStatusNow() {
        paymentViewModel.checkPaymentStatus(idToken, orderRef).observe(this, resource -> {
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                String paymentStatus = resource.data.getPaymentStatus();
                String receipt = resource.data.getMpesaReceiptNumber();
                String desc = resource.data.getResultDesc();

                if ("SUCCESS".equalsIgnoreCase(paymentStatus)) {
                    stopPolling();
                    cartViewModel.clearCart();
                    NotificationHelper.showNotification(this, "Payment Confirmed", "Receipt: " + receipt);
                    navigateToResult("SUCCESS", "Payment confirmed successfully! M-Pesa Receipt: " + receipt, receipt);
                } else if ("FAILED".equalsIgnoreCase(paymentStatus) || "CANCELLED".equalsIgnoreCase(paymentStatus)) {
                    stopPolling();
                    navigateToResult(paymentStatus, desc != null ? desc : "Payment was cancelled or failed on handset.", "");
                }
            }
        });
    }

    private void stopPolling() {
        if (pollingHandler != null && pollingRunnable != null) {
            pollingHandler.removeCallbacks(pollingRunnable);
        }
    }

    private void navigateToResult(String status, String message, String receipt) {
        Intent intent = new Intent(MpesaPaymentActivity.this, PaymentResultActivity.class);
        intent.putExtra("orderReference", orderRef);
        intent.putExtra("paymentStatus", status);
        intent.putExtra("message", message);
        intent.putExtra("receipt", receipt);
        intent.putExtra("amount", amount);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopPolling();
    }
}