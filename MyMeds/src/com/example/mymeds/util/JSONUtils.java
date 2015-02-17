package com.example.mymeds.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.mymeds.libs.PojoMapper;

import android.content.Context;
import android.util.Log;

public class JSONUtils {
	
	private final static String FILE_PATH = "meddata.json";
	
	@SuppressWarnings("resource")
	static public void writeToFile(ArrayList<Medication> meds, Context context)
	{
		String med = "";
		try {
			med = PojoMapper.toJson(meds, true);
		} catch (JsonMappingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (JsonGenerationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		StringBuffer json = new StringBuffer("");
		try {
			File filesDir = context.getFilesDir();
			Scanner input = new Scanner(new File(filesDir, FILE_PATH));
			
			while(input.hasNext()){
				json.append(input.next());
			}

			FileOutputStream fos = new FileOutputStream(new File(context.getFilesDir()+"//"+FILE_PATH), true);

			System.out.println(med);
			String temp = "{ \"medication\": ";
			String temp2 = "}";
			med = temp+med+temp2;
			fos = context.openFileOutput(FILE_PATH, Context.MODE_PRIVATE);

			fos.write(med.getBytes());

			fos.close();
			input.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("resource")
	static public void writeStringToFile(String meds, Context context)
	{
		try {
			File filesDir = context.getFilesDir();
			Scanner input = new Scanner(new File(filesDir, FILE_PATH));
			
			FileOutputStream fos = new FileOutputStream(new File(context.getFilesDir()+"//"+FILE_PATH), true);
			fos = context.openFileOutput(FILE_PATH, Context.MODE_PRIVATE);

			fos.write(meds.getBytes());

			fos.close();
			input.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static String readFile(String filename, Context context) {
		Log.i("json values", "reading file");

		StringBuffer json = new StringBuffer("");

		try {
			File filesDir = context.getFilesDir();
			Scanner input = new Scanner(new File(filesDir, filename));
			while(input.hasNext()){
				json.append(input.next());
			}
			Log.i("Completed", "Medication read in from external file");
			input.close();
		}
		catch (FileNotFoundException fnfe)
		{
			Log.w("FileNotFound", "File could not be located, will create");
		}
		catch (@SuppressWarnings("hiding") IOException ioe) {
			Log.e("JSONRead", "An IO Exception occured when reading file");
		}

		return json.toString();
	}
	
	public static ArrayList<Medication> loadValues(Context context){
		String JSONstring = readFile(FILE_PATH, context);
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
