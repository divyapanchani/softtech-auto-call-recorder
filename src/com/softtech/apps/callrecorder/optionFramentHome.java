package com.softtech.apps.callrecorder;

import java.io.File;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.ViewFlipper;

@SuppressLint({ "NewApi", "ValidFragment" })
public class optionFramentHome extends ListFragment implements
		OnItemClickListener {

	private Button btAllCalls, btFavorites;

	private static final String TAG = null;

	private int positionTab = 0;
	private ViewFlipper mViewFlipper;

	private Context mContext;
	private MediaPlayer mediaPlayer;
	Button start, pause, stop;
	Dialog dialog;

	private SeekBar volumeControl = null;
	Handler seekHandler = new Handler();
	int progressChanged = 0;

	private CustomListVoiceAdapter voiceAdapter;
	int selected_item = 0;

	@SuppressLint("ValidFragment")
	public optionFramentHome(Context context) {
		super();
		mContext = context;

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView = inflater.inflate(R.layout.home, container, false);

		mViewFlipper = (ViewFlipper) rootView.findViewById(R.id.view_flipper);

		btAllCalls = (Button) rootView.findViewById(R.id.btAllCalls);
		btFavorites = (Button) rootView.findViewById(R.id.btFavorites);

		if (positionTab == 0) {

			btAllCalls.setBackgroundResource(R.drawable.hometab_btselected);
		} else {
			btFavorites.setBackgroundResource(R.drawable.hometab_btselected);
			Log.d("Tag", "Tab 0 click");
		}

		btAllCalls.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				Log.d("Tag", "Tab 0 click");
				if (positionTab != 0) {
					positionTab = 0;

					initAdapter(positionTab);
					btFavorites
							.setBackgroundResource(R.drawable.hometab_btdefault);
					btAllCalls
							.setBackgroundResource(R.drawable.hometab_btselected);

					mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(
							mContext, R.anim.in_from_right));
					mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(
							mContext, R.anim.out_to_left));
					mViewFlipper.showNext();

				}
			}
		});

		btFavorites.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d("Tag", "Tab 1 click");
				if (positionTab != 1) {
					positionTab = 1;
					
					initAdapter(positionTab);
					
					btFavorites
							.setBackgroundResource(R.drawable.hometab_btselected);
					btAllCalls
							.setBackgroundResource(R.drawable.hometab_btdefault);

					mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(
							mContext, R.anim.in_from_left));
					mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(
							mContext, R.anim.out_to_right));
					mViewFlipper.showPrevious();
				}
			}
		});

		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		initAdapter(positionTab);
		
	}

	public void initAdapter(int type){
		setListAdapter(null);
		if (type == 0) {
			voiceAdapter = new CustomListVoiceAdapter(mContext, 1);
			setListAdapter(voiceAdapter);
			getListView().setOnItemClickListener(this);
			registerForContextMenu(getListView());
			getListView().setOnCreateContextMenuListener(this);
		} else {
			// list Favorites
			// list Favorites -> only read in "favorites" folder
			voiceAdapter = new CustomListVoiceAdapter(mContext, 2);
			Log.d("TOTAL", "Tong so file = " + voiceAdapter.getCount());
			setListAdapter(voiceAdapter);
			getListView().setOnItemClickListener(this);
			registerForContextMenu(getListView());
			getListView().setOnCreateContextMenuListener(this);
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		RowVoiceRecorded itemClicked = CustomListVoiceAdapter.rowVoiceRecorded
				.get(position);
		final String path = itemClicked.getmPath();

		Log.d("ITEM", "on item, click");
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

		dialog = new Dialog(getActivity());
		dialog.setContentView(R.layout.media_player);
		dialog.setCanceledOnTouchOutside(true);
		dialog.setCancelable(true);
		dialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				// TODO Auto-generated method stub
				volumeControl = null;
				mediaPlayer.stop();
				mediaPlayer.release();
				mediaPlayer = null;
			}
		});

		try {
			mediaPlayer.setDataSource(path);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			mediaPlayer.prepare();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		start = (Button) dialog.findViewById(R.id.btnStart);
		pause = (Button) dialog.findViewById(R.id.btnPause);
		stop = (Button) dialog.findViewById(R.id.btnStop);

		// Seek bar volume control
		volumeControl = (SeekBar) dialog.findViewById(R.id.seekBar);
		volumeControl.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				progressChanged = progress;
			}

			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}

			public void onStopTrackingTouch(SeekBar seekBar) {

			}
		});

		start.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mediaPlayer.start();
				volumeControl.setMax(mediaPlayer.getDuration());
				seekUpdation();
			}
		});
		pause.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mediaPlayer.pause();
			}
		});
		stop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		dialog.show();

	}

	public void seekUpdation() {
		if (volumeControl != null && mediaPlayer != null) {
			volumeControl.setProgress(mediaPlayer.getCurrentPosition());
			seekHandler.postDelayed(run, 1000);
		}

	}

	Runnable run = new Runnable() {

		@Override
		public void run() {
			seekUpdation();
		}
	};

	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getActivity().getMenuInflater();
		inflater.inflate(R.menu.dialog, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// find out which menu item was pressed
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();
		Log.d("CONTEXT", "on context item selected = " + info.position);
		switch (item.getItemId()) {
		case R.id.action_delete:
			Log.d("SELECTED", "Delete");
			Boolean xoa = voiceAdapter.removeItem(info.position);
			voiceAdapter.notifyDataSetChanged();
			Log.d("XOA", "Da xoa = " + xoa);
			return true;
		case R.id.action_backup:
			Log.d("SELECTED", "Backup");
			return true;
		case R.id.action_share:
			Log.d("SELECTED", "Share");
			Object a = voiceAdapter.getItem(info.position);
			RowVoiceRecorded row = (RowVoiceRecorded) a;
			String file_path = row.getmPath();
			// Share this to social network
			Intent shareIntent = new Intent();
			shareIntent.setAction(Intent.ACTION_SEND);
			shareIntent.putExtra(Intent.EXTRA_TEXT, "http://softtech.vn"); // sua
																			// cai
																			// text
																			// mong
																			// muon
			// Log.e(TAG , "phat file ="+PATH_APP +"/" + TEMP_FILE );
			shareIntent.putExtra(Intent.EXTRA_STREAM,
					Uri.fromFile(new File(file_path))); // doi cai path
			shareIntent.setType("audio/mp3"); // set lai cai type
			shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			startActivity(Intent.createChooser(shareIntent, "Share with"));
			return true;
		case R.id.action_favorite:
			Log.d("SELECTED", "Favorite");

			return true;
		default:
			return false;
		}
	}

}
