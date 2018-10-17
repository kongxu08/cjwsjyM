package com.cjwsjy.app.vehicle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cjwsjy.app.utils.ValidUtil;
import com.do1.cjmobileoa.parent.callback.ResultObject;
import com.cjwsjy.app.R;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;

/**
 * 员工列表页面
 * 
 * @Copyright: Copyright (c)2014
 * @Company: 广州市道一信息有限公司
 * @author: liuwende
 * @version: 1.0
 * @date: 2014-8-19 下午4:55:50
 * 
 *        Modification History: Date Author Version Description
 *        ------------------------------------------------------------------
 *        2014-8-19 liuwende 1.0 1.0 Version
 */

public class EmployeeSelectsActivity extends BaseListActivity2 implements
		OnClickListener {

	private TextView name;
	private String empName;
	private String empName2;
	private String empDept;
	private int  index=-1;

	private boolean isResume = false;
	public static Map<String, String> selectEmps = new HashMap<String, String>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setHeadView(R.drawable.onclick_back_btn, "", "人员选择",0, "", this, this, null);
		requestBeforeDialog(this);
		init();
		vehileApply_dispatchActivity.activityList.add(this);
		empDept=this.getIntent().getStringExtra("dept_name");
	}

	private void init() {
		listview.setMode(Mode.PULL_FROM_END);
		name = (TextView) findViewById(R.id.name);
	}    

	@Override
	protected void itemClick(AdapterView<?> arg0, View view, int position, long id)
	{
		// TODO Auto-generated method stub
		position = position - 1;
	/*	index=position;
		ImageView select_btn = (ImageView) view.findViewById(R.id.select_btn);
		String emp_name= datalist.get(position).get("name") != null ? datalist.get(position).get("name").toString() : "";
		empName=emp_name;
		empName2=(String) datalist.get(position).get("username");
		name.setText(emp_name);
		if (ValidUtil.isNullOrEmpty(select_btn.getTag() + "")) {
	//		selectEmps.put(datalist.get(position).get("name").toString(),datalist.get(position).get("name").toString());
			select_btn.setImageResource(R.drawable.select_on_btn);
			select_btn.setTag(position);
		} else {
			selectEmps.remove(datalist.get(position).get("name").toString());
			select_btn.setImageResource(R.drawable.select_default_btn);
			select_btn.setTag("");
		}
		adapter.notifyDataSetChanged();
		*/

  	  //选择人员
  	vehileApply_dispatchActivity.empName=datalist.get(position).get("name") != null ? datalist.get(position).get("name").toString() : "";
	    vehileApply_dispatchActivity.empName2=(String) datalist.get(position).get("username");
		vehileApply_dispatchActivity.empDept=(String) datalist.get(position).get("deptName");
		for (int i = 0; i < vehileApply_dispatchActivity.activityList.size(); i++) {
			vehileApply_dispatchActivity.activityList.get(i).finish();
		}
    
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		Intent intent = new Intent();  
		
		switch (v.getId()) {
		case R.id.leftImage:
			finish();
			break;
		case R.id.rightImage:
			if(ValidUtil.isNullOrEmpty(empName)){
				Toast.makeText(EmployeeSelectsActivity.this, "请选择人员", Toast.LENGTH_SHORT).show();
				return;
			}
			vehileApply_dispatchActivity.empName=empName;
			vehileApply_dispatchActivity.empName2=empName2;
			vehileApply_dispatchActivity.empDept=empDept;
			for (int i = 0; i < vehileApply_dispatchActivity.activityList.size(); i++) {
				vehileApply_dispatchActivity.activityList.get(i).finish();
			}
			//this.finish();
			break;
		default:
			break;
		}
	}

	@Override
	public void handleItemView(BaseAdapter adapter, final int position,
			View itemView, ViewGroup parent) {/*
		// TODO Auto-generated method stub
		TextView emp_name = (TextView) itemView.findViewById(R.id.emp_name);
		 ImageView select_btn = (ImageView) itemView.findViewById(R.id.select_btn);

		String name = datalist.get(position).get("name") != null ? datalist.get(position).get("name").toString() : "";
		emp_name.setText(name);
		if(index==position){
			select_btn.setImageResource(R.drawable.select_on_btn);
		}else{
			select_btn.setImageResource(R.drawable.select_default_btn);
		}
	*/

		// TODO Auto-generated method stub
		ImageView image= (ImageView) itemView.findViewById(R.id.image);
		TextView department_name=(TextView) itemView.findViewById(R.id.department_name);
		department_name.setText(datalist.get(position).get("name") != null ? datalist.get(position).get("name").toString() : "");
		image.setBackgroundResource(R.drawable.login_user_icon);
	}

	@Override
	public void initParams() {
		// TODO Auto-generated method stub
		/*parentResId = R.layout.activity_emp_list;
		listItemResId = R.layout.item_empl;*/
		parentResId = R.layout.activity_deparment_list;
		listItemResId = R.layout.item_department;
		from = new String[] { "empname"};
		ids = new int[] { R.id.vehile_no};
		url = UrlManager.appRemoteUrl + UrlManager.GET_USERLIST;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("deptid", getIntent().getStringExtra("dept_id"));
		map = params;
	}

	/*@Override
	protected void onExecuteSuccess(int requestId, ResultObject result) {
		// TODO Auto-generated method stub
		super.onExecuteSuccess(requestId, result);
		stopProgressDialog();
		isResume = true;
	}

	@Override
	protected void onExecuteFail(int requestId, ResultObject result) {
		// TODO Auto-generated method stub
		super.onExecuteFail(requestId, result);
		stopProgressDialog();
		isResume = true;
	}*/
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (isResume) {
			requestBeforeDialog(this);
			doSearch();
		}
	}

}
