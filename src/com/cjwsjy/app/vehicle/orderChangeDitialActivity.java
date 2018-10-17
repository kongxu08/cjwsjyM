package com.cjwsjy.app.vehicle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cjwsjy.app.R;
import com.cjwsjy.app.SmApplication;
import com.cjwsjy.app.utils.HttpClientUtil;
import com.cjwsjy.app.utils.ValidUtil;


import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.*;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * 申请详情
 * 
 * @Company: 广州市道一信息有限公司
 * @author: liuwende
 * 
 */
public class orderChangeDitialActivity extends BaseActivity2 implements OnClickListener
{
	private Map dataMap = new  HashMap<String, Object>();
	
	private String order_no;
	private TextView person_name;
	private TextView order_department;
	private TextView order_date;
	private TextView order_car_type;
	private TextView order_count;
	private TextView order_address;
	private TextView order_return_address;
	private TextView order_reason;
	private TextView order_tujing_address;
	private TextView order_comment;
	private SharedPreferences sp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_orderchangeditail);
		setHeadView(R.drawable.onclick_back_btn, "", "已申请", 0, "", this, this, this);
         
		Intent intent = this.getIntent();
		order_no=intent.getStringExtra("order_no");
		sp = SmApplication.sp;
		init();
		getDetail();
	}
	
	     public void init(){
	    	 person_name=(TextView) findViewById(R.id.person_name);
	    	 person_name.setText(sp.getString(Constants.sp_userName, ""));
	    	 order_department=(TextView) findViewById(R.id.order_department);
	    	 order_date=(TextView) findViewById(R.id.order_date);
	    	 order_car_type=(TextView) findViewById(R.id.order_car_type);
	    	 order_count=(TextView) findViewById(R.id.order_count);
	    	 order_address=(TextView) findViewById(R.id.order_address);
	    	 order_return_address=(TextView) findViewById(R.id.order_return_address);
	    	 order_reason=(TextView) findViewById(R.id.order_reason);
	    	 order_tujing_address=(TextView) findViewById(R.id.order_tujing_address);
	    	 order_comment=(TextView) findViewById(R.id.order_coment);
	    	 findViewById(R.id.btn_apply).setOnClickListener(this);
	    	 findViewById(R.id.btn_cancel).setOnClickListener(this);
	     }
  
	     
	     public void initData(){
	    	 //person_name.setText(dataMap.get("applicants_code") != null ? dataMap.get("applicants_code").toString() : "");
	    	 order_department.setText(dataMap.get("department") != null ? dataMap.get("department").toString() : "");
	    	 order_date.setText(dataMap.get("startdate") != null ? dataMap.get("startdate").toString() : "");
	    	 
	    	 int car_type=0;
	    	 String vehicleTypeString=(String) dataMap.get("vehicle_type");
	    	 if(vehicleTypeString!=null&&!vehicleTypeString.equals("")){
	    		 car_type=Integer.parseInt(vehicleTypeString);
	    	 }
	    	 switch (car_type) {
			case 1:
				order_car_type.setText("其他");
				break;
			case 2:
				order_car_type.setText("小轿车");
				break;
			case 3:
				order_car_type.setText("越野车");
				break;
			case 4:
				order_car_type.setText("商务车");
				break;
			case 5:
				order_car_type.setText("中客车");
				break;
			case 6:
				order_car_type.setText("大客车");
				break;

			default:
				break;
			}

	    	 order_count.setText(dataMap.get("passenger") != null ? dataMap.get("passenger").toString() : "");
	    	 order_address.setText(dataMap.get("pick_place") != null ? dataMap.get("pick_place").toString() : "");
	    	 order_return_address.setText(dataMap.get("return_place") != null ? dataMap.get("return_place").toString() : "");
	    	 if(!dataMap.get("Mid_place").equals("(null)"))
	    	 order_tujing_address.setText(dataMap.get("Mid_place") != null ? dataMap.get("Mid_place").toString() : "");
	    	 if(!dataMap.get("Mid_place").equals("(null)"))
	    	 order_comment.setText(dataMap.get("comment") != null ? dataMap.get("comment").toString() : "");
	    	 
	    	 int reson_no=8;
	    	 String resean=(String) dataMap.get("VehiclesSubject");
	    	 order_reason.setText(resean);  
	    	 /*if(resean!=null&&!resean.equals("")){
	    		 reson_no=Integer.parseInt(resean);
	    	 }
	    	 switch (reson_no) {
			case 0:
		     order_reason.setText("其它");  
				break;
           case 1:
  	         order_reason.setText("出差");
				break;
           case 2:
  	         order_reason.setText("接人");
				break;
           case 3:
  	         order_reason.setText( "参加会议");
				break;
			default:
				break;
			}*/
	     }
	/*
	 * 获取订单详情
	 */
	public void getDetail()
	{
		if (!ValidUtil.isNullOrEmpty(order_no))
		{
			requestBeforeDialog(orderChangeDitialActivity.this);
			Map<String, String> map = new HashMap<String, String>();
			map.put("order_no", order_no);
			String urlString = UrlManager.GET_ORDER_DETAIL; //+ "?order_no=" + order_no;
			doRequest2(0, urlString, map);
		}
	}

	public void doRequest2(final int requestId, final String url, final Map dataMap)
	{
		new ReqThread(requestId, url, dataMap).start();
	}

	class ReqThread extends Thread{
		private int requestId;
		private String url;
		private Map dataMap;
		public ReqThread(int requestId,  String url, Map dataMap)
		{
			this.requestId = requestId;
			this.url = url;
			this.dataMap = dataMap;
		}
		@Override
		public void run()
		{
			int length = 0;
			String strurl = "";
			String resultStr = "";

			strurl = UrlManager.appRemoteUrl+url;
			if(requestId==0)
			{
				resultStr = HttpClientUtil.HttpUrlConnectionGet2(strurl, dataMap,"UTF-8");
			}
			else if(requestId==11)
			{
				resultStr = HttpClientUtil.HttpUrlConnectionGet2(strurl, dataMap,"UTF-8");
			}
			else if(requestId==12)
			{
				resultStr = HttpClientUtil.HttpUrlConnectionPost6(strurl, dataMap,"UTF-8",103);
			}
			else
			{
				resultStr = HttpClientUtil.HttpUrlConnectionPost4(strurl, dataMap,"UTF-8");
			}

			length = resultStr.length();
			if( length==0 )
			{
				baseHandler.obtainMessage(2, requestId, 0, null).sendToTarget();
				return;
			}

			try {
				JSONObject jsonObj = new JSONObject(resultStr);
				ResultObject obj = DefaultDataParser.getInstance().parseData3(jsonObj);
				try{
					if (obj.isSuccess())
					{
						baseHandler.obtainMessage(0, requestId, 0, obj).sendToTarget();
					}
					else
					{
						baseHandler.obtainMessage(1, requestId, 0, obj).sendToTarget();
					}

				} catch(Exception e){
					baseHandler.obtainMessage(1, requestId, 0, obj)
							.sendToTarget();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
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
				onExecuteSuccess(msg.arg1, (ResultObject) msg.obj);
			}
			else if (msg.what == 1)
			{
				onExecuteFail(msg.arg1, (ResultObject) msg.obj);
			}
			else if (msg.what == 2)
			{
				Toast.makeText(getApplicationContext(), "暂无数据", Toast.LENGTH_SHORT).show();
			}
		};
	};

		@Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub


		Map<String, Object> paraMap = new HashMap<String, Object>();

		switch (v.getId()) {
		case R.id.leftImage:
			finish();
			break;
		case R.id.btn_apply:
			Intent intent=new Intent(orderChangeDitialActivity.this, vehileApplyActivity.class);
			intent.putExtra("type", 1);
			intent.putExtra("order_no", order_no);
			intent.putExtra("order_date", ""+dataMap.get("startdate"));
			intent.putExtra("car_type", ""+dataMap.get("vehicle_type"));
			intent.putExtra("order_count", ""+dataMap.get("passenger"));
			intent.putExtra("order_plite_address", ""+dataMap.get("pick_place"));
			intent.putExtra("order_mid_address", ""+dataMap.get("Mid_place"));
			intent.putExtra("order_return_address", ""+dataMap.get("return_place"));
			intent.putExtra("order_reason", ""+dataMap.get("VehiclesSubject"));
			intent.putExtra("comment", ""+dataMap.get("comment"));
			vehileApplyActivity.isFarst=true;
			startActivity(intent);
			finish();
			break;
		case R.id.btn_cancel:
	    	   if (!ValidUtil.isNullOrEmpty(order_no)) {
	   			requestBeforeDialog(orderChangeDitialActivity.this);
	   			Map<String, String> map = new HashMap<String, String>();
	   			String urlString=UrlManager.CANCEL_ORDER+"?order_no="+order_no;
	   			doRequest(1, urlString, map);
	    	   }
			break;
		default:
			break;
		}
	}
	
	
	@Override
	protected void onExecuteSuccess(int requestId, ResultObject result) {
		// TODO Auto-generated method stub
		requestAfterDialog();
		switch (requestId) {
		case 0:
			dataMap=result.getDataMap();
			initData();
			break;
		case 1:
			Toast.makeText(this, "取消成功",Toast.LENGTH_LONG).show();
			finish();
			break;

		default:
			break;
		}

	}

	@Override
	protected void onExecuteFail(int requestId, ResultObject result) {
		// TODO Auto-generated method stub
		requestAfterDialog();
		switch (requestId) {
		case 0:
			Toast.makeText(this, result.getDesc(),Toast.LENGTH_LONG).show();
			break;
		case 1:
			Toast.makeText(this, "取消失败，请重试",Toast.LENGTH_LONG).show();
			break;
		default:
			break;
		}
	}

}
