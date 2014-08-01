package com.softtech.apps.sync.android.util;

import java.io.File;

import android.content.res.Resources;
import android.util.TypedValue;

public class Util {
	
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
	public static boolean moveFile(String oldfilename, String newFolderPath) {
		File file = new File(oldfilename);
		File file2 = new File(newFolderPath);
		boolean success = false;
		if (!file2.exists()) {
			success = file.renameTo(file2) && file.delete();
		}
		return success;
	}
}
