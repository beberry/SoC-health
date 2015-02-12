package com.example.mymeds.util;

public class futureMedDetails {
	private int medId;
	private String medName, displayName;
	private int amountNeeded;

	// constructor
	public futureMedDetails (int itemId, String itemName,String displayName, int amountNeeded) {
		this.medId = itemId;
		this.medName = itemName;
		this.displayName = displayName;
		this.amountNeeded = amountNeeded;
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
	
	public int getAmountNeeded() {
		return amountNeeded;
	}

	public void setAmountNeeded(int amountNeeded) {
		this.amountNeeded = amountNeeded;
	}
	
	public void increaseAmountNeeded(int amountNeeded) {
		this.amountNeeded += amountNeeded;
	}
}
