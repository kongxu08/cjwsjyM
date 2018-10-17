package com.cjwsjy.app.phonebook;

public class Org {
	
	private String tvTitle;//标题
	private int ivIcon; //图标
	public Org(){
		
	}
	
	public Org(int ivIcon, String tvTitle) {
		this.ivIcon = ivIcon;
		this.tvTitle = tvTitle;
		// TODO Auto-generated constructor stub
	}

	public int getIvIcon() {
		return ivIcon;
	}

	public void setIvIcon(int ivIcon) {
		this.ivIcon = ivIcon;
	}

	public String getTvTitle() {
		return tvTitle;
	}

	public void setTvTitle(String tvTitle) {
		this.tvTitle = tvTitle;
	}
	
}
