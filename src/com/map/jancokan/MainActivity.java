package com.map.jancokan;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends Activity implements OnClickListener {
	
	public final static String EXTRA_MESSAGE = "com.map.jancokan.MESSAGE";
	public final static String UNIVERSITY_ID = "com.map.jancokan.UNIVERSITY_MESSAGE";
	
	ImageButton mapButton, searchButton, locationButton;
	EditText locationInput;
	ListView list;
	
	protected Cursor cursor;
	protected ListAdapter adapter;
	protected SQLiteDatabase db;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		locationInput = (EditText) findViewById(R.id.location_input);
		
		mapButton = (ImageButton) findViewById(R.id.map_button);
		mapButton.setOnClickListener(this);
		
		searchButton = (ImageButton) findViewById(R.id.search_button);
		searchButton.setOnClickListener(this);
		
		locationButton = (ImageButton) findViewById(R.id.location_button);
		locationButton.setOnClickListener(this);
		
		db = (new DatabaseHelper(this)).getWritableDatabase();
	}

	@Override
	public void onClick(View clickedButton) {
		// TODO Auto-generated method stub
		switch (clickedButton.getId()) {
		case R.id.search_button:
			search();
			break;
		case R.id.location_button:
			locationOnMap();
			break;
		case R.id.map_button:
			showMap();
			break;
		}
	}
	
	public void locationOnMap() {
		Intent intent = new Intent(this, LatitudeActivity.class);
		startActivity(intent);
	}
	
	public void showMap() {
		Intent intent = new Intent(this, MapActivity.class);
		startActivity(intent);
	}
	
	// This for database operations
	public void search() {
	    // || is the concatenation operation in SQLite
	            cursor = db.rawQuery("SELECT _id, universityName, universitySureName, address FROM university WHERE universityName || ' ' || universitySureName LIKE ?", 
	            		new String[]{"%" + locationInput.getText().toString() + "%"});
	            
	            adapter = new SimpleCursorAdapter(
	            		this,
	            		R.layout.university_list_item,
	            		cursor,
	            		new String[] {"universityName", "universitySureName", "address"},
	            		new int[] {R.id.universityName, R.id.universitySureName, R.id.address}, 0);
	            list = (ListView) findViewById(R.id.listView1);
	            list.setAdapter(adapter);
	            
	            list.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> listView, View view,
							int position, long id) {
						// TODO Auto-generated method stub
						Cursor cursor = (Cursor) list.getItemAtPosition(position);
						String universitiName = cursor.getString(cursor.getColumnIndexOrThrow("universityName"));
						Intent intent = new Intent(getApplicationContext(), UniversityActivity.class);
						intent.putExtra(UNIVERSITY_ID, universitiName);
						startActivity(intent);
						
					}
				});
	}
}