package com.example.mymeds.tabs;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.PopupWindow;
import android.widget.TableLayout;
import android.widget.TextView;

import com.example.mymeds.R;
import com.example.mymeds.util.ListItemAdapterFuture;
import com.example.mymeds.util.MedFetcher;
import com.example.mymeds.util.futureMedDetails;

@SuppressLint("SimpleDateFormat")
public class FutureMeds extends FragmentActivity {

	//Variables for start date, end date, start date in milliseconds, end date in milliseconds
	long startDate, def, sMilli, eMilli = 0;
	
	int year, month, day =0;

	//Flag for start or end day inputed
	boolean startPressed =false;
	
	//Length of a day in milliseconds
	final int DAY = 86400000;

	//Difference between inputed days
	int daysDiff = 0;

	//id for dialog date picker
	static final int DATE_DIALOG_ID = 999;
	
	TextView startDateText, endDateText;

	@SuppressLint("SimpleDateFormat")
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab_third);
				
		//Initilse buttons and textViews
		final Button startBtn = (Button) findViewById(R.id.start);
		final Button endBtn = (Button)    findViewById(R.id.end);
		startDateText = (TextView)    findViewById(R.id.startDate);
		endDateText = (TextView)    findViewById(R.id.endDate);				
		
		//Calculate and display the medication and amounts needed for the next 7 days
		//Calendar to get todays date 
		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);
		
		//Format for dispaly
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		
		//Get todays date in milliseconds
		sMilli = milliDate(year, month, day);
		
		//Get date for 7 days into the future
		eMilli = sMilli + 7*DAY;
		
		//Get the medication needed between the two dates
		getMedication();
	     
		//Get and format current date
		String date = formatDate(year, month,day);
		//Display start date
		startDateText.setText(date);
				
		//Format end date
		date = formatDate(year, month,day+7);
		//Display end date
		endDateText.setText(date);

		//On click listener for button to allow start date input
		startBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					//flag shows start date input selected
					startPressed = true;
					//begin display of date picker
					onDateEntrySelected(startPressed, sMilli); 

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});

		//On click listener for button to allow end date input		
		endBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				try {
					//flag shows end date input selected
					startPressed = false;
					//begin display of date picker
					onDateEntrySelected(startPressed, eMilli); 

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});	

	}


	//set default values of picker and display it
	@SuppressWarnings("deprecation")
	public void onDateEntrySelected(boolean startDate, long defaultDate) {
		
		//Use calendar to generate date values for default
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(defaultDate);
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);
		//calling date picker
		showDialog(DATE_DIALOG_ID);

	}


	//display date picker 
	@Override
	protected Dialog onCreateDialog(int id) {
		//Switch to identify picker called (Date pciker is only one possible)
		switch (id) {
		//id matches date picker
		case DATE_DIALOG_ID:
			//create new date picker with default values
			return new DatePickerDialog(this, datePickerListener, 
					year, month,day);
		}
		return null;
	}

	//Capture input made by user
	private DatePickerDialog.OnDateSetListener datePickerListener 
	= new DatePickerDialog.OnDateSetListener() {

		// when dialog box is closed, below method will be called.
		public void onDateSet(DatePicker view, int selectedYear,
				int selectedMonth, int selectedDay) {
			//Store selected year, month and day
			year = selectedYear;
			month = selectedMonth;
			day = selectedDay;

			//format date for display
			String date = formatDate(year, month, day);
			//convert to milliseconds
			Long milliTime = milliDate(year, month, day);
			
			//if flag is true
			if (startPressed)
			{
				//display start date
				startDateText.setText(date);
				sMilli = milliTime;
			}
			
			//if false
			else
			{
				//display end date
				endDateText.setText(date);
				eMilli = milliTime;

			}
			
			//get medication needed between the 2 dates
			getMedication();
		}
	};	

	
	//gets and displays the medication needed between a start ad end date
	public void getMedication()
	{
		//get diffrence between dates in milliseconds
		long millisDiff = eMilli - sMilli;

		//claculate the diffrence in days
		daysDiff = (int)(millisDiff / 86400000);

		//create new instance of MefFetcher to calculate the medication needed
		MedFetcher mf = new MedFetcher();
		mf.loadAssets(this, getIntent().getParcelableArrayListExtra("meds"));

		//store the medication needed between the two dates
		ArrayList<futureMedDetails>  futureMeds =  mf.futureMedication(sMilli,eMilli);

		//new instance of display adapter
		ListItemAdapterFuture adapter;

		
		TableLayout listViewItems = (TableLayout) findViewById(R.id.futurelistview);
		listViewItems.removeAllViewsInLayout();
		
		// our adapter instance
		adapter = new ListItemAdapterFuture(this, R.id.futurelistview, futureMeds);

		//for each medication display in new row
		for(int i=0;i<futureMeds.size();i++){
			adapter.setFutureRow(i, this.findViewById(R.layout.tab_third), listViewItems);
		}
		
		listViewItems.requestLayout();
	};

	//Convert year, month, day into milliseconds
	private long milliDate(int year, int month, int day)
	{
		Calendar cal = new GregorianCalendar(year, month, day);
		return cal.getTimeInMillis();
	}
	
	//Convert year, month, day into date formated string
	private String formatDate(int year, int month, int day)
	{
		//Formatter for displaying date
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

		//calendar to turn ints to date
		Calendar c = new GregorianCalendar(year, month, day);

		//format date for display and return
		return formatter.format(c.getTime());
	}

}