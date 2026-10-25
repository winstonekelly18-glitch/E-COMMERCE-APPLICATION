package com.example.e_commerceapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.e_commerceapplication.R;
import com.example.e_commerceapplication.models.Order;
import com.example.e_commerceapplication.models.Product;
import com.example.e_commerceapplication.repository.Resource;
import com.example.e_commerceapplication.viewmodels.AdminViewModel;
import com.example.e_commerceapplication.viewmodels.AuthViewModel;
import com.example.e_commerceapplication.viewmodels.ProductViewModel;
import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.Locale;

/**
 * AdminDashboardActivity presents overview analytics (Today's Sales, Orders, Pending Payments, Low Stock count)
 * and navigates to Product Manager & Order Manager with CSV export. Restricted to role = "admin".
 */
public class AdminDashboardActivity extends AppCompatActivity {

    private TextView tvAdminSales, tvAdminTotalOrders, tvAdminPendingPayments, tvAdminLowStockCount;
    private MaterialButton btnAdminLogout, btnManageProducts, btnManageOrders, btnExportSalesReport;

    private AdminViewModel adminViewModel;
    private ProductViewModel productViewModel;
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        adminViewModel = new ViewModelProvider(this).get(AdminViewModel.class);
        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        initViews();
        loadMetrics();
        setupListeners();
    }

    private void initViews() {
        tvAdminSales = findViewById(R.id.tvAdminSales);
        tvAdminTotalOrders = findViewById(R.id.tvAdminTotalOrders);
        tvAdminPendingPayments = findViewById(R.id.tvAdminPendingPayments);
        tvAdminLowStockCount = findViewById(R.id.tvAdminLowStockCount);

        btnAdminLogout = findViewById(R.id.btnAdminLogout);
        btnManageProducts = findViewById(R.id.btnManageProducts);
        btnManageOrders = findViewById(R.id.btnManageOrders);
        btnExportSalesReport = findViewById(R.id.btnExportSalesReport);
    }

    private void loadMetrics() {
        // Load Orders & Sales
        adminViewModel.getAllOrders().observe(this, resource -> {
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                List<Order> orders = resource.data;
                tvAdminTotalOrders.setText(String.valueOf(orders.size()));

                double todaySales = 0.0;
                int pendingPayments = 0;

                for (Order o : orders) {
                    if ("PAID".equalsIgnoreCase(o.getOrderStatus()) || "SUCCESS".equalsIgnoreCase(o.getPaymentStatus())) {
                        todaySales += o.getTotalAmount();
                    }
                    if ("PENDING_PAYMENT".equalsIgnoreCase(o.getOrderStatus()) || "PENDING".equalsIgnoreCase(o.getPaymentStatus())) {
                        pendingPayments++;
                    }
                }

                tvAdminSales.setText(String.format(Locale.US, "KES %.2f", todaySales));
                tvAdminPendingPayments.setText(String.valueOf(pendingPayments));
            }
        });

        // Load Products & Low Stock count (<5)
        productViewModel.getAllProducts().observe(this, resource -> {
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                List<Product> products = resource.data;
                int lowStockCount = 0;
                for (Product p : products) {
                    if (p.getStockQuantity() < 5) {
                        lowStockCount++;
                    }
                }
                tvAdminLowStockCount.setText(lowStockCount + " Items");
            }
        });
    }

    private void setupListeners() {
        btnManageProducts.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, AdminProductManagerActivity.class));
        });

        btnManageOrders.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, AdminOrderManagerActivity.class));
        });

        btnExportSalesReport.setOnClickListener(v -> exportCsvReport());

        btnAdminLogout.setOnClickListener(v -> {
            authViewModel.logout();
            Intent intent = new Intent(AdminDashboardActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void exportCsvReport() {
        adminViewModel.getAllOrders().observe(this, resource -> {
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                StringBuilder csv = new StringBuilder();
                csv.append("OrderReference,CustomerName,Phone,TotalAmount,OrderStatus,MpesaReceipt\n");
                for (Order o : resource.data) {
                    csv.append(o.getOrderReference()).append(",")
                            .append(o.getCustomerName()).append(",")
                            .append(o.getCustomerPhone()).append(",")
                            .append(o.getTotalAmount()).append(",")
                            .append(o.getOrderStatus()).append(",")
                            .append(o.getMpesaReceiptNumber() != null ? o.getMpesaReceiptNumber() : "N/A").append("\n");
                }
                try {
                    File dir = getExternalFilesDir(android.os.Environment.DIRECTORY_DOCUMENTS);
                    File csvFile = new File(dir, "Sales_Report_" + System.currentTimeMillis() + ".csv");
                    FileWriter writer = new FileWriter(csvFile);
                    writer.write(csv.toString());
                    writer.close();
                    Toast.makeText(this, "Sales CSV Report saved: " + csvFile.getName(), Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(this, "Failed to export CSV: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}