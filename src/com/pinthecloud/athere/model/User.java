package com.pinthecloud.athere.model;

public class User {
	@com.google.gson.annotations.SerializedName("id")
	private String id;
	@com.google.gson.annotations.SerializedName("nickName")
	private String nickName;
	@com.google.gson.annotations.SerializedName("profilePic")
	private String profilePic;
	@com.google.gson.annotations.SerializedName("mobileId")
	private String mobileId = "Android";
	@com.google.gson.annotations.SerializedName("registrationId")
	private String registrationId;
	@com.google.gson.annotations.SerializedName("isMale")
	private boolean isMale;
	@com.google.gson.annotations.SerializedName("companyNum")
	private int companyNum;
	@com.google.gson.annotations.SerializedName("age")
	private int age;
	@com.google.gson.annotations.SerializedName("squareId")
	private String squareId;
	
	public User() {
		
	}
	public User(String id, String nickName, String profilePic, String mobileId,
			String registrationId, boolean isMale, int companyNum, int age,
			String squareId) {
		super();
		this.id = id;
		this.nickName = nickName;
		this.profilePic = profilePic;
		this.mobileId = mobileId;
		this.registrationId = registrationId;
		this.isMale = isMale;
		this.companyNum = companyNum;
		this.age = age;
		this.squareId = squareId;
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
	public String getRegistrationId() {
		return registrationId;
	}
	public void setRegistrationId(String registrationId) {
		this.registrationId = registrationId;
	}
	public boolean isMale() {
		return isMale;
	}
	public void setMale(boolean isMale) {
		this.isMale = isMale;
	}
	public int getCompanyNum() {
		return companyNum;
	}
	public void setCompanyNum(int companyNum) {
		this.companyNum = companyNum;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public String getSquareId() {
		return squareId;
	}
	public void setSquareId(String squareId) {
		this.squareId = squareId;
	}
	
	
}
