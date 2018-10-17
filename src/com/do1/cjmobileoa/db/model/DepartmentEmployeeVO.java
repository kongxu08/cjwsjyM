package com.do1.cjmobileoa.db.model;

public class DepartmentEmployeeVO 
{
	private String id; // id
	private String userRID; // 编号
	private String orgRID; // 部门编号
	private String userTitle; // 职位
	private int userSort; // 部门名称
	
	public String getid() {
		return id;
	}

	public void setid(String id) {
		this.id = id;
	}
	
	public String getuserRID() {
		return userRID;
	}

	public void setuserRID(String userRID) {
		this.userRID = userRID;
	}

	public String getorgRID() {
		return orgRID;
	}

	public void setorgRID(String orgRID) {
		this.orgRID = orgRID;
	}

	public String getuserTitle() {
		return userTitle;
	}

	public void setuserTitle(String strings) {
		this.userTitle = strings;
	}
	
	public int getuserSort() {
		return userSort;
	}
	
	public void setuserSort(int userSort) {
		this.userSort = userSort;
	}
}
