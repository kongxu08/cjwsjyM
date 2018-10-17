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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cjwsjy.app.R;
import com.cjwsjy.app.SmApplication;
import com.cjwsjy.app.utils.ValidUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
/**
 * 变更申请
 * @author think
 *
 */

public class orderQueryActivity extends BaseListActivity4 implements OnClickListener{
	private EditText search_text;
	private SharedPreferences sp;
	
	private String vehicleUrl = com.cjwsjy.app.SmApplication.vehicle_ip;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		sp = SmApplication.sp;
		setHeadView(R.drawable.onclick_back_btn, "", "用车查询", R.drawable.onclick_new_apply_btn, "新申请", this, this, this);
		init();
		requestBeforeDialog(this);
	}

	public void init()
	{
		listview.setMode(Mode.PULL_FROM_END);
		search_text = (EditText) findViewById(R.id.search_text);
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
	
	  private void blurSearch(String state)
	  {
		    Map<String, Object> params = new HashMap<String, Object>();
		  //  params.put("CurrentLoginUserAccount", "admin");
		    params.put("CurrentLoginUserAccount", sp.getString(Constants.sp_loginName, ""));
			params.put("getType", "0");
			params.put("keyword", state);
			map = params;
			doSearch();
	  }
	 
	@Override
	public void handleItemView(BaseAdapter adapter, int position, View itemView, ViewGroup parent)
	{
		// TODO Auto-generated method stub

		TextView order_no=(TextView) itemView.findViewById(R.id.order_no);
		TextView car_type=(TextView) itemView.findViewById(R.id.order_car_type);
		TextView order_time=(TextView) itemView.findViewById(R.id.order_time);
		TextView order_address=(TextView) itemView.findViewById(R.id.order_address);
		TextView plate_no=(TextView) itemView.findViewById(R.id.order_car_no);
		TextView order_person_name=(TextView) itemView.findViewById(R.id.order_person_name);
		
		TextView icon_car=(TextView) itemView.findViewById(R.id.icon_car);
		TextView icon_smile=(TextView) itemView.findViewById(R.id.icon_smile);
		LinearLayout  car_color=(LinearLayout) itemView.findViewById(R.id.car_color);
		
		ImageView img= (ImageView) itemView.findViewById(R.id.img);
		ImageViewTool.getAsyncImageBgReal(""+datalist.get(position).get("ImgUrl"), img, 0);
		
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
	//	car_type.setText(datalist.get(position).get("department") != null ? datalist.get(position).get("department").toString() : "");
		String timeString=(String) datalist.get(position).get("startdate");
		if(timeString!=null){
		if(timeString.length()>15){
			order_time.setText(timeString.subSequence(0, 16));
		}else{
			order_time.setText(timeString);
		}
		}
		//order_time.setText(datalist.get(position).get("startdate") != null ? datalist.get(position).get("startdate").toString().subSequence(0, 16): "");
		order_address.setText(datalist.get(position).get("return_place") != null ? datalist.get(position).get("return_place").toString() : "");
		
		String plateString=(String) datalist.get(position).get("plate_no");
		if(ValidUtil.isNullOrEmpty(plateString)){
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
	       protected void itemClick(AdapterView<?> arg0, View view, int position,
			long id) {
	          // TODO Auto-generated method stub
	          super.itemClick(arg0, view, position, id);
	          position=position-1;
	          String order_no=datalist.get(position).get("order_no") != null ? datalist.get(position).get("order_no").toString() : "";
	          Intent intent=new Intent(orderQueryActivity.this, orderQueryDitialActivity.class);
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
			case R.id.rightImage:
				Intent intent=new Intent(this, vehileApplyActivity.class);
				intent.putExtra("type", 3);
				startActivity(intent);
				break;
			case R.id.search_img:
				  String temp = search_text.getText().toString();  
	              blurSearch(temp);
				break;
			}
			
			}
	    
	    @Override
	    protected void onResume() {
	    	// TODO Auto-generated method stub
	    	super.onResume();
				doSearch();
	    }
	@Override
	public void initParams() {
		// TODO Auto-generated method stub
		parentResId = R.layout.activity_orderquery_list;
		listItemResId = R.layout.item_orderquery;
		from = new String[] { "plate_no" };
		ids = new int[] { R.id.order_car_no};
		url =vehicleUrl+ UrlManager.GET_ORDER_LIST;
		Map<String, Object> params = new HashMap<String, Object>();
		//params.put("CurrentLoginUserAccount", "admin");
		sp = SmApplication.sp;
		params.put("CurrentLoginUserAccount", sp.getString(Constants.sp_loginName, ""));
		params.put("getType", "0");
		map = params;
	}

}
