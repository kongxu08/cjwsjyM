package com.cjwsjy.app.news;

import android.media.Image;

public class List_Item {
	private int iv_tip;
	private String tv_title;//标题
	private String tv_description;//简介
	private String tv_source;//作者
	private String tv_time;//时间
	private String tv_comment_count;//点击次数
	
	public List_Item(){
		
	}
	
	public List_Item(int iv_tip, String tv_title,String tv_description,String tv_source,String tv_time,String tv_comment_count) {
		this.iv_tip = iv_tip;
		this.tv_title = tv_title;
		this.tv_description = tv_description;
		this.tv_source = tv_source;
		this.tv_time = tv_time;
		this.tv_comment_count = tv_comment_count;
		// TODO Auto-generated constructor stub
	}

	public int getIv_tip() {
		return iv_tip;
	}

	public void setIv_tip(int iv_tip) {
		this.iv_tip = iv_tip;
	}

	public String getTv_title() {
		return tv_title;
	}

	public void setTv_title(String tv_title) {
		this.tv_title = tv_title;
	}

	public String getTv_description() {
		return tv_description;
	}

	public void setTv_description(String tv_description) {
		this.tv_description = tv_description;
	}

	public String getTv_source() {
		return tv_source;
	}

	public void setTv_source(String tv_source) {
		this.tv_source = tv_source;
	}

	public String getTv_time() {
		return tv_time;
	}

	public void setTv_time(String tv_time) {
		this.tv_time = tv_time;
	}

	public String getTv_comment_count() {
		return tv_comment_count;
	}

	public void setTv_comment_count(String tv_comment_count) {
		this.tv_comment_count = tv_comment_count;
	}
	
	
}
