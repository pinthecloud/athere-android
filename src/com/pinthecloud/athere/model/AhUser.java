package com.pinthecloud.athere.model;

import java.util.Random;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonObject;
import com.pinthecloud.athere.AhGlobalVariable;

public class AhUser implements Parcelable{
	@com.google.gson.annotations.SerializedName("id")
	private String id;
	@com.google.gson.annotations.SerializedName("nickName")
	private String nickName;
	@com.google.gson.annotations.SerializedName("profilePic")
	private String profilePic;
	@com.google.gson.annotations.SerializedName("mobileId")
	private String mobileId = AhGlobalVariable.ANDROID;
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
	@com.google.gson.annotations.SerializedName("isChupaEnable")
	private boolean isChupaEnable;

	public static final Parcelable.Creator<AhUser> CREATOR = new Creator<AhUser>(){
		public AhUser createFromParcel(Parcel in){
			return new AhUser(in);
		}
		public AhUser[] newArray(int size){
			return new AhUser[size];
		}
	};

	public AhUser() {

	}
	public AhUser(Parcel in){
		readToParcel(in);
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
	public boolean isChupaEnable() {
		return isChupaEnable;
	}
	public void setChupaEnable(boolean isChupaEnable) {
		this.isChupaEnable = isChupaEnable;
	}
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int frags) {
		dest.writeString(id);
		dest.writeString(nickName);
		dest.writeString(profilePic);
		dest.writeString(mobileId);	
		dest.writeString(registrationId);	
		dest.writeInt(isMale? 1 : 0);	
		dest.writeInt(companyNum);	
		dest.writeInt(age);	
		dest.writeString(squareId);
		dest.writeInt(isChupaEnable? 1 : 0);
	}
	public void readToParcel(Parcel in){
		id = in.readString();
		nickName = in.readString();
		profilePic = in.readString();
		mobileId = in.readString();
		registrationId = in.readString();
		isMale = in.readInt() == 1;
		companyNum = in.readInt();
		age = in.readInt();
		squareId = in.readString();
		isChupaEnable = in.readInt() == 1;
	}

	public static AhUser addUserTest(){
		AhUser user = new AhUser();
		user.id = getRandomString();
		user.nickName = getRandomString();
		user.profilePic = getRandomString();
		user.mobileId = getRandomString();
		user.registrationId = getRandomString();
		user.isMale = getRandomInt() < 20;
		user.companyNum = getRandomInt();
		user.age = getRandomInt();
		user.squareId = getRandomString();
		user.isChupaEnable = getRandomInt() < 20;
		return user;
	}

	private static String getRandomString(){
		char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
		StringBuilder sb = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < random.nextInt(25); i++) {
			char c = chars[random.nextInt(chars.length)];
			sb.append(c);
		}
		String output = sb.toString();
		return output;
	}

	private static int getRandomInt(){
		Random random = new Random();
		return random.nextInt(40);
	}

	public String toString() {
		return 
				"{ id : "+this.id + " \n "+
				" nickName : "+this.nickName + " \n "+
				" mobileId : "+this.mobileId + " \n "+
				" registrationId : "+ (this.registrationId.length() < 20 ? 
						this.registrationId.substring(0, this.registrationId.length() - 1) : this.registrationId.substring(0, 20)) + " \n "+
						" isMale : "+this.isMale + " \n "+
						" companyNum : "+this.companyNum + " \n "+
						" age : "+this.age + " \n "+
						" isChupaEnable : "+this.isChupaEnable + " \n "+
						" squareId : "+this.squareId + " }";
	}

	public JsonObject toJson() {
		JsonObject jo = new JsonObject();
		jo.addProperty("id", this.id);
		jo.addProperty("nickName", this.nickName);
		jo.addProperty("profilePic", this.profilePic);
		jo.addProperty("mobileId", this.mobileId);
		jo.addProperty("registrationId", this.registrationId);
		jo.addProperty("isMale", this.isMale);
		jo.addProperty("companyNum", this.companyNum);
		jo.addProperty("age", this.age);
		jo.addProperty("squareId", this.squareId);
		jo.addProperty("isChupaEnable", this.isChupaEnable);

		return jo;
	}
}
