package com.softtech.apps.callrecorder;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class MainActivity extends Activity {

	public static final String FILE_DIRECTORY = "softtech";
	public static final String LISTEN_ENABLED = "ListenEnabled";
	private static final int CATEGORY_DETAIL = 1;
	private static final int NO_MEMORY_CARD = 2;
	private static final int TERMS = 3;

	public static final int MEDIA_MOUNTED = 0;
	public static final int MEDIA_MOUNTED_READ_ONLY = 1;
	public static final int NO_MEDIA = 2;

	private Context context;

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

	// - END navication menu

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// enabling action bar app icon and behaving it as toggle button
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

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
				getActionBar().setTitle(mTitle);
				// calling onPrepareOptionsMenu() to show action bar icons
				invalidateOptionsMenu();
			}

			@SuppressLint("NewApi")
			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
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
	}

	@SuppressLint("NewApi")
	private void updateDisplay(int position) {
		Fragment fragment = null;
		
		switch (position) {
		case 0:
			fragment = new optionFramentHome(context);
			break;
		case 1:
			fragment = new optionFrament1();
			break;
		case 2:
			fragment = new SyncToDropbox();
			break;
		case 3:
			fragment = new optionFrament3();
			break;
		case 4:
			// Share app to social network
			Intent shareIntent = new Intent();
			shareIntent.setAction(Intent.ACTION_SEND);
			String text_share = "Welcome to my App";
			shareIntent.putExtra(Intent.EXTRA_TEXT, text_share); // sua cai text mong muon
			shareIntent.setType("text/plain"); // set lai cai type
			shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			startActivity(Intent.createChooser(shareIntent, "Share this to"));
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

	private void setSharedPreferences(boolean settingsValue) {
		SharedPreferences settings = this.getSharedPreferences(LISTEN_ENABLED,
				0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean("silentMode", settingsValue);
		editor.commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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
		menu.findItem(R.id.action_save).setVisible(!drawerOpen);

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

}
