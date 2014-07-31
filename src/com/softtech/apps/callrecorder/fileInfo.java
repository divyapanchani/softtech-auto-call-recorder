package com.softtech.apps.callrecorder;

public class fileInfo {
	String dateTime;
	String phoneNumber;
	boolean isSync;
	
	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public boolean isSync() {
		return isSync;
	}

	public void setSync(boolean isSync) {
		this.isSync = isSync;
	}

	public fileInfo() {
		// TODO Auto-generated constructor stub
	}
	
	public fileInfo(String _dateTime, String _phoneNumber, boolean _isSync) {
		// TODO Auto-generated constructor stub
		this.dateTime = _dateTime;
		this.phoneNumber = _phoneNumber;
		this.isSync = _isSync;
	}

}
