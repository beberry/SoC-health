package com.example.mymeds.activites;

import java.lang.reflect.Field;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewConfiguration;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.example.mymeds.R;
import com.example.mymeds.tabs.AllMeds;
import com.example.mymeds.tabs.MyProfile;
import com.example.mymeds.tabs.TodaysMeds;
import com.example.mymeds.util.NotificationsService;

public class MainActivity extends TabActivity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Resources ressources = getResources(); 
		TabHost tabHost = getTabHost(); 

		Intent intentToday = new Intent().setClass(this, TodaysMeds.class);
		TabSpec tabSpecToday = tabHost
				.newTabSpec("Todays Meds")
				.setIndicator("Todays Meds", null)
				.setContent(intentToday);

		Intent intentAll = new Intent().setClass(this, AllMeds.class);
		TabSpec tabSpecAll = tabHost
				.newTabSpec("All Meds")
				.setIndicator("All Meds", null)
				.setContent(intentAll);

		Intent intentProfile = new Intent().setClass(this, MyProfile.class);
		TabSpec tabSpecProfile = tabHost
				.newTabSpec("Profile")
				.setIndicator("Profile", null)
				.setContent(intentProfile);

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
		Log.d("Problem Determination", "action_settings id: " + R.id.action_settings);
		//Log.d("Problem Determination", "action_exit id: " + R.id.action_exit);

		//if(id == R.id.action_settings - 10){ //ID of action_settings is 10 higher than viewPager.getID() for some reason.
		this.startActivity(new Intent(this, SettingsActivity.class));
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
}