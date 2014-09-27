package com.pinthecloud.athere.model;

public class NotificationDrawerListItem {
	private int iconId;
	private String title;
	private String content;
	private String time;

	public NotificationDrawerListItem(int iconId, String title, String content,
			String time) {
		super();
		this.iconId = iconId;
		this.title = title;
		this.content = content;
		this.time = time;
	}

	public int getIconId() {
		return iconId;
	}
	public void setIconId(int iconId) {
		this.iconId = iconId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
}
