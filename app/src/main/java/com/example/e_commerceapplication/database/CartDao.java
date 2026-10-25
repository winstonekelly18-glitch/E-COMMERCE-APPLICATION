package com.example.e_commerceapplication.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * Data Access Object (DAO) for local cart database operations.
 */
@Dao
public interface CartDao {

    @Query("SELECT * FROM cart_items")
    LiveData<List<LocalCartItem>> getAllCartItems();

    @Query("SELECT * FROM cart_items WHERE productId = :productId LIMIT 1")
    LocalCartItem getCartItemByIdSync(String productId);

    @Query("SELECT COUNT(*) FROM cart_items")
    LiveData<Integer> getCartCount();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(LocalCartItem item);

    @Update
    void update(LocalCartItem item);

    @Delete
    void delete(LocalCartItem item);

    @Query("DELETE FROM cart_items")
    void clearCart();
}