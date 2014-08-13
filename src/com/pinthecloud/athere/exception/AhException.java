package com.pinthecloud.athere.exception;

import com.pinthecloud.athere.fragment.AhFragment;

public class AhException extends RuntimeException {

	private static final long serialVersionUID = -5944663372661859514L;
	
	private AhException.TYPE type;
	private AhFragment from;
	private String methodName;

	public AhException(String string) {
		super(string);
	}

	public AhException(AhFragment from, String methodName, TYPE type) {
		this.from = from;
		this.type = type;
		this.methodName = methodName;
	}

	public TYPE getType() {
		return type;
	}
	
	public AhFragment fromWho() {
		return from;
	}
	
	public String getMethodName() {
		return methodName;
	}

	@Override
	public String toString() {
		if (super.getMessage() != null) { 
			return "{ message : " + super.getMessage() + " }";
		}
		return "{ type : " + type + "," +
				" from : " + from.getClass().getSimpleName()  + "," +
				" method : " + methodName + " }";
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
