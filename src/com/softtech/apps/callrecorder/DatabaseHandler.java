package com.softtech.apps.callrecorder;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper {

	private static final String LOG = DatabaseHandler.class.getName();
	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 17;

	// Database Name
	private static final String DATABASE_NAME = "CallRecorder";

	private static final String TABLE_CONFIGS = "configs";
	
	 // Contacts table name
    private static final String TABLE_CONTACTS = "contacts";
	

	// Configs Table Column names
	private static final String CONFIG_KEY_ID = "id";
	private static final String CONFIG_KEY_VALUE = "value";
	private static final String CONFIG_KEY_WORD = "keyword";
	

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_PH_NO = "phone_number";
    private static final String KEY_CONTACT_ID = "contact_id";
	Context myContext;
	
	private static String DB_PATH = "";

	private final String CREATE_CONFIGS_TABLE = "CREATE TABLE " + TABLE_CONFIGS
			+ "(" + CONFIG_KEY_ID + " INTEGER PRIMARY KEY," + CONFIG_KEY_VALUE
			+ " INTEGER,"+ CONFIG_KEY_WORD +" TEXT)";
	
	private final String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
             + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
             + KEY_PH_NO + " TEXT," + KEY_CONTACT_ID+ " TEXT)";
	

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.myContext = context;

		if (android.os.Build.VERSION.SDK_INT >= 4.2) {
			DB_PATH = myContext.getApplicationInfo().dataDir + "/databases/";
		} else {
			DB_PATH = "/data/data/" + myContext.getPackageName()
					+ "/databases/";
		}

		/*
		 * DB_PATH = Environment.getDataDirectory().getPath() + "/data/" +
		 * myContext.getPackageName() + "/databases/";
		 */
	}
	 @Override
	    public void onCreate(SQLiteDatabase db) {
	 
	        // creating required tables
	        db.execSQL(CREATE_CONFIGS_TABLE);
	        db.execSQL(CREATE_CONTACTS_TABLE);
	        
	        // Init with table config
	        db.execSQL("INSERT INTO " + TABLE_CONFIGS + "(" + CONFIG_KEY_VALUE + ","+CONFIG_KEY_WORD+") values(1,1)"); // enable mode
	        db.execSQL("INSERT INTO " + TABLE_CONFIGS + "(" + CONFIG_KEY_VALUE + ","+CONFIG_KEY_WORD+") values(3,2)"); // enable mode
	        db.execSQL("INSERT INTO " + TABLE_CONFIGS + "(" + CONFIG_KEY_VALUE + ","+CONFIG_KEY_WORD+") values(0,3)"); // enable mode
	        db.execSQL("INSERT INTO " + TABLE_CONFIGS + "(" + CONFIG_KEY_VALUE + ","+CONFIG_KEY_WORD+") values(0,4)"); // enable mode
	        
	        /**
	         * 1 = enable automatic record
	         * 2 = audio quality
	         * 3 = mode sync -> 0 = manual sync ; 1 = auto sync
	         * 4 = sync range -> 0 = all calls ; 1 = favorites call
	         * */
	        
	        // Init with table contacts
	        
	    }
	 
	    @Override
	    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	        // on upgrade drop older tables
	        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONFIGS);
	        // Drop older table if existed
	        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
	        // create new tables
	        onCreate(db);
	    }

	/**
	 * Xu ly tat cac van de lien quan den config o day
	 * Them sua, xoa config
	 * Cac thong tin ve cau hinh duoc dat co dinh va khong the xoa, chi co the sua
	 * */
	    
	// Update config
	public int updateConfig(Config config) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(CONFIG_KEY_VALUE, config.get_value());
		// updating row
		int temp = db.update(TABLE_CONFIGS, values, CONFIG_KEY_ID + " = ?",
				new String[] { String.valueOf(config.get_id()) });
				db.close();
	    return temp;
	}

	// Select config
	Config getConfig(int pos) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_CONFIGS,
				new String[] { CONFIG_KEY_ID,CONFIG_KEY_VALUE,CONFIG_KEY_WORD }, CONFIG_KEY_ID + "=?",
				new String[] {  String.valueOf(pos) }, null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();

		Config config = new Config(Integer.parseInt(cursor.getString(0)),
				Integer.parseInt(cursor.getString(1)),Integer.parseInt(cursor.getString(2)));
		// return message
		db.close();
		return config;
	}

	// Getting All config
	public List<Config> getAllConfigs() {

		//Log.d("CONFIG", "Bat dau truy van");

		List<Config> configList = new ArrayList<Config>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_CONFIGS;

		try {
			SQLiteDatabase db = this.getWritableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);

			// looping through all rows and adding to list
			if (cursor.moveToFirst()) {
				do {
					Config config = new Config();
					config.set_id((Integer.parseInt(cursor.getString(0))));
					config.set_value(Integer.parseInt(cursor.getString(1)));
					config.set_keyword(Integer.parseInt(cursor.getString(2)));
					// Adding contact to list
					configList.add(config);
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// return contact list
		return configList;
	}
	
	
	
	/**
	 * Xu ly contact o day. Bao gom cac thao tac them moi, sua, xoa, cap nhat contact
	 * 
	 * */
	
	// Adding new contact
	public void addContact(Contact contact) {
	    SQLiteDatabase db = this.getWritableDatabase();
	    
	    //if(verification(contact.get_contact_id())==false){
	    	// Inserting Row
	    	Log.d("FAILED", "##########################Contact da duoc chen vao bang");
	    	ContentValues values = new ContentValues();
		    values.put(KEY_NAME, contact.get_name()); // Contact Name
		    values.put(KEY_PH_NO, contact.get_phone_number()); // Contact Phone Number
		    values.put(KEY_CONTACT_ID, contact.get_contact_id());
		    db.insert(TABLE_CONTACTS, null, values);
		    db.close(); // Closing database connection
//	    }else{
//	    	Log.d("FAILED", "##########################3Contact da ton tai trong table");
//	    	return;
//	    }
	    
	}
	public boolean verification(String _contact_id) throws SQLException{
		SQLiteDatabase db = this.getWritableDatabase();
	    Cursor c = db.rawQuery("SELECT * FROM "+TABLE_CONTACTS+" WHERE "+KEY_CONTACT_ID+"="+_contact_id, null);
	    if (c!=null)
	    {
	    	Log.d("EXIST", "Phone number nay da ton tai");
	    	return true; // return true if the value of _username already exists
	    	
	    }
	        
	    return false; // Return false if _username doesn't match with any value of the columns "Username"
	}
	// Getting single contact
	public Contact getContact(int id) {
	    SQLiteDatabase db = this.getReadableDatabase();
	 
	    Cursor cursor = db.query(TABLE_CONTACTS, new String[] { KEY_ID,
	            KEY_NAME, KEY_PH_NO, KEY_CONTACT_ID }, KEY_ID + "=?",
	            new String[] { String.valueOf(id) }, null, null, null, null);
	    if (cursor != null)
	        cursor.moveToFirst();
	 
	    Contact contact = new Contact(cursor.getString(0),
	            cursor.getString(1), cursor.getString(2));
	    // return contact
	    return contact;
	}
	// Get contact by
	public Cursor getContactBy(String PhoneNumber){
		SQLiteDatabase db = this.getReadableDatabase();
		 
	    Cursor cursor = db.query(TABLE_CONTACTS, new String[] { KEY_ID,
	            KEY_NAME, KEY_PH_NO,KEY_CONTACT_ID }, KEY_PH_NO + "=?",
	            new String[] { String.valueOf(PhoneNumber) }, null, null, null, null);
	    if (cursor != null)
	        cursor.moveToFirst();
	 
//	    Contact contact = new Contact(cursor.getString(0),
//	            cursor.getString(1), cursor.getString(2));
	    // return contact
	    return cursor;
	}
	// Check contact exist
	private boolean field_exists( String p_query )
	{
		SQLiteDatabase db = this.getWritableDatabase();
	    Cursor mCursor  = db.rawQuery( p_query, null );
	    if  (  ( mCursor != null ) && ( mCursor.moveToFirst()) )
	    {
	        mCursor.close();
	        return true ;
	    }
	    mCursor.close();
	    return false ;
	}
	// Getting All Contacts
	 public List<Contact> getAllContacts() {
	    List<Contact> contactList = new ArrayList<Contact>();
	    // Select All Query
	    String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;
	 
	    SQLiteDatabase db = this.getWritableDatabase();
	    Cursor cursor = db.rawQuery(selectQuery, null);
	 
	    // looping through all rows and adding to list
	    if (cursor.moveToFirst()) {
	        do {
	            Contact contact = new Contact();
	            contact.set_id(cursor.getString(0));
	            contact.set_name(cursor.getString(1));
	            contact.set_phone_number(cursor.getString(2));
	            contact.set_contact_id(cursor.getString(3));
	            // Adding contact to list
	            contactList.add(contact);
	        } while (cursor.moveToNext());
	    }
	 
	    // return contact list
	    return contactList;
	}

	// Getting contacts Count
    public int getContactsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_CONTACTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
 
        // return count
        return cursor.getCount();
    }
    
    // Updating single contact
    public int updateContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
     
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, contact.get_name());
        values.put(KEY_PH_NO, contact.get_phone_number());
        values.put(KEY_CONTACT_ID, contact.get_contact_id());
        // updating row
        return db.update(TABLE_CONTACTS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(contact.get_id()) });
    }
    
    // Deleting single contact
    public void deleteContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACTS, KEY_CONTACT_ID + " = ?",
                new String[] { String.valueOf(contact.get_contact_id()) });
        db.close();
    }
}
