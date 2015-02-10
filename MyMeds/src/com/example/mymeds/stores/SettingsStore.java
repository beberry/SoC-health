package com.example.mymeds.stores;
//test
public class SettingsStore {

	public Boolean getBanners() {
		return banners;
	}
	public void setBanners(Boolean banners) {
		this.banners = banners;
	}
	public Boolean getSounds() {
		return sounds;
	}
	public void setSounds(Boolean sounds) {
		this.sounds = sounds;
	}
	public int getSoundSelection() {
		return soundSelection;
	}
	public void setSoundSelection(int soundSelection) {
		this.soundSelection = soundSelection;
	}
	public int getSnoozeTime() {
		return snoozeTime;
	}
	public void setSnoozeTime(int snoozeTime) {
		this.snoozeTime = snoozeTime;
	}
	public int getHowLongBefore() {
		return howLongBefore;
	}
	public void setHowLongBefore(int howLongBefore) {
		this.howLongBefore = howLongBefore;
	}
	public int getTextSize() {
		return textSize;
	}
	public void setTextSize(int textSize) {
		this.textSize = textSize;
	}
	private Boolean banners;
	private Boolean sounds;
	private int soundSelection;
	private int snoozeTime;
	private int howLongBefore;
	private int textSize;

}
