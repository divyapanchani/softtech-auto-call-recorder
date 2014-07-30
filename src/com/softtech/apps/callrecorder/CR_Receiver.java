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
	private List<Contact> blackList;
	
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
        blackList = db.getAllContacts();
        cfg = db.getAllConfigs();
        db.close();
        Config cc = cfg.get(0);
        Config aq = cfg.get(1);
        // Check mounted SdCard
        
		Log.d("RECEIVER", "Value = "+cc.get_value());
		//Log.d("RECEIVER", "Boolean = "+ silent);
		// && MainActivity.updateExternalStorageState() == MainActivity.MEDIA_MOUNTED
		Log.d("MOUNTED","Update internal stroge from main activity ="+MainActivity.updateExternalStorageState());
		Log.d("MOUNTED","Update media mounted from main activity ="+MainActivity.MEDIA_MOUNTED);
		boolean mounted = false;
		if((MainActivity.updateExternalStorageState() == MainActivity.MEDIA_MOUNTED)){
			mounted = true;
		}
		Log.d("MOUNTED","Gia tri so sanh duoc ="+mounted);
		
		if (silent && cc.get_value()==1 && mounted)
		{
			if (phoneNumber == null)
			{
				if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) 
				{
					Log.d(tag,"#############Bat day nhan cuoc goi");
					Intent myIntent = new Intent(context, CR_RecordService.class);
					myIntent.putExtra("commandType", STATE_CALL_START);
					myIntent.putExtra("phoneNumber",  phoneNumber);
					myIntent.putExtra("audioQuality",aq.get_value());
					context.startService(myIntent);
				}
				else if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_IDLE)) 
				{
					Log.d(tag,"#############Ket thuc cuoc goi");
					Intent myIntent = new Intent(context, CR_RecordService.class);
					myIntent.putExtra("commandType", STATE_CALL_END);
					context.startService(myIntent);
					
					
				}
				else if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_RINGING)) 
				{
					Log.d(tag,"#########Bat dau do chuong");
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
				Log.d("START_INTENT", "Vao day roi");
				if(!checkBlackList(blackList,phoneNumber)){
					Log.d("START_INTENT", "Start intent here");
					Intent myIntent = new Intent(context, CR_RecordService.class);
					myIntent.putExtra("commandType", TelephonyManager.EXTRA_INCOMING_NUMBER);
					myIntent.putExtra("phoneNumber",  phoneNumber);
					context.startService(myIntent);
				}
				
			}
			
		}
	}
	
	public boolean checkBlackList(List<Contact> blackList,String phoneNum){
		if(phoneNum.equals("") || phoneNum.equals(null) || phoneNum == "")
			return false;
		for(Contact contact: blackList){
        	if(contact.get_phone_number().contains(phoneNum)){
        		//Log.d("BLACKLIST","Thoi xong nam trong black list CMNR");
        		return true;
        	}
        }
		return false;
	}


}
