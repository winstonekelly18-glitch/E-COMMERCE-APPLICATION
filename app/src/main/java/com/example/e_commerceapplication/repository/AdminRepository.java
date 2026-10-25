package com.example.e_commerceapplication.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.e_commerceapplication.models.Order;
import com.example.e_commerceapplication.models.Payment;
import com.example.e_commerceapplication.models.Product;
import com.example.e_commerceapplication.utils.Constants;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * Repository for Admin dashboard statistics, product CRUD, and order status updates with safe Exception handling.
 */
public class AdminRepository {

    private FirebaseFirestore getDb() {
        try {
            return FirebaseFirestore.getInstance();
        } catch (Exception e) {
            return null;
        }
    }

    public LiveData<Resource<List<Order>>> getAllOrders() {
        MutableLiveData<Resource<List<Order>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        FirebaseFirestore db = getDb();
        if (db == null) {
            result.setValue(Resource.error("Firestore unavailable"));
            return result;
        }

        db.collection(Constants.COLLECTION_ORDERS)
                .orderBy("orderDate", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Order> orders = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Order order = doc.toObject(Order.class);
                        if (order != null) orders.add(order);
                    }
                    result.setValue(Resource.success(orders));
                })
                .addOnFailureListener(e -> result.setValue(Resource.error(e.getLocalizedMessage())));

        return result;
    }

    public LiveData<Resource<Boolean>> updateOrderStatus(String orderRef, String newStatus) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        FirebaseFirestore db = getDb();
        if (db == null) {
            result.setValue(Resource.error("Firestore unavailable"));
            return result;
        }

        db.collection(Constants.COLLECTION_ORDERS)
                .document(orderRef)
                .update("orderStatus", newStatus)
                .addOnSuccessListener(unused -> result.setValue(Resource.success(true)))
                .addOnFailureListener(e -> result.setValue(Resource.error(e.getLocalizedMessage())));

        return result;
    }

    public LiveData<Resource<Boolean>> saveProduct(Product product) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        FirebaseFirestore db = getDb();
        if (db == null) {
            result.setValue(Resource.error("Firestore unavailable"));
            return result;
        }

        String docId = product.getProductId();
        if (docId == null || docId.isEmpty()) {
            docId = db.collection(Constants.COLLECTION_PRODUCTS).document().getId();
            product.setProductId(docId);
        }

        db.collection(Constants.COLLECTION_PRODUCTS)
                .document(docId)
                .set(product)
                .addOnSuccessListener(unused -> result.setValue(Resource.success(true)))
                .addOnFailureListener(e -> result.setValue(Resource.error(e.getLocalizedMessage())));

        return result;
    }

    public LiveData<Resource<Boolean>> deleteProduct(String productId) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        FirebaseFirestore db = getDb();
        if (db == null) {
            result.setValue(Resource.error("Firestore unavailable"));
            return result;
        }

        db.collection(Constants.COLLECTION_PRODUCTS)
                .document(productId)
                .delete()
                .addOnSuccessListener(unused -> result.setValue(Resource.success(true)))
                .addOnFailureListener(e -> result.setValue(Resource.error(e.getLocalizedMessage())));

        return result;
    }

    public LiveData<Resource<List<Payment>>> getAllPayments() {
        MutableLiveData<Resource<List<Payment>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        FirebaseFirestore db = getDb();
        if (db == null) {
            result.setValue(Resource.error("Firestore unavailable"));
            return result;
        }

        db.collection(Constants.COLLECTION_PAYMENTS)
                .orderBy("transactionDate", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Payment> payments = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Payment payment = doc.toObject(Payment.class);
                        if (payment != null) payments.add(payment);
                    }
                    result.setValue(Resource.success(payments));
                })
                .addOnFailureListener(e -> result.setValue(Resource.error(e.getLocalizedMessage())));

        return result;
    }
}