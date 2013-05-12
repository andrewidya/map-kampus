package com.map.kampus;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class DatabaseHelper extends SQLiteOpenHelper {

	public static final String DATABASE_NAME = "university_list.db";
	public static final String DATABASE_PATH = "/data/data/com.map.kampus/databases/";
	Context myContext;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, 1);
		// TODO Auto-generated constructor stub
		myContext = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		
		boolean dbExist = isDatabaseAvaliable();
		
		if(dbExist) {
			// do nothing
		} else {
			this.getReadableDatabase();
			try {
				copyDatabase();
			} catch (IOException e) {
				Log.e("CopyDatabase", e.getMessage());
			}
		}
		
		SQLiteDatabase.openDatabase(DATABASE_PATH + DATABASE_NAME, null, SQLiteDatabase.OPEN_READONLY);
		/*
		String sql = "CREATE TABLE IF NOT EXISTS university ("
				+ "_id INTERGER PRIMARY KEY, " + "universityName TEXT, "
				+ "universitySureName TEXT, " + "address TEXT, "
				+ "officePhone TEXT, " + "details TEXT)";

		db.execSQL(sql);

		ContentValues values = new ContentValues();
		values.put("universityName", "UNESA");
		values.put("universitySureName", "Universitas Negeri Surabaya");
		values.put("address", "Jl Ketitang Ora Eruh");
		values.put("officePhone", "999");
		values.put("details", "");
		db.insert("university", "universityName", values);

		values.put("universityName", "UNAIR");
		values.put("universitySureName", "Universitas Airlangga");
		values.put("address", "Jl Dharmawangsa g eruh");
		values.put("officePhone", "999");
		values.put("details", "iki detaile");
		db.insert("university", "universityName", values);
		*/
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROF TABLE IF EXISTS university");
		onCreate(db);
	}
	
	public boolean isDatabaseAvaliable() {
		SQLiteDatabase checkDB = null;
		
		try {
			String databasePath = DATABASE_PATH + DATABASE_NAME;
			checkDB = SQLiteDatabase.openDatabase(databasePath, null, SQLiteDatabase.OPEN_READONLY);
		} catch (SQLiteException e) {
			Log.e("DatabaseChecking", e.getMessage());
		}
		
		if(checkDB != null) {
			checkDB.close();
		}
		
		return checkDB != null ? true : false;
	}
	
	public void copyDatabase() throws IOException {
		InputStream inputFile = myContext.getAssets().open(DATABASE_NAME);
		String outputFile = DATABASE_PATH + DATABASE_NAME;
		OutputStream outputStream = new FileOutputStream(outputFile);
		
		byte[] data = new byte[1024];
		int length;
		
		while((length = inputFile.read(data)) > 0) {
			outputStream.write(data, 0, length);
		}
		
		outputStream.flush();
		outputStream.close();
		inputFile.close();
	}

}
