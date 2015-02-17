package com.example.mymeds.stores;

import java.util.ArrayList;
import java.util.List;

public class MedicationStore {

	String medicineName;
	String displayName;
	String description;
	String type;
	long startTime;
	long endTime;
	String remaining;
	String repeatPeriod;
	
	List<Integer> listTime = new ArrayList<Integer>();
	List<Integer> listDosage = new ArrayList<Integer>();
	List<Integer> listUnit = new ArrayList<Integer>();
	
	
	public String getMedicineName() {
		return medicineName;
	}
	public void setMedicineName(String medicineName) {
		this.medicineName = medicineName;
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

	public String getRemaining() {
		return remaining;
	}
	public void setRemaining(String remaining) {
		this.remaining = remaining;
	}
	public String getRepeatPeriod() {
		return repeatPeriod;
	}
	public void setRepeatPeriod(String repeatPeriod) {
		this.repeatPeriod = repeatPeriod;
	}
	public List<Integer> getListTime() {
		return listTime;
	}
	public void setListTime(List<Integer> listTime) {
		this.listTime = listTime;
	}
	public List<Integer> getListDosage() {
		return listDosage;
	}
	public void setListDosage(List<Integer> listDosage) {
		this.listDosage = listDosage;
	}
	public List<Integer> getListUnit() {
		return listUnit;
	}
	public void setListUnit(List<Integer> listUnit) {
		this.listUnit = listUnit;
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
	
	
}
