package com.pinthecloud.athere.model;

import android.os.Parcel;
import android.os.Parcelable;

public class AnonymProfileNoun implements Parcelable {
	@com.google.gson.annotations.SerializedName("id")
	private String id;
	@com.google.gson.annotations.SerializedName("noun")
	private String noun;
	
	public AnonymProfileNoun(){
		
	}
	public AnonymProfileNoun(Parcel in) {
		// TODO Auto-generated constructor stub
		readToParcel(in);
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getNoun() {
		return noun;
	}
	public void setNoun(String noun) {
		this.noun = noun;
	}

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(id);
		dest.writeString(noun);
	}
	public void readToParcel(Parcel in){
		id = in.readString();
		noun = in.readString();
	}
	
	public static final Parcelable.Creator<AnonymProfileNoun> CREATOR = new Creator<AnonymProfileNoun>(){
		public AnonymProfileNoun createFromParcel(Parcel in){
			return new AnonymProfileNoun(in);
		}
		public AnonymProfileNoun[] newArray(int size){
			return new AnonymProfileNoun[size];
		}
	};
}
