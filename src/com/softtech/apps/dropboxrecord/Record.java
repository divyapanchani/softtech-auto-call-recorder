package com.softtech.apps.dropboxrecord;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ArrayAdapter;

public class Record extends ListActivity {

	private File file;
	private List<String> myList;
		
	public void onCreate(Bundle savedInstanceState) 
	{
	    super.onCreate(savedInstanceState);
	
	    myList = new ArrayList<String>();   
	
	    String root_sd = Environment.getExternalStorageDirectory().toString();
	    file = new File( root_sd + "/mkdevbehoctap/audio") ;       
	    File list[] = file.listFiles();
	
	    
	    for( int i=0; i< list.length; i++)
	    {
	            myList.add( list[i].getName() );
	    }
	
	    setListAdapter(new ArrayAdapter<String>(this,
	            android.R.layout.simple_list_item_1, myList ));
	
	}
}
