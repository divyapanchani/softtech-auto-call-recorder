package com.softtech.apps.callrecorder;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ToggleButton;

@SuppressLint("NewApi")
public class SyncToDropbox extends Fragment {

	private Context mContext;
	
	private ToggleButton toggleManual, toggleAuto;
	
	public SyncToDropbox(Context context) {
		// TODO Auto-generated constructor stub
		super();
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		mContext = activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView = inflater .inflate(R.layout.sync_to_dropbox, container, false); 
		
		toggleManual = (ToggleButton) rootView.findViewById(R.id.toggleManualSync);
		
		toggleAuto = (ToggleButton) rootView.findViewById(R.id.toggleAutoSync);
		
        return rootView;
	}

}

