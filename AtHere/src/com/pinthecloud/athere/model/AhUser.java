package com.pinthecloud.athere.model;

import java.util.Calendar;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonObject;
import com.pinthecloud.athere.R;

public class AhUser implements Parcelable{
	@com.google.gson.annotations.SerializedName("id")
	private String id;
	@com.google.gson.annotations.SerializedName("ahId")
	private String ahId;
	@com.google.gson.annotations.SerializedName("mobileId")
	private String mobileId;
	@com.google.gson.annotations.SerializedName("mobileType")
	private String mobileType = "Android";
	@com.google.gson.annotations.SerializedName("registrationId")
	private String registrationId;
	@com.google.gson.annotations.SerializedName("isMale")
	private boolean isMale;
	@com.google.gson.annotations.SerializedName("birthYear")
	private int birthYear;
	@com.google.gson.annotations.SerializedName("profilePic")
	private String profilePic = "NOT_IN_USE";
	@com.google.gson.annotations.SerializedName("nickName")
	private String nickName;
	@com.google.gson.annotations.SerializedName("isChupaEnable")
	private boolean isChupaEnable;


	public AhUser() {

	}
	public AhUser(Parcel in){
		this();
		readToParcel(in);
	}


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
	public String getMobileId() {
		return mobileId;
	}
	public void setMobileId(String mobileId) {
		this.mobileId = mobileId;
	}
	public String getMobileType() {
		return mobileType;
	}
	public void setMobileType(String mobileType) {
		this.mobileType = mobileType;
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
	public int getBirthYear() {
		return birthYear;
	}
	public void setBirthYear(int birthYear) {
		this.birthYear = birthYear;
	}
	public String getProfilePic() {
		return profilePic;
	}
	public void setProfilePic(String profilePic) {
		this.profilePic = profilePic;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public boolean isChupaEnable() {
		return isChupaEnable;
	}
	public void setChupaEnable(boolean isChupaEnable) {
		this.isChupaEnable = isChupaEnable;
	}


	public int getAge() {
		Calendar c = Calendar.getInstance();
		return c.get(Calendar.YEAR) - (getBirthYear() - 1);
	}
	public String getGenderString(Context context){
		if(isMale){
			return context.getResources().getString(R.string.man);
		}else{
			return context.getResources().getString(R.string.woman);
		}
	}


	/*
	 * Parcelable
	 */
	public static final Parcelable.Creator<AhUser> CREATOR = new Creator<AhUser>(){
		public AhUser createFromParcel(Parcel in){
			return new AhUser(in);
		}
		public AhUser[] newArray(int size){
			return new AhUser[size]; 
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(getId());
		dest.writeString(getAhId());
		dest.writeString(getMobileId());
		dest.writeString(getMobileType());
		dest.writeString(getRegistrationId());
		dest.writeInt(isMale() ? 1 : 0);
		dest.writeInt(getBirthYear());
		dest.writeString(getProfilePic());
		dest.writeString(getNickName());
		dest.writeInt(isChupaEnable() ? 1 : 0);
	}

	public void readToParcel(Parcel in){
		setId(in.readString());
		setAhId(in.readString());
		setMobileId(in.readString());
		setMobileType(in.readString());
		setRegistrationId(in.readString());
		setMale(in.readInt() == 1);
		setBirthYear(in.readInt());
		setProfilePic(in.readString());
		setNickName(in.readString());
		setChupaEnable(in.readInt() == 1);
	}


	/*
	 * Utility
	 */
	@Override
	public String toString() {
		return concate(getId(), getAhId(), getMobileId(), getMobileType(), getRegistrationId(),
				isMale(), getBirthYear(), getProfilePic(), getNickName(), isChupaEnable());
	}

	private String concate(Object... args) {
		String str = "";
		for(Object arg : args) {
			str += arg.toString() + " : ";
		}
		return str;
	}

	public JsonObject toJson() {
		JsonObject jo = new JsonObject();

		jo.addProperty("id", getId());
		jo.addProperty("ahId", getAhId());
		jo.addProperty("mobileId", getMobileId());
		jo.addProperty("mobileType", getMobileType());
		jo.addProperty("registrationId", getRegistrationId());
		jo.addProperty("isMale", isMale());
		jo.addProperty("birthYear", getBirthYear());
		jo.addProperty("profilePic", getProfilePic());
		jo.addProperty("nickName", getNickName());
		jo.addProperty("isChupaEnable", isChupaEnable());

		return jo;
	}


	/*
	 * Test
	 */
	//	public static AhUser addUserTest(){
	//		AhUser user = new AhUser();
	//		return user;
	//	}
	//
	//	private static String getRandomString(){
	//		char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
	//		StringBuilder sb = new StringBuilder();
	//		Random random = new Random();
	//		for (int i = 0; i < random.nextInt(25); i++) {
	//			char c = chars[random.nextInt(chars.length)];
	//			sb.append(c);
	//		}
	//		String output = sb.toString();
	//		return output;
	//	}
	//
	//	private static int getRandomInt(){
	//		Random random = new Random();
	//		return random.nextInt(40);
	//	}
}
