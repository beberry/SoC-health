package com.example.mymeds.util;

//another class to handle item's id and name
public class Medication {

    public int medId;
    public String medName;
    public String description;
    
    public Medication(){
    	
    }

    // constructor
    public Medication(int itemId, String itemName, String itemDescription) {
        this.medId = itemId;
        this.medName = itemName;
        this.description = itemDescription;
    }

	public int getItemId() {
		return medId;
	}

	public void setItemId(int itemId) {
		this.medId = itemId;
	}

	public String getItemName() {
		return medName;
	}

	public void setItemName(String itemName) {
		this.medName = itemName;
	}
	
	public String getItemDescription() {
		return medName;
	}

	public void setItemDescription(String itemDesc) {
		this.description = itemDesc;
	}
	
	public String toString(){
		return this.medId+"    "+this.medName+"    "+this.description;
	}

}