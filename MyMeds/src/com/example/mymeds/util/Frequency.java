package com.example.mymeds.util;

import android.os.Parcel;
import android.os.Parcelable;

//another class to handle item's id and name
public class Frequency implements Parcelable{

	String dosage, time;
	int units;

	// constructor
	public Frequency(){

	}

	public String getDosage() {
		return dosage;
	}

	public void setDosage(String dosage) {
		this.dosage = dosage;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public int getUnits() {
		return units;
	}

	public void setUnits(int units) {
		this.units = units;
	}

	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(dosage);
		dest.writeString(time);
		dest.writeInt(units);
		
	}
	
	public static final Parcelable.Creator<Frequency> CREATOR
	= new Parcelable.Creator<Frequency>() 
	{
		public Frequency createFromParcel(Parcel in) 
		{
			return new Frequency(in);
		}

		public Frequency[] newArray (int size) 
		{
			return new Frequency[size];
		}
	};
	
	public Frequency (Parcel in)
	{
		dosage = in.readString ();

		time = in.readString();
		units = in.readInt();
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
}