package com.softtech.apps.autocallrecorder;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

// This class to excute record incomming call when receiver receive an incomming call
public class CR_RecordService extends Service {

	public static final String LISTEN_ENABLED = "ListenEnabled";
	public static final String FILE_DIRECTORY = "softtech";
	private MediaRecorder recorder = null;
	private String phoneNumber = null;
	private String nameContact = "Unknown";
	public static final int STATE_INCOMING_NUMBER = 0;
	public static final int STATE_CALL_START = 1;
	public static final int STATE_CALL_END = 2;

	private NotificationManager manger;
	private String myFileName;
	private Boolean is_offhook = false;
	private int notificationID = 100;
	AudioManager am; // Audio manager
	
	public CR_RecordService() {
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		int commandType = 0;
		if(intent != null){
			commandType = intent.getIntExtra("commandType",
				STATE_INCOMING_NUMBER);
		}
		if (commandType == STATE_INCOMING_NUMBER) {
			if (phoneNumber == null)
				phoneNumber = intent.getStringExtra("phoneNumber");
		} else if (commandType == STATE_CALL_START) {
			if (phoneNumber == null)
				phoneNumber = intent.getStringExtra("phoneNumber");
				nameContact = intent.getStringExtra("nameContact");
				if(nameContact == null)
					nameContact = "Unknown";
			startRecord();

		} else if (commandType == STATE_CALL_END) {
			try {

				if (is_offhook == true) {
					stopRecord();
				}
				Toast toast = Toast.makeText(this,
						this.getString(R.string.reciever_end_call),
						Toast.LENGTH_SHORT);
				toast.show();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}
			if (manger != null)
				manger.cancel(0);
			stopForeground(true);
			this.stopSelf();
		}
		return super.onStartCommand(intent, flags, startId);
	}

	private void startRecord() {
		recorder = new MediaRecorder();
		try {
			Log.d("RECORD", "------> Chuan bi moi thu den ghi am");
//			recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);
//			recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//			recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//			recorder.setMaxFileSize(1024 * 1024 * 250); // 250 MB
			recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
			recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
			myFileName = getFilename();
			recorder.setOutputFile(myFileName);
			am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			am.setMode(AudioManager.MODE_IN_CALL);
			int volume_level = am.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
			int max_volume = am.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);
			if(volume_level < max_volume){
				am.setStreamVolume(AudioManager.STREAM_VOICE_CALL,max_volume, AudioManager.FLAG_SHOW_UI);
			}
		} catch (IllegalStateException e) {
			// Log.e("Call recorder IllegalStateException: ", "");
			terminateAndEraseFile();
		} catch (Exception e) {
			// Log.e("Call recorder Exception: ", "");
			terminateAndEraseFile();
		}
		
		OnErrorListener errorListener = new OnErrorListener() {

			public void onError(MediaRecorder arg0, int arg1, int arg2) {
				arg0.stop();
				arg0.reset();
				arg0.release();
				arg0 = null;
				terminateAndEraseFile();
			}

		};
		recorder.setOnErrorListener(errorListener);

		try {
			recorder.prepare();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			recorder.start();
			is_offhook = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		Toast toast = Toast.makeText(this,
				this.getString(R.string.reciever_start_call),
				Toast.LENGTH_SHORT);
		toast.show();
		createNotification(phoneNumber);
	}

	private void stopRecord() {
		if (recorder != null) {
			recorder.setOnErrorListener(null);
			recorder.setOnInfoListener(null);
			recorder.stop();
			recorder.reset();
			recorder.release();
			recorder = null;
			is_offhook = false;
		}
		// System.gc();
	}

	/**
	 * in case it is impossible to record Check device can surport record
	 */
	private void terminateAndEraseFile() {
		try {

			stopRecord();

			Toast toast = Toast.makeText(this,
					this.getString(R.string.reciever_end_call),
					Toast.LENGTH_SHORT);
			toast.show();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
		File file = new File(myFileName);

		if (file.exists()) {
			file.delete();

		}
		Toast toast = Toast.makeText(this,
				this.getString(R.string.record_impossible), Toast.LENGTH_LONG);
		toast.show();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	private String getFilename() {
		// String filepath = getFilesDir().getAbsolutePath();
		String filepath = Environment.getExternalStorageDirectory().getPath();
		File file = new File(filepath, FILE_DIRECTORY);

		if (!file.exists()) {
			file.mkdirs();
		}

		String myDate = new String();
		myDate = (String) DateFormat.format("yyyyMMddkkmmss", new Date());

		return (file.getAbsolutePath() + "/allcalls/" + myDate + "-"
				+ phoneNumber + "-isSync0-.mp3");
	}

	@SuppressLint("NewApi")
	public void createNotification(String phoneNumber) {

		RemoteViews remoteViews = new RemoteViews(getPackageName(),
				R.layout.widget);
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this).setSmallIcon(R.drawable.icon).setContent(remoteViews);
		remoteViews.setTextViewText(R.id.tvNotificationTitle, "New recorded");
		String content = "with " + nameContact + "-" + phoneNumber;
		remoteViews.setTextViewText(R.id.tvNotificationContent, content);
		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(this, MainActivity.class);
		resultIntent.putExtra("me", "notify");
		// The stack builder object will contain an artificial back stack for
		// the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder
				.create(getApplicationContext());
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(MainActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
				PendingIntent.FLAG_UPDATE_CURRENT);
		remoteViews.setOnClickPendingIntent(R.id.tvNotificationTitle,
				resultPendingIntent);
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(notificationID, mBuilder.build());
	}

	public void removeNotification() {
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager nMgr = (NotificationManager) getBaseContext()
				.getSystemService(ns);
		nMgr.cancel(0);
	}

}
