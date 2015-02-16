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

import com.example.mymeds.activites.MainActivity;

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
		int alarmTime;

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
				// work to get into frequency to get time
				name = record.getString("displayName");
				id = record.getInt("index");
				JSONArray freqIndex = record.getJSONArray("frequency");
				JSONObject freq;
				for (int t = 0; t < freqIndex.length(); t++) {
					freq = freqIndex.getJSONObject(t);
					dosage = freq.getString("dosage");
					units = freq.getString("units");
					alarmTime = freq.getInt("time");
					Log.v("Dosage", dosage);
					Log.v("Units", units);
					Log.v("Name", name);
					
					//Calculate idValue for individual alarms
					StringBuilder idValue = new StringBuilder();
					idValue.append(id);
					idValue.append(alarmTime);
					Log.v("ID", idValue.toString());

					Calendar c = Calendar.getInstance();
					int year = c.get(Calendar.YEAR);
					int month = c.get(Calendar.MONTH);
					int day = c.get(Calendar.DAY_OF_MONTH);

					LinkedList<Integer> stack = new LinkedList<Integer>();

					while (alarmTime > 0) {
						stack.push(alarmTime % 10);
						alarmTime = alarmTime / 10;
					}

					int fMin = (stack.pop());
					int sMin = (stack.pop());
					int fHour = (stack.pop());
					int sHour = (stack.pop());

					StringBuilder sb = new StringBuilder();

					sb.append(fMin);
					sb.append(sMin);

					int hour = Integer.parseInt(sb.toString());

					StringBuilder sb2 = new StringBuilder();

					sb2.append(fHour);
					sb2.append(sHour);

					int minute = Integer.parseInt(sb2.toString());

					StringBuilder displayTime = new StringBuilder();
					displayTime.append(sb.toString());
					displayTime.append(sb2.toString());

					c.set(year, month, day, hour, minute, 00);
					long time = c.getTimeInMillis();

					Date date = new Date(time);
					SimpleDateFormat df2 = new SimpleDateFormat(
							"dd/MM/yy HH:mm:ss");
					String dateText = df2.format(date);

					Log.v("time fucker", dateText);

					Intent myIntent = new Intent(context, AlarmReceiver.class);
					myIntent.putExtra("id", id);
					myIntent.putExtra("time", displayTime.toString());
					myIntent.putExtra("dosage", dosage);
					myIntent.putExtra("units", units);
					myIntent.putExtra("name", name); 

					PendingIntent pendingIntent = PendingIntent.getBroadcast(
							context, Integer.valueOf(idValue.toString()),
							myIntent, 0);
					AlarmManager alarmManager = (AlarmManager) context
							.getSystemService(Context.ALARM_SERVICE);
					alarmManager
							.setExact(AlarmManager.RTC, time, pendingIntent);
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
}
