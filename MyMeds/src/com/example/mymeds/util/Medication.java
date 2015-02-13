package com.example.mymeds.util;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

//another class to handle item's id and name
public class Medication implements Parcelable{

	private int medId;
	private String medName, displayName, description, type, time;
	private long startTime, endTime;
	private int remaining, repeatPeriod;
	ArrayList<Frequency> frequency;

	// constructor
	public Medication(){

	}

	// constructor
	public Medication(int itemId, String itemName, String itemDescription) {
		this.medId = itemId;
		this.medName = itemName;
		this.description = itemDescription;		
	}

	public int getMedId() {
		return medId;
	}

	public void setMedId(int medId) {
		this.medId = medId;
	}

	public String getMedName() {
		return medName;
	}

	public void setMedName(String medName) {
		this.medName = medName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String newTime) {
		this.time = newTime;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public int getRemaining() {
		return remaining;
	}

	public void setRemaining(int remaining) {
		this.remaining = remaining;
	}

	public int getRepeatPeriod() {
		return repeatPeriod;
	}

	public void setRepeatPeriod(int repeatPeriod) {
		this.repeatPeriod = repeatPeriod;
	}

	public void setFrequency(ArrayList<Frequency> freq){
		this.frequency = freq;
	}

	public ArrayList<Frequency> getFrequency(){
		return frequency;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public Medication (Parcel in)
	{
		medName = in.readString ();
		displayName = in.readString ();
		description = in.readString ();
		type = in.readString ();
		time = in.readString ();

		medId = in.readInt();
		remaining = in.readInt();
		startTime = in.readLong();
		endTime = in.readLong();
	}

	public static final Parcelable.Creator<Medication> CREATOR
	= new Parcelable.Creator<Medication>() 
	{
		public Medication createFromParcel(Parcel in) 
		{
			return new Medication(in);
		}

		public Medication[] newArray (int size) 
		{
			return new Medication[size];
		}
	};

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(medName);
		dest.writeString(displayName);
		dest.writeString(description);
		dest.writeString(type);
		dest.writeString(time);

		dest.writeInt(medId);
		dest.writeInt(remaining);
		dest.writeLong(startTime);
		dest.writeLong(endTime);	
	}

}