package com.softtech.apps.dropbox;

import java.util.List;

import com.softtech.apps.callrecorder.Config;
import com.softtech.apps.callrecorder.DatabaseHandler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AutoUploadRecord extends BroadcastReceiver{
	
	DatabaseHandler db;
	
	private static List<Config> cfg;
	@Override
	public void onReceive(Context arg0, Intent arg1) {
		// TODO Auto-generated method stub
		
        cfg = db.getAllConfigs();
        
        db.close();
        
        Config isAutoSync = cfg.get(2);
		
	}

}
