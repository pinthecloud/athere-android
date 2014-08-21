package com.pinthecloud.athere.database;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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

	public MessageDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		badgeDBHelper = new BadgeDBHelper(context);
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

	// Adding new contact
	public int addMessage(AhMessage message) {
		SQLiteDatabase db = this.getWritableDatabase();

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
		long id = db.insert(TABLE_NAME, null, values);
		db.close(); // Closing database connection
		return (int)id;
	}


	private void putValueWithoutNull(ContentValues values, String id, String value){
		if (value == null) value = "";
		values.put(id, value);
	}


	// Getting All Messages
	public void updateMessages(int id, AhMessage message) {
		SQLiteDatabase db = this.getWritableDatabase();

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
		db.update(TABLE_NAME, values, ID + " = ?", new String[]{ String.valueOf(id) });
		db.close(); // Closing database connection
	}


	// Getting single contact
	public AhMessage getMessage(int id) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_NAME, null, ID + " = ?",
				new String[] { String.valueOf(id) }, null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();

		AhMessage message = convertToMessage(cursor);
		db.close();
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


	// Getting All Messages
	public List<AhMessage> getAllMessages() {
		List<AhMessage> messages = new ArrayList<AhMessage>();

		// Select All Query
		String selectQuery = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + ID;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				messages.add(convertToMessage(cursor));
			} while (cursor.moveToNext());
		}
		db.close();
		// return contact list
		return messages;
	}

	public List<AhMessage> getAllMessages(String type) {
		List<AhMessage> messages = new ArrayList<AhMessage>();

		// Select All Query
		String selectQuery = "SELECT * FROM " + TABLE_NAME +
				" WHERE " + TYPE + " = ?" +
				" ORDER BY " + ID;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, new String[]{ type });

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				messages.add(convertToMessage(cursor));
			} while (cursor.moveToNext());
		}
		db.close();
		// return contact list
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
				" ORDER BY " + ID;

		SQLiteDatabase db = this.getReadableDatabase();
		String[] argArray = typeArr.toArray(new String[typeArr.size()]);
		Cursor cursor = db.rawQuery(selectQuery, argArray);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				messages.add(convertToMessage(cursor));
			} while (cursor.moveToNext());
		}
		db.close();
		// return contact list
		return messages;
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
				" ORDER BY " + ID;

		SQLiteDatabase db = this.getReadableDatabase();
		String[] argArray = typeArr.toArray(new String[typeArr.size()]);
		Cursor cursor = db.rawQuery(selectQuery, argArray);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				messages.add(convertToMessage(cursor));
			} while (cursor.moveToNext());
		}
		db.close();
		// return contact list
		return messages;
	}

	public List<AhMessage> getAllMessages(AhMessage.TYPE type) {
		return this.getAllMessages(type.toString());
	}

	public boolean isEmpty() {
		String selectQuery = "SELECT COUNT(*) FROM " + TABLE_NAME;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		cursor.moveToFirst();
		int count = cursor.getInt(0);
		db.close();
		return count == 0;
	}

	public boolean isEmpty(String type) {
		String selectQuery = "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE " + TYPE + " = ?";

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, new String[]{ type });

		cursor.moveToFirst();
		int count = cursor.getInt(0);
		db.close();
		return count == 0;
	}

	public boolean isEmpty(String... types){

		List<String> typeArr = new ArrayList<String>();
		StringBuilder whereStr = new StringBuilder();
		for (String type : types) {
			typeArr.add(type);
			whereStr.append(TYPE + "=? OR ");
		}
		whereStr.delete(whereStr.length()- 3, whereStr.length());


		String selectQuery = "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE " + whereStr.toString();
		String[] argArray = typeArr.toArray(new String[typeArr.size()]);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, argArray);

		cursor.moveToFirst();
		int count = cursor.getInt(0);
		db.close();
		return count == 0;
	}

	public boolean isEmpty(AhMessage.TYPE... types){
		List<String> typeArr = new ArrayList<String>();
		StringBuilder whereStr = new StringBuilder();
		for (AhMessage.TYPE type : types) {
			typeArr.add(type.toString());
			whereStr.append(TYPE + "=? OR ");
		}
		whereStr.delete(whereStr.length()- 3, whereStr.length());


		String selectQuery = "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE " + whereStr.toString();
		String[] argArray = typeArr.toArray(new String[typeArr.size()]);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, argArray);

		cursor.moveToFirst();
		int count = cursor.getInt(0);
		db.close();
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
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_NAME, ID + " = ?",
				new String[] { String.valueOf(messageId) });
		db.close();
	}

	public void deleteAllMessages() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_NAME, null ,null);
		db.close();
	}

	public void deleteAllMessages(String strType) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_NAME, TYPE + " = ?", new String[]{ strType });
		db.close();
	}

	public void deleteAllMessages(AhMessage.TYPE type) {
		this.deleteAllMessages(type.toString());
	}

	public List<AhMessage> getLastChupas(){
		List<AhMessage> list = new ArrayList<AhMessage>();

		// Select All Query
		String selectQuery = "SELECT t1.* FROM " + TABLE_NAME + " t1" +
				" JOIN (SELECT MAX(id) id FROM " + TABLE_NAME +
				" GROUP BY " + CHUPA_COMMUN_ID + ") t2 on t1.id = t2.id" +
				" WHERE " + TYPE + " = ?" +
				" ORDER BY id DESC";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, new String[]{ AhMessage.TYPE.CHUPA.toString()});

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				list.add(convertToMessage(cursor));
			} while (cursor.moveToNext());
		}
		db.close();
		return list;
	}

	public List<AhMessage> getChupasByCommunId(String chupaCommunId){
		List<AhMessage> list = new ArrayList<AhMessage>();

		// Select All Query
		String selectQuery = "SELECT * FROM " + TABLE_NAME +
				" WHERE " + TYPE + "=? AND " + CHUPA_COMMUN_ID + " = ?" +
				" ORDER BY id";

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, new String[]{ AhMessage.TYPE.CHUPA.toString() 
				,chupaCommunId});

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				list.add(convertToMessage(cursor));
			} while (cursor.moveToNext());
		}
		db.close();
		return list;
	}


	private void sortMessages(List<AhMessage> list){
		Collections.sort(list, new Comparator<AhMessage>() {

			@Override
			public int compare(AhMessage lhs, AhMessage rhs) {
				int left = Integer.valueOf(lhs.getId());
				int right = Integer.valueOf(rhs.getId());
				if (left == right) return 0;
				return left > right ? 1: -1;
			}
		});
	}



	public int getBadgeNum(String chupaCommunId) {
		return badgeDBHelper.getBadgeNum(chupaCommunId);
	}

	public int getAllBadgeNum() {
		return badgeDBHelper.getAllBadgeNum();
	}

	public void increaseBadgeNum(String chupaCommunId) {
		badgeDBHelper.increaseBadgeNum(chupaCommunId);
	}


	public void clearBadgeNum(String chupaCommunId) {
		badgeDBHelper.deleteBadge(chupaCommunId);
	}

	public void cleareAllBadgeNum() {
		badgeDBHelper.cleareAllBadgeNum();
	}


	private class BadgeDBHelper extends SQLiteOpenHelper {
		// Database Version
		private static final int BADGE_DATABASE_VERSION = 2;

		// Database Name
		private static final String BADGE_DATABASE_NAME = "badge_db";

		// Badges table name
		private static final String BADGE_TABLE_NAME = "badges";

		// Messages Table Columns names
		private final String BADGE_ID = "badge_id";
		private final String BADGE_CHUPA_COMMUN_ID = "badge_chupa_commun_id";
		private final String COUNT = "count";

		public BadgeDBHelper(Context context) {
			super(context, BADGE_DATABASE_NAME, null, BADGE_DATABASE_VERSION);
		}

		// Creating Tables
		@Override
		public void onCreate(SQLiteDatabase db) {
			String CREATE_CONTACTS_TABLE = "CREATE TABLE " + BADGE_TABLE_NAME + 
					"("
					+ BADGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," 
					+ BADGE_CHUPA_COMMUN_ID + " TEXT,"
					+ COUNT + " INTEGER"
					+")";
			db.execSQL(CREATE_CONTACTS_TABLE);
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
			db.execSQL("DROP TABLE IF EXISTS " + BADGE_TABLE_NAME);
		}

		private int createBadge(String chupaCommunId){
			SQLiteDatabase db = this.getWritableDatabase();

			ContentValues values = new ContentValues();
			putValueWithoutNull(values, BADGE_CHUPA_COMMUN_ID, chupaCommunId);
			values.put(COUNT, 0);

			// Inserting Row
			long id = db.insert(BADGE_TABLE_NAME, null, values);
			db.close(); // Closing database connection
			return (int)id;
		}
		private void updateBadge(String chupaCommunId, int count) {
			SQLiteDatabase db = this.getWritableDatabase();

			ContentValues values = new ContentValues();
			values.put(COUNT, count);

			db.update(BADGE_TABLE_NAME, values, BADGE_CHUPA_COMMUN_ID + " = ?", new String[]{ chupaCommunId });
			db.close(); // Closing database connection
		}
		private void deleteBadge(String chupaCommunId) {
			SQLiteDatabase db = this.getWritableDatabase();
			db.delete(BADGE_TABLE_NAME, BADGE_CHUPA_COMMUN_ID + " = ?", new String[]{ chupaCommunId });
			db.close();
		}
		private int getBadgeNum(String chupaCommunId) {
			String selectQuery = "SELECT * FROM " + BADGE_TABLE_NAME +
					" WHERE " + BADGE_CHUPA_COMMUN_ID + " = ?";
			SQLiteDatabase db = this.getReadableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, new String[]{ chupaCommunId });

			// looping through all rows and adding to list
			if (cursor.moveToFirst()) {
				int ret = cursor.getInt(2);
				db.close();
				return ret;
			}
			db.close();
			return 0;
		}

		private int getAllBadgeNum() {
			String selectQuery = "SELECT * FROM " + BADGE_TABLE_NAME;
			SQLiteDatabase db = this.getReadableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);
			int total = 0;
			// looping through all rows and adding to list
			if (cursor.moveToFirst()) {
				do {
					total += cursor.getInt(2);
				} while(cursor.moveToNext());
			}
			db.close();
			return total;
		}

		private void increaseBadgeNum(String chupaCommunId) {
			int badgeNum = this.getBadgeNum(chupaCommunId);
			if (badgeNum == 0) {
				this.createBadge(chupaCommunId);
			}
			this.updateBadge(chupaCommunId, badgeNum + 1);
		}


		//		private void clearBadgeNum(String chupaCommunId) {
		//			this.deleteBadge(chupaCommunId);
		//		}

		private void cleareAllBadgeNum() {
			SQLiteDatabase db = this.getWritableDatabase();
			db.delete(BADGE_TABLE_NAME, null, null);
			db.close();
		}
	}
}