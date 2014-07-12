package com.pinthecloud.athere.model;

import android.os.Parcel;
import android.os.Parcelable;

public class AnonymProfileAdj implements Parcelable{
	
	@com.google.gson.annotations.SerializedName("id")
	private String id;
	@com.google.gson.annotations.SerializedName("adj")
	private String adj;
	
	public AnonymProfileAdj(){
		
	}
	public AnonymProfileAdj(Parcel in) {
		// TODO Auto-generated constructor stub
		readToParcel(in);
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getAdj() {
		return adj;
	}
	public void setAdj(String adj) {
		this.adj = adj;
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
		dest.writeString(adj);
	}
	public void readToParcel(Parcel in){
		id = in.readString();
		adj = in.readString();
	}
	
	public static final Parcelable.Creator<AnonymProfileAdj> CREATOR = new Creator<AnonymProfileAdj>(){
		public AnonymProfileAdj createFromParcel(Parcel in){
			return new AnonymProfileAdj(in);
		}
		public AnonymProfileAdj[] newArray(int size){
			return new AnonymProfileAdj[size];
		}
	};
}
