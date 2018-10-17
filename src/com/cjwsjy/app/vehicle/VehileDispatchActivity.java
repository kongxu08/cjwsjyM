package com.cjwsjy.app.vehicle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;

import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;


import com.cjwsjy.app.R;

import com.cjwsjy.app.SmApplication;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;

public class VehileDispatchActivity extends BaseListActivity2{
	private RadioGroup rGroup;
	private EditText search_text; 
	private SharedPreferences sp;
	public List<Map<String, Object>> statusdatalist1 = new ArrayList<Map<String,Object>>();
	public List<Map<String, Object>>statusdatalist2 = new ArrayList<Map<String,Object>>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
	
		setHeadView(R.drawable.onclick_back_btn, "", "用车调度", R.drawable.onclick_new_apply_btn, "用车申请", this, this, this);
		init();
		requestBeforeDialog(this);
		sp = SmApplication.sp;
	}
	
	public void init(){
		listview.setMode(Mode.PULL_FROM_END);
		rGroup=(RadioGroup) findViewById(R.id.rg_state);
		rGroup.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				// TODO Auto-generated method stub
				
				switch (arg1) {
				case R.id.rb_state_1:              //全部
					blurSearchByState("");
					//doSearch2("");
					break;
               case R.id.rb_state_2:              //已申请
            	   blurSearchByState("1");
				   //doSearch2("1");
					break;
              case R.id.rb_state_3:              //已调度
            	   blurSearchByState("2");
				   //doSearch2("2");
	           break;

				default:
					break;
				}
			}
		});
		
		search_text=(EditText) findViewById(R.id.search_text);
		search_text.clearFocus();
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
			              blurSearchByKeyWord(temp);
			         }
			         });

		findViewById(R.id.search_img).setOnClickListener(this);
	}
	
	 private void blurSearchByState(String state){
		    Map<String, Object> params = new HashMap<String, Object>();
		    //params.put("CurrentLoginUserAccount", "admin");
			//params.put("CurrentLoginUserAccount", sp.getString(Constants.sp_loginName, ""));
			params.put("getType", "3");
			params.put("status", state);
			map = params;
			doSearch();
	 }

	 private void blurSearchByKeyWord(String keyword){
		    Map<String, Object> params = new HashMap<String, Object>();
		    //params.put("CurrentLoginUserAccount", "admin");
			//params.put("CurrentLoginUserAccount", sp.getString(Constants.sp_loginName, ""));
			params.put("getType", "3");
			params.put("keyword", keyword);
			map = params;
			doSearch();
	 }
	 
	 @Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		doSearch();
	}
	@Override
	public void handleItemView(BaseAdapter adapter, int position,View itemView, ViewGroup parent) {
		// TODO Auto-generated method stub
         LinearLayout car_color=(LinearLayout) itemView.findViewById(R.id.car_color);
		TextView dis_no=(TextView) itemView.findViewById(R.id.dis_no);
		TextView dis_date=(TextView) itemView.findViewById(R.id.dis_date);
		TextView dis_department=(TextView) itemView.findViewById(R.id.dis_department);
		TextView dis_name=(TextView) itemView.findViewById(R.id.dis_name);
		TextView dis_phone=(TextView) itemView.findViewById(R.id.dis_phone);
		TextView dis_1=(TextView) itemView.findViewById(R.id.dis_1);
		TextView dis_2=(TextView) itemView.findViewById(R.id.dis_2);
		
		
		//dis_no.setText(datalist.get(position).get("order_no") != null ? datalist.get(position).get("order_no").toString() : "");
		String timeString=(String) datalist.get(position).get("startdate");
		if(timeString.length()>=10){
			dis_no.setText(timeString.subSequence(0, 10));
		}
		String status=(String) datalist.get(position).get("status");
		int sta=0;
		if(status!=null&&!status.equals("")){
			sta=Integer.parseInt(status);
		}
		switch (sta) {
		case 1:
			dis_date.setText("已申请");
			break;
         case 2:
        	 dis_date.setText("已调度");
			break;
         case 3:
        	 dis_date.setText("申请取消");
			break;
         case 4:
        	 dis_date.setText("已取消");
			break;
         case 5:
        	 dis_date.setText("已提前完成");
			break;
         case 6:
        	 dis_date.setText("申请变更");
			break;
         case 7:
        	 dis_date.setText("变更并取消");
			break;
         case 8:
        	 dis_date.setText("已完成");
			break;

		default:
			break;
		}
		
		
		
		dis_department.setText(datalist.get(position).get("department") != null ? datalist.get(position).get("department").toString() : "");
		dis_name.setText(datalist.get(position).get("applicants") != null ? datalist.get(position).get("applicants").toString() : "");
		dis_phone.setText(datalist.get(position).get("ApplicantsPhone") != null ? datalist.get(position).get("ApplicantsPhone").toString() : "");
		//dis_phone.setText("13203156459");
		dis_phone.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		dis_1.setText(datalist.get(position).get("pick_place") != null ? datalist.get(position).get("pick_place").toString() : "");
		dis_2.setText(datalist.get(position).get("return_place") != null ? datalist.get(position).get("return_place").toString() : "");
		
		final String phoneString=(String) datalist.get(position).get("ApplicantsPhone");
		if(phoneString!=null&&!phoneString.equals("")){
		dis_phone.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Uri telUri = Uri.parse("tel:"+phoneString);
				Intent intent= new Intent(Intent.ACTION_DIAL, telUri);
				startActivity(intent);
			}
		});
		}
		// 颜色
		int  type=0;
		String  statusString=(String) datalist.get(position).get("status");
		if(statusString!=null&&!statusString.equals("")){
		 type=Integer.parseInt(statusString);
		}
		switch (type) {
		case 2://已调度
			car_color.setBackgroundColor(Color.parseColor("#33CC33"));             //绿色
			break;
		case 8://已完成
			car_color.setBackgroundColor(Color.parseColor("#cccccc"));             //灰色
			break;
		case 5:
			 car_color.setBackgroundColor(Color.parseColor("#cccccc"));             //灰色
			break;
		case 3:
			car_color.setBackgroundColor(Color.parseColor("#ebce67"));             //黄色
			break;
		case 6:
			car_color.setBackgroundColor(Color.parseColor("#ebce67"));             //黄色
			break;
		case 1:
			car_color.setBackgroundColor(Color.parseColor("#f55351"));             //红色
			break;
		case 4:
			car_color.setBackgroundColor(Color.parseColor("#6ec4e8"));  
			break;
		case 7:
			car_color.setBackgroundColor(Color.parseColor("#6ec4e8"));                                                   //不显示
			break;
		default:
			break;
		}
	}

	    @Override
	       protected void itemClick(AdapterView<?> arg0, View view, int position,
			long id) {
		          // TODO Auto-generated method stub
		          super.itemClick(arg0, view, position, id);
		          position=position-1;
		          String order_no=datalist.get(position).get("order_no") != null ? datalist.get(position).get("order_no").toString() : "";
		          String order_state=datalist.get(position).get("status") != null ? datalist.get(position).get("status").toString() : "";
		          Intent intent=new Intent(VehileDispatchActivity.this, VehileDispatchDitialActivity.class);
		          intent.putExtra("order_no", order_no);
		          intent.putExtra("order_state", order_state);
		          startActivity(intent);
	        }

	    @Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.leftImage:
				finish();
				break;
			case R.id.rightImage:
				Intent intent=new Intent(VehileDispatchActivity.this, vehileApply_dispatchActivity.class);
				startActivity(intent);
				break;
			case R.id.search_img:
			String temp = search_text.getText().toString();
	              blurSearchByKeyWord(temp);
				break;
			}
		}
	    
	@Override
	public void initParams() {
		// TODO Auto-generated method stub
		parentResId = R.layout.activity_vehiledispatch_list;
		listItemResId = R.layout.item_vehiledispatch;
		from = new String[] { "plate_no" };
		ids = new int[] { R.id.dis_no};
		url = UrlManager.appRemoteUrl + UrlManager.GET_ORDER_LIST;
		Map<String, Object> params = new HashMap<String, Object>();
	//	params.put("CurrentLoginUserAccount", "admin");
		sp = SmApplication.sp;
	//	params.put("CurrentLoginUserAccount", sp.getString(Constants.sp_loginName, ""));
		params.put("getType", "3");
		map = params;
	}

}
