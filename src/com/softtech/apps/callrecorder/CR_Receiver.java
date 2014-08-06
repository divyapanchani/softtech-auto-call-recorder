package com.softtech.apps.callrecorder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.telephony.PhoneStateListener;
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
	private List<Contact> listContact;
	private List<Contact> blackList;
	int mType;
	private static String mPhoneNumber;
	public CR_Receiver() {
		// TODO Auto-generated constructor stub
		listContact = new ArrayList<Contact>();
		blackList = new ArrayList<Contact>();
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		SharedPreferences settings = context.getSharedPreferences(LISTEN_ENABLED, 0);
		boolean silent = settings.getBoolean("silentMode", true);
		phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
//		getContacts(context);
        db = new DatabaseHandler(context);
        cfg = db.getAllConfigs();
        db.close();
        Config cc = cfg.get(0);
        Config aq = cfg.get(1);
        Config cfgType = cfg.get(4);
        mType = cfgType.get_value();
        blackList = db.getContactsByType(mType);
        getContacts(context);
		Log.d("CONTACT", "Contact size = "+listContact.size());
		Log.d("CONTACT", "Blacklist size = "+blackList.size());
        // Check mounted SdCard
		boolean mounted = false;
		if((MainActivity.updateExternalStorageState() == MainActivity.MEDIA_MOUNTED)){
			mounted = true;
		}
		//Log.d("MOUNTED","Gia tri so sanh duoc ="+mounted);
		if (silent && cc.get_value()==1 && mounted)
		{
			Log.d(tag, "Phone number = "+phoneNumber);
			if(phoneNumber != null){
				mPhoneNumber = phoneNumber;
			}
			if (phoneNumber == null)
			{
				if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) 
				{
					
						Log.d(tag,"############# OFFHOOK STATE");
						boolean isOK = false;
						Log.d(tag, "Phone number in Offhook = "+mPhoneNumber);
						if(mType == 0){
							// Khong thuoc blackList thi record
							isOK = !checkBlackList(blackList,mPhoneNumber);
						}else if(mType==1){
							isOK = !checkBlackList(blackList,mPhoneNumber) && checkContact(listContact,phoneNumber);
						}else{
							isOK = checkBlackList(blackList,mPhoneNumber) && !checkContact(listContact,phoneNumber);
						}
						if(isOK){
							Log.d("OK", "##################### OK");
							Intent myIntent = new Intent(context, CR_RecordService.class);
							myIntent.putExtra("commandType", STATE_CALL_START);
							myIntent.putExtra("phoneNumber",  phoneNumber);
							myIntent.putExtra("audioQuality",aq.get_value());
							context.startService(myIntent);
						}
					
				}
				else if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_IDLE)) 
				{
					Log.d(tag,"#############Ket thuc cuoc goi");
					Log.d(tag, "Phone number in Ket thuc = "+phoneNumber);
					Intent myIntent = new Intent(context, CR_RecordService.class);
					myIntent.putExtra("commandType", STATE_CALL_END);
					context.startService(myIntent);
					
					
				}
				else if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_RINGING)) 
				{
					Log.d(tag,"######### Bat dau do chuong");
					if (phoneNumber == null)
						phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
					
					if(phoneNumber!=null){
						mPhoneNumber = phoneNumber;
					}
					Log.d(tag, "Phone number in Ringing = "+mPhoneNumber);
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
	
	public boolean checkBlackList(List<Contact> blackList,String phoneNum){
		if(phoneNum==null || phoneNum.equals(null) || phoneNum.equals("") || phoneNum.trim().length()<=0)
			return false;
		for(Contact contact: blackList){
        	if(contact.get_phone_number().contains(phoneNum)){
        		//Log.d("BLACKLIST","Thoi xong nam trong black list CMNR");
        		return true;
        	}
        }
		return false;
	}
	
	public boolean checkContact(List<Contact> contactList,String phoneNum){
		if(phoneNum.equals("") || phoneNum.equals(null) || phoneNum == "")
			return false;
		for(Contact contact: contactList){
        	if(contact.get_phone_number().contains(phoneNum)){
        		//Log.d("BLACKLIST","Thoi xong nam trong contact list CMNR");
        		return true;
        	}
        }
		return false;
	}
	public void getContacts(Context context) {
		// Get all contact here
		ContentResolver cr = context.getContentResolver();
		Cursor cur = cr.query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,
				null, null);
		cur.moveToFirst();

		while (cur.moveToNext()) {
			String contactId = cur.getString(cur
					.getColumnIndex(ContactsContract.Contacts._ID));

			// Log.e("contact id"," contact id="+contactId);

			String name = cur
					.getString(cur
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

			// String
			// phone=cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

			String hasPhone = null;

			int hasphone = -1;
			try {
				hasPhone = cur
						.getString(cur
								.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
				hasphone = Integer.parseInt(hasPhone);
				// Log.e("contactID",contactId);
			} catch (Exception ex) {

				// Log.e("contactID",contactId);
			}

			if (hasphone > 0) {
				// Log.d("CONTACT", "Has phone number from SIM CARD");
				String phone = cur
						.getString(cur
								.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				Contact ct = new Contact(name, phone, contactId,0);
				listContact.add(ct);
			} else {

			}

			Comparator<Contact> a = new Comparator<Contact>() {

				@Override
				public int compare(Contact lhs, Contact rhs) {
					// TODO Auto-generated method stub
					return lhs.get_name().compareTo(rhs.get_name());
				}
			};

			Collections.sort(listContact, a);
		}
	}
}
