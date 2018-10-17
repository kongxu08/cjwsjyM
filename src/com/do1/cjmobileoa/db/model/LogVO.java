package com.do1.cjmobileoa.db.model;

public class LogVO
{
	private String department; // 部门名称
	private String userid; // 用户编号
	private String nameSpell;// 名字拼音
	private String userDisplayName; // 用户显示姓名(中文)
	private String phoneNumber; // 手机号码
	private String email; // 邮箱
	private String createtime;

	public String getDepartment() 
	{
		return department;
	}
	
	public void setDepartment(String department) 
	{
		this.department = department;
	}
}
