package com.cjwsjy.app.item;

import android.graphics.Bitmap;


public class LeaderSignItem {
	private String tv_description;
	private int iv_signName;
	private String tv_date;
	
	private Bitmap bitmap;
	public LeaderSignItem(){
		
	}
	
	public LeaderSignItem(String tv_description,int iv_signName,String tv_date) {
		
		this.tv_description =tv_description;
		this.iv_signName = iv_signName;
		this.tv_date = tv_date;
	}

	public String getTv_description() {
		return tv_description;
	}

	public void setTv_description(String tv_description) {
		this.tv_description = tv_description;
	}

	public int getIv_signName() {
		return iv_signName;
	}

	public void setIv_signName(int iv_signName) {
		this.iv_signName = iv_signName;
	}

	public String getTv_date() {
		return tv_date;
	}

	public void setTv_date(String tv_date) {
		this.tv_date = tv_date;
	}

	public void setImageBitmap(Bitmap bitmap){
		this.bitmap = bitmap;
	}
	
	public Bitmap getImageBitmap(){
		return bitmap;
	}
}
