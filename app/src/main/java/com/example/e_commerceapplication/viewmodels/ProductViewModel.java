package com.example.e_commerceapplication.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.e_commerceapplication.models.Product;
import com.example.e_commerceapplication.models.Review;
import com.example.e_commerceapplication.repository.ProductRepository;
import com.example.e_commerceapplication.repository.Resource;

import java.util.List;

/**
 * ViewModel managing product catalogue data, category filtering, and product reviews.
 */
public class ProductViewModel extends AndroidViewModel {

    private final ProductRepository productRepository;

    public ProductViewModel(@NonNull Application application) {
        super(application);
        productRepository = new ProductRepository();
    }

    public LiveData<Resource<List<Product>>> getAllProducts() {
        return productRepository.getAllProducts();
    }

    public LiveData<Resource<List<Product>>> getProductsByCategory(String category) {
        return productRepository.getProductsByCategory(category);
    }

    public LiveData<Resource<Product>> getProductById(String productId) {
        return productRepository.getProductById(productId);
    }

    public LiveData<Resource<Boolean>> toggleWishlist(String userId, String productId, boolean isCurrentlyWishlisted) {
        return productRepository.toggleWishlist(userId, productId, isCurrentlyWishlisted);
    }

    public LiveData<Resource<List<Review>>> getProductReviews(String productId) {
        return productRepository.getProductReviews(productId);
    }

    public LiveData<Resource<Boolean>> addProductReview(Review review) {
        return productRepository.addProductReview(review);
    }
}