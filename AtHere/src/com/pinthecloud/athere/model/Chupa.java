package com.pinthecloud.athere.model;

public class Chupa {
//	map.put("profilePic", profilePic);
//	map.put("userNickName", userNickName);
//	map.put("userId", userId);
//	map.put("content", content);
//	map.put("timeStamp", timeStamp);
//	map.put("chupaCommunId", chupaCommunId);
//	map.put("isExit", isExit);
//	map.put("chupaBadge", chupaBadge);
	
	private String id;
	private String userNickName;
	private String userId;
	private String content;
	private String timeStamp;
	private boolean isExit;
	private int badgeNum;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUserNickName() {
		return userNickName;
	}
	public void setUserNickName(String userNickName) {
		this.userNickName = userNickName;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}
	public boolean isExit() {
		return isExit;
	}
	public void setExit(boolean isExit) {
		this.isExit = isExit;
	}
	public int getBadgeNum() {
		return badgeNum;
	}
	public void setBadgeNum(int badgeNum) {
		this.badgeNum = badgeNum;
	}
}
