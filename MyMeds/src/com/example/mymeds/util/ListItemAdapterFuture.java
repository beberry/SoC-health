package com.example.mymeds.util;

import java.util.ArrayList;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.mymeds.R;

//used to display future medication table
public class ListItemAdapterFuture extends BaseAdapter {

	
	Context mContext;
	
	//ArrayList to store all medication found between the two dates
	ArrayList<futureMedDetails> data = null;
		
	//Constructor
	public ListItemAdapterFuture(Context mContext,int layoutID, ArrayList<futureMedDetails> medData) {

		super();

		this.mContext = mContext;
		//Store information to be displayed
		this.data = medData;
	}


	//displays each row in table of future medication
	public View setFutureRow(int position, View root, TableLayout table){

		//Create the new row
		TableRow row = (TableRow) LayoutInflater.from(mContext).inflate(R.layout.future_table_row, null);

		//Populate each column in the new row
		
		//second row to the real name of the medication
		TextView t1 = (TextView) row.findViewById(R.id.realName);
		t1.setText(data.get(position).getMedName());
		
		//first row set to the display/nickname of the medication
		TextView t2 = (TextView) row.findViewById(R.id.name);
		t2.setText(data.get(position).getDisplayName());

		//third row set to the amount needed of the medicastion
		TextView t3 = (TextView) row.findViewById(R.id.amount);
		t3.setText(Integer.toString(data.get(position).getAmountNeeded()));
		
		//set the size of the text depending on user settings
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
		table.addView(row, new TableLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

		return root;
	}


	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return null;
	}


}
