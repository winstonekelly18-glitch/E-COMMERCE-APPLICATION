package com.example.e_commerceapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_commerceapplication.R;
import com.example.e_commerceapplication.adapters.CartAdapter;
import com.example.e_commerceapplication.database.LocalCartItem;
import com.example.e_commerceapplication.viewmodels.CartViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.util.List;
import java.util.Locale;

/**
 * CartActivity displays local Room database cart items, auto-recalculates subtotal/total,
 * and handles quantity modifications.
 */
public class CartActivity extends AppCompatActivity implements CartAdapter.OnCartChangeListener {

    private MaterialToolbar toolbarCart;
    private RecyclerView rvCartItems;
    private TextView tvEmptyCart, tvCartTotalAmount;
    private MaterialButton btnClearCart, btnCheckout;

    private CartViewModel cartViewModel;
    private CartAdapter cartAdapter;
    private List<LocalCartItem> currentCartList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);

        initViews();
        setupRecyclerView();
        observeCartData();
        setupListeners();
    }

    private void initViews() {
        toolbarCart = findViewById(R.id.toolbarCart);
        rvCartItems = findViewById(R.id.rvCartItems);
        tvEmptyCart = findViewById(R.id.tvEmptyCart);
        tvCartTotalAmount = findViewById(R.id.tvCartTotalAmount);
        btnClearCart = findViewById(R.id.btnClearCart);
        btnCheckout = findViewById(R.id.btnCheckout);

        setSupportActionBar(toolbarCart);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbarCart.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        cartAdapter = new CartAdapter(this, this);
        rvCartItems.setLayoutManager(new LinearLayoutManager(this));
        rvCartItems.setAdapter(cartAdapter);
    }

    private void observeCartData() {
        cartViewModel.getCartItems().observe(this, items -> {
            currentCartList = items;
            cartAdapter.setCartItems(items);
            if (items == null || items.isEmpty()) {
                tvEmptyCart.setVisibility(View.VISIBLE);
                btnCheckout.setEnabled(false);
            } else {
                tvEmptyCart.setVisibility(View.GONE);
                btnCheckout.setEnabled(true);
            }
        });

        cartViewModel.getTotalPrice().observe(this, total -> {
            double amount = total != null ? total : 0.0;
            tvCartTotalAmount.setText(String.format(Locale.US, "KES %.2f", amount));
        });
    }

    private void setupListeners() {
        btnClearCart.setOnClickListener(v -> {
            cartViewModel.clearCart();
            Toast.makeText(this, "Cart cleared", Toast.LENGTH_SHORT).show();
        });

        btnCheckout.setOnClickListener(v -> {
            if (currentCartList == null || currentCartList.isEmpty()) {
                Toast.makeText(this, "Your cart is empty!", Toast.LENGTH_SHORT).show();
                return;
            }
            startActivity(new Intent(CartActivity.class.cast(this), CheckoutActivity.class));
        });
    }

    @Override
    public void onQuantityChange(LocalCartItem item, int newQuantity) {
        if (newQuantity > item.getMaxStock()) {
            Toast.makeText(this, "Stock limit reached! Max available: " + item.getMaxStock(), Toast.LENGTH_SHORT).show();
            return;
        }
        cartViewModel.updateQuantity(item, newQuantity);
    }

    @Override
    public void onItemRemove(LocalCartItem item) {
        cartViewModel.removeItem(item);
        Toast.makeText(this, item.getName() + " removed", Toast.LENGTH_SHORT).show();
    }
}