package com.cjwsjy.app.vehicle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.cjwsjy.app.R;
import com.cjwsjy.app.SmApplication;
import com.cjwsjy.app.utils.HttpClientUtil;
import com.cjwsjy.app.utils.ValidUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 变更申请
 * @author think
 *
 */

public class orderAuditeActivity extends BaseListActivity2 implements OnClickListener{
	private RadioGroup rGroup;
	private EditText search_text; 
	private SharedPreferences sp;
	public List<Map<String, Object>> statusdatalist1 = new ArrayList<Map<String,Object>>();
	public List<Map<String, Object>>statusdatalist2 = new ArrayList<Map<String,Object>>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	
		setHeadView(R.drawable.onclick_back_btn, "", "用车审核", 0, "", this, this, this);
		init();
		requestBeforeDialog(this);
		sp = SmApplication.sp;
	}
	
	public void init()
	{
		listview.setMode(Mode.PULL_FROM_END);
		rGroup=(RadioGroup) findViewById(R.id.rg_state);
		rGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				// TODO Auto-generated method stub
				
				switch (arg1) {
				case R.id.rb_state_1:              //已审核
					blurSearchByState("5");
					break;
               case R.id.rb_state_2:             //未审核
            	   blurSearchByState("4");
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

	// 5 已审核
	// 4 未审核
	private void blurSearchByState(String state)
	{
		Map<String, Object> params = new HashMap<String, Object>();
		//params.put("CurrentLoginUserAccount", "admin");
		params.put("CurrentLoginUserAccount", sp.getString(Constants.sp_loginName, ""));
		params.put("getType", state);
		map = params;
		doSearch5();
	}

	 private void blurSearchByKeyWord(String keyword){
		    Map<String, Object> params = new HashMap<String, Object>();
		    //params.put("CurrentLoginUserAccount", "admin");
			params.put("CurrentLoginUserAccount", sp.getString(Constants.sp_loginName, ""));
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

	private void doSearch5()
	{
		if(isRequest)
		{
			return;
		}
		isReflesh = true;
		int currentPage = 1;
		map.put("current_page", currentPage+"");
		//	map.put("rows", pageSize);

		doRequest5(1098, url, map );
	}

	private void doRequest5( int requestId,  String url,  Map dataMap)
	{
		new ReqThread5(requestId, url, dataMap).start();
	}

	class ReqThread5 extends Thread
	{
		private int requestId;
		private String url;
		private Map dataMap;
		public ReqThread5(int requestId,  String url, Map dataMap){
			this.requestId = requestId;
			this.url = url;
			this.dataMap = dataMap;
		}

		@Override
		public void run()
		{
			int length = 0;
			String strurl;

			//strurl = UrlManager.appRemoteUrl+url;
			//String resultStr = HttpClientUtil.HttpUrlConnectionPost4(strurl, dataMap,"UTF-8");
			String resultStr = HttpClientUtil.HttpUrlConnectionGet2(url, dataMap,"UTF-8");
			length = resultStr.length();
			if( length==0 )
			{
				baseHandler.obtainMessage(2, requestId, 0, null).sendToTarget();
				return;
			}

			//String jsonStr = "{\"data\":" + resultStr + "}";

			try
			{
				JSONObject jsonObj = new JSONObject(resultStr);
				//JSONObject jsonObj = new JSONObject(jsonStr);
				//JSONObject jsonObj = new JSONObject(responseMsg);
				com.cjwsjy.app.vehicle.ResultObject obj = DefaultDataParser.getInstance().parseData3(jsonObj);
				try
				{
					if (obj.isSuccess())
					{
						baseHandler.obtainMessage(0, requestId, 0, obj)
								.sendToTarget();
					}
					else
					{
						baseHandler.obtainMessage(1, requestId, 0, obj)
								.sendToTarget();
					}

				}
				catch(Exception e)
				{
					baseHandler.obtainMessage(1, requestId, 0, obj)
							.sendToTarget();
				}
			}
			catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private Handler baseHandler = new Handler()
	{
		@Override
		public void handleMessage(android.os.Message msg)
		{
			requestAfterDialog();
			if (msg.what == 0)
			{
				onExecuteSuccess2(msg.arg1, (com.cjwsjy.app.vehicle.ResultObject)msg.obj);
			}
			else if (msg.what == 1)
			{
				//onExecuteFail2(msg.arg1, (com.cjwsjy.app.vehicle.ResultObject) msg.obj);
				datalist.clear();
			}
			else if (msg.what == 2)
			{
				Toast.makeText(getApplicationContext(), "暂无数据", Toast.LENGTH_SHORT).show();
			}
		};
	};

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
		case 9:
			if (ValidUtil.isNullOrEmpty(plateString)){
				 plate_no.setText("待审核  申请人:");
				 order_person_name.setVisibility(View.VISIBLE);
				 order_person_name.setText(datalist.get(position).get("applicants")+"");
			 }else{
				 plate_no.setText(plateString);
			 }
			car_color.setBackgroundColor(Color.parseColor("#6ec4e8"));                                                   //不显示
			break;
		case 10:
			if (ValidUtil.isNullOrEmpty(plateString)){
				 plate_no.setText("审核未通过  申请人:");
				 order_person_name.setVisibility(View.VISIBLE);
				 order_person_name.setText(datalist.get(position).get("applicants")+"");
			 }else{
				 plate_no.setText(plateString);
			 }
			car_color.setBackgroundColor(Color.parseColor("#ebce67"));                                                   //不显示
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
		          Intent intent=new Intent(orderAuditeActivity.this, orderAuditeDitialActivity.class);
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
			case R.id.search_img:
			String	 temp = search_text.getText().toString();  
	              blurSearchByKeyWord(temp);
				break;
			}
		    
		}
			
	    
	    
	@Override
	public void initParams() {
		// TODO Auto-generated method stub
		parentResId = R.layout.activity_audite_list;
		listItemResId = R.layout.item_orderchange;
		from = new String[] { "plate_no" };
		ids = new int[] { R.id.order_car_no};
		url = UrlManager.appRemoteUrl + UrlManager.GET_ORDER_LIST;
		Map<String, Object> params = new HashMap<String, Object>();
	//	params.put("CurrentLoginUserAccount", "admin");
		sp = SmApplication.sp;
		params.put("CurrentLoginUserAccount", sp.getString(Constants.sp_loginName, ""));
		params.put("getType", "5");
		map = params;
	}

}
