package com.softtech.apps.sync.android.util;

import android.app.Activity;
import android.content.res.Resources;
import android.util.TypedValue;

public class Util extends Activity{

    public static String stripExtension(String extension, String filename) {
        extension = "." + extension;
        if (filename.endsWith(extension)) {
            return filename.substring(0, filename.length() - extension.length());
        }
        return filename;
    }
    
    public static float convertDpToPx(Resources r, float dp){
    	
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
	}   
}
