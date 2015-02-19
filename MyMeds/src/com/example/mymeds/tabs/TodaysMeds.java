package com.example.mymeds.tabs;

import java.util.ArrayList;
import java.util.Collections;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TextView;

import com.example.mymeds.R;
import com.example.mymeds.stores.TodayMedStore;
import com.example.mymeds.util.Frequency;
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
		TextView t1 = (TextView)findViewById(R.id.headerRName);
		TextView t2 = (TextView)findViewById(R.id.headerName);
		TextView t3 = (TextView)findViewById(R.id.headerTaken);
		TextView t4 = (TextView)findViewById(R.id.headerTime);
		if(prefs.getInt("textSize", -1) == 1)
		{
			t1.setTextAppearance(mContext, R.style.textLarge);
			t2.setTextAppearance(mContext, R.style.textLarge);
			t3.setTextAppearance(mContext, R.style.textLarge);
			t4.setTextAppearance(mContext, R.style.textLarge);
		}
		else
		{
			t1.setTextAppearance(mContext, R.style.textNormal);
			t2.setTextAppearance(mContext, R.style.textNormal);
			t3.setTextAppearance(mContext, R.style.textNormal);
			t4.setTextAppearance(mContext, R.style.textNormal);
		}

		ArrayList<TodayMedStore> tmsList = new ArrayList<TodayMedStore>();


		for(int i=0;i<meds.size();i++){						

			String name = meds.get(i).getName();
			String nickName = meds.get(i).getDisplayName();

			ArrayList<Frequency> freqs = meds.get(i).getFrequency(); 

			for(int j = 0; j< freqs.size(); j++){
				TodayMedStore tms = new TodayMedStore();

				tms.setMedIndex(i);
				tms.setFreqIndex(j);
				tms.setMedName(name);
				tms.setNickName(nickName);
				tms.setTime(freqs.get(j).getTime());
				tms.setTakenTime(freqs.get(j).getTaken());
				
				tmsList.add(tms);
			}
		}

		Collections.sort(tmsList);
		
		for(int i=0;i<tmsList.size();i++){						
			adapter.setFirstView(tmsList.get(i), this.findViewById(R.layout.tab_first), listViewItems);
		}

		listViewItems.requestLayout();
	}
}