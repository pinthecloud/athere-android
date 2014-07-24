package com.pinthecloud.athere.sqlite; 

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.pinthecloud.athere.model.User;

public class UserDBHelper extends SQLiteOpenHelper {

	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 2;

	// Database Name
	private static final String DATABASE_NAME = "userManagerDB";

	// Contacts table name
	private final String TABLE_NAME = "users";

	// Contacts Table Columns names
	private final String ID = "id";
	private final String NICK_NAME = "nick_name";
	private final String PROFILE_PIC = "profile_pic";
	private final String MOBILE_ID = "mobile_id";
	private final String REGISTRATION_ID = "registration_id";
	private final String IS_MALE = "is_male";
	private final String COMPANY_NUM = "company_num";
	private final String AGE = "age";
	private final String SQUARE_ID = "square_id";

	public UserDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_NAME + 
				"("
				+ ID + " TEXT PRIMARY KEY," 
				+ NICK_NAME + " TEXT,"
				+ PROFILE_PIC + " TEXT,"
				+ MOBILE_ID + " TEXT,"
				+ REGISTRATION_ID + " TEXT,"
				+ IS_MALE + " INTEGER,"
				+ COMPANY_NUM + " INTEGER,"
				+ AGE + " INTEGER,"
				+ SQUARE_ID + " TEXT"
				+")";
		db.execSQL(CREATE_CONTACTS_TABLE);
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
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
	public void addUser(User user) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(ID, user.getId());
		values.put(NICK_NAME, user.getNickName());
		values.put(PROFILE_PIC, user.getProfilePic());
		values.put(MOBILE_ID, user.getMobileId());
		values.put(REGISTRATION_ID, user.getRegistrationId());
		values.put(IS_MALE, user.isMale());
		values.put(COMPANY_NUM, user.getCompanyNum());
		values.put(AGE, user.getAge());
		values.put(SQUARE_ID, user.getSquareId());

		// Inserting Row
		db.insert(TABLE_NAME, null, values);
		db.close(); // Closing database connection
	}

	public void addAllUsers(List<User> list){
		SQLiteDatabase db = this.getWritableDatabase();

		for(User user : list){
			ContentValues values = new ContentValues();
			values.put(ID, user.getId());
			values.put(NICK_NAME, user.getNickName());
			values.put(PROFILE_PIC, user.getProfilePic());
			values.put(MOBILE_ID, user.getMobileId());
			values.put(REGISTRATION_ID, user.getRegistrationId());
			values.put(IS_MALE, user.isMale());
			values.put(COMPANY_NUM, user.getCompanyNum());
			values.put(AGE, user.getAge());
			values.put(SQUARE_ID, user.getSquareId());

			// Inserting Row
			db.insert(TABLE_NAME, null, values);
		}

		db.close(); // Closing database connection
	}

	// Getting single contact
	public User getUser(int id) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_NAME, null, ID + "=?",
				new String[] { String.valueOf(id) }, null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();

		return convertToUser(cursor);
	}

	private User convertToUser(Cursor cursor) {
		String _id = cursor.getString(0);
		String nickName = cursor.getString(1);
		String profilePic = cursor.getString(2);
		String mobileId = cursor.getString(3);
		String registrationId = cursor.getString(4);
		boolean isMale = cursor.getInt(5) == 1;
		int companyNum = cursor.getInt(6);
		int age = cursor.getInt(7);
		String squareId = cursor.getString(8);

		return new User(_id,nickName,profilePic,mobileId,registrationId,isMale,companyNum,age,squareId);
	}

	// Getting All Contacts
	public List<User> getAllUsers() {
		List<User> users = new ArrayList<User>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_NAME;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				users.add(convertToUser(cursor));
			} while (cursor.moveToNext());
		}

		// return contact list
		return users;
	}

	//    // Updating single contact
	//    public int updateContact(String userId, User user) {
	//        SQLiteDatabase db = this.getWritableDatabase();
	// 
	//        ContentValues values = new ContentValues();
	//        values.put(NICK_NAME, user.getNickName());
	// 
	//        // updating row
	//        return db.update(TABLE_NAME, values, ID + " = ?",
	//                new String[] { String.valueOf(userId) });
	//    }

	// Deleting single contact
	public void deleteUser(String userId) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_NAME, ID + " = ?",
				new String[] { String.valueOf(userId) });
		db.close();
	}
}