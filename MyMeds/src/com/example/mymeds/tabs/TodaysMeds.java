package com.example.mymeds.tabs;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.TableLayout;

import com.example.mymeds.R;
import com.example.mymeds.util.ListItemAdapter;
import com.example.mymeds.util.MedFetcher;
import com.example.mymeds.util.Medication;

public class TodaysMeds extends Activity {
	Context mContext;
	ListItemAdapter adapter;
	public ArrayList<Medication> meds = new ArrayList<Medication>();
	public TodaysMeds today;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab_first);
		mContext = this;

		meds = getIntent().getParcelableArrayListExtra("meds");
		System.out.println("before passing: "+meds.size());
		
		calculateMeds();
		
		System.out.println("after parsing: "+meds.size());

		TableLayout listViewItems = (TableLayout) findViewById(R.id.listview);

		// our adapter instance
		adapter = new ListItemAdapter(mContext, R.id.listview, meds);

		for(int i=0;i<meds.size();i++){
			adapter.setFirstView(i, this.findViewById(R.layout.tab_first), listViewItems);
		}
		listViewItems.requestLayout();
	}

	public void onResume(){
		super.onResume();

		meds = getIntent().getParcelableArrayListExtra("meds");
		System.out.println("before passing: "+meds.size());
		
		calculateMeds();
		
		System.out.println("after parsing: "+meds.size());

		TableLayout listViewItems = (TableLayout) findViewById(R.id.listview);
		listViewItems.removeAllViews();

		// our adapter instance
		adapter = new ListItemAdapter(mContext, R.id.listview, meds);

		for(int i=0;i<meds.size();i++){
			adapter.setFirstView(i, this.findViewById(R.layout.tab_first), listViewItems);
		}
		listViewItems.requestLayout();

	}

	public void calculateMeds(){
		MedFetcher medFetcher = new MedFetcher();
		medFetcher.loadAssets(mContext, meds);
		Calendar c = new GregorianCalendar();
		meds = medFetcher.daysMedication(c.getTime().getTime());
	}
}