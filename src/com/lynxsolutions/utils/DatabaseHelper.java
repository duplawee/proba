package com.lynxsolutions.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
	public static final String DATABASE_NAME = "student_note.db";
	public static final String DATABASE_NAME_CLONE = "student_note_clone.db";
	private static final int DATABASE_VERSION = 2;	
	
		// Notes
		public static String NOTES_TABLE = "notes";
		public static String ID = "_id"; 
		public static String TITLE = "title";
		public static String TYPE = "type";
		public static String TAG = "tag";
		public static String URI = "uri";
		public static String DATE = "date";
		
		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		public DatabaseHelper(Context context, String databaseName) {
			super(context, databaseName, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			String CREATE_NOTES_TABLE = "CREATE TABLE IF NOT EXISTS " + NOTES_TABLE
					+ " ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " + TITLE
					+ " BLOB, " + TYPE + " BLOB, " + TAG + " BLOB, " + URI + " BLOB, " + DATE + " BLOB )";
			Log.i("ok", "CREATE_NOTES_TABLE "+CREATE_NOTES_TABLE);
			
			db.execSQL(CREATE_NOTES_TABLE);
			
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + NOTES_TABLE);
			
		}
	
	
}
