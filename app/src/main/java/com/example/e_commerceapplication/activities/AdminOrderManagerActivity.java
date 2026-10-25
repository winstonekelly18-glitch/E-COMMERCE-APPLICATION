package com.example.e_commerceapplication.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_commerceapplication.R;
import com.example.e_commerceapplication.adapters.OrderAdapter;
import com.example.e_commerceapplication.models.Order;
import com.example.e_commerceapplication.repository.Resource;
import com.example.e_commerceapplication.viewmodels.AdminViewModel;
import com.google.android.material.appbar.MaterialToolbar;

/**
 * AdminOrderManagerActivity allows admin users to view all orders across all customers and change order status.
 */
public class AdminOrderManagerActivity extends AppCompatActivity implements OrderAdapter.OnOrderClickListener {

    private MaterialToolbar toolbarAdminOrders;
    private RecyclerView rvAdminOrders;

    private AdminViewModel adminViewModel;
    private OrderAdapter orderAdapter;

    private final String[] statuses = {"PAID", "PROCESSING", "SHIPPED", "COMPLETED", "CANCELLED"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_order_manager);

        adminViewModel = new ViewModelProvider(this).get(AdminViewModel.class);

        initViews();
        setupRecyclerView();
        loadOrders();
    }

    private void initViews() {
        toolbarAdminOrders = findViewById(R.id.toolbarAdminOrders);
        rvAdminOrders = findViewById(R.id.rvAdminOrders);

        setSupportActionBar(toolbarAdminOrders);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbarAdminOrders.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        orderAdapter = new OrderAdapter(this, this);
        rvAdminOrders.setLayoutManager(new LinearLayoutManager(this));
        rvAdminOrders.setAdapter(orderAdapter);
    }

    private void loadOrders() {
        adminViewModel.getAllOrders().observe(this, resource -> {
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                orderAdapter.setOrders(resource.data);
            }
        });
    }

    @Override
    public void onOrderClick(Order order) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Status for " + order.getOrderReference());

        builder.setItems(statuses, (dialog, which) -> {
            String selectedStatus = statuses[which];
            adminViewModel.updateOrderStatus(order.getOrderReference(), selectedStatus).observe(this, resource -> {
                if (resource.status == Resource.Status.SUCCESS) {
                    Toast.makeText(this, "Order status updated to " + selectedStatus, Toast.LENGTH_SHORT).show();
                    loadOrders();
                }
            });
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}