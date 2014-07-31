package com.softtech.apps.callrecorder;

public class RowVoiceRecorded {

	private String mName;
	private String mPath;
	private String mPhoneNumber;
	private Long mTimeCreate;
	public RowVoiceRecorded() {
		// TODO Auto-generated constructor stub
	}
	
	public RowVoiceRecorded(String Name, String Path, Long TimeCreate,String phoneNumber) {
		this.mName = Name;
		this.mPath = Path;
		this.mTimeCreate = TimeCreate;
		this.mPhoneNumber = phoneNumber;
	}

	public String getmName() {
		return mName;
	}

	public void setmName(String mName) {
		this.mName = mName;
	}

	public String getmPath() {
		return mPath;
	}

	public void setmPath(String mPath) {
		this.mPath = mPath;
	}

	public Long getmTimeCreate() {
		return mTimeCreate;
	}

	public void setmTimeCreate(Long mTimeCreate) {
		this.mTimeCreate = mTimeCreate;
	}
	public String getmPhoneNumber() {
		return mPhoneNumber;
	}

	public void setmPhoneNumber(String mPhoneNumber) {
		this.mPhoneNumber = mPhoneNumber;
	}

}
