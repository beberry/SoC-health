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
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

public class SettingsActivity extends Activity{

	Button buttonSave;
	Button buttonCancel;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_settings);
				
		buttonSave = (Button) findViewById(R.id.buttonSave);
		buttonCancel = (Button) findViewById(R.id.buttonCancel);
		
		Switch switchBanners = (Switch) findViewById(R.id.switchBanners);
		Switch switchSounds = (Switch) findViewById(R.id.switchSounds);
		
		Spinner spinnerSoundsSelection = (Spinner) findViewById(R.id.spinnerSoundsSelection);
		Spinner spinnerSnoozeTime = (Spinner) findViewById(R.id.spinnerSnoozeTime);
		Spinner spinnerHowLongBefore = (Spinner) findViewById(R.id.spinnerHowLongBefore);
		Spinner spinnerTextSize = (Spinner) findViewById(R.id.spinnerTextSize);
		
		spinnerSoundsSelection.setOnItemSelectedListener(new OnItemSelectedListener()
		{
			Boolean init_done = false;
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				if(init_done)
				{
					Spinner sound_select = (Spinner) findViewById(R.id.spinnerSoundsSelection);
					if(sound_select.getSelectedItem().equals("Notification Sound"))
					{
						MediaPlayer player = MediaPlayer.create(getApplicationContext(),
							    Settings.System.DEFAULT_NOTIFICATION_URI);
							player.start();
					}
					else if(sound_select.getSelectedItem().equals("Ringtone Sound"))
					{
						MediaPlayer player = MediaPlayer.create(getApplicationContext(),
							    Settings.System.DEFAULT_RINGTONE_URI);
							player.start();
					}
					else if(sound_select.getSelectedItem().equals("Alarm Sound"))
					{
						MediaPlayer player = MediaPlayer.create(getApplicationContext(),
							    Settings.System.DEFAULT_ALARM_ALERT_URI);
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
		
		try {
			store = (SettingsStore)PojoMapper.fromJson(readFromSettings(), SettingsStore.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			Log.w("Hello", PojoMapper.toJson(store, true));
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		
		Log.d("Problem Determination", "banner: " + banner +
									   "\nsounds: " + sounds + 
									   "\nsoundsSelection: " + soundsSelection +
									   "\nsnoozeTime: " + snoozeTime + 
									   "\nhowLongBefore: " + howLongBefore +
									   "\ntextSize: " + textSize);
		
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
			Log.w("osfn", PojoMapper.toJson(store, true));
			writeToSettings(PojoMapper.toJson(store, true));
			Toast.makeText(getApplicationContext(), "Settings Saved", Toast.LENGTH_LONG).show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(getApplicationContext(), "Error Saving Settings", Toast.LENGTH_LONG).show();
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
		Toast.makeText(getApplicationContext(), "Changes Not Saved", Toast.LENGTH_LONG).show();
		finish();
	}
	
}   
