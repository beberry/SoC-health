package com.example.mymeds.util;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

@SuppressLint("SimpleDateFormat")
public class Alarms {
	private Context context;
	

	public Alarms(Context context) {
		this.context = context;
	}

	/**
	 * This will get a Medication based on a ID.
	 * 
	 * @param actualIndex
	 * @return
	 */
	public Medication getMedicationById(int actualIndex) {
		ArrayList<Medication> meds = JSONUtils.loadValues(context);
		int index;
		for (int i = 0; i < meds.size(); i++) {
			index = meds.get(i).getIndex();
			if(index == actualIndex) {
				return meds.get(i);
			}
		}
		return null;
	}

	/**
	 * This sets alarms for all the Medication's in the JSON file.
	 */
//	public void setAllAlarms() {
//		String dosage = null, units = null, name = null, time = null;
//		int id;
//
//		try {
//			JSONObject jsonObject = new JSONObject(loadJSON());
//			JSONArray medIndex = jsonObject.getJSONArray("medication");
//			JSONObject medObject;
//
//			for (int k = 0; k < medIndex.length(); k++) {
//				medObject = medIndex.getJSONObject(k);
//				name = medObject.getString("displayName");
//				id = medObject.getInt("index");
//				Calendar cal = getSpecifiedTime(medObject.getLong("startTime"));
//
//				JSONArray freqIndex = medObject.getJSONArray("frequency");
//				JSONObject freqObject;
//				for (int t = 0; t < freqIndex.length(); t++) {
//					freqObject = freqIndex.getJSONObject(t);
//					dosage = freqObject.getString("dosage");
//					units = freqObject.getString("units");
//					time = freqObject.getString("time");
//
//					cal = calculateTimeOfAlarm(time, cal);
//					long alarmTime = cal.getTimeInMillis();
//					printFormattedDate(alarmTime, name, "Set All Alarms");
//					Log.w("ALARM", "A: " + alarmTime + " - N: " + name);
//
//					Intent myIntent = new Intent(context, AlarmReceiver.class);
//					myIntent.putExtra("id", id);
//					myIntent.putExtra("time", time);
//					myIntent.putExtra("dosage", dosage);
//					myIntent.putExtra("units", units);
//					myIntent.putExtra("name", name);
//
//					PendingIntent pendingIntent = PendingIntent.getBroadcast(context, calcaluteAlarmId(id, time), myIntent, 0);
//					AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//					alarmManager.set(AlarmManager.RTC, alarmTime, pendingIntent);
//					Log.v("", "");
//				}
//			}
//		} catch (JSONException e) {
//			Log.e("JSONException", "JSON exception");
//			e.printStackTrace();
//		}
//	}

	/**
	 * This adds an alarm for an Medication based on the MedID.
	 * 
	 * @param id
	 */
	public void addAlarm(int id) {
		Medication med = getMedicationById(id);
		ArrayList<Frequency> frequencyList = med.getFrequency();
		Frequency freqObject;
		String dosage = null, freqTime = null;
		int units = 0;
		for (int i = 0; i < frequencyList.size(); i++) {
			freqObject = frequencyList.get(i);
			freqTime = String.valueOf(freqObject.getTime());
			units = freqObject.getUnits();
			dosage = freqObject.getDosage();
		}

		Calendar cal = getSpecifiedTime(med.getStartTime());
		cal = calculateTimeOfAlarm(freqTime, cal);
		long time = cal.getTimeInMillis();
		printFormattedDate(time, med.getName(), "Added Alarm");

		Intent myIntent = new Intent(context, AlarmReceiver.class);
		myIntent.putExtra("id", id);
		myIntent.putExtra("time", freqTime);
		myIntent.putExtra("dosage", dosage);
		myIntent.putExtra("units", String.valueOf(units));
		myIntent.putExtra("name", med.getName());

		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, calcaluteAlarmId(id, freqTime), myIntent, 0);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC, time, pendingIntent);
	}

	public void setNextAlarm(int medID, int alarmID, String time) {
		Medication med = getMedicationById(medID);
		int repeatPeriod = med.getRepeatPeriod();
		ArrayList<Frequency> freqList = med.getFrequency();
		Frequency freqIndex;
		String dosage = null, freqTime = null;
		Calendar cal = Calendar.getInstance();
		int units = 0;
		for (int i = 0; i < freqList.size(); i++) {
			freqIndex = freqList.get(i);
			if (time.equals(freqIndex.getTime())) {
				freqTime = freqIndex.getTime();
				units = freqIndex.getUnits();
				dosage = freqIndex.getDosage();

				cal = calculateTimeOfAlarm(String.valueOf(freqTime), cal);
				cal.add(Calendar.DAY_OF_MONTH, repeatPeriod);
				long alarmTime = cal.getTimeInMillis();
				printFormattedDate(alarmTime, med.getName(), "Next Alarm");

				Intent myIntent = new Intent(context, AlarmReceiver.class);
				myIntent.putExtra("id", medID);
				myIntent.putExtra("time", freqTime);
				myIntent.putExtra("dosage", dosage);
				myIntent.putExtra("units", String.valueOf(units));
				myIntent.putExtra("name", med.getName());

				PendingIntent pendingIntent = PendingIntent.getBroadcast(context, calcaluteAlarmId(medID, freqTime), myIntent, 0);
				AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
				alarmManager.set(AlarmManager.RTC, alarmTime, pendingIntent);
			}
		}
	}

	public Calendar calculateTimeOfAlarm(String alarmTime, Calendar cal) {
		LinkedList<String> stack = new LinkedList<String>();
		String value;
		for (int i = 0; i < alarmTime.length(); i++) {
			value = String.valueOf(alarmTime.charAt(i));
			stack.push(value);
		}
		Log.v("SHIT", alarmTime);

		int sMin = Integer.valueOf(stack.pop());
		int fMin = Integer.valueOf(stack.pop());
		int sHour = Integer.valueOf(stack.pop());
		int fHour = Integer.valueOf(stack.pop());

		StringBuilder sb = new StringBuilder();
		sb.append(fMin);
		sb.append(sMin);
		int minute = Integer.parseInt(sb.toString());

		StringBuilder sb2 = new StringBuilder();
		sb2.append(fHour);
		sb2.append(sHour);
		int hour = Integer.parseInt(sb2.toString());

		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, minute);
		cal.set(Calendar.SECOND, 0);
		return cal;
	}

	public Calendar getSpecifiedTime(long time) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(time);
		return cal;
	}

	/**
	 * Used for testing purposes to print out the date/time in a readable format.
	 * @param time
	 */
	private void printFormattedDate(long time, String name, String label) {
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
	private int calcaluteAlarmId(int medID, String alarmTime) {
		StringBuilder idValue = new StringBuilder();
		idValue.append(medID);
		idValue.append(alarmTime);
		return Integer.valueOf(idValue.toString());
	}
}