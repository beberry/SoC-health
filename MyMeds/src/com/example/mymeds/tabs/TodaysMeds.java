package com.example.mymeds.tabs;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.TableLayout;

import com.example.mymeds.R;
import com.example.mymeds.util.ListItemAdapter;
import com.example.mymeds.util.Medication;

public class TodaysMeds extends Activity {
	Context mContext;
	ListItemAdapter adapter;
	ArrayList<Medication> meds = new ArrayList<Medication>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab_first);

		mContext = this;

		meds = getIntent().getParcelableArrayListExtra("meds");

		TableLayout listViewItems = (TableLayout) findViewById(R.id.listview);

		// our adapter instance
		adapter = new ListItemAdapter(mContext, R.id.listview, meds);

		for(int i=0;i<meds.size();i++){
			adapter.setFirstView(i, this.findViewById(R.layout.tab_first), listViewItems);
		}
		listViewItems.requestLayout();
	}

}