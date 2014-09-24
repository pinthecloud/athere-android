package com.pinthecloud.athere.database;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.pinthecloud.athere.exception.AhException;
import com.pinthecloud.athere.model.AhMessage;

public class MessageDBHelper extends SQLiteOpenHelper {

	// All Static variables
	// Database Version
	private static int DATABASE_VERSION = 2;
	//	static{
	//		Random r= new Random();
	//		DATABASE_VERSION = r.nextInt(10) + 1; 
	//	}

	// Database Name
	private static final String DATABASE_NAME = "messageReceivedDB";

	// Messages table name
	private static final String TABLE_NAME = "messages";

	// Messages Table Columns names
	private final String ID = "id";
	private final String TYPE = "type";
	private final String CONTENT = "content";
	private final String SENDER = "sender";
	private final String SENDER_ID = "senderId";
	private final String RECEIVER = "receiver";
	private final String RECEIVER_ID = "receiverId";
	private final String TIME_STAMP = "timestamp";
	private final String CHUPA_COMMUN_ID = "chupaCommunId";
	private final String STATUS = "status";

	private BadgeDBHelper badgeDBHelper;

	private SQLiteDatabase mDb;
	private AtomicInteger mCount = new AtomicInteger();

	
	public MessageDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		badgeDBHelper = new BadgeDBHelper(context);
	}

	private synchronized SQLiteDatabase openDataBase(String name) {
		if (mCount.incrementAndGet() == 1) {
			mDb = this.getWritableDatabase();
		}
		return mDb;
	}

	private synchronized void closeDatabase(String name) {
		if (mCount.decrementAndGet() == 0) {
			mDb.close();
		}
	}
	
	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_NAME + 
				"("
				+ ID + " INTEGER PRIMARY KEY AUTOINCREMENT," 
				+ TYPE + " TEXT,"
				+ CONTENT + " TEXT,"
				+ SENDER + " TEXT,"
				+ SENDER_ID + " TEXT,"
				+ RECEIVER + " TEXT,"
				+ RECEIVER_ID + " TEXT,"
				+ TIME_STAMP + " TEXT,"
				+ CHUPA_COMMUN_ID + " TEXT,"
				+ STATUS + " INTEGER"
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
	
	private void putValueWithoutNull(ContentValues values, String id, String value){
		if (value == null) value = "";
		values.put(id, value);
	}

	// Adding new contact
	public int addMessage(AhMessage message) {
		//		SQLiteDatabase db = this.getWritableDatabase();
		SQLiteDatabase db = this.openDataBase("addMessage");

		ContentValues values = new ContentValues();
		putValueWithoutNull(values, TYPE, message.getType());
		putValueWithoutNull(values, CONTENT, message.getContent());
		putValueWithoutNull(values, SENDER, message.getSender());
		putValueWithoutNull(values, SENDER_ID, message.getSenderId());
		putValueWithoutNull(values, RECEIVER, message.getReceiver());
		putValueWithoutNull(values, RECEIVER_ID, message.getReceiverId());
		putValueWithoutNull(values, TIME_STAMP, message.getTimeStamp());
		putValueWithoutNull(values, CHUPA_COMMUN_ID, message.getChupaCommunId());
		values.put(STATUS, message.getStatus());

		// Inserting Row
		if (db == null) throw new AhException("db null in addMessage");
		long id = db.insert(TABLE_NAME, null, values);

		this.closeDatabase("addMessage");
		return (int)id;
	}

	// Getting All Messages
	public void updateMessages(AhMessage message) {
		//		SQLiteDatabase db = this.getWritableDatabase();
		SQLiteDatabase db = this.openDataBase("updateMessages");

		ContentValues values = new ContentValues();
		putValueWithoutNull(values, TYPE, message.getType());
		putValueWithoutNull(values, CONTENT, message.getContent());
		putValueWithoutNull(values, SENDER, message.getSender());
		putValueWithoutNull(values, SENDER_ID, message.getSenderId());
		putValueWithoutNull(values, RECEIVER, message.getReceiver());
		putValueWithoutNull(values, RECEIVER_ID, message.getReceiverId());
		putValueWithoutNull(values, TIME_STAMP, message.getTimeStamp());
		putValueWithoutNull(values, CHUPA_COMMUN_ID, message.getChupaCommunId());
		values.put(STATUS, message.getStatus());

		// Inserting Row
		db.update(TABLE_NAME, values, ID + " = ?", new String[]{ message.getId() });
		this.closeDatabase("updateMessages");
	}


	// Getting single contact
	public AhMessage getMessage(int id) {
		//		SQLiteDatabase db = this.getReadableDatabase();
		SQLiteDatabase db = this.openDataBase("getMessage");

		Cursor cursor = db.query(TABLE_NAME, null, ID + " = ?",
				new String[] { String.valueOf(id) }, null, null, null, null);
		if (cursor != null){
			cursor.moveToFirst();
		}

		AhMessage message = convertToMessage(cursor);
		this.closeDatabase("getMessage");
		return message;
	}

	// Getting All Messages
	public List<AhMessage> getAllMessages() {
		List<AhMessage> messages = new ArrayList<AhMessage>();

		// Select All Query
		String selectQuery = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + TIME_STAMP;

		//		SQLiteDatabase db = this.getReadableDatabase();
		SQLiteDatabase db = this.openDataBase("getAllMessages()");
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor != null && cursor.moveToFirst()) {
			do {
				messages.add(convertToMessage(cursor));
			} while (cursor.moveToNext());
		}

		this.closeDatabase("getAllMessages()");
		return messages;
	}

	public List<AhMessage> getAllMessages(String type) {
		List<AhMessage> messages = new ArrayList<AhMessage>();

		// Select All Query
		String selectQuery = "SELECT * FROM " + TABLE_NAME +
				" WHERE " + TYPE + " = ?" +
				" ORDER BY " + TIME_STAMP;

		//		SQLiteDatabase db = this.getReadableDatabase();
		SQLiteDatabase db = this.openDataBase("getAllMessages(String type)");
		Cursor cursor = db.rawQuery(selectQuery, new String[]{ type });

		// looping through all rows and adding to list
		if (cursor != null && cursor.moveToFirst()) {
			do {
				messages.add(convertToMessage(cursor));
			} while (cursor.moveToNext());
		}

		this.closeDatabase("getAllMessages(String type)");
		return messages;
	}

	//	public List<AhMessage> getAllMessagesByFifties(int offset, String type) {
	//		List<AhMessage> messages = new ArrayList<AhMessage>();
	//
	//		SQLiteDatabase db = this.getReadableDatabase();
	//		
	//		String countQuery = "SELECT COUNT(*) FROM "+TABLE_NAME+ " WHERE " + TYPE + " = ?";
	//		Cursor countCursor = db.rawQuery(countQuery, new String[] { type });
	//		int total = 0;
	//		if (countCursor!= null){
	//			if(countCursor.moveToFirst()){
	//				total = countCursor.getInt(0);
	//			}
	//		}
	//		
	//		// Select All Query
	//		String selectQuery = "SELECT * FROM " + TABLE_NAME + " WHERE " + TYPE
	//				+ " = ?" + " ORDER BY " + ID + " LIMIT 10 OFFSET "
	//				+ (total - ((offset+1) * 10));
	//		
	//		Cursor cursor = db.rawQuery(selectQuery, new String[] { type });
	//
	//		// looping through all rows and adding to list
	//		if (cursor.moveToFirst()) {
	//			do {
	//				messages.add(convertToMessage(cursor));
	//			} while (cursor.moveToNext());
	//		}
	//		db.close();
	//		// return contact list
	//		return messages;
	//	}
	//	
	//	public List<AhMessage> getAllMessagesByFifties(int offset, String... types) {
	//		List<AhMessage> messages = new ArrayList<AhMessage>();
	//
	//		for(String type : types){
	//			messages.addAll(this.getAllMessagesByFifties(offset, type));
	//		}
	//		sortMessages(messages);
	//		return messages;
	//	}
	//	
	//	public List<AhMessage> getAllMessagesByFifties(int offset, AhMessage.TYPE... types) {
	//		List<AhMessage> messages = new ArrayList<AhMessage>();
	//
	//		for(AhMessage.TYPE type : types){
	//			messages.addAll(this.getAllMessagesByFifties(offset, type.toString()));
	//		}
	//		sortMessages(messages);
	//		return messages;
	//	}

	public List<AhMessage> getAllMessages(String... types) {
		//		List<AhMessage> messages = new ArrayList<AhMessage>();
		//
		//		for(String type : types){
		//			messages.addAll(this.getAllMessages(type));
		//		}
		//		sortMessages(messages);
		//		return messages;

		List<String> typeArr = new ArrayList<String>();
		StringBuilder whereStr = new StringBuilder();
		for (String type : types) {
			typeArr.add(type);
			whereStr.append(TYPE + "=? OR ");
		}
		whereStr.delete(whereStr.length()- 3, whereStr.length());
		List<AhMessage> messages = new ArrayList<AhMessage>();

		// Select All Query
		String selectQuery = "SELECT * FROM " + TABLE_NAME +
				" WHERE " + whereStr.toString() + 
				" ORDER BY " + TIME_STAMP;

		//		SQLiteDatabase db = this.getReadableDatabase();
		SQLiteDatabase db = this.openDataBase("getAllMessages(String... type)");
		String[] argArray = typeArr.toArray(new String[typeArr.size()]);
		Cursor cursor = db.rawQuery(selectQuery, argArray);

		// looping through all rows and adding to list
		if (cursor != null && cursor.moveToFirst()) {
			do {
				messages.add(convertToMessage(cursor));
			} while (cursor.moveToNext());
		}

		this.closeDatabase("getAllMessages(String... type)");
		return messages;
	}

	public AhMessage getLastMessage(AhMessage.TYPE type) {
		String selectQuery = "SELECT * FROM " + TABLE_NAME +
				" WHERE " + TYPE + " = ?" + 
				" ORDER BY " + TIME_STAMP + " DESC LIMIT 1";

		//		SQLiteDatabase db = this.getReadableDatabase();
		SQLiteDatabase db = this.openDataBase("getLastMessage(AhMessage.TYPE type)");
		Cursor cursor = db.rawQuery(selectQuery, new String[]{ type.toString() });

		// looping through all rows and adding to list
		AhMessage message = null;
		if (cursor != null && cursor.moveToFirst()) {
			message = convertToMessage(cursor);
		}
		//		db.close();
		this.closeDatabase("getLastMessage(AhMessage.TYPE type)");

		return message;
	}

	public AhMessage getLastMessage(AhMessage.TYPE... types) {
		List<String> typeArr = new ArrayList<String>();
		StringBuilder whereStr = new StringBuilder();
		for (AhMessage.TYPE type : types) {
			typeArr.add(type.toString());
			whereStr.append(TYPE + "=? OR ");
		}
		whereStr.delete(whereStr.length()- 3, whereStr.length());

		// Select All Query
		String selectQuery = "SELECT * FROM " + TABLE_NAME +
				" WHERE " + whereStr.toString() + 
				" ORDER BY " + TIME_STAMP + " DESC LIMIT 1";

		//		SQLiteDatabase db = this.getReadableDatabase();
		SQLiteDatabase db = this.openDataBase("getLastMessage(AhMessage.TYPE... types)");
		String[] argArray = typeArr.toArray(new String[typeArr.size()]);
		Cursor cursor = db.rawQuery(selectQuery, argArray);

		// looping through all rows and adding to list
		AhMessage message = null;
		if (cursor != null && cursor.moveToFirst()) {
			message = convertToMessage(cursor);
		}

		this.closeDatabase("getLastMessage(AhMessage.TYPE... types)");
		return message;
	}

	public List<AhMessage> getAllMessages(AhMessage.TYPE... types) {
		List<String> typeArr = new ArrayList<String>();
		StringBuilder whereStr = new StringBuilder();
		for (AhMessage.TYPE type : types) {
			typeArr.add(type.toString());
			whereStr.append(TYPE + "=? OR ");
		}
		whereStr.delete(whereStr.length()- 3, whereStr.length());
		List<AhMessage> messages = new ArrayList<AhMessage>();

		// Select All Query
		String selectQuery = "SELECT * FROM " + TABLE_NAME +
				" WHERE " + whereStr.toString() + 
				" ORDER BY " + TIME_STAMP;

		//		SQLiteDatabase db = this.getReadableDatabase();
		SQLiteDatabase db = this.openDataBase("getAllMessages(AhMessage.TYPE... types)");
		String[] argArray = typeArr.toArray(new String[typeArr.size()]);
		Cursor cursor = db.rawQuery(selectQuery, argArray);

		// looping through all rows and adding to list
		if (cursor != null && cursor.moveToFirst()) {
			do {
				messages.add(convertToMessage(cursor));
			} while (cursor.moveToNext());
		}

		this.closeDatabase("getAllMessages(AhMessage.TYPE... types)");
		return messages;
	}

	public List<AhMessage> getAllMessages(AhMessage.TYPE type) {
		return this.getAllMessages(type.toString());
	}

	public boolean isEmpty() {
		String selectQuery = "SELECT COUNT(*) FROM " + TABLE_NAME;

		//		SQLiteDatabase db = this.getReadableDatabase();
		SQLiteDatabase db = this.openDataBase("getAllMessages(AhMessage.TYPE type)");
		Cursor cursor = db.rawQuery(selectQuery, null);

		int count = 0;
		if(cursor != null && cursor.moveToFirst()){
			count = cursor.getInt(0);
		}

		this.closeDatabase("getAllMessages(AhMessage.TYPE type)");
		return count == 0;
	}

	public boolean isEmpty(String type) {
		String selectQuery = "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE " + TYPE + " = ?";

		//		SQLiteDatabase db = this.getReadableDatabase();
		SQLiteDatabase db = this.openDataBase("isEmpty(String type)");
		Cursor cursor = db.rawQuery(selectQuery, new String[]{ type });

		int count = 0;
		if(cursor != null && cursor.moveToFirst()){
			count = cursor.getInt(0);
		}

		this.closeDatabase("isEmpty(String type)");
		return count == 0;
	}

	public boolean isEmpty(String... types) {
		List<String> typeArr = new ArrayList<String>();
		StringBuilder whereStr = new StringBuilder();
		for (String type : types) {
			typeArr.add(type);
			whereStr.append(TYPE + "=? OR ");
		}
		whereStr.delete(whereStr.length()- 3, whereStr.length());

		String selectQuery = "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE " + whereStr.toString();
		String[] argArray = typeArr.toArray(new String[typeArr.size()]);

		//		SQLiteDatabase db = this.getReadableDatabase();
		SQLiteDatabase db = this.openDataBase("isEmpty(String... types)");
		Cursor cursor = db.rawQuery(selectQuery, argArray);

		int count = 0;
		if(cursor != null && cursor.moveToFirst()){
			count = cursor.getInt(0);
		}

		this.closeDatabase("isEmpty(String... types)");
		return count == 0;
	}

	public boolean isEmpty(AhMessage.TYPE... types) {
		List<String> typeArr = new ArrayList<String>();
		StringBuilder whereStr = new StringBuilder();
		for (AhMessage.TYPE type : types) {
			typeArr.add(type.toString());
			whereStr.append(TYPE + "=? OR ");
		}
		whereStr.delete(whereStr.length()- 3, whereStr.length());

		String selectQuery = "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE " + whereStr.toString();
		String[] argArray = typeArr.toArray(new String[typeArr.size()]);

		//		SQLiteDatabase db = this.getReadableDatabase();
		SQLiteDatabase db = this.openDataBase("isEmpty(AhMessage.TYPE... types)");
		Cursor cursor = db.rawQuery(selectQuery, argArray);

		int count = 0;
		if(cursor != null && cursor.moveToFirst()){
			count = cursor.getInt(0);
		}

		this.closeDatabase("isEmpty(AhMessage.TYPE... types)");
		return count == 0;
	}

	public boolean isEmpty(AhMessage.TYPE type) {
		return this.isEmpty(type.toString());
	}

	//	public List<AhMessage> popAllMessages() {
	//		List<AhMessage> messages = new ArrayList<AhMessage>();
	//
	//		// Select All Query
	//		String selectQuery = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + ID;
	//
	//		SQLiteDatabase db = this.getReadableDatabase();
	//		Cursor cursor = db.rawQuery(selectQuery, null);
	//
	//		// looping through all rows and adding to list
	//		if (cursor.moveToFirst()) {
	//			do {
	//				messages.add(convertToMessage(cursor));
	//			} while (cursor.moveToNext());
	//		}
	//		db.delete(TABLE_NAME, null ,null);
	//		db.close();
	//		// return contact list
	//		return messages;
	//	}
	//
	//	public List<AhMessage> popAllMessages(String type) {
	//		List<AhMessage> messages = new ArrayList<AhMessage>();
	//
	//		// Select All Query
	//		String selectQuery = "SELECT * FROM " + TABLE_NAME +
	//				" WHERE " + TYPE + " = ?" +
	//				" ORDER BY " + ID;
	//
	//		SQLiteDatabase db = this.getReadableDatabase();
	//		Cursor cursor = db.rawQuery(selectQuery, new String[]{ type });
	//
	//		// looping through all rows and adding to list
	//		if (cursor.moveToFirst()) {
	//			do {
	//				messages.add(convertToMessage(cursor));
	//			} while (cursor.moveToNext());
	//		}
	//		this.deleteAllMessages(type);
	//		db.close();
	//		// return message list
	//		return messages;
	//	}
	//
	//	public List<AhMessage> popAllMessages(String... types) {
	//		List<AhMessage> messages = new ArrayList<AhMessage>();
	//		List<String> typeArr = new ArrayList<String>();
	//		StringBuilder whereStr = new StringBuilder();
	//		for (String type : types) {
	//			typeArr.add(type);
	//			whereStr.append(TYPE + "=? OR ");
	//		}
	//		whereStr.delete(whereStr.length()- 3, whereStr.length());
	//		
	//		
	//		String selectQuery = "SELECT * FROM " + TABLE_NAME +
	//				" WHERE " + whereStr.toString() +
	//				" ORDER BY " + ID;
	//
	//		String[] argArray = typeArr.toArray(new String[typeArr.size()]);
	//		SQLiteDatabase db = this.getReadableDatabase();
	//		
	//		Cursor cursor = db.rawQuery(selectQuery, argArray);
	//
	//		// looping through all rows and adding to list
	//		if (cursor.moveToFirst()) {
	//			do {
	//				messages.add(convertToMessage(cursor));
	//			} while (cursor.moveToNext());
	//		}
	//		this.deleteAllMessages(type);
	//		db.close();
	//		// return message list
	//		return messages;
	//	}
	//
	//	public List<AhMessage> popAllMessages(AhMessage.TYPE... types) {
	//		List<AhMessage> messages = new ArrayList<AhMessage>();
	//
	//		for(AhMessage.TYPE type : types){
	//			messages.addAll(this.popAllMessages(type));
	//		}
	//		sortMessages(messages);
	//		return messages;
	//	}
	//
	//	public List<AhMessage> popAllMessages(AhMessage.TYPE type) {
	//		return this.popAllMessages(type.toString());
	//	}

	// Deleting single contact
	public void deleteMessage(String messageId) {
		//		SQLiteDatabase db = this.getWritableDatabase();
		SQLiteDatabase db = this.openDataBase("deleteMessage(String messageId)");
		db.delete(TABLE_NAME, ID + " = ?",
				new String[] { String.valueOf(messageId) });
		//		db.close();
		this.closeDatabase("deleteMessage(String messageId)");
	}

	public void deleteAllMessages() {
		//		SQLiteDatabase db = this.getWritableDatabase();
		SQLiteDatabase db = this.openDataBase("deleteAllMessages()");
		db.delete(TABLE_NAME, null ,null);
		this.closeDatabase("deleteAllMessages()");
	}

	public void deleteAllMessages(String strType) {
		//		SQLiteDatabase db = this.getWritableDatabase();
		SQLiteDatabase db = this.openDataBase("deleteAllMessages(String strType)");
		db.delete(TABLE_NAME, TYPE + " = ?", new String[]{ strType });
		this.closeDatabase("deleteAllMessages(String strType)");
	}

	public void deleteAllMessages(AhMessage.TYPE type) {
		this.deleteAllMessages(type.toString());
	}

	public List<AhMessage> getLastChupas() {
		List<AhMessage> list = new ArrayList<AhMessage>();

		// Select All Query
		String selectQuery = "SELECT t1.* FROM " + TABLE_NAME + " t1" +
				" JOIN (SELECT MAX(id) id FROM " + TABLE_NAME +
				" GROUP BY " + CHUPA_COMMUN_ID + ") t2 on t1.id = t2.id" +
				" WHERE " + TYPE + " = ?" +
				" ORDER BY " + TIME_STAMP + " DESC";
		//		SQLiteDatabase db = this.getReadableDatabase();
		SQLiteDatabase db = this.openDataBase("getLastChupas()");
		Cursor cursor = db.rawQuery(selectQuery, new String[]{ AhMessage.TYPE.CHUPA.toString()});

		// looping through all rows and adding to list
		if (cursor != null && cursor.moveToFirst()) {
			do {
				list.add(convertToMessage(cursor));
			} while (cursor.moveToNext());
		}

		this.closeDatabase("getLastChupas()");
		return list;
	}

	public List<AhMessage> getChupasByCommunId(String chupaCommunId) {
		List<AhMessage> list = new ArrayList<AhMessage>();

		// Select All Query
		String selectQuery = "SELECT * FROM " + TABLE_NAME +
				" WHERE " + TYPE + "=? AND " + CHUPA_COMMUN_ID + " = ?" +
				" ORDER BY " + TIME_STAMP;

		//		SQLiteDatabase db = this.getReadableDatabase();
		SQLiteDatabase db = this.openDataBase("getChupasByCommunId(String chupaCommunId)");
		Cursor cursor = db.rawQuery(selectQuery, new String[]{ AhMessage.TYPE.CHUPA.toString() 
				,chupaCommunId});

		// looping through all rows and adding to list
		if (cursor != null && cursor.moveToFirst()) {
			do {
				list.add(convertToMessage(cursor));
			} while (cursor.moveToNext());
		}

		this.closeDatabase("getChupasByCommunId(String chupaCommunId)");
		return list;
	}

	public AhMessage getLastChupaByCommunId(String chupaCommunId) {
		// Select All Query
		String selectQuery = "SELECT * FROM " + TABLE_NAME +
				" WHERE " + TYPE + "=? AND " + CHUPA_COMMUN_ID + " = ?" +
				" ORDER BY " + TIME_STAMP + " DESC LIMIT 1";

		//		SQLiteDatabase db = this.getReadableDatabase();
		SQLiteDatabase db = this.openDataBase("getLastChupaByCommunId(String chupaCommunId)");
		Cursor cursor = db.rawQuery(selectQuery, new String[]{ AhMessage.TYPE.CHUPA.toString() 
				,chupaCommunId});

		AhMessage message = null;
		// looping through all rows and adding to list
		if (cursor != null && cursor.moveToFirst()) {
			message = (convertToMessage(cursor));
		}

		this.closeDatabase("getLastChupaByCommunId(String chupaCommunId)");
		return message;
	}

	private AhMessage convertToMessage(Cursor cursor) {
		String _id = cursor.getString(0);
		String type = cursor.getString(1);
		String content = cursor.getString(2);
		String sender = cursor.getString(3);
		String senderId = cursor.getString(4);
		String receiver = cursor.getString(5);
		String receiverId = cursor.getString(6);
		String timeStamp = cursor.getString(7);
		String chupaCommunId = cursor.getString(8);
		int status = cursor.getInt(9);

		AhMessage.Builder messageBuilder = new AhMessage.Builder();
		messageBuilder.setId(_id)
		.setType(type)
		.setContent(content)
		.setSender(sender)
		.setSenderId(senderId)
		.setReceiver(receiver)
		.setReceiverId(receiverId)
		.setTimeStamp(timeStamp)
		.setChupaCommunId(chupaCommunId)
		.setStatus(status);
		return messageBuilder.build();
	}

	public int getChupaBadgeNum(String chupaCommunId) {
		return badgeDBHelper.getChupaBadgeNum(chupaCommunId);
	}

	public int getAllChupaBadgeNum() {
		return badgeDBHelper.getAllChupaBadgeNum();
	}

	public void increaseChupaBadgeNum(String chupaCommunId) {
		badgeDBHelper.increaseChupaBadgeNum(chupaCommunId);
	}


	public void clearChupaBadgeNum(String chupaCommunId) {
		badgeDBHelper.clearChupaBadge(chupaCommunId);
	}

	public void clearAllChupaBadgeNum() {
		badgeDBHelper.cleareAllChupaBadgeNum();
	}


	private class BadgeDBHelper extends SQLiteOpenHelper {
		// Database Version
		private static final int BADGE_DATABASE_VERSION = 2;

		// Database Name
		private static final String BADGE_DATABASE_NAME = "badge_db";

		// Badges table name
		private static final String CHUPA_BADGE_TABLE_NAME = "chupa_badges";

		// Chupa badges Table Columns names
		private final String CHUPA_BADGE_ID = "badge_id";
		private final String CHUPA_BADGE_COMMUN_ID = "badge_chupa_commun_id";
		private final String CHUPA_BADGE_COUNT = "count";

		private AtomicInteger mBadgeCount = new AtomicInteger();
		private SQLiteDatabase mBadgeDb;


		private synchronized SQLiteDatabase openDatabase(String name) {
			if (mBadgeCount.incrementAndGet() == 1) {
				mBadgeDb = this.getWritableDatabase();
			}
			return mBadgeDb;
		}

		private synchronized void closeDatabase(String name) {
			if (mBadgeCount.decrementAndGet() == 0) {
				mBadgeDb.close();
			}
		}

		public BadgeDBHelper(Context context) {
			super(context, BADGE_DATABASE_NAME, null, BADGE_DATABASE_VERSION);
		}

		// Creating Tables
		@Override
		public void onCreate(SQLiteDatabase db) {
			String CREATE_CHUPA_BADGES_TABLE = "CREATE TABLE " + CHUPA_BADGE_TABLE_NAME + 
					"("
					+ CHUPA_BADGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," 
					+ CHUPA_BADGE_COMMUN_ID + " TEXT,"
					+ CHUPA_BADGE_COUNT + " INTEGER"
					+")";
			db.execSQL(CREATE_CHUPA_BADGES_TABLE);
		}

		// Upgrading database
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// Drop older table if existed
			dropTable(db);

			// Create tables again
			onCreate(db);
		}

		@Override
		public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// Drop older table if existed
			dropTable(db);

			// Create tables again
			onCreate(db);
		}

		public void dropTable(SQLiteDatabase db) {
			db.execSQL("DROP TABLE IF EXISTS " + CHUPA_BADGE_TABLE_NAME);
		}

		private int createChupaBadge(String chupaCommunId) {
			//			SQLiteDatabase db = this.getWritableDatabase();
			SQLiteDatabase db = this.openDatabase("createChupaBadge(String chupaCommunId)");

			ContentValues values = new ContentValues();
			putValueWithoutNull(values, CHUPA_BADGE_COMMUN_ID, chupaCommunId);
			values.put(CHUPA_BADGE_COUNT, 0);

			// Inserting Row
			long id = db.insert(CHUPA_BADGE_TABLE_NAME, null, values);
			this.closeDatabase("createChupaBadge(String chupaCommunId)");
			return (int)id;
		}

		private void updateChupaBadge(String chupaCommunId, int count) {
			//			SQLiteDatabase db = this.getWritableDatabase();
			SQLiteDatabase db = this.openDatabase("updateChupaBadge(String chupaCommunId, int count)");

			ContentValues values = new ContentValues();
			values.put(CHUPA_BADGE_COUNT, count);

			db.update(CHUPA_BADGE_TABLE_NAME, values, CHUPA_BADGE_COMMUN_ID + " = ?", new String[]{ chupaCommunId });
			this.closeDatabase("updateChupaBadge(String chupaCommunId, int count)");
		}

		private void clearChupaBadge(String chupaCommunId) {
			this.updateChupaBadge(chupaCommunId, 0);
		}

		private int getChupaBadgeNum(String chupaCommunId) {
			String selectQuery = "SELECT * FROM " + CHUPA_BADGE_TABLE_NAME +
					" WHERE " + CHUPA_BADGE_COMMUN_ID + " = ?";
			//			SQLiteDatabase db = this.getReadableDatabase();
			SQLiteDatabase db = this.openDatabase("getChupaBadgeNum(String chupaCommunId)");
			Cursor cursor = db.rawQuery(selectQuery, new String[]{ chupaCommunId });

			// looping through all rows and adding to list
			int ret = -1;
			if (cursor != null && cursor.moveToFirst()) {
				ret = cursor.getInt(2);
			}

			this.closeDatabase("getChupaBadgeNum(String chupaCommunId)");
			return ret;
		}

		private int getAllChupaBadgeNum() {
			String selectQuery = "SELECT * FROM " + CHUPA_BADGE_TABLE_NAME;
			SQLiteDatabase db = this.openDatabase("getAllChupaBadgeNum()");
			Cursor cursor = db.rawQuery(selectQuery, null);

			// looping through all rows and adding to list
			int total = 0;
			if (cursor != null && cursor.moveToFirst()) {
				do {
					total += cursor.getInt(2);
				} while(cursor.moveToNext());
			}

			this.closeDatabase("getAllChupaBadgeNum()");
			return total;
		}

		private void increaseChupaBadgeNum(String chupaCommunId) {
			int badgeNum = this.getChupaBadgeNum(chupaCommunId);
			if (badgeNum == -1) {
				this.createChupaBadge(chupaCommunId);
				badgeNum = 0;
			}
			this.updateChupaBadge(chupaCommunId, ++badgeNum);
		}

		private void cleareAllChupaBadgeNum() {
			SQLiteDatabase db = this.openDatabase("cleareAllChupaBadgeNum()");
			db.delete(CHUPA_BADGE_TABLE_NAME, null, null);
			this.closeDatabase("cleareAllChupaBadgeNum()");
		}
	}
}