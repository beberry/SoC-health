package com.example.mymeds.fragments;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;

import com.example.mymeds.R;
import com.example.mymeds.util.ListItemAdapter;
import com.example.mymeds.util.Medication;

public class FirstFragment extends Fragment {
	Context mContext;
	ListItemAdapter adapter;
	ArrayList<Medication> meds = new ArrayList<Medication>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_first, container, false);

		mContext = getActivity().getApplicationContext();

		if(meds.size()==0){
			System.out.println("Loading values");
			loadValues();
		}

		TableLayout listViewItems = (TableLayout) rootView.findViewById(R.id.listview);

		// our adapter instance
		adapter = new ListItemAdapter(mContext, R.id.listview, meds);

		for(int i=0;i<meds.size();i++){
			adapter.setFirstView(i, rootView, listViewItems);
		}
		listViewItems.requestLayout();

		return rootView;
	}

	public boolean loadValues(){
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
				Medication med = new Medication();

				JSONObject tempCheck = medIndex.getJSONObject(k);
				int itemID = tempCheck.getInt("index");
				String itemName = tempCheck.getString("name");
				String displayName = tempCheck.getString("displayName");
				String time = tempCheck.getString("time");

				if(meds.contains((Integer)med.getMedId())==false){
					System.out.println("adding "+ itemID + " to the list");
					med.setMedId(itemID);
					med.setMedName(itemName);
					med.setDisplayName(displayName);
					med.setTime(time);
					meds.add(med);
				}else{
					med = null;
				}
			}
		} catch (IOException e) {
			Log.e("IOException","Error loading file");
			e.printStackTrace();
			return false;
		} catch (JSONException e) {
			Log.e("JSONException","JSON exception");
			e.printStackTrace();			
			return false;
		}
		System.out.println(meds.toString());
		return true;
	}
}