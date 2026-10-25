package com.example.e_commerceapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.e_commerceapplication.R;
import com.example.e_commerceapplication.database.LocalCartItem;
import com.example.e_commerceapplication.models.Order;
import com.example.e_commerceapplication.models.OrderItem;
import com.example.e_commerceapplication.models.User;
import com.example.e_commerceapplication.repository.Resource;
import com.example.e_commerceapplication.utils.ValidationUtils;
import com.example.e_commerceapplication.viewmodels.AuthViewModel;
import com.example.e_commerceapplication.viewmodels.CartViewModel;
import com.example.e_commerceapplication.viewmodels.OrderViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * CheckoutActivity collects delivery information, calculates total, validates promo code,
 * saves order in Firestore with status PENDING_PAYMENT, and navigates to MpesaPaymentActivity.
 */
public class CheckoutActivity extends AppCompatActivity {

    private TextInputLayout tilCustomerName, tilMpesaPhone, tilAddress, tilPromoCode;
    private TextInputEditText etCustomerName, etMpesaPhone, etAddress, etPromoCode;
    private RadioGroup rgDeliveryType;
    private RadioButton rbDelivery, rbCollection;
    private MaterialButton btnApplyPromo, btnPlaceOrder;
    private TextView tvSubtotal, tvDeliveryFee, tvDiscountAmount, tvGrandTotal;
    private ProgressBar progressBar;

    private AuthViewModel authViewModel;
    private CartViewModel cartViewModel;
    private OrderViewModel orderViewModel;

    private List<LocalCartItem> cartItemsList = new ArrayList<>();
    private double cartSubtotal = 0.0;
    private double deliveryFee = 250.0; // Default Nairobi Delivery
    private double discountAmount = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);
        orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);

        initViews();
        loadUserDataAndCart();
        setupListeners();
    }

    private void initViews() {
        tilCustomerName = findViewById(R.id.tilCustomerName);
        tilMpesaPhone = findViewById(R.id.tilMpesaPhone);
        tilAddress = findViewById(R.id.tilAddress);
        tilPromoCode = findViewById(R.id.tilPromoCode);

        etCustomerName = findViewById(R.id.etCustomerName);
        etMpesaPhone = findViewById(R.id.etMpesaPhone);
        etAddress = findViewById(R.id.etAddress);
        etPromoCode = findViewById(R.id.etPromoCode);

        rgDeliveryType = findViewById(R.id.rgDeliveryType);
        rbDelivery = findViewById(R.id.rbDelivery);
        rbCollection = findViewById(R.id.rbCollection);

        btnApplyPromo = findViewById(R.id.btnApplyPromo);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);

        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvDeliveryFee = findViewById(R.id.tvDeliveryFee);
        tvDiscountAmount = findViewById(R.id.tvDiscountAmount);
        tvGrandTotal = findViewById(R.id.tvGrandTotal);

        progressBar = findViewById(R.id.progressBar);
    }

    private void loadUserDataAndCart() {
        FirebaseUser currentUser = authViewModel.getCurrentUser();
        String userId = (currentUser != null) ? currentUser.getUid() : "demo_user_123";

        authViewModel.getUserProfile(userId).observe(this, resource -> {
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                User user = resource.data;
                etCustomerName.setText(user.getFullName());
                etMpesaPhone.setText(user.getPhone());
                etAddress.setText(user.getAddress());
            }
        });

        cartViewModel.getCartItems().observe(this, items -> {
            cartItemsList = items != null ? items : new ArrayList<>();
        });

        cartViewModel.getTotalPrice().observe(this, total -> {
            cartSubtotal = total != null ? total : 0.0;
            recalculateTotal();
        });
    }

    private void setupListeners() {
        rgDeliveryType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbCollection) {
                deliveryFee = 0.0;
                tilAddress.setVisibility(View.GONE);
            } else {
                deliveryFee = 250.0;
                tilAddress.setVisibility(View.VISIBLE);
            }
            recalculateTotal();
        });

        btnApplyPromo.setOnClickListener(v -> applyPromoCode());
        btnPlaceOrder.setOnClickListener(v -> proceedToMpesaPayment());
    }

    private void recalculateTotal() {
        double grandTotal = Math.max(0.0, (cartSubtotal + deliveryFee) - discountAmount);
        tvSubtotal.setText(String.format(Locale.US, "KES %.2f", cartSubtotal));
        tvDeliveryFee.setText(String.format(Locale.US, "KES %.2f", deliveryFee));
        tvDiscountAmount.setText(String.format(Locale.US, "- KES %.2f", discountAmount));
        tvGrandTotal.setText(String.format(Locale.US, "KES %.2f", grandTotal));
    }

    private void applyPromoCode() {
        String code = etPromoCode.getText() != null ? etPromoCode.getText().toString().trim() : "";
        if (code.isEmpty()) return;

        orderViewModel.validatePromoCode(code).observe(this, resource -> {
            if (resource.status == Resource.Status.LOADING) {
                progressBar.setVisibility(View.VISIBLE);
            } else if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                progressBar.setVisibility(View.GONE);
                double pct = resource.data.getDiscountPercentage() / 100.0;
                double max = resource.data.getMaxDiscount();
                discountAmount = Math.min(cartSubtotal * pct, max);
                recalculateTotal();
                Toast.makeText(this, "Promo code applied!", Toast.LENGTH_SHORT).show();
            } else if (resource.status == Resource.Status.ERROR) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void proceedToMpesaPayment() {
        String name = etCustomerName.getText() != null ? etCustomerName.getText().toString().trim() : "";
        String phone = etMpesaPhone.getText() != null ? etMpesaPhone.getText().toString().trim() : "";
        String address = etAddress.getText() != null ? etAddress.getText().toString().trim() : "";

        tilCustomerName.setError(null);
        tilMpesaPhone.setError(null);
        tilAddress.setError(null);

        if (name.isEmpty()) {
            tilCustomerName.setError("Name is required");
            return;
        }

        if (!ValidationUtils.isValidKenyanPhone(phone)) {
            tilMpesaPhone.setError("Enter valid Kenyan phone number");
            return;
        }

        boolean isDelivery = rgDeliveryType.getCheckedRadioButtonId() == R.id.rbDelivery;
        if (isDelivery && address.isEmpty()) {
            tilAddress.setError("Delivery address is required");
            return;
        }

        FirebaseUser currentUser = authViewModel.getCurrentUser();
        String userId = (currentUser != null) ? currentUser.getUid() : "demo_user_123";

        String formattedPhone = ValidationUtils.formatKenyanPhoneTo254(phone);
        String orderRef = ValidationUtils.generateOrderReference();
        double grandTotal = Math.max(0.0, (cartSubtotal + deliveryFee) - discountAmount);

        List<OrderItem> orderItems = new ArrayList<>();
        for (LocalCartItem cartItem : cartItemsList) {
            orderItems.add(new OrderItem(
                    orderRef + "_" + cartItem.getProductId(),
                    orderRef,
                    cartItem.getProductId(),
                    cartItem.getName(),
                    cartItem.getQuantity(),
                    cartItem.getPrice(),
                    cartItem.getSubtotal()
            ));
        }

        Order newOrder = new Order(
                orderRef,
                orderRef,
                userId,
                name,
                formattedPhone,
                System.currentTimeMillis(),
                grandTotal,
                discountAmount,
                deliveryFee,
                "PENDING_PAYMENT",
                "PENDING",
                isDelivery ? "DELIVERY" : "COLLECTION",
                isDelivery ? address : "COLLECT FROM SHOP"
        );
        newOrder.setItems(orderItems);

        orderViewModel.createOrder(newOrder).observe(this, resource -> {
            if (resource.status == Resource.Status.LOADING) {
                progressBar.setVisibility(View.VISIBLE);
                btnPlaceOrder.setEnabled(false);
            } else if (resource.status == Resource.Status.SUCCESS) {
                progressBar.setVisibility(View.GONE);
                btnPlaceOrder.setEnabled(true);

                Intent intent = new Intent(CheckoutActivity.this, MpesaPaymentActivity.class);
                intent.putExtra("orderReference", orderRef);
                intent.putExtra("phone", formattedPhone);
                intent.putExtra("amount", grandTotal);
                startActivity(intent);
                finish();
            } else if (resource.status == Resource.Status.ERROR) {
                progressBar.setVisibility(View.GONE);
                btnPlaceOrder.setEnabled(true);
                Toast.makeText(this, "Failed to create order: " + resource.message, Toast.LENGTH_LONG).show();
            }
        });
    }
}