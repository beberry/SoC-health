package com.example.mymeds.activites;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.example.mymeds.R;
import com.example.mymeds.tabs.AllMeds;
import com.example.mymeds.tabs.FutureMeds;
import com.example.mymeds.tabs.TodaysMeds;
import com.example.mymeds.util.Frequency;
import com.example.mymeds.util.MedFetcher;
import com.example.mymeds.util.Medication;
import com.example.mymeds.util.NotificationsService;

@SuppressWarnings("deprecation")
public class MainActivity extends TabActivity {
	private GestureDetector gestureDetector;
	TabHost tabHost;
	ArrayList<Medication> allmeds = new ArrayList<Medication>();
	ArrayList<Medication> todaysmeds = new ArrayList<Medication>();
	Context mContext;
	File file;

	public void onCreate(Bundle savedInstanceState) {
		System.out.println(getFilesDir());
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mContext=this;

		createFile();

		String json = readFile("meddata.json");
		loadValues(json);
		calculateMeds();

		disableHardwareMenuKey();
		gestureDetector = new GestureDetector(new SwipeGestureDetector());

		tabHost = getTabHost(); 

		Intent intentToday = new Intent().setClass(this, TodaysMeds.class);
		intentToday.putParcelableArrayListExtra("meds", todaysmeds);
		TabSpec tabSpecToday = tabHost
				.newTabSpec("Todays Meds")
				.setIndicator("Todays Meds", null)
				.setContent(intentToday);

		Intent intentAll = new Intent().setClass(this, AllMeds.class);
		intentAll.putParcelableArrayListExtra("meds", allmeds);
		TabSpec tabSpecAll = tabHost
				.newTabSpec("All Meds")
				.setIndicator("All Meds", null)
				.setContent(intentAll);

		Intent intentFuture = new Intent().setClass(this, FutureMeds.class);
		intentFuture.putParcelableArrayListExtra("meds", allmeds);
		TabSpec tabSpecProfile = tabHost
				.newTabSpec("Future")
				.setIndicator("Future", null)
				.setContent(intentFuture);

		// add all tabs 
		tabHost.addTab(tabSpecToday);
		tabHost.addTab(tabSpecAll);
		tabHost.addTab(tabSpecProfile);
		tabHost.setCurrentTab(0);

		if (!isMyServiceRunning()){
			Log.v("NotificationsService", "Running");
			Intent serviceIntent = new Intent("com.example.mymeds.util.NotificationsService");
			getApplicationContext().startService(serviceIntent);
		}

		//Alarms alarm = new Alarms(getApplicationContext());
		//alarm.setAlarms();
	}

	public void onResume(Bundle savedInstanceState){
		super.onResume();

	}

	private boolean isMyServiceRunning() {
		ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if (NotificationsService.class.getName().equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Creates the Menu Bar.
	 * 
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	} 

	/**
	 * Event Handlers for Menu Bar.
	 * 
	 * (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{		
		switch(item.getItemId()){
		case R.id.action_settings:
			this.startActivity(new Intent(this, SettingsActivity.class));
			return true;
		case R.id.add_medication:
			this.startActivityForResult(new Intent(this, MedicationInputActivity.class), 100);
			return true;
		}
		return false;
	}

	protected boolean createFile(){
		file = new File(getFilesDir(), "meddata.json" );

		if(!file.exists()){
			try{
				// read file from assets
				AssetManager assetManager = getAssets();
				InputStream is = assetManager.open("meds.json");
				int size = is.available();
				byte[] buffer = new byte[size];
				is.read(buffer);
				is.close();
				String bufferString = new String(buffer);	

				Writer writer = new BufferedWriter(new FileWriter(file));
				writer.write(bufferString);
				writer.close();
				is.close();
			}
			catch(Exception e){
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	/**
	 * Disable Hardware Menu Button on phones. Force Menu drop down on Action Bar.
	 * 
	 * Referenced from: http://stackoverflow.com/questions/9286822/how-to-force-use-of-overflow-menu-on-devices-with-menu-button
	 */
	private void disableHardwareMenuKey()
	{
		try
		{
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
			if(menuKeyField != null) {
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(config, false);
			}
		} catch (Exception ex) {
			// Ignore
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (gestureDetector.onTouchEvent(event)) {
			return true;
		}
		return super.onTouchEvent(event);
	}

	private void onLeftSwipe() {
		if(tabHost.getCurrentTab()<3){
			tabHost.setCurrentTab(tabHost.getCurrentTab()+1);
		}
	}

	private void onRightSwipe() {
		if(tabHost.getCurrentTab()>0){
			tabHost.setCurrentTab(tabHost.getCurrentTab()-1);
		}
	}

	// Private class for gestures
	private class SwipeGestureDetector 
	extends SimpleOnGestureListener {
		// Swipe properties, you can change it to make the swipe 
		// longer or shorter and speed
		private static final int SWIPE_MIN_DISTANCE = 120;
		private static final int SWIPE_MAX_OFF_PATH = 200;
		private static final int SWIPE_THRESHOLD_VELOCITY = 200;

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2,
				float velocityX, float velocityY) {
			try {
				float diffAbs = Math.abs(e1.getY() - e2.getY());
				float diff = e1.getX() - e2.getX();

				if (diffAbs > SWIPE_MAX_OFF_PATH)
					return false;

				// Left swipe
				if (diff > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					MainActivity.this.onLeftSwipe();

					// Right swipe
				} else if (-diff > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					MainActivity.this.onRightSwipe();
				}
			} catch (Exception e) {
				Log.e("YourActivity", "Error on gestures");
			}
			return false;
		}
	}

	public void calculateMeds(){
		MedFetcher medFetcher = new MedFetcher();
		medFetcher.loadAssets(mContext, allmeds);
		Calendar c = new GregorianCalendar();
		todaysmeds = medFetcher.daysMedication(c.getTime().getTime());
	}

	public String readFile(String filename) {
		Log.i("json values", "reading file");

		StringBuffer json = new StringBuffer("");

		try {
			File filesDir = getFilesDir();
			Scanner input = new Scanner(new File(filesDir, filename));
			while(input.hasNext()){
				json.append(input.next());
			}
			Log.i("Completed", "Medication read in from external file");
		}
		catch (FileNotFoundException fnfe)
		{
			Log.w("FileNotFound", "File could not be located, will create");
		}
		catch (IOException ioe) {
			Log.e("JSONRead", "An IO Exception occured when reading file");
		}

		return json.toString();
	}


	public boolean loadValues(String JSONstring){
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
					}
				}
			} catch (JSONException e) {
				Log.e("JSONException","JSON exception");
				e.printStackTrace();			
				return false;
			}

			return true;
		}
		else {
			return false;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == 100) {
			ArrayList<Medication> temp = new ArrayList<Medication>();
			temp = data.getParcelableArrayListExtra("meddata");
			allmeds.add(temp.get(0));

		}

	}
}