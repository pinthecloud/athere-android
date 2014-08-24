package com.pinthecloud.athere.model;

import java.util.Random;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.Time;

import com.pinthecloud.athere.AhApplication;
import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.helper.PreferenceHelper;

public class AhMessage implements Parcelable {

	public enum STATUS{
		FAIL(-1),
		SENDING(0),
		SENT(1);

		private final int value;

		private STATUS(final int value) {
			this.value = value;
		}
		public int getValue() {
			return value;
		}
	}

	public enum TYPE {
		// No user Update
		TALK("TALK"),			// To Square Users
		SHOUTING("SHOUTING"),	// To Square Users
		CHUPA("CHUPA"),			// To Individual

		// User Update
		ENTER_SQUARE("ENTER_SQUARE"),	// To Square Users
		EXIT_SQUARE("EXIT_SQUARE"),		// To Square Users
		UPDATE_USER_INFO("UPDATE_USER_INFO"), // To Square Users
		MESSAGE_READ("MESSAGE_READ");

		private final String value;

		private TYPE(final String value) {
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

	private String id;
	private String type;
	private String content;
	private String sender;
	private String senderId;
	private String receiver;
	private String receiverId;
	private String timeStamp;
	private String chupaCommunId;
	private int status = STATUS.SENDING.getValue();


	public static final Parcelable.Creator<AhMessage> CREATOR = new Creator<AhMessage>(){
		public AhMessage createFromParcel(Parcel in){
			return new AhMessage(in);
		}
		public AhMessage[] newArray(int size){
			return new AhMessage[size]; 
		}
	};


	private AhMessage() {
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
	public String getContent() {
		return content;
	}
	public String getSender() {
		return sender;
	}
	public String getSenderId() {
		return senderId;
	}
	public String getReceiver() {
		return receiver;
	}
	public String getReceiverId() {
		return receiverId;
	}
	public String getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp() {
		Time time = new Time();
		time.setToNow();
		this.timeStamp = time.format("%Y%m%d%H%M%S");
	}
	public String getChupaCommunId() {
		return chupaCommunId;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public void setStatus(STATUS status) {
		this.status = status.getValue();
	}


	@Override
	public String toString() {
		return "{ id : "+this.id + " \n " + 
				" type : "+this.type + " \n " +
				" content : "+this.content + " \n " + 
				" sender : "+this.sender + " \n " + 
				" senderId : "+this.senderId + " \n " +
				" receiver : "+this.receiver + " \n " + 
				" receiverId : "+this.receiverId + " \n " +
				" timeStamp : "+this.timeStamp + " \n " +
				" chupaCommunId : "+this.chupaCommunId  + " \n " +
				" status : "+this.status + " }";
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
		dest.writeString(timeStamp);
		dest.writeString(chupaCommunId);
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
		timeStamp = in.readString();
		chupaCommunId = in.readString();
		status = in.readInt();
	}

	public boolean isMine(){
		PreferenceHelper pref = AhApplication.getInstance().getPref();
		return this.senderId.equals(pref.getString(AhGlobalVariable.USER_ID_KEY));
	}

	public boolean isNotification(){
		return (type.equals(TYPE.ENTER_SQUARE.toString()) || type.equals(TYPE.EXIT_SQUARE.toString()) 
				|| type.equals(TYPE.UPDATE_USER_INFO.toString()));
	}

	public static String buildChupaCommunId(String id0, String id1) {
		if (id0.compareTo(id1) > 0) {
			return  id0 + id1;
		} else {
			return id1 + id0;
		}
	}


	/*
	 * Make random message for test
	 */
	private static int count = 0;
	public static AhMessage buildMessage(String type){
		AhMessage message = new AhMessage();

		//message.id = ("id-"+(count));
		message.type = (type);
		message.content = ("Content-" + (count));
		message.sender = ("sender-"+count);
		message.senderId = ("senderId-"+count);
		message.receiver = ("receiver-"+count);
		message.receiverId = ("receiverId-"+count);
		count++;

		return message;
	}


	public static AhMessage buildMessage(AhMessage.TYPE type){
		return buildMessage(type.toString());
	}


	public static AhMessage buildMessage(){
		AhMessage.TYPE type;
		Random r = new Random();

		int _count = r.nextInt(6);
		switch(_count){
		case 0: type = AhMessage.TYPE.TALK; break;
		case 1: type = AhMessage.TYPE.CHUPA; break;
		case 2: type = AhMessage.TYPE.SHOUTING; break;
		case 3: type = AhMessage.TYPE.ENTER_SQUARE; break;
		case 4: type = AhMessage.TYPE.EXIT_SQUARE; break;
		case 5: type = AhMessage.TYPE.UPDATE_USER_INFO; break;
		default: type = AhMessage.TYPE.CHUPA; break;
		}

		return buildMessage(type);
	}


	/*
	 * Message Builder Class
	 */
	public static class Builder {

		private String id;
		private String type;
		private String content;
		private String sender;
		private String senderId;
		private String receiver;
		private String receiverId;
		private String timeStamp = null;
		private String chupaCommunId = null;
		private int status = STATUS.SENDING.getValue();

		public Builder setId(String id) {
			this.id = id;
			return this;
		}
		public Builder setType(String type) {
			this.type = type;
			return this;
		}
		public Builder setType(AhMessage.TYPE type) {
			this.type = type.toString();
			return this;
		}
		public Builder setContent(String content) {
			this.content = content;
			return this;
		}
		public Builder setSender(String sender) {
			this.sender = sender;
			return this;
		}
		public Builder setSenderId(String senderId) {
			this.senderId = senderId;
			return this;
		}
		public Builder setReceiver(String receiver) {
			this.receiver = receiver;
			return this;
		}
		public Builder setReceiverId(String receiverId) {
			this.receiverId = receiverId;
			return this;
		}
		public Builder setTimeStamp() {
			Time time = new Time();
			time.setToNow();
			this.timeStamp = time.format("%Y%m%d%H%M%S");
			return this;
		}
		public Builder setTimeStamp(String timeStamp) {
			this.timeStamp = timeStamp;
			return this;
		}
		public Builder setChupaCommunId(String chupaCommunId) {
			this.chupaCommunId = chupaCommunId;
			return this;
		}
		public Builder setStatus(int status) {
			this.status = status;
			return this;
		}
		public Builder setStatus(STATUS status) {
			this.status = status.getValue();
			return this;
		}

		public AhMessage build(){
			AhMessage message = new AhMessage();
			message.id = id;
			message.type = type;
			message.content = content;
			message.sender = sender;
			message.senderId = senderId;
			message.receiver = receiver;
			message.receiverId = receiverId;
			message.status = status;

			if (this.timeStamp == null){
				message.timeStamp = "99999999999999";
			} else {
				message.timeStamp = timeStamp;
			}
			
			if (this.chupaCommunId == null){
				message.chupaCommunId = buildChupaCommunId(this.senderId, this.receiverId);
			} else {
				message.chupaCommunId = chupaCommunId;
			}

			return message;
		}
	}
}
