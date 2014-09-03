package com.pinthecloud.athere.database; 

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.model.AhUser;

public class UserDBHelper extends SQLiteOpenHelper {


	// All Static variables
	// Database Version

	private static int DATABASE_VERSION = 1;
	//	static{
	//		Random r= new Random();
	//		DATABASE_VERSION = r.nextInt(10) + 1; 
	//	}
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
	private final String IS_CHUPA_ENABLE = "is_chupa_enable";
	private final String AH_ID_USER_KEY = "ah_id_user_key";

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
				+ SQUARE_ID + " TEXT,"
				+ IS_CHUPA_ENABLE + " INTEGER,"
				+ HAS_BEEN_OUT + " INTEGER, "
				+ AH_ID_USER_KEY + " TEXT"
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

	public void dropTable(SQLiteDatabase db) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
	}

	/**
	 * All CRUD(Create, Read, Update, Delete) Operations
	 */

	// Adding new contact
	public void addUser(AhUser user) {

		if (user == null) return;
//		SQLiteDatabase db = this.getWritableDatabase();
		SQLiteDatabase db = this.openDatabase("addUser");

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
		values.put(IS_CHUPA_ENABLE, user.isChupaEnable());
		values.put(HAS_BEEN_OUT, false);
		values.put(AH_ID_USER_KEY, user.getAhIdUserKey());

		// Inserting Row
		db.insert(TABLE_NAME, null, values);
//		db.close(); // Closing database connection
		this.closeDatabase("addUser");
	}

	public void addAllUsers(List<AhUser> list){

		if (list == null || list.size() == 0) return;
//		SQLiteDatabase db = this.getWritableDatabase();
		SQLiteDatabase db = this.openDatabase("addAllUsers");

		for(AhUser user : list){
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
			values.put(IS_CHUPA_ENABLE, user.isChupaEnable());
			values.put(HAS_BEEN_OUT, false);
			values.put(AH_ID_USER_KEY, user.getAhIdUserKey());
			
			// Inserting Row
			db.insert(TABLE_NAME, null, values);
		}
//		db.close(); // Closing database connection
		this.closeDatabase("addAllUsers");
	}

	// Getting single contact
	public AhUser getUser(String id) {
		return this.getUser(id, false);
	}

	public synchronized AhUser getUser(String id, boolean includingExits){
//		SQLiteDatabase db = this.getReadableDatabase();
		SQLiteDatabase db = this.openDatabase("getUser(String id, boolean includingExits)");
		AhUser user = this.getAdminUser();
		String _query = ID + "=?";
		String[] args = new String[] { id };

		if (!includingExits) {
			_query = ID + "=? and " + HAS_BEEN_OUT + "=?";
			args = new String[] { id, "0" };
		}
		Cursor cursor = db.query(TABLE_NAME, null, _query,
				args, null, null, null, null);
		if (cursor != null) {
			if(cursor.moveToFirst()){
				user = convertToUser(cursor);
			}
		}
//		db.close();
		this.closeDatabase("getUser(String id, boolean includingExits)");
		return user;
	}

	public boolean isUserExist(String userId){
		boolean isExist = false;
//		SQLiteDatabase db = this.getReadableDatabase();
		SQLiteDatabase db = this.openDatabase("isUserExist");

		Cursor cursor = db.query(TABLE_NAME, null, ID + "=?",
				new String[] { userId }, null, null, null, null);
		if (cursor != null) {
			isExist = cursor.moveToFirst();
		}
//		db.close();
		this.closeDatabase("isUserExist");
		return isExist;
	}

	public synchronized boolean isUserExit(String userId) {
		boolean isExit = false;
//		SQLiteDatabase db = this.getReadableDatabase();
		SQLiteDatabase db = this.openDatabase("isUserExit(String userId)");

		Cursor cursor = db.query(TABLE_NAME, new String[]{ HAS_BEEN_OUT }, ID + "=?",
				new String[] { userId }, null, null, null, null);
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				if (cursor.getInt(0) == 1){
					isExit = true;
				} else {
					isExit = false;
				}
			}
		}
//		db.close();
		this.closeDatabase("isUserExit(String userId)");
		return isExit;
	}

	public void addIfNotExistOrUpdate(AhUser user){
		if (user == null) return;

		if (this.isUserExist(user.getId()))
			this.updateUser(user);
		else 
			this.addUser(user);
	}

	private AhUser convertToUser(Cursor cursor) {
		AhUser user = new AhUser();
		String _id = cursor.getString(0);
		String nickName = cursor.getString(1);
		String profilePic = cursor.getString(2);
		String mobileId = cursor.getString(3);
		String registrationId = cursor.getString(4);
		boolean isMale = cursor.getInt(5) == 1;
		int companyNum = cursor.getInt(6);
		int age = cursor.getInt(7);
		String squareId = cursor.getString(8);
		boolean chupaEnable = cursor.getInt(9) == 1;
		String ahIdUserKey = cursor.getString(10);

		user.setId(_id);
		user.setNickName(nickName);
		user.setProfilePic(profilePic);
		user.setMobileId(mobileId);
		user.setRegistrationId(registrationId);
		user.setMale(isMale);
		user.setCompanyNum(companyNum);
		user.setAge(age);
		user.setSquareId(squareId);
		user.setChupaEnable(chupaEnable);
		user.setAhIdUserKey(ahIdUserKey);
		return user;
	}


	// Getting All Contacts
	public List<AhUser> getAllUsers() {
		return this.getAllUsers(false);
	}

	public List<AhUser> getAllUsers(boolean includingExits) {
		List<AhUser> users = new ArrayList<AhUser>();

		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_NAME;
		String[] args = null;
		if (!includingExits) {
			selectQuery = "SELECT  * FROM " + TABLE_NAME + " WHERE " + HAS_BEEN_OUT + "=?";
			args = new String[] { "0" };
		}

//		SQLiteDatabase db = this.getWritableDatabase();
		SQLiteDatabase db = this.openDatabase("getAllUsers(boolean includingExits)");
		Cursor cursor = db.rawQuery(selectQuery, args);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				users.add(convertToUser(cursor));
			} while (cursor.moveToNext());
		}
//		db.close();
		this.closeDatabase("getAllUsers(boolean includingExits)");
		return users;
	}

	// Updating single contact
	public void updateUser(AhUser user) {

		if (user == null) return;

//		SQLiteDatabase db = this.getWritableDatabase();
		SQLiteDatabase db = this.openDatabase("updateUser");

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
		values.put(IS_CHUPA_ENABLE, user.isChupaEnable());
		values.put(AH_ID_USER_KEY, user.getAhIdUserKey());

		// Inserting Row
		db.update(TABLE_NAME, values, ID + "=?", new String[] { user.getId() });
//		db.close(); // Closing database connection
		this.closeDatabase("updateUser");
	}

	// Deleting single contact
	public void deleteUser(String id) {
//		SQLiteDatabase db = this.getWritableDatabase();
		SQLiteDatabase db = this.openDatabase("deleteUser");
		db.delete(TABLE_NAME, ID + " = ?", new String[] { id });
//		db.close();
		this.closeDatabase("deleteUser");
	}

	public void exitUser(String id) {
		if (id == null || id.equals("")) return;

//		SQLiteDatabase db = this.getWritableDatabase();
		SQLiteDatabase db = this.openDatabase("exitUser(String id)");

		ContentValues values = new ContentValues();
		values.put(HAS_BEEN_OUT, true);
		// Inserting Row
		db.update(TABLE_NAME, values, ID + "=?", new String[] { id });
//		db.close(); // Closing database connection
		this.closeDatabase("exitUser(String id)");

		//		String query = "UPDATE " + TABLE_NAME + " SET " + HAS_BEEN_OUT+"=?"+
		//				" WHERE " + ID + "=?";
		//		String [] selectionArgs = new String[]{ String.valueOf(1), id};
		//		db.rawQuery(query, selectionArgs);
		//		db.close();
	}

	public void deleteAllUsers() {
//		SQLiteDatabase db = this.getWritableDatabase();
		SQLiteDatabase db = this.openDatabase("deleteAllUsers");
		db.delete(TABLE_NAME, null, null);
//		db.close();
		this.closeDatabase("deleteAllUsers");
	}
	
	public static final String ADMIN_ID = "PintheCloud";
	private AhUser getAdminUser() {
		AhUser user = new AhUser();
		user.setId(UserDBHelper.ADMIN_ID);
		user.setNickName("관리자");
		user.setProfilePic("");
		user.setMale(true);
		user.setCompanyNum(0);
		user.setAge(40);
		user.setSquareId("NO_SQUARE_ID");
		user.setChupaEnable(true);
		user.setAhIdUserKey("NO_AH_ID_USER_KEY");
		
		
		return user;
	}
	
	private boolean isAdminUser(AhUser user) {
		return "PintheCloud".equals(user.getId());
	}
}