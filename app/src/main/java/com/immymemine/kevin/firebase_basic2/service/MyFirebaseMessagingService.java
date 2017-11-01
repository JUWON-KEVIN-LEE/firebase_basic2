package com.immymemine.kevin.firebase_basic2.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.immymemine.kevin.firebase_basic2.R;
import com.immymemine.kevin.firebase_basic2.activity.StorageActivity;

/**
 * Created by quf93 on 2017-11-01.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "Messaging Service";

    public MyFirebaseMessagingService() {
    }

    // call this method when app is running on the screen
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "FCM Data Message : " + remoteMessage.getData());
        }


        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            sendNotification(remoteMessage.getNotification().getBody());
        }
    }

    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, StorageActivity.class);
        // activity stack : 0 > 1 > 2 > 3
        // activity(3) 에서 (1) 을 FLAG_ACTIVITY_CLEAR_TOP flag 를 달아 호출하면
        // [ 0 > 1 ] 까지 남고 [ 2 > 3 ] 부분 onDestroy
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /*request code*/,
                intent, PendingIntent.FLAG_ONE_SHOT);

        String channelId = "DEFAULT CHANNEL";

        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId);
        builder.setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle("FCM Message")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSound)
                .setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        int notificationId = 0;
        Log.d("messageBody = ", messageBody);
        manager.notify(notificationId,builder.build());
    }
}

