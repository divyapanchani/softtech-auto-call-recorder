package com.softtech.apps.callrecorder;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ToggleButton;

import com.softtech.apps.constant.Constant;
import com.softtech.apps.dropbox.DropboxApi;

@SuppressLint("ValidFragment")
public class SyncToDropbox extends Fragment {

	private Context mContext;

	private ToggleButton toggleManual, toggleAuto;

	DatabaseHandler db;

	private Config cfgAutoSync, cfgTypeSync;

	RadioGroup mRadioGroup;

	RadioButton radAllCalls;

	RadioButton radFavorites;

	private Button btLink, btUnlink, btRunCleanUp;

	private DropboxApi mDropboxApi;

	public SyncToDropbox(){
		
	}
	public SyncToDropbox(Context context, DropboxApi dropboxApi) {
		// TODO Auto-generated constructor stub
		super();
		mDropboxApi = dropboxApi;

		if (mDropboxApi == null) {
			mDropboxApi = new DropboxApi(mContext);
			mDropboxApi.registerAccountDropbox();
		}
		// Doc database va khoi tao o day
		db = new DatabaseHandler(context);
		cfgAutoSync = db.getConfig(3);
		cfgTypeSync = db.getConfig(4);
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
		View rootView = inflater.inflate(R.layout.sync_to_dropbox, container,
				false);

		toggleManual = (ToggleButton) rootView
				.findViewById(R.id.toggleManualSync);

		toggleAuto = (ToggleButton) rootView.findViewById(R.id.toggleAutoSync);

		btLink = (Button) rootView.findViewById(R.id.btLinkDropbox);

		btUnlink = (Button) rootView.findViewById(R.id.btUnlink);

		btRunCleanUp = (Button) rootView.findViewById(R.id.btRunCleanUp);

		initDropboxApi();

		setEventClickButton();

		// Log.d("CONFIG", "Init Checked = "+cfg.get_value());
		if (cfgAutoSync.get_value() == 0) {

			toggleManual.setChecked(true);
			toggleAuto.setChecked(false);
		} else {

			toggleManual.setChecked(false);
			toggleAuto.setChecked(true);
		}

		toggleManual.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					toggleAuto.setChecked(false);
					cfgAutoSync.set_value(0);
					db.updateConfig(cfgAutoSync);
				} else {
					toggleAuto.setChecked(true);
					cfgAutoSync.set_value(1);
					db.updateConfig(cfgAutoSync);
				}
			}
		});

		toggleAuto.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					toggleManual.setChecked(false);
					cfgAutoSync.set_value(1);
					db.updateConfig(cfgAutoSync);
					
					((MainActivity)getActivity()).autoSyncDropbox();

				} else {
					toggleManual.setChecked(true);
					cfgAutoSync.set_value(0);
					db.updateConfig(cfgAutoSync);
				}
			}

		});

		// listener for RadioGroup Java Android example
		mRadioGroup = (RadioGroup) rootView.findViewById(R.id.radioGroupSync);

		radAllCalls = (RadioButton) rootView
				.findViewById(R.id.radButton_Allcalls);
		radFavorites = (RadioButton) rootView
				.findViewById(R.id.radButton_Favorites);
		// Log.d("CHECKED","Init Radio checked = "+cfg3.get_value());
		if (cfgTypeSync.get_value() == 0) {
			radAllCalls.setChecked(true);

		} else {

			radFavorites.setChecked(true);
		}
		mRadioGroup
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						// Log.v("Selected", "New radio item selected: " +
						// checkedId);
						switch (checkedId) {
						case R.id.radButton_Allcalls:
							
							cfgTypeSync.set_value(0);
							db.updateConfig(cfgTypeSync);
							// do something
							break;

						case R.id.radButton_Favorites:
							cfgTypeSync.set_value(1);
							db.updateConfig(cfgTypeSync);
							// do something

							break;
						}
					}

				});

		// Run clean up button
		final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		btRunCleanUp = (Button) rootView.findViewById(R.id.btRunCleanUp);
		btRunCleanUp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				builder.setMessage("This action clears all stored audio files (except for the backup file in dropbox account). Are you sure ?")
						.setNegativeButton("No", dialogClickListener)
						.setPositiveButton("Yes", dialogClickListener).show();
			}
		});

		return rootView;
	}

	public void initDropboxApi() {

		if (mDropboxApi.getDbxAccountManager().hasLinkedAccount()) {

			btLink.setBackgroundResource(R.drawable.selector_synctab_btlink);

			btUnlink.setBackgroundResource(R.drawable.selector_synctab_btunlink);

		} else {

			btLink.setBackgroundResource(R.drawable.selector_synctab_btunlink);
			btUnlink.setBackgroundResource(R.drawable.selector_synctab_btlink);
		}
	}

	private void setEventClickButton() {

		btLink.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (mDropboxApi == null) {
					mDropboxApi = new DropboxApi(mContext);
					mDropboxApi.registerAccountDropbox();

				}

				if (mDropboxApi.getDbxAccountManager() != null) {
					if (!mDropboxApi.getDbxAccountManager().hasLinkedAccount()) {
						mDropboxApi.getDbxAccountManager().startLink(
								(Activity) mContext,
								Constant.REQUEST_LINK_TO_DBX_SYNCTODROPBOX);

					}
				}

				mDropboxApi.linkAccountToFileFS();
			}
		});

		btUnlink.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				btUnlink.setBackgroundResource(R.drawable.selector_synctab_btlink);

				btLink.setBackgroundResource(R.drawable.selector_synctab_btunlink);

				if (mDropboxApi != null) {
					if (mDropboxApi.getDbxAccountManager().hasLinkedAccount()) {

						mDropboxApi.getDbxAccountManager().unlink();

						mDropboxApi = null;

					}
				}
			}
		});

	}

	public void onActivityReSultMe() {
		mDropboxApi.linkAccountToFileFS();

		btUnlink.setBackgroundResource(R.drawable.selector_synctab_btunlink);

		btLink.setBackgroundResource(R.drawable.selector_synctab_btlink);

	}

	final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case DialogInterface.BUTTON_POSITIVE:
				// Yes
				String filepath = Environment.getExternalStorageDirectory()
						.getPath();
				File file = new File(filepath, "softtech/allcalls");
				File file2 = new File(filepath, "softtech/favorites");
				// Log.d("File", "File to delete = "+file.getAbsolutePath());
				deleteNon_EmptyDir(file);
				deleteNon_EmptyDir(file2);
				break;
			case DialogInterface.BUTTON_NEGATIVE:
				// No button clicked
				break;
			}
		}
	};

	public static boolean deleteNon_EmptyDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteNon_EmptyDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		return dir.delete();
	}
}
