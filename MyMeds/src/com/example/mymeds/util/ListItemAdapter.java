package com.example.mymeds.util;

import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
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

@SuppressLint("InflateParams")
public class ListItemAdapter extends BaseAdapter {

	Context mContext;
	
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
		mMedFetcher.loadAssets(mContext, medData);
	}


	//used to display information on the first tab (todays medicaiton)
	public View setFirstView(final int position, View root, TableLayout table){
		
		tempTable = table;
		
		//create new row for table
		final TableRow row = (TableRow) LayoutInflater.from(mContext).inflate(R.layout.table_row, null);

		//get the medication needing added
		final Medication toAdd = data.get(position);

		//populate the first textView with medicaiton name
		final TextView t1 = (TextView) row.findViewById(R.id.name);
		t1.setText(data.get(position).getDisplayName());

		
		//populate the second textView with the time the medication need to be taken	
		final TextView t2 = (TextView) row.findViewById(R.id.time);
		
		//populate the third tab with a button to say the medication has been taken
		String takeTime = String.valueOf(data.get(position).getFrequency().get(timesTaken).getTime());
		
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
	
		//populate the third column with a button to indicate if pill has been taken
		final Button taken = (Button) row.findViewById(R.id.pillTaken);	
		
		//on click listenter for when button is pressed
		taken.setOnClickListener(new OnClickListener() {
					
			@Override
			public void onClick(View v) {
				
				Toast.makeText(mContext, toAdd.getDisplayName()+" "+timesTaken  , Toast.LENGTH_SHORT).show();
				
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

					t2.setText( h+ ":" + m);
					
					row.setPadding(5, 20, 5, 20);
					
					//replace the old row with the new one
					tempTable.addView(row, new TableLayout.LayoutParams(
							LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
					toAdd.getFrequency().remove((Frequency) data.get(position).getFrequency().get(timesTaken));
					
					//Update the locally held todays meds, knock out the first frequency
					data.get(position).getFrequency().remove(0);
					JSONUtils.writeToFile(data, mContext, false);					
					//Modify and update the numbers of pills remaining for this pill
					mMedFetcher.modifyQuantity(toAdd.getIndex(), toAdd.getFrequency().get(timesTaken).getUnits());
					//Force refresh of data
					ArrayList<Medication> newData = JSONUtils.loadValues(JSONUtils.readFile(mContext, true), mContext);
					Log.d("sender", "Broadcasting message");
					Intent intent = new Intent("Med-Taken");
					intent.putParcelableArrayListExtra("medData", newData);
					LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
				}
				else{
					
				}
				
			}
		});
		row.setPadding(5, 20, 5, 20);
		
		//add row to table
		tempTable.addView(row, new TableLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

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

		//populate the first column with the name of the medication
				TextView t2 = (TextView) row.findViewById(R.id.realName);
				t2.setText(data.get(position).getName());
		
		
		//display the amount of medicaiton
		TextView t3 = (TextView) row.findViewById(R.id.ammountLeft);
		
		t3.setText(String.valueOf(data.get(position).getRemaining()));

		boolean refill = calcRefill(data.get(position).getFrequency(), data.get(position).getRepeatPeriod(), data.get(position).getRemaining());

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
		}
		else
		{
			t1.setTextAppearance(mContext, R.style.textNormal);
			t2.setTextAppearance(mContext, R.style.textNormal);
		}
				
		row.setPadding(5, 20, 5, 20);
		
		//add row to table
		table.addView(row, new TableLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

		return root;
	}

	
	
	private boolean calcRefill(ArrayList<Frequency> frequency, int repeat, int amountLeft)
	{
		
		int needOneDay = 0;
		
		for( int i = 0; i < frequency.size(); i++){
		
			needOneDay += frequency.get(i).getUnits();
		}
		
		int daysLeft = (amountLeft / needOneDay) * repeat;
		
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

}