package com.cjwsjy.app.vehicle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.cjwsjy.app.R;
import com.cjwsjy.app.utils.HttpClientUtil;
import com.cjwsjy.app.utils.ValidUtil;

import android.Manifest;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.*;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
public class orderQueryDitialActivity extends BaseActivity2 implements
		OnClickListener {
	private Map dataMap = new  HashMap<String, Object>();
	private List<Map<String, Object>> datalist = new ArrayList<Map<String,Object>>();
	
	private String order_no;
	private String m_strObject;
	private TextView query_order_no;
	private TextView query_date;
	private TextView query_time;
	private TextView query_car_no;
	private TextView query_order_status;
	private TextView query_driver;
	private TextView query_count;
	private TextView query_reason;
	private TextView query_plite_address;
	private TextView query_return_address;
	private TextView query_tujing_address;

	//经纬度
	private  double latitude;
	private  double longitude;

	private String vehicleUrl = com.cjwsjy.app.SmApplication.vehicle_ip;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_queryditail);
		setHeadView(R.drawable.onclick_back_btn, "", "用车查询", 0, "", this, this, this);
         
		Intent intent = this.getIntent();
		order_no=intent.getStringExtra("order_no");
		init();
		getDetail();
	}

	public void init()
	{
		query_order_no = (TextView) findViewById(R.id.query_order_no);
		query_order_status = (TextView) findViewById(R.id.query_order_status);
		query_date = (TextView) findViewById(R.id.query_date);
		query_time = (TextView) findViewById(R.id.query_time);
		query_car_no = (TextView) findViewById(R.id.query_car_no);
		query_driver = (TextView) findViewById(R.id.query_driver);
		query_count = (TextView) findViewById(R.id.query_count);
		query_reason = (TextView) findViewById(R.id.query_reason);
		query_plite_address = (TextView) findViewById(R.id.query_plite_address);
		query_return_address = (TextView) findViewById(R.id.query_return_address);
		query_tujing_address = (TextView) findViewById(R.id.query_tujing_address);

		LinearLayout ll = (LinearLayout)findViewById(R.id.license_plate);
		ll.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				android.util.Log.i("cjwsjy", "------LinearLayout-------");
				order_no = query_car_no.getText().toString();
				//order_no = "鄂A83W38";
				int length = order_no.length();
				if(length==0)
				{
					Toast.makeText(orderQueryDitialActivity.this, "没有车牌号", Toast.LENGTH_SHORT).show();
					return;
				}

				requestBeforeDialog(orderQueryDitialActivity.this);

				//请求车辆的gps位置
				Map<String, String> map = new HashMap<String, String>();
				map.put("v_no", order_no);
				String url=UrlManager.appRemoteUrl+UrlManager.PositionTextAPI;
				doRequest2(13, url, map);
			}
		});
	}
  
	     
	     public void initData(){
	    	 query_order_no.setText(dataMap.get("order_no") != null ? dataMap.get("order_no").toString() : "");
	    	 String dateString=(String) dataMap.get("startdate");
	    	 if(dateString!=null&&!dateString.equals("")){
	    		 String date=dateString;
	    		 String time="";
	    		 if(dateString.length()>=11){
					 date=dateString.substring(0, 11);
					 time=dateString.substring(11);
	    		 }
					query_date.setText(date);
					query_time.setText(time);
				}
	    	 query_car_no.setText(dataMap.get("plate_no") != null ? dataMap.get("plate_no").toString() : "");
	    	 query_driver.setText(dataMap.get("driver_name") != null ? dataMap.get("driver_name").toString() : "");
	    	 query_count.setText(dataMap.get("passenger") != null ? dataMap.get("passenger").toString() : "");
	    	 int reson_no=8;
	    	 String resean=(String) dataMap.get("VehiclesSubject");
	    	 query_reason.setText(resean);  
	    	 /*if(resean!=null&&!resean.equals("")){
	    		 reson_no=Integer.parseInt(resean);
	    	 }
	    	 switch (reson_no) {
			case 0:
				query_reason.setText("其它");  
				break;
           case 1:
        	   query_reason.setText("出差");
				break;
           case 2:
        	   query_reason.setText("接人");
				break;
           case 3:
        	   query_reason.setText( "参加会议");
				break;
			default:
				break;
			}*/
	    	 //query_reason.setText(dataMap.get("VehiclesSubject") != null ? dataMap.get("VehiclesSubject").toString() : "");
	    	 query_plite_address.setText(dataMap.get("pick_place") != null ? dataMap.get("pick_place").toString() : "");
	    	 query_return_address.setText(dataMap.get("return_place") != null ? dataMap.get("return_place").toString() : "");
	         query_tujing_address.setText(dataMap.get("Mid_place") != (null )? dataMap.get("Mid_place").toString() : "");
	         
	        int  type=0;
	 		String  statusString=(String) dataMap.get("status");
	 		if(statusString!=null&&!statusString.equals("")){
	 		 type=Integer.parseInt(statusString);
	 		}
	 		switch (type) {
	 		case 2://已调度
	 				query_order_status.setText("已调度");
	 			break;
	 		case 8://已完成
	 			query_order_status.setText("已完成");
	 			break;
	 		case 5:
	 				query_order_status.setText("已提前完成");
	 			break;
	 		case 3:
	 				query_order_status.setText("申请取消");
	 			break;
	 		case 6:
	 				query_order_status.setText("申请变更");
	 			break;
	 		case 1:
	 				query_order_status.setText("已申请");
	 			break;
	 		case 4:
	 				query_order_status.setText("已取消");
	 			break;
	 		case 7:
	 				query_order_status.setText("变更并取消");
	 			break;
	 		default:
	 			break;
	 		}
	     }
	/*
	 * 获取订单详情
	 */
       public void getDetail()
	   {
    	   if (!ValidUtil.isNullOrEmpty(order_no))
		   {
   			requestBeforeDialog(orderQueryDitialActivity.this);
   			Map<String, String> map = new HashMap<String, String>();
   			//map.put("order_no", order_no);
   			String url=vehicleUrl+UrlManager.GET_ORDER_DETAIL+"?order_no="+order_no;
   			doRequest2(0, url, map);
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

			Message msg = baseHandler.obtainMessage();

			strurl = url;
			if(requestId==11)
			{
				resultStr = HttpClientUtil.HttpUrlConnectionGet2(strurl, dataMap,"UTF-8");
			}
			else if( requestId==12 )
			{
				resultStr = HttpClientUtil.HttpUrlConnectionPost6(strurl, dataMap,"UTF-8",103);
				//resultStr = HttpClientUtil.HttpUrlConnectionGet2(strurl, dataMap,"UTF-8");
			}
			else if(requestId==13)
			{
				resultStr = HttpClientUtil.HttpUrlConnectionGet2(strurl, dataMap,"UTF-8");

				length = resultStr.length();
				if( length==0 )
				{
					msg.what = 4;
					msg.obj = resultStr;
					baseHandler.handleMessage(msg);
					return;
				}

				msg.what = 3;
				msg.obj = resultStr;
				baseHandler.handleMessage(msg);

				return;
			}
			else
			{
				resultStr = HttpClientUtil.HttpUrlConnectionGet(strurl, "UTF-8");
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

	protected void MapActivity2(String result)
	{
		//String result=(String) msg.obj;
		if( result!=null && result.contains("%GPS"))
		{
			result=result.substring(result.indexOf("%GPS"));
			String[] str=result.split(",");
			String  jingdu=str[3];
			String  weidu=str[4];
			double  jing=Double.parseDouble(jingdu);
			double  wei=Double.parseDouble(weidu);
			String  desc="";
			for (int i = 20; i < str.length; i++) {
				desc+=str[i];
			}
			desc=desc.replace("\\n%\"}", "");
			desc=desc.replace("\"}", "");
			//Intent intent=new Intent(selectVehicleActivity.this, VehileMapActivity.class);
			Intent intent=new Intent(orderQueryDitialActivity.this, VehileMapActivity2.class);
			intent.putExtra("jing", jing);
			intent.putExtra("wei", wei);//plate
			intent.putExtra("v_no", order_no);
			intent.putExtra("desc", desc);
			startActivity(intent);
		}
		else
		{
			Toast.makeText(orderQueryDitialActivity.this, "该车未装有GPS定位。。", Toast.LENGTH_SHORT).show();
			return;
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
			else if (msg.what == 3)
			{
				requestAfterDialog();

				m_strObject = (String) msg.obj;
				int ret = requestPermissions2(Manifest.permission.ACCESS_FINE_LOCATION,103);
				if(ret!=1) return;

				MapActivity2(m_strObject);
			}
			else if (msg.what == 4)
			{
				requestAfterDialog();
				Toast.makeText(getApplicationContext(), "无法获取车辆GPS数据", Toast.LENGTH_SHORT).show();
			}
		};
	};

	private int requestPermissions2(String strpermission, int code )
	{
		if( Build.VERSION.SDK_INT<23 ) return 1;

		//判断该权限
		int Permission = ContextCompat.checkSelfPermission(this, strpermission);
		if( Permission!= PackageManager.PERMISSION_GRANTED )
		{
			//没有权限，申请权限
			ActivityCompat.requestPermissions(this,new String[]{ strpermission}, code);
			return 1013;
		}

		return 1;
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
	{
		switch (requestCode)
		{
			case 103:
				if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
				{
					// 同意
					//insertDummyContact();
					MapActivity2(m_strObject);
				}
				else
				{
					// 拒绝
					Toast.makeText(orderQueryDitialActivity.this, "WRITE_CONTACTS Denied", Toast.LENGTH_SHORT).show();
				}
				break;
			default:
				super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}


	@Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub


		Map<String, Object> paraMap = new HashMap<String, Object>();

		switch (v.getId()) {
		case R.id.leftImage:
			finish();
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

		default:
			break;
		}
	}

}
