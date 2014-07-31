package com.pinthecloud.athere.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

import android.os.Parcel;
import android.os.Parcelable;

public class AhMessage implements Parcelable {

	public static final int SENDING = 0;
	public static final int SENT = 1;
	public static final int FAIL = -1;

	public enum MESSAGE_TYPE {
		// No user Update
		TALK("TALK"),			// To Square Users
		SHOUTING("SHOUTING"),	// To Square Users
		CHUPA("CHUPA"),			// To Individual
		
		// User Update
		ENTER_SQUARE("ENTER_SQUARE"),	// To Square Users
		EXIT_SQUARE("EXIT_SQUARE"),		// To Square Users
		UPDATE_USER_INFO("UPDATE_USER_INFO"); // To Square Users  

		private final String value;

		private MESSAGE_TYPE(final String value) {
			this.value = value;
		}
		public String getValue() {
			return value;
		}
		@Override
		public String toString() {
			return getValue();
		}
	};

	private String id = "";
	private String type = "";
	private String content = "";
	private String sender = "";
	private String senderId = "";
	private String receiver = "";
	private String receiverId = "";
	private String timeStamp = "";
	private int status = 0;


	public static final Parcelable.Creator<AhMessage> CREATOR = new Creator<AhMessage>(){
		public AhMessage createFromParcel(Parcel in){
			return new AhMessage(in);
		}
		public AhMessage[] newArray(int size){
			return new AhMessage[size]; 
		}
	};

	public AhMessage() {
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.US);
		String time = sdf.format(calendar.getTime());
		this.setTimeStamp(time);
	}
	public AhMessage(Parcel in){
		this();
		readToParcel(in);
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public void setType(MESSAGE_TYPE type) {
		this.type = type.toString();
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	public String getSenderId() {
		return senderId;
	}
	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}
	public String getReceiver() {
		return receiver;
	}
	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}
	public String getReceiverId() {
		return receiverId;
	}
	public void setReceiverId(String receiverId) {
		this.receiverId = receiverId;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getTimeStamp() {
		return timeStamp;
	}
	private void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}
	@Override
	public String toString() {
		return this.id + " : " + 
				this.type + " : " +
				this.content + " : " + 
				this.sender + " : " + 
				this.senderId + " : " +
				this.receiver + " : " + 
				this.receiverId + " : " +
				this.timeStamp;
	}
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(type);
		dest.writeString(content);
		dest.writeString(sender);
		dest.writeString(senderId);
		dest.writeString(receiver);
		dest.writeString(receiverId);
		dest.writeInt(status);
		dest.writeString(timeStamp);
	}

	public void readToParcel(Parcel in){
		id = in.readString();
		type = in.readString();
		content = in.readString();
		sender = in.readString();
		senderId = in.readString();
		receiver = in.readString();
		receiverId = in.readString();
		status = in.readInt();
		timeStamp = in.readString();
	}

	public boolean isMine(String id){
		return this.senderId.equals(id);
	}
	
	private static int count = 0;
	public static AhMessage buildMessage(String type){
		AhMessage message = new AhMessage();
		
		message.setId("id-"+(count));
		message.setType(type);
		message.setContent("Content-" + (count));
		message.setSender("sender-"+count);
		message.setSenderId("senderId-"+count);
		message.setReceiver("receiver-"+count);
		message.setReceiverId("receiverId-"+count);
		count++;
		
		return message;
	}
	
	public static AhMessage buildMessage(AhMessage.MESSAGE_TYPE type){
		return buildMessage(type.toString());
	}
	
	public static AhMessage buildMessage(){
		AhMessage.MESSAGE_TYPE type;
		Random r = new Random();
		
		int _count = r.nextInt(6);
		switch(_count){
			case 0: type = AhMessage.MESSAGE_TYPE.TALK; break;
			case 1: type = AhMessage.MESSAGE_TYPE.CHUPA; break;
			case 2: type = AhMessage.MESSAGE_TYPE.SHOUTING; break;
			case 3: type = AhMessage.MESSAGE_TYPE.ENTER_SQUARE; break;
			case 4: type = AhMessage.MESSAGE_TYPE.EXIT_SQUARE; break;
			case 5: type = AhMessage.MESSAGE_TYPE.UPDATE_USER_INFO; break;
			default: type = AhMessage.MESSAGE_TYPE.CHUPA; break;
		}
		
		return buildMessage(type);
	}
}
