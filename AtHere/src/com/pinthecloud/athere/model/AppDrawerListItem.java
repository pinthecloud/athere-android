package com.pinthecloud.athere.model;

public class AppDrawerListItem {
	private int iconId;
	private String title;
	private String content;

	public AppDrawerListItem(int iconId, String title, String content) {
		super();
		this.iconId = iconId;
		this.title = title;
		this.content = content;
	}

	public int getIconId() {
		return iconId;
	}
	public void setIconId(int iconId) {
		this.iconId = iconId;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
}
