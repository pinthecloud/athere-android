package com.pinthecloud.athere.model;

public class AhMessage {
	
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
	        // TODO Auto-generated method stub
	        return getValue();
	    }
	};
	
	private String id;
 	private String type = "";
	private String content = "";
	private String sender = "";
	private String senderId = "";
	private String receiver = "";
	private String receiverId = "";
	
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
	
}