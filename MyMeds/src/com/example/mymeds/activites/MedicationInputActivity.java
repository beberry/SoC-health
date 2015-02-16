package com.example.mymeds.activites;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Scanner;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.mymeds.R;
import com.example.mymeds.libs.PojoMapper;
import com.example.mymeds.stores.MedicationStore;
import com.example.mymeds.util.Frequency;
import com.example.mymeds.util.Medication;

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
	int size=0;
	ArrayList<Medication> meds;

	final int DAY = 86400000;
	static final int DATE_DIALOG_ID = 999;
	static final int TIME_DIALOG_ID = 100;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_medication_input);
		Intent intent = getIntent();
		size = intent.getIntExtra("size", 1);
		meds = getIntent().getParcelableArrayListExtra("meds");

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
	public void addInput(View view) throws IOException
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
			Log.d("Problem Determination", "List Dosage: " + listDosage.get(0));
			Log.d("Problem Determination", "List Unit: " + listUnit.get(0));

		}

		//Calculate StartTime and EndTime | Parse '/' out from Dates, append the Time.
		//TODO
		long startTime = 0;
		long endTime = 0;

		String parsedStartDate = startDate.replaceAll("/", "");
		String parsedEndDate = endDate.replaceAll("/", "");

		Log.d("PD", "parsing StartDate");
		startTime = Long.parseLong(parsedStartDate) + Long.parseLong(listTime.get(0).toString());
		endTime = Long.parseLong(parsedEndDate) + Long.parseLong(listTime.get(0).toString());
		Log.d("PD", "long startTime: " + startTime);

		//Store in MedicationStore
		ArrayList<Frequency> freq = new ArrayList<Frequency>();
		Medication med = new Medication();
		med.setName(medicineName);
		med.setDisplayName(displayName);
		med.setDescription(description);
		med.setType(type);
		med.setStartTime(startTime);
		med.setEndTime(endTime);
		med.setRemaining(Integer.valueOf(remaining));
		med.setRepeatPeriod(Integer.valueOf(repeatPeriod));
		med.setIndex(size);

		for(int i =0;i<listTime.size();i++){
			Frequency freq2 = new Frequency();

			freq2.setTime(listTime.get(i));
			freq2.setDosage(String.valueOf(listDosage.get(i)));
			freq2.setUnits(listUnit.get(i));

			freq.add(freq2);
		}

		med.setFrequency(freq);

		Log.i("Add medication",med.toString());
		String new_med_json = PojoMapper.toJson(med, true);
		String med_json = getMedsJSON();
		System.out.println("MED JSON "+med_json);
		String first_part = med_json.substring(0, med_json.lastIndexOf("]"));
		System.out.println("THE FIRST PART IS " +first_part);
		first_part += ","+ new_med_json;
		String json_end = med_json.substring(med_json.lastIndexOf(']'), med_json.length()); 
		String final_json = first_part + json_end;
		System.out.println("FINAL PART IS "+final_json);
		meds.add(med);
		//		MedicationStore medStore = new MedicationStore();
		//		
		//		medStore.setMedicineName(medicineName);
		//		medStore.setDisplayName(displayName);
		//		medStore.setDescription(description);
		//		medStore.setType(type);
		//		medStore.setStartTime(startTime);
		//		medStore.setEndTime(endTime);
		//		medStore.setRemaining(remaining);
		//		medStore.setRepeatPeriod(repeatPeriod);
		//		medStore.setListTime(listTime);
		//		medStore.setListDosage(listDosage);
		//		medStore.setListUnit(listUnit);
		//		
		//writeToJSON(PojoMapper.toJson(medStore, true));
		//writeFile("add.json", medStore);

		writeToFile(PojoMapper.toJson(meds, true));

		Intent intent = new Intent();
		intent.putParcelableArrayListExtra("meddata", meds);
		setResult(100, intent);
		finish();
	}
	
	private String getMedsNew()
	{
	   String filename = "meddata.json";
	   String content = null;
	   File file = new File(filename); //for ex foo.txt
	   try {
	       FileReader reader = new FileReader(file);
	       char[] chars = new char[(int) file.length()];
	       reader.read(chars);
	       content = new String(chars);
	       reader.close();
	   } catch (IOException e) {
	       e.printStackTrace();
	   }
	   return content;
	}
	
	private String getMedsJSON()
	{
		String filename = "meddata.json";
		StringBuffer json = new StringBuffer("");



		try {
			File filesDir = getFilesDir();
			Scanner input = new Scanner(new File(filesDir, filename));
			while(input.hasNext()){
				json.append(input.next());
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return json.toString();
	}

	private void writeToFile(String med)
	{
		String filename = "meddata.json";
		StringBuffer json = new StringBuffer("");



		try {
			File filesDir = getFilesDir();
			Scanner input = new Scanner(new File(filesDir, filename));
			while(input.hasNext()){
				json.append(input.next());
			}
			
			FileOutputStream fos = new FileOutputStream(new File(getFilesDir()+"//"+filename), true);

			System.out.println(med);
			String temp = "{ \"medication\": ";
			String temp2 = "}";
			med = temp+med+temp2;
			fos = openFileOutput(filename, Context.MODE_PRIVATE);

			fos.write(med.getBytes());

			fos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void writeToJSON(String json)
	{
		String filename = "meddata.json";
		FileOutputStream outputStream;

		if(json != "") //If JSON is not null
		{
			try 
			{
				outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
				outputStream.write(json.getBytes());
				outputStream.close();

				Toast.makeText(getApplicationContext(), "Medication Added", Toast.LENGTH_LONG).show();
			} catch (Exception e) {
				e.printStackTrace();
			}	
		}
	}

	public void writeFile(String filename, MedicationStore medStore) {
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		String json = new String();

		try {
			json = ow.writeValueAsString(medStore);
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("JSON Error", "Error occurred when parsing object into JSON");
		}

		if (json != "") {
			try {
				FileOutputStream fos = openFileOutput(filename, Context.MODE_PRIVATE);
				fos.write(json.getBytes());
				fos.close();
				Toast.makeText(getApplicationContext(), "Medication Added", Toast.LENGTH_LONG).show();
			}
			catch (FileNotFoundException fnfe) {
				Log.i("FileNotFound", "File could not be located");
			}
			catch (IOException ioe) {
				Log.e("FileWrite", "An IO Exception occured when writing file");
			}
		}
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

	protected boolean createFisle(){
		File directory = new File(Environment.getExternalStorageDirectory().getPath()+"//SudoShip//");
		directory.mkdirs();
		File file = new File(directory, "winConditions.json" );

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
			}
			catch(Exception e){
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
}
