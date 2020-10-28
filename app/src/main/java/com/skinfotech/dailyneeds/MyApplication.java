package com.skinfotech.dailyneeds;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class MyApplication extends Application {
   public static Integer selectedPosition = 2;
   public static final String FCM_CHANNEL_ID = "FCM_CHANNEL_ID";
   private static final String FCM_CHANNEL_NAME = "MJWEL NOTIFICATION CHANNEL";

   @Override
   public void onCreate() {
      super.onCreate();
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
         NotificationChannel notificationChannel = new NotificationChannel(
                 FCM_CHANNEL_ID,
                 FCM_CHANNEL_NAME,
                 NotificationManager.IMPORTANCE_HIGH
         );
         NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
         if (manager != null) {
            manager.createNotificationChannel(notificationChannel);
         }
      }
   }
}
