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

public class selectVehileModelActivity extends BaseListActivity2 implements
OnClickListener{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setHeadView(R.drawable.onclick_back_btn, "", "品牌", 0, "", this, this, this);
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
		model_name.setText(datalist.get(position).get("model_name") != null ? datalist.get(position).get("model_name").toString() : "");
	}

	    @Override
	       protected void itemClick(AdapterView<?> arg0, View view, int position,
			long id) {
	          // TODO Auto-generated method stub
	          super.itemClick(arg0, view, position, id);
	          view.setBackgroundResource(R.drawable.bg_item_check);
	          position=position-1;
	          String model_name=datalist.get(position).get("model_name") != null ? datalist.get(position).get("model_name").toString() : "";
	          String model_id=datalist.get(position).get("model_id") != null ? datalist.get(position).get("model_id").toString() : "";
	          Intent intent=new Intent();
	          Bundle bundle=new Bundle();
	          bundle.putString("model_name", model_name);  
	          bundle.putString("model_id", model_id);  
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
	public void initParams() {
		// TODO Auto-generated method stub
		parentResId = R.layout.activity_model_list;
		listItemResId = R.layout.item_model;
		from = new String[] { "model_name" };
		ids = new int[] { R.id.model_name};
		url = UrlManager.GET_MODEL;
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
