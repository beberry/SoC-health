package com.example.mymeds.tabs;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;

import com.example.mymeds.R;
import com.example.mymeds.activites.MedicationInputActivity;
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
		
		final Button addMeds = (Button) findViewById(R.id.add_medication);
		addMeds.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(AllMeds.this, MedicationInputActivity.class);
				intent.putExtra("size", allmeds.size()+1);
				intent.putParcelableArrayListExtra("meds", allmeds);
				startActivityForResult(intent, 100);
			}
		});

		// our adapter instance
		adapter = new ListItemAdapter(mContext, R.id.listview, allmeds);

		for(int i=0;i<allmeds.size();i++){
			adapter.setSecondView(i,this.findViewById(R.layout.tab_second), listViewItems);
		}

		SharedPreferences prefs = mContext.getSharedPreferences(
				"com.example.mymeds", Context.MODE_PRIVATE);
		TextView t1 = (TextView)findViewById(R.id.headerName);
		TextView t2 = (TextView)findViewById(R.id.headerTaken);
		//TextView t3 = (TextView)findViewById(R.id.headerTime);
		TextView t4 = (TextView)findViewById(R.id.headerStatus);
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
		listViewItems.requestLayout();

	}


}