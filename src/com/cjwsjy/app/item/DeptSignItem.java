package com.cjwsjy.app.item;

import android.graphics.Bitmap;


public class DeptSignItem {
	private String dept_description;
	private int dept_signName;
	private String dept_date;
	
	private Bitmap bitmap;
	public DeptSignItem(){
		
	}
	
	public DeptSignItem(String dept_description,int dept_signName,String dept_date) {
		
		this.dept_description =dept_description;
		this.dept_signName = dept_signName;
		this.dept_date = dept_date;
	}

	public String getDept_description() {
		return dept_description;
	}

	public void setDept_description(String dept_description) {
		this.dept_description = dept_description;
	}

	public int getDept_signName() {
		return dept_signName;
	}

	public void setDept_signName(int dept_signName) {
		this.dept_signName = dept_signName;
	}

	public String getDept_date() {
		return dept_date;
	}

	public void setDept_date(String dept_date) {
		this.dept_date = dept_date;
	}

	public void setImageBitmap(Bitmap bitmap){
		this.bitmap = bitmap;
	}
	
	public Bitmap getImageBitmap(){
		return bitmap;
	}
}
