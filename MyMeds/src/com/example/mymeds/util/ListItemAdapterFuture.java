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

public class ListItemAdapterFuture extends BaseAdapter {

	Context mContext;
	ArrayList<futureMedDetails> data = null;
	int count=0;

	public ListItemAdapterFuture(Context mContext,int layoutID, ArrayList<futureMedDetails> medData) {

		super();

		this.mContext = mContext;
		this.data = medData;
		this.count=medData.size();
	}


	public View setFirstView(int position, View root, TableLayout table){

		TableRow row = (TableRow) LayoutInflater.from(mContext).inflate(R.layout.future_table_row, null);

		TextView t1 = (TextView) row.findViewById(R.id.name);
		t1.setText(data.get(position).getDisplayName());

		TextView t2 = (TextView) row.findViewById(R.id.realName);
		t2.setText(data.get(position).getMedName());
		
		TextView t3 = (TextView) row.findViewById(R.id.amount);
		t3.setText(Integer.toString(data.get(position).getAmountNeeded()));

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
	public futureMedDetails getItem(int position) {
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
