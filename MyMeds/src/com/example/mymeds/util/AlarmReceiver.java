package com.example.mymeds.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {

    /**
     * Starts the NotificationService once it receives a Alarm that has gone off.
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, NotificationService.class);
        serviceIntent.putExtra("id", intent.getIntExtra("id", 0));
        serviceIntent.putExtra("time", intent.getStringExtra("time"));
        serviceIntent.putExtra("dosage", intent.getStringExtra("dosage"));
        serviceIntent.putExtra("name", intent.getStringExtra("name"));
        serviceIntent.putExtra("units", intent.getStringExtra("units"));
        context.startService(serviceIntent);
    }
}

