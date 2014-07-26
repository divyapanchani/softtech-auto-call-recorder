package com.softtech.apps.callrecorder;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CR_Receiver extends BroadcastReceiver{

	public static final String LISTEN_ENABLED = "ListenEnabled";
	public static final String FILE_DIRECTORY = "softtech";
	private String phoneNumber;
	public static final int STATE_INCOMING_NUMBER = 0;
	public static final int STATE_CALL_START = 1;
	public static final int STATE_CALL_END = 2;
	
	DatabaseHandler db;
	private static List<Config> cfg;
	
	String tag = "TAG";
	
	public CR_Receiver() {
		// TODO Auto-generated constructor stub
		
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		SharedPreferences settings = context.getSharedPreferences(LISTEN_ENABLED, 0);
		boolean silent = settings.getBoolean("silentMode", true);
		phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
		
        db = new DatabaseHandler(context);
        cfg = db.getAllConfigs();
        db.close();
        Config cc = cfg.get(0);
        Config aq = cfg.get(1);
        
		Log.d("RECEIVER", "Value = "+cc.get_value());
		//Log.d("RECEIVER", "Boolean = "+ silent);
		// && MainActivity.updateExternalStorageState() == MainActivity.MEDIA_MOUNTED
		if (silent && cc.get_value()==1)
		{
			if (phoneNumber == null)
			{
				if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) 
				{
					Log.d(tag,"Bat day nhan cuoc goi");
					Intent myIntent = new Intent(context, CR_RecordService.class);
					myIntent.putExtra("commandType", STATE_CALL_START);
					myIntent.putExtra("phoneNumber",  phoneNumber);
					myIntent.putExtra("audioQuality",aq.get_value());
					context.startService(myIntent);
				}
				else if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_IDLE)) 
				{
					Log.d(tag,"Ket thuc cuoc goi");
					Intent myIntent = new Intent(context, CR_RecordService.class);
					myIntent.putExtra("commandType", STATE_CALL_END);
					context.startService(myIntent);
					
					
				}
				else if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_RINGING)) 
				{
					Log.d(tag,"Bat dau do chuong");
					if (phoneNumber == null)
						phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
					Intent myIntent = new Intent(context, CR_RecordService.class);
					myIntent.putExtra("commandType", STATE_INCOMING_NUMBER);
					myIntent.putExtra("phoneNumber",  phoneNumber);
					context.startService(myIntent);
					
				}
			}
			else
			{
				Intent myIntent = new Intent(context, CR_RecordService.class);
				myIntent.putExtra("commandType", TelephonyManager.EXTRA_INCOMING_NUMBER);
				myIntent.putExtra("phoneNumber",  phoneNumber);
				context.startService(myIntent);
			}
			
		}
	}

}
