package com.map.jancokan;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper{
	
	public static final String DATABASE_NAME = "university_list";
	
	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, 1);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		
		String sql = "CREATE TABLE IF NOT EXISTS university (" + "_id INTERGER PRIMARY KEY, " + "universityName TEXT, " + "universitySureName TEXT, " + "address TEXT, " + "officePhone TEXT, " + "details TEXT)";

		db.execSQL(sql);
		
		ContentValues values = new ContentValues();
		values.put("universityName", "UNESA");
		values.put("universitySureName", "Universitas Negeri Surabaya");
		values.put("address", "Jl Ketitang Ora Eruh");
		values.put("officePhone", "999");
		values.put("details", "iki detaile");
		db.insert("university", "universityName", values);
		
		values.put("universityName", "UNAIR");
		values.put("universitySureName", "Universitas Airlangga");
		values.put("address", "Jl Dharmawangsa g eruh");
		values.put("officePhone", "999");
		values.put("details", "iki detaile");
		db.insert("university", "universityName", values);
		
		/*
		 * db.execSQL(sql);
               
                ContentValues values = new ContentValues();

                values.put("firstName", "John");
                values.put("lastName", "Smith");
                values.put("title", "CEO");
                values.put("officePhone", "617-219-2001");
                values.put("cellPhone", "617-456-7890");
                values.put("email", "jsmith@email.com");
                db.insert("employee", "lastName", values);

                values.put("firstName", "Robert");
                values.put("lastName", "Jackson");
                values.put("title", "VP Engineering");
                values.put("officePhone", "617-219-3333");
                values.put("cellPhone", "781-444-2222");
                values.put("email", "rjackson@email.com");
                values.put("managerId", "1");
                db.insert("employee", "lastName", values);

                values.put("firstName", "Marie");
                values.put("lastName", "Potter");
                values.put("title", "VP Sales");
                values.put("officePhone", "617-219-2002");
                values.put("cellPhone", "987-654-3210");
                values.put("email", "mpotter@email.com");
                values.put("managerId", "1");
                db.insert("employee", "lastName", values);
               
                values.put("firstName", "Lisa");
                values.put("lastName", "Jordan");
                values.put("title", "VP Marketing");
                values.put("officePhone", "617-219-2003");
                values.put("cellPhone", "987-654-7777");
                values.put("email", "ljordan@email.com");
                values.put("managerId", "2");
                db.insert("employee", "lastName", values);

                values.put("firstName", "Christophe");
                values.put("lastName", "Coenraets");
                values.put("title", "Evangelist");
                values.put("officePhone", "617-219-0000");
                values.put("cellPhone", "617-666-7777");
                values.put("email", "ccoenrae@adobe.com");
                values.put("managerId", "2");
                db.insert("employee", "lastName", values);

                values.put("firstName", "Paula");
                values.put("lastName", "Brown");
                values.put("title", "Director Engineering");
                values.put("officePhone", "617-612-0987");
                values.put("cellPhone", "617-123-9876");
                values.put("email", "pbrown@email.com");
                values.put("managerId", "2");
                db.insert("employee", "lastName", values);

                values.put("firstName", "Mark");
                values.put("lastName", "Taylor");
                values.put("title", "Lead Architect");
                values.put("officePhone", "617-444-1122");
                values.put("cellPhone", "617-555-3344");
                values.put("email", "mtaylor@email.com");
                values.put("managerId", "2");
                db.insert("employee", "lastName", values);

		 */

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROF TABLE IF EXISTS university");
		onCreate(db);
	}

}
