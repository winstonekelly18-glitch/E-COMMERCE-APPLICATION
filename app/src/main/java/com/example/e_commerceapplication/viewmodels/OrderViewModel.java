package com.example.e_commerceapplication.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.e_commerceapplication.models.Order;
import com.example.e_commerceapplication.models.PromoCode;
import com.example.e_commerceapplication.repository.OrderRepository;
import com.example.e_commerceapplication.repository.Resource;

import java.util.List;

/**
 * ViewModel managing checkout order creation, retrieving past orders, and validating promo codes.
 */
public class OrderViewModel extends AndroidViewModel {

    private final OrderRepository orderRepository;

    public OrderViewModel(@NonNull Application application) {
        super(application);
        orderRepository = new OrderRepository();
    }

    public LiveData<Resource<String>> createOrder(Order order) {
        return orderRepository.createOrder(order);
    }

    public LiveData<Resource<List<Order>>> getCustomerOrders(String userId) {
        return orderRepository.getCustomerOrders(userId);
    }

    public LiveData<Resource<Order>> getOrderByReference(String orderReference) {
        return orderRepository.getOrderByReference(orderReference);
    }

    public LiveData<Resource<PromoCode>> validatePromoCode(String code) {
        return orderRepository.validatePromoCode(code);
    }
}