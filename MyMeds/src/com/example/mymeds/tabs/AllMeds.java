package com.example.mymeds.tabs;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TableLayout;

import com.example.mymeds.R;
import com.example.mymeds.util.ListItemAdapter;
import com.example.mymeds.util.Medication;

public class AllMeds extends Activity {
	Context mContext;
	ListItemAdapter adapter;
	ArrayList<Medication> allmeds = new ArrayList<Medication>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getApplicationContext();
		setContentView(R.layout.tab_second);
		allmeds = getIntent().getParcelableArrayListExtra("meds");
		TableLayout listViewItems = (TableLayout) findViewById(R.id.listview);

		// our adapter instance
		adapter = new ListItemAdapter(mContext, R.id.listview, allmeds);

		for(int i=0;i<allmeds.size();i++){
			adapter.setSecondView(i,this.findViewById(R.layout.tab_second), listViewItems);
		}
		listViewItems.requestLayout();

	}


}