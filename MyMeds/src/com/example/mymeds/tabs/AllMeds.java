package com.example.mymeds.tabs;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TableLayout;

import com.example.mymeds.R;
import com.example.mymeds.util.Frequency;
import com.example.mymeds.util.ListItemAdapter;
import com.example.mymeds.util.Medication;

public class AllMeds extends Activity {
	Context mContext;
	ListItemAdapter adapter;
	ArrayList<Medication> allmeds = new ArrayList<Medication>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getApplicationContext();
		setContentView(R.layout.tab_second);
		
		if(allmeds.size()==0){
			loadValues();
		}

		TableLayout listViewItems = (TableLayout) findViewById(R.id.listview);

		// our adapter instance
		adapter = new ListItemAdapter(mContext, R.id.listview, allmeds);

		for(int i=0;i<allmeds.size();i++){
			adapter.setSecondView(i,this.findViewById(R.layout.tab_second), listViewItems);
		}
		listViewItems.requestLayout();

	}

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