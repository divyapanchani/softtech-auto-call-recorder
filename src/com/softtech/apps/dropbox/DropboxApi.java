package com.softtech.apps.dropbox;

import java.io.File;
import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;

import com.dropbox.client2.android.AuthActivity;
import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxException.Unauthorized;
import com.dropbox.sync.android.DbxFile;
import com.dropbox.sync.android.DbxFileInfo;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxPath;
import com.softtech.apps.constant.Constant;
import com.softtech.apps.sync.android.util.FolderLoader;
import com.softtech.apps.sync.android.util.Util;

public class DropboxApi {

	private DbxFileSystem dbxFileFs;

	private DbxAccountManager dbxAccountManager;

	private List<DbxFileInfo> listDbxFI;

	private File file, fileList[];

	private FolderLoader folderLoad;

	private Context mContext;
	
	public DropboxApi(Context context) {
		mContext = context;
	}

	public DbxFileSystem getDbxFileFs() {
		return dbxFileFs;
	}

	public void setDbxFileFs(DbxFileSystem dbxFileFs) {
		this.dbxFileFs = dbxFileFs;
	}

	public DbxAccountManager getDbxAccountManager() {
		return dbxAccountManager;
	}

	public void setDbxAccountManager(DbxAccountManager dbxAccountManager) {
		this.dbxAccountManager = dbxAccountManager;
	}

	public List<DbxFileInfo> getListDbxFI() {
		return listDbxFI;
	}

	public void setListDbxFI(List<DbxFileInfo> listDbxFI) {
		this.listDbxFI = listDbxFI;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public File[] getFileList() {
		return fileList;
	}

	public void setFileList(File[] fileList) {
		this.fileList = fileList;
	}

	public void registerAccountDropbox() {
		dbxAccountManager = NotesAppConfig.getAccountManager(mContext);

		checkAppKeySetup();

	}

	public void registerToServer() {
		if (dbxAccountManager != null) {
			if (dbxAccountManager.hasLinkedAccount()) {
				try {

					dbxFileFs = DbxFileSystem.forAccount(dbxAccountManager
							.getLinkedAccount());

					listDbxFI = getListFileInfo();

				} catch (Unauthorized e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public void linkAccountToFileFS() {
		if (dbxAccountManager != null) {
			if (dbxAccountManager.hasLinkedAccount()) {
				try {

					dbxFileFs = DbxFileSystem.forAccount(dbxAccountManager
							.getLinkedAccount());

				} catch (Unauthorized e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public boolean hasLinkedAccount() {

		if (dbxAccountManager != null) {
			return dbxAccountManager.hasLinkedAccount();
		}

		return false;
	}

	private List<DbxFileInfo> getListFileInfo() {
		// TODO Auto-generated method stub
		if (dbxFileFs != null) {
			folderLoad = new FolderLoader(mContext, dbxAccountManager,
					DbxPath.ROOT);

			return folderLoad.loadInBackground();
		}

		return null;
	}

	private void checkAppKeySetup() {
		// Check to make sure that we have a valid app key
		if (NotesAppConfig.appKey.startsWith("CHANGE")
				|| NotesAppConfig.appSecret.startsWith("CHANGE")) {

			// dialog note secret key || app key fail

			return;
		}

		// Check if the app has set up its manifest properly.
		Intent testIntent = new Intent(Intent.ACTION_VIEW);
		String scheme = "db-" + NotesAppConfig.appKey;
		String uri = scheme + "://" + AuthActivity.AUTH_VERSION + "/test";
		testIntent.setData(Uri.parse(uri));
		PackageManager pm = mContext.getPackageManager();
		if (0 == pm.queryIntentActivities(testIntent, 0).size()) {
			// check manifest
		}
	}

	public void createFolderOnServer(DbxFileSystem dbxFileFS) {

		DbxPath p;

		// create folder softtech is parent
		p = new DbxPath("/" + Constant.FILE_DIRECTORY);

		try {
			dbxFileFS.open(p);
		} catch (DbxException e) {
			// TODO Auto-generated catch block
			try {
				dbxFileFS.createFolder(p);
			} catch (DbxException e1) {
				// TODO Auto-generated catch block
				// dialog error
			}
		}

		// //create folder allcalls
		p = new DbxPath("/" + Constant.FILE_DIRECTORY + "/"
				+ Constant.FILE_ALLCALLS);
		try {
			dbxFileFS.open(p);
		} catch (DbxException e) {
			// TODO Auto-generated catch block
			try {
				dbxFileFS.createFolder(p);
			} catch (DbxException e1) {
				// TODO Auto-generated catch block
				// dialog error
			}
		}

		// create folder favorites
		p = new DbxPath("/" + Constant.FILE_DIRECTORY + "/"
				+ Constant.FILE_FAVORITES);

		try {
			dbxFileFS.open(p);
		} catch (DbxException e) {
			// TODO Auto-generated catch block
			try {
				dbxFileFS.createFolder(p);
			} catch (DbxException e1) {
				// TODO Auto-generated catch block
				// dialog error
			}
		}

	}

	public void getFolderInSDCard() {

		createFolderSofftech();

		createFolderAllcalls();

		createFolderFavorites();

		String filepath = Environment.getExternalStorageDirectory().getPath();

		File file = new File(filepath, Constant.FILE_DIRECTORY);

		fileList = file.listFiles();
	}

	public File[] getListFileInSDCard(){
		getFolderInSDCard();
		
		return fileList;
	}
	
	public void createFolderSofftech() {
		String filepath = Environment.getExternalStorageDirectory().getPath();
		File file = new File(filepath, Constant.FILE_DIRECTORY);

		if (!file.exists()) {
			file.mkdirs();
		}
	}

	public File getFolderAllcall(){
		createFolderAllcalls();
		
		return new File(Environment.getExternalStorageDirectory().getPath()
				+ "/" + Constant.FILE_DIRECTORY, "/" + Constant.FILE_ALLCALLS);
	}
	
	public void createFolderAllcalls() {
		String filepath = Environment.getExternalStorageDirectory().getPath()
				+ "/" + Constant.FILE_DIRECTORY;
		File folderAll = new File(filepath, "/" + Constant.FILE_ALLCALLS);
		if (!folderAll.exists()) {
			folderAll.mkdirs();
		}
	}

	public File getFolderFavorites(){
		createFolderAllcalls();
		
		return new File(Environment.getExternalStorageDirectory().getPath()
				+ "/" + Constant.FILE_DIRECTORY, "/" + Constant.FILE_FAVORITES);
	}
	
	public void createFolderFavorites() {
		String filepath = Environment.getExternalStorageDirectory().getPath()
				+ "/" + Constant.FILE_DIRECTORY;
		File folderAll = new File(filepath, "/" + Constant.FILE_FAVORITES);
		if (!folderAll.exists()) {
			folderAll.mkdirs();
		}
	}

	// type 0 : all, 1 : favorites
	public void syncFileToDropBoxFolder(int type, final File fileSync) {
		DbxPath p;
		DbxFile mFile = null;
		
		String name = "";
		
		//rename file
		String fileName[] = fileSync.getName().split("-");
		
		// date
		//month
		name += fileName[0].substring(4, 6) + "-";
		//day
		name += fileName[0].substring(6, 8) + "-";

		//year
		
		name += fileName[0].substring(0, 4);
		name += " ";
		
		// hour
		name += fileName[0].substring(8,10) + "h";
		name += fileName[0].substring(10, 12) + "m";
		name += fileName[0].substring(12, 14) + "s";
		name += "-";
		
		//phone
		name += fileName[1];
		
		name += ".mp3";
		
		// create file on server
		if (type == 0) {
			p = new DbxPath(Constant.FILE_DIRECTORY + "/"
					+ Constant.FILE_ALLCALLS + "/" + name);
		} else if (type == 1) {
			p = new DbxPath(Constant.FILE_DIRECTORY + "/"
					+ Constant.FILE_FAVORITES + "/" + name);
		} else {
			p = new DbxPath(Constant.FILE_DIRECTORY + "/"
					+ Constant.FILE_ALLCALLS + "/" + name);
		}

		try {
			try {
				mFile = dbxFileFs.open(p);
			} catch (DbxException.NotFound e) {
				mFile = dbxFileFs.create(p);
			}
		} catch (DbxException e) {

		}

		if (mFile != null) {
			final DbxFile mFileTmp = mFile;

			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						
						mFileTmp.writeFromExistingFile(fileSync, false);

						Util.moveFile(fileSync.getAbsolutePath(), fileSync.getAbsolutePath().replace(Constant.ISSYNC0, Constant.ISSYNC1));
					} catch (IOException e) {
					}
					mFileTmp.close();
				}
			}).start();
		}
	}
}
