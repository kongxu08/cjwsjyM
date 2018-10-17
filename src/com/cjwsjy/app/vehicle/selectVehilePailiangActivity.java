package com.cjwsjy.app.vehicle;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cjwsjy.app.R;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
/**
 * 变更申请
 * @author think
 *
 */

public class selectVehilePailiangActivity extends BaseListActivity2 implements
OnClickListener{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setHeadView(R.drawable.onclick_back_btn, "", "排量", 0, "", this, this, this);
		init();
		requestBeforeDialog(this);
	}
	
	public void init(){
		listview.setMode(Mode.PULL_FROM_END);
	}
	
	@Override
	public void handleItemView(BaseAdapter adapter, int position,
			View itemView, ViewGroup parent) {
		// TODO Auto-generated method stub

		TextView model_name=(TextView) itemView.findViewById(R.id.model_name);
		model_name.setText(datalist.get(position).get("Name") != null ? datalist.get(position).get("Name").toString() : "");
	}

	    @Override
	       protected void itemClick(AdapterView<?> arg0, View view, int position,
			long id) {
	          // TODO Auto-generated method stub
	          super.itemClick(arg0, view, position, id);
	          view.setBackgroundResource(R.drawable.bg_item_check);
	          position=position-1;
	          String Name=datalist.get(position).get("Name") != null ? datalist.get(position).get("Name").toString() : "";
	          String ID=datalist.get(position).get("ID") != null ? datalist.get(position).get("ID").toString() : "";
	          Intent intent=new Intent();
	          Bundle bundle=new Bundle();
	          bundle.putString("Name", Name);  
	          bundle.putString("ID", ID);  
	          intent.putExtras(bundle); 
	          setResult(0x003, intent); 
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
	public void initParams() {
		// TODO Auto-generated method stub
		parentResId = R.layout.activity_model_list;
		listItemResId = R.layout.item_model;
		from = new String[] { "Name" };
		ids = new int[] { R.id.model_name};
		url = UrlManager.GET_CER;
		Map<String, Object> params = new HashMap<String, Object>();
		map = params;
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		doSearch();
	}
}
