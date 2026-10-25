package com.example.e_commerceapplication.activities;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.e_commerceapplication.R;
import com.example.e_commerceapplication.models.Order;
import com.example.e_commerceapplication.models.OrderItem;
import com.example.e_commerceapplication.repository.Resource;
import com.example.e_commerceapplication.utils.PdfGenerator;
import com.example.e_commerceapplication.viewmodels.OrderViewModel;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * OrderDetailsActivity displays full order details, tracking stepper status, and PDF e-receipt download button.
 */
public class OrderDetailsActivity extends AppCompatActivity {

    private TextView tvDetailOrderRef, tvDetailOrderDate, tvOrderStepper, tvDetailCustomerName, tvDetailPhone, tvDetailDeliveryType, tvDetailAddress, tvDetailMpesaReceipt;
    private LinearLayout containerOrderItems;
    private MaterialButton btnDownloadPdfReceipt;

    private OrderViewModel orderViewModel;
    private String orderRef;
    private Order loadedOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        orderRef = getIntent().getStringExtra("orderReference");
        orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);

        initViews();
        loadOrderDetails();

        btnDownloadPdfReceipt.setOnClickListener(v -> {
            if (loadedOrder != null) {
                PdfGenerator.generateReceiptPdf(this, loadedOrder);
            }
        });
    }

    private void initViews() {
        tvDetailOrderRef = findViewById(R.id.tvDetailOrderRef);
        tvDetailOrderDate = findViewById(R.id.tvDetailOrderDate);
        tvOrderStepper = findViewById(R.id.tvOrderStepper);

        tvDetailCustomerName = findViewById(R.id.tvDetailCustomerName);
        tvDetailPhone = findViewById(R.id.tvDetailPhone);
        tvDetailDeliveryType = findViewById(R.id.tvDetailDeliveryType);
        tvDetailAddress = findViewById(R.id.tvDetailAddress);
        tvDetailMpesaReceipt = findViewById(R.id.tvDetailMpesaReceipt);

        containerOrderItems = findViewById(R.id.containerOrderItems);
        btnDownloadPdfReceipt = findViewById(R.id.btnDownloadPdfReceipt);
    }

    private void loadOrderDetails() {
        if (orderRef == null) return;

        orderViewModel.getOrderByReference(orderRef).observe(this, resource -> {
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                loadedOrder = resource.data;
                bindOrderData(loadedOrder);
            } else if (resource.status == Resource.Status.ERROR) {
                Toast.makeText(this, "Error loading order: " + resource.message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bindOrderData(Order order) {
        tvDetailOrderRef.setText("Order Ref: " + order.getOrderReference());

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.US);
        tvDetailOrderDate.setText("Date: " + sdf.format(new Date(order.getOrderDate())));

        tvDetailCustomerName.setText("Customer: " + order.getCustomerName());
        tvDetailPhone.setText("Phone: " + order.getCustomerPhone());
        tvDetailDeliveryType.setText("Fulfillment: " + order.getDeliveryType());
        tvDetailAddress.setText("Address: " + (order.getDeliveryAddress() != null ? order.getDeliveryAddress() : "N/A"));
        tvDetailMpesaReceipt.setText("M-Pesa Receipt: " + (order.getMpesaReceiptNumber() != null ? order.getMpesaReceiptNumber() : "N/A"));

        // Format Stepper Timeline
        String status = order.getOrderStatus() != null ? order.getOrderStatus().toUpperCase() : "PENDING_PAYMENT";
        if ("PAID".equalsIgnoreCase(status)) {
            tvOrderStepper.setText("✔ Placed ➔ ✔ Paid ➔ ⏳ Processing ➔ Shipped ➔ Completed");
        } else if ("PROCESSING".equalsIgnoreCase(status)) {
            tvOrderStepper.setText("✔ Placed ➔ ✔ Paid ➔ ✔ Processing ➔ ⏳ Shipped ➔ Completed");
        } else if ("SHIPPED".equalsIgnoreCase(status)) {
            tvOrderStepper.setText("✔ Placed ➔ ✔ Paid ➔ ✔ Processing ➔ ✔ Shipped ➔ ⏳ Completed");
        } else if ("COMPLETED".equalsIgnoreCase(status)) {
            tvOrderStepper.setText("✔ Placed ➔ ✔ Paid ➔ ✔ Processing ➔ ✔ Shipped ➔ ✔ Completed");
        } else if ("CANCELLED".equalsIgnoreCase(status)) {
            tvOrderStepper.setText("❌ ORDER CANCELLED / PAYMENT FAILED");
        } else {
            tvOrderStepper.setText("⏳ Placed (Awaiting Payment) ➔ Paid ➔ Processing ➔ Shipped ➔ Completed");
        }

        containerOrderItems.removeAllViews();
        if (order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                TextView itemView = new TextView(this);
                itemView.setPadding(0, 8, 0, 8);
                itemView.setText(String.format(Locale.US, "• %s x%d @ KES %.2f = KES %.2f",
                        item.getProductName(), item.getQuantity(), item.getUnitPrice(), item.getSubtotal()));
                containerOrderItems.addView(itemView);
            }
        }
    }
}