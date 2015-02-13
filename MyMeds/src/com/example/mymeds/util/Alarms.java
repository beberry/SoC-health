package com.example.mymeds.util;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
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
	 * Go through the mymeds json file and set an alarm for each piece of
	 * medication to be taken during the day
	 */
	@SuppressLint("NewApi")
	public void setAlarms() {
		String jsonTime = null;
		String dosage = null;
		String units = null;
		String name = null;
		int id;
		String alarmTime;

		try {
			// read file from assets
			AssetManager assetManager = context.getAssets();
			InputStream is = assetManager.open("meds.json");
			int size = is.available();
			byte[] buffer = new byte[size];
			is.read(buffer);
			is.close();
			String bufferString = new String(buffer);

			JSONObject jsonObject = new JSONObject(bufferString);
			JSONArray medIndex = jsonObject.getJSONArray("medication");
			JSONObject record;

			for (int k = 0; k < medIndex.length(); k++) {
				record = medIndex.getJSONObject(k);
				name = record.getString("displayName");
				id = record.getInt("index");
				Calendar cal = getSpecifiedTime(record.getLong("startTime"));
						
				JSONArray freqIndex = record.getJSONArray("frequency");
				JSONObject freq;
				for (int t = 0; t < freqIndex.length(); t++) {
					freq = freqIndex.getJSONObject(t);
					dosage = freq.getString("dosage");
					units = freq.getString("units");
					alarmTime = freq.getString("time");
					Log.v("Name", name);
					
					//Calculate idValue for individual alarms
					StringBuilder idValue = new StringBuilder();
					idValue.append(id);
					idValue.append(alarmTime);
					Log.v("ID", idValue.toString());

					LinkedList<String> stack = getTimeOfDose(alarmTime);
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
					long time = cal.getTimeInMillis();

					Date date = new Date(time);
					SimpleDateFormat df2 = new SimpleDateFormat(
							"dd/MM/yyyy HH:mm:ss");
					String dateText = df2.format(date);
					Log.v("Alarm Time", dateText);	

					Intent myIntent = new Intent(context, AlarmReceiver.class);
					myIntent.putExtra("id", id);
					myIntent.putExtra("time", alarmTime);
					myIntent.putExtra("dosage", dosage);
					myIntent.putExtra("units", units);
					myIntent.putExtra("name", name); 

					PendingIntent pendingIntent = PendingIntent.getBroadcast(context, Integer.valueOf(idValue.toString()),myIntent, 0);
					AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
					alarmManager.set(AlarmManager.RTC, time, pendingIntent);
					Log.v("","");
				}
			}
		} catch (IOException e) {
			Log.e("IOException", "Error loading file");
			e.printStackTrace();
		} catch (JSONException e) {
			Log.e("JSONException", "JSON exception");
			e.printStackTrace();
		}
	}
	
	public LinkedList<String> getTimeOfDose(String alarmTime) {
		LinkedList<String> stack = new LinkedList<String>();
		String value;
		for(int i = 0; i < alarmTime.length(); i++) {
			value = String.valueOf(alarmTime.charAt(i));
			stack.push(value);
		}
		return stack;
	}
	
	public Calendar getSpecifiedTime(long time) {
		time = time*1000;
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(time);
		return cal;
	}
}
