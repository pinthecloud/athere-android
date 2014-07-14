package com.pinthecloud.athere.model;

import android.os.Parcel;
import android.os.Parcelable;

public class AhMessage implements Parcelable {

	public enum MESSAGE_TYPE { 
		SQUARE("SQUARE"), ROOM("ROOM"), SHOUTING("SHOUTING"); 

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
	}


	public void readToParcel(Parcel in){
		id = in.readString();
		type = in.readString();
		content = in.readString();
		sender = in.readString();
		senderId = in.readString();
		receiver = in.readString();
		receiverId = in.readString();
	}


	public boolean isMine(String id){
		return this.sender == id;
		// TODO return this.senderId == id;
	}
}
