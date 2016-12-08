package com.sdsmdg.bookshareapp.BSA.firebase_classes;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;
import com.sdsmdg.bookshareapp.BSA.R;
import com.sdsmdg.bookshareapp.BSA.ui.MainActivity;


/**
 * Created by ajayrahul on 1/12/16.
 */
public class MyFirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    PendingIntent pendingIntent;

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "FCMSERVICE DESTROOOOOOYED ");

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "FCMSERVICE STARTED ");
//        sendNotification("Notif created","Yup");


    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "FROM : " + remoteMessage.getFrom());


        //check if msg contains data..
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data  : " + remoteMessage.getData());
            String name = remoteMessage.getData().get("name");
            String body = remoteMessage.getData().get("body");
            String book = remoteMessage.getData().get("book");
            String title = remoteMessage.getData().get("title");
            String message = name + " " + body + " " + book;
            String x = remoteMessage.getNotification().getBody();
            Log.i(TAG,x+"-------kkn");


            sendNotification(title, message);


        }

        //chck for notifs..
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message data  : " + remoteMessage.getNotification().getBody());

//            sendNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());


        }

    }


        private void sendNotification(String title, String body) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("data", "open");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            pendingIntent = PendingIntent.getActivity(this, 0 /*request code */, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            //setting sound :
            Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Notification notification = new NotificationCompat.Builder(this).setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setAutoCancel(true)
                    .setSound(notificationSound)
                    .setContentIntent(pendingIntent)
                    .build();


            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0/*id of notif*/, notification);


        }




    @Override
    public void onTaskRemoved(Intent rootIntent){
        Log.d(TAG, " onTaskRemoved Callled  ");

        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.setPackage(getPackageName());

        PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 1000,
                restartServicePendingIntent);

        super.onTaskRemoved(rootIntent);
    }


}
