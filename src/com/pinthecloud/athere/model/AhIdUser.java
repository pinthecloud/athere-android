package com.pinthecloud.athere.model;

public class AhIdUser {
	@com.google.gson.annotations.SerializedName("id")
	private String id;
	@com.google.gson.annotations.SerializedName("ahId")
	private String ahId;
	@com.google.gson.annotations.SerializedName("password")
	private String password;
	@com.google.gson.annotations.SerializedName("registrationId")
	private String registrationId;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getAhId() {
		return ahId;
	}
	public void setAhId(String ahId) {
		this.ahId = ahId;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getRegistrationId() {
		return registrationId;
	}
	public void setRegistrationId(String registrationId) {
		this.registrationId = registrationId;
	}
}
