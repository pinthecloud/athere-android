package com.pinthecloud.athere.model;

public class AhMessage {
	
	public enum MESSAGE_TYPE { 
		INPUT("INPUT"), OUTPUT("OUTPUT"), SHOUTING("SHOUTING"), EXCEPTION("EXCEPTION"), ETC("ETC"); 
		
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
	
	@com.google.gson.annotations.SerializedName("id")
	private String id;
	@com.google.gson.annotations.SerializedName("type")
 	private String type; //{ INPUT, OUTPUT, SHOUTING, EXCEPTION, ETC }
	@com.google.gson.annotations.SerializedName("content")
	private String content;
	@com.google.gson.annotations.SerializedName("sender")
	private String sender;
	@com.google.gson.annotations.SerializedName("senderId")
	private String senderId;
	@com.google.gson.annotations.SerializedName("receiver")
	private String receiver;
	@com.google.gson.annotations.SerializedName("receiverId")
	private String receiverId;
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
}


