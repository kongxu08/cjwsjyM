package com.do1.cjmobileoa.db.model;

import java.io.Serializable;
import java.text.CollationKey;
import java.text.Collator;

public class SUserVO implements Comparable, Serializable{
	private String userId;   // userid
	private String name;// 姓名 中文
	private String pinyin;// 姓名拼音
	private String Organization;// 二级单位
	private String mobile;// 电话号码
	private String mobile_type;// 电话号码 说明
	private String nameNumber;// 名称首字母对应数字
	private String pinyinNumber;// 全拼对应数字
	private int matcher_type;
	private String  searchStr;
	
	public static final int matcher_type_mobile=1;    // 电话号码匹配
	public static final int matcher_type_nameNumber=2; // 名称首字母匹配 
	public static final int matcher_type_pinyinNumber=3;// 全拼匹配
	
	@Override
	public boolean equals(Object o)
	{
		// TODO 自动生成的方法存根
		//android.util.Log.d("cjwsjy", "---------equals-------VO");
		//return super.equals(o);
		return	((SUserVO)o).getPinyin().equals(pinyin);
	}
	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String param) {
		this.userId = param;
	}
	
	public String getName() {
		return name;
	}

//	@Override
//	public boolean equals(Object o)
//	{
//		// TODO 自动生成的方法存根
//		//return super.equals(o);
//		return	((SUserVO)o).getPinyin().equals(pinyin);
//	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOrganization() {
		return Organization;
	}

	public void setOrganization(String param) {
		this.Organization = param;
	}

	public String getPinyin() {
		return pinyin;
	}

	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getMobile_type() {
		return mobile_type;
	}

	public void setMobile_type(String mobile_type) {
		this.mobile_type = mobile_type;
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

	public int getMatcher_type() {
		return matcher_type;
	}

	public void setMatcher_type(int matcher_type) {
		this.matcher_type = matcher_type;
	}

	public String getSearchStr() {
		return searchStr;
	}

	public void setSearchStr(String searchStr) {
		this.searchStr = searchStr;
	}

	public  void clear() 
	{
		this.userId = "";
		this.name = "";
		this.pinyin = "";
		this.mobile = "";
		this.mobile_type = "";
		this.nameNumber = "";
		this.pinyinNumber = "";
		this.matcher_type = 0;
		this.searchStr = "";
		this.Organization = "";
	}
	
	public  SUserVO clone() 
	{
		SUserVO newVo=new SUserVO();
		newVo.setUserId(userId);
		newVo.setMobile(mobile);
		newVo.setMobile_type(mobile_type);
		newVo.setName(name);
		newVo.setNameNumber(nameNumber);
		newVo.setPinyin(pinyin);
		newVo.setPinyinNumber(pinyinNumber);
		newVo.setOrganization(Organization);
//		newVo.setSearchStr(searchStr);
//		newVo.setMatcher_type(matcher_type);
	return newVo;
	}

	@Override
	public int compareTo(Object another)
	{
		int result = 0;
		Collator collator = Collator.getInstance();//点击查看中文api详解  
		SUserVO sdto = (SUserVO)another;
	    String piny = sdto.getPinyin();
	    result = this.pinyin.compareTo(piny);
		
		//String names = sdto.getName();
		//result = this.name.compareTo(names);
	    
		//CollationKey key1=collator.getCollationKey(o1.toString());
		
//	    String names = sdto.getName();
//	    CollationKey key1=collator.getCollationKey(names);
//	    CollationKey key2=collator.getCollationKey(this.name);
//	    result = key1.compareTo(key2);
	    
	    //result = this.name.compareTo(names);
	    
		return result;
	}

}
