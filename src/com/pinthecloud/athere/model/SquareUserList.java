package com.pinthecloud.athere.model;

import android.os.Parcel;
import android.os.Parcelable;

public class SquareUserList implements Parcelable{
	@com.google.gson.annotations.SerializedName("id")
	private String id;
	@com.google.gson.annotations.SerializedName("squareId")
	private String squareId;
	@com.google.gson.annotations.SerializedName("userId")
	private String userId;
	
	public SquareUserList(){
		
	}
	public SquareUserList(Parcel in) {
		// TODO Auto-generated constructor stub
		readToParcel(in);
	}
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
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(id);
		dest.writeString(squareId);
		dest.writeString(userId);
	}
	public void readToParcel(Parcel in){
		id = in.readString();
		squareId = in.readString();
		userId = in.readString();
	}
	
	public static final Parcelable.Creator<SquareUserList> CREATOR = new Creator<SquareUserList>(){
		public SquareUserList createFromParcel(Parcel in){
			return new SquareUserList(in);
		}
		public SquareUserList[] newArray(int size){
			return new SquareUserList[size];
		}
	};
}
