package com.softtech.apps.callrecorder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CustomListVoiceAdapter extends BaseAdapter{
	
	Context context;
	private static final String AUDIO_RECORDER_FOLDER = "softtech";
	static List<RowVoiceRecorded> rowVoiceRecorded = new ArrayList<RowVoiceRecorded>();
	
	private File folder;
	private File files[];
	
	public CustomListVoiceAdapter(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
		
		// Read all file in folder and add it into listview
		String filepath = Environment.getExternalStorageDirectory().getPath();
		folder = new File(filepath, AUDIO_RECORDER_FOLDER);
		if (!folder.exists()) {
			folder.mkdirs();
		}
		files = folder.listFiles();
		RowVoiceRecorded voice = null;
		if (!files.equals(null)) {
			for (File a : files) {
				//int msec = MediaPlayer.create(context, Uri.fromFile(new File(a.getAbsolutePath()))).getDuration(); 
				voice = new RowVoiceRecorded(a.getName(),a.getAbsolutePath(),a.lastModified(),50);
				rowVoiceRecorded.add(voice);
			}
		}
		
		
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
        TextView dateTime = (TextView) convertView.findViewById(R.id.tvDateTime);
        TextView duration = (TextView) convertView.findViewById(R.id.tvDuration);

        RowVoiceRecorded row_pos = rowVoiceRecorded.get(position);
        // setting the image resource and title
        imgAvatar.setImageResource(R.drawable.home_noavatar_male);
        contactName.setText(row_pos.getmName());
        dateTime.setText(row_pos.getmTimeCreate().toString());
        //duration.setText(row_pos.getmDuration());
       
        
        return convertView;
		
	}

}
