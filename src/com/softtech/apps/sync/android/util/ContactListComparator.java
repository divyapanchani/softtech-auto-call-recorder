package com.softtech.apps.sync.android.util;

import java.util.Comparator;

import com.softtech.apps.callrecorder.RowVoiceRecorded;

public class ContactListComparator implements Comparator<RowVoiceRecorded> {

	@Override
	public int compare(RowVoiceRecorded lhs, RowVoiceRecorded rhs) {
		// TODO Auto-generated method stub
		return compareDate(lhs, rhs);
	}
	
	private int compareDate(RowVoiceRecorded lhs, RowVoiceRecorded rhs){
		return rhs.getmTimeCreate().compareTo(lhs.getmTimeCreate()) ;	}

}
