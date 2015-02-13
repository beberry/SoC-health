package com.example.mymeds.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.mymeds.tabs.FutureMeds;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

public class MedFetcher {

	ArrayList<Medication> allmeds = new ArrayList<Medication>();
	Context mContext;

	public void loadAssets(Context c)
	{
		mContext = c;
		loadValues();
	};
	
	
	public ArrayList<Medication> daysMedication(long day)
	{
		ArrayList<Medication> daysMeds = new ArrayList<Medication>();
		
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
		
		return  daysMeds;
	};
	
	
	public ArrayList<futureMedDetails>  futureMedication(long sDay, long eDay)
	{
		ArrayList<futureMedDetails> futureMeds = new ArrayList<futureMedDetails>();
		
		long millisDiff = eDay - sDay;
		
		long daysDiff = (int)(millisDiff / 86400000);
		
		long currDay = sDay;
		
		for (int i = 0;i < daysDiff;i++)
		{
			ArrayList<Medication> daysMeds = daysMedication(sDay);
			 combineLists(futureMeds, daysMeds);
					
			 sDay += 86400000;
		}
		
		return futureMeds;
	};
	
	
	private ArrayList<futureMedDetails> combineLists(ArrayList<futureMedDetails> futureMeds, ArrayList<Medication> daysMeds)
	{
		for(int i = 0;i < daysMeds.size();i++){
			int id = daysMeds.get(i).getMedId();
			
			int found = 0;
			int  totalUnits = 0;
			
			for(int j = 0; j < futureMeds.size(); j++){
				
				if(id == futureMeds.get(j).getMedId()){
					found = 1;
					totalUnits = unitsTotal(daysMeds.get(i).getFrequency());						
					futureMeds.get(j).increaseAmountNeeded(totalUnits);
					break;
				}
				
			}
			
			if(found == 0){
										
				totalUnits = unitsTotal(daysMeds.get(i).getFrequency());
				
				futureMedDetails newMed = new futureMedDetails(id, daysMeds.get(i).getMedName(),daysMeds.get(i).getDisplayName(), totalUnits);
				futureMeds.add(newMed);
			}
		
		}	
		
		return futureMeds;
	};
	
	private int unitsTotal(ArrayList<Frequency> frequency){
		int totalUnits = 0;
		
		for(int i = 0; i<frequency.size();i++){
			totalUnits += frequency.get(i).units;
		}
		
		return totalUnits;
	};
	
	
	public boolean loadValues(){
		ArrayList<Frequency> frequencyList = new ArrayList<Frequency>();

		try {
			// read file from assets
			AssetManager assetManager = mContext.getAssets();
			InputStream is = assetManager.open("meds.json");
			int size = is.available();
			byte[] buffer = new byte[size];
			is.read(buffer);
			is.close();
			String bufferString = new String(buffer);	

			JSONObject jsonObject = new JSONObject(bufferString);
			JSONArray medIndex = jsonObject.getJSONArray("medication");

			for(int k=0;k<medIndex.length();k++){
				frequencyList = new ArrayList<Frequency>();
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
					String time = frequencyObject.getString("time");
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
