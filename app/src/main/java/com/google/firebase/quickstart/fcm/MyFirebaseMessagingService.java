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

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.GregorianCalendar;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private Context context=this;
    public MyFirebaseMessagingService(Context context){

        this.context = context;
    }
    public MyFirebaseMessagingService(){
    }
    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
                scheduleJob();
            } else {
                // Handle message within 10 seconds
                handleNow();
            }
        }
        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody()+"recieved in onmessagerecieved");
        }
        sendNotification(this,remoteMessage.getNotification().getBody());
        Log.d(TAG, "sent notif using notification manager");
        Log.i("notif","sent notif using notification manager" );
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]

    /**
     * Schedule a job using FirebaseJobDispatcher.
     */
    private void scheduleJob() {
        // [START dispatch_job]
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
        Job myJob = dispatcher.newJobBuilder()
                .setService(MyJobService.class)
                .setTag("my-job-tag")
                .build();
        dispatcher.schedule(myJob);
        // [END dispatch_job]
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }

    public void cancelNotification(Context context, int id){

//        ComponentName receiver = new ComponentName(context,  MyNotificationPublisher.class);
//        PackageManager pm = context.getPackageManager();
//        pm.setComponentEnabledSetting(receiver,
//                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
//                PackageManager.DONT_KILL_APP);

        Intent notificationIntent = new Intent(context,MyNotificationPublisher.class );
        notificationIntent.putExtra(MyNotificationPublisher.NOTIFICATION_ID, id);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                id, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        am.cancel(pendingIntent);
        pendingIntent.cancel();
//        NotificationManager notificationManager =
//                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//
//        notificationManager.cancel(id /* ID of notification */);
        Toast.makeText(context.getApplicationContext(),"notification cancelled for notif id : "+id, Toast.LENGTH_LONG).show();

    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void scheduleNotification(Context context, long delay, int notificationId, String message) {//delay is after how much time(in millis) from current time you want to schedule the notification

//        ComponentName receiver = new ComponentName(context,  MyNotificationPublisher.class);
//        PackageManager pm = context.getPackageManager();
//        pm.setComponentEnabledSetting(receiver,
//                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
//                PackageManager.DONT_KILL_APP);

        Intent intent = new Intent(context, Notif.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,
//                PendingIntent.FLAG_ONE_SHOT);


        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack( Notif.class);
        stackBuilder.addNextIntent(intent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(
               0,PendingIntent.FLAG_UPDATE_CURRENT);

        String channelId = "channel_id_01";
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, channelId)
                        .setSmallIcon(R.drawable.ic_stat_ic_notification)
                        .setContentTitle("FCM Message")
                        .setContentText(message)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        Notification notification = notificationBuilder.build();

        Intent notificationIntent = new Intent(context, MyNotificationPublisher.class);
        notificationIntent.putExtra(MyNotificationPublisher.NOTIFICATION_ID, notificationId);
        notificationIntent.putExtra(MyNotificationPublisher.NOTIFICATION, notification);

        Intent notificationIntent2 = new Intent(context, MyNotificationPublisher.class);
        notificationIntent2.putExtra(MyNotificationPublisher.NOTIFICATION_ID,5);

        boolean intentequal =notificationIntent.filterEquals(notificationIntent2);
        Log.d("MainActivity", " "+intentequal+"" );
        PendingIntent pendingIntent2 = PendingIntent.getBroadcast(context, notificationId, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent2);
        Log.d("MainActivity", "schedule function called " +intentequal);
        Toast.makeText(context.getApplicationContext(),"schedule function called for notif id : "+notificationId +intentequal, Toast.LENGTH_SHORT).show();

    }
    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */


    public void sendNotification(Context context, String messageBody) {
        Log.d(TAG, "toast done.");
      //  Toast toast = Toast.makeText(this, "1st line executed", Toast.LENGTH_LONG);
      //  toast.show();

        Intent intent = new Intent(context, Notif.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = "channel_id_01";
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_stat_ic_notification)
                .setContentTitle("FCM MESSAGE")
                .setContentText(messageBody)
//        NotificationCompat.InboxStyle inboxStyle =
//                new NotificationCompat.InboxStyle();
//
//// Sets a title for the Inbox in expanded layout
//        inboxStyle.setBigContentTitle("Mood Indigo");
//        inboxStyle.addLine("Compi event name");
//        inboxStyle.addLine("Venue : Convocation Hall");
//        inboxStyle.addLine("17:00 hrs");
//        inboxStyle.addLine("This notif is brought to u by BOX 8");
//
//        notificationBuilder.setStyle(inboxStyle)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
