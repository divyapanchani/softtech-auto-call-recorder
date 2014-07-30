package com.softtech.apps.callrecorder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomListVoiceAdapter extends BaseAdapter{
	
	Context context;
	private static final String AUDIO_RECORDER_FOLDER = "allcalls";
	private static final String AUDIO_RECORDER_FOLDER_FAVORITES = "favorites";
	static List<RowVoiceRecorded> rowVoiceRecorded = new ArrayList<RowVoiceRecorded>();
	
	private File folder;
	private File files[];
	
	private File folder_favorite;
	private File files__favorites[];
	
	
	public CustomListVoiceAdapter(Context context,int type) {
		// TODO Auto-generated constructor stub
		this.context = context;
		
		//Log.d("ADAPTER","Type = "+type);
		
		RowVoiceRecorded voice = null;
		rowVoiceRecorded.clear();
		// Read all favorites file
		String filepath_favorite = Environment.getExternalStorageDirectory().getPath();
		folder_favorite = new File(filepath_favorite,"softtech/" + AUDIO_RECORDER_FOLDER_FAVORITES);
		if (!folder_favorite.exists()) {
			folder_favorite.mkdirs();
		}
		files__favorites = folder_favorite.listFiles();
		if (!files__favorites.equals(null)) {
			for (File a : files__favorites) {
				voice = new RowVoiceRecorded(a.getName(),a.getAbsolutePath(),a.lastModified(),50);
				rowVoiceRecorded.add(voice);
			}
		}
		
		
		// Read all file in folder and add it into listview
		if(type == 0){
			String filepath = Environment.getExternalStorageDirectory().getPath();
			folder = new File(filepath, "softtech/" + AUDIO_RECORDER_FOLDER);
			if (!folder.exists()) {
				folder.mkdirs();
			}
			files = folder.listFiles();
			if (!files.equals(null)) {
				for (File a : files) {
					//int msec = MediaPlayer.create(context, Uri.fromFile(new File(a.getAbsolutePath()))).getDuration();
					// Xu ly voice name o day
					//String ss[] = a.getName().split("-");
					//Log.d("NAME","d = "+ss[0]+" p="+ss[1]);
					//Log.d("SIZE", "Tong so contact ="+listContact);
					//int index = getContact(listContact,ss[1]);
//					if(ss[1] != null && index != -1){
//						voice = new RowVoiceRecorded(listContact.get(index).get_name(),a.getAbsolutePath(),a.lastModified(),50);
//					}else{
//						voice = new RowVoiceRecorded("Unknown",a.getAbsolutePath(),a.lastModified(),50);
//					}
					voice = new RowVoiceRecorded(a.getName(),a.getAbsolutePath(),a.lastModified(),50);
					rowVoiceRecorded.add(voice);
				}
			}
		}
		
	}
	
	public Boolean removeItem(int position){
		String file_path = rowVoiceRecorded.get(position).getmPath();
		File file = new File(file_path);
		boolean deleted = file.delete();
		if(deleted){
			rowVoiceRecorded.remove(position);
			notifyDataSetChanged();
		}
		return deleted;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return rowVoiceRecorded.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return rowVoiceRecorded.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return rowVoiceRecorded.indexOf(getItem(position));
	}

	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {
		// TODO Auto-generated method stub
		
		if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.list_voice_custom, null);
        }
		
        ImageView imgAvatar = (ImageView) convertView.findViewById(R.id.imgAvatar);
        TextView contactName = (TextView) convertView.findViewById(R.id.tvContactName);
        TextView dateTime = (TextView) convertView.findViewById(R.id.tvPhoneNumber);
        TextView duration = (TextView) convertView.findViewById(R.id.tvDuration);

        RowVoiceRecorded row_pos = rowVoiceRecorded.get(position);
        // setting the image resource and title
        imgAvatar.setImageResource(R.drawable.home_noavatar_male);
        contactName.setText(row_pos.getmName());
        dateTime.setText(row_pos.getmTimeCreate().toString());
        //duration.setText(row_pos.getmDuration());
       
        
        return convertView;
		
	}
	
	void dropData(){
		rowVoiceRecorded.removeAll(rowVoiceRecorded);
		notifyDataSetChanged();
	}
	
}
