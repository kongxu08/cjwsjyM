package com.cjwsjy.app.item;

public class OutOfOfficeItem {
	private String tv_outTimeFrom; //离岗时间
	private String tv_address;//出差地点
	private String tv_event;//出差事由
	private int iv_modify;//修改图标
	
	public OutOfOfficeItem(){
		
	}
	
	public OutOfOfficeItem(String tv_outTimeFrom,String tv_address,String tv_event,int iv_modify) {

		this.tv_outTimeFrom = tv_outTimeFrom;
		this.tv_address = tv_address;
		this.tv_event = tv_event;
		this.iv_modify = iv_modify;
		
	}

	public String getTv_outTimeFrom() {
		return tv_outTimeFrom;
	}

	public void setTv_outTimeFrom(String tv_outTimeFrom) {
		this.tv_outTimeFrom = tv_outTimeFrom;
	}

	public String getTv_address() {
		return tv_address;
	}

	public void setTv_address(String tv_address) {
		this.tv_address = tv_address;
	}

	public String getTv_event() {
		return tv_event;
	}

	public void setTv_event(String tv_event) {
		this.tv_event = tv_event;
	}

	public int getIv_modify() {
		return iv_modify;
	}

	public void setIv_modify(int iv_modify) {
		this.iv_modify = iv_modify;
	}

}
