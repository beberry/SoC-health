package com.example.mymeds.tabs;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TextView;

import com.example.mymeds.R;
import com.example.mymeds.util.ListItemAdapter;
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
		today  = new TodaysMeds();
		mContext = this;

		meds = getIntent().getParcelableArrayListExtra("meds");

		TableLayout listViewItems = (TableLayout) findViewById(R.id.listview);

		// our adapter instance
		adapter = new ListItemAdapter(mContext, R.id.listview, meds);

		SharedPreferences prefs = mContext.getSharedPreferences(
			      "com.example.mymeds", Context.MODE_PRIVATE);
		TextView t1 = (TextView)findViewById(R.id.headerName);
		TextView t2 = (TextView)findViewById(R.id.headerTaken);
		TextView t3 = (TextView)findViewById(R.id.headerTime);
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
			t3.setTextAppearance(mContext, R.style.textNormal);
		}
		
		for(int i=0;i<meds.size();i++){
			adapter.setFirstView(i, this.findViewById(R.layout.tab_first), listViewItems);
		}
		listViewItems.requestLayout();
	}
}