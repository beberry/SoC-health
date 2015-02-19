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
import com.example.mymeds.stores.TodayMedStore;

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
	public View setFirstView(TodayMedStore tms, View root, TableLayout table){

		tempTable = table;
	
			//if the medicaiton has already been taken at that time for the day
			if(MedFetcher.toBeTakenToday(tms.getTakenTime()))
			{
				//create new row for table
				final TableRow row = (TableRow) LayoutInflater.from(mContext).inflate(R.layout.todaysmeds_table_row, null);

				//populate the first textView with medicaiton name
				final TextView t1 = (TextView) row.findViewById(R.id.name);
				t1.setText(tms.getMedName());


				//populate the second textView with the time the medication need to be taken	
				final TextView t2 = (TextView) row.findViewById(R.id.time);
	

				//populate the third tab with a button to say the medication has been taken
				String takeTime = String.valueOf(tms.getTime());

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

				
		final int posIndex = tms.getMedIndex();
		
		//stores the indes of the frequency object
		final int frqIndex = tms.getFreqIndex();	
				
		//populate the third column with a button to indicate if pill has been taken
		final Button taken = (Button) row.findViewById(R.id.pillTaken);	
		
		//on click listenter for when button is pressed
		taken.setOnClickListener(new OnClickListener() {
					
			@Override
			public void onClick(View v) {
			
				//Get user confirmation
				showDialog(posIndex, frqIndex);
			
			}
		});
		row.setPadding(5, 20, 5, 20);
		
		//add row to table
		tempTable.addView(row, new TableLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		
				
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
		TextView t1 = (TextView) row.findViewById(R.id.realName);
		t1.setText(data.get(position).getName());
		
		
		//display the Nickname
		TextView t2 = (TextView) row.findViewById(R.id.name);
		t2.setText(data.get(position).getDisplayName());
		
		//display the number of units left
		TextView t3 = (TextView) row.findViewById(R.id.ammountLeft);
		t3.setText(String.valueOf(data.get(position).getRemaining()));
		
		//flag to tell what colour the text should be
		boolean refill = true;
		
		//if there are more than 0 pills
		if(data.get(position).getRemaining() != 0)
		{
			//caculate if a refill is needed
			refill = calcRefill(data.get(position).getFrequency(), data.get(position).getRepeatPeriod(), data.get(position).getRemaining());	
		}
		
		//if refill is needed disply amount left in red, if not green
		if(refill){
				t3.setTextColor(Color.RED);
			}		
		else{
				t3.setTextColor(Color.GREEN);
			}
				
		
		
		SharedPreferences prefs = mContext.getSharedPreferences(
				"com.example.mymeds", Context.MODE_PRIVATE);
		if(prefs.getInt("textSize", -1) == 1)
		{
			t1.setTextAppearance(mContext, R.style.textLarge);
			t2.setTextAppearance(mContext, R.style.textLarge);
			t3.setTextAppearance(mContext, R.style.textLarge);
		}
		else
		{
			t1.setTextAppearance(mContext, R.style.textNormal);
			t2.setTextAppearance(mContext, R.style.textNormal);
			t3.setTextAppearance(mContext, R.style.textLarge);
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

	/**
	 * Method to convert date in separate integers into milliseconds
	 * @param frequenct The details of each time the medication is taken
	 * @param repeat The number of days between taking the medication 
	 * @param amountLeft How many units are left
	 * @return boolean Indicates if the user will require a refill soon
	 */
	private boolean calcRefill(ArrayList<Frequency> frequency, int repeat, int amountLeft)
		{
			//stores how many units needed in a day
			int needOneDay = 0;
			
			//add up all the units needed for a day
			for( int i = 0; i < frequency.size(); i++){
			
				needOneDay += frequency.get(i).getUnits();
			}
			
			//caculate how many days left the user has of the medication
			int daysLeft = (amountLeft / needOneDay) * repeat;
			
			//if less than 2 weeks then indicate refill needed
			if(daysLeft < 14){			
				return true;
			}
			else{
				return false;
			}
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
	private void showDialog(int position, int index)
	{
		final int p = position;
		final int ind = index;
		
		//Reset isPillTaken value
		isPillTaken = false;
		
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        switch (which){
		        case DialogInterface.BUTTON_POSITIVE:
		            //Yes button clicked broadcast medication has been taken
					Intent intentP = new Intent("Med-Taken");
					//pass medication and frequency indexs
					intentP.putExtra("position", p);
					intentP.putExtra("timesTaken", ind);
					intentP.putExtra("actualyTaken", true);
					LocalBroadcastManager.getInstance(mContext).sendBroadcast(intentP);		
		            break;

		        case DialogInterface.BUTTON_NEUTRAL:
		        	Intent intentN = new Intent("Med-Taken");
					intentN.putExtra("position", p);
					intentN.putExtra("timesTaken", ind);
					intentN.putExtra("actualyTaken", false);
					LocalBroadcastManager.getInstance(mContext).sendBroadcast(intentN);	
		            break;
		        }
		    }
		};

		//options for dialogue box
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setMessage("What action are you taking?")
	    	.setPositiveButton("Medication Taken", dialogClickListener)
	        .setNeutralButton("Meditcation Not taken", dialogClickListener)
		    .setNegativeButton("Cancel", dialogClickListener)
		    .show();
	}	
}