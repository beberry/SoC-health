package com.example.mymeds.stores;

@SuppressWarnings("rawtypes")
public class TodayMedStore implements Comparable {

	private String medName;
	private String nickName;
	private String time;
	private int medIndex;
	private int freqIndex;
	private long takenTime;

	public String getMedName() {
		return medName;
	}
	public void setMedName(String medName) {
		this.medName = medName;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public int getMedIndex() {
		return medIndex;
	}
	public void setMedIndex(int medIndex) {
		this.medIndex = medIndex;
	}
	public int getFreqIndex() {
		return freqIndex;
	}
	public void setFreqIndex(int freqIndex) {
		this.freqIndex = freqIndex;
	}


	@Override
	public int compareTo(Object otherFreq) {
		String compareage=((TodayMedStore)otherFreq).getTime();
		/* For Ascending order*/
		return this.time.compareTo(compareage);

	}
	public long getTakenTime() {
		return takenTime;
	}
	public void setTakenTime(long takenTime) {
		this.takenTime = takenTime;
	}
}
