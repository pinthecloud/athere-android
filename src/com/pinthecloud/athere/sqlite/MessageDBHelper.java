package com.pinthecloud.athere.sqlite;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.pinthecloud.athere.model.AhMessage;

public class MessageDBHelper  extends SQLiteOpenHelper {

	// All Static variables
	// Database Version
	private static int DATABASE_VERSION = 1;
	static{
		Random r= new Random();
		DATABASE_VERSION = r.nextInt(10) + 1; 
	}

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


	public MessageDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
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
				+ TIME_STAMP + " TEXT"
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
	public void addMessage(AhMessage message) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		//values.put(ID, message.getId());
		//putValueWithoutNull(values, ID, message.getId());
		
		//values.put(TYPE, message.getType());
		putValueWithoutNull(values, TYPE, message.getType());
		
		//values.put(CONTENT, message.getContent());
		putValueWithoutNull(values, CONTENT, message.getContent());
		
		//values.put(SENDER, message.getSender());
		putValueWithoutNull(values, SENDER, message.getSender());
		
		//values.put(SENDER_ID, message.getSenderId());
		putValueWithoutNull(values, SENDER_ID, message.getSenderId());
		
		//values.put(RECEIVER, message.getReceiver());
		putValueWithoutNull(values, RECEIVER, message.getReceiver());
		
		//values.put(RECEIVER_ID, message.getReceiverId());
		putValueWithoutNull(values, RECEIVER_ID, message.getReceiverId());

		//values.put(TIME_STAMP, message.getTimeStamp());
		putValueWithoutNull(values, TIME_STAMP, message.getTimeStamp());
		
		// Inserting Row
		db.insert(TABLE_NAME, null, values);
		db.close(); // Closing database connection
	}
	
	private void putValueWithoutNull(ContentValues contentValue, String id, String value){
		if (value == null) value = "";
		contentValue.put(id, value);
	}
	
	// Getting single contact
	public AhMessage getMessage(int id) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_NAME, null, ID + " = ?",
				new String[] { String.valueOf(id) }, null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();

		return convertToMessage(cursor);
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

		AhMessage message = new AhMessage();
		message.setId(_id);
		message.setType(type);
		message.setContent(content);
		message.setSender(sender);
		message.setSenderId(senderId);
		message.setReceiver(receiver);
		message.setReceiverId(receiverId);
		message.setTimeStamp(timeStamp);
		return message;
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
		// return contact list
		return messages;
	}
	
	public List<AhMessage> getAllMessages(AhMessage.MESSAGE_TYPE type) {
		return this.getAllMessages(type.toString());
	}
	
	public boolean isEmpty() {
		String selectQuery = "SELECT * FROM " + TABLE_NAME;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		return !cursor.moveToFirst();
	}
	
	public boolean isEmpty(String type) {
		String selectQuery = "SELECT * FROM " + TABLE_NAME + " WHERE " + TYPE + " = ?";

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, new String[]{ type });

		return !cursor.moveToFirst();
	}
	
	public boolean isEmpty(AhMessage.MESSAGE_TYPE type) {
		return this.isEmpty(type.toString());
	}
	
	public List<AhMessage> popAllMessages() {
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
		this.deleteAllMessages();
		// return contact list
		return messages;
	}

	public List<AhMessage> popAllMessages(String type) {
		List<AhMessage> messages = new ArrayList<AhMessage>();

		// Select All Query

		String selectQuery = "SELECT * FROM " + TABLE_NAME +
				" WHERE " + TYPE + " = ?" +
				" ORDER BY " + ID;
		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				messages.add(convertToMessage(cursor));
			} while (cursor.moveToNext());
		}
		this.deleteAllMessages(type);
		// return contact list
		return messages;
	}
	
	public List<AhMessage> popAllMessages(AhMessage.MESSAGE_TYPE type) {
		return this.popAllMessages(type.toString());
	}

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
	
	public void deleteAllMessages(AhMessage.MESSAGE_TYPE type) {
		this.deleteAllMessages(type.toString());
	}
}