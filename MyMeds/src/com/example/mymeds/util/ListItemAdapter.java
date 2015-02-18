package com.example.mymeds.util;

import java.sql.Date;
import java.util.ArrayList;

import java.util.Calendar;
import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import com.example.mymeds.R;
import com.example.mymeds.activites.MedicationEditActivity;

@SuppressLint("InflateParams")
public class ListItemAdapter extends BaseAdapter {

	Context mContext;
	boolean isPillTaken = false;
	
	//stores returned medication
	ArrayList<Medication> data = null;

	TableLayout tempTable;

	//counts how many time a medication has been taken
	int timesTaken;
	//create new instance of medFetcher
	MedFetcher mMedFetcher = new MedFetcher();

	//constructor
	public ListItemAdapter(Context mContext,int layoutID, ArrayList<Medication> medData) {

		super();

		//get context, the data t be displayed and load the assets of medFetcher
		this.mContext = mContext;
		this.data = medData;
	}


	//used to display information on the first tab (todays medicaiton)
	public View setFirstView(final int position, View root, TableLayout table){

		tempTable = table;

		for( int t = 0;t < data.get(position).getFrequency().size(); t++)
		{				
			if(MedFetcher.toBeTakenToday(data.get(position).getFrequency().get(t).getTaken()))
			{
				//create new row for table
				final TableRow row = (TableRow) LayoutInflater.from(mContext).inflate(R.layout.todaysmeds_table_row, null);

				//get the medication needing added
				final Medication toAdd = data.get(position);

				//populate the first textView with medicaiton name
				final TextView t1 = (TextView) row.findViewById(R.id.name);
				t1.setText(data.get(position).getDisplayName());


				//populate the second textView with the time the medication need to be taken	
				final TextView t2 = (TextView) row.findViewById(R.id.time);
	

				//populate the third tab with a button to say the medication has been taken
				String takeTime = String.valueOf(data.get(position).getFrequency().get(t).getTime());

				//split the time string and add colon 
				String h = takeTime.substring( 0,2);
				String m = takeTime.substring( 2,takeTime.length());

				t2.setText( h+ ":" + m);		
				
				//set text size dependent on user settings
				SharedPreferences prefs = mContext.getSharedPreferences(
						"com.example.mymeds", Context.MODE_PRIVATE);
				if(prefs.getInt("textSize", -1) == 1)
				{
					t1.setTextAppearance(mContext, R.style.textLarge);
					t2.setTextAppearance(mContext, R.style.textLarge);
				}
				else
				{
					t1.setTextAppearance(mContext, R.style.textNormal);
					t2.setTextAppearance(mContext, R.style.textNormal);
				}

		
	
		final int index = t;	
				
		//populate the third column with a button to indicate if pill has been taken
		final Button taken = (Button) row.findViewById(R.id.pillTaken);	
		
		//on click listenter for when button is pressed
		taken.setOnClickListener(new OnClickListener() {
					
			@Override
			public void onClick(View v) {
				
				//Get user confirmation
				showDialog();
				
				if(isPillTaken == true)
				{
					//remove the row the nutton belong to from the table
					tempTable.removeView(row);

					//if there are more instances of the medication being taken that day
					if(toAdd.getFrequency().size()>1){

						//incremnet to access the details of the next time drug is to be taken
						timesTaken = timesTaken++;

						//get time to take, split up add colon and display
						String takeTime = (String.valueOf(data.get(position).getFrequency().get(timesTaken).getTime()));

						String h = takeTime.substring( 0,2);
						String m = takeTime.substring( 2,takeTime.length());

						t2.setText( h + ":" + m);

						row.setPadding(5, 20, 5, 20);

						//replace the old row with the new one
						tempTable.addView(row, new TableLayout.LayoutParams(
								LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
						toAdd.getFrequency().remove((Frequency) data.get(position).getFrequency().get(timesTaken));

					}
					
					Intent intent = new Intent("Med-Taken");
					intent.putExtra("position", position);

					intent.putExtra("timesTaken", index);
					LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
				}
				
			}
		});
		row.setPadding(5, 20, 5, 20);
		
		//add row to table
		tempTable.addView(row, new TableLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		
				
			}
		}
		return root;
	}


	//used to display information on the second tab (all medicaiton)
	public View setSecondView(int position, View root, TableLayout table){

		//create row to e populated
		TableRow row = (TableRow) LayoutInflater.from(mContext).inflate(R.layout.allmeds_table_row, null);

		//get the details of the medicaiton to be added
		final Medication toAdd = data.get(position);

		//populate the first column with the name of the medication
		TextView t1 = (TextView) row.findViewById(R.id.name);
		t1.setText(data.get(position).getDisplayName());

		//display the amount of medicaiton
		TextView t2 = (TextView) row.findViewById(R.id.ammountLeft);
		t2.setText(String.valueOf(data.get(position).getRemaining()));

		SharedPreferences prefs = mContext.getSharedPreferences(
				"com.example.mymeds", Context.MODE_PRIVATE);
		if(prefs.getInt("textSize", -1) == 1)
		{
			t1.setTextAppearance(mContext, R.style.textLarge);
			t2.setTextAppearance(mContext, R.style.textLarge);
		}
		else
		{
			t1.setTextAppearance(mContext, R.style.textNormal);
			t2.setTextAppearance(mContext, R.style.textNormal);
		}


		//display status colour of stock level
		TextView t4 = (TextView) row.findViewById(R.id.status);

		//if more than 25 units green colour, if less red
		if(data.get(position).getRemaining()>=25){
			t4.setBackgroundColor(Color.GREEN);
		}else{
			t4.setBackgroundColor(Color.RED);
		}

		row.setPadding(5, 20, 5, 20);

		// Add a listener for the Edit functionality.
		final int editIndex = position;

		row.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(), MedicationEditActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

				intent.putExtra("size", data.size()+1);

				intent.putExtra("editIndex", editIndex);
				intent.putParcelableArrayListExtra("meds", data);
				mContext.startActivity(intent);
			}
		});

		//add row to table
		table.addView(row, new TableLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

		return root;
	}

	@Override
	public int getCount() {
		return 0;
	}

	@Override
	public Medication getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return data.get(position).getIndex();
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Dialog to confirm if a Pill is taken or not.
	 * Changes global var: isPillTaken, if true
	 */
	private void showDialog()
	{
		//Reset isPillTaken value
		isPillTaken = false;
		
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        switch (which){
		        case DialogInterface.BUTTON_POSITIVE:
		            //Yes button clicked
		        	isPillTaken = true;
		            break;

		        case DialogInterface.BUTTON_NEGATIVE:
		            //No button clicked
		        	isPillTaken = false;
		            break;
		        }
		    }
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setMessage("Are you sure medication has been taken?").setPositiveButton("Yes", dialogClickListener)
		    .setNegativeButton("No", dialogClickListener).show();
	}	
}