package com.pinthecloud.athere.model;

import android.os.Parcel;
import android.os.Parcelable;

public class AhMessage implements Parcelable {
	@com.google.gson.annotations.SerializedName("id")
	private String id;
	@com.google.gson.annotations.SerializedName("type")
 	private String type; //{ INPUT, OUTPUT, SHOUTING, EXCEPTION, ETC },
	@com.google.gson.annotations.SerializedName("content")
	private String content;
	
	public AhMessage() {
	}
	
	public AhMessage(Parcel in){
		readToParcel(in);
	}
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
	//to Parcelable
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(id);
		dest.writeString(type);
		dest.writeString(content);		
	}
	
	public void readToParcel(Parcel in){
		id = in.readString();
		type = in.readString();
		content = in.readString();
	}
	
	public static final Parcelable.Creator<AhMessage> CREATOR = new Creator<AhMessage>(){
		public AhMessage createFromParcel(Parcel in){
			return new AhMessage(in);
		}
		public AhMessage[] newArray(int size){
			return new AhMessage[size]; 
		}
		
	};
}


