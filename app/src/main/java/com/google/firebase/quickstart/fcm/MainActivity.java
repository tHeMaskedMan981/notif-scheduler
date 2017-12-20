/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.firebase.quickstart.fcm;
import com.google.firebase.quickstart.fcm.MyFirebaseMessagingService;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    int i=0;
    Intent intentnotif;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate()");

        intentnotif = new Intent(this, Autostart.class);
        Date currentTime = Calendar.getInstance().getTime();
        String time = currentTime.toString();
        String array1[] = time.split(" ");
        String time1 = array1[3];
        String timesplit[]= time1.split(":");
        String actualtime = timesplit[0]+timesplit[1];
        Log.d(TAG, "time in string format :" +actualtime);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId  = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.default_notification_channel_name);
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW));
        }

        // If a notification message is tapped, any data accompanying the notification
        // message is available in the intent extras. In this sample the launcher
        // intent is fired when the notification is tapped, so any accompanying data would
        // be handled here. If you want a different intent fired, set the click_action
        // field of the notification message to the desired intent. The launcher intent
        // is used when no click_action is specified.
        //
        // Handle possible data accompanying notification message.
        // [START handle_data_extras]
        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d(TAG, "Key: " + key + " Value: " + value);
            }
        }
        // [END handle_data_extras]

//        Button subscribeButton = findViewById(R.id.subscribeButton);
//        subscribeButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // [START subscribe_topics]
//                FirebaseMessaging.getInstance().subscribeToTopic("news");
//                // [END subscribe_topics]
//
//                // Log and toast
//                String msg = getString(R.string.msg_subscribed);
//                Log.d(TAG, msg);
//                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        Button logTokenButton = findViewById(R.id.logTokenButton);
//        logTokenButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Get token
//                String token = FirebaseInstanceId.getInstance().getToken();
//
//                // Log and toast
//                String msg = getString(R.string.msg_token_fmt, token);
//                Log.d(TAG, msg);
//                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
//            }
//        });

        final Button notif = (Button) findViewById(R.id.notif);
        final Button schedule = (Button) findViewById(R.id.schedule);
        final Button cancel_schedule = (Button) findViewById(R.id.cancel_schedule);
        final EditText message = (EditText) findViewById(R.id.notiftext);
        final EditText notifid = (EditText) findViewById(R.id.notifid);
        final EditText notiftime = (EditText) findViewById(R.id.notiftime);
        final EditText notifday = (EditText) findViewById(R.id.notifday);
        final EditText notifidi = (EditText) findViewById(R.id.notifidi);

        schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyFirebaseMessagingService scheduler = new MyFirebaseMessagingService(MainActivity.this);
                String message1 = message.getText().toString();
                String time = notiftime.getText().toString();
                int day = Integer.parseInt(notifday.getText().toString());
                int notifidint = Integer.parseInt(notifidi.getText().toString());

                long delay = delay(time, day);
                long days = delay/(60000*24*60);
                long hours = (delay%(60000*24*60))/(60000*60);
                long minutes = ((delay%(60000*24*60))%(60000*60))/(60000);

                intentnotif.putExtra("delay", delay);
                intentnotif.putExtra("notifidint", notifidint);
                intentnotif.putExtra("message1", message1);
                scheduler.scheduleNotification(MainActivity.this, delay, notifidint,message1);
                Log.d("MainActivity", "schedule clicked" );
                Log.d("MainActivity", " "+minutes);
                Toast.makeText(getApplicationContext(),"scheduled for "+days+" days, "+hours+" hours,"+minutes+" minutes", Toast.LENGTH_SHORT).show();
                i++;
            }
        });
        cancel_schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyFirebaseMessagingService scheduler = new MyFirebaseMessagingService(MainActivity.this);
                String id =notifid.getText().toString();
                scheduler.cancelNotification(MainActivity.this,Integer.parseInt(id));
                Log.d("MainActivity", "schedule clicked" );
                Toast.makeText(getApplicationContext(),"cancel_schedule clicked for id " + id, Toast.LENGTH_LONG).show();

            }
        });

         findViewById(R.id.cancelAll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                nMgr.cancelAll();
                Log.d("Service Status ", "the state of service running is "+isMyServiceRunning(StarterService.class));
//                Intent startIntent = new Intent(getApplicationContext(), MyService.class);
//                startService(startIntent);
            }
        });
        findViewById(R.id.service).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(MainActivity.this, StarterService.class);
                MainActivity.this.startService(intent1);
            }
        });
        notif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MyFirebaseMessagingService notifi = new MyFirebaseMessagingService(MainActivity.this);
                String message1 = message.getText().toString();
                notifi.sendNotification(MainActivity.this,"notification from system lalala \naaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"+message1);
                Log.d("MainActivity", "notification generated using notification manager" );
                Toast.makeText(getApplicationContext(),"notification generated using notification manager", Toast.LENGTH_LONG).show();
            }
        });

    }


    public long delay(String time, int day)
    {
        Calendar current_time = Calendar.getInstance();
        Calendar future_time = Calendar.getInstance();
        int date, hr, min;
        if (day==1)
            date=22;
        else if (day==2)
            date = 23;
        else if (day==3)
            date=24;
        else if (day==4)
            date=25;
        else
            date=day;
        int timeint = Integer.parseInt(time);
        hr = timeint/100;
        min = timeint%100;

        future_time.set(Calendar.MONTH, Calendar.DECEMBER);
        future_time.set(Calendar.DATE, date);
        future_time.set(Calendar.HOUR_OF_DAY, hr);
        future_time.set(Calendar.MINUTE, min);
        future_time.set(Calendar.SECOND,0);

        long future = future_time.getTimeInMillis();
        long current = current_time.getTimeInMillis();

        return future-current;
    }
    public boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy()");
        Intent intent = new Intent(this,Autostart.class );
        sendBroadcast(intent);

    }

}
