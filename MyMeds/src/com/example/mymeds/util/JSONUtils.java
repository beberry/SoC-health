package com.example.mymeds.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.mymeds.libs.PojoMapper;

import android.content.Context;
import android.util.Log;

/*
 * Utility class for JSON reading and writing
 */
public class JSONUtils {
	
	private final static String ALL_FILE_PATH = "meddata.json";
	private final static String TODAYS_FILE_PATH = "todaydata.json";
	
	/**
	 * Method to write a specified ArrayList to a file, set all to true if all meds, otherwise assumes today's med's
	 * @param meds
	 * @param context
	 */
	static public void writeToFile(ArrayList<Medication> meds, Context context, boolean all)
	{
		FileOutputStream fos;
		String fileName = ALL_FILE_PATH;
		if(!all){
			fileName = TODAYS_FILE_PATH;
		}
			
		
		try
		{
			String med = PojoMapper.toJson(meds, true);

			fos = new FileOutputStream(new File(context.getFilesDir()+"//"+fileName), true);

			System.out.println(med);
			String temp = "{ \"medication\": ";
			String temp2 = "}";
			med = temp+med+temp2;
			fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);

			fos.write(med.getBytes());

			fos.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Method to write a specified String to a file.
	 * @param meds
	 * @param context
	 */
	static public void writeStringToFile(String meds, Context context, boolean all)
	{
		FileOutputStream fos;
		String fileName = ALL_FILE_PATH;
		if(!all){
			fileName = TODAYS_FILE_PATH;
		}
		
		try
		{
			
			fos = new FileOutputStream(new File(context.getFilesDir()+"//"+fileName), true);
			fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);

			fos.write(meds.getBytes());

			fos.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Method to read file into a string
	 * @param filename
	 * @param context
	 * @return
	 */
	public static String readFile(Context context, boolean all) {
		StringBuffer json = new StringBuffer("");
		String fileName = ALL_FILE_PATH;
		if(!all){
			fileName = TODAYS_FILE_PATH;
		}
		
		try
		{
			File filesDir = context.getFilesDir();
			Scanner input = new Scanner(new File(filesDir, fileName));
			while(input.hasNext()){
				json.append(input.next());
			}
			Log.i("JSONUtils", "Medication read in from external file");
			input.close();
		}
		catch (FileNotFoundException fnfe)
		{
			Log.w("JSONUtils", "File could not be located, will create");
		}
		
		return json.toString();
	}
	
	/**
	 * Method to load JSON string into a specified object, then contain within ArrayList
	 * @param context
	 * @return
	 */
	public static ArrayList<Medication> loadValues(String JSONstring, Context context){
		
		ArrayList<Medication> allmeds = new ArrayList<Medication>();
		if (JSONstring != "") {
			try {
				JSONObject jsonObject = new JSONObject(JSONstring);
				JSONArray medIndex = jsonObject.getJSONArray("medication");

				for(int k=0;k<medIndex.length();k++){
					Medication med = new Medication();
					ArrayList<Frequency> frequencyList = new ArrayList<Frequency>();

					JSONObject tempCheck = medIndex.getJSONObject(k);
					int itemID = tempCheck.getInt("index");
					String itemName = tempCheck.getString("name");
					String displayName = tempCheck.getString("displayName");
					String description = tempCheck.getString("description");
					String type = tempCheck.getString("type");
					long startTime = tempCheck.getLong("startTime");
					long endTime = tempCheck.getLong("endTime");
					int remaining = tempCheck.getInt("remaining");
					int repeatPeriod = tempCheck.getInt("repeatPeriod");

					JSONArray frequency = tempCheck.getJSONArray("frequency");
					for(int i=0;i<frequency.length();i++){
						JSONObject frequencyObject = frequency.getJSONObject(i);
						String time = frequencyObject.getString("time");
						String dosage = frequencyObject.getString("dosage");
						int units = frequencyObject.getInt("units");
						Frequency frequency2 = new Frequency();
						frequency2.setDosage(dosage);
						frequency2.setUnits(units);
						frequency2.setTime(time);
						frequencyList.add(frequency2);
					}

					if(allmeds.contains((Integer)med.getIndex())==false){
						med.setIndex(itemID);
						med.setName(itemName);
						med.setDisplayName(displayName);
						med.setDescription(description);
						med.setType(type);
						med.setStartTime(startTime);
						med.setEndTime(endTime);
						med.setRemaining(remaining);
						med.setRepeatPeriod(repeatPeriod);
						med.setFrequency(frequencyList);
						allmeds.add(med);
					}
				}
			} catch (JSONException e) {
				Log.e("JSONException","JSON exception");
				e.printStackTrace();
			}
		}
		return allmeds;
	}
}
