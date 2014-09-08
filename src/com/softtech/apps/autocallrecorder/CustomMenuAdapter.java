package com.softtech.apps.autocallrecorder;

import java.util.List;

import com.softtech.apps.sync.android.util.Util;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CustomMenuAdapter extends BaseAdapter {

	Context context;
	List<RowItem> rowItem;

	public CustomMenuAdapter(Context context, List<RowItem> rowItem) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.rowItem = rowItem;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return rowItem.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return rowItem.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return rowItem.indexOf(getItem(position));
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater) context
					.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.drawer_list_item, null);
		}

		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				new LayoutParams((int) Util.convertDpToPx(
						context.getResources(), 24), (int) Util.convertDpToPx(
						context.getResources(), 24)));

		ImageView imgIcon = (ImageView) convertView.findViewById(R.id.icon);
		TextView txtTitle = (TextView) convertView.findViewById(R.id.title);

		RowItem row_pos = rowItem.get(position);
		// setting the image resource and title
		imgIcon.setImageResource(row_pos.getIcon());

		imgIcon.setLayoutParams(layoutParams);

		txtTitle.setText(row_pos.getTitle());

		return convertView;

	}

}
