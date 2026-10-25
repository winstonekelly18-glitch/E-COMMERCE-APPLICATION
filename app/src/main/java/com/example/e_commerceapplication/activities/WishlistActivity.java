package com.example.e_commerceapplication.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_commerceapplication.R;
import com.example.e_commerceapplication.adapters.ProductAdapter;
import com.example.e_commerceapplication.database.LocalCartItem;
import com.example.e_commerceapplication.models.Product;
import com.example.e_commerceapplication.repository.Resource;
import com.example.e_commerceapplication.viewmodels.CartViewModel;
import com.example.e_commerceapplication.viewmodels.ProductViewModel;
import com.google.android.material.appbar.MaterialToolbar;

/**
 * WishlistActivity displays customer saved favorite products.
 */
public class WishlistActivity extends AppCompatActivity implements ProductAdapter.OnProductClickListener {

    private MaterialToolbar toolbarWishlist;
    private RecyclerView rvWishlist;

    private ProductViewModel productViewModel;
    private CartViewModel cartViewModel;
    private ProductAdapter productAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);

        toolbarWishlist = findViewById(R.id.toolbarWishlist);
        rvWishlist = findViewById(R.id.rvWishlist);

        setSupportActionBar(toolbarWishlist);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbarWishlist.setNavigationOnClickListener(v -> finish());

        productAdapter = new ProductAdapter(this, this);
        rvWishlist.setLayoutManager(new GridLayoutManager(this, 2));
        rvWishlist.setAdapter(productAdapter);

        loadWishlist();
    }

    private void loadWishlist() {
        productViewModel.getAllProducts().observe(this, resource -> {
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                productAdapter.setProducts(resource.data);
            }
        });
    }

    @Override
    public void onProductClick(Product product) {
        Intent intent = new Intent(WishlistActivity.this, ProductDetailsActivity.class);
        intent.putExtra("product", product);
        startActivity(intent);
    }

    @Override
    public void onAddToCartClick(Product product) {
        LocalCartItem cartItem = new LocalCartItem(
                product.getProductId(),
                product.getName(),
                product.getPrice(),
                1,
                product.getImageUrl(),
                product.getStockQuantity()
        );
        cartViewModel.addToCart(cartItem);
    }
}