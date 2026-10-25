package com.example.e_commerceapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_commerceapplication.R;
import com.example.e_commerceapplication.adapters.OrderAdapter;
import com.example.e_commerceapplication.models.Order;
import com.example.e_commerceapplication.repository.Resource;
import com.example.e_commerceapplication.viewmodels.AuthViewModel;
import com.example.e_commerceapplication.viewmodels.OrderViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * OrderHistoryActivity displays past customer orders filtered by status tabs (All | Awaiting Payment | Paid | Failed).
 */
public class OrderHistoryActivity extends AppCompatActivity implements OrderAdapter.OnOrderClickListener {

    private MaterialToolbar toolbarOrders;
    private TabLayout tabLayoutOrders;
    private RecyclerView rvOrderHistory;
    private TextView tvEmptyOrders;
    private ProgressBar progressBar;

    private OrderViewModel orderViewModel;
    private AuthViewModel authViewModel;
    private OrderAdapter orderAdapter;

    private List<Order> allOrdersList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        initViews();
        setupTabs();
        setupRecyclerView();
        loadOrders();
    }

    private void initViews() {
        toolbarOrders = findViewById(R.id.toolbarOrders);
        tabLayoutOrders = findViewById(R.id.tabLayoutOrders);
        rvOrderHistory = findViewById(R.id.rvOrderHistory);
        tvEmptyOrders = findViewById(R.id.tvEmptyOrders);
        progressBar = findViewById(R.id.progressBar);

        setSupportActionBar(toolbarOrders);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbarOrders.setNavigationOnClickListener(v -> finish());
    }

    private void setupTabs() {
        tabLayoutOrders.addTab(tabLayoutOrders.newTab().setText("All"));
        tabLayoutOrders.addTab(tabLayoutOrders.newTab().setText("Awaiting Payment"));
        tabLayoutOrders.addTab(tabLayoutOrders.newTab().setText("Paid"));
        tabLayoutOrders.addTab(tabLayoutOrders.newTab().setText("Failed/Cancelled"));

        tabLayoutOrders.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) {
                filterOrdersByTab(tab.getPosition());
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupRecyclerView() {
        orderAdapter = new OrderAdapter(this, this);
        rvOrderHistory.setLayoutManager(new LinearLayoutManager(this));
        rvOrderHistory.setAdapter(orderAdapter);
    }

    private void loadOrders() {
        FirebaseUser user = authViewModel.getCurrentUser();
        if (user == null) {
            finish();
            return;
        }

        orderViewModel.getCustomerOrders(user.getUid()).observe(this, resource -> {
            if (resource.status == Resource.Status.LOADING) {
                progressBar.setVisibility(View.VISIBLE);
            } else if (resource.status == Resource.Status.SUCCESS) {
                progressBar.setVisibility(View.GONE);
                allOrdersList = resource.data != null ? resource.data : new ArrayList<>();
                filterOrdersByTab(tabLayoutOrders.getSelectedTabPosition());
            } else if (resource.status == Resource.Status.ERROR) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(this, "Failed to load orders: " + resource.message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterOrdersByTab(int position) {
        List<Order> filtered = new ArrayList<>();
        for (Order o : allOrdersList) {
            String status = o.getOrderStatus() != null ? o.getOrderStatus() : "";
            if (position == 0) { // All
                filtered.add(o);
            } else if (position == 1 && "PENDING_PAYMENT".equalsIgnoreCase(status)) { // Awaiting Payment
                filtered.add(o);
            } else if (position == 2 && ("PAID".equalsIgnoreCase(status) || "PROCESSING".equalsIgnoreCase(status) || "SHIPPED".equalsIgnoreCase(status) || "COMPLETED".equalsIgnoreCase(status))) {
                filtered.add(o);
            } else if (position == 3 && ("CANCELLED".equalsIgnoreCase(status) || "FAILED".equalsIgnoreCase(status))) {
                filtered.add(o);
            }
        }
        orderAdapter.setOrders(filtered);
        tvEmptyOrders.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onOrderClick(Order order) {
        Intent intent = new Intent(OrderHistoryActivity.this, OrderDetailsActivity.class);
        intent.putExtra("orderReference", order.getOrderReference());
        startActivity(intent);
    }
}