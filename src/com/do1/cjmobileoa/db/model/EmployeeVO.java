package com.do1.cjmobileoa.db.model;

import java.io.Serializable;
import java.text.Collator;

public class EmployeeVO implements Comparable, Serializable{

	private String id; // 编号
	private String department; // 部门名称
	private String deptId;// 部门ID
	private String userid; // 用户编号
	private String photo; // 用户头像
	private String username; // 用户姓名
	private String mobileIphone="";// 电话号码
	private String nameSpell;// 名字拼音
	private String nextDepartment;// 下一个部门
	private String preDepartment;// 原来部门
	private String userDisplayName; // 用户显示姓名(中文)
	private String officeAddress;  //办公地址
	private String phoneNumber=""; // 手机号码
	private String phoneShortNumber; // 手机短号
	private String telephone; // 办公电话
	private String email; // 邮箱
	private String mobilePublic; // 是否公开(待确认)
	private int userSortIndex;// 用户分类坐标
	private String userTitle;
	private String createtime;
	private String jobNumber;
	private String gender;
	private String userState;
	private String postDuty;
	private String imAccounts;
	private String mobile;
	private String mobileShortNumber;
	private String mobileIphoneShortNumber;
	private String officeTelShort;
	private String nameNumber;// 名称首字母对应数字
	private String pinyinNumber;// 全拼对应数字
	private String group;
	private Integer dataCount;// 分页使用，总记录数

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getDeptId() {
		return deptId;
	}

	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getOfficeAddress() {
		return officeAddress;
	}

	public void setOfficeAddress(String officeaddress) {
		this.officeAddress = officeaddress;
	}
	
	public String getUserDisplayName() {
		return userDisplayName;
	}

	public void setUserDisplayName(String userDisplayName) {
		this.userDisplayName = userDisplayName;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getPhoneShortNumber() {
		return phoneShortNumber;
	}

	public void setPhoneShortNumber(String phoneShortNumber) {
		this.phoneShortNumber = phoneShortNumber;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobilePublic() {
		return mobilePublic;
	}

	public void setMobilePublic(String mobilePublic) {
		this.mobilePublic = mobilePublic;
	}

	public String getCreatetime() {
		return createtime;
	}

	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}

	public String getMobileIphone() {
		return mobileIphone;
	}

	public void setMobileIphone(String mobileIphone) {
		this.mobileIphone = mobileIphone;
	}

	public String getNameSpell() {
		return nameSpell;
	}

	public void setNameSpell(String nameSpell) {
		this.nameSpell = nameSpell;
	}

	public String getNextDepartment() {
		return nextDepartment;
	}

	public void setNextDepartment(String nextDepartment) {
		this.nextDepartment = nextDepartment;
	}

	public String getPreDepartment() {
		return preDepartment;
	}

	public void setPreDepartment(String preDepartment) {
		this.preDepartment = preDepartment;
	}

	public int getUserSortIndex() {
		return userSortIndex;
	}

	public void setUserSortIndex(int userSortIndex) {
		this.userSortIndex = userSortIndex;
	}

	public String getUserTitle() {
		return userTitle;
	}

	public void setUserTitle(String userTitle) {
		this.userTitle = userTitle;
	}

	public Integer getDataCount() {
		return dataCount;
	}

	public void setDataCount(Integer dataCount) {
		this.dataCount = dataCount;
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

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}
	
	public String getjobNumber() {
		return jobNumber;
	}

	public void setjobNumber(String jobNumber) {
		this.jobNumber = jobNumber;
	}
	
	public String getgender() {
		return gender;
	}

	public void setgender(String gender) {
		this.gender = gender;
	}
	
	public String getuserState() {
		return userState;
	}

	public void setuserState(String userState) {
		this.userState = userState;
	}
	
	public String getpostDuty() {
		return postDuty;
	}

	public void setpostDuty(String postDuty) {
		this.postDuty = postDuty;
	}
	
	public String getimAccounts() {
		return imAccounts;
	}

	public void setimAccounts(String imAccounts) {
		this.imAccounts = imAccounts;
	}
	
	public String getmobile() {
		return mobile;
	}

	public void setmobile(String mobile) {
		this.mobile = mobile;
	}
	
	public String getmobileShortNumber() {
		return mobileShortNumber;
	}

	public void setmobileShortNumber(String mobileShortNumber) {
		this.mobileShortNumber = mobileShortNumber;
	}
	
	public String getmobileIphoneShortNumber() {
		return mobileIphoneShortNumber;
	}

	public void setmobileIphoneShortNumber(String mobileIphoneShortNumber) {
		this.mobileIphoneShortNumber = mobileIphoneShortNumber;
	}
	
	public String getofficeTelShort() {
		return officeTelShort;
	}

	public void setofficeTelShort(String officeTelShort) {
		this.officeTelShort = officeTelShort;
	}

	@Override
	public int compareTo(Object another) 
	{
		int result = 0;
		//Collator collator = Collator.getInstance();//点击查看中文api详解  
		EmployeeVO sdto = (EmployeeVO)another;
	    String piny = sdto.getUsername();
	    result = this.username.compareTo(piny);
	    
		return result;
	}

}
