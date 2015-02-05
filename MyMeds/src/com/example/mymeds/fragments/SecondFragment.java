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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.example.mymeds.R;
import com.example.mymeds.util.ListItemAdapter;
import com.example.mymeds.util.Medication;

public class SecondFragment extends Fragment {
	Context mContext;
	ListItemAdapter adapter;
	ArrayList<Medication> allmeds = new ArrayList<Medication>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_first, container, false);

		mContext = getActivity().getApplicationContext();

		ListView listViewItems = (ListView) rootView.findViewById(R.id.listview);

		if(allmeds.size()==0){
			loadValues();
		}

		// our adapter instance
		adapter = new ListItemAdapter(mContext,allmeds);

		// create a new ListView, set the adapter and item click listener
		listViewItems.setAdapter(adapter);

		// Set up the user interaction to manually show or hide the system UI.
		listViewItems.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				Medication item = adapter.getItem(position);
				Toast.makeText(mContext, String.valueOf(item.getItemName()),Toast.LENGTH_LONG).show();
			}
		});

		return rootView;
	}

	public boolean loadValues(){
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
				String description = tempCheck.getString("description");

				med.setItemDescription(description);
				med.setItemId(itemID);
				med.setItemName(itemName);
				allmeds.add(med);
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
		return true;
	}
}