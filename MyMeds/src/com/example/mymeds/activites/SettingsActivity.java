package com.example.mymeds.activites;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.example.mymeds.R;
import com.example.mymeds.libs.PojoMapper;
import com.example.mymeds.stores.SettingsStore;

/**
 * Handles the Activity for the Settings in the application.
 * Banners, Sounds, Notification Alert and Snooze not implemented for prototype.
 * Sounds are not global to the application, only in this Activity.
 * TextSize is global to the application.
 */
public class SettingsActivity extends Activity{

	Button buttonSave;
	Button buttonCancel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_settings);

		//Locate UI components
		buttonSave = (Button) findViewById(R.id.buttonSave);
		buttonCancel = (Button) findViewById(R.id.buttonCancel);

		Switch switchBanners = (Switch) findViewById(R.id.switchBanners);
		Switch switchSounds = (Switch) findViewById(R.id.switchSounds);

		Spinner spinnerSoundsSelection = (Spinner) findViewById(R.id.spinnerSoundsSelection);
		Spinner spinnerSnoozeTime = (Spinner) findViewById(R.id.spinnerSnoozeTime);
		Spinner spinnerHowLongBefore = (Spinner) findViewById(R.id.spinnerHowLongBefore);
		Spinner spinnerTextSize = (Spinner) findViewById(R.id.spinnerTextSize);

		//Onclick Event Handler for Sounds Spinner.
		spinnerSoundsSelection.setOnItemSelectedListener(new OnItemSelectedListener()
		{
			//Disable sounds played on startup.
			Boolean init_done = false;
			
			//Plays default notification sound on item selected.
			//All selections currently play the same sound.
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				if(init_done)
				{
					Spinner sound_select = (Spinner) findViewById(R.id.spinnerSoundsSelection);
					if(sound_select.getSelectedItem().equals("Sound 1"))
					{
						MediaPlayer player = MediaPlayer.create(getApplicationContext(),
								Settings.System.DEFAULT_NOTIFICATION_URI);
						player.start();
					}
					else if(sound_select.getSelectedItem().equals("Sound 2"))
					{
						MediaPlayer player = MediaPlayer.create(getApplicationContext(),
								Settings.System.DEFAULT_NOTIFICATION_URI);
						player.start();
					}
					else if(sound_select.getSelectedItem().equals("Sound 3"))
					{
						MediaPlayer player = MediaPlayer.create(getApplicationContext(),
								Settings.System.DEFAULT_NOTIFICATION_URI);
						player.start();
					}
				}
				init_done = true;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}


		});

		SettingsStore store = null;

		//Read in SharedPreferences and store them locally.
		try {
			store = (SettingsStore)PojoMapper.fromJson(readFromSettings(), SettingsStore.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Store SharedPreferences locally.
		if(store != null)
		{
			switchBanners.setChecked(store.getBanners());
			switchSounds.setChecked(store.getSounds());

			spinnerSoundsSelection.setSelection(store.getSoundSelection());
			spinnerSnoozeTime.setSelection(store.getSnoozeTime());
			spinnerHowLongBefore.setSelection(store.getHowLongBefore());
			spinnerTextSize.setSelection(store.getTextSize());
		}
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
		int soundsSelection = (int)spinnerSoundsSelection.getSelectedItemPosition();
		int snoozeTime = (int)spinnerSnoozeTime.getSelectedItemPosition();
		int howLongBefore = (int) spinnerHowLongBefore.getSelectedItemPosition();
		int textSize = (int) spinnerTextSize.getSelectedItemPosition();

//		Log.d("Problem Determination", "banner: " + banner +
//				"\nsounds: " + sounds + 
//				"\nsoundsSelection: " + soundsSelection +
//				"\nsnoozeTime: " + snoozeTime + 
//				"\nhowLongBefore: " + howLongBefore +
//				"\ntextSize: " + textSize);

		//Export to JSON
		SettingsStore store = new SettingsStore();
		store.setBanners(banner);
		store.setSounds(sounds);
		store.setSoundSelection(soundsSelection);
		store.setSnoozeTime(snoozeTime);
		store.setHowLongBefore(howLongBefore);	
		store.setTextSize(textSize);

		//Save to Shared Preferences
		SharedPreferences prefs = this.getSharedPreferences(
				"com.example.mymeds", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean("banner",banner);
		editor.putBoolean("sounds", sounds);
		editor.putInt("soundsSelection", soundsSelection);
		editor.putInt("snoozeTime", snoozeTime);
		editor.putInt("howLongBefore", howLongBefore);
		editor.putInt("textSize", textSize);
		editor.apply();

		try {
			writeToSettings(PojoMapper.toJson(store, true));
			Toast.makeText(getApplicationContext(), "Settings Saved", Toast.LENGTH_LONG).show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(getApplicationContext(), "Error Saving Settings", Toast.LENGTH_LONG).show();
		}
		
		finish();
	}

	/**
	 * Reads in Settings from previous time.
	 * @return
	 */
	private String readFromSettings()
	{
		String ret = "";

		try {
			//Open desired file
			InputStream inputStream = openFileInput("settings.txt");

			//Check for null
			if ( inputStream != null ) {
				InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				
				String receiveString = "";
				StringBuilder stringBuilder = new StringBuilder();

				//While still to read, append next line onto stringbuilder
				while ( (receiveString = bufferedReader.readLine()) != null ) {
					stringBuilder.append(receiveString);
				}

				//Close the file reader
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

	/**
	 * Write to Settings file.
	 * @param json
	 */
	private void writeToSettings(String json)
	{
		String filename = "settings.txt";
		FileOutputStream outputStream;

		//Write to application's directory
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
		Toast.makeText(getApplicationContext(), "Changes Not Saved", Toast.LENGTH_LONG).show();
		finish();
	}

}   
