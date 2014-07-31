package com.softtech.apps.sync.android.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.util.TypedValue;

public class Util extends Activity {

	private Context mContext;

	public Util(Context context) {
		// TODO Auto-generated constructor stub
		mContext = context;
	}

	public static String stripExtension(String extension, String filename) {
		extension = "." + extension;
		if (filename.endsWith(extension)) {
			return filename
					.substring(0, filename.length() - extension.length());
		}
		return filename;
	}

	public static float convertDpToPx(Resources r, float dp) {

		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				r.getDisplayMetrics());
	}

	public boolean hasConnections() {
		ConnectivityManager cm = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (null == ni)
			return false;
		return ni.isConnectedOrConnecting();
	}

	public boolean hasActiveInternetConnection() {
		if (hasConnections()) {
			try {
				HttpURLConnection urlc = (HttpURLConnection) (new URL(
						"http://www.google.com").openConnection());
				urlc.setRequestProperty("User-Agent", "Test");
				urlc.setRequestProperty("Connection", "close");
				urlc.setConnectTimeout(1500);
				urlc.connect();
				return (urlc.getResponseCode() == 200);
			} catch (IOException e) {
				Log.e("ERROR", "Error checking internet connection", e);
			}
		} else {
			Log.d("ERROR", "No network available!");
		}
		return false;
	}
}
