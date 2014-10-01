package com.pinthecloud.athere.model;

public class AppDrawerListItem {
	private int iconId;
	private String title;

	public AppDrawerListItem(int iconId, String title) {
		super();
		this.iconId = iconId;
		this.title = title;
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
}
