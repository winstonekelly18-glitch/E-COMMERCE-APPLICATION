package com.example.e_commerceapplication.services;

import androidx.annotation.NonNull;
import com.example.e_commerceapplication.utils.NotificationHelper;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Service handling incoming Firebase Cloud Messaging (FCM) notifications.
 */
public class ShopFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String title = "ShopMpesa Notification";
        String body = "You have a new update";

        if (remoteMessage.getNotification() != null) {
            title = remoteMessage.getNotification().getTitle();
            body = remoteMessage.getNotification().getBody();
        }

        NotificationHelper.showNotification(getApplicationContext(), title, body);
    }
}