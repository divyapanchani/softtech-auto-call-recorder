package com.softtech.apps.callrecorder;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SearchView;

import com.softtech.apps.constant.Constant;
import com.softtech.apps.dropbox.DropboxApi;

public class MainActivity extends Activity {

	private static final int CATEGORY_DETAIL = 1;
	private static final int NO_MEMORY_CARD = 2;
	private static final int TERMS = 3;

	public static final int MEDIA_MOUNTED = 0;
	public static final int MEDIA_MOUNTED_READ_ONLY = 1;
	public static final int NO_MEDIA = 2;

	private static int linkToDropbox = 0;

	private Context context;

	private Fragment fragment = null;

	DatabaseHandler db;

	/**
	 * Navication menu
	 * */
	String[] menutitles;
	TypedArray menuIcons;

	// nav drawer title
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;

	private DrawerLayout drawer;
	private ListView drawerList;
	private ActionBarDrawerToggle drawerToggle;

	private List<RowItem> rowItems;
	private CustomMenuAdapter adapter;

	private static List<Config> cfg;
	public DropboxApi mDropboxApi = null;

	// - END navication menu

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// enabling action bar app icon and behaving it as toggle button
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		// register for dropbox account
		mDropboxApi = new DropboxApi(getApplicationContext());

		mDropboxApi.registerAccountDropbox();

		// put my code here
		context = this.getBaseContext();

		// Get TabHost Refference

		setSharedPreferences(true);

		// Get all config and store it to STATIC variable
		db = new DatabaseHandler(this);
		cfg = db.getAllConfigs();

		Config cc = cfg.get(0);

		Log.d("CONFIG",
				"Value = " + cc.get_value() + " KeyWord =" + cc.get_keyword());

		/**
		 * Navication menu here
		 * */
		mTitle = mDrawerTitle = getTitle();

		menutitles = getResources().getStringArray(R.array.titles);
		menuIcons = getResources().obtainTypedArray(R.array.icons);

		drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerList = (ListView) findViewById(R.id.drawer);

		// Get value and store to list Item
		rowItems = new ArrayList<RowItem>();
		for (int i = 0; i < menutitles.length; i++) {
			RowItem items = new RowItem(menutitles[i], menuIcons.getResourceId(
					i, -1));
			rowItems.add(items);
		}

		adapter = new CustomMenuAdapter(getApplicationContext(), rowItems);

		drawerList.setAdapter(adapter);

		drawerList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub

				// Display fragment
				updateDisplay(position);

			}
		});

		drawerToggle = new ActionBarDrawerToggle(this, drawer,
				R.drawable.ic_drawer, R.string.app_name, R.string.app_name) {
			@SuppressLint("NewApi")
			public void onDrawerClosed(View view) {
				// getActionBar().setTitle(mTitle);
				// calling onPrepareOptionsMenu() to show action bar icons
				invalidateOptionsMenu();
			}

			@SuppressLint("NewApi")
			public void onDrawerOpened(View drawerView) {
				// getActionBar().setTitle(mDrawerTitle);
				// calling onPrepareOptionsMenu() to hide action bar icons
				invalidateOptionsMenu();
			}
		};
		drawer.setDrawerListener(drawerToggle);

		if (savedInstanceState == null) {
			// On first time, show Home Fragment
			updateDisplay(0);
		}
		// END - navication menu here

		// Search filter here
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		autoSyncDropbox();

	}

	public void autoSyncDropbox() {
		if (db == null) {
			db = new DatabaseHandler(context);
		}

		Config configAutoSync = db.getConfig(2);

		if (configAutoSync != null && configAutoSync.get_value() == 1) {
			// thuc hien chuc nang auto sync
			// kiem tra internet
			AsyncTask<String, Void, String> netWork = new AsyncTask<String, Void, String>() {

				@Override
				protected String doInBackground(String... urls) {
					String response = "";
					for (String url : urls) {
						try {
							HttpURLConnection urlc = (HttpURLConnection) (new URL(
									url).openConnection());
							urlc.setRequestProperty("User-Agent", "Test");
							urlc.setRequestProperty("Connection", "close");
							urlc.setConnectTimeout(1500);
							urlc.connect();
							response = String.valueOf(urlc.getResponseCode());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					return response;
				}

				@Override
				protected void onPostExecute(String result) {
					if (result.length() > 0 && Integer.valueOf(result) == 200) {
						// do anything
						
						Config configSyncType = db.getConfig(3);

						if (mDropboxApi == null) {
							mDropboxApi = new DropboxApi(context);
							mDropboxApi.registerAccountDropbox();

						}

						if (mDropboxApi.getDbxAccountManager() != null) {
							if (!mDropboxApi.getDbxAccountManager()
									.hasLinkedAccount()) {

								
								AlertDialog dlg = createDialog();
								
								dlg.show();
								
							} else {
								mDropboxApi.linkAccountToFileFS();
								linkToDropbox = 1;
							}
						}

						if (configSyncType != null && linkToDropbox == 1) {

							// favorites
							File favorites = mDropboxApi.getFolderFavorites();
							File[] listFavorites = favorites.listFiles();
							for (File tmpFile : listFavorites) {
								// Log.e("TMP FILE", tmp.getAbsolutePath());
								if (tmpFile.isFile()
										&& tmpFile.getName().contains(
												Constant.ISSYNC0)) {

									mDropboxApi.syncFileToDropBoxFolder(1,
											tmpFile);
								}
							}

							if (configSyncType.get_value() == 0) {
								// all calls

								File allCalls = mDropboxApi.getFolderAllcall();
								File[] listAllCalls = allCalls.listFiles();
								for (File tmpFile : listAllCalls) {
									if (tmpFile.isFile()
											&& tmpFile.getName().contains(
													Constant.ISSYNC0)) {
										mDropboxApi.syncFileToDropBoxFolder(0,
												tmpFile);
									}
								}
							}
						} else {
							// default error config
						}
					}
				}

			};

			if (hasConnections()) {
				netWork.execute(new String[] { "http://www.google.com" });
			}
		}

	}

	private AlertDialog createDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(
				MainActivity.this);
		builder.setTitle("Dropbox login required !");
		
		builder.setMessage(
				"Please keep dropbox account login to auto-sync function can operate without interruption !\n Choose \"Login\" button to login otherwise choose \"Cancle\" button")
				.setPositiveButton("Login",
						new OnClickListener() {

							@Override
							public void onClick(
									DialogInterface arg0,
									int arg1) {
								// TODO Auto-generated
								// method stub
								mDropboxApi
										.getDbxAccountManager()
										.startLink(
												MainActivity.this,
												Constant.REQUEST_LINK_TO_DBX_MAINACTIVITY);
							}
						})
				.setNegativeButton("Cancel",
						new OnClickListener() {

							@Override
							public void onClick(
									DialogInterface dialog,
									int which) {
								// TODO Auto-generated
								// method stub

							}
						});
		return builder.create();
	}
	
	@SuppressLint("SimpleDateFormat")
	private Date stringToDate(String aDate) {

		if (aDate == null)
			return null;
		ParsePosition pos = new ParsePosition(0);
		SimpleDateFormat simpledateformat = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss'Z'");
		Date stringDate = simpledateformat.parse(aDate, pos);
		return stringDate;

	}

	@SuppressLint("NewApi")
	private void updateDisplay(int position) {
		if (mDropboxApi == null) {
			mDropboxApi = new DropboxApi(context);

			mDropboxApi.registerAccountDropbox();
		}
		switch (position) {
		case 0:
			fragment = new optionFramentHome(context, mDropboxApi);
			getActionBar().setTitle("Home");
			break;
		case 1:
			getActionBar().setTitle("General setting");
			fragment = new GeneralSetting(context);
			break;
		case 2:
			fragment = new SyncToDropbox(context, mDropboxApi);
			break;
		case 3:
			getActionBar().setTitle("About us");
			fragment = new optionFrament3();
			break;
		case 4:
			// Share app to social network
			Intent shareIntent = new Intent();
			shareIntent.setAction(Intent.ACTION_SEND);
			shareIntent.putExtra(Intent.EXTRA_TEXT, "http://softtech.vn");// change link to app store
			shareIntent.setType("text/plain"); // set lai cai type
			shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			startActivity(Intent.createChooser(shareIntent, "Share with"));
			break;
		default:
			break;
		}

		if (fragment != null) {
			FragmentManager fragmentManager = getFragmentManager();

			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, fragment).commit();
			// update selected item and title, then close the drawer
			setTitle(menutitles[position]);
			drawerList.setItemChecked(position, true);
			drawerList.setSelection(position);
			drawer.closeDrawer(drawerList);
		} else {
			// error in creating fragment
			Log.e("MainActivity", "Error in creating fragment");
		}
	}

	/**
	 * checks if an external memory card is available
	 * 
	 * @return
	 */
	public static int updateExternalStorageState() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return MEDIA_MOUNTED;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			return MEDIA_MOUNTED_READ_ONLY;
		} else {
			return NO_MEDIA;
		}

	}

	private void setSharedPreferences(boolean settingsValue) {
		SharedPreferences settings = this.getSharedPreferences(
				Constant.LISTEN_ENABLED, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean("silentMode", settingsValue);
		editor.commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		
		//Log.d("QUERY", "Khoi tao menu o Home Fragment ###");
		
		 SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
         SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();

         searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
         searchView.setIconifiedByDefault(false);  

         SearchView.OnQueryTextListener textChangeListener = new SearchView.OnQueryTextListener()
         {
             @Override
             public boolean onQueryTextChange(String newText)
             {
                 // this is your adapter that will be filtered
            	 Message msg=new Message();
            	 Bundle b=new Bundle();
            	 b.putString("text", newText);
            	 msg.setData(b);
            	//Log.d("FRAGMENT","Send mesage to Fragment");
            	 optionFramentHome.handle.sendMessage(msg);
                 return true;
             }
             @Override
             public boolean onQueryTextSubmit(String query)
             {
                 // this is your adapter that will be filtered
            	 //Log.d("QUERY", "Submit query ="+query);
                 return true;
             }
         };
         searchView.setOnQueryTextListener(textChangeListener);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// toggle nav drawer on selecting action bar app icon/title
		if (drawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action bar actions click
		switch (item.getItemId()) {
		case R.id.action_settings:
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/***
	 * Called when invalidateOptionsMenu() is triggered
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// if nav drawer is opened, hide the action items
		boolean drawerOpen = drawer.isDrawerOpen(drawerList);
		menu.findItem(R.id.action_settings).setVisible(!drawerOpen);

		// If the nav drawer is open, hide action items related to the content
		// view

		return super.onPrepareOptionsMenu(menu);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		drawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggles
		drawerToggle.onConfigurationChanged(newConfig);
	}

	// @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		
		if (requestCode == Constant.REQUEST_LINK_TO_DBX_SYNCTODROPBOX) {
			if (resultCode == Activity.RESULT_OK) {
				// success

				((SyncToDropbox) fragment).onActivityReSultMe();

			} else {
				// link dropbox fail
			}
		} else if (requestCode == Constant.REQUEST_LINK_TO_DBX_optionFramentHome) {
			if (resultCode == Activity.RESULT_OK) {
				// success

				// ((optionFramentHome) fragment).onActivityReSultMe();

			} else {
				// link dropbox fail
			}
		} else if (requestCode == Constant.REQUEST_LINK_TO_DBX_MAINACTIVITY) {
			if (resultCode == Activity.RESULT_OK) {
				// success
				mDropboxApi.linkAccountToFileFS();

				linkToDropbox = 1;

			} else {
				// link dropbox fail
			}
		}

		else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	public boolean hasConnections() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (null == ni)
			return false;
		return ni.isConnectedOrConnecting();
	}
	
}
