package com.lynxsolutions.utils;

import java.util.ArrayList;
import java.util.List;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

public class DatabaseManager {

	private DatabaseHelper sqlLiteHelper;
	private SQLiteDatabase database;
	private String DB_TAG = "DatabaseHelper";
	private String[] allColumns = { "_id", DatabaseHelper.TITLE,
			DatabaseHelper.TYPE, DatabaseHelper.TAG, DatabaseHelper.URI, DatabaseHelper.DATE };

	public DatabaseManager(Context context) {
		sqlLiteHelper = new DatabaseHelper(context);
	}

	public void open() throws SQLException {
		database = sqlLiteHelper.getWritableDatabase();
	}

	public void close() {
		sqlLiteHelper.close();
	}

	// ADD NOTE
	public boolean addNote(Context context, String title, String type,
			String tag, String uri, String date) {

		ContentValues contentValues = new ContentValues();
		contentValues.put(DatabaseHelper.TITLE, title);
		contentValues.put(DatabaseHelper.TYPE, type);
		contentValues.put(DatabaseHelper.TAG, tag);
		contentValues.put(DatabaseHelper.URI, uri);
		contentValues.put(DatabaseHelper.DATE, date);

		long row = database.insertOrThrow(DatabaseHelper.NOTES_TABLE, null,
				contentValues);
		if (row >= 0)
			return true;
		return false;
	}

	// GET ALL NOTES
	public ArrayList<Note> getAllNotes(Context context) {
		ArrayList<Note> notes = new ArrayList<Note>();
		Cursor cursor = database.query(DatabaseHelper.NOTES_TABLE, allColumns,
				null, null, null, null, null);

		if (cursor.getCount() > 0 && cursor.moveToFirst()) {
			do {
				String _id = cursor.getString(0);
				String title = cursor.getString(1);
				String type = cursor.getString(2);
				String tag = cursor.getString(3);
				String uri = cursor.getString(4);
				String date = cursor.getString(5);

				notes.add(new Note(_id, title, type, tag, uri, date));
			} while (cursor.moveToNext());
		}
		cursor.close();

		return notes;
	}

	private Note cursorToNote(Cursor cursor) {
		Note note = new Note();
		note.setId(cursor.getString(0));
		note.setTitle(cursor.getString(1));
		note.setType(cursor.getString(2));
		note.setTag(cursor.getString(3));
		note.setUri(cursor.getString(4));
		note.setNotificationDate(cursor.getString(5));
		return note;
	}

	//GET NOTE BY ID
	public Note getNoteByID(String id) {
		Cursor cursor = database.query(DatabaseHelper.NOTES_TABLE, allColumns,
				DatabaseHelper.ID + "=?", new String[] { String.valueOf(id) },
				null, null, null, null);

		if (cursor != null)
			cursor.moveToFirst();

		Note note = cursorToNote(cursor);

		return note;
	}
	
	//UPDATE/EDIT NOTE
	public boolean editNote(Context context, String id, String title, String type,
			String tag, String uri, String date){
		Cursor cursor = database.query(DatabaseHelper.NOTES_TABLE, allColumns,
				DatabaseHelper.ID + "=?", new String[] { String.valueOf(id) },
				null, null, null, null);
		Log.i("update", title+" "+type+" "+tag+" "+uri +" "+date);
		if (cursor != null){
			ContentValues contentValues = new ContentValues();
			contentValues.put(DatabaseHelper.TITLE, title);
			contentValues.put(DatabaseHelper.TYPE, type);
			contentValues.put(DatabaseHelper.TAG, tag);
			contentValues.put(DatabaseHelper.URI, uri);
			contentValues.put(DatabaseHelper.DATE, date);
			long row = database.update(DatabaseHelper.NOTES_TABLE,
					contentValues, "_id = " + id, null);
			if (row>=0){
				Log.i("update", "oke");
				return true;
			}
		}			
		return false;
	}
	
	//DELETE NOTE
	public boolean deleteNote(String id){
		long row = 	database.delete(DatabaseHelper.NOTES_TABLE, DatabaseHelper.ID + "="
					+ id, null);
		if (row>=0){
			Log.i("update", "oke");
			return true;
		}
		return false;
	}

	// GET NOTE BY TAG AND TYPE
	public ArrayList<Note> getFilteredList(Context context, String type,
			String tag) {
		Log.i("mylist", "db manager tag: " + tag + " type:" + type);
		ArrayList<Note> notes = new ArrayList<Note>();
		Log.i("LIST", "TYPE " + type + ", TAG: " + tag);

		if (type.toString().equals("ALL") && tag.toString().equals("ALL")) {
			notes = getAllNotes(context);
		} else {
			// String whereClause = DatabaseHelper.TAG + " == " + tag;
			switch (tag) {
			case "ALL":
				Cursor cursor = database.query(DatabaseHelper.NOTES_TABLE,
						allColumns, null, null, null, null, null);
				Log.i("list", "here: " + tag + "count: " + cursor.getCount());
				notes = selectNotesByType(cursor, type);
				break;
			case "HOME":
				Log.i("list", "here: " + tag);
				cursor = database.query(DatabaseHelper.NOTES_TABLE, allColumns,
						DatabaseHelper.TAG + "=?",
						new String[] { String.valueOf(tag) }, null, null, null,
						null);
				Log.i("list", "here: " + tag + "count: " + cursor.getCount());
				notes = selectNotesByType(cursor, type);
				break;
			case "WORK":
				Log.i("list", "here: " + tag);
				cursor = database.query(DatabaseHelper.NOTES_TABLE, allColumns,
						DatabaseHelper.TAG + "=?",
						new String[] { String.valueOf(tag) }, null, null, null,
						null);
				Log.i("list", "here: " + tag + "count: " + cursor.getCount());
				notes = selectNotesByType(cursor, type);
				break;

			default:
				break;
			}
		}
		return notes;
	}

	// SELECT NOTES BY TYPE (AFTER TAG)
	public ArrayList<Note> selectNotesByType(Cursor cursor, String selectedType) {
		ArrayList<Note> notes = new ArrayList<Note>();

		if (cursor.getCount() > 0 && cursor.moveToFirst()) {
			do {
				String _id = cursor.getString(0);
				String title = cursor.getString(1);
				String type = cursor.getString(2);
				String tag = cursor.getString(3);
				String uri = cursor.getString(4);
				String date = cursor.getString(5);

				if (type.toString().equals(selectedType.toString())
						|| selectedType.equals("ALL")) {
					notes.add(new Note(_id, title, type, tag, uri, date));
				}
			} while (cursor.moveToNext());
		}
		return notes;
	}
}
