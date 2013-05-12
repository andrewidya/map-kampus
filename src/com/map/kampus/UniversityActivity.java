package com.map.kampus;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class UniversityActivity extends Activity {

	public final static String UNIVERSITY_LOCATION = "com.map.kampus.LOCATE_MESSAGE";
	protected String universityCode;
	private TextView universityName;
	private TextView universitySureName;
	private TextView address;
	private TextView officePhone;
	private TextView details;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_university);

		Intent intent = getIntent();
		universityCode = intent.getStringExtra(MainActivity.UNIVERSITY_ID);
		SQLiteDatabase db = (new DatabaseHelper(this)).getWritableDatabase();
		Cursor cursor = db
				.rawQuery(
						"SELECT _id, universityName, universitySureName, address, officePhone, details FROM university WHERE universityName || ' ' || universitySureName LIKE ?",
						new String[] { "%" + universityCode.toString() + "%" });

		if (cursor.getCount() >= 1) {
			cursor.moveToFirst();

			universityName = (TextView) findViewById(R.id.university_name);
			universityName.setText(cursor.getString(cursor
					.getColumnIndex("universityName")));

			universitySureName = (TextView) findViewById(R.id.university_description);
			universitySureName.setText(cursor.getString(cursor
					.getColumnIndex("universitySureName")));

			address = (TextView) findViewById(R.id.university_address);
			address.setText(cursor.getString(cursor.getColumnIndex("address")));

			officePhone = (TextView) findViewById(R.id.university_phone);
			officePhone.setText(cursor.getString(cursor
					.getColumnIndex("officePhone")));

			details = (TextView) findViewById(R.id.university_details);
			details.setText(cursor.getString(cursor.getColumnIndex("details")));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_university, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_show_map:
			startMapLocation();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void startMapLocation() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, NavigateActivity.class);
		String message = universitySureName.getText().toString();
		intent.putExtra(UNIVERSITY_LOCATION, message);
		startActivity(intent);
	}
}
