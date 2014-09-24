package com.pinthecloud.athere.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ChupaRoom implements Parcelable {
	public ChupaRoom(){
	}
	public ChupaRoom(Parcel in){
		readToParcel(in);
	}


	public static final Parcelable.Creator<ChupaRoom> CREATOR = new Creator<ChupaRoom>(){
		public ChupaRoom createFromParcel(Parcel in){
			return new ChupaRoom(in);
		}
		public ChupaRoom[] newArray(int size){
			return new ChupaRoom[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {

	}

	public void readToParcel(Parcel in){

	}
}
