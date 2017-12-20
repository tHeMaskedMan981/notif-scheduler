package com.google.firebase.quickstart.fcm;

/**
 * Created by akash on 20-Dec-17.
 */
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class StarterService extends Service {
    private static final String TAG = "MyService";
    private int time=1;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");

        Intent notificationIntent = new Intent(this, Notif.class);
        notificationIntent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1,
                notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this,"channel01")
                .setSmallIcon(R.drawable.ic_stat_ic_notification)
                .setContentTitle("My Awesome App")
                .setContentText("Doing some work...")
                .setContentIntent(pendingIntent).build();

        startForeground(1337, notification);
        Log.d(TAG, "startForeground()");
    }

    /**
     * The started service starts the AlarmManager.
     */

    @Override
    public int onStartCommand(Intent intent,int flags, int startid) {

        Log.d("in StartService", "in onStartCommand threadId:"+Thread.currentThread().getId());
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("in StartService", "in onStartCommand newThreadId:"+Thread.currentThread().getId());
                Intent i = new Intent(StarterService.this, NotificationBarAlarm.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

// Repeat the notification every 15 seconds (15000)
                AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                while (time<7) {
                    PendingIntent pi = PendingIntent.getBroadcast(StarterService.this, time, i, PendingIntent.FLAG_UPDATE_CURRENT);
                    am.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+ 2000*time, pi);
                    Log.d(TAG, "on alarm "+time);
                    time++;
                }

                Log.d(TAG, "onStartCommand()");
            }
        }).start();
        Toast.makeText(StarterService.this, "My Service started", Toast.LENGTH_LONG).show();

        return START_REDELIVER_INTENT;

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent intent = new Intent(this,Autostart.class );
        sendBroadcast(intent);
        super.onTaskRemoved(rootIntent);
        Log.d(TAG, "onTaskRemoved");

    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "My Service stopped", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onDestroy");
    }
}