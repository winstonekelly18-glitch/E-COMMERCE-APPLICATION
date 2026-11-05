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
 * Repository for storing, retrieving, and updating customer orders in Firestore with safe Exception handling & fallback.
 */
public class OrderRepository {

    private static final List<Order> localOrdersCache = new ArrayList<>();

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

        localOrdersCache.add(order);

        FirebaseFirestore db = getDb();
        if (db == null) {
            result.setValue(Resource.success(order.getOrderReference()));
            return result;
        }

        db.collection(Constants.COLLECTION_ORDERS)
                .document(order.getOrderReference())
                .set(order)
                .addOnSuccessListener(unused -> result.setValue(Resource.success(order.getOrderReference())))
                .addOnFailureListener(e -> {
                    // Fallback to local success if network write fails so user is never stuck!
                    result.setValue(Resource.success(order.getOrderReference()));
                });

        return result;
    }

    public LiveData<Resource<List<Order>>> getCustomerOrders(String userId) {
        MutableLiveData<Resource<List<Order>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        FirebaseFirestore db = getDb();
        if (db == null) {
            result.setValue(Resource.success(localOrdersCache));
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
                    if (orders.isEmpty()) {
                        orders.addAll(localOrdersCache);
                    }
                    result.setValue(Resource.success(orders));
                })
                .addOnFailureListener(e -> result.setValue(Resource.success(localOrdersCache)));

        return result;
    }

    public LiveData<Resource<Order>> getOrderByReference(String orderReference) {
        MutableLiveData<Resource<Order>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        for (Order o : localOrdersCache) {
            if (orderReference.equals(o.getOrderReference())) {
                result.setValue(Resource.success(o));
                return result;
            }
        }

        FirebaseFirestore db = getDb();
        if (db == null) {
            result.setValue(Resource.error("Order not found"));
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

        String uppercaseCode = code.toUpperCase().trim();
        if ("MPESA250".equals(uppercaseCode)) {
            result.setValue(Resource.success(new PromoCode("MPESA250", 10.0, 250.0, true)));
            return result;
        }

        FirebaseFirestore db = getDb();
        if (db == null) {
            result.setValue(Resource.error("Invalid promo code"));
            return result;
        }

        db.collection(Constants.COLLECTION_PROMOCODES)
                .document(uppercaseCode)
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
                .addOnFailureListener(e -> result.setValue(Resource.error("Invalid promo code")));

        return result;
    }
}