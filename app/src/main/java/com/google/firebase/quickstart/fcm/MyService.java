package com.google.firebase.quickstart.fcm;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import static android.app.Service.START_STICKY;

/**
 * Created by akash on 20-Dec-17.
 */
public class MyService extends Service {
    static final int NOTIFICATION_ID = 543;
    final String TAG = "MyService";

    public static boolean isServiceRunning = false;

    @Override
    public void onCreate() {
        super.onCreate();
        startServiceWithNotification();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            startServiceWithNotification();
            Log.d(TAG, "onStartCommand, startServiceNotif called");
        }
        else stopMyService();

        Log.d(TAG, "outside if else, in onStartCommand");
        return START_STICKY;
    }

    // In case the service is deleted or crashes some how
    @Override
    public void onDestroy() {
        isServiceRunning = false;
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Used only in case of bound services.
        return null;
    }


    void startServiceWithNotification() {
        if (isServiceRunning) return;
        isServiceRunning = true;

        Intent notificationIntent = new Intent(getApplicationContext(), Notif.class);
//        notificationIntent.setAction(C.ACTION_MAIN);  // A string containing the action name
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent contentPendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_stat_ic_notification);

        Notification notification = new NotificationCompat.Builder(this, "channel01")
                .setContentTitle(getResources().getString(R.string.app_name))
                .setTicker(getResources().getString(R.string.app_name))
                .setContentText("yo foreground service")
                .setSmallIcon(R.drawable.ic_stat_ic_notification)
                .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                .setContentIntent(contentPendingIntent)
                .setOngoing(true)
//                .setDeleteIntent(contentPendingIntent)  // if needed
                .build();
        notification.flags = notification.flags | Notification.FLAG_NO_CLEAR;     // NO_CLEAR makes the notification stay when the user performs a "delete all" command
        startForeground(NOTIFICATION_ID, notification);
    }

    void stopMyService() {
        stopForeground(true);
        stopSelf();
        isServiceRunning = false;
    }
}