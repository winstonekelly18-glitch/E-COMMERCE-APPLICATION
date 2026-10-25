package com.example.e_commerceapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.e_commerceapplication.activities.CartActivity;
import com.example.e_commerceapplication.activities.OrderHistoryActivity;
import com.example.e_commerceapplication.activities.ProductDetailsActivity;
import com.example.e_commerceapplication.activities.ProfileActivity;
import com.example.e_commerceapplication.activities.SettingsActivity;
import com.example.e_commerceapplication.activities.WishlistActivity;
import com.example.e_commerceapplication.adapters.CategoryAdapter;
import com.example.e_commerceapplication.adapters.ProductAdapter;
import com.example.e_commerceapplication.database.LocalCartItem;
import com.example.e_commerceapplication.models.Product;
import com.example.e_commerceapplication.repository.Resource;
import com.example.e_commerceapplication.viewmodels.CartViewModel;
import com.example.e_commerceapplication.viewmodels.ProductViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * MainActivity is the customer home screen displaying product grid, category filter chips, search bar,
 * live cart badge count, and sorting options.
 */
public class MainActivity extends AppCompatActivity implements ProductAdapter.OnProductClickListener {

    private MaterialToolbar toolbar;
    private TextInputEditText etSearch;
    private RecyclerView rvCategories, rvProducts;
    private SwipeRefreshLayout swipeRefresh;
    private TextView tvEmptyState;
    private BottomNavigationView bottomNavigation;

    private ProductViewModel productViewModel;
    private CartViewModel cartViewModel;
    private ProductAdapter productAdapter;

    private List<Product> fullProductList = new ArrayList<>();
    private List<Product> displayedProductList = new ArrayList<>();
    private final List<String> categories = Arrays.asList("All", "Clothing", "Utensils", "Electronics", "Fashion", "Home & Kitchen", "Groceries", "Beauty");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);

        initViews();
        setupToolbar();
        setupRecyclerViews();
        setupListeners();
        observeCartBadge();

        loadProducts();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        etSearch = findViewById(R.id.etSearch);
        rvCategories = findViewById(R.id.rvCategories);
        rvProducts = findViewById(R.id.rvProducts);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        bottomNavigation = findViewById(R.id.bottomNavigation);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
    }

    private void setupRecyclerViews() {
        // Horizontal Categories
        CategoryAdapter categoryAdapter = new CategoryAdapter(this, categories, this::filterByCategory);
        rvCategories.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvCategories.setAdapter(categoryAdapter);

        // Disallow parent touch interception when scrolling categories horizontally
        rvCategories.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull android.view.MotionEvent e) {
                int action = e.getAction();
                if (action == android.view.MotionEvent.ACTION_DOWN || action == android.view.MotionEvent.ACTION_MOVE) {
                    rv.getParent().requestDisallowInterceptTouchEvent(true);
                }
                return false;
            }
            @Override public void onTouchEvent(@NonNull RecyclerView rv, @NonNull android.view.MotionEvent e) {}
            @Override public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {}
        });

        // Grid Products with smooth parent NestedScrollView scrolling
        productAdapter = new ProductAdapter(this, this);
        rvProducts.setLayoutManager(new GridLayoutManager(this, 2));
        rvProducts.setNestedScrollingEnabled(false);
        rvProducts.setAdapter(productAdapter);
    }

    private void setupListeners() {
        swipeRefresh.setOnRefreshListener(this::loadProducts);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterSearch(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_cart) {
                startActivity(new Intent(MainActivity.this, CartActivity.class));
                return true;
            } else if (id == R.id.nav_orders) {
                startActivity(new Intent(MainActivity.this, OrderHistoryActivity.class));
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                return true;
            }
            return true;
        });
    }

    private void observeCartBadge() {
        cartViewModel.getCartCount().observe(this, count -> {
            int badgeCount = count != null ? count : 0;
            BadgeDrawable badge = bottomNavigation.getOrCreateBadge(R.id.nav_cart);
            if (badgeCount > 0) {
                badge.setVisible(true);
                badge.setNumber(badgeCount);
            } else {
                badge.setVisible(false);
            }
        });
    }

    private void loadProducts() {
        productViewModel.getAllProducts().observe(this, resource -> {
            if (resource.status == Resource.Status.LOADING) {
                swipeRefresh.setRefreshing(true);
            } else if (resource.status == Resource.Status.SUCCESS) {
                swipeRefresh.setRefreshing(false);
                fullProductList = resource.data != null ? resource.data : new ArrayList<>();
                updateUI(fullProductList);
            } else if (resource.status == Resource.Status.ERROR) {
                swipeRefresh.setRefreshing(false);
                Toast.makeText(this, "Error loading products: " + resource.message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterByCategory(String category) {
        if ("All".equalsIgnoreCase(category)) {
            updateUI(fullProductList);
        } else {
            List<Product> filtered = new ArrayList<>();
            for (Product p : fullProductList) {
                if (category.equalsIgnoreCase(p.getCategory())) {
                    filtered.add(p);
                }
            }
            updateUI(filtered);
        }
    }

    private void filterSearch(String query) {
        if (query.trim().isEmpty()) {
            updateUI(fullProductList);
            return;
        }
        String lowerQuery = query.toLowerCase().trim();
        List<Product> filtered = new ArrayList<>();
        for (Product p : fullProductList) {
            if ((p.getName() != null && p.getName().toLowerCase().contains(lowerQuery)) ||
                (p.getCategory() != null && p.getCategory().toLowerCase().contains(lowerQuery))) {
                filtered.add(p);
            }
        }
        updateUI(filtered);
    }

    private void updateUI(List<Product> products) {
        displayedProductList = new ArrayList<>(products);
        productAdapter.setProducts(displayedProductList);
        if (displayedProductList.isEmpty()) {
            tvEmptyState.setVisibility(View.VISIBLE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
        }
    }

    private void showSortDialog() {
        String[] options = {"Price: Low to High", "Price: High to Low", "Name: A to Z"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sort Products");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) { // Price Low to High
                Collections.sort(displayedProductList, Comparator.comparingDouble(Product::getPrice));
            } else if (which == 1) { // Price High to Low
                Collections.sort(displayedProductList, (p1, p2) -> Double.compare(p2.getPrice(), p1.getPrice()));
            } else if (which == 2) { // Name A-Z
                Collections.sort(displayedProductList, (p1, p2) -> p1.getName().compareToIgnoreCase(p2.getName()));
            }
            productAdapter.setProducts(displayedProductList);
        });
        builder.show();
    }

    @Override
    public void onProductClick(Product product) {
        Intent intent = new Intent(MainActivity.this, ProductDetailsActivity.class);
        intent.putExtra("product", product);
        startActivity(intent);
    }

    @Override
    public void onAddToCartClick(Product product) {
        if (product.getStockQuantity() <= 0) {
            Toast.makeText(this, "Item is out of stock!", Toast.LENGTH_SHORT).show();
            return;
        }
        LocalCartItem cartItem = new LocalCartItem(
                product.getProductId(),
                product.getName(),
                product.getPrice(),
                1,
                product.getImageUrl(),
                product.getStockQuantity()
        );
        cartViewModel.addToCart(cartItem);

        Snackbar.make(findViewById(R.id.main), product.getName() + " added to cart!", Snackbar.LENGTH_LONG)
                .setAction("VIEW CART", v -> startActivity(new Intent(MainActivity.this, CartActivity.class)))
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem themeItem = menu.findItem(R.id.action_theme);
        if (themeItem != null) {
            themeItem.setIcon(com.example.e_commerceapplication.utils.ThemeManager.isDarkNow(this)
                    ? R.drawable.ic_light_mode
                    : R.drawable.ic_dark_mode);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_theme) {
            int newMode = com.example.e_commerceapplication.utils.ThemeManager.isDarkNow(this)
                    ? com.example.e_commerceapplication.utils.ThemeManager.LIGHT
                    : com.example.e_commerceapplication.utils.ThemeManager.DARK;
            com.example.e_commerceapplication.utils.ThemeManager.setMode(this, newMode);
            return true;
        } else if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if (id == R.id.action_sort) {
            showSortDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}