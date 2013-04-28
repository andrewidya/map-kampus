package com.map.jancokan;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends ListActivity implements OnClickListener {
	
	public final static String EXTRA_MESSAGE = "com.map.jancokan.MESSAGE";
	
	ImageButton mapButton, searchButton, locationButton;
	EditText locationInput;
	
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
		//case R.id.search_button:
		//	String message = locationInput.getText().toString();
		//	Intent intent = new Intent(this, NavigateActivity.class);
		//	intent.putExtra(EXTRA_MESSAGE, message);
		//	startActivity(intent);
		//	break;
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
	            
	            //String[] from = new String[] {"universityName", "universitySureName", "address"};
	            //int[] to = new int[] {R.id.universityName, R.id.universitySureName, R.id.address};	            	            
	            adapter = new SimpleCursorAdapter(this, R.layout.university_list_item, cursor, new String[] {"universityName", "universitySureName", "address"}, new int[] {R.id.universityName, R.id.universitySureName, R.id.address}, 0);
	            setListAdapter(adapter);
	}	
}
/*
public class EmployeeList extends ListActivity {
    
    protected EditText searchText;
    protected SQLiteDatabase db;
    protected Cursor cursor;
    protected ListAdapter adapter;
   
//Called when the activity is first created.
@Override
public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    db = (new DatabaseHelper(this)).getWritableDatabase();
    searchText = (EditText) findViewById (R.id.searchText);
}

public void search(View view) {
    // || is the concatenation operation in SQLite
            cursor = db.rawQuery("SELECT _id, firstName, lastName, title FROM employee WHERE firstName || ' ' || lastName LIKE ?",
                                            new String[]{"%" + searchText.getText().toString() + "%"});
            adapter = new SimpleCursorAdapter(
                            this,
                            R.layout.employee_list_item,
                            cursor,
                            new String[] {"firstName", "lastName", "title"},
                            new int[] {R.id.firstName, R.id.lastName, R.id.title});
            setListAdapter(adapter);
}

public void onListItemClick(ListView parent, View view, int position, long id) {
    Intent intent = new Intent(this, EmployeeDetails.class);
    Cursor cursor = (Cursor) adapter.getItem(position);
    intent.putExtra("EMPLOYEE_ID", cursor.getInt(cursor.getColumnIndex("_id")));
    startActivity(intent);
}

}*/