package com.example.mymeds.stores;

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
	public String getSoundSelection() {
		return soundSelection;
	}
	public void setSoundSelection(String soundSelection) {
		this.soundSelection = soundSelection;
	}
	public String getSnoozeTime() {
		return snoozeTime;
	}
	public void setSnoozeTime(String snoozeTime) {
		this.snoozeTime = snoozeTime;
	}
	public String getHowLongBefore() {
		return howLongBefore;
	}
	public void setHowLongBefore(String howLongBefore) {
		this.howLongBefore = howLongBefore;
	}
	public String getTextSize() {
		return textSize;
	}
	public void setTextSize(String textSize) {
		this.textSize = textSize;
	}
	private Boolean banners;
	private Boolean sounds;
	private String soundSelection;
	private String snoozeTime;
	private String howLongBefore;
	private String textSize;
}
