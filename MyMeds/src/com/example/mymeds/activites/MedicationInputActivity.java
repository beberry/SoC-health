package com.example.mymeds.activites;

import java.util.ArrayList;
import java.util.List;

import com.example.mymeds.R;

import android.app.Activity;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TableRow.LayoutParams;

public class MedicationInputActivity extends Activity{

	public TableLayout frequencyTable;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_medication_input);
		
		//Obtain frequencyTable from the layout_medication_input.xml 
		frequencyTable = (TableLayout)findViewById(R.id.frequencyTable);
		addTableHeader();
		addFrequency(this.findViewById(R.id.frequencyTable));
				
	} 
	
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
		
		removeButton.setOnClickListener(new OnClickListener() {

		     @Override
		      public void onClick(View pos) {
		    	 buttonRemoveFrequency(pos);
		      }
		      });

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
		//Find UI components.
		EditText editMedicineName = (EditText) findViewById(R.id.editMedicineName);
		EditText editDisplayName = (EditText) findViewById(R.id.editDisplayName);
		EditText editDescription = (EditText) findViewById(R.id.editDescription);
		Spinner spinnerMedicationType = (Spinner) findViewById(R.id.spinnerMedicationType);
		EditText editStartDate = (EditText) findViewById(R.id.editStartDate);
		EditText editEndDate = (EditText) findViewById(R.id.editEndDate);
		EditText editRemaining = (EditText) findViewById(R.id.editRemaining);
		EditText editRepeatPeriod = (EditText) findViewById(R.id.editRepeatPeriod);
		
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
			frequencyTime = (EditText) tableRow.getChildAt(0);
			frequencyDosage = (EditText) tableRow.getChildAt(1);
			frequencyUnit = (EditText) tableRow.getChildAt(2);
			
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
		
		//Calculate StartTime and EndTime | Parse '/' out from Dates
		//TODO
		
		//Export to AllMed.json
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
