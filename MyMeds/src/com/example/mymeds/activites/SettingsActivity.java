package com.example.mymeds.activites;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

import com.example.mymeds.R;
import com.example.mymeds.libs.PojoMapper;
import com.example.mymeds.stores.SettingsStore;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

public class SettingsActivity extends Activity implements OnClickListener{

	Button buttonSave;
	Button buttonCancel;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_settings);
				
		buttonSave = (Button) findViewById(R.id.buttonSave);
		buttonCancel = (Button) findViewById(R.id.buttonCancel);
		
		SettingsStore store = null;
		try {
			store = (SettingsStore)PojoMapper.fromJson(readFromSettings(), SettingsStore.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			Log.w("Hello", PojoMapper.toJson(store, true));
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Called when Save button is selected. Extracts data from UI widgets and exports into JSON.
	 * @param view
	 */
	public void saveChanges(View view){
		
		//Find UI widgets.
		Switch switchBanners = (Switch) findViewById(R.id.switchBanners);
		Switch switchSounds = (Switch) findViewById(R.id.switchSounds);
		
		Spinner spinnerSoundsSelection = (Spinner) findViewById(R.id.spinnerSoundsSelection);
		Spinner spinnerSnoozeTime = (Spinner) findViewById(R.id.spinnerSnoozeTime);
		Spinner spinnerHowLongBefore = (Spinner) findViewById(R.id.spinnerHowLongBefore);
		Spinner spinnerTextSize = (Spinner) findViewById(R.id.spinnerTextSize);
		
		//Extract data from UI widgets.
		boolean banner = switchBanners.isChecked();
		boolean sounds = switchSounds.isChecked();
		String soundsSelection = (String) spinnerSoundsSelection.getSelectedItem();
		String snoozeTime = (String) spinnerSnoozeTime.getSelectedItem();
		String howLongBefore = (String) spinnerHowLongBefore.getSelectedItem();
		String textSize = (String) spinnerTextSize.getSelectedItem();
		
		Log.d("Problem Determination", "banner: " + banner +
									   "\nsounds: " + sounds + 
									   "\nsoundsSelection: " + soundsSelection +
									   "\nsnoozeTime: " + snoozeTime + 
									   "\nhowLongBefore: " + howLongBefore +
									   "\ntextSize: " + textSize);
		
		SettingsStore store = new SettingsStore();
		store.setBanners(banner);
		store.setSounds(sounds);
		store.setSoundSelection(soundsSelection);
		store.setSnoozeTime(snoozeTime);
		store.setHowLongBefore(howLongBefore);	
		store.setTextSize(textSize);
		
		try {
			Log.w("osfn", PojoMapper.toJson(store, true));
			writeToSettings(PojoMapper.toJson(store, true));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private String readFromSettings()
	{
		String ret = "";

	    try {
	        InputStream inputStream = openFileInput("settings.txt");

	        if ( inputStream != null ) {
	            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
	            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
	            String receiveString = "";
	            StringBuilder stringBuilder = new StringBuilder();

	            while ( (receiveString = bufferedReader.readLine()) != null ) {
	                stringBuilder.append(receiveString);
	            }

	            inputStream.close();
	            ret = stringBuilder.toString();
	        }
	    }
	    catch (FileNotFoundException e) {
	        Log.e("error", "File not found: " + e.toString());
	    } catch (IOException e) {
	        Log.e("error", "Can not read file: " + e.toString());
	    }

	    return ret;
	}
	
	
	private void writeToSettings(String json)
	{
		String filename = "settings.txt";
		FileOutputStream outputStream;

		try {
		  outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
		  outputStream.write(json.getBytes());
		  outputStream.close();
		} catch (Exception e) {
		  e.printStackTrace();
		}	
	}
	
	
	/**
	 * Called when Cancel button is selected. Kills activity and returns to MainActivity.
	 * @param view
	 */
	public void cancelChanges(View view){
		
	}
	
	/**
	 * Deprecated Code		
	 */
	void Useless()
	{
	/*
	 * switchBanners.setChecked(true);
		switchSounds.setChecked(true);
		
		//Set switchBanners onClickListener listeners
		switchBanners.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				//Output isChecked to preferences file		
				if(isChecked)
				{
					Toast.makeText(getApplicationContext(), "Mike Coutts - Teradata", Toast.LENGTH_LONG).show();
				}
			}			
		});
		
		//Set switchSounds onClickListener listeners
		switchSounds.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				//Output isChecked to preferences file			
			}			
		});
	 */
	}
}   
