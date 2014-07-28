package com.pinthecloud.athere.model;

public class MessageBox {

	private AhMessage message;
	private User user;
	
	public MessageBox(){
		this(null, null);
	}
	public MessageBox(AhMessage message){
		this(message, null);
	}
	public MessageBox(AhMessage message, User user){
		this.message = message;
		this.user = user;
	}
	public AhMessage getMessage() {
		return message;
	}
	public void setMessage(AhMessage message) {
		this.message = message;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	
}
