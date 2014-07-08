package com.pinthecloud.athere.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Square implements Parcelable {
	public Square(){
	}
	public Square(Parcel in){
		readToParcel(in);
	}
	
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
	@com.google.gson.annotations.SerializedName("participants")
	private int participants;
	
	private int distance;
	
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
	public int getParticipants() {
		return participants;
	}
	public void setParticipants(int participants) {
		this.participants = participants;
	}
	public int getDistance() {
		return distance;
	}
	public void setDistance(int distance) {
		this.distance = distance;
	}
	
	
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
		dest.writeInt(participants);
	}
	
	
	public void readToParcel(Parcel in){
		id = in.readString();
		whoMade = in.readString();
		name = in.readString();
		latitude = in.readDouble();
		longitude = in.readDouble();
		participants = in.readInt();
	}
	
	
	public static final Parcelable.Creator<Square> CREATOR = new Creator<Square>(){
		public Square createFromParcel(Parcel in){
			return new Square(in);
		}
		public Square[] newArray(int size){
			return new Square[size];
		}
	};
}
