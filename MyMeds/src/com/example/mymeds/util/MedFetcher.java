package com.example.mymeds.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

public class MedFetcher {

	ArrayList<Medication> allmeds = new ArrayList<Medication>();
	Context mContext;
	
	public void loadAssets(Context c)
	{
		mContext = c;
	};
	
	
	public Medication[] daysMedication(long day)
	{
		ArrayList<Medication> daysMeds = new ArrayList<Medication>();
		
		loadValues();
		
		for (int i=0;i<allmeds.size();i++){
			
			long st = allmeds.get(i).startTime;
			long et = allmeds.get(i).endTime;
			
			long stDiff = day - st;
			
			
			if(stDiff >= 0 && !(day >et)){
				if(stDiff % allmeds.get(i).repeatPeriod == 0)
				{
					daysMeds.add(allmeds.get(i));
				}				
			}				
		}
		
		
		return  (Medication[]) daysMeds.toArray();
	};
	
	
	public boolean loadValues(){
		ArrayList<Frequency> frequencyList = new ArrayList<Frequency>();

		try {
			// read file from assets
			AssetManager assetManager = mContext.getAssets();
			InputStream is = assetManager.open("allmeds.json");
			int size = is.available();
			byte[] buffer = new byte[size];
			is.read(buffer);
			is.close();
			String bufferString = new String(buffer);	

			JSONObject jsonObject = new JSONObject(bufferString);
			JSONArray medIndex = jsonObject.getJSONArray("medication");

			for(int k=0;k<medIndex.length();k++){
				Medication med = new Medication();

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
					int time = frequencyObject.getInt("time");
					String dosage = frequencyObject.getString("dosage");
					int units = frequencyObject.getInt("units");
					Frequency frequency2 = new Frequency();
					frequency2.setDosage(dosage);
					frequency2.setUnits(units);
					frequency2.setTime(time);
					frequencyList.add(frequency2);
				}

				if(allmeds.contains((Integer)med.getMedId())==false){
					med.setMedId(itemID);
					med.setMedName(itemName);
					med.setDisplayName(displayName);
					med.setDescription(description);
					med.setType(type);
					med.setStartTime(startTime);
					med.setEndTime(endTime);
					med.setRemaining(remaining);
					med.setRepeatPeriod(repeatPeriod);
					med.setFrequency(frequencyList);
					allmeds.add(med);
				}else{
					med = null;
				}			}
		} catch (IOException e) {
			Log.e("IOException","Error loading file");
			e.printStackTrace();
			return false;
		} catch (JSONException e) {
			Log.e("JSONException","JSON exception");
			e.printStackTrace();			
			return false;
		}
		return true;
	}

}
