package com.sdsmdg.bookshareapp.BSA.firebase_classes;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;
import com.sdsmdg.bookshareapp.BSA.R;
import com.sdsmdg.bookshareapp.BSA.ui.MainActivity;

import java.util.ArrayList;


/**
 * Created by ajayrahul on 1/12/16.
 */
public class MyFirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    public static ArrayList<String> notifications = new ArrayList<>();
    PendingIntent pendingIntent;
    final static String GROUP_KEY = "fcm_notifs";
    int size = 0;
    public int no_notifs = 0;


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        //check for notifs..
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message data  : " + remoteMessage.getNotification().getBody());
            String title = remoteMessage.getNotification().getTitle();
            String message = remoteMessage.getNotification().getBody();
            sendNotification(title,message);
            return;
        }


        //check if msg contains data..
        if (remoteMessage.getData().size() > 0) {
            String name = remoteMessage.getData().get("name");
            String body = remoteMessage.getData().get("body");
            String book = remoteMessage.getData().get("book");
            String title = remoteMessage.getData().get("title");
            String message = name + " " + body + " " + book;
            sendNotification(title, message);
        }


    }


    private void sendNotification(String title, String body) {

        Log.i("Reached here ", "inside send ----->");

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("data", "open");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_INCLUDE_STOPPED_PACKAGES);

        pendingIntent = PendingIntent.getActivity(this, 0 /*request code */, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //setting sound :
        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder;
        notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.logo1)
                .setAutoCancel(true)
                .setSound(notificationSound)
                .setContentIntent(pendingIntent);

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        if (notifications.size() == 0) {
            notificationBuilder.setContentTitle(title)
                    .setContentText(body);
        } else {
            size = notifications.size() + 1;
            notificationBuilder.setContentTitle(title);
            inboxStyle.setBigContentTitle("You have " + size + " Notifications.");
        }


        notifications.add(body);
        for (int i = 0; i < notifications.size(); i++) {
            inboxStyle.addLine(notifications.get(i));
        }

        notificationBuilder.setStyle(inboxStyle);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());

    }


}
