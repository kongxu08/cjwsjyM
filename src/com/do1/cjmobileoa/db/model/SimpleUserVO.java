package com.do1.cjmobileoa.db.model;

public class SimpleUserVO {

	private String userId; // 用户ID
	private String userName; // 用户名称
	private String personName; // 中文名称
	private String mobile; // 手机号码
	private String shortMobile; // 短号
	private String picPath; // 头像地址
	private String email; // 邮箱地址
	private String userCount; // 该单位人数

	public String getUserCount() {
		return userCount;
	}

	public void setUserCount(String userCount) {
		this.userCount = userCount;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getPersonName() {
		return personName;
	}

	public void setPersonName(String personName) {
		this.personName = personName;
	}

	public String getPicPath() {
		return picPath;
	}

	public void setPicPath(String picPath) {
		this.picPath = picPath;
	}

	public String getShortMobile() {
		return shortMobile;
	}

	public void setShortMobile(String shortMobile) {
		this.shortMobile = shortMobile;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
}
