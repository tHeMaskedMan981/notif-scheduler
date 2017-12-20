package com.google.firebase.quickstart.fcm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by akash on 20-Dec-17.
 */

public class ServiceRestarter extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Autostart", "on TaskRemoved broadcast recieved. Executing starter service");

        Intent intent1 = new Intent(context, StarterService.class);
        context.startService(intent1);
    }
}