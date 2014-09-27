package com.pinthecloud.athere.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Square implements Parcelable {
	@com.google.gson.annotations.SerializedName("id")
	private String id;
	@com.google.gson.annotations.SerializedName("whoMade")
	private String whoMade;
	@com.google.gson.annotations.SerializedName("name")
	private String name;
	@com.google.gson.annotations.SerializedName("latitude")
	private double latitude;
	@com.google.gson.annotations.SerializedName("longitude")
	private double longitude;
	@com.google.gson.annotations.SerializedName("maleNum")
	private int maleNum;
	@com.google.gson.annotations.SerializedName("femaleNum")
	private int femaleNum;
	@com.google.gson.annotations.SerializedName("isAdmin")
	private boolean isAdmin;
	@com.google.gson.annotations.SerializedName("distance")
	private int distance;
	@com.google.gson.annotations.SerializedName("code")
	private String code;
	@com.google.gson.annotations.SerializedName("resetTime")
	private int resetTime;
	@com.google.gson.annotations.SerializedName("showRange")
	private int showRange;
	@com.google.gson.annotations.SerializedName("entryRange")
	private int entryRange;

	
	public Square(){
	}
	public Square(Parcel in){
		readToParcel(in);
	}
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getWhoMade() {
		return whoMade;
	}
	public void setWhoMade(String whoMade) {
		this.whoMade = whoMade;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public int getMaleNum() {
		return maleNum;
	}
	public void setMaleNum(int maleNum) {
		this.maleNum = maleNum;
	}
	public int getFemaleNum() {
		return femaleNum;
	}
	public void setFemaleNum(int femaleNum) {
		this.femaleNum = femaleNum;
	}
	public int getDistance() {
		return distance;
	}
	public void setDistance(int distance) {
		this.distance = distance;
	}
	public boolean isAdmin() {
		return isAdmin;
	}
	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public int getResetTime() {
		return resetTime;
	}
	public void setResetTime(int resetTime) {
		this.resetTime = resetTime;
	}
	public int getShowRange() {
		return showRange;
	}
	public void setShowRange(int showRange) {
		this.showRange = showRange;
	}
	public int getEntryRange() {
		return entryRange;
	}
	public void setEntryRange(int entryRange) {
		this.entryRange = entryRange;
	}

	
	/*
	 * Parcelable
	 */
	public static final Parcelable.Creator<Square> CREATOR = new Creator<Square>(){
		public Square createFromParcel(Parcel in){
			return new Square(in);
		}
		public Square[] newArray(int size){
			return new Square[size];
		}
	};
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(whoMade);
		dest.writeString(name);
		dest.writeDouble(latitude);
		dest.writeDouble(longitude);
		dest.writeInt(maleNum);
		dest.writeInt(femaleNum);
		dest.writeInt(distance);
		dest.writeInt(isAdmin? 1: 0);
		dest.writeString(code);
		dest.writeInt(resetTime);
		dest.writeInt(showRange);
		dest.writeInt(entryRange);
	}

	public void readToParcel(Parcel in){
		id = in.readString();
		whoMade = in.readString();
		name = in.readString();
		latitude = in.readDouble();
		longitude = in.readDouble();
		maleNum = in.readInt();
		femaleNum = in.readInt();
		distance = in.readInt();
		isAdmin = in.readInt() == 1;
		code = in.readString();
		resetTime = in.readInt();
		showRange = in.readInt();
		entryRange = in.readInt();
	}
}
