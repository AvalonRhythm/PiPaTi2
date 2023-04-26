package com.example.pipati;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
        }
        if (remoteMessage.getNotification() != null) {
        }
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
    }
}
