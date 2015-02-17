package com.example.mymeds.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.util.Log;

public class AlarmUtils {
	/**
	 * Get the current time
	 * @param time
	 * @return calendar instance
	 */
	static public Calendar getCalendarTime(long time) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(time);
		return cal;
	}

	/**
	 * Used for testing purposes to print out the date/time in a readable format.
	 * @param time
	 */
	static public void printFormattedDate(long time, String name, String label) {
		Date date = new Date(time);
		SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		String dateText = df2.format(date);
		Log.v("Name", name);
		Log.v(label, dateText);
	}

	/**
	 * Calculates AlarmId based on a medID and an alarmTime.
	 * @param medID
	 * @param alarmTime
	 * @return
	 */
	static public int calculateAlarmId(int id, String time) {
		StringBuilder idValue = new StringBuilder();
		idValue.append(id);
		idValue.append(time);
		return Integer.valueOf(idValue.toString());
	}
}
