package com.example.mymeds.util;

//another class to handle item's id and name
public class Frequency {

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

}