package com.example.mymeds.activites;

import android.app.ActionBar;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.example.mymeds.R;
import com.example.mymeds.tabs.AllMeds;
import com.example.mymeds.tabs.MyProfile;
import com.example.mymeds.tabs.TodaysMeds;
import com.example.mymeds.util.NotificationsService;

public class MainActivity extends TabActivity {

	private ViewPager viewPager;
	private ActionBar actionBar;
	// Tab titles
	private String[] tabs = { "Daily Meds", "All Meds", "Profile" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Resources resources = getResources(); 
		TabHost tabHost = getTabHost(); 

		// Android tab
		Intent intentAndroid = new Intent().setClass(this, TodaysMeds.class);
		TabSpec tabSpecAndroid = tabHost
				.newTabSpec("Today")
				.setIndicator("Todays Meds", null)
				.setContent(intentAndroid);

		// Apple tab
		Intent intentApple = new Intent().setClass(this, AllMeds.class);
		TabSpec tabSpecApple = tabHost
				.newTabSpec("All")
				.setIndicator("All Meds", null)
				.setContent(intentApple);

		// Windows tab
		Intent intentWindows = new Intent().setClass(this, MyProfile.class);
		TabSpec tabSpecWindows = tabHost
				.newTabSpec("Profile")
				.setIndicator("Profile", null)
				.setContent(intentWindows);


		// add all tabs 
		tabHost.addTab(tabSpecAndroid);
		tabHost.addTab(tabSpecApple);
		tabHost.addTab(tabSpecWindows);

		//set Windows tab as default (zero based)
		tabHost.setCurrentTab(1);


		if (!isMyServiceRunning()){
			Log.v("NotificationsService", "Running");
			Intent serviceIntent = new Intent("com.example.mymeds.util.NotificationsService");
			getApplicationContext().startService(serviceIntent);
		}
	}

	private boolean isMyServiceRunning() {
		ActivityManager manager = (ActivityManager) getSystemService(getApplicationContext().ACTIVITY_SERVICE);
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
		int id = viewPager.getId();
		Log.d("Problem Determination", "id: " + id);
		Log.d("Problem Determination", "action_settings id: " + R.id.action_settings);
		//Log.d("Problem Determination", "action_exit id: " + R.id.action_exit);

		//if(id == R.id.action_settings - 10){ //ID of action_settings is 10 higher than viewPager.getID() for some reason.
		this.startActivity(new Intent(this, SettingsActivity.class));
		return true;

	}
}