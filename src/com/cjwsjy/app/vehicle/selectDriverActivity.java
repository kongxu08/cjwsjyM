package com.cjwsjy.app.vehicle;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;

import android.widget.TextView;

import com.cjwsjy.app.R;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
/**
 * 变更申请
 * @author think
 *
 */

public class selectDriverActivity extends BaseListActivity2 implements
OnClickListener{
	private EditText search_text;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setHeadView(R.drawable.onclick_back_btn, "", "司机选择", 0, "", this, this, this);
		init();
		requestBeforeDialog(this);
	}
	
	public void init(){
		listview.setMode(Mode.PULL_FROM_END);
		search_text=(EditText) findViewById(R.id.search_text);
		search_text.addTextChangedListener(new TextWatcher() {  
			  private String temp;  
			  @Override  
			   public void onTextChanged(CharSequence s, int start, int before, int count) {  
			     // TODO Auto-generated method stub                
			         }  
		         @Override  
			           public void beforeTextChanged(CharSequence s, int start, int count,  
			                   int after) {  
		            }  
					@Override
					public void afterTextChanged(Editable arg0) {  
			               // TODO Auto-generated method stub  
			              temp = search_text.getText().toString();  
			              blurSearch(temp);
			         }
			         });

		findViewById(R.id.search_img).setOnClickListener(this);
	}
	
	private void blurSearch(String state){
	    Map<String, Object> params = new HashMap<String, Object>();
		params.put("keyword", state);
		params.put("status", "0");
		map = params;
		doSearch();
 } 
 
	@Override
	public void handleItemView(BaseAdapter adapter, int position,
			View itemView, ViewGroup parent) {
		// TODO Auto-generated method stub

		TextView driver_name=(TextView) itemView.findViewById(R.id.driver_name);
		TextView driver_old=(TextView) itemView.findViewById(R.id.driver_old);
		TextView driver_car_old=(TextView) itemView.findViewById(R.id.driver_car_old);
		TextView driver_type=(TextView) itemView.findViewById(R.id.driver_type);
		
		driver_name.setText(datalist.get(position).get("name") != null ? datalist.get(position).get("name").toString() : "");
		driver_old.setText(datalist.get(position).get("BAge") != null ? datalist.get(position).get("BAge").toString() : "");
		driver_car_old.setText(datalist.get(position).get("VAge") != null ? datalist.get(position).get("VAge").toString() : "");
		driver_type.setText(datalist.get(position).get("DriverType") != null ? datalist.get(position).get("DriverType").toString() : "");
	}

	@Override
	protected void itemClick(AdapterView<?> arg0, View view, int position,
							 long id) {
		// TODO Auto-generated method stub
		super.itemClick(arg0, view, position, id);
		view.setBackgroundResource(R.drawable.bg_item_check);
		position = position - 1;
		String driver_name = datalist.get(position).get("name") != null ? datalist.get(position).get("name").toString() : "";
		String driver_phone = datalist.get(position).get("phone") != null ? datalist.get(position).get("phone").toString() : "";
		String driver_id = datalist.get(position).get("id") != null ? datalist.get(position).get("id").toString() : "";
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putString("driver_name", driver_name);
		bundle.putString("driver_phone", driver_phone);
		bundle.putString("driver_id", driver_id);
		intent.putExtras(bundle);
		setResult(0x001, intent);
		finish();
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.leftImage:
				finish();
				break;
		}
	}


	@Override
	public void initParams()
	{
		// TODO Auto-generated method stub
		parentResId = R.layout.activity_driver_list;
		listItemResId = R.layout.item_driver;
		from = new String[] { "name" };
		ids = new int[] { R.id.driver_name};
		url = UrlManager.appRemoteUrl + UrlManager.GET_DRIVER_LIST;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("status", "0");
		map = params;
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		doSearch();
	}
}
