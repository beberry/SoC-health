package com.example.mymeds.tabs;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.mymeds.R;

public class FutureMeds extends FragmentActivity {

	PopupWindow pw;

	long startDate, def = 0;
	boolean startPressed =false;
	int year, month, day =0;
	long sMilli, eMilli = 0;
	
	int daysDiff = 0;
	
	TextView startDateText, endDateText;
	 	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab_third);

		final Button startBtn = (Button) findViewById(R.id.start);
		final Button endBtn = (Button)    findViewById(R.id.end);
		startDateText = (TextView)    findViewById(R.id.startDate);
		endDateText = (TextView)    findViewById(R.id.endDate);

		startBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					startPressed = true;
					onDateEntrySelected(startPressed, System.currentTimeMillis()); 
					
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});
		
		endBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(startDate==0){
					//Have the default date as tomorrow if the start date hasn't been
					//selected yet
					def = System.currentTimeMillis() + 86400000;
				}
				else{
					def = startDate + 86400000;
				}
				try {
					startPressed = false;
					onDateEntrySelected(startPressed, def); 
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});	

	}

	
	public void onDateEntrySelected(boolean startDate, long defaultDate) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(defaultDate);
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);
		showDialog(DATE_DIALOG_ID);
				
	}
	
	static final int DATE_DIALOG_ID = 999;
	
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
		
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		
		Calendar c = new GregorianCalendar(year, month, day);
		
		
		String date = formatter.format(c.getTime());
		
		if (startPressed)
		{
			startDateText.setText(date);
			sMilli = c.getTimeInMillis();
			
			//differenceInDays();
		}
		else
		{
			endDateText.setText(date);
			eMilli = c.getTimeInMillis();
			
			differenceInDays();
		}
		
}
};	
	
public void differenceInDays()
{
	
	long millisDiff = eMilli - sMilli;
		
	daysDiff = (int)(millisDiff / 86400000);
		
};


}