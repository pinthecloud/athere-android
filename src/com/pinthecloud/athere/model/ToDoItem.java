package com.pinthecloud.athere.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Represents an item in a ToDo list
 */
public class ToDoItem implements Parcelable {

	/**
	 * Item text
	 */
	@com.google.gson.annotations.SerializedName("text")
	private String mText;

	/**
	 * Item Id
	 */
	@com.google.gson.annotations.SerializedName("id")
	private String mId;

	/**
	 * Indicates if the item is completed
	 */
	@com.google.gson.annotations.SerializedName("complete")
	private boolean mComplete;

	/**
	 * ToDoItem constructor
	 */
	public ToDoItem() {

	}

	public ToDoItem(Parcel in) {
		// TODO Auto-generated constructor stub
		readToParcel(in);
	}

	@Override
	public String toString() {
		return getText();
	}

	/**
	 * Initializes a new ToDoItem
	 * 
	 * @param text
	 *            The item text
	 * @param id
	 *            The item id
	 */
//	public ToDoItem(String text, String id) {
//		this.setText(text);
//		this.setId(id);
//	}
	

	/**
	 * Returns the item text
	 */
	public String getText() {
		return mText;
	}

	/**
	 * Sets the item text
	 * 
	 * @param text
	 *            text to set
	 */
	public final void setText(String text) {
		mText = text;
	}

	/**
	 * Returns the item id
	 */
	public String getId() {
		return mId;
	}

	/**
	 * Sets the item id
	 * 
	 * @param id
	 *            id to set
	 */
	public final void setId(String id) {
		mId = id;
	}

	/**
	 * Indicates if the item is marked as completed
	 */
	public boolean isComplete() {
		return mComplete;
	}

	/**
	 * Marks the item as completed or incompleted
	 */
	public void setComplete(boolean complete) {
		mComplete = complete;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof ToDoItem && ((ToDoItem) o).mId == mId;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(mText);
		dest.writeString(mId);
		dest.writeByte((byte) (mComplete ? 1:0)); // if mComplete == true, byte == 1
	}
	
	public void readToParcel(Parcel in){
		mText = in.readString();
		mId = in.readString();
		mComplete = in.readByte() != 0;	// mComplete == true if byte != 0
	}
	
	public static final Parcelable.Creator<ToDoItem> CREATOR = new Creator<ToDoItem>(){
		public ToDoItem createFromParcel(Parcel in){
			return new ToDoItem(in);
		}
		public ToDoItem[] newArray(int size){
			return new ToDoItem[size];
		}
	};
}
