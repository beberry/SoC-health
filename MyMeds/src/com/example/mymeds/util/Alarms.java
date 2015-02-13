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
import android.content.res.AssetManager;
import android.util.Log;

public class Alarms {
	private Context context;

	public Alarms(Context context) {
		this.context = context;
	}

	/**
	 * Load JSON from Assets and return jsonString.
	 * @return json
	 */
	public String loadJSON() {
		String json = null;
		try {
			InputStream is = context.getAssets().open("meds.json");
			int size = is.available();
			byte[] buffer = new byte[size];
			is.read(buffer);
			is.close();
			json = new String(buffer, "UTF-8");
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
		return json;
	}

	/**
	 * This will get a Medication based on a ID.
	 * 
	 * @param actualIndex
	 * @return
	 */
	public Medication getMedicationById(int actualIndex) {
		Medication med = new Medication();
		try {
			JSONObject jsonObject = new JSONObject(loadJSON());
			JSONArray medIndex = jsonObject.getJSONArray("medication");
			JSONObject medObject;

			for (int k = 0; k < medIndex.length(); k++) {
				medObject = medIndex.getJSONObject(k);
				int jsonIndex = medObject.getInt("index");

				if (jsonIndex == actualIndex) {
					med.setMedId(actualIndex);
					med.setMedName(medObject.getString("name"));
					med.setDisplayName(medObject.getString("displayName"));
					med.setDescription(medObject.getString("description"));
					med.setType(medObject.getString("type"));
					med.setStartTime(medObject.getLong("startTime"));
					med.setEndTime(medObject.getLong("endTime"));
					med.setRemaining(medObject.getInt("remaining"));
					med.setRepeatPeriod(medObject.getInt("repeatPeriod"));

					JSONArray freqIndex = medObject.getJSONArray("frequency");
					ArrayList<Frequency> frequencyList = new ArrayList<Frequency>();
					for (int i = 0; i < freqIndex.length(); i++) {
						JSONObject frequencyObject = freqIndex.getJSONObject(i);
						Frequency freq = new Frequency();
						freq.setDosage(frequencyObject.getString("dosage"));
						freq.setUnits(frequencyObject.getInt("units"));
						freq.setTime(frequencyObject.getInt("time"));
						frequencyList.add(freq);
					}
					med.setFrequency(frequencyList);
				}
			}
		} catch (JSONException e) {
			Log.e("JSONException", "JSON exception");
			e.printStackTrace();
			return null;
		}
		return med;
	}

	/**
	 * This sets alarms for all the Medication's in the JSON file.
	 */
	public void setAllAlarms() {
		String dosage = null, units = null, name = null, time = null;
		int id;

		try {
			JSONObject jsonObject = new JSONObject(loadJSON());
			JSONArray medIndex = jsonObject.getJSONArray("medication");
			JSONObject medObject;

			for (int k = 0; k < medIndex.length(); k++) {
				medObject = medIndex.getJSONObject(k);
				name = medObject.getString("displayName");
				id = medObject.getInt("index");
				Calendar cal = getSpecifiedTime(medObject.getLong("startTime"));

				JSONArray freqIndex = medObject.getJSONArray("frequency");
				JSONObject freqObject;
				for (int t = 0; t < freqIndex.length(); t++) {
					freqObject = freqIndex.getJSONObject(t);
					dosage = freqObject.getString("dosage");
					units = freqObject.getString("units");
					time = freqObject.getString("time");
					
					long alarmTime = calculateTimeOfAlarm(time, cal);
					printFormattedDate(alarmTime, name);

					Intent myIntent = new Intent(context, AlarmReceiver.class);
					myIntent.putExtra("id", id);
					myIntent.putExtra("time", time);
					myIntent.putExtra("dosage", dosage);
					myIntent.putExtra("units", units);
					myIntent.putExtra("name", name);

					PendingIntent pendingIntent = PendingIntent.getBroadcast(context, calcaluteAlarmId(id, time), myIntent, 0);
					AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
					alarmManager.set(AlarmManager.RTC, alarmTime, pendingIntent);
					Log.v("", "");
				}
			}
		} catch (JSONException e) {
			Log.e("JSONException", "JSON exception");
			e.printStackTrace();
		}
	}

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
		long time = calculateTimeOfAlarm(freqTime, cal);
		printFormattedDate(time, med.getMedName());

		Intent myIntent = new Intent(context, AlarmReceiver.class);
		myIntent.putExtra("id", id);
		myIntent.putExtra("time", freqTime);
		myIntent.putExtra("dosage", dosage);
		myIntent.putExtra("units", String.valueOf(units));
		myIntent.putExtra("name", med.getMedName());

		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, calcaluteAlarmId(id, freqTime), myIntent, 0);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC, time, pendingIntent);
	}

	public long calculateTimeOfAlarm(String alarmTime, Calendar cal) {
		LinkedList<String> stack = new LinkedList<String>();
		String value;
		for (int i = 0; i < alarmTime.length(); i++) {
			value = String.valueOf(alarmTime.charAt(i));
			stack.push(value);
		}
		
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
		return cal.getTimeInMillis();
	}

	public Calendar getSpecifiedTime(long time) {
		time = time * 1000;
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(time);
		return cal;
	}

	/**
	 * Used for testing purposes to print out the date/time in a readable format.
	 * @param time
	 */
	private void printFormattedDate(long time, String name) {
		Date date = new Date(time);
		SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		String dateText = df2.format(date);
		Log.v("Name", name);
		Log.v("Alarm Time", dateText);
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