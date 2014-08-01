package com.pinthecloud.athere.interfaces;

public class AhException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5944663372661859514L;
	private String message;
	private Exception ex;
	
	public AhException(){
		super();
	}
	
	public AhException(Exception ex, String message) {
		this.ex = ex;
		this.message = message;
	}
	
	public AhException(String string) {
		// TODO Auto-generated constructor stub
		this.ex = new Exception(string);
		this.message = string;
	}

	public String getMessage() {
		return message;
	}
	
	@Override
	public String toString() {
		return ex.toString() + " ==[ "+ message + " ]==";
	}
//	
//	@Override
//	public void printStackTrace() {
//		// TODO Auto-generated method stub
//		super.printStackTrace();
//		System.err.print(this.getMessage());
//	}
}
