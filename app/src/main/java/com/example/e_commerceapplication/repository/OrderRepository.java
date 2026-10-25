package com.example.e_commerceapplication.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.e_commerceapplication.models.Order;
import com.example.e_commerceapplication.models.PromoCode;
import com.example.e_commerceapplication.utils.Constants;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * Repository for storing, retrieving, and updating customer orders in Firestore with safe Exception handling.
 */
public class OrderRepository {

    private FirebaseFirestore getDb() {
        try {
            return FirebaseFirestore.getInstance();
        } catch (Exception e) {
            return null;
        }
    }

    public LiveData<Resource<String>> createOrder(Order order) {
        MutableLiveData<Resource<String>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        FirebaseFirestore db = getDb();
        if (db == null) {
            result.setValue(Resource.error("Firestore unavailable"));
            return result;
        }

        // Ensure order status is initially PENDING_PAYMENT
        order.setOrderStatus(Constants.ORDER_STATUS_PENDING_PAYMENT);
        order.setPaymentStatus(Constants.PAYMENT_STATUS_PENDING);

        db.collection(Constants.COLLECTION_ORDERS)
                .document(order.getOrderReference())
                .set(order)
                .addOnSuccessListener(unused -> result.setValue(Resource.success(order.getOrderReference())))
                .addOnFailureListener(e -> result.setValue(Resource.error(e.getLocalizedMessage())));

        return result;
    }

    public LiveData<Resource<List<Order>>> getCustomerOrders(String userId) {
        MutableLiveData<Resource<List<Order>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        FirebaseFirestore db = getDb();
        if (db == null) {
            result.setValue(Resource.error("Firestore unavailable"));
            return result;
        }

        db.collection(Constants.COLLECTION_ORDERS)
                .whereEqualTo("userId", userId)
                .orderBy("orderDate", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Order> orders = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Order order = doc.toObject(Order.class);
                        if (order != null) {
                            orders.add(order);
                        }
                    }
                    result.setValue(Resource.success(orders));
                })
                .addOnFailureListener(e -> result.setValue(Resource.error(e.getLocalizedMessage())));

        return result;
    }

    public LiveData<Resource<Order>> getOrderByReference(String orderReference) {
        MutableLiveData<Resource<Order>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        FirebaseFirestore db = getDb();
        if (db == null) {
            result.setValue(Resource.error("Firestore unavailable"));
            return result;
        }

        db.collection(Constants.COLLECTION_ORDERS)
                .document(orderReference)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        Order order = doc.toObject(Order.class);
                        result.setValue(Resource.success(order));
                    } else {
                        result.setValue(Resource.error("Order not found"));
                    }
                })
                .addOnFailureListener(e -> result.setValue(Resource.error(e.getLocalizedMessage())));

        return result;
    }

    public LiveData<Resource<PromoCode>> validatePromoCode(String code) {
        MutableLiveData<Resource<PromoCode>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        FirebaseFirestore db = getDb();
        if (db == null) {
            result.setValue(Resource.error("Firestore unavailable"));
            return result;
        }

        db.collection(Constants.COLLECTION_PROMOCODES)
                .document(code.toUpperCase().trim())
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        PromoCode promo = doc.toObject(PromoCode.class);
                        if (promo != null && promo.isActive()) {
                            result.setValue(Resource.success(promo));
                        } else {
                            result.setValue(Resource.error("Promo code is inactive or expired"));
                        }
                    } else {
                        result.setValue(Resource.error("Invalid promo code"));
                    }
                })
                .addOnFailureListener(e -> result.setValue(Resource.error(e.getLocalizedMessage())));

        return result;
    }
}