package com.pinthecloud.athere.model;

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
	}
	public AhMessage(Parcel in){
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
	@Override
	public String toString() {
		return this.id + this.type + this.content + this.sender + this.senderId +
				this.receiver + this.receiverId;
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
	}


	public boolean isMine(String id){
		return this.senderId.equals(id);
	}
	
	public static class MessageBuilder {
		
//		private String type = "";
//		private String content = "";
//		private String sender = "";
//		private String senderId = "";
//		private String receiver = "";
//		private String receiverId = "";
		
	}
}
