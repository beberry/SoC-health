package com.example.mymeds.activites;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.example.mymeds.R;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.TableRow.LayoutParams;

public class MedicationInputActivity extends Activity{

	public TableLayout frequencyTable;
	
	private EditText editMedicineName;
	private EditText editDisplayName;
	private EditText editDescription;
	private Spinner spinnerMedicationType;
	private EditText editStartDate;
	private EditText editEndDate;
	private EditText editRemaining;
	private EditText editRepeatPeriod;
	
	PopupWindow datePicker;
	PopupWindow timePicker;
	
	long startDate, def, sMilli, eMilli = 0;
	boolean dateStartPressed = false;
	boolean timeStartPressed = false;
	boolean is24Hour = true;
	int year, month, day = 0;
	int hour, minute = 0;
	int selectedHour, selectedMinute = 0;
	String selectedTime = "";
	
	final int DAY = 86400000;
	static final int DATE_DIALOG_ID = 999;
	static final int TIME_DIALOG_ID = 100;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_medication_input);
		
		//Find UI components.
		editMedicineName = (EditText) findViewById(R.id.editMedicineName);
		editDisplayName = (EditText) findViewById(R.id.editDisplayName);
		editDescription = (EditText) findViewById(R.id.editDescription);
		spinnerMedicationType = (Spinner) findViewById(R.id.spinnerMedicationType);
		editStartDate = (EditText) findViewById(R.id.editStartDate);
		editEndDate = (EditText) findViewById(R.id.editEndDate);
		editRemaining = (EditText) findViewById(R.id.editRemaining);
		editRepeatPeriod = (EditText) findViewById(R.id.editRepeatPeriod);
		
		//Add onclicklistener for datePicker
		editStartDate.setOnClickListener(new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
	        	Log.d("Problem Determination", "editStartDate clicked");
	        	dateStartPressed = true;
	        	onDateEntrySelected(dateStartPressed, System.currentTimeMillis());
	        }

	    });
		
		//Add onclicklistener for datePicker
		editEndDate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d("Problem Determination", "editStartDate clicked");
				dateStartPressed = false;
				onDateEntrySelected(dateStartPressed, System.currentTimeMillis());
			}

		});
		
		//Obtain frequencyTable from the layout_medication_input.xml 
		frequencyTable = (TableLayout)findViewById(R.id.frequencyTable);
		addTableHeader();
		addFrequency(this.findViewById(R.id.frequencyTable));
		
		//Setup Date Picker variables
		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		Calendar cal = new GregorianCalendar(year, month, day);
		sMilli = cal.getTimeInMillis();
		eMilli = sMilli + 7*DAY;
				
	} 
	
	public void onDateEntrySelected(boolean startDate, long defaultDate) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(defaultDate);
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);
		showDialog(DATE_DIALOG_ID);
				
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
		   return new DatePickerDialog(this, datePickerListener, year, month, day);

		}
		return null;
	}
	
	private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

		// when dialog box is closed, below method will be called.
		public void onDateSet(DatePicker view, int selectedYear,
				int selectedMonth, int selectedDay) {

			Log.d("Problem Determination", "onDateSet ENTRY");

			year = selectedYear;
			month = selectedMonth;
			day = selectedDay;
			//Log.d("Problem Determination", "selectedYear: " + selectedYear);
			//Log.d("Problem Determination", "selectedMonth: " + selectedMonth);
			//Log.d("Problem Determination", "selectedDay: " + selectedDay);

			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

			Calendar c = new GregorianCalendar(year, month, day);


			String date = formatter.format(c.getTime());
			Log.d("test", "." + dateStartPressed);
			if (dateStartPressed)
			{
				editStartDate.setText(date);
				sMilli = c.getTimeInMillis();
				Log.d("Problem Determination", "editStartDate setText");
			}
			else
			{
				editEndDate.setText(date);
				sMilli = c.getTimeInMillis();
				Log.d("Problem Determination", "editEndDate setText");
			}

		}
	};	
	
	/**
	 * Called when User presses add Frequency button.
	 * Adds a new row of options for user to fill out.
	 * @param view
	 */
	public void addFrequency(View view)
	{
		TableRow inputRow = (TableRow) LayoutInflater.from(getApplicationContext()).inflate(R.layout.frequency_table_row, null);
		
		//Button to remove the row.
		Button removeButton = (Button)inputRow.findViewById(R.id.buttonRemoveFrequency);
		
		//Button Listener
		removeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View pos) {
				buttonRemoveFrequency(pos);
			}
		});

		
		final EditText editTime = (EditText)inputRow.findViewById(R.id.timeTextBox);
		
		//Add onclicklistener for timePicker
		editTime.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
								
				// Process to get Current Time
		        final Calendar c = Calendar.getInstance();
		        int mHour = c.get(Calendar.HOUR_OF_DAY);
		        int mMinute = c.get(Calendar.MINUTE);

		        TimePickerDialog tpd = new TimePickerDialog(MedicationInputActivity.this, //same Activity Context like before
		        new TimePickerDialog.OnTimeSetListener() {

		            @Override
		            public void onTimeSet(TimePicker view, int hour,
		                    int minute) {
		            	
		            	String finalHour = String.valueOf(hour);
		            	if(finalHour.length() < 2)
		            	{
		            		finalHour = '0' + finalHour; //Prevent removal of 0's at start
		            	}
		                editTime.setText(finalHour + String.valueOf(minute)); //You set the time for the EditText created
		            }
		        }, mHour, mMinute, true); //true = is24Hour format
		        tpd.show();
			}
		});

		//Add new Row
		frequencyTable.addView(inputRow, new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		
	}
	
	/**
	 * Called when User presses remove Frequency button.
	 * Removes row of options from the FrequencyTable.
	 * @param view
	 */
	public void buttonRemoveFrequency(View view)
	{
		frequencyTable.removeView((TableRow)view.getParent());
	}
	
	/**
	 * Called when User presses Add button.
	 * Extracts input from UI components and exports to JSON.
	 * 
	 * @param view
	 */
	public void addInput(View view)
	{		
		//Extract data from UI components.
		String medicineName = editMedicineName.getText().toString();
		String displayName = editDisplayName.getText().toString();
		String description = editDescription.getText().toString();
		String type = (String) spinnerMedicationType.getSelectedItem();
		String startDate = editStartDate.getText().toString();
		String endDate = editEndDate.getText().toString();
		String remaining = editRemaining.getText().toString();
		String repeatPeriod = editRepeatPeriod.getText().toString();
		
		List<Integer> listTime = new ArrayList<Integer>();
		List<Integer> listDosage = new ArrayList<Integer>();
		List<Integer> listUnit = new ArrayList<Integer>();
		
		TableRow tableRow;
		EditText frequencyTime;
		EditText frequencyDosage;
		EditText frequencyUnit;
		
		//Get contents from each table row
		for(int i = 1; i < frequencyTable.getChildCount(); i++)
		{
			//Get table row
			tableRow = (TableRow) frequencyTable.getChildAt(i);
			
			//Get table cells
			frequencyTime = (EditText) tableRow.getChildAt(0); //Time
			frequencyDosage = (EditText) tableRow.getChildAt(1); //Dosage
			frequencyUnit = (EditText) tableRow.getChildAt(2); //Unit
			
			//Add cell data to Lists (parse to int)
			listTime.add(Integer.valueOf(frequencyTime.getText().toString()));
			listDosage.add(Integer.valueOf(frequencyDosage.getText().toString()));
			listUnit.add(Integer.valueOf(frequencyUnit.getText().toString()));
			
			Log.d("Problem Determination", "Time: " + frequencyTime.getText().toString());
			Log.d("Problem Determination", "Dosage: " + frequencyDosage.getText().toString());
			Log.d("Problem Determination", "Unit: " + frequencyUnit.getText().toString());
			
			Log.d("Problem Determination", "List Time: " + listTime.get(0));
			Log.d("Problem Determination", "List Dosage: " + listTime.get(0));
			Log.d("Problem Determination", "List Unit: " + listTime.get(0));

		}
		
		//Calculate StartTime and EndTime | Parse '/' out from Dates, append the Time.
		//TODO
		long startTime = 0;
		long endTime = 0;
		
		//Export to AllMed.json, save to device
		//TODO
		
		Toast.makeText(getApplicationContext(), "Medication Added", Toast.LENGTH_LONG).show();
		finish();
	}
	
	/**
	 * Called when User presses Cancel button.
	 * Returns user back to Main Activity.
	 * 
	 * @param view
	 */
	public void cancelInput(View view)
	{
		Toast.makeText(getApplicationContext(), "Medication Not Added", Toast.LENGTH_LONG).show();
		finish();
	}

	/**
	 * Adds a Header Row with column titles.
	 */
	private void addTableHeader()
	{
		//Header row
		TableRow headerRow = new TableRow(this);
		headerRow.setPadding(5, 0, 0, 10);//40 50 10 5
		headerRow.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		
		//Title
		TextView headerTitle = new TextView(this);
		
		headerTitle.setTextSize(15);
		headerTitle.setText("Time Taken"); 
		headerTitle.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
		headerTitle.setGravity(Gravity.CENTER);
		
		headerRow.addView(headerTitle);
		
		
		//Dosage
		TextView headerDosage = new TextView(this);
		
		headerDosage.setTextSize(15);
		headerDosage.setText("Dosage"); 
		headerDosage.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
		headerDosage.setGravity(Gravity.CENTER);
		
		headerRow.addView(headerDosage);	
		
		
		//Units
		TextView headerUnits = new TextView(this);
		
		headerUnits.setTextSize(15);
		headerUnits.setText("Servings"); 
		headerUnits.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
		headerUnits.setGravity(Gravity.CENTER);
		
		headerRow.addView(headerUnits);
				
		frequencyTable.addView(headerRow, new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	}
}
