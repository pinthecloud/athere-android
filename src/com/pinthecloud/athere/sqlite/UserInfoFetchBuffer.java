package com.pinthecloud.athere.sqlite; 

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserInfoFetchBuffer extends SQLiteOpenHelper {

	
	// All Static variables
	// Database Version
	
	private static int DATABASE_VERSION = 1;
	static{
		Random r= new Random();
		DATABASE_VERSION = r.nextInt(10) + 1; 
	}
	// Database Name
	private static final String DATABASE_NAME = "userInfoFetchBuffer";

	// Contacts table name
	private final String TABLE_NAME = "userIds";

	// Contacts Table Columns names
	private final String ID = "id";
	private final String TIME_STAMP = "timestamp";


	public UserInfoFetchBuffer(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	/*
	 * Creating Tables(non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_NAME + 
				"("
					+ ID + " TEXT PRIMARY KEY,"
					+ TIME_STAMP + " INTEGER"
				+")";
		db.execSQL(CREATE_CONTACTS_TABLE);
	}

	/*
	 * Upgrading database(non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

		// Create tables again
		onCreate(db);
	}
	
	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

		// Create tables again
		onCreate(db);
	}

	public void dropTable() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
	}

	/**
	 * All CRUD(Create, Read, Update, Delete) Operations
	 */

	// Adding new contact
	public void addUserId(String userId) {
		if (userId == null) return;
		
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(ID, userId);
		values.put(TIME_STAMP, System.currentTimeMillis());

		// Inserting Row
		db.insert(TABLE_NAME, null, values);
		db.close(); // Closing database connection
	}

	public void addAllUsersId(List<String> list){
		SQLiteDatabase db = this.getWritableDatabase();

		for(String userId : list){
			ContentValues values = new ContentValues();
			values.put(ID, userId);
			values.put(TIME_STAMP, System.currentTimeMillis());
			
			// Inserting Row
			db.insert(TABLE_NAME, null, values);
		}
		db.close(); // Closing database connection
	}

	// Getting single contact
	public String pop() {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_NAME, null, null,
				null, null, null, TIME_STAMP, "1");
		if (cursor != null)
			if (cursor.moveToFirst()) {
				String id = cursor.getString(0);
				this.deleteUser(id);
				return id;
			}

		
		return null;
	}
	
	public String peek() {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_NAME, null, null,
				null, null, null, TIME_STAMP, "1");
		if (cursor != null)
			if (cursor.moveToFirst()) {
				return cursor.getString(0);
			}

		
		return null;
	}
	
	public boolean isEmpty() {
		boolean isExist = false;
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.query(TABLE_NAME, null, null,
				null, null, null, null, null);
		if (cursor != null) return cursor.moveToFirst();
		
		return isExist;
	}
	

	// Getting All Contacts
	public List<String> getAllUsersId() {
		List<String> usersId = new ArrayList<String>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_NAME;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				usersId.add(cursor.getString(0));
			} while (cursor.moveToNext());
		}

		// return contact list
		return usersId;
	}
	
	public List<String> popAllUsersId() {
		List<String> usersId = new ArrayList<String>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_NAME;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				String id = cursor.getString(0);
				usersId.add(id);
				this.deleteUser(id);
			} while (cursor.moveToNext());
		}

		// return contact list
		return usersId;
	}

	// Deleting single contact
	public void deleteUser(String id) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_NAME, ID + " = ?", new String[] { id });
		db.close();
	}
}