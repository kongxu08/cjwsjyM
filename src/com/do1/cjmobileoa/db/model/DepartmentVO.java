package com.do1.cjmobileoa.db.model;

public class DepartmentVO {

	private String id; // 编号
	private String deptId; // 部门编号
	private String deptName; // 部门名称
	private String deptParentid; // 父部门编号
	private String deptDisplayname; // 部门简称
	private String isCompany;
	private String isSubCompany;
	private String webURL;
	private String fax;
	private String tel;
	private String address;
	private String state;
	private String remark;
	private String orgCode;
	private int sortIndex;

//	public DepartmentVO() 
//	{
//		id = "";
//		deptId = "";
//		deptName = "";
//		deptParentid = "";
//		deptDisplayname = "";
//		isCompany = 0;
//		isSubCompany = 0;
//		webURL = "";
//		fax = "";
//		tel = "";
//		address = "";
//		state = "";
//		remark = "";
//		orgCode = "";
//		sortIndex = 0;
//	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDeptId() {
		return deptId;
	}

	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public String getDeptParentid() {
		return deptParentid;
	}

	public void setDeptParentid(String deptParentid) {
		this.deptParentid = deptParentid;
	}

	public String getisCompany() 
	{
		return isCompany;
	}

	public void setisCompany(String isCompany) 
	{
		this.isCompany = isCompany;
	}

	public String getisSubCompany() 
	{
		return isSubCompany;
	}

	public void setisSubCompany(String isSubCompany) 
	{
		this.isSubCompany = isSubCompany;
	}

	public String getwebURL() 
	{
		return webURL;
	}

	public void setwebURL(String webURL) 
	{
		this.webURL = webURL;
	}

	public String getfax() 
	{
		return fax;
	}

	public void setfax(String fax) 
	{
		this.fax = fax;
	}

	public String gettel() 
	{
		return tel;
	}

	public void settel(String tel) 
	{
		this.tel = tel;
	}

	public String getaddress() 
	{
		return address;
	}

	public void setaddress(String address) 
	{
		this.address = address;
	}

	public String getstate() 
	{
		return state;
	}

	public void setstate(String state) 
	{
		this.state = state;
	}

	public String getremark() 
	{
		return remark;
	}

	public void setremark(String remark) 
	{
		this.remark = remark;
	}

	public String getorgCode() 
	{
		return orgCode;
	}

	public void setorgCode(String orgCode) 
	{
		this.orgCode = orgCode;
	}
	
	public String getDeptDisplayname() {
		return deptDisplayname;
	}

	public void setDeptDisplayname(String deptDisplayname) {
		this.deptDisplayname = deptDisplayname;
	}

	public int getSortIndex() {
		return sortIndex;
	}

	public void setSortIndex(int sortIndex) {
		this.sortIndex = sortIndex;
	}

}
