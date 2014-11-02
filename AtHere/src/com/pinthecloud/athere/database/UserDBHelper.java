package com.pinthecloud.athere.database; 

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.pinthecloud.athere.AhApplication;
import com.pinthecloud.athere.helper.UserHelper;
import com.pinthecloud.athere.model.AhUser;

public class UserDBHelper extends SQLiteOpenHelper {

	// All Static variables
	// Database Version
	private static int DATABASE_VERSION = 2;

	// Database Name
	private static final String DATABASE_NAME = "userManagerDB";

	// Users table name
	private final String TABLE_NAME = "users";

	// Users Table Columns names
	private final String ID = "id";
	private final String AH_ID = "ah_id";
	private final String MOBILE_ID = "mobile_id";
	private final String MOBILE_TYPE = "mobile_type";
	private final String REGISTRATION_ID = "registration_id";
	private final String IS_MALE = "is_male";
	private final String BIRTH_YEAR = "birth_year";
	private final String PROFILE_PIC = "profile_pic";
	private final String NICK_NAME = "nick_name";
	private final String IS_CHUPA_ENABLE = "is_chupa_enable";

	private final String HAS_BEEN_OUT = "has_been_out";

	private SQLiteDatabase mDb;
	private AtomicInteger mCount = new AtomicInteger();


	public UserDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	
	/*
	 * Creating Tables(non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
	 */
	private SQLiteDatabase openDatabase(String name) {
		//		Log.e("ERROR", "open : " + name);
		if (mCount.incrementAndGet() == 1) {
			mDb = this.getWritableDatabase();
		}
		return mDb;
	}

	
	private void closeDatabase(String name) {
		//		Log.e("ERROR", "close : " + name);
		if (mCount.decrementAndGet() == 0) {
			mDb.close();
		}
	}


	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + 
				"("
				+ ID + " TEXT PRIMARY KEY,"
				+ AH_ID + " TEXT,"
				+ MOBILE_ID + " TEXT,"
				+ MOBILE_TYPE + " TEXT,"
				+ REGISTRATION_ID + " TEXT,"
				+ IS_MALE + " INTEGER,"
				+ BIRTH_YEAR + " INTEGER,"
				+ PROFILE_PIC + " TEXT,"
				+ NICK_NAME + " TEXT,"
				+ IS_CHUPA_ENABLE + " INTEGER,"
				+ HAS_BEEN_OUT + " INTEGER"
				+")";
		db.execSQL(CREATE_TABLE);
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

	
	public void dropTable(SQLiteDatabase db) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
	}


	/**
	 * All CRUD(Create, Read, Update, Delete) Operations
	 */

	public void addUser(AhUser user) {
		if (user == null) return;
		SQLiteDatabase db = this.openDatabase("addUser");
		ContentValues values = setAndGetValue(user);

		db.insert(TABLE_NAME, null, values);
		this.closeDatabase("addUser");
	}

	public void addAllUsers(List<AhUser> list){

		if (list == null || list.size() == 0) return;
		SQLiteDatabase db = this.openDatabase("addAllUsers");

		for(AhUser user : list){
			ContentValues values = setAndGetValue(user);
			db.insert(TABLE_NAME, null, values);
		}
		this.closeDatabase("addAllUsers");
	}

	private ContentValues setAndGetValue(AhUser user) {
		ContentValues values = new ContentValues();
		values.put(ID, user.getId());
		values.put(AH_ID, user.getAhId());
		values.put(MOBILE_ID, user.getMobileId());
		values.put(MOBILE_TYPE, user.getMobileType());
		values.put(REGISTRATION_ID, user.getRegistrationId());
		values.put(IS_MALE, user.isMale());
		values.put(BIRTH_YEAR, user.getBirthYear());
		values.put(PROFILE_PIC, user.getProfilePic());
		values.put(NICK_NAME, user.getNickName());
		values.put(IS_CHUPA_ENABLE, user.isChupaEnable());
		values.put(HAS_BEEN_OUT, false);
		return values;
	}

	public synchronized AhUser getUser(String id, boolean includingExits){
		SQLiteDatabase db = this.openDatabase("getUser(String id, boolean includingExits)");
		UserHelper userHelper = AhApplication.getInstance().getUserHelper();
		AhUser user = userHelper.getAdminUser(id);

		String query = ID + "=?";
		String[] args = new String[] { id };
		if (!includingExits) {
			query = ID + "=? and " + HAS_BEEN_OUT + "=?";
			args = new String[] { id, "0" };
		}

		Cursor cursor = db.query(TABLE_NAME, null, query, args, null, null, null, null);
		if (cursor != null && cursor.moveToFirst()) {
			user = convertToUser(cursor);
		}

		this.closeDatabase("getUser(String id, boolean includingExits)");
		return user;
	}

	public boolean isUserExist(String userId){
		boolean isExist = false;
		SQLiteDatabase db = this.openDatabase("isUserExist");
		Cursor cursor = db.query(TABLE_NAME, null, ID + "=?",
				new String[] { userId }, null, null, null, null);
		if (cursor != null) {
			isExist = cursor.moveToFirst();
		}
		this.closeDatabase("isUserExist");
		return isExist;
	}

	public synchronized boolean isUserExit(String userId) {
		boolean isExit = false;
		SQLiteDatabase db = this.openDatabase("isUserExit(String userId)");

		Cursor cursor = db.query(TABLE_NAME, new String[]{ HAS_BEEN_OUT }, ID + "=?",
				new String[] { userId }, null, null, null, null);

		if (cursor != null && cursor.moveToFirst()) {
			if (cursor.getInt(0) == 1){
				isExit = true;
			} else {
				isExit = false;
			}
		}

		this.closeDatabase("isUserExit(String userId)");
		return isExit;
	}

	public void addIfNotExistOrUpdate(AhUser user){
		if (user == null) return;
		if (this.isUserExist(user.getId())){
			this.updateUser(user);
		}
		else {
			this.addUser(user);
		}
	}

	public List<AhUser> getAllUsers(boolean includingExits) {
		List<AhUser> users = new ArrayList<AhUser>();

		String selectQuery = "SELECT  * FROM " + TABLE_NAME;
		String[] args = null;
		if (!includingExits) {
			selectQuery = "SELECT  * FROM " + TABLE_NAME + " WHERE " + HAS_BEEN_OUT + "=?";
			args = new String[] { "0" };
		}

		SQLiteDatabase db = this.openDatabase("getAllUsers(boolean includingExits)");
		Cursor cursor = db.rawQuery(selectQuery, args);

		if (cursor != null && cursor.moveToFirst()) {
			do {
				users.add(convertToUser(cursor));
			} while (cursor.moveToNext());
		}

		this.closeDatabase("getAllUsers(boolean includingExits)");
		return users;
	}

	public void updateUser(AhUser user) {
		if (user == null) return;
		SQLiteDatabase db = this.openDatabase("updateUser");
		ContentValues values = setAndGetValue(user);

		db.update(TABLE_NAME, values, ID + "=?", new String[] { user.getId() });
		this.closeDatabase("updateUser");
	}

	public void deleteUser(String id) {
		SQLiteDatabase db = this.openDatabase("deleteUser");
		db.delete(TABLE_NAME, ID + " = ?", new String[] { id });
		this.closeDatabase("deleteUser");
	}

	public void exitUser(String id) {
		if (id == null || id.equals("")) return;
		SQLiteDatabase db = this.openDatabase("exitUser(String id)");
		ContentValues values = new ContentValues();
		values.put(HAS_BEEN_OUT, true);

		db.update(TABLE_NAME, values, ID + "=?", new String[] { id });
		this.closeDatabase("exitUser(String id)");
	}

	public void deleteAllUsers() {
		SQLiteDatabase db = this.openDatabase("deleteAllUsers");
		db.delete(TABLE_NAME, null, null);
		this.closeDatabase("deleteAllUsers");
	}

	private AhUser convertToUser(Cursor cursor) {
		AhUser user = new AhUser();
		String _id = cursor.getString(0);
		String ahId = cursor.getString(1);
		String mobileId = cursor.getString(2);
		String mobileType = cursor.getString(3);
		String registrationId = cursor.getString(4);
		boolean isMale = cursor.getInt(5) == 1;
		int birthYear = cursor.getInt(6);
		String profilePic = cursor.getString(7);
		String nickName = cursor.getString(8);
		boolean chupaEnable = cursor.getInt(9) == 1;

		user.setId(_id);
		user.setAhId(ahId);
		user.setMobileId(mobileId);
		user.setMobileType(mobileType);
		user.setRegistrationId(registrationId);
		user.setMale(isMale);
		user.setBirthYear(birthYear);
		user.setProfilePic(profilePic);
		user.setNickName(nickName);
		user.setChupaEnable(chupaEnable);		

		return user;
	}
}