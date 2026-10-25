package com.example.e_commerceapplication.activities;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.e_commerceapplication.R;
import com.example.e_commerceapplication.adapters.ReviewAdapter;
import com.example.e_commerceapplication.database.LocalCartItem;
import com.example.e_commerceapplication.models.Product;
import com.example.e_commerceapplication.repository.Resource;
import com.example.e_commerceapplication.viewmodels.AuthViewModel;
import com.example.e_commerceapplication.viewmodels.CartViewModel;
import com.example.e_commerceapplication.viewmodels.ProductViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;

/**
 * Displays full product information, quantity selection, stock status, reviews, and wishlist toggle.
 */
public class ProductDetailsActivity extends AppCompatActivity {

    private ImageView ivDetailProductImage;
    private ImageButton btnWishlistToggle, btnQtyDecrease, btnQtyIncrease;
    private TextView tvDetailProductName, tvDetailProductPrice, tvStockStatus, tvDetailProductCategory, tvDetailDescription, tvDetailQuantity;
    private MaterialButton btnAddToCartDetail;
    private RecyclerView rvReviews;

    private Product product;
    private int selectedQuantity = 1;
    private boolean isWishlisted = false;

    private ProductViewModel productViewModel;
    private CartViewModel cartViewModel;
    private AuthViewModel authViewModel;
    private ReviewAdapter reviewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        product = (Product) getIntent().getSerializableExtra("product");
        if (product == null) {
            Toast.makeText(this, "Product details not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        initViews();
        bindProductData();
        setupListeners();
        loadReviews();
    }

    private void initViews() {
        ivDetailProductImage = findViewById(R.id.ivDetailProductImage);
        btnWishlistToggle = findViewById(R.id.btnWishlistToggle);
        btnQtyDecrease = findViewById(R.id.btnQtyDecrease);
        btnQtyIncrease = findViewById(R.id.btnQtyIncrease);

        tvDetailProductName = findViewById(R.id.tvDetailProductName);
        tvDetailProductPrice = findViewById(R.id.tvDetailProductPrice);
        tvStockStatus = findViewById(R.id.tvStockStatus);
        tvDetailProductCategory = findViewById(R.id.tvDetailProductCategory);
        tvDetailDescription = findViewById(R.id.tvDetailDescription);
        tvDetailQuantity = findViewById(R.id.tvDetailQuantity);

        btnAddToCartDetail = findViewById(R.id.btnAddToCartDetail);
        rvReviews = findViewById(R.id.rvReviews);

        reviewAdapter = new ReviewAdapter(this);
        rvReviews.setLayoutManager(new LinearLayoutManager(this));
        rvReviews.setAdapter(reviewAdapter);
    }

    private void bindProductData() {
        tvDetailProductName.setText(product.getName());
        tvDetailProductPrice.setText(String.format(Locale.US, "KES %.2f", product.getPrice()));
        tvDetailProductCategory.setText("Category: " + product.getCategory());
        tvDetailDescription.setText(product.getDescription());

        if (product.getStockQuantity() <= 0) {
            tvStockStatus.setText("OUT OF STOCK");
            tvStockStatus.setBackgroundColor(getColor(R.color.low_stock_red));
            btnAddToCartDetail.setEnabled(false);
            btnAddToCartDetail.setText("Out of Stock");
        } else {
            tvStockStatus.setText(String.format(Locale.US, "In Stock (%d)", product.getStockQuantity()));
            btnAddToCartDetail.setEnabled(true);
        }

        Glide.with(this)
                .load(product.getImageUrl())
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(ivDetailProductImage);
    }

    private void setupListeners() {
        btnQtyIncrease.setOnClickListener(v -> {
            if (selectedQuantity < product.getStockQuantity()) {
                selectedQuantity++;
                tvDetailQuantity.setText(String.valueOf(selectedQuantity));
            } else {
                Toast.makeText(this, "Cannot select more than available stock (" + product.getStockQuantity() + ")", Toast.LENGTH_SHORT).show();
            }
        });

        btnQtyDecrease.setOnClickListener(v -> {
            if (selectedQuantity > 1) {
                selectedQuantity--;
                tvDetailQuantity.setText(String.valueOf(selectedQuantity));
            }
        });

        btnWishlistToggle.setOnClickListener(v -> {
            FirebaseUser currentUser = authViewModel.getCurrentUser();
            if (currentUser != null) {
                productViewModel.toggleWishlist(currentUser.getUid(), product.getProductId(), isWishlisted)
                        .observe(this, resource -> {
                            if (resource.status == Resource.Status.SUCCESS) {
                                isWishlisted = resource.data != null && resource.data;
                                Toast.makeText(this, isWishlisted ? "Added to Wishlist!" : "Removed from Wishlist!", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(this, "Please log in to add items to wishlist", Toast.LENGTH_SHORT).show();
            }
        });

        btnAddToCartDetail.setOnClickListener(v -> {
            LocalCartItem cartItem = new LocalCartItem(
                    product.getProductId(),
                    product.getName(),
                    product.getPrice(),
                    selectedQuantity,
                    product.getImageUrl(),
                    product.getStockQuantity()
            );
            cartViewModel.addToCart(cartItem);
            Toast.makeText(this, selectedQuantity + "x " + product.getName() + " added to cart!", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadReviews() {
        productViewModel.getProductReviews(product.getProductId()).observe(this, resource -> {
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                reviewAdapter.setReviews(resource.data);
            }
        });
    }
}