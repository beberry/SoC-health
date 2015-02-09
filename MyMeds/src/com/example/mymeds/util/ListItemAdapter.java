package com.example.mymeds.util;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.mymeds.R;

public class ListItemAdapter extends BaseAdapter {

	Context mContext;
	ArrayList<Medication> data = null;
	int count=0;

	public ListItemAdapter(Context mContext,ArrayList<Medication> medData) {

		super();

		this.mContext = mContext;
		this.data = medData;
		this.count=medData.size();
		
		System.out.println(data.toString());
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View list;
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		if(convertView==null){

			list = new View(mContext);
			list = inflater.inflate(R.layout.list_item, null);

			// object item based on the position
			Medication objectItem = data.get(position);

			TextView textViewItemName = (TextView) list.findViewById(R.id.name);
			textViewItemName.setText(objectItem.getMedName());
			
			TextView textViewItemDisplayName = (TextView) list.findViewById(R.id.displayName);
			textViewItemDisplayName.setText(objectItem.getDisplayName());
		}
		else{
			list = (View) convertView;
		}

		return list;

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

}