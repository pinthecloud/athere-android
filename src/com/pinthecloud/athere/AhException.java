package com.pinthecloud.athere;

public class AhException extends RuntimeException {

	private static final long serialVersionUID = -5944663372661859514L;
	private String message;
	private TYPE type;

	public AhException(){
		super();
	}
	
	public AhException(String string) {
		super(string);
		this.message = string;
	}
	
	public AhException(TYPE type) {
		this.type = type;
	}
	
	public AhException(String string, TYPE type) {
		super(string);
		this.message = string;
		this.type = type;
	}

	public AhException(Exception ex, String message) {
		super(ex);
		this.message = message;
	}

	public AhException(Exception ex, String message, TYPE type) {
		super(ex);
		this.message = message;
		this.type = type;
	}

	public String getMessage() {
		return message;
	}
	
	public TYPE getType() {
		return type;
	}

	@Override
	public String toString() {
		String str = "";
		if (message != null) {
			str += " [message : " + this.message + "]";
		}
		if (type != null) {
			str += " [type : " + this.type + "]";
		}
		return super.toString() + str;
	}
	
	public enum TYPE {
		INTERNET_NOT_CONNECTED,
		SERVER_ERROR,
		PARSING_ERROR,
		MESSAGE_ERROR,
		NO_USER_ID,
		GCM_REGISTRATION_FAIL
	}
}
