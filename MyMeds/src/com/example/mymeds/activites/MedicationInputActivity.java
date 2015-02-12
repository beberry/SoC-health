package com.example.mymeds.activites;

import com.example.mymeds.R;

import android.app.Activity;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
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
	} 
	
	/**
	 * Called when User presses add Frequency button.
	 * Adds a new row of options for user to fill out.
	 * @param view
	 */
	public void addFrequency(View view)
	{
		TableRow inputRow = (TableRow) LayoutInflater.from(getApplicationContext()).inflate(R.layout.frequency_table_row, null);
		
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

	private void addTableHeader()
	{
		//Header row
		TableRow headerRow = new TableRow(this);
		headerRow.setPadding(40, 50, 10, 5);
		headerRow.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		
		//Title
		TextView headerTitle = new TextView(this);
		
		headerTitle.setTextSize(15);
		headerTitle.setText("Time"); 
		headerTitle.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
		
		headerRow.addView(headerTitle);
		
		
		//Dosage
		TextView headerDosage = new TextView(this);
		
		headerDosage.setTextSize(15);
		headerDosage.setText("Dosage"); 
		headerDosage.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
		
		headerRow.addView(headerDosage);	
		
		
		//Units
		TextView headerUnits = new TextView(this);
		
		headerUnits.setTextSize(15);
		headerUnits.setText("Units"); 
		headerUnits.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
		
		headerRow.addView(headerUnits);
		
		
		frequencyTable.addView(headerRow, new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	}
}
