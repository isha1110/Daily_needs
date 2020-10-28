package com.skinfotech.dailyneeds;

import android.app.Notification;
import android.app.NotificationManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static com.skinfotech.dailyneeds.MyApplication.FCM_CHANNEL_ID;

public class FCMNotificationService extends FirebaseMessagingService {

    private static final String TAG = "FCMNotificationService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "onMessageReceived: ");
        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
            Notification notification = new NotificationCompat.Builder(this, FCM_CHANNEL_ID)
                .setSmallIcon(R.drawable.app_logo)
                .setContentTitle(title)
                .setContentText(body)
                .setColor(ContextCompat.getColor(this, R.color.colorPrimaryDark)).build();
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (null != manager) {
                manager.notify(1001, notification);
            }
        }
    }

    @Override
    public void onDeletedMessages() {
        Log.d(TAG, "onDeletedMessages: ");
    }

    @Override
    public void onNewToken(String s) {
        Log.d(TAG, "onNewToken: " + s);
    }
}
