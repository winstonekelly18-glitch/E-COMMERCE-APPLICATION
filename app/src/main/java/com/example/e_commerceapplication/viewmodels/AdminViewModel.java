package com.example.e_commerceapplication.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.e_commerceapplication.models.Order;
import com.example.e_commerceapplication.models.Payment;
import com.example.e_commerceapplication.models.Product;
import com.example.e_commerceapplication.repository.AdminRepository;
import com.example.e_commerceapplication.repository.Resource;

import java.util.List;

/**
 * ViewModel managing Admin dashboard metrics, product management, and order status updates.
 */
public class AdminViewModel extends AndroidViewModel {

    private final AdminRepository adminRepository;

    public AdminViewModel(@NonNull Application application) {
        super(application);
        adminRepository = new AdminRepository();
    }

    public LiveData<Resource<List<Order>>> getAllOrders() {
        return adminRepository.getAllOrders();
    }

    public LiveData<Resource<Boolean>> updateOrderStatus(String orderRef, String newStatus) {
        return adminRepository.updateOrderStatus(orderRef, newStatus);
    }

    public LiveData<Resource<Boolean>> saveProduct(Product product) {
        return adminRepository.saveProduct(product);
    }

    public LiveData<Resource<Boolean>> deleteProduct(String productId) {
        return adminRepository.deleteProduct(productId);
    }

    public LiveData<Resource<List<Payment>>> getAllPayments() {
        return adminRepository.getAllPayments();
    }
}