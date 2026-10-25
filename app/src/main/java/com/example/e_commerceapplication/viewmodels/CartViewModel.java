package com.example.e_commerceapplication.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.example.e_commerceapplication.database.LocalCartItem;
import com.example.e_commerceapplication.repository.CartRepository;

import java.util.List;

/**
 * ViewModel for shopping cart screen, calculating totals and managing local cart changes.
 */
public class CartViewModel extends AndroidViewModel {

    private final CartRepository cartRepository;
    private final LiveData<List<LocalCartItem>> cartItems;
    private final LiveData<Double> totalPrice;

    public CartViewModel(@NonNull Application application) {
        super(application);
        cartRepository = new CartRepository(application);
        cartItems = cartRepository.getAllCartItems();

        // Calculate total amount whenever cart items update
        totalPrice = Transformations.map(cartItems, items -> {
            double total = 0.0;
            if (items != null) {
                for (LocalCartItem item : items) {
                    total += item.getSubtotal();
                }
            }
            return total;
        });
    }

    public LiveData<List<LocalCartItem>> getCartItems() {
        return cartItems;
    }

    public LiveData<Integer> getCartCount() {
        return cartRepository.getCartCount();
    }

    public LiveData<Double> getTotalPrice() {
        return totalPrice;
    }

    public void addToCart(LocalCartItem item) {
        cartRepository.addToCart(item);
    }

    public void updateQuantity(LocalCartItem item, int newQuantity) {
        cartRepository.updateQuantity(item, newQuantity);
    }

    public void removeItem(LocalCartItem item) {
        cartRepository.removeItem(item);
    }

    public void clearCart() {
        cartRepository.clearCart();
    }
}