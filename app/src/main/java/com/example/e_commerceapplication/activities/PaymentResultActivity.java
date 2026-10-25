package com.example.e_commerceapplication.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.e_commerceapplication.MainActivity;
import com.example.e_commerceapplication.R;
import com.example.e_commerceapplication.repository.Resource;
import com.example.e_commerceapplication.utils.PdfGenerator;
import com.example.e_commerceapplication.viewmodels.OrderViewModel;
import com.google.android.material.button.MaterialButton;

import java.util.Locale;

/**
 * PaymentResultActivity presents final payment status (SUCCESS, FAILED, CANCELLED, TIMEOUT),
 * provides option to download PDF e-receipt, or retry payment.
 */
public class PaymentResultActivity extends AppCompatActivity {

    private ImageView ivResultIcon;
    private TextView tvResultTitle, tvResultMessage, tvResultOrderRef, tvResultReceipt, tvResultAmount;
    private MaterialButton btnDownloadReceipt, btnRetryPayment, btnBackToHome;

    private OrderViewModel orderViewModel;

    private String orderRef, paymentStatus, message, receipt;
    private double amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_result);

        orderRef = getIntent().getStringExtra("orderReference");
        paymentStatus = getIntent().getStringExtra("paymentStatus");
        message = getIntent().getStringExtra("message");
        receipt = getIntent().getStringExtra("receipt");
        amount = getIntent().getDoubleExtra("amount", 0.0);

        orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);

        initViews();
        displayResult();
        setupListeners();
    }

    private void initViews() {
        ivResultIcon = findViewById(R.id.ivResultIcon);
        tvResultTitle = findViewById(R.id.tvResultTitle);
        tvResultMessage = findViewById(R.id.tvResultMessage);
        tvResultOrderRef = findViewById(R.id.tvResultOrderRef);
        tvResultReceipt = findViewById(R.id.tvResultReceipt);
        tvResultAmount = findViewById(R.id.tvResultAmount);

        btnDownloadReceipt = findViewById(R.id.btnDownloadReceipt);
        btnRetryPayment = findViewById(R.id.btnRetryPayment);
        btnBackToHome = findViewById(R.id.btnBackToHome);
    }

    private void displayResult() {
        tvResultOrderRef.setText(orderRef);
        tvResultAmount.setText(String.format(Locale.US, "KES %.2f", amount));
        tvResultReceipt.setText(receipt != null && !receipt.isEmpty() ? receipt : "N/A");

        if ("SUCCESS".equalsIgnoreCase(paymentStatus)) {
            tvResultTitle.setText("Payment Confirmed!");
            tvResultTitle.setTextColor(getColor(R.color.status_paid));
            tvResultMessage.setText(message);
            btnDownloadReceipt.setVisibility(View.VISIBLE);
            btnRetryPayment.setVisibility(View.GONE);
        } else {
            tvResultTitle.setText("Payment Not Completed");
            tvResultTitle.setTextColor(getColor(R.color.status_failed));
            tvResultMessage.setText(message != null ? message : "The M-Pesa transaction was not completed. You may try again.");
            btnDownloadReceipt.setVisibility(View.GONE);
            btnRetryPayment.setVisibility(View.VISIBLE);
        }
    }

    private void setupListeners() {
        btnDownloadReceipt.setOnClickListener(v -> {
            orderViewModel.getOrderByReference(orderRef).observe(this, resource -> {
                if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                    PdfGenerator.generateReceiptPdf(this, resource.data);
                } else if (resource.status == Resource.Status.ERROR) {
                    Toast.makeText(this, "Could not load order for receipt generation", Toast.LENGTH_SHORT).show();
                }
            });
        });

        btnRetryPayment.setOnClickListener(v -> {
            Intent intent = new Intent(PaymentResultActivity.this, MpesaPaymentActivity.class);
            intent.putExtra("orderReference", orderRef);
            intent.putExtra("amount", amount);
            startActivity(intent);
            finish();
        });

        btnBackToHome.setOnClickListener(v -> {
            Intent intent = new Intent(PaymentResultActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}