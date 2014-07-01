package com.pinthecloud.athere.model;

public class User {
	@com.google.gson.annotations.SerializedName("id")
	private String id;
	@com.google.gson.annotations.SerializedName("nickName")
	private String nickName;
	@com.google.gson.annotations.SerializedName("profilePic")
	private String profilePic;
	@com.google.gson.annotations.SerializedName("mobileId")
	private String mobileId;
	
	public User() {
		// TODO Auto-generated constructor stub
	}
	public User(String id, String nickName, String profilePic, String mobileId) {
		super();
		this.id = id;
		this.nickName = nickName;
		this.profilePic = profilePic;
		this.mobileId = mobileId;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getProfilePic() {
		return profilePic;
	}
	public void setProfilePic(String profilePic) {
		this.profilePic = profilePic;
	}
	public String getMobileId() {
		return mobileId;
	}
	public void setMobileId(String mobileId) {
		this.mobileId = mobileId;
	}
	
	@Override
	public boolean equals(Object o) {
		return true;
	}
	
	@Override
	public String toString() {
		return getNickName();
	}
}
