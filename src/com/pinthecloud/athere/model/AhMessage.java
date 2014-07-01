package com.pinthecloud.athere.model;

public class AhMessage {
	@com.google.gson.annotations.SerializedName("id")
	private String id;
	@com.google.gson.annotations.SerializedName("type")
 	private String type; //{ INPUT, OUTPUT, SHOUTING, EXCEPTION, ETC },
	@com.google.gson.annotations.SerializedName("content")
	private String content;
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
	
}


