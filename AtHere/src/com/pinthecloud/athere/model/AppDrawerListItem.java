package com.pinthecloud.athere.model;

public class AppDrawerListItem {
	private int iconId;
	private String content;
	private String badge;

	public AppDrawerListItem(int iconId, String content, String badge) {
		super();
		this.iconId = iconId;
		this.content = content;
		this.badge = badge;
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
	public String getBadge() {
		return badge;
	}
	public void setBadge(String badge) {
		this.badge = badge;
	}
}
