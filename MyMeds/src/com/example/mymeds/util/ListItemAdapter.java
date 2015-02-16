package com.example.mymeds.util;

import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
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
	ArrayList<Medication> data = null;
	int count=0;
	TableLayout tempTable;
	int timesTaken=0;

	public ListItemAdapter(Context mContext,int layoutID, ArrayList<Medication> medData) {

		super();

		this.mContext = mContext;
		this.data = medData;
		this.count=medData.size();
	}


	public View setFirstView(final int position, View root, TableLayout table){
		tempTable = table;
		final TableRow row = (TableRow) LayoutInflater.from(mContext).inflate(R.layout.table_row, null);

		final Medication toAdd = data.get(position);

		final TextView t1 = (TextView) row.findViewById(R.id.name);
		t1.setText(data.get(position).getDisplayName());

		final TextView t2 = (TextView) row.findViewById(R.id.time);


		String takeTime = String.valueOf(data.get(position).getFrequency().get(timesTaken).getTime());

		String h = takeTime.substring( 0,2);
		String m = takeTime.substring( 2,takeTime.length());

		t2.setText( h+ ":" + m);

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

		row.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(mContext, toAdd.getDisplayName(), Toast.LENGTH_LONG).show();
			}
		});

		final Button taken = (Button) row.findViewById(R.id.pillTaken);	
		taken.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(mContext, toAdd.getDisplayName()+ " taken" , Toast.LENGTH_SHORT).show();
				tempTable.removeView(row);
				if(toAdd.getFrequency().size()>1){
					timesTaken = timesTaken++;
					
					String takeTime = (String.valueOf(data.get(position).getFrequency().get(timesTaken).getTime()));

					String h = takeTime.substring( 0,2);
					String m = takeTime.substring( 2,takeTime.length());

					t2.setText( h+ ":" + m);
					
					row.setPadding(5, 20, 5, 20);
					tempTable.addView(row, new TableLayout.LayoutParams(
							LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
					toAdd.getFrequency().remove((Frequency) data.get(position).getFrequency().get(timesTaken));
				}

			}
		});
		row.setPadding(5, 20, 5, 20);
		tempTable.addView(row, new TableLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

		return root;
	}

	public View setSecondView(int position, View root, TableLayout table){

		TableRow row = (TableRow) LayoutInflater.from(mContext).inflate(R.layout.allmeds_table_row, null);

		final Medication toAdd = data.get(position);

		TextView t1 = (TextView) row.findViewById(R.id.name);
		t1.setText(data.get(position).getDisplayName());

		//TextView t3 = (TextView) row.findViewById(R.id.dosage);
		//		t3.setText(String.valueOf(data.get(position).getFrequency().get(0).getUnits()) + "x" + data.get(position).getFrequency().get(0).getDosage());

		TextView t2 = (TextView) row.findViewById(R.id.ammountLeft);
		t2.setText(String.valueOf(data.get(position).getRemaining()));

		TextView t4 = (TextView) row.findViewById(R.id.status);

		SharedPreferences prefs = mContext.getSharedPreferences(
				"com.example.mymeds", Context.MODE_PRIVATE);
		if(prefs.getInt("textSize", -1) == 1)
		{
			t1.setTextAppearance(mContext, R.style.textLarge);
			t2.setTextAppearance(mContext, R.style.textLarge);
			//t3.setTextAppearance(mContext, R.style.textLarge);
			t4.setTextAppearance(mContext, R.style.textLarge);
		}
		else
		{
			t1.setTextAppearance(mContext, R.style.textNormal);
			t2.setTextAppearance(mContext, R.style.textNormal);
			//t3.setTextAppearance(mContext, R.style.textNormal);
			t4.setTextAppearance(mContext, R.style.textNormal);
		}
		if(data.get(position).getRemaining()>=25){
			t4.setBackgroundColor(Color.GREEN);
		}else{
			t4.setBackgroundColor(Color.RED);
		}

		row.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(mContext, toAdd.getDisplayName(), Toast.LENGTH_LONG).show();
				return;
			}
		});

		row.setPadding(5, 20, 5, 20);
		table.addView(row, new TableLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

		return root;
	}

	@Override
	public int getCount() {
		return count;
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