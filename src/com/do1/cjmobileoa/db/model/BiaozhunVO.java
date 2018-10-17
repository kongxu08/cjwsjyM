package com.do1.cjmobileoa.db.model;

import java.io.Serializable;

public class BiaozhunVO implements Comparable, Serializable{

	private String id; // 编号
	private String Oldname; // 原名字
	private String Newname;// 重命名
	private String isdelete; // 删除标志

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOldname() {
		return Oldname;
	}

	public void setOldname(String department) {
		this.Oldname = department;
	}

	public String getNewname() {
		return Newname;
	}

	public void setNewname(String deptId) {
		this.Newname = deptId;
	}

	public String getIsdelete() {
		return isdelete;
	}

	public void setIsdelete(String userid) {
		this.isdelete = userid;
	}


	@Override
	public int compareTo(Object another) 
	{
		int result = 0;
		//Collator collator = Collator.getInstance();//点击查看中文api详解  
		BiaozhunVO sdto = (BiaozhunVO)another;
	    //String piny = sdto.getUsername();
	    //result = this.username.compareTo(piny);
	    
		return result;
	}

}
