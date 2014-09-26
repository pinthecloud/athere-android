package com.pinthecloud.athere.model;

public class SquareUser {
	@com.google.gson.annotations.SerializedName("id")
	private String id;
	@com.google.gson.annotations.SerializedName("squareId")
	private String squareId;
	@com.google.gson.annotations.SerializedName("userId")
	private String userId;
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSquareId() {
		return squareId;
	}
	public void setSquareId(String squareId) {
		this.squareId = squareId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
}
