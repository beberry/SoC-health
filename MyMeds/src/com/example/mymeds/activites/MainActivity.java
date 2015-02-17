package com.example.mymeds.activites;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.LocalActivityManager;
import android.app.TabActivity;
import android.app.ActionBar.LayoutParams;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TabHost.TabSpec;

import com.example.mymeds.R;
import com.example.mymeds.tabs.AllMeds;
import com.example.mymeds.tabs.FutureMeds;
import com.example.mymeds.tabs.TodaysMeds;
import com.example.mymeds.util.Alarms;
import com.example.mymeds.util.JSONUtils;
import com.example.mymeds.util.MedFetcher;
import com.example.mymeds.util.Medication;

@SuppressWarnings("deprecation")
public class MainActivity extends TabActivity {
	private GestureDetector gestureDetector;
	TabHost tabHost;
	ArrayList<Medication> allMeds = new ArrayList<Medication>();
	ArrayList<Medication> todaysMeds = new ArrayList<Medication>();
	Context mContext;
	MainActivity self = this;
	MedFetcher mMedFetcher = new MedFetcher();

	public void onCreate(Bundle savedInstanceState) {
		File file = new File(this.getFilesDir()+"//"+"meddata.json");
		if(!file.exists()){
		JSONUtils.writeStringToFile(readAssets(), this.getApplicationContext(), true); // Forces overwrite of existing JSON.
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mContext=this;

		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);

		disableHardwareMenuKey();
		gestureDetector = new GestureDetector(new SwipeGestureDetector());
		long today = MedFetcher.milliDate(year, month, day);


		allMeds = JSONUtils.loadValues(JSONUtils.readFile(this.getApplicationContext(), true), this.getApplicationContext());

		//Load all meds into mMedFetcher, get only today's, write to the today's meds file
		mMedFetcher.loadAssets(this, allMeds);
		File file2 = new File(this.getFilesDir()+"//"+"todaydata.json");
		if(!file2.exists()){
		JSONUtils.writeToFile(mMedFetcher.daysMedication(today), this, false); // Forces overwrite of existing JSON.
		}
		todaysMeds = JSONUtils.loadValues(JSONUtils.readFile(this.getApplicationContext(), false), this.getApplicationContext());

		tabHost = getTabHost();
		populateTabs();
		
		Alarms alarm = new Alarms(getApplicationContext());
		//alarm.setAllAlarms();
		//alarm.addAlarm(0);
		//alarm.addAlarm(1);
		//alarm.addAlarm(2);
		//alarm.setNextAlarm(0, 02300, "2300");
		LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
				new IntentFilter("Med-Taken"));
	}

	// Our handler for received Intents. This will be called whenever an Intent
	// with an action named "custom-event-name" is broadcasted.
	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// Get extra data included in the Intent
			//Update the locally held todays meds, knock out the first frequency
			int position = intent.getIntExtra("position", 0);

			//Modify and update the numbers of pills remaining for this pill
			mMedFetcher.modifyQuantity(todaysMeds.get(position).getIndex(), todaysMeds.get(position).getFrequency().get(0).getUnits());
			//allMeds.get(todaysMeds.get(position).getIndex()).setRemaining(allMeds.get(todaysMeds.get(position).getIndex()).getRemaining()-todaysMeds.get(position).getFrequency().get(0).getUnits());
			JSONUtils.writeToFile(todaysMeds, mContext, true);
			if(todaysMeds.get(position).getFrequency().size()==1){
				todaysMeds.remove(position);
			}
			else{
				todaysMeds.get(position).getFrequency().remove(0);
			}
			
			JSONUtils.writeToFile(todaysMeds, mContext, false);
			//Force refresh of data
			LocalActivityManager manager = getLocalActivityManager();
	        manager.destroyActivity("Today's Medication", true);
	        manager.destroyActivity("All Medication", true);
	        manager.destroyActivity("My Record", true);
	        tabHost.clearAllTabs();
			populateTabs();
		}
	};

	public void onResume(Bundle savedInstanceState){
		super.onResume();
	}
	
	private void populateTabs(){
		Intent intentToday = new Intent().setClass(this, TodaysMeds.class);
		intentToday.putParcelableArrayListExtra("meds", todaysMeds);
		TabSpec tabSpecToday = tabHost
				.newTabSpec("Today's Medication")
				.setIndicator("Today's Medication", null)
				.setContent(intentToday);


		Intent intentAll = new Intent().setClass(this, AllMeds.class);
		intentAll.putParcelableArrayListExtra("meds", allMeds);
		TabSpec tabSpecAll = tabHost
				.newTabSpec("All Medication")
				.setIndicator("All Medication", null)
				.setContent(intentAll);

		Intent intentFuture = new Intent().setClass(this, FutureMeds.class);
		intentFuture.putParcelableArrayListExtra("meds", allMeds);
		TabSpec tabSpecProfile = tabHost
				.newTabSpec("My Record")
				.setIndicator("My Record", null)
				.setContent(intentFuture);

		tabHost.setBackgroundResource(R.drawable.ab_stacked_solid_health);;

		// add all tabs 
		tabHost.addTab(tabSpecToday);
		tabHost.addTab(tabSpecAll);
		tabHost.addTab(tabSpecProfile);
		tabHost.setCurrentTab(0);
		
		tabHost.getTabWidget().getChildAt(0).setBackgroundColor(Color.parseColor("#71B238"));	
		tabHost.getTabWidget().getChildAt(1).setBackgroundColor(Color.parseColor("#A6CB45"));	
		tabHost.getTabWidget().getChildAt(2).setBackgroundColor(Color.parseColor("#A6CB45"));
		
		
//		tabHost.setOnTabChangedListener(new OnTabChangeListener(){
//			@Override
//			public void onTabChanged(String tabId) {
//			    if(tabId.equals("Today's Medication")) {					
//					tabHost.getTabWidget().getChildAt(0).setBackgroundColor(Color.parseColor("#71B238"));	
//					tabHost.getTabWidget().getChildAt(1).setBackgroundColor(Color.parseColor("#A6CB45"));	
//					tabHost.getTabWidget().getChildAt(2).setBackgroundColor(Color.parseColor("#A6CB45"));	
//			    }
//			    if(tabId.equals("All Medication")) {					
//					tabHost.getTabWidget().getChildAt(0).setBackgroundColor(Color.parseColor("#A6CB45"));	
//					tabHost.getTabWidget().getChildAt(1).setBackgroundColor(Color.parseColor("#71B238"));	
//					tabHost.getTabWidget().getChildAt(2).setBackgroundColor(Color.parseColor("#A6CB45"));	
//			    }
//			    if(tabId.equals("My Record")) {					
//					tabHost.getTabWidget().getChildAt(0).setBackgroundColor(Color.parseColor("#A6CB45"));	
//					tabHost.getTabWidget().getChildAt(1).setBackgroundColor(Color.parseColor("#A6CB45"));	
//					tabHost.getTabWidget().getChildAt(2).setBackgroundColor(Color.parseColor("#71B238"));	
//			    }
//			 
//			}});
		
		
		
	}

	/**
	 * Creates the Menu Bar.
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);

		MenuItem itemBlog = menu.add(Menu.NONE, // Group ID
				R.id.action_settings, // Item ID
				101, // Order
				"Blog"); // Title
		// To showAsAction attribute, use MenuItemCompat (set to always)
		MenuItemCompat.setShowAsAction(itemBlog, MenuItem.SHOW_AS_ACTION_ALWAYS);
		itemBlog.setIcon(R.drawable.ic_settings);
		super.onCreateOptionsMenu(menu);
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
		}
		return false;
	}

	/**
	 * Reads assets file into a string
	 * @return Assets file as string
	 */
	protected String readAssets()
	{	
		String bufferString = null;

		try
		{
			AssetManager assetManager = getAssets();
			InputStream is = assetManager.open("meds.json");
			int size = is.available();
			byte[] buffer = new byte[size];
			is.read(buffer);
			is.close();
			bufferString = new String(buffer);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return bufferString;
	}

	/**
	 * Method to get today's date and then calculate medication required for today.
	 */
	public void calculateMeds(){
		//TODO Is this needed?
		MedFetcher medFetcher = new MedFetcher();
		medFetcher.loadAssets(mContext, allMeds);
		Calendar c = new GregorianCalendar();
		todaysMeds = medFetcher.daysMedication(c.getTime().getTime());
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
			tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).setBackgroundColor(Color.parseColor("#A6CB45"));		
			tabHost.setCurrentTab(tabHost.getCurrentTab()+1);
			tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).setBackgroundColor(Color.parseColor("#71B238"));			
		}
	}

	private void onRightSwipe() {
		if(tabHost.getCurrentTab()>0){
			tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).setBackgroundColor(Color.parseColor("#A6CB45"));	
			tabHost.setCurrentTab(tabHost.getCurrentTab()-1);
			tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).setBackgroundColor(Color.parseColor("#71B238"));					
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == 100) {
			ArrayList<Medication> temp = new ArrayList<Medication>();
			temp = data.getParcelableArrayListExtra("meddata");
			allMeds = temp;
			this.recreate();

		}

	}
}
