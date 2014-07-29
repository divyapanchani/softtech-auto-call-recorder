package com.softtech.apps.dropbox;

import java.io.File;
import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.dropbox.client2.android.AuthActivity;
import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxException.Unauthorized;
import com.dropbox.sync.android.DbxFile;
import com.dropbox.sync.android.DbxFileInfo;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxPath;
import com.softtech.apps.callrecorder.R;
import com.softtech.apps.sync.android.util.FolderLoader;
import com.softtech.apps.sync.android.util.Util;

public class DemoDB extends Activity {

	private static String RECORD = "Records";
	
	private static String PATH_RECORD = "/mkdevbehoctap/default";
	
	static final int REQUEST_LINK_TO_DBX = 0; // This value is up to you
	
	private Button btLinkDropbox;
	
	private ListView lvFolder;

	private DbxAccountManager mDbAccountManager;

	private List<DbxFileInfo> listDbxFI;

	private DbxFileSystem dbxFileFs;

	private FolderLoader folderLoad;

	private FolderAdapter folderAdapter;

	private File file, fileList[];
	
//	private List<DbxFileInfo> listF = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_record);

		mDbAccountManager = NotesAppConfig
				.getAccountManager(getApplicationContext());
		checkAppKeySetup();
		
		lvFolder = (ListView) findViewById(R.id.lvFolder);

		lvFolder.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				DbxFileInfo fileOb = (DbxFileInfo) parent
						.getItemAtPosition(position);

				if (!fileOb.isFolder) {
					try {
						DbxFileSystem.forAccount(
								mDbAccountManager.getLinkedAccount()).delete(
								fileOb.path);

						if (folderAdapter != null) {
							folderAdapter.notifyDataSetChanged();
						}

					} catch (DbxException e) {
						e.printStackTrace();
					}
				}
			}
		});

		btLinkDropbox = (Button) findViewById(R.id.bt_linkdropbox);
		if (mDbAccountManager != null) {
			if (mDbAccountManager.hasLinkedAccount()) {
				btLinkDropbox.setText("Unlink DropBox");
				try {

					dbxFileFs = DbxFileSystem.forAccount (mDbAccountManager
							.getLinkedAccount());
					
					listDbxFI = getListFileInfo();

					if (listDbxFI != null) {

						folderAdapter = new FolderAdapter(
								getApplicationContext(), listDbxFI);

						lvFolder.setAdapter(folderAdapter);
					}
				} catch (Unauthorized e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				btLinkDropbox.setText("Link DropBox");
			}
		}

		btLinkDropbox.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mDbAccountManager != null) {
					if (mDbAccountManager.hasLinkedAccount()) {
						mDbAccountManager.unlink();
						btLinkDropbox.setText("Link DropBox");
						lvFolder.setAdapter(null);
					} else {
						mDbAccountManager.startLink(DemoDB.this,
								REQUEST_LINK_TO_DBX);
					}
				} else {
					mDbAccountManager.startLink(DemoDB.this,
							REQUEST_LINK_TO_DBX);
				}
//				Intent intent = new Intent(getApplicationContext(), Record.class);
//				startActivity(intent);
			}
		});
		
		
		int isHave = 0;
		
		for(DbxFileInfo fileInfo : listDbxFI){
			String nameFolder = Util.stripExtension("txt", fileInfo.path.getName());
			if(nameFolder.compareTo(RECORD) == 0){
				isHave = 1;
				break;
			}
			if(isHave == 1) break;
		}
		
		if(isHave == 0){
			 DbxPath p;
             try {
                 p = new DbxPath("/" + RECORD);
                 
             } catch (DbxPath.InvalidPathException e) {
                 return;
             }
             
			try {
				DbxFileSystem.forAccount(mDbAccountManager.getLinkedAccount()).createFolder(p);
			} catch (DbxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
			
		}
		
		getListFile();
		
		copyFileToDropBoxFolder();
	}

	private void copyFileToDropBoxFolder()
	{
		if(fileList != null && fileList.length > 0){
			
			int size = fileList.length;
			DbxFile mFile;
			DbxPath p;
			for(int i=0; i< size; i++)
		    {
				mFile = null;
				p = new DbxPath( RECORD + "/" + fileList[i].getName());
				try {
		            try {
		                mFile = dbxFileFs.open(p);
		            } catch (DbxException.NotFound e) {
		                mFile = dbxFileFs.create(p);
		            }
		        } catch (DbxException e) {
		            Log.e("UHMUHMUHM", "failed to open or create file.", e);
		            return;
		        }
				
				if(mFile!= null){
					final DbxFile mFileTmp = mFile;
					final File tmp = fileList[i];
					new Thread(new Runnable() {
	                    @Override
	                    public void run() {
	                            try {
	                            	mFileTmp.writeFromExistingFile(tmp, false);
	                            } catch (IOException e) {
	                                Log.e("WRITEFILE", "failed to write to file", e);
	                            }
	                            mFileTmp.close();
	                            Log.d("WRITEFILE", "write done");
	                    }
	                }).start();
				}
		    }
		}
	}
	
	private void getListFile(){
	
	    file = new File( Environment.getExternalStorageDirectory().toString() + PATH_RECORD) ;
	    
	    fileList = file.listFiles();
	    
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (requestCode == REQUEST_LINK_TO_DBX) {
			if (resultCode == Activity.RESULT_OK) {
				Toast.makeText(getApplicationContext(),
						"Link To DropBox Success", Toast.LENGTH_SHORT).show();

				btLinkDropbox.setText("Unlink DropBox");

				try {
					dbxFileFs = DbxFileSystem.forAccount(mDbAccountManager
							.getLinkedAccount());

					listDbxFI = getListFileInfo();
					
//					try {
//						listF = dbxFileFs.listFolder(DbxPath.ROOT);
//					} catch (DbxException e1) {
//						// TODO Auto-generated catch block
//						e1.printStackTrace();
//					}
//					listDbxFI = listF;
					
					if (listDbxFI != null) {
						folderAdapter = new FolderAdapter(
								getApplicationContext(), listDbxFI);
						lvFolder.setAdapter(folderAdapter);
					}
				} catch (Unauthorized e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					mDbAccountManager.startLink(DemoDB.this,
							REQUEST_LINK_TO_DBX);
				}
			} else {

				Toast.makeText(getApplicationContext(), "Link To DropBox Fail",
						Toast.LENGTH_SHORT).show();
			}
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	public List<DbxFileInfo> getListFileInfo() {
		if (dbxFileFs != null) {
			folderLoad = new FolderLoader(DemoDB.this, mDbAccountManager,
					DbxPath.ROOT);

			return folderLoad.loadInBackground();
		}

		return null;
	}

	private void checkAppKeySetup() {
		// Check to make sure that we have a valid app key
		if (NotesAppConfig.appKey.startsWith("CHANGE") || NotesAppConfig.appSecret.startsWith("CHANGE")) {
			Toast.makeText(
					getApplicationContext(),
					"You must apply for an app key and secret from developers.dropbox.com, and add them to the DBRoulette ap before trying it.",
					Toast.LENGTH_SHORT).show();
			finish();
			return;
		}

		// Check if the app has set up its manifest properly.
		Intent testIntent = new Intent(Intent.ACTION_VIEW);
		String scheme = "db-" + NotesAppConfig.appKey;
		String uri = scheme + "://" + AuthActivity.AUTH_VERSION + "/test";
		testIntent.setData(Uri.parse(uri));
		PackageManager pm = getPackageManager();
		if (0 == pm.queryIntentActivities(testIntent, 0).size()) {
			Toast.makeText(
					getApplicationContext(),
					"URL scheme in your app's "
							+ "manifest is not set up correctly. You should have a "
							+ "com.dropbox.client2.android.AuthActivity with the "
							+ "scheme: " + scheme, Toast.LENGTH_SHORT).show();
			finish();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
