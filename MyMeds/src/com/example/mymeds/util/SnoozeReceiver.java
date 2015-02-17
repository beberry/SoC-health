package com.example.mymeds.util;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class SnoozeReceiver extends BroadcastReceiver {
	private Bundle b;
	private String time, dosage, units, name;
	private int id;
	
	/**
	 * When Snooze btn is pressed set alarm for a minute.
	 * @param context
	 * @param intent
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		b = intent.getExtras();
		id = b.getInt("id");
	    time = b.getString("time");
	    dosage = b.getString("dosage");
	    units = b.getString("units");
	    name = b.getString("name");
		
		Intent myIntent = new Intent(context, AlarmReceiver.class);
		myIntent.putExtra("id", id);
		myIntent.putExtra("time", time);
		myIntent.putExtra("dosage", dosage);
		myIntent.putExtra("units", units);
		myIntent.putExtra("name", name); 
		
		Calendar snoozeTime = Calendar.getInstance();
		snoozeTime.add(Calendar.MINUTE, 5);
		long snoozeValue = snoozeTime.getTimeInMillis();
		AlarmUtils.printFormattedDate(snoozeValue, name, "Snooze");
		
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, AlarmUtils.calculateAlarmId(id, time), myIntent, 0);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC, snoozeValue, pendingIntent);
	}
}
