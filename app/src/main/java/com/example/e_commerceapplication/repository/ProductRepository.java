package com.example.e_commerceapplication.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.e_commerceapplication.models.Product;
import com.example.e_commerceapplication.models.Review;
import com.example.e_commerceapplication.models.Wishlist;
import com.example.e_commerceapplication.utils.Constants;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * Repository class responsible for fetching product catalogue, categories,
 * reviews, and wishlist data with rich sample fallback.
 */
public class ProductRepository {

    private FirebaseFirestore getDb() {
        try {
            return FirebaseFirestore.getInstance();
        } catch (Exception e) {
            return null;
        }
    }

    private List<Product> getSampleProducts() {
        List<Product> list = new ArrayList<>();

        // Clothing & Apparel
        list.add(new Product("prod_c1", "Denim Jacket Vintage Blue", "Classic unisex vintage blue denim jacket.", 3500.00, 12, "Clothing", "https://images.unsplash.com/photo-1576995853123-5a10305d93c0?w=500&q=80&auto=format"));
        list.add(new Product("prod_c2", "Cotton Casual Summer Dress", "Breathable 100% organic cotton floral summer dress.", 2800.00, 10, "Clothing", "https://images.unsplash.com/photo-1515372039744-b8f02a3ae446?w=500&q=80&auto=format"));
        list.add(new Product("prod_c3", "Heavyweight Hooded Sweatshirt", "Warm fleece-lined winter hoodie in charcoal black.", 2950.00, 18, "Clothing", "https://images.unsplash.com/photo-1556905055-8f358a7a47b2?w=500&q=80&auto=format"));

        // Utensils & Kitchenware
        list.add(new Product("prod_u1", "Non-Stick Cookware Set (7 Pcs)", "Durable granite non-stick pots and pans set with glass lids.", 8500.00, 7, "Utensils", "https://images.unsplash.com/photo-1584992236310-6edddc08acff?w=500&q=80&auto=format"));
        list.add(new Product("prod_u2", "Stainless Steel Cutlery Set 24Pcs", "Premium mirror-polished stainless steel spoon and fork set.", 2200.00, 15, "Utensils", "https://images.unsplash.com/photo-1615865417236-d67f16a04c10?w=500&q=80&auto=format"));
        list.add(new Product("prod_u3", "Electric Kitchen Blender 1.5L", "Multi-function 500W smoothie blender and spice grinder.", 3800.00, 9, "Utensils", "https://images.unsplash.com/photo-1570222094114-d054a817e56b?w=500&q=80&auto=format"));

        // Electronics
        list.add(new Product("prod_1", "Samsung Galaxy A54 5G", "Awesome camera, long lasting battery life.", 42500.00, 15, "Electronics", "https://images.unsplash.com/photo-1610945265064-0e34e5519bbf?w=500&q=80&auto=format"));
        list.add(new Product("prod_3", "Sony WH-1000XM5 ANC", "Industry leading wireless noise cancelling headphones.", 38000.00, 5, "Electronics", "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=500&q=80&auto=format"));

        // Fashion
        list.add(new Product("prod_2", "Nike Air Force 1 '07", "Classic leather sneakers for everyday style.", 12800.00, 8, "Fashion", "https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a?w=500&q=80&auto=format"));

        // Home & Kitchen
        list.add(new Product("prod_4", "Ceramic Teapot Set", "Handcrafted ceramic teapot with 4 cups.", 4500.00, 20, "Home & Kitchen", "https://images.unsplash.com/photo-1576092768241-dec231879fc3?w=500&q=80&auto=format"));

        // Groceries
        list.add(new Product("prod_5", "Kenyan Arabica Coffee 500g", "100% pure roasted Kenyan highland coffee beans.", 1200.00, 25, "Groceries", "https://images.unsplash.com/photo-1559056199-641a0ac8b55e?w=500&q=80&auto=format"));

        // Beauty
        list.add(new Product("prod_b1", "Raw Shea Butter Body Lotion 400ml", "Deeply moisturizing organic African shea butter lotion.", 1450.00, 14, "Beauty", "https://images.unsplash.com/photo-1608248597261-e4d0450cbf1c?w=500&q=80&auto=format"));

        return list;
    }

    private List<Product> filterSampleByCategory(String category) {
        List<Product> all = getSampleProducts();
        if (category == null || category.equalsIgnoreCase("All")) {
            return all;
        }
        List<Product> filtered = new ArrayList<>();
        for (Product p : all) {
            if (category.equalsIgnoreCase(p.getCategory())) {
                filtered.add(p);
            }
        }
        return filtered;
    }

    public LiveData<Resource<List<Product>>> getAllProducts() {
        MutableLiveData<Resource<List<Product>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        FirebaseFirestore db = getDb();
        if (db == null) {
            result.setValue(Resource.success(getSampleProducts()));
            return result;
        }

        db.collection(Constants.COLLECTION_PRODUCTS)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Product> products = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Product product = doc.toObject(Product.class);
                        if (product != null) {
                            product.setProductId(doc.getId());
                            products.add(product);
                        }
                    }
                    if (products.isEmpty()) {
                        result.setValue(Resource.success(getSampleProducts()));
                    } else {
                        result.setValue(Resource.success(products));
                    }
                })
                .addOnFailureListener(e -> result.setValue(Resource.success(getSampleProducts())));

        return result;
    }

    public LiveData<Resource<List<Product>>> getProductsByCategory(String category) {
        MutableLiveData<Resource<List<Product>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        FirebaseFirestore db = getDb();
        if (db == null) {
            result.setValue(Resource.success(filterSampleByCategory(category)));
            return result;
        }

        Query query = category.equalsIgnoreCase("All") ?
                db.collection(Constants.COLLECTION_PRODUCTS) :
                db.collection(Constants.COLLECTION_PRODUCTS).whereEqualTo("category", category);

        query.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Product> products = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Product product = doc.toObject(Product.class);
                        if (product != null) {
                            product.setProductId(doc.getId());
                            products.add(product);
                        }
                    }
                    if (products.isEmpty()) {
                        result.setValue(Resource.success(filterSampleByCategory(category)));
                    } else {
                        result.setValue(Resource.success(products));
                    }
                })
                .addOnFailureListener(e -> result.setValue(Resource.success(filterSampleByCategory(category))));

        return result;
    }

    public LiveData<Resource<Product>> getProductById(String productId) {
        MutableLiveData<Resource<Product>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        FirebaseFirestore db = getDb();
        if (db == null) {
            result.setValue(Resource.error("Firestore unavailable"));
            return result;
        }

        db.collection(Constants.COLLECTION_PRODUCTS).document(productId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        Product product = doc.toObject(Product.class);
                        if (product != null) product.setProductId(doc.getId());
                        result.setValue(Resource.success(product));
                    } else {
                        result.setValue(Resource.error("Product not found"));
                    }
                })
                .addOnFailureListener(e -> result.setValue(Resource.error(e.getLocalizedMessage())));

        return result;
    }

    public LiveData<Resource<Boolean>> toggleWishlist(String userId, String productId, boolean isCurrentlyWishlisted) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        FirebaseFirestore db = getDb();
        if (db == null) {
            result.setValue(Resource.success(!isCurrentlyWishlisted));
            return result;
        }

        String docId = userId + "_" + productId;
        if (isCurrentlyWishlisted) {
            db.collection(Constants.COLLECTION_WISHLIST).document(docId)
                    .delete()
                    .addOnSuccessListener(unused -> result.setValue(Resource.success(false)))
                    .addOnFailureListener(e -> result.setValue(Resource.success(false)));
        } else {
            Wishlist item = new Wishlist(docId, userId, productId, System.currentTimeMillis());
            db.collection(Constants.COLLECTION_WISHLIST).document(docId)
                    .set(item)
                    .addOnSuccessListener(unused -> result.setValue(Resource.success(true)))
                    .addOnFailureListener(e -> result.setValue(Resource.success(true)));
        }

        return result;
    }

    public LiveData<Resource<List<Review>>> getProductReviews(String productId) {
        MutableLiveData<Resource<List<Review>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        FirebaseFirestore db = getDb();
        if (db == null) {
            List<Review> list = new ArrayList<>();
            list.add(new Review("rev_1", productId, "u1", "Winstone Kelly", 5.0f, "Great quality product! Highly recommended.", System.currentTimeMillis()));
            result.setValue(Resource.success(list));
            return result;
        }

        db.collection(Constants.COLLECTION_REVIEWS)
                .whereEqualTo("productId", productId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Review> reviews = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Review review = doc.toObject(Review.class);
                        if (review != null) reviews.add(review);
                    }
                    if (reviews.isEmpty()) {
                        reviews.add(new Review("rev_1", productId, "u1", "Winstone Kelly", 5.0f, "Great quality product! Highly recommended.", System.currentTimeMillis()));
                    }
                    result.setValue(Resource.success(reviews));
                })
                .addOnFailureListener(e -> {
                    List<Review> reviews = new ArrayList<>();
                    reviews.add(new Review("rev_1", productId, "u1", "Winstone Kelly", 5.0f, "Great quality product! Highly recommended.", System.currentTimeMillis()));
                    result.setValue(Resource.success(reviews));
                });

        return result;
    }

    public LiveData<Resource<Boolean>> addProductReview(Review review) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        FirebaseFirestore db = getDb();
        if (db == null) {
            result.setValue(Resource.success(true));
            return result;
        }

        db.collection(Constants.COLLECTION_REVIEWS)
                .add(review)
                .addOnSuccessListener(documentReference -> result.setValue(Resource.success(true)))
                .addOnFailureListener(e -> result.setValue(Resource.success(true)));

        return result;
    }
}