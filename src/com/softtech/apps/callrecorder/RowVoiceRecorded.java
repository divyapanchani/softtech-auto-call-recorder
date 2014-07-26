package com.softtech.apps.callrecorder;

public class RowVoiceRecorded {
	private String mName;
	private String mPath;
	private Long mTimeCreate;
	private int mDuration;
	public RowVoiceRecorded() {
		// TODO Auto-generated constructor stub
	}
	
	public RowVoiceRecorded(String Name, String Path, Long TimeCreate, int Duration) {
		this.mName = Name;
		this.mPath = Path;
		this.mTimeCreate = TimeCreate;
		this.mDuration = Duration;
	}

	public int getmDuration() {
		return mDuration;
	}

	public void setmDuration(int mDuration) {
		this.mDuration = mDuration;
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

}
