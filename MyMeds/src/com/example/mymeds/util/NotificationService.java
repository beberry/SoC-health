package com.example.mymeds.util;

import java.util.Calendar;

import com.example.mymeds.R;
import com.example.mymeds.activites.MainActivity;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class NotificationService extends Service {
	private NotificationManager notificationManager;

	/**
	 * Returning null, as the service will not be communication with any other
	 * components.
	 */
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	/**
	 * Called by the system when the service is first created.
	 */
	@Override
	public void onCreate() {
		super.onCreate();
	}

	/**
	 * Called every time the service is started by calling startService(Intent).
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null) {
			int id = intent.getIntExtra("id", 0);
			String time = intent.getStringExtra("time");
			String dosage = intent.getStringExtra("dosage");
			String units = intent.getStringExtra("units");
			String name = intent.getStringExtra("name");
			showNotification(id, time, dosage, units, name);
		}
		return START_STICKY;
	}

	/**
	 * Builds & Displays a notification.
	 */
	private void showNotification(int id, String time, String dosage, String units, String name) {
		// Initialise notificationManager
		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		
		Log.v("ID", String.valueOf(getID(id, time)));
		
		Intent snoozeIntent = new Intent(this, SnoozeReceiver.class);
		Bundle bundle = new Bundle();
		bundle.putInt("id", id);
		bundle.putString("time", time);
		bundle.putString("dosage", dosage);
		bundle.putString("units", units);
		bundle.putString("name", name);
		bundle.putInt("alarmID", getID(id, time));
		Log.v("alarmID ns", String.valueOf(getID(id, time)));
		snoozeIntent.putExtras(bundle);
		
		PendingIntent snoozePendingIntent = PendingIntent.getBroadcast(this, getID(id, time), snoozeIntent, 0);
		
		NotificationCompat.Builder builder =
		        new NotificationCompat.Builder(this)
		        .setSmallIcon(R.drawable.ic_launcher)
		        .setContentTitle("Time for Pill")
		        .setContentText(name + ": " + dosage + "  x" + units)
		        .setDefaults(Notification.DEFAULT_ALL)
		        .setStyle(new NotificationCompat.BigTextStyle().bigText(name + ": " + dosage + "  x" + units))
		        .addAction (R.drawable.ic_launcher, "Snooze", snoozePendingIntent);

		// Send the notification.
		notificationManager.notify(getID(id, time), builder.build());
		
		Alarms alarm = new Alarms(this);
		alarm.setNextAlarm(id, getID(id, time), time);	
	}

	/**
	 * Get the id based on the id & time of a medication
	 */
	public int getID(int id, String time) {
		StringBuilder idValue = new StringBuilder();
		idValue.append(id);
		idValue.append(time);
		return Integer.valueOf(idValue.toString());
	}

	/**
	 * Called by the system to notify a Service that it is no longer used and is
	 * being removed.
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
}