package com.example.mymeds.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.content.Context;

public class MedFetcher {

	private static final int SECONDS_IN_DAY = 86400000;
	
	final String JSON_PATH = "meds.json";
	ArrayList<Medication> allmeds = new ArrayList<Medication>();
	Context mContext;

	/**
	 * This method loads in the parameter arrayList into the MedFetcher for later use.
	 * @param c
	 * @param arrayList
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void loadAssets(Context c, ArrayList arrayList)
	{
		mContext = c;
		allmeds = arrayList;
	}

	/**
	 * This method is used to calculate the medication for a specified day
	 * @param day The day being specified
	 * @return ArrayList of day's medication
	 */
	public ArrayList<Medication> daysMedication(long day)
	{
		ArrayList<Medication> daysMeds = new ArrayList<Medication>();

		for (int i=0;i<allmeds.size();i++) {

			long st = allmeds.get(i).getStartTime();
			long et = allmeds.get(i).getEndTime();

			long stDiff = day - st;

			if (stDiff >= 0 && !(day > et)) 
			{
				if (stDiff % allmeds.get(i).getRepeatPeriod() == 0)
				{
					daysMeds.add(allmeds.get(i));
				}				
			}				
		}

		return  daysMeds;
	}


	/**
	 * This method is used to calculate medication between a start day and end day.
	 * @param sDay The day the period should start from
	 * @param eDay The day the period should end.
	 * @return ArrayList of period's medication
	 */
	@SuppressWarnings("unused")
	public ArrayList<futureMedDetails> futureMedication(long sDay, long eDay)
	{
		long millisDiff = eDay - sDay;
		long daysDiff = (int)(millisDiff / SECONDS_IN_DAY);
		long currDay = sDay;
		
		ArrayList<futureMedDetails> futureMeds = new ArrayList<futureMedDetails>();

		for (int i = 0;i < daysDiff;i++)
		{
			ArrayList<Medication> daysMeds = daysMedication(sDay);
			combineLists(futureMeds, daysMeds);

			sDay += SECONDS_IN_DAY;
		}

		return futureMeds;
	}

	/**
	 * Method to combine two ArrayLists together.
	 * @param futureMeds
	 * @param daysMeds
	 * @return
	 */
	private ArrayList<futureMedDetails> combineLists(ArrayList<futureMedDetails> futureMeds, ArrayList<Medication> daysMeds)
	{
		for(int i = 0;i < daysMeds.size();i++)
		{
			int id = daysMeds.get(i).getIndex();
			int totalUnits = 0;
			boolean found = false;

			for(int j = 0; j < futureMeds.size(); j++)
			{
				if(id == futureMeds.get(j).getMedId())
				{
					found = true;
					totalUnits = unitsTotal(daysMeds.get(i).getFrequency());						
					futureMeds.get(j).increaseAmountNeeded(totalUnits);
					break;
				}
			}

			if(!found){

				totalUnits = unitsTotal(daysMeds.get(i).getFrequency());

				futureMedDetails newMed = new futureMedDetails(id, daysMeds.get(i).getName(),daysMeds.get(i).getDisplayName(), totalUnits);
				futureMeds.add(newMed);
			}

		}	

		return futureMeds;
	}

	/**
	 * Method to calculate required medication for a specified frequency.
	 * @param frequency
	 * @return Amount of medication
	 */
	private int unitsTotal(ArrayList<Frequency> frequency)
	{
		int totalUnits = 0;

		for(int i = 0; i<frequency.size();i++){
			totalUnits += frequency.get(i).units;
		}

		return totalUnits;
	}

	/**
	 * Method to modify the remaining quantity of a specified medication
	 * @param medId Medication to have it's quantity adjusted
	 * @param quantConsumed Quantity of medication that has been consumed
	 */
	public void modifyQuantity(int medId, int quantConsumed)
	{
		for(int i=0; i<allmeds.size(); i++){
			Medication currentMed = allmeds.get(i);
			if(currentMed.getIndex() == medId)
			{
				Medication modMed = currentMed;			
				int remaining = currentMed.getRemaining()-quantConsumed;
				modMed.setRemaining(remaining>0 ? remaining : 0);
				allmeds.set(i, modMed);
				break;
			}
		}
		
		JSONUtils.writeToFile(allmeds, mContext, true);
	}
	
	//Convert year, month, day into milliseconds
		public static long milliDate(int year, int month, int day)
		{
			Calendar cal = new GregorianCalendar(year, month, day);
			return cal.getTimeInMillis();
		}

}
