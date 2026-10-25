package com.example.e_commerceapplication.repository;

import android.content.Context;
import androidx.lifecycle.LiveData;

import com.example.e_commerceapplication.database.AppDatabase;
import com.example.e_commerceapplication.database.CartDao;
import com.example.e_commerceapplication.database.LocalCartItem;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Repository for managing local cart items via Room Database.
 * Runs disk operations on a background thread pool.
 */
public class CartRepository {

    private final CartDao cartDao;
    private final ExecutorService executor;

    public CartRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        cartDao = db.cartDao();
        executor = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<LocalCartItem>> getAllCartItems() {
        return cartDao.getAllCartItems();
    }

    public LiveData<Integer> getCartCount() {
        return cartDao.getCartCount();
    }

    public void addToCart(LocalCartItem item) {
        executor.execute(() -> {
            LocalCartItem existing = cartDao.getCartItemByIdSync(item.getProductId());
            if (existing != null) {
                int newQty = existing.getQuantity() + item.getQuantity();
                if (newQty > item.getMaxStock()) {
                    newQty = item.getMaxStock();
                }
                existing.setQuantity(newQty);
                cartDao.update(existing);
            } else {
                cartDao.insertOrUpdate(item);
            }
        });
    }

    public void updateQuantity(LocalCartItem item, int newQuantity) {
        executor.execute(() -> {
            if (newQuantity <= 0) {
                cartDao.delete(item);
            } else {
                item.setQuantity(Math.min(newQuantity, item.getMaxStock()));
                cartDao.update(item);
            }
        });
    }

    public void removeItem(LocalCartItem item) {
        executor.execute(() -> cartDao.delete(item));
    }

    public void clearCart() {
        executor.execute(cartDao::clearCart);
    }
}