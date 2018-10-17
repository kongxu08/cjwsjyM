package com.cjwsjy.app.vehicle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cjwsjy.app.R;
import com.cjwsjy.app.SmApplication;
import com.cjwsjy.app.utils.ValidUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;

import android.R.integer;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

/**
 * 变更申请
 * @author think
 *
 */

public class orderChangeActivity extends BaseListActivity2 implements
OnClickListener{
	private RadioGroup rGroup;
	private EditText search_text; 
	private SharedPreferences sp;
	public List<Map<String, Object>> statusdatalist1 = new ArrayList<Map<String,Object>>();
	public List<Map<String, Object>>statusdatalist2 = new ArrayList<Map<String,Object>>();
	
	private String vehicleUrl = UrlManager.appRemoteUrl;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	
		setHeadView(R.drawable.onclick_back_btn, "", "变更申请", 0, "", this, this, this);
		init();
		requestBeforeDialog(this);
		sp = SmApplication.sp;
	}
	
	public void init()
	{
		listview.setMode(Mode.PULL_FROM_END);
		rGroup=(RadioGroup) findViewById(R.id.rg_state_user);
		rGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				// TODO Auto-generated method stub
				
				switch (arg1) {
				case R.id.rb_state_user_1:              //全部
					blurSearchByState("");
					break;
               case R.id.rb_state_user_2:              //已申请
            	   blurSearchByState("1");
					break;
              case R.id.rb_state_user_3:              //已调度
            	   blurSearchByState("2");
	           break;

				default:
					break;
				}
			}
		});
		
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
			              blurSearchByKeyWord(temp);
			         }
			         });

		findViewById(R.id.search_img).setOnClickListener(this);
	}
	
	 private void blurSearchByState(String state){
		    Map<String, Object> params = new HashMap<String, Object>();
		    //params.put("CurrentLoginUserAccount", "admin");
			params.put("CurrentLoginUserAccount", sp.getString("USERDATA.LOGIN.NAME", ""));
			params.put("getType", "1");
			params.put("status", state);
			map = params;
			doSearch();
	 }

	 private void blurSearchByKeyWord(String keyword){
		    Map<String, Object> params = new HashMap<String, Object>();
		    //params.put("CurrentLoginUserAccount", "admin");
			params.put("CurrentLoginUserAccount", sp.getString("USERDATA.LOGIN.NAME", ""));
			params.put("getType", "1");
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

		TextView order_no=(TextView) itemView.findViewById(R.id.order_no);
		TextView car_type=(TextView) itemView.findViewById(R.id.order_car_type);
		TextView order_time=(TextView) itemView.findViewById(R.id.order_time);
		TextView order_address=(TextView) itemView.findViewById(R.id.order_address);
		TextView plate_no=(TextView) itemView.findViewById(R.id.order_car_no);
		TextView order_person_name=(TextView) itemView.findViewById(R.id.order_person_name);
		
		ImageView icon_address_type=(ImageView) itemView.findViewById(R.id.icon_address_type);
		ImageView icon_car=(ImageView) itemView.findViewById(R.id.icon_car);
		ImageView icon_smile=(ImageView) itemView.findViewById(R.id.icon_smile);
		LinearLayout  car_color=(LinearLayout) itemView.findViewById(R.id.car_color);
		order_no.setText(datalist.get(position).get("order_no") != null ? datalist.get(position).get("order_no").toString() : "");
		
		String cartype=(String) datalist.get(position).get("vehicle_type");
		int  type_car=0;
		if(cartype!=null&&!cartype.equals("")){
			type_car=Integer.parseInt(cartype);
		}
		switch (type_car) {
		case 1:
			car_type.setText("其他");
			break;
		case 2:
			car_type.setText("小轿车");
			break;
		case 3:
			car_type.setText("越野车");
			break;
		case 4:
			car_type.setText("商务车");
			break;
		case 5:
			car_type.setText("中客车");
			break;
		case 6:
			car_type.setText("大客车");
			break;

		default:
			break;
		}
		//car_type.setText(datalist.get(position).get("department") != null ? datalist.get(position).get("department").toString() : "");
		String timeString=(String) datalist.get(position).get("startdate");
		if(timeString.length()>15){
			order_time.setText(timeString.subSequence(0, 16));
		}else{
			order_time.setText(timeString);
		}
		//order_time.setText(datalist.get(position).get("startdate") != null ? datalist.get(position).get("startdate").toString().subSequence(0, 16): "");
		String address=(String) datalist.get(position).get("pick_place");
		order_address.setText(address);
		ImageViewTool.getAsyncImageBgReal(""+datalist.get(position).get("ImgUrl"), icon_address_type, 0);
		/*if(address.contains("站")){
			icon_address_type.setBackgroundResource(R.drawable.icon_huoche);
		}else if(address.contains("机场")){
			icon_address_type.setBackgroundResource(R.drawable.icon_feiji);
		}else{
			icon_address_type.setBackgroundResource(R.drawable.icon_car);
		}*/
		
		
		String plateString=(String) datalist.get(position).get("plate_no");
		if(plateString==null||plateString.equals("")){
			//未派遣
		//	plate_no.setText("未派遣");
			icon_car.setVisibility(View.GONE);
			icon_smile.setVisibility(View.INVISIBLE);
			order_person_name.setVisibility(View.INVISIBLE);
		}else{
			icon_car.setVisibility(View.VISIBLE);
			icon_smile.setVisibility(View.VISIBLE);
			order_person_name.setVisibility(View.VISIBLE);
			order_person_name.setText(datalist.get(position).get("driver_name") != null ? datalist.get(position).get("driver_name").toString() : "");
		}
		// 颜色
		int  type=0;
		String  statusString=(String) datalist.get(position).get("status");
		if(statusString!=null&&!statusString.equals("")){
		 type=Integer.parseInt(statusString);
		}
		switch (type) {
		case 2://已调度
			 if (ValidUtil.isNullOrEmpty(plateString)){
				 plate_no.setText("已调度");
			 }else{
				 plate_no.setText(plateString);
			 }
			car_color.setBackgroundColor(Color.parseColor("#33CC33"));             //绿色
			break;
		case 8://已完成
			 if (ValidUtil.isNullOrEmpty(plateString)){
				 plate_no.setText("已完成");
			 }else{
				 plate_no.setText(plateString);
			 }
			car_color.setBackgroundColor(Color.parseColor("#cccccc"));             //灰色
			break;
		case 5:
			 if (ValidUtil.isNullOrEmpty(plateString)){
				 plate_no.setText("已提前完成");
			 }else{
				 plate_no.setText(plateString);
			 }
			 car_color.setBackgroundColor(Color.parseColor("#cccccc"));             //灰色
			break;
		case 3:
			 if (ValidUtil.isNullOrEmpty(plateString)){
				 plate_no.setText("申请取消");
			 }else{
				 plate_no.setText(plateString);
			 }
			car_color.setBackgroundColor(Color.parseColor("#ebce67"));             //黄色
			break;
		case 6:
			 if (ValidUtil.isNullOrEmpty(plateString)){
				 plate_no.setText("申请变更");
			 }else{
				 plate_no.setText(plateString);
			 }
			car_color.setBackgroundColor(Color.parseColor("#ebce67"));             //黄色
			break;
		case 1:
			 if (ValidUtil.isNullOrEmpty(plateString)){
				 plate_no.setText("已申请");
			 }else{
				 plate_no.setText(plateString);
			 }
			car_color.setBackgroundColor(Color.parseColor("#f55351"));             //红色
			break;
		case 4:
			if (ValidUtil.isNullOrEmpty(plateString)){
				 plate_no.setText("已取消");
			 }else{
				 plate_no.setText(plateString);
			 }
			car_color.setBackgroundColor(Color.parseColor("#6ec4e8"));  
			break;
		case 7:
			if (ValidUtil.isNullOrEmpty(plateString)){
				 plate_no.setText("变更并取消");
			 }else{
				 plate_no.setText(plateString);
			 }
			car_color.setBackgroundColor(Color.parseColor("#6ec4e8"));                                                   //不显示
			break;
		default:
			break;
		}
	}

	@Override
	protected void itemClick(AdapterView<?> arg0, View view, int position, long id)
	{
		// TODO Auto-generated method stub
		super.itemClick(arg0, view, position, id);
		position = position - 1;
		String order_no = datalist.get(position).get("order_no") != null ? datalist.get(position).get("order_no").toString() : "";
		Intent intent = new Intent(orderChangeActivity.this, orderChangeDitialActivity.class);
		intent.putExtra("order_no", order_no);
		startActivity(intent);
	}
	
	    
	    @Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.leftImage:
				finish();
				break;
			case R.id.search_img:
			String	 temp = search_text.getText().toString();  
	              blurSearchByKeyWord(temp);
				break;
			}
		    
		}
			
	    
	    
	@Override
	public void initParams() {
		// TODO Auto-generated method stub
		parentResId = R.layout.activity_orderchange_list;
		listItemResId = R.layout.item_orderchange;
		from = new String[] { "plate_no" };
		ids = new int[] { R.id.order_car_no};
		url =vehicleUrl +UrlManager.GET_ORDER_LIST;
		Map<String, Object> params = new HashMap<String, Object>();
	//	params.put("CurrentLoginUserAccount", "admin");
		sp = SmApplication.sp;
		params.put("CurrentLoginUserAccount", sp.getString(Constants.sp_loginName, ""));
		params.put("getType", "1");
		map = params;
	}

}
