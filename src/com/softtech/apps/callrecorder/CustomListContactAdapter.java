package com.softtech.apps.callrecorder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

public class CustomListContactAdapter extends BaseAdapter {
	private List<Contact> listContact;
	private List<Contact> blackList;
	private Context context;

	private int mType;

	private Config cfgTypeRecord;

	// private ContactComparator mComparator;

	DatabaseHandler db;

	public CustomListContactAdapter(Context context) {
		// TODO Auto-generated constructor stub
		// List contact trong danh ba la co dinh
		listContact = new ArrayList<Contact>();
		this.context = context;

		db = new DatabaseHandler(context);

		getContacts();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listContact.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return listContact.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return listContact.indexOf(getItem(position));
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder = null;
		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater) context
					.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.list_contact_custom, null);

			viewHolder = new ViewHolder();

			viewHolder.imgAvatar = (ImageView) convertView
					.findViewById(R.id.imgAvatar);
			viewHolder.contactName = (TextView) convertView
					.findViewById(R.id.tvContactName);
			viewHolder.PhoneNumber = (TextView) convertView
					.findViewById(R.id.tvPhoneNumber);
			viewHolder.btnOnOff = (ToggleButton) convertView
					.findViewById(R.id.btnOnOff);

			viewHolder.position = position;

			convertView.setTag(viewHolder);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		cfgTypeRecord = db.getConfig(5);
		mType = cfgTypeRecord.get_value();
//		Log.d("LOG", "M type = " + mType);
		blackList = db.getContactsByType(mType);
//		Log.d("BLACKLIST", "BlackList Size init = " + blackList.size());
		viewHolder.btnOnOff.setOnCheckedChangeListener(null);
		Contact a = listContact.get(position);

		viewHolder.imgAvatar.setImageResource(R.drawable.home_noavatar_male);
		viewHolder.contactName.setText(a.get_name());
		viewHolder.PhoneNumber.setText(a.get_phone_number());

		final boolean checked = checkBlackList(blackList, a.get_phone_number());
		if(mType == 2)
		{
			if (checked) {
				viewHolder.btnOnOff.setChecked(true);
			} else {
				viewHolder.btnOnOff.setChecked(false);
			}
		}else{
			if (checked) {
				viewHolder.btnOnOff.setChecked(false);
			} else {
				viewHolder.btnOnOff.setChecked(true);
			}
		}
		
		// Here again set the listener as in your code..
		viewHolder.btnOnOff
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						Log.d("CHANGE", "On checked change");
						if(mType != 2){
							if (isChecked == false && !checked ) {
								// Them vao Blacklist
								blackList.removeAll(blackList);
								listContact.get(position).set_type(mType);
								db.addContact(listContact.get(position));
								blackList = db.getAllContacts();
								// Log.d("BLACKLIST","BlackList Size after add = "+blackList.size());
							} else {
								// Remove khoi blackList
								db.deleteContact(listContact.get(position));
								blackList.removeAll(blackList);
								blackList = db.getAllContacts();
								// Log.d("BLACKLIST","BlackList Size after remove = "+blackList.size());
							}
						}else{
							if (isChecked == true && !checked ) {
								// Them vao Blacklist
								blackList.removeAll(blackList);
								listContact.get(position).set_type(mType);
								db.addContact(listContact.get(position));
								blackList = db.getAllContacts();
								// Log.d("BLACKLIST","BlackList Size after add = "+blackList.size());
							} else {
								// Remove khoi blackList
								db.deleteContact(listContact.get(position));
								blackList.removeAll(blackList);
								blackList = db.getAllContacts();
								// Log.d("BLACKLIST","BlackList Size after remove = "+blackList.size());
							}
						}
							
						}

				});

		return convertView;
	}

	static class ViewHolder {
		ImageView imgAvatar;
		TextView contactName;
		TextView PhoneNumber;
		ToggleButton btnOnOff;
		int position;
	}

	public boolean checkBlackList(List<Contact> blackList, String phoneNum) {
		for (Contact contact : blackList) {
			if (contact.get_phone_number().contains(phoneNum)) {
				return true;
			}
		}
		return false;
	}

	public void getContacts() {
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
				Contact ct = new Contact(name, phone, contactId, 0);
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
