package com.pinthecloud.athere.exception;

import com.pinthecloud.athere.fragment.AhFragment;

public class AhException extends RuntimeException {

	private static final long serialVersionUID = -5944663372661859514L;
	
	private AhException.TYPE type;
	private AhFragment from;
	private String methodName;
	private Object parameter;

	public AhException(String string) {
		super(string);
	}
	
	public AhException(TYPE type) {
		this.type = type;
		this.from = null;
	}

	public AhException(AhFragment from, String methodName, TYPE type) {
		this.from = from;
		this.type = type;
		this.methodName = methodName;
	}

	public AhException(AhFragment from, String methodName, TYPE type, Object parameter) {
		this.from = from;
		this.type = type;
		this.methodName = methodName;
		this.parameter = parameter;
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

	public Object getParameter() {
		return parameter;
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
		GCM_REGISTRATION_FAIL,
		BLOB_STORAGE_ERROR,
		SD_CARD_FAIL,
		LOCATION_CONNECTION_FAILED,
		DUPLICATED_NICK_NAME
	}
}
