package com.example.mymeds.util;

import java.util.ArrayList;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.mymeds.R;

public class ListItemAdapter extends BaseAdapter {

	Context mContext;
	ArrayList<Medication> data = null;
	int count=0;

	public ListItemAdapter(Context mContext,int layoutID, ArrayList<Medication> medData) {

		super();

		this.mContext = mContext;
		this.data = medData;
		this.count=medData.size();
	}


	public View setFirstView(int position, View root, TableLayout table){

		TableRow row = (TableRow) LayoutInflater.from(mContext).inflate(R.layout.table_row, null);

		TextView t1 = (TextView) row.findViewById(R.id.name);
		t1.setText(data.get(position).getDisplayName());

		TextView t2 = (TextView) row.findViewById(R.id.time);
		t2.setText(data.get(position).getTime());

		row.setPadding(5, 20, 5, 20);
		table.addView(row, new TableLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

		return root;
	}

	public View setSecondView(int position, View root, TableLayout table){

		TableRow row = (TableRow) LayoutInflater.from(mContext).inflate(R.layout.allmeds_table_row, null);

		TextView t1 = (TextView) row.findViewById(R.id.name);
		t1.setText(data.get(position).getDisplayName());
		
		//TextView t1 = (TextView) row.findViewById(R.id.dosage);
		//t1.setText(data.get(position).getD);

		TextView t2 = (TextView) row.findViewById(R.id.ammountLeft);
		t2.setText(String.valueOf(data.get(position).getRemaining()));

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