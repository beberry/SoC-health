package com.example.mymeds.util;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class SnoozeReceiver extends BroadcastReceiver {

	/**
	 * When Snooze btn is pressed set alarm for a minute.
	 * @param context
	 * @param intent
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle b = intent.getExtras();
		int id = b.getInt("id");
	    String time = b.getString("time");
	    String dosage = b.getString("dosage");
	    String units = b.getString("units");
	    String name = b.getString("name");
	    int alarmID = b.getInt("alarmID");
		
		Intent myIntent = new Intent(context, AlarmReceiver.class);
		myIntent.putExtra("id", id);
		myIntent.putExtra("time", time);
		myIntent.putExtra("dosage", dosage);
		myIntent.putExtra("units", units);
		myIntent.putExtra("name", name); 
		
		Calendar snoozeTime = Calendar.getInstance();
		snoozeTime.add(Calendar.MINUTE, 5);
		long snoozeValue = snoozeTime.getTimeInMillis();
		
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarmID, myIntent, 0);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC, snoozeValue, pendingIntent);
	}
}
