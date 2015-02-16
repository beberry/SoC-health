package com.example.mymeds.util;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

//another class to handle item's id and name
public class Medication implements Parcelable{

	private int index;
	private String name, displayName, description, type;
	private long startTime, endTime;
	private int remaining, repeatPeriod;
	ArrayList<Frequency> frequency;

	// constructor
	public Medication(){

	}

	// constructor
	public Medication(int itemId, String itemName, String itemDescription) {
		this.index = itemId;
		this.name = itemName;
		this.description = itemDescription;		
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getName() {
		return name;
	}

	public void setName(String newName) {
		this.name = newName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
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
		name = in.readString ();
		displayName = in.readString ();
		description = in.readString ();
		type = in.readString ();

		index = in.readInt();
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
		dest.writeString(name);
		dest.writeString(displayName);
		dest.writeString(description);
		dest.writeString(type);

		dest.writeInt(index);
		dest.writeInt(remaining);
		dest.writeLong(startTime);
		dest.writeLong(endTime);	
	}

}