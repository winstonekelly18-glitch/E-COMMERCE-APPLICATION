package com.example.e_commerceapplication.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_commerceapplication.R;
import com.example.e_commerceapplication.adapters.ProductAdapter;
import com.example.e_commerceapplication.models.Product;
import com.example.e_commerceapplication.repository.Resource;
import com.example.e_commerceapplication.viewmodels.AdminViewModel;
import com.example.e_commerceapplication.viewmodels.ProductViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Arrays;
import java.util.List;

/**
 * AdminProductManagerActivity allows adding, editing, and deleting products in the catalogue.
 */
public class AdminProductManagerActivity extends AppCompatActivity implements ProductAdapter.OnProductClickListener {

    private MaterialToolbar toolbarAdminProducts;
    private RecyclerView rvAdminProducts;
    private FloatingActionButton fabAddProduct;

    private ProductViewModel productViewModel;
    private AdminViewModel adminViewModel;
    private ProductAdapter productAdapter;

    private final List<String> categories = Arrays.asList("Clothing", "Utensils", "Electronics", "Fashion", "Home & Kitchen", "Groceries", "Beauty");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_product_manager);

        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);
        adminViewModel = new ViewModelProvider(this).get(AdminViewModel.class);

        initViews();
        setupRecyclerView();
        loadProducts();

        fabAddProduct.setOnClickListener(v -> showAddEditProductDialog(null));
    }

    private void initViews() {
        toolbarAdminProducts = findViewById(R.id.toolbarAdminProducts);
        rvAdminProducts = findViewById(R.id.rvAdminProducts);
        fabAddProduct = findViewById(R.id.fabAddProduct);

        setSupportActionBar(toolbarAdminProducts);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbarAdminProducts.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        productAdapter = new ProductAdapter(this, this);
        rvAdminProducts.setLayoutManager(new GridLayoutManager(this, 2));
        rvAdminProducts.setAdapter(productAdapter);
    }

    private void loadProducts() {
        productViewModel.getAllProducts().observe(this, resource -> {
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                productAdapter.setProducts(resource.data);
            }
        });
    }

    @Override
    public void onProductClick(Product product) {
        showAddEditProductDialog(product);
    }

    @Override
    public void onAddToCartClick(Product product) {
        showAddEditProductDialog(product);
    }

    private void showAddEditProductDialog(Product existing) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_product, null);
        builder.setView(view);

        TextInputEditText etName = view.findViewById(R.id.etDialogName);
        TextInputEditText etPrice = view.findViewById(R.id.etDialogPrice);
        TextInputEditText etStock = view.findViewById(R.id.etDialogStock);
        TextInputEditText etDescription = view.findViewById(R.id.etDialogDescription);
        TextInputEditText etImageUrl = view.findViewById(R.id.etDialogImageUrl);
        AutoCompleteTextView actvCategory = view.findViewById(R.id.actvCategory);

        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, categories);
        actvCategory.setAdapter(catAdapter);

        if (existing != null) {
            etName.setText(existing.getName());
            etPrice.setText(String.valueOf(existing.getPrice()));
            etStock.setText(String.valueOf(existing.getStockQuantity()));
            etDescription.setText(existing.getDescription());
            etImageUrl.setText(existing.getImageUrl());
            actvCategory.setText(existing.getCategory(), false);
        }

        builder.setTitle(existing == null ? "Add New Product" : "Edit Product");
        builder.setPositiveButton("Save", (dialog, which) -> {
            String name = etName.getText() != null ? etName.getText().toString().trim() : "";
            double price = Double.parseDouble(etPrice.getText() != null && !etPrice.getText().toString().isEmpty() ? etPrice.getText().toString() : "0");
            int stock = Integer.parseInt(etStock.getText() != null && !etStock.getText().toString().isEmpty() ? etStock.getText().toString() : "0");
            String desc = etDescription.getText() != null ? etDescription.getText().toString().trim() : "";
            String imgUrl = etImageUrl.getText() != null ? etImageUrl.getText().toString().trim() : "";
            String category = actvCategory.getText() != null ? actvCategory.getText().toString() : "General";

            Product prod = existing != null ? existing : new Product();
            prod.setName(name);
            prod.setPrice(price);
            prod.setStockQuantity(stock);
            prod.setDescription(desc);
            prod.setImageUrl(imgUrl);
            prod.setCategory(category);

            adminViewModel.saveProduct(prod).observe(this, resource -> {
                if (resource.status == Resource.Status.SUCCESS) {
                    Toast.makeText(this, "Product saved successfully!", Toast.LENGTH_SHORT).show();
                    loadProducts();
                }
            });
        });

        if (existing != null) {
            builder.setNeutralButton("Delete", (dialog, which) -> {
                adminViewModel.deleteProduct(existing.getProductId()).observe(this, resource -> {
                    if (resource.status == Resource.Status.SUCCESS) {
                        Toast.makeText(this, "Product deleted!", Toast.LENGTH_SHORT).show();
                        loadProducts();
                    }
                });
            });
        }

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}