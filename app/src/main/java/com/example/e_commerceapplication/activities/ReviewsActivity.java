package com.example.e_commerceapplication.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_commerceapplication.R;
import com.example.e_commerceapplication.adapters.ReviewAdapter;
import com.example.e_commerceapplication.repository.Resource;
import com.example.e_commerceapplication.viewmodels.ProductViewModel;
import com.google.android.material.appbar.MaterialToolbar;

/**
 * ReviewsActivity lists customer ratings and reviews for a product.
 */
public class ReviewsActivity extends AppCompatActivity {

    private MaterialToolbar toolbarReviews;
    private RecyclerView rvReviewsList;

    private ProductViewModel productViewModel;
    private ReviewAdapter reviewAdapter;
    private String productId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);

        productId = getIntent().getStringExtra("productId");
        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);

        toolbarReviews = findViewById(R.id.toolbarReviews);
        rvReviewsList = findViewById(R.id.rvReviewsList);

        setSupportActionBar(toolbarReviews);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbarReviews.setNavigationOnClickListener(v -> finish());

        reviewAdapter = new ReviewAdapter(this);
        rvReviewsList.setLayoutManager(new LinearLayoutManager(this));
        rvReviewsList.setAdapter(reviewAdapter);

        if (productId != null) {
            productViewModel.getProductReviews(productId).observe(this, resource -> {
                if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                    reviewAdapter.setReviews(resource.data);
                }
            });
        }
    }
}