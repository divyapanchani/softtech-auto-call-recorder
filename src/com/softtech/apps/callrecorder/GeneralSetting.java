package com.softtech.apps.callrecorder;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.ViewFlipper;

@SuppressLint({ "NewApi", "ValidFragment" })
public class GeneralSetting extends ListFragment {

	private int positionTab = 0;

	private Button btGeneralSetting, btSelectContacts;
	private ToggleButton btEnableCall;

	private ViewFlipper mViewFlipper;

	private Context mContext;

	DatabaseHandler db;
	private Config cfg;
	private Config cfgTypeRecord, cfgAudioQuality;
	private RadioGroup radGroupTypeRecord, radGroupQualityRecord;
	private RadioButton radAllcalls, radContacts, radUnknown, radLowQuality,
			radMediumQuality, radHighQuality;
	private CustomListContactAdapter contactAdapter;

	private TextView tvNotes;

	private View rootView;

	public GeneralSetting(Context context) {
		// TODO Auto-generated constructor stub
		super();

		// Doc database va khoi tao o day
		db = new DatabaseHandler(context);
		// auto record
		cfg = db.getConfig(1);
		// type records
		cfgTypeRecord = db.getConfig(4);
		cfgAudioQuality = db.getConfig(2);
		

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

		rootView = inflater.inflate(R.layout.general_setting, container, false);

		mViewFlipper = (ViewFlipper) rootView.findViewById(R.id.view_flipper);

		btGeneralSetting = (Button) rootView
				.findViewById(R.id.btGeneralSetting);

		btSelectContacts = (Button) rootView.findViewById(R.id.btSelectContact);

		settingTabGeneral();

		settingTabContacts();

		setEventClickButton();

		setEventClickButtonTabGeneral();

		setEventClickButtonTabContacts();

		if (positionTab == 0) {

			btGeneralSetting
					.setBackgroundResource(R.drawable.selector_hometab_btselected);
		} else {
			btSelectContacts
					.setBackgroundResource(R.drawable.selector_hometab_btselected);
			Log.d("Tag", "Tab select contact clicked");

			// Get contact and show here
		}
		// Xu ly nut enable automatic call recorder
		/**
		 * Khoi tao trang thai ban dau cho nut toggle tu viec doc tu csdl
		 * */
		if (cfg.get_value() == 1) {
			btEnableCall.setChecked(true);
		} else {
			btEnableCall.setChecked(false);
		}

		Log.d("CONFIG", "Init Checked = " + cfg.get_value());

		/**
		 * Khoi tao trang thai ban dau cho nut radio
		 * */

		cfgTypeRecord = db.getConfig(4);
		Log.d("CONFIG TYPE", cfgTypeRecord.get_value() + "");

		if (cfgTypeRecord != null) {
			if (cfgTypeRecord.get_value() == 0) {
				radAllcalls.setChecked(true);
				tvNotes.setText("Select contact to Ignore");
				
				contactAdapter = new CustomListContactAdapter(mContext);
				
			} else if (cfgTypeRecord.get_value() == 1) {
				tvNotes.setText("Select contact to Ignore");
				radContacts.setChecked(true);
				
				contactAdapter = new CustomListContactAdapter(mContext);
			} else if (cfgTypeRecord.get_value() == 2) {
				tvNotes.setText("Select contact to Record");
				radUnknown.setChecked(true);
				
				contactAdapter = new CustomListContactAdapter(mContext);
			}else{
				contactAdapter = new CustomListContactAdapter(mContext);
			}
		}

		cfgAudioQuality = db.getConfig(2);
		if (cfgAudioQuality != null) {
			if (cfgAudioQuality.get_value() == 1) {
				radHighQuality.setChecked(true);
			} else if (cfgAudioQuality.get_value() == 2) {
				radMediumQuality.setChecked(true);
			} else if (cfgAudioQuality.get_value() == 3) {
				radLowQuality.setChecked(true);
			}

		}

		return rootView;
	}

	private void settingTabGeneral() {
		// tab general setting
		btEnableCall = (ToggleButton) rootView
				.findViewById(R.id.toggleEnableCall);

		radGroupQualityRecord = (RadioGroup) rootView
				.findViewById(R.id.radioGroupQuality);
		radLowQuality = (RadioButton) rootView.findViewById(R.id.radButton_Low);
		radMediumQuality = (RadioButton) rootView
				.findViewById(R.id.radButton_Medium);
		radHighQuality = (RadioButton) rootView
				.findViewById(R.id.radButton_High);
	}

	private void settingTabContacts() {
		// tab Contacts

		tvNotes = (TextView) rootView.findViewById(R.id.tvContentSelect);

		radGroupTypeRecord = (RadioGroup) rootView
				.findViewById(R.id.radioGroupTypeRecord);

		radAllcalls = (RadioButton) rootView.findViewById(R.id.radAllcalls);

		radContacts = (RadioButton) rootView.findViewById(R.id.radContacts);

		radUnknown = (RadioButton) rootView.findViewById(R.id.radUnknown);
	}

	private void setEventClickButton() {
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

					getListView().setAdapter(contactAdapter);

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
	}

	private void setEventClickButtonTabGeneral() {

		btEnableCall.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					Config newConfig = new Config(cfg.get_id(), 1, cfg
							.get_keyword());
					db.updateConfig(newConfig);
				} else {
					Config newConfig2 = new Config(cfg.get_id(), 0, cfg
							.get_keyword());
					db.updateConfig(newConfig2);
				}

				cfg = db.getConfig(1);
				// Log.d("CONFIG", "Checked = "+cfg.get_value());
			}
		});

		radGroupQualityRecord
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						// TODO Auto-generated method stub
						Config newConfig = null;
						switch (checkedId) {
						case R.id.radButton_Low:
							newConfig = new Config(cfgAudioQuality.get_id(), 3,
									cfgAudioQuality.get_keyword());
							break;
						case R.id.radButton_Medium:
							newConfig = new Config(cfgAudioQuality.get_id(), 2,
									cfgAudioQuality.get_keyword());
							break;
						case R.id.radButton_High:
							newConfig = new Config(cfgAudioQuality.get_id(), 1,
									cfgAudioQuality.get_keyword());
							break;
						default:
							newConfig = cfgAudioQuality;
							break;
						}
						db.updateConfig(newConfig);

						cfgAudioQuality.set_value(newConfig.get_value());
					}
				});

	}

	private void setEventClickButtonTabContacts() {
		// page Contacts
		radGroupTypeRecord
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						// TODO Auto-generated method stub
						Config newConf = null;

						switch (checkedId) {
						case R.id.radAllcalls:
							// update config
							newConf = new Config(cfgTypeRecord.get_id(), 0,
									cfgTypeRecord.get_keyword());
							
							//update UI
							tvNotes.setText("Select contact to Ignore");
							
							
							break;
						case R.id.radContacts:
							// update config
							newConf = new Config(cfgTypeRecord.get_id(), 1,
									cfgTypeRecord.get_keyword());
							
							//update UI
							tvNotes.setText("Select contact to Ignore");
							break;
						case R.id.radUnknown:
							// update config
							newConf = new Config(cfgTypeRecord.get_id(), 2,
									cfgTypeRecord.get_keyword());
							
							//update UI
							tvNotes.setText("Select contact to Record");
							break;
						default:
							newConf = cfgTypeRecord;
							break;
						}
						
						db.updateConfig(newConf);
						cfgTypeRecord.set_value(newConf.get_value());
					}
				});

	}
}
