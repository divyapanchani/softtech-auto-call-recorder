package com.softtech.apps.callrecorder;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;
import android.widget.ViewFlipper;

import com.softtech.apps.callrecorder.Config;

@SuppressLint({ "NewApi", "ValidFragment" })
public class GeneralSetting extends Fragment {

	private int positionTab = 0;

	private Button btGeneralSetting, btSelectContacts;
	private ToggleButton btEnableCall;

	private ViewFlipper mViewFlipper;
	
	private Context mContext;
	
	DatabaseHandler db;
	private Config cfg;
	
	
	public GeneralSetting(Context context) {
		// TODO Auto-generated constructor stub
		super();
		
		// Doc database va khoi tao o day
		db = new DatabaseHandler(context);
		cfg = db.getConfig(1);
		
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
		View rootView = inflater .inflate(R.layout.general_setting, container, false);
		
		mViewFlipper = (ViewFlipper) rootView.findViewById(R.id.view_flipper);
		
		btGeneralSetting = (Button) rootView.findViewById(R.id.btGeneralSetting);
		btSelectContacts = (Button) rootView.findViewById(R.id.btSelectContact);
		btEnableCall = (ToggleButton) rootView.findViewById(R.id.toggleEnableCall);
		
		if (positionTab == 0) {

			btGeneralSetting.setBackgroundResource(R.drawable.selector_hometab_btselected);
		} else {
			btSelectContacts.setBackgroundResource(R.drawable.selector_hometab_btselected);
			Log.d("Tag", "Tab 0 click");
		}
		
		btGeneralSetting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				Log.d("Tag", "Tab 0 click");
				if (positionTab != 0) {
					positionTab = 0;

					btSelectContacts
							.setBackgroundResource(R.drawable.selector_hometab_btdefault);
					btGeneralSetting
							.setBackgroundResource(R.drawable.selector_hometab_btselected);

					mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(
							mContext, R.anim.in_from_right));
					mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(
							mContext, R.anim.out_to_left));
					mViewFlipper.showNext();

				}
			}
		});

		btSelectContacts.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d("Tag", "Tab 1 click");
				if (positionTab != 1) {
					positionTab = 1;
					
					btSelectContacts
							.setBackgroundResource(R.drawable.selector_hometab_btselected);
					btGeneralSetting
							.setBackgroundResource(R.drawable.selector_hometab_btdefault);

					mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(
							mContext, R.anim.in_from_left));
					mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(
							mContext, R.anim.out_to_right));
					mViewFlipper.showPrevious();
				}
			}
		});
		
		// Xu ly nut enable automatic call recorder
		/**
		 * Khoi tao trang thai ban dau cho nut toggle tu viec doc tu csdl
		 * */
		if(cfg.get_value() == 1){
			btEnableCall.setChecked(true);
		}else{
			btEnableCall.setChecked(false);
		}
		
		Log.d("CONFIG", "Init Checked = "+cfg.get_value());
		
		btEnableCall.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){
					Config newConfig = new Config(cfg.get_id(),1,cfg.get_keyword());
					db.updateConfig(newConfig);
				}else{
					Config newConfig2 = new Config(cfg.get_id(),0,cfg.get_keyword());
					db.updateConfig(newConfig2);
				}

				cfg = db.getConfig(1);
				//Log.d("CONFIG", "Checked = "+cfg.get_value());
			}
		});
		
        return rootView;
	}

	
}
