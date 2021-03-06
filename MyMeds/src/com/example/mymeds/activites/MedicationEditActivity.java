package com.example.mymeds.activites;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.mymeds.R;
import com.example.mymeds.util.Frequency;
import com.example.mymeds.util.Medication;

@SuppressLint({ "SimpleDateFormat", "InflateParams" })
public class MedicationEditActivity extends Activity{

	public TableLayout frequencyTable;

	private EditText editMedicineName;
	private EditText editDisplayName;
	private EditText editDescription;
	private Spinner spinnerMedicationType;
	private EditText editStartDate;
	private EditText editEndDate;
	private EditText editRemaining;
	private EditText editRepeatPeriod;

	PopupWindow datePicker, timePicker;

	private int editIndex;
	
	long startDate, def, sMilli, eMilli = 0;
	long actualStartDate, actualEndDate;
	boolean dateStartPressed = false;
	boolean timeStartPressed = false;
	boolean is24Hour = true;
	int year, month, day = 0;
	int hour, minute = 0;
	int selectedHour, selectedMinute = 0;
	String selectedTime = "";
	int size=0;
	ArrayList<Medication> meds;

	final int DAY = 86400000;
	static final int DATE_DIALOG_ID = 999;

	@SuppressWarnings("unused")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_medication_input);
		Intent intent = getIntent();
		size = intent.getIntExtra("size", 1);
		meds = intent.getParcelableArrayListExtra("meds");
		this.editIndex = intent.getIntExtra("editIndex", -1);
	
		//Find UI components.
		editMedicineName = (EditText) findViewById(R.id.editMedicineName);
		editDisplayName = (EditText) findViewById(R.id.editDisplayName);
		editDescription = (EditText) findViewById(R.id.editDescription);
		spinnerMedicationType = (Spinner) findViewById(R.id.spinnerMedicationType);
		editStartDate = (EditText) findViewById(R.id.editStartDate);
		editEndDate = (EditText) findViewById(R.id.editEndDate);
		editRemaining = (EditText) findViewById(R.id.editRemaining);
		editRepeatPeriod = (EditText) findViewById(R.id.editRepeatPeriod);

		//Force activity to show soft keyboard on startup.
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(editMedicineName, InputMethodManager.SHOW_IMPLICIT);
		
		//Add onclicklistener for datePicker
		editStartDate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
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

		//Setup Date Picker variables
		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		Calendar cal = new GregorianCalendar(year, month, day);
		sMilli = cal.getTimeInMillis();
		eMilli = sMilli + 7*DAY;
		
		// Init the input fields.
		if(this.editIndex >= 0)
		{
			this.initInputFields();
		}
	} 
	
	/**
	 * Set the input values using the selected object.
	 */
	public void initInputFields()
	{
		Medication editMed = new Medication();
		
		// Sandys suggestion to use if statements insead of try/catch.
		if(this.editIndex >= 0 && this.editIndex < this.meds.size())
		{
			editMed = this.meds.get(editIndex);
			
			// Set the input field default values.
			this.editMedicineName.setText(editMed.getName());
			this.editDisplayName.setText(editMed.getDisplayName());
			this.editDescription.setText(editMed.getDescription());
			
			// Look for the proper medication type. (Not the best way to do.
			int selectionIndex = 0;
			
			for (int i=0; i < this.spinnerMedicationType.getCount(); i++)
			{
				if (this.spinnerMedicationType.getItemAtPosition(i).toString().equalsIgnoreCase(editMed.getType()))
				{
					selectionIndex = i;
					break;
				}
			}
			  
			this.spinnerMedicationType.setSelection(selectionIndex);
			
			try {
				String startDateFormatted = new java.text.SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date (editMed.getStartTime()));
				String endDateFormatted   = new java.text.SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date (editMed.getEndTime()));
				this.editStartDate.setText(startDateFormatted);
				this.editEndDate.setText(endDateFormatted);
				
				this.actualStartDate = editMed.getStartTime();
				this.actualEndDate = editMed.getEndTime();
			}
			catch(Exception e)
			{
				// Problems with date format.
			}
			
			this.editRemaining.setText(""+editMed.getRemaining());
			this.editRepeatPeriod.setText(""+editMed.getRepeatPeriod());
			
			
			// Set the frequency.
			ArrayList <Frequency> frequencies = editMed.getFrequency();
			
			for(Frequency f : frequencies)
			{
				View freqTbl = this.findViewById(R.id.frequencyTable);
				
				this.addExistingFrequency(f, freqTbl);
			}
			
			// If there are no pre-defined frequencies, add an empty field.
			if(frequencies.isEmpty())
			{
				addFrequency(this.findViewById(R.id.frequencyTable));
			}
		}
		else
		{
			finish();
		}
	}
	
	/**
	 * Called when the date picker selects a date
	 */
	@SuppressWarnings("deprecation")
	public void onDateEntrySelected(boolean startDate, long defaultDate) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(defaultDate);
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);
		showDialog(DATE_DIALOG_ID);

	}

	/**
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreateDialog(int)
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			return new DatePickerDialog(this, datePickerListener, year, month, day);

		}
		return null;
	}

	//Add on date set listener to Date Picker Dialog.
	private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

		// when dialog box is closed, below method will be called.
		public void onDateSet(DatePicker view, int selectedYear,
				int selectedMonth, int selectedDay) {

			year = selectedYear;
			month = selectedMonth;
			day = selectedDay;

			//Set format for date to be obtained in.
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

			Calendar c = new GregorianCalendar(year, month, day);

			//Obtain date in text and milliseconds.
			String date = formatter.format(c.getTime());
			if (dateStartPressed)
			{
				editStartDate.setText(date);
				sMilli = c.getTimeInMillis();
				actualStartDate = c.getTimeInMillis();
			}
			else
			{
				editEndDate.setText(date);
				sMilli = c.getTimeInMillis();
				actualEndDate = c.getTimeInMillis();
			}
		}
	};	

	/**
	 * Called when User presses add Frequency button.
	 * Adds a new row of options for user to fill out.
	 * @param view
	 */
	@SuppressLint("InflateParams")
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

				TimePickerDialog tpd = new TimePickerDialog(MedicationEditActivity.this, //same Activity Context like before
						new TimePickerDialog.OnTimeSetListener() {

					//Fires the Time Picker Dialog.
					@Override
					public void onTimeSet(TimePicker view, int hour,
							int minute) {
					
						String finalHour = String.valueOf(hour);
						
						//Check length of Hour is 2 chars.
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
	
	public void addExistingFrequency(Frequency freq, View view)
	{
		TableRow inputRow = (TableRow) LayoutInflater.from(getApplicationContext()).inflate(R.layout.frequency_table_row, null);
		
		// Set the existing values.
		((EditText)inputRow.findViewById(R.id.timeTextBox)).setText(freq.getTime());
		((EditText)inputRow.findViewById(R.id.dosageTextBox)).setText(freq.getDosage());
		((EditText)inputRow.findViewById(R.id.unitsTextBox)).setText(""+freq.getUnits());
		
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
				
				editTime.setText("SAA");
				
				// Process to get Current Time
				final Calendar c = Calendar.getInstance();
				int mHour = c.get(Calendar.HOUR_OF_DAY);
				int mMinute = c.get(Calendar.MINUTE);

				TimePickerDialog tpd = new TimePickerDialog(MedicationEditActivity.this, //same Activity Context like before
						new TimePickerDialog.OnTimeSetListener() {

					//Fires the Time Picker Dialog.
					@Override
					public void onTimeSet(TimePicker view, int hour,
							int minute) {
					
						String finalHour = String.valueOf(hour);
						
						//Check length of Hour is 2 chars.
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
	public void addInput(View view) throws IOException
	{	
		// To be sure that there are no errors in the input data.
		boolean dataValid = true;
		
		//Extract data from UI components.
		String medicineName = editMedicineName.getText().toString();
		String displayName = editDisplayName.getText().toString();
		String description = editDescription.getText().toString();
		String type = (String) spinnerMedicationType.getSelectedItem();
		String remaining = editRemaining.getText().toString();
		String repeatPeriod = editRepeatPeriod.getText().toString();

		List<Integer> listTime = new ArrayList<Integer>();
		List<String> listDosage = new ArrayList<String>();
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
			
			listDosage.add(frequencyDosage.getText().toString());
			
			// Make sure that the app does not crash on empty / invalid input for int fields.
			try{
				//Add cell data to Lists (parse to int)
				listTime.add(Integer.valueOf(frequencyTime.getText().toString()));
				listUnit.add(Integer.valueOf(frequencyUnit.getText().toString()));
			}
			catch(Exception e)
			{
				dataValid = false;
			}
		}

		//Store in MedicationStore
		ArrayList<Frequency> freq = new ArrayList<Frequency>();
		Medication med = new Medication();
		med.setName(medicineName);
		med.setDisplayName(displayName);
		med.setDescription(description);
		med.setType(type);
		med.setStartTime(actualStartDate);
		med.setEndTime(actualEndDate);

		// Make sure that the app does not crash on empty / invalid input for int fields.
		try {
			med.setRemaining(Integer.valueOf(remaining));
			med.setRepeatPeriod(Integer.valueOf(repeatPeriod));
			med.setIndex(size);

			for(int i =0;i<listTime.size();i++){
				Frequency freq2 = new Frequency();
	
				freq2.setTime(String.valueOf(listTime.get(i)));
				freq2.setDosage(String.valueOf(listDosage.get(i)));
				freq2.setUnits(listUnit.get(i));
	
				freq.add(freq2);
			}
		}
		catch(Exception e)
		{
			// In case of a number format or any other exception, don't add this record.
			dataValid = false;
		}

		
		med.setFrequency(freq);
		
		if(dataValid)
		{
			// There were no problems with integer input data, remove the old element and save data.
			this.meds.remove(this.editIndex);
			
			meds.add(med);
			Intent intent = new Intent("Med-Edited");
			intent.putParcelableArrayListExtra("allMeds", meds);
			LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
			
			//TODO check this still works
			setResult(100, intent);
			
			finish();
		}
		else
		{
			// The data was not saved, warn the user.
			Toast.makeText(getApplicationContext(), "The input is not valid!", Toast.LENGTH_LONG).show();
		}

		//Attach alarm to new medication.
		//Alarms alarm = new Alarms(getApplicationContext());
		//alarm.addAlarm(size);
	}

	/**
	 * Called when User presses Cancel button.
	 * Returns user back to Main Activity.
	 * 
	 * @param view
	 */
	public void cancelInput(View view)
	{
		Toast.makeText(getApplicationContext(), "Changes Not Saved.", Toast.LENGTH_LONG).show();
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

		headerTitle.setTextSize(20);
		headerTitle.setText("Time Taken"); 
		headerTitle.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
		headerTitle.setGravity(Gravity.CENTER);

		headerRow.addView(headerTitle);

		//Dosage
		TextView headerDosage = new TextView(this);

		headerDosage.setTextSize(20);
		headerDosage.setText("Dosage"); 
		headerDosage.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
		headerDosage.setGravity(Gravity.CENTER);

		headerRow.addView(headerDosage);	

		//Units
		TextView headerUnits = new TextView(this);

		headerUnits.setTextSize(20);
		headerUnits.setText("Servings"); 
		headerUnits.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
		headerUnits.setGravity(Gravity.CENTER);

		headerRow.addView(headerUnits);

		frequencyTable.addView(headerRow, new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	}

}
