package com.example.mymeds.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class Alarms {
	private Context context;
	
	/**
	 * Passing context to use with AlarmManager
	 * @param context
	 */
	public Alarms(Context context) {
		this.context = context;
	}

	/**
	 * Get one Medication based on a ID.
	 * 
	 * @param actualIndex
	 * @return
	 */
	public Medication getMedicationById(int actualIndex) {
		ArrayList<Medication> meds = JSONUtils.loadValues(JSONUtils.readFile(context, false), context);
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
	 * This sets alarms for all Medication's.
	 */
	public void setAllAlarms() {
		ArrayList<Medication> medicationList = JSONUtils.loadValues(JSONUtils.readFile(context, false), context);
		ArrayList<Frequency> frequencyList;
		Medication med;
		Frequency freq;
		Calendar cal;
		String name, dosage, time;
		int id, units;
		
		for (int i = 0; i < medicationList.size(); i++) {
			med = medicationList.get(i);
			name = med.getName();
			id = med.getIndex();
			cal = AlarmUtils.getCalendarTime(med.getStartTime());
			
			frequencyList = med.getFrequency();
			for (int t = 0; t < frequencyList.size(); t++) {
				freq = frequencyList.get(t); 
				dosage = freq.getDosage();
				units = freq.getUnits();
				time = freq.getTime();

				cal = calculateTimeOfAlarm(time, cal);
				long alarmTime = cal.getTimeInMillis();
				AlarmUtils.printFormattedDate(alarmTime, name, "Set All Alarms");

				Intent myIntent = new Intent(context, AlarmReceiver.class);
				myIntent.putExtra("id", id);
				myIntent.putExtra("time", time);
				myIntent.putExtra("dosage", dosage);
				myIntent.putExtra("units", String.valueOf(units));
				myIntent.putExtra("name", name);

				PendingIntent pendingIntent = PendingIntent.getBroadcast(context, AlarmUtils.calculateAlarmId(id, time), myIntent, 0);
				AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
				alarmManager.set(AlarmManager.RTC, alarmTime, pendingIntent);
			}
		}
	}

	/**
	 * This adds an alarm for an Medication based on the MedID.
	 * Method is used when we add a Medication.
	 * @param id
	 */
	public void addAlarm(Medication med) {
		//Medication med = getMedicationById(id);
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

		Calendar cal = AlarmUtils.getCalendarTime(med.getStartTime());
		cal = calculateTimeOfAlarm(freqTime, cal);
		long time = cal.getTimeInMillis();
		AlarmUtils.printFormattedDate(time, med.getName(), "Added Alarm");

		Intent myIntent = new Intent(context, AlarmReceiver.class);
		myIntent.putExtra("id", med.getIndex());
		myIntent.putExtra("time", freqTime);
		myIntent.putExtra("dosage", dosage);
		myIntent.putExtra("units", String.valueOf(units));
		myIntent.putExtra("name", med.getName());

		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, AlarmUtils.calculateAlarmId(med.getIndex(), freqTime), myIntent, 0);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC, time, pendingIntent);
	}

	/**
	 * Sets the next alarm in the sequence using the current date and repeatPeriod to calculate this.
	 * Method is used in NotificationService.
	 * @param medID
	 * @param alarmID
	 * @param time
	 */
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
				AlarmUtils.printFormattedDate(alarmTime, med.getName(), "Next Alarm");

				Intent myIntent = new Intent(context, AlarmReceiver.class);
				myIntent.putExtra("id", medID);
				myIntent.putExtra("time", freqTime);
				myIntent.putExtra("dosage", dosage);
				myIntent.putExtra("units", String.valueOf(units));
				myIntent.putExtra("name", med.getName());

				PendingIntent pendingIntent = PendingIntent.getBroadcast(context, AlarmUtils.calculateAlarmId(medID, freqTime), myIntent, 0);
				AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
				alarmManager.set(AlarmManager.RTC, alarmTime, pendingIntent);
			}
		}
	}

	/**
	 * Calculate the time of the alarm using the startTime(cal) and alarmTime(str)
	 * @param alarmTime
	 * @param cal
	 * @return calendar
	 */
	public Calendar calculateTimeOfAlarm(String alarmTime, Calendar cal) {
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
		return cal;
	}
}