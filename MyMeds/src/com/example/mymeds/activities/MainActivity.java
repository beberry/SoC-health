package com.example.mymeds.activities;
 
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.content.Intent;
import com.example.mymeds.R;
import com.example.mymeds.util.NotificationsService;
import com.example.mymeds.util.TabsPagerAdapter;
import com.example.mymeds.fragments.DatePickerFragment;
import com.example.mymeds.fragments.ThirdFragment;
import com.example.mymeds.fragments.ThirdFragment.OnDateEntrySelectedListener;
import com.example.mymeds.util.TabsPagerAdapter;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.widget.DatePicker;
 
public class MainActivity extends FragmentActivity implements
        ActionBar.TabListener, OnDateEntrySelectedListener{
 
    private ViewPager viewPager;
    private TabsPagerAdapter mAdapter;
    private ActionBar actionBar;
    // Tab titles
    private String[] tabs = { "Daily Meds", "All Meds", "Future Meds" };
    int year, month, day =0;
    private int currentPage =1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_ACTION_BAR);
		setContentView(R.layout.activity_main);

		// Initialisation
		viewPager = (ViewPager) findViewById(R.id.pager);
		actionBar = getActionBar();
		mAdapter = new TabsPagerAdapter(getSupportFragmentManager());
 
		viewPager.setAdapter(mAdapter);
		actionBar.setHomeButtonEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);        

		// Adding Tabs
		for (String tab_name : tabs) {
			actionBar.addTab(actionBar.newTab().setText(tab_name)
					.setTabListener(this));
		}

		/**
		 * on swiping the viewpager make respective tab selected
		 * */
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				// on changing the page
				// make respected tab selected
				currentPage = position;
				actionBar.setSelectedNavigationItem(position);
			}
 
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
		
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
    
    
 
	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// on tab selected
		// show respected fragment view
		currentPage = tab.getPosition();
		viewPager.setCurrentItem(currentPage);
	}
 
    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
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
	



	@SuppressWarnings("deprecation")
	@Override
	public void onDateEntrySelected(boolean startDate, long defaultDate) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(defaultDate);
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);
		showDialog(DATE_DIALOG_ID);
				
	}
	
	static final int DATE_DIALOG_ID = 5;
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
		   return new DatePickerDialog(this, datePickerListener, 
                         year, month,day);
		}
		return null;
	}
	
	private DatePickerDialog.OnDateSetListener datePickerListener 
    = new DatePickerDialog.OnDateSetListener() {

// when dialog box is closed, below method will be called.
public void onDateSet(DatePicker view, int selectedYear,
	int selectedMonth, int selectedDay) {
		year = selectedYear;
		month = selectedMonth;
		day = selectedDay;
		SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy");
		Date d = new Date();
		try {
			d = f.parse(day+"-"+month+"-"+year);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long dateLong = d.getTime();
		ThirdFragment articleFrag = (ThirdFragment)
                getSupportFragmentManager().findFragmentById(currentPage);
		articleFrag.setDate(dateLong);		
		}
	};
	
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