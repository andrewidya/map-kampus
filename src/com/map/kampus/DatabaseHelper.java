package com.map.kampus;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

	public static final String DATABASE_NAME = "university_list.db";
	Context myContext;
	private SQLiteDatabase myDatabase;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, 1);
		// TODO Auto-generated constructor stub
		myContext = context;
	}
	

	@Override
	public void onCreate(SQLiteDatabase db) {
		
		// TODO Auto-generated method stub
		
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		//db.execSQL("DROF TABLE IF EXISTS university");
		//onCreate(db);
	}
	
	public boolean isDatabaseAvaliable(String DATABASE_PATH) {
		SQLiteDatabase checkDB = null;
		//String databasePath = DATABASE_PATH;
		
		try {
			checkDB = SQLiteDatabase.openDatabase(DATABASE_PATH, null, SQLiteDatabase.OPEN_READONLY);
		} catch (SQLiteException e) {
			Log.e("DatabaseChecking", e.getMessage() + DATABASE_PATH);
		}
		
		if(checkDB != null) {
			checkDB.close();
			return true;
		}

		return false;
		
		//return checkDB != null ? true : false;
		//File dbFile = new File(DATABASE_PATH);
		//return dbFile.exists();
	}
	
	public void copyDatabase(String DATABASE_PATH) throws IOException {
		//AssetManager myAssets = myContext.getAssets();
		//InputStream inputFile = myAssets.open(DATABASE_NAME);
		
		InputStream inputFile = myContext.getAssets().open(DATABASE_NAME);
		String outputFile = DATABASE_PATH;
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
	
	public void createDatabase() {
	    boolean dbExist = isDatabaseAvaliable(myContext.getDatabasePath(DATABASE_NAME).toString());
			
			if(dbExist) {
				// do nothing
			} else {
				this.getReadableDatabase();
				try {
					copyDatabase(myContext.getDatabasePath(DATABASE_NAME).toString());
				} catch (IOException e) {
					Log.e("CopyDatabase", e.getMessage());
				}
			}
			
			//SQLiteDatabase.openDatabase(myContext.getDatabasePath(DATABASE_NAME).toString(), null, SQLiteDatabase.OPEN_READONLY);
			//SQLiteDatabase.openOrCreateDatabase(myContext.getDatabasePath(DATABASE_NAME), null);
		}
	
	public void openDatabase() {
		myDatabase = SQLiteDatabase.openDatabase(myContext.getDatabasePath(DATABASE_NAME).toString(), null, SQLiteDatabase.OPEN_READONLY);
	}
	
	@Override
	public synchronized void close() {
		if(myDatabase != null) {
			myDatabase.close();
		}
		super.close();
	}
}
