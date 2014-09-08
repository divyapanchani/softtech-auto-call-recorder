package com.softtech.apps.autocallrecorder;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.ViewFlipper;

@SuppressLint({ "NewApi", "ValidFragment" })
public class GeneralSetting extends ListFragment {

	private int positionTab = 0;
	private AdView adView;
	private Button btGeneralSetting, btSelectContacts, btGeneralSave,btGeneralCancel;
	private ToggleButton btEnableCall,btEnablePinCode;

	private ViewFlipper mViewFlipper;

	private Context mContext;

	DatabaseHandler db;
	private Config cfg;
	private Config cfgTypeRecord, cfgAudioQuality, cfgEnablePinCode,cfgPinCode;
	private RadioGroup radGroupTypeRecord, radGroupQualityRecord;
	private RadioButton radAllcalls, radContacts, radUnknown, radLowQuality,
			radMediumQuality, radHighQuality;
	private static CustomListContactAdapter contactAdapter;

	private TextView tvNotes;
	EditText etPinCode,etPinCodeConfirm;
	private View rootView;

	public GeneralSetting(Context context) {
		// TODO Auto-generated constructor stub
		super();

		// Doc database va khoi tao o day
		db = new DatabaseHandler(context);
		// auto record
		cfg = db.getConfig(1);
		// type records
		cfgTypeRecord = db.getConfig(5);
		cfgEnablePinCode = db.getConfig(6);
		cfgPinCode = db.getConfig(7);
		cfgAudioQuality = db.getConfig(2);
		contactAdapter = new CustomListContactAdapter(context);
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		mContext = activity;
	}
	
	public static final Handler handle = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			Bundle b = msg.getData();
			String dieukien = b.getString("text");
			Log.d("FRAGMENT", "Da nhan duoc chuoi =" + dieukien);
			contactAdapter.getFilter().filter(dieukien);
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		rootView = inflater.inflate(R.layout.general_setting, container, false);
		adView = (AdView) rootView.findViewById(R.id.adView);
		if(hasConnections()){
			// Look up the AdView as a resource and load a request
			    AdRequest adRequest = new AdRequest.Builder().build();
			    adView.loadAd(adRequest);
		}
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
		
		Log.d("PINCODE", "Pin Code on Init = "+cfgPinCode.get_value());
		if(cfgPinCode.get_value() == 0){
			if(cfgEnablePinCode.get_value()==0){
				cfgEnablePinCode.set_value(0);
				db.updateConfig(cfgEnablePinCode);
			}
			btEnablePinCode.setChecked(false);
		}else{
			if(cfgEnablePinCode.get_value() == 1){
				btEnablePinCode.setChecked(true);
			}else{
				btEnablePinCode.setChecked(false);
			}
		}
		Log.d("CONFIG", "Init Checked = " + cfg.get_value());

		/**
		 * Khoi tao trang thai ban dau cho nut radio
		 * */

		cfgAudioQuality = db.getConfig(2);
		
		if (cfgAudioQuality != null) {
			if (cfgAudioQuality.get_value() == 1) {
				radHighQuality.setChecked(true);
			} else if (cfgAudioQuality.get_value() == 2) {
				radMediumQuality.setChecked(true);
			} else if (cfgAudioQuality.get_value() == 3) {
				radLowQuality.setChecked(true);
			}else{
				Log.e("CONFIG AUDIO QUALITY ", ""+ cfgAudioQuality.get_value());
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
		
		btEnablePinCode = (ToggleButton) rootView.findViewById(R.id.btnEnablePinCode);
		btGeneralSave = (Button) rootView.findViewById(R.id.btnSave);
		btGeneralCancel = (Button) rootView.findViewById(R.id.btnCancel);
		
		etPinCode = (EditText) rootView.findViewById(R.id.edPin);
		etPinCodeConfirm = (EditText) rootView.findViewById(R.id.edPinConfirm);
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

					cfgTypeRecord = db.getConfig(5);
					
					Log.d("CONFIG TYPE", cfgTypeRecord.get_value() + "");

					if (cfgTypeRecord != null) {
						if (cfgTypeRecord.get_value() == 0) {
							radAllcalls.setChecked(true);
							tvNotes.setText("Select contact to Ignore");

						} else if (cfgTypeRecord.get_value() == 1) {
							tvNotes.setText("Select contact to Ignore");
							radContacts.setChecked(true);

						} else if (cfgTypeRecord.get_value() == 2) {
							tvNotes.setText("Select contact to Record");
							radUnknown.setChecked(true);
						}
					}
					
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
					cfg.set_value(1);
					db.updateConfig(cfg);
				} else {
					cfg.set_value(0);
					db.updateConfig(cfg);
				}
			}
		});

		radGroupQualityRecord
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						// TODO Auto-generated method stub
						
						switch (checkedId) {
						case R.id.radButton_Low:						
							cfgAudioQuality.set_value(3);
							
							break;
						case R.id.radButton_Medium:
							cfgAudioQuality.set_value(2);
							break;
						case R.id.radButton_High:
							cfgAudioQuality.set_value(1);
							break;
						default:
							cfgAudioQuality.set_value(3);
							break;
						}
						db.updateConfig(cfgAudioQuality);
					}
				});
		btEnablePinCode.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){
					cfgEnablePinCode.set_value(1);
				}else{
					cfgEnablePinCode.set_value(0);
				}
				db.updateConfig(cfgEnablePinCode);
			}
		});
		
		btGeneralSave.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// Kiem tra dieu kien nhap vao
				String pinCode = etPinCode.getText().toString();
				String pinCodeConfirm = etPinCodeConfirm.getText().toString();
				Log.d("PINCODE", "Pin code save button click");
				if(pinCode.length() == 4 && pinCode.equals(pinCodeConfirm)){
					Log.d("PINCODE", "Pin code = "+pinCode);
					cfgPinCode.set_value(Integer.parseInt(pinCode));
					db.updateConfig(cfgPinCode);
					AlertDialog.Builder builder = new AlertDialog.Builder(
							mContext);
					builder.setTitle("Success");
					builder.setMessage("Save Pin success.");
					builder.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									// if this button is clicked, close
									// current activity
									dialog.dismiss();
									etPinCode.setText("");
									etPinCodeConfirm.setText("");
								}
							});
			        AlertDialog alertDialog = builder.create();
			        alertDialog.show();
				}else{
					AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
			        builder.setTitle("Don't match");
			        builder.setMessage("Pin don't match, please try again");
			        builder.setPositiveButton("OK",new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int id) {
							// if this button is clicked, close
							// current activity
							dialog.dismiss();
						}
					  });
			        AlertDialog alertDialog = builder.create();
			        alertDialog.show();
				}
			}
		});
		
		btGeneralCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent itent = new Intent(getActivity(), MainActivity.class);
				startActivity(itent);
				getActivity().finish();
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

						switch (checkedId) {
						case R.id.radAllcalls:
							// update config
							cfgTypeRecord.set_value(0);
							db.updateConfig(cfgTypeRecord);

							// update UI
							tvNotes.setText("Select contact to Ignore");

							break;
						case R.id.radContacts:
							// update config
							cfgTypeRecord.set_value(1);
							db.updateConfig(cfgTypeRecord);
							
							// update UI
							tvNotes.setText("Select contact to Ignore");

							break;
						case R.id.radUnknown:
							// update config
							cfgTypeRecord.set_value(2);
							db.updateConfig(cfgTypeRecord);
							
							tvNotes.setText("Select contact to Record");

							break;
						default:
							cfgTypeRecord.set_value(0);
							db.updateConfig(cfgTypeRecord);
							break;
						}
						contactAdapter.notifyDataSetChanged();
						
						Log.d("CHECKED",
								"Checked value = " + cfgTypeRecord.get_value());
						// cfgTypeRecord.set_value(newConf.get_value());
						getListView().setAdapter(contactAdapter);
					}
				});

	}
	public boolean hasConnections() {
		ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (null == ni)
			return false;
		return ni.isConnectedOrConnecting();
	}
	
	
}
