package com.cjwsjy.app.item;

import android.graphics.Bitmap;


public class ConSignItem {
	private String con_description;
	private int con_signName;
	private String con_date;
	
	private Bitmap bitmap;
	public ConSignItem(){
		
	}
	
	public ConSignItem(String con_description,int con_signName,String con_date) {
		
		this.con_description =con_description;
		this.con_signName = con_signName;
		this.con_date = con_date;
	}

	public String getCon_description() {
		return con_description;
	}

	public void setCon_description(String con_description) {
		this.con_description = con_description;
	}

	public int getCon_signName() {
		return con_signName;
	}

	public void setCon_signName(int con_signName) {
		this.con_signName = con_signName;
	}

	public String getCon_date() {
		return con_date;
	}

	public void setCon_date(String con_date) {
		this.con_date = con_date;
	}
	
	public void setImageBitmap(Bitmap bitmap){
		this.bitmap = bitmap;
	}
	
	public Bitmap getImageBitmap(){
		return bitmap;
	}
}
