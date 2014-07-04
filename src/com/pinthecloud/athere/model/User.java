package com.pinthecloud.athere.model;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable{
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
	public User(Parcel in){
		readToParcel(in);
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
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int frags) {
		dest.writeString(id);
		dest.writeString(nickName);
		dest.writeString(profilePic);
		dest.writeString(mobileId);		
	}
	public void readToParcel(Parcel in){
		id = in.readString();
		nickName = in.readString();
		profilePic = in.readString();
		mobileId = in.readString();
	}
	public static final Parcelable.Creator<User> CREATOR = new Creator<User>(){
		public User createFromParcel(Parcel in){
			return new User(in);
		}
		public User[] newArray(int size){
			return new User[size];
		}
	};
}
