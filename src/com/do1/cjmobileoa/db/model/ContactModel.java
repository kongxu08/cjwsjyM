package com.do1.cjmobileoa.db.model;

import java.io.Serializable;

/**
 * 通讯录model（其中包含-集团通讯录，黄号）
 * 
 */
public class ContactModel implements Serializable {

	private String id;
	private String name;// 姓名
	private String userPhoto;
	private String phoneNumber;// 手机号
	private String pinyin;// 拼音
	private String nameNumber;// 名称首字母对应数字
	private String pinyinNumber;// 全拼对应数字
	private String email;// 邮箱
	private String createTime;// 创建时间
	private String updateTime;// 修改时间

	private Boolean isChecked;

	private Integer dataCount;// 分页使用，总记录数
	private String group;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUserPhoto() {
		return userPhoto;
	}

	public void setUserPhoto(String userPhoto) {
		this.userPhoto = userPhoto;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getPinyin() {
		return pinyin;
	}

	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}

	public String getNameNumber() {
		return nameNumber;
	}

	public void setNameNumber(String nameNumber) {
		this.nameNumber = nameNumber;
	}

	public String getPinyinNumber() {
		return pinyinNumber;
	}

	public void setPinyinNumber(String pinyinNumber) {
		this.pinyinNumber = pinyinNumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public Integer getDataCount() {
		return dataCount;
	}

	public void setDataCount(Integer dataCount) {
		this.dataCount = dataCount;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public Boolean getIsChecked() {
		return isChecked;
	}

	public void setIsChecked(Boolean isChecked) {
		this.isChecked = isChecked;
	}

}
