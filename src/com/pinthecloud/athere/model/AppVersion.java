package com.pinthecloud.athere.model;

public class AppVersion {
	
	@com.google.gson.annotations.SerializedName("id")
	private String id;
	@com.google.gson.annotations.SerializedName("version")
	private double version;
	@com.google.gson.annotations.SerializedName("type")
	private String type;
	
	
	public enum TYPE {
		MANDATORY,
		OPTIONAL
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public double getVersion() {
		return version;
	}
	public void setVersion(double version) {
		this.version = version;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}
