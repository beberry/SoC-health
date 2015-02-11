package com.example.mymeds.util;

import java.util.ArrayList;

import android.content.Context;

public class MedFetcher {

	ArrayList<Medication> allmeds = new ArrayList<Medication>();
	Context mContext;

	public void loadAssets(Context c)
	{
		mContext = c;
	};


	public ArrayList<Medication> daysMedication(long day, ArrayList<Medication> inMeds)
	{
		ArrayList<Medication> daysMeds = inMeds;

		for (int i=0;i<allmeds.size();i++){

			long st = allmeds.get(i).startTime;
			long et = allmeds.get(i).endTime;

			long stDiff = day - st;


			if(stDiff >= 0 && !(day >et)){
				if(stDiff % allmeds.get(i).repeatPeriod == 0)
				{
					daysMeds.add(allmeds.get(i));
				}				
			}				
		}

		return  daysMeds;
	};
}