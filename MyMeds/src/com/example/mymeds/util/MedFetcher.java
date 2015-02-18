package com.example.mymeds.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
	 * This method resets the ArrayList of medication
	 * @param allMeds The new list of medication
	 */
	public void resetMeds (ArrayList<Medication> allmeds)
	{
		this.allmeds = allmeds;
	}

	/**
	 * This method is used to calculate the medication for a specified day
	 * @param day The day being specified 
	 * @return ArrayList of day's medication
	 */
	public ArrayList<Medication> daysMedication(long day)
	{
		//create array list to hold the days medication
		ArrayList<Medication> daysMeds = new ArrayList<Medication>();

		//for all the medication
		for (int i=0;i<allmeds.size();i++) {

			//get the first and last days to take the medication
			long st = allmeds.get(i).getStartTime();
			long et = allmeds.get(i).getEndTime();

			//get diffrence between input day and the start day
			long stDiff = day - st;

			//if the difference is larger (the medicaiton has started to be taken) and
			//the input day is after the end day (the medication is still being taken)
			if (stDiff >= 0 && !(day > et)) 
			{
				//if the modules of the days diffrece by the day repeat is 0 it means the medication is to be taken that day
				if (stDiff % allmeds.get(i).getRepeatPeriod() == 0)
				{
					//add the medication to the days medication
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
		//get difference between the first and last day the user wants to see a medication total for
		long millisDiff = eDay - sDay;
		//calculate how many days that diffrence is
		long daysDiff = (int)(millisDiff / SECONDS_IN_DAY);

		long currDay = sDay;

		//create a new ArrayList to store each medication being taken between those two dates
		ArrayList<futureMedDetails> futureMeds = new ArrayList<futureMedDetails>();

		//for each day
		for (int i = 0;i < daysDiff;i++)
		{
			//get the medication for a day
			ArrayList<Medication> daysMeds = daysMedication(sDay);
			//combine the currently found medication and the newly found
			combineLists(futureMeds, daysMeds);

			//increment to check for next day
			sDay += SECONDS_IN_DAY;
		}

		//return ArrayList of medication
		return futureMeds;
	}

	/**
	 * Method to combine two ArrayLists together.
	 * @param futureMeds Total list of medication for between 2 dates
	 * @param daysMeds List of medication for one day
	 * @return
	 */
	private ArrayList<futureMedDetails> combineLists(ArrayList<futureMedDetails> futureMeds, ArrayList<Medication> daysMeds)
	{
		//for every medication in the list for one day
		for(int i = 0;i < daysMeds.size();i++)
		{
			//get the medication index
			int id = daysMeds.get(i).getIndex();
			//variable to store ow many units to take in that day
			int totalUnits = 0;
			//tells if medication has been found
			boolean found = false;

			//for every medication in the combined list
			for(int j = 0; j < futureMeds.size(); j++)
			{
				//if medication is allready in combined list
				if(id == futureMeds.get(j).getMedId())
				{
					found = true;

					//add the amount of medicatin needed for that day to the instance in the combined list
					totalUnits = unitsTotal(daysMeds.get(i).getFrequency());						
					futureMeds.get(j).increaseAmountNeeded(totalUnits);
					break;
				}
			}

			//if the medication is not allready in the list add it
			if(!found){

				totalUnits = unitsTotal(daysMeds.get(i).getFrequency());
				futureMedDetails newMed = new futureMedDetails(id, daysMeds.get(i).getName(),daysMeds.get(i).getDisplayName(), totalUnits);
				futureMeds.add(newMed);
			}

		}	

		//return combined lists
		return futureMeds;
	}

	/**
	 * Method to calculate required medication for an entire day
	 * @param frequency ArrayList of each time the medication needs to be taken
	 * @return Amount of medication
	 */
	private int unitsTotal(ArrayList<Frequency> frequency)
	{
		int totalUnits = 0;

		//loop through each frequency object totaling the amount of units that need to be taken
		for(int i = 0; i<frequency.size();i++){
			totalUnits += frequency.get(i).units;
		}

		//return the total
		return totalUnits;
	}

	/**
	 * Method to modify the remaining quantity of a specified medication
	 * @param medId Medication to have it's quantity adjusted
	 * @param quantConsumed Quantity of medication that has been consumed
	 * @param timeTaken the time the lates dosage was taken
	 * @param freqNum the frequency object index
	 */
	public void modifyQuantity(int medId, int quantConsumed, long timeTaken, int freqNum)
	{
		//for all medication
		for(int i=0; i<allmeds.size(); i++){

			//get the medication id
			Medication currentMed = allmeds.get(i);
			//if that is the same as the one passed in
			if(currentMed.getIndex() == medId)
			{
				//update the amount left by subtrackting the amount taken and store in medication object
				Medication modMed = currentMed;			
				int remaining = currentMed.getRemaining()-quantConsumed;
				modMed.setRemaining(remaining>0 ? remaining : 0);

				//update the frequency taken time
				modMed.getFrequency().get(freqNum).setTaken(timeTaken);				
				//update all meds ArrayList
				allmeds.set(i, modMed);


				break;
			}
		}

		//save changes to JSON file
		JSONUtils.writeToFile(allmeds, mContext, true);
	}

	/**
	 * Method to convert date in separate integers into milliseconds
	 * @param int year, month, day Date in separate values 
	 * @return date in milliseconds
	 */
	public static long milliDate(int year, int month, int day)
	{
		Calendar cal = new GregorianCalendar(year, month, day);
		return cal.getTimeInMillis();
	}

	/**
	 * Method calculates if medication has been taken that day
	 * @param lastTaken the last time the medicaiton was taken in milliseconds
	 */
	public static boolean toBeTakenToday(long lastTaken){

		//get todays date
		Date td = new Date();
		//find iddfrence between todays date and the last time it was taken
		long diff = td.getTime() - lastTaken;

		//if the diffrence is greater than a day return true, else false
		if(diff > SECONDS_IN_DAY)
		{
			return true;
		}
		else
		{
			return false;   	     
		}
	}

}
