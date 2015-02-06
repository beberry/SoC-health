package com.example.mymeds.util;

//another class to handle item's id and name
public class Frequency {

	String dosage;
	int time, units;

	// constructor
	public Frequency(){

	}

	public String getDosage() {
		return dosage;
	}

	public void setDosage(String dosage) {
		this.dosage = dosage;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public int getUnits() {
		return units;
	}

	public void setUnits(int units) {
		this.units = units;
	}

}