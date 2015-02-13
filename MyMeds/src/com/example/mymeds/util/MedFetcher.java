package com.example.mymeds.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.mymeds.libs.PojoMapper;
import com.example.mymeds.tabs.FutureMeds;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Parcelable;
import android.util.Log;

public class MedFetcher {

	final String JSON_PATH = "meds.json";
	ArrayList<Medication> allmeds = new ArrayList<Medication>();
	Context mContext;

	public void loadAssets(Context c, ArrayList arrayList)
	{
		mContext = c;
		allmeds = arrayList;
	};
	
	
	public ArrayList<Medication> daysMedication(long day)
	{
		ArrayList<Medication> daysMeds = new ArrayList<Medication>();
		
		for (int i=0;i<allmeds.size();i++){
			
			long st = allmeds.get(i).getStartTime();
			long et = allmeds.get(i).getEndTime();
			
			long stDiff = day - st;
			
			if(stDiff >= 0 && !(day >et)){
				if(stDiff % allmeds.get(i).getRepeatPeriod() == 0)
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
	
	public void modifyQuantity(int medId, int quantConsumed){
		
		//TODO: This method needs to use the load/save stuff from the MainActivity
		
		for(int i=0; i<allmeds.size(); i++){
			Medication currentMed = allmeds.get(i);
			if(currentMed.getMedId()==medId){
				Medication modMed = currentMed;
				//TODO: No checking this results in a positive number currently
				modMed.setRemaining(currentMed.getRemaining()-quantConsumed);
				allmeds.set(i, modMed);
				break;
			}
		}
		
	}

}
