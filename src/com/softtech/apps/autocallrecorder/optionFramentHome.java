package com.softtech.apps.autocallrecorder;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.ViewFlipper;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.softtech.apps.constant.Constant;

@SuppressLint({ "NewApi", "ValidFragment" })
public class optionFramentHome extends Fragment {
	private AdView adView;
	private Button btAllCalls, btFavorites;

	private int positionTab = 0;
	private ViewFlipper mViewFlipper;

	private Context mContext;
	private MediaPlayer mediaPlayer;
	Button start, pause, stop;
	Dialog dialog;
	ToggleButton btnSpeaker;

	private SeekBar volumeControl = null;
	Handler seekHandler = new Handler();
	int progressChanged = 0;

	private static CustomListVoiceAdapter voiceAdapter;
	int selected_item = 0;

	ListView lvAllcalls, lvFavorites;

	SearchView searchView;
	private TextView tvFromContact, tvDuration;
	private InterstitialAd interstitial;
	public optionFramentHome() {
		super();
	}

	@SuppressLint("ValidFragment")
	public optionFramentHome(Context context) {
		super();
		
		interstitial = new InterstitialAd(context);
		interstitial.setAdUnitId("ca-app-pub-2100208056165316/2543069585");
		AdRequest ads = new AdRequest.Builder().build();
		interstitial.loadAd(ads);
		interstitial.setAdListener(new AdListener() {
			@Override
			public void onAdClosed() {
				AdRequest ads = new AdRequest.Builder().build();
				interstitial.loadAd(ads);
				super.onAdClosed();
			}
		});
	}

	@Override
	public void onAttach(Activity activity) {

		super.onAttach(activity);
		mContext = activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.home, container, false);

		mViewFlipper = (ViewFlipper) rootView.findViewById(R.id.view_flipper);

		btAllCalls = (Button) rootView.findViewById(R.id.btAllCalls);

		btFavorites = (Button) rootView.findViewById(R.id.btFavorites);

		lvAllcalls = (ListView) rootView.findViewById(R.id.lv_allcalls);

		lvFavorites = (ListView) rootView.findViewById(R.id.lv_favorites);
		adView = (AdView) rootView.findViewById(R.id.adView);
		if (hasConnections()) {
			// Look up the AdView as a resource and load a request
			// AdRequest adRequest = new AdRequest.Builder().build();
			// adView.loadAd(adRequest);
		}
		if (positionTab == 0) {

			btAllCalls
					.setBackgroundResource(R.drawable.selector_hometab_btselected);
		} else {
			btFavorites
					.setBackgroundResource(R.drawable.selector_hometab_btselected);
			Log.d("Tag", "Tab 0 click");
		}

		btAllCalls.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if (positionTab != 0) {
					positionTab = 0;

					initAdapter(positionTab);
					btFavorites
							.setBackgroundResource(R.drawable.selector_hometab_btdefault);
					btAllCalls
							.setBackgroundResource(R.drawable.selector_hometab_btselected);

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

				if (positionTab != 1) {
					positionTab = 1;

					initAdapter(positionTab);

					btFavorites
							.setBackgroundResource(R.drawable.selector_hometab_btselected);
					btAllCalls
							.setBackgroundResource(R.drawable.selector_hometab_btdefault);
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

		super.onActivityCreated(savedInstanceState);

		AsyncTask<Void, Void, Void> aa = new AsyncTask<Void, Void, Void>() {

			ProgressDialog progressDialog = null;

			@Override
			protected Void doInBackground(Void... params) {

				initLoadAdapter(positionTab);
				return null;
			}

			@Override
			protected void onPreExecute() {

				super.onPreExecute();
				if (mContext == null) {
					progressDialog = new ProgressDialog(
							optionFramentHome.this.getActivity());
				} else {
					progressDialog = new ProgressDialog(mContext);
				}

				progressDialog.setMessage("processing...");
				progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				progressDialog.setIndeterminate(true);

				progressDialog.show();

			}

			@Override
			protected void onPostExecute(Void result) {

				super.onPostExecute(result);
				finishLoadAdapter(positionTab);
				progressDialog.dismiss();
			}

		};

		aa.execute(null, null, null);

	}

	public void initAdapter(int type) {

		if (voiceAdapter != null && !voiceAdapter.equals(null)
				&& voiceAdapter.getCount() > 0) {
			voiceAdapter.dropData();
		}

		if (type == 0) {
			voiceAdapter = new CustomListVoiceAdapter(mContext, 0);

			lvAllcalls.setAdapter(voiceAdapter);

			registerForContextMenu(lvAllcalls);

			lvAllcalls.setOnItemClickListener(myclick);

			lvAllcalls.setOnCreateContextMenuListener(this);

			Log.d("TYPE", "Vao day roi #############");

		} else if (type == 1) {
			// list Favorites
			// list Favorites -> only read in "favorites" folder
			voiceAdapter = new CustomListVoiceAdapter(mContext, 1);

			lvFavorites.setAdapter(voiceAdapter);

			registerForContextMenu(lvFavorites);

			lvFavorites.setOnItemClickListener(myclick);

			lvFavorites.setOnCreateContextMenuListener(this);
		}
		Log.d("TONG", "Total rows = " + voiceAdapter.getCount());

		voiceAdapter.notifyDataSetChanged();
		lvAllcalls.invalidate();
		lvFavorites.invalidate();
	}

	public void initLoadAdapter(int type) {

		// if (voiceAdapter != null && !voiceAdapter.equals(null)
		// && voiceAdapter.getCount() > 0) {
		// voiceAdapter.dropData();
		// }

		if (type == 0) {
			voiceAdapter = new CustomListVoiceAdapter(mContext, 0);
			Log.d("TYPE", "Vao day roi #############");
		} else if (type == 1) {
			// list Favorites
			// list Favorites -> only read in "favorites" folder
			voiceAdapter = new CustomListVoiceAdapter(mContext, 1);
		}
	}

	public void finishLoadAdapter(int type) {

		if (type == 0) {
			lvAllcalls.setAdapter(voiceAdapter);

			registerForContextMenu(lvAllcalls);

			lvAllcalls.setOnItemClickListener(myclick);

			lvAllcalls.setOnCreateContextMenuListener(this);

			Log.d("TYPE", "Vao day roi #############");

		} else if (type == 1) {
			// list Favorites
			// list Favorites -> only read in "favorites" folder
			lvFavorites.setAdapter(voiceAdapter);

			registerForContextMenu(lvFavorites);

			lvFavorites.setOnItemClickListener(myclick);

			lvFavorites.setOnCreateContextMenuListener(this);
		}

		Log.d("TONG", "Total rows = " + voiceAdapter.getCount());

		voiceAdapter.notifyDataSetChanged();
		lvAllcalls.invalidate();
		lvFavorites.invalidate();
	}

	private OnItemClickListener myclick = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {

			if (CustomListVoiceAdapter.rowVoiceRecorded == null
					|| CustomListVoiceAdapter.rowVoiceRecorded.equals(null)
					|| CustomListVoiceAdapter.rowVoiceRecorded.size() == 0) {
				return;
			}

			if (dialog != null && dialog.isShowing()) {
				return;
			}

			RowVoiceRecorded itemClicked = CustomListVoiceAdapter.rowVoiceRecorded
					.get(arg2);
			final String path = itemClicked.getmPath();

			Log.d("ITEM", "on item, click");
			final AudioManager am = (AudioManager) getActivity()
					.getSystemService(Context.AUDIO_SERVICE);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

			dialog = new Dialog(getActivity(), R.style.mydialogstyle);
			dialog.getWindow();
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setContentView(R.layout.media_player);
			dialog.setCanceledOnTouchOutside(true);
			dialog.setCancelable(true);
			dialog.setOnDismissListener(new OnDismissListener() {

				@Override
				public void onDismiss(DialogInterface dialog) {

					am.setSpeakerphoneOn(false);
					volumeControl = null;
					if (mediaPlayer != null) {
						mediaPlayer.stop();
						mediaPlayer.release();
						mediaPlayer = null;
					}
					
					
					Random rand = new Random();
					if(rand.nextInt(2) == 1){
						interstitial.show();	
					}
				}
			});

			try {
				mediaPlayer.setDataSource(path);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				mediaPlayer.prepare();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			start = (Button) dialog.findViewById(R.id.btnStart);
			pause = (Button) dialog.findViewById(R.id.btnPause);
			stop = (Button) dialog.findViewById(R.id.btnStop);
			btnSpeaker = (ToggleButton) dialog.findViewById(R.id.btnSpeaker);
			tvFromContact = (TextView) dialog.findViewById(R.id.tvFromContent);
			tvDuration = (TextView) dialog.findViewById(R.id.tvDurationContent);
			tvFromContact.setText(itemClicked.getmName());
			tvDuration.setText(getTimeString(mediaPlayer.getDuration()));
			// Seek bar volume control
			volumeControl = (SeekBar) dialog.findViewById(R.id.seekBar);
			volumeControl
					.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

						public void onProgressChanged(SeekBar seekBar,
								int progress, boolean fromUser) {
							progressChanged = progress;
						}

						public void onStartTrackingTouch(SeekBar seekBar) {

						}

						public void onStopTrackingTouch(SeekBar seekBar) {

						}
					});

			start.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mediaPlayer.start();
					am.setSpeakerphoneOn(true);
					int volume_level = am
							.getStreamVolume(AudioManager.STREAM_MUSIC);
					int max_volume = am
							.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
					if (volume_level < max_volume) {
						am.setStreamVolume(AudioManager.STREAM_MUSIC,
								max_volume, AudioManager.FLAG_SHOW_UI);
					}
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

			btnSpeaker
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {

							if (isChecked) {
								btnSpeaker.setChecked(true);
								am.setSpeakerphoneOn(true);
							} else {
								btnSpeaker.setChecked(false);
								am.setSpeakerphoneOn(false);
							}
						}
					});

			dialog.show();

		}
	};

	private String getTimeString(long millis) {
		StringBuffer buf = new StringBuffer();

		int hours = (int) (millis / (1000 * 60 * 60));
		int minutes = (int) ((millis % (1000 * 60 * 60)) / (1000 * 60));
		int seconds = (int) (((millis % (1000 * 60 * 60)) % (1000 * 60)) / 1000);

		buf.append(String.format("%02d", hours)).append(":")
				.append(String.format("%02d", minutes)).append(":")
				.append(String.format("%02d", seconds));

		return buf.toString();
	}

	public void seekUpdation() {
		if (volumeControl != null && mediaPlayer != null) {
			volumeControl.setProgress(mediaPlayer.getCurrentPosition());
			seekHandler.postDelayed(run, 50);
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
		final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();
		Log.d("CONTEXT", "on context item selected = " + info.position);
		switch (item.getItemId()) {
		case R.id.action_delete:
			Log.d("SELECTED", "Delete");
			Boolean xoa = voiceAdapter.removeItem(info.position);
			initAdapter(positionTab);
			Log.d("XOA", "Da xoa = " + xoa);
			return true;
			/*
			 * case R.id.action_backup:
			 * 
			 * if (mDropboxApi != null &&
			 * !mDropboxApi.getDbxAccountManager().hasLinkedAccount()) {
			 * 
			 * AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
			 * builder.setTitle("Login Required !"); builder.setMessage(
			 * "To use this feature requires you to sign in with your dropbox account. Choose \"Yes\" button to sign or \"No\" button to cancel !"
			 * ) .setNegativeButton("No", dialogClickListener)
			 * .setPositiveButton("Yes", dialogClickListener).show();
			 * 
			 * } else {
			 * 
			 * Object object = voiceAdapter.getItem(info.position);
			 * 
			 * final RowVoiceRecorded rowVoiceRecorded = (RowVoiceRecorded)
			 * object;
			 * 
			 * String pathString = rowVoiceRecorded.getmPath();
			 * 
			 * if (pathString.contains(Constant.ISSYNC0)) {
			 * mDropboxApi.createFolderSofftech();
			 * 
			 * mDropboxApi.linkAccountToFileFS();
			 * 
			 * int type = -1;
			 * 
			 * if (pathString.contains(Constant.FILE_ALLCALLS)) {
			 * 
			 * type = 0; } else if
			 * (pathString.contains(Constant.FILE_FAVORITES)) {
			 * 
			 * type = 1; }
			 * 
			 * final File fileSync = new File(pathString);
			 * 
			 * final int typeTmp = type;
			 * 
			 * AsyncTask<String, Void, String> netWork = new AsyncTask<String,
			 * Void, String>() {
			 * 
			 * @Override protected String doInBackground(String... urls) {
			 * String response = "";
			 * 
			 * for (String url : urls) { try { HttpURLConnection urlc =
			 * (HttpURLConnection) (new URL( url).openConnection());
			 * urlc.setRequestProperty("User-Agent", "Test");
			 * urlc.setRequestProperty("Connection", "close");
			 * urlc.setConnectTimeout(1500); urlc.connect(); response =
			 * String.valueOf(urlc .getResponseCode()); } catch (Exception e) {
			 * e.printStackTrace(); } }
			 * 
			 * return response; }
			 * 
			 * @Override protected void onPostExecute(String result) { if
			 * (result.length() > 0 && Integer.valueOf(result) == 200) { // do
			 * anything
			 * 
			 * mDropboxApi.syncFileToDropBoxFolder(typeTmp, fileSync);
			 * 
			 * View view = null;
			 * 
			 * int position = -1; if (typeTmp == 0) { position = info.position -
			 * lvAllcalls .getFirstVisiblePosition();
			 * 
			 * if (position <= 0) { position = lvAllcalls
			 * .getFirstVisiblePosition(); }
			 * 
			 * view = lvAllcalls.getChildAt(position); } else { position =
			 * info.position - lvFavorites .getFirstVisiblePosition();
			 * 
			 * if (position <= 0) { position = lvFavorites
			 * .getFirstVisiblePosition(); }
			 * 
			 * view = lvFavorites.getChildAt(position); }
			 * 
			 * if (view != null) { ImageView imgView = (ImageView) view
			 * .findViewById(R.id.imgCloud);
			 * 
			 * imgView.setImageResource(R.drawable.home_cloud); }
			 * 
			 * rowVoiceRecorded.setIsSync(true);
			 * 
			 * } else { AlertDialog.Builder builder = new AlertDialog.Builder(
			 * mContext); builder.setTitle("Internet Connection Error !");
			 * builder.setMessage( "Please check for internet connection !")
			 * .setNegativeButton("Ok", dialogInternetClickListener) .show(); }
			 * }
			 * 
			 * };
			 * 
			 * if (hasConnections()) { netWork.execute(new String[] {
			 * "http://www.google.com" }); } else { AlertDialog.Builder builder
			 * = new AlertDialog.Builder( mContext);
			 * builder.setTitle("Internet Connection Error !");
			 * builder.setMessage( "Please check for internet connection !")
			 * .setNegativeButton("Ok", dialogInternetClickListener).show(); } }
			 * } return true;
			 */
		case R.id.action_share:
			Log.d("SELECTED", "Share");
			Object a = voiceAdapter.getItem(info.position);
			RowVoiceRecorded row = (RowVoiceRecorded) a;
			String file_path = row.getmPath();
			// Share this to social network
			Intent shareIntent = new Intent();
			shareIntent.setAction(Intent.ACTION_SEND);
			shareIntent.putExtra(Intent.EXTRA_TEXT, "http://softtech.vn");
			// Log.e(TAG , "phat file ="+PATH_APP +"/" + TEMP_FILE );
			shareIntent.putExtra(Intent.EXTRA_STREAM,
					Uri.fromFile(new File(file_path))); // doi cai path
			shareIntent.setType("audio/mp3"); // set lai cai type
			shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			startActivity(Intent.createChooser(shareIntent, "Share with"));
			return true;
		case R.id.action_favorite:
			Log.d("SELECTED", "Favorite");
			Object a2 = voiceAdapter.getItem(info.position);
			RowVoiceRecorded row2 = (RowVoiceRecorded) a2;
			String file_path2 = row2.getmPath();
			String output = file_path2.replaceAll("/" + Constant.FILE_ALLCALLS
					+ "/", "/" + Constant.FILE_FAVORITES + "/");
			Log.d("FILEPATH", "Duong dan file Path = " + file_path2);
			Log.d("OUTPUT", "Duong dan output = " + output);
			boolean moved = moveFile(file_path2, output);
			if (moved != true) {
				Log.d("MOVED", "File has moved");
			} else {
				Log.d("MOVED", "File hasn't moved yet !!");
			}
			initAdapter(positionTab);
			return true;
		default:
			return false;
		}
	}

	private DialogInterface.OnClickListener dialogInternetClickListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case DialogInterface.BUTTON_NEGATIVE:
				// No button clicked
				break;
			}
		}
	};

//	private DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
//		@Override
//		public void onClick(DialogInterface dialog, int which) {
//			switch (which) {
//			case DialogInterface.BUTTON_POSITIVE:
//				// Yes
//				mDropboxApi.getDbxAccountManager().startLink(
//						(Activity) mContext,
//						Constant.REQUEST_LINK_TO_DBX_optionFramentHome);
//				break;
//			case DialogInterface.BUTTON_NEGATIVE:
//				// No button clicked
//				break;
//			}
//		}
//	};

	public void onActivityReSultMe() {

	}

	public static final Handler handle = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			Bundle b = msg.getData();
			String dieukien = b.getString("text");
			Log.d("FRAGMENT", "Da nhan duoc chuoi =" + dieukien);
			voiceAdapter.getFilter().filter(dieukien);
		}
	};

	public boolean moveFile(String oldfilename, String newFolderPath) {
		File file = new File(oldfilename);
		File file2 = new File(newFolderPath);
		boolean success = false;
		if (!file2.exists()) {
			success = file.renameTo(file2) && file.delete();
		}
		return success;
	}

	public boolean hasConnections() {
		ConnectivityManager cm = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (null == ni)
			return false;
		return ni.isConnectedOrConnecting();
	}
}
