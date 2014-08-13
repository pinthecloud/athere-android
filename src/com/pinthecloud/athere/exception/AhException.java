package com.pinthecloud.athere.exception;

import com.pinthecloud.athere.fragment.AhFragment;

public class AhException extends RuntimeException {

	private static final long serialVersionUID = -5944663372661859514L;
	private TYPE type;

	public AhException(){
		super();
	}
	
	public AhException(String string) {
		super(string);
	}
	
	public AhException(TYPE type) {
		this.type = type;
	}
	
	public AhException(TYPE type,AhFragment fragment) {
		this.type = type;
	}
	
	public AhException(String string, TYPE type) {
		super(string);
		this.type = type;
	}

	public AhException(Exception ex, String message) {
		super(message , ex);
		//this.message = message;
	}

	public AhException(Exception ex, String message, TYPE type) {
		super(message, ex);
		this.type = type;
	}

	public TYPE getType() {
		return type;
	}

	@Override
	public String toString() {
		String str = "";
		if (super.getMessage() != null) {
			str += " message : " + super.getMessage() + "";
		}
		if (type != null) {
			str += " type : " + this.type + "";
		}
		return " { "+ str + " }";
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
