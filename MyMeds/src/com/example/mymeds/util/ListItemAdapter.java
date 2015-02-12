package com.example.mymeds.util;

import java.util.ArrayList;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mymeds.R;

public class ListItemAdapter extends BaseAdapter {

	Context mContext;
	ArrayList<Medication> data = null;
	int count=0;
	TableLayout tempTable;

	public ListItemAdapter(Context mContext,int layoutID, ArrayList<Medication> medData) {

		super();

		this.mContext = mContext;
		this.data = medData;
		this.count=medData.size();
	}


	public View setFirstView(int position, View root, TableLayout table){

		tempTable = table;
		final TableRow row = (TableRow) LayoutInflater.from(mContext).inflate(R.layout.table_row, null);

		final Medication toAdd = data.get(position);

		System.out.println("pill no: "+position + " no of times to take: " + toAdd.frequency.size());

		TextView t1 = (TextView) row.findViewById(R.id.name);
		t1.setText(data.get(position).getDisplayName());

		TextView t2 = (TextView) row.findViewById(R.id.time);
		t2.setText(data.get(position).getTime());

		row.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(mContext, toAdd.getDisplayName(), Toast.LENGTH_LONG).show();
			}
		});

		Switch taken = (Switch) row.findViewById(R.id.pillTaken);	
		taken.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					if(toAdd.getFrequency().size()>1){
						tempTable.removeView(row);
						row.setPadding(5, 20, 5, 20);
						tempTable.addView(row, new TableLayout.LayoutParams(
								LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
					}
					else{
						Toast.makeText(mContext, toAdd.getDisplayName()+ " taken" , Toast.LENGTH_SHORT).show();
						tempTable.removeView(row);
					}
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

		TextView t2 = (TextView) row.findViewById(R.id.ammountLeft);
		t2.setText(String.valueOf(data.get(position).getRemaining()));

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
		return data.get(position).getMedId();
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return null;
	}

}