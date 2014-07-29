package com.softtech.apps.callrecorder;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;

@SuppressLint({ "NewApi", "ValidFragment" })
public class SyncToDropbox extends Fragment {

	private Context mContext;
	
	private ToggleButton toggleManual, toggleAuto;
	
	DatabaseHandler db;
	private Config cfg;
	
	public SyncToDropbox(Context context) {
		// TODO Auto-generated constructor stub
		super();
		
		// Doc database va khoi tao o day
		db = new DatabaseHandler(context);
		cfg = db.getConfig(2);
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
		
		Log.d("CONFIG", "Init Checked = "+cfg.get_value());
		if(cfg.get_value() == 0){
			toggleManual.setChecked(true);
			toggleAuto.setChecked(false);
		}else{
			toggleManual.setChecked(false);
			toggleAuto.setChecked(true);
		}
		
		toggleManual.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){
					toggleAuto.setChecked(false);
					Config newConfig = new Config(cfg.get_id(),0,cfg.get_keyword());
					db.updateConfig(newConfig);
				}else{
					toggleAuto.setChecked(true);
					Config newConfig = new Config(cfg.get_id(),1,cfg.get_keyword());
					db.updateConfig(newConfig);
				}
				//Log.d("CONFIG", "Checked = "+cfg.get_value());
			}
		});
		
		toggleAuto.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){
					toggleManual.setChecked(false);
					Config newConfig = new Config(cfg.get_id(),1,cfg.get_keyword());
					db.updateConfig(newConfig);
				}else{
					toggleManual.setChecked(true);
					Config newConfig = new Config(cfg.get_id(),0,cfg.get_keyword());
					db.updateConfig(newConfig);
				}
				cfg = db.getConfig(2);
				Log.d("CONFIG", "Checked = "+cfg.get_value());
			}
			
		});
		
        return rootView;
	}

}

