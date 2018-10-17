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
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
public class orderAuditeDitialActivity extends BaseActivity2 implements OnClickListener {
	private Map dataMap = new  HashMap<String, Object>();
	private List<Map<String, Object>> datalist = new ArrayList<Map<String,Object>>();
	
	private String order_no;
	private String plate_no;
	private String driver_id;
	private String order_state;
	private int state;
	private TextView tv_order_no;
	private TextView person_name;
	private TextView order_department;
	private TextView order_date;
	private TextView order_time;
	private TextView order_address;
	private TextView return_address;
	private TextView order_car_type;
	private TextView order_reason;
	private TextView order_driver;
	private TextView order_phone;
	private TextView order_plate_no;
	

	private Button btn_submit;
	private Button btn_done;
	
	private EditText et_content;
	
	private LinearLayout ll_driver;
	private LinearLayout ll_plate_no;
	
	private String AuditDesc;
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_auditeditail);
		setHeadView(R.drawable.onclick_back_btn, "", "用车审核", 0, "", this, this, this);
		sp = SmApplication.sp;
		Intent intent = this.getIntent();
		order_no=intent.getStringExtra("order_no");
		order_state=intent.getStringExtra("order_state");
		if(!ValidUtil.isNullOrEmpty(order_state))
		{
			state=Integer.parseInt(order_state);
		}
		init();
		getDetail();
	}
	
	     public void init(){
	    	 et_content=(EditText) findViewById(R.id.content);
	    	 tv_order_no=(TextView) findViewById(R.id.order_no_no);
	    	 person_name=(TextView) findViewById(R.id.person_name);
	    	 order_department=(TextView) findViewById(R.id.order_department);
	    	 order_date=(TextView) findViewById(R.id.order_date);
	    	 order_time=(TextView) findViewById(R.id.order_time);
	    	 order_address=(TextView) findViewById(R.id.order_address);
	    	 return_address=(TextView) findViewById(R.id.return_address);
	    	 order_car_type=(TextView) findViewById(R.id.order_car_type);
	    	 order_reason=(TextView) findViewById(R.id.order_reason);
	    	 order_driver=(TextView) findViewById(R.id.order_driver);
	    	 order_phone=(TextView) findViewById(R.id.order_phone);
	    	 order_plate_no=(TextView) findViewById(R.id.order_plate_no);
	    	 
	    	 btn_submit=(Button) findViewById(R.id.btn_submit);
	    	 btn_submit.setOnClickListener(this);
	    	 btn_done=(Button) findViewById(R.id.btn_done);
	    	 btn_done.setOnClickListener(this);
	    	 ll_driver=(LinearLayout) findViewById(R.id.ll_driver);
	    	 ll_plate_no=(LinearLayout) findViewById(R.id.ll_plate_no);
	     }
  
	     
	     public void initData(){
	    	 tv_order_no.setText(dataMap.get("order_no") != null ? dataMap.get("order_no").toString() : "");
	    	 person_name.setText(dataMap.get("applicants") != null ? dataMap.get("applicants").toString() : "");
	    	 order_department.setText(dataMap.get("department") != null ? dataMap.get("department").toString() : "");
	    	 String dateString=(String) dataMap.get("startdate");
	    	 if(dateString!=null&&!dateString.equals("")){
	    		 dateString=dateString.replace(" ", "");
	    		 String date="";
	    		 String time="";
	    		 if(dateString.length()>=10){
					 date=dateString.substring(5, 10);
					 date=date.replace("-", "月");
					 date=date+"日";
					 if(dateString.length()>=15)
					 time=dateString.substring(10,15);
	    		 }
	    		 order_date.setText(date);
	    		 order_time.setText(time);
				}
	    	 int reson_no=8;
	    	 String resean=(String) dataMap.get("VehiclesSubject");
	    	 order_reason.setText(resean);
	    	 order_address.setText(dataMap.get("pick_place") != null ? dataMap.get("pick_place").toString() : "");
	    	 return_address.setText(dataMap.get("return_place") != null ? dataMap.get("return_place").toString() : "");
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
	    	 if(state==9){            //待审核
	    	 btn_submit.setVisibility(View.VISIBLE);
	    	 btn_done.setVisibility(View.VISIBLE);
	    	 }else{                     //已审核
	    	 btn_submit.setVisibility(View.GONE);
		     btn_done.setVisibility(View.GONE);
	    	 et_content.setText(dataMap.get("auditDesc") != null ? dataMap.get("auditDesc").toString() : "(无审核意见)");
	    	 }
	    	 order_driver.setText(dataMap.get("driver_name") != null ? dataMap.get("driver_name").toString() : "");
	    	 order_plate_no.setText(dataMap.get("plate_no") != null ? dataMap.get("plate_no").toString() : "");
	    	 order_phone.setText(dataMap.get("phone") != null ? dataMap.get("phone").toString() : "");
	     }
	/*
	 * 获取订单详情
	 */
       public void getDetail(){
    	   if (!ValidUtil.isNullOrEmpty(order_no)) {
   			requestBeforeDialog(orderAuditeDitialActivity.this);
   			Map<String, String> map = new HashMap<String, String>();
   			map.put("order_no", order_no);
   			//String url=UrlManager.GET_ORDER_DETAIL+"?order_no="+order_no;
			String url=UrlManager.GET_ORDER_DETAIL;
   			doRequest2(0, url, map);
    	   }
       }

	public void doRequest2(final int requestId, final String url, final Map dataMap)
	{
		new ReqThread2(requestId, url, dataMap).start();
	}

	class ReqThread2 extends Thread{
		private int requestId;
		private String url;
		private Map dataMap;
		public ReqThread2(int requestId,  String url, Map dataMap)
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
				resultStr = HttpClientUtil.HttpUrlConnectionGet2(strurl, dataMap, "UTF-8");
			}
			else if(requestId==1)
			{
				resultStr = HttpClientUtil.HttpUrlConnectionGet2(strurl, dataMap, "UTF-8");
			}
			else if(requestId==2)
			{
				resultStr = HttpClientUtil.HttpUrlConnectionGet2(strurl, dataMap, "UTF-8");
			}
			else if(requestId==11)
			{
				resultStr = HttpClientUtil.HttpUrlConnectionGet(strurl, "UTF-8");
			}
			else if(requestId==12)
			{
				resultStr = HttpClientUtil.HttpUrlConnectionPost6(strurl, dataMap,"UTF-8",103);
				//resultStr = HttpClientUtil.HttpUrlConnectionGet2(strurl, dataMap,"UTF-8");
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
				//JSONObject jsonObj = new JSONObject(jsonStr);
				//JSONObject jsonObj = new JSONObject(responseMsg);
				ResultObject obj = DefaultDataParser.getInstance().parseData3(jsonObj);
				try{
					if (obj.isSuccess())
					{
						baseHandler.obtainMessage(0, requestId, 0, obj).sendToTarget();
						//baseHandler.obtainMessage(0, 0, 0, obj).sendToTarget();
					}
					else
					{
						baseHandler.obtainMessage(1, requestId, 0, obj).sendToTarget();
						//baseHandler.obtainMessage(1, 0, 0, obj).sendToTarget();
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

	private Handler baseHandler = new Handler() {

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
		switch (v.getId()) {
		case R.id.leftImage:
			finish();
			break;
		case R.id.btn_done:             //审核不通过
			AuditDesc=et_content.getText().toString();
			if(AuditDesc==null||AuditDesc.equals(""))
			{
				Toast.makeText(orderAuditeDitialActivity.this, "请填写审核意见！", Toast.LENGTH_LONG).show();
				return;
			}

			AuditDesc = AuditDesc.replaceAll(" ","%20");
			String url=UrlManager.SubmitAuditResult;                     
   			requestBeforeDialog(orderAuditeDitialActivity.this);
   			Map<String, String> map = new HashMap<String, String>();
   			map.put("order_no", order_no);
   			map.put("AuditResult", "0");
   			map.put("AuditDesc", AuditDesc);
   			map.put("account", sp.getString(Constants.sp_loginName, ""));
   			doRequest2(1, url, map);
			break;
		case R.id.btn_submit:         //审核通过
			AuditDesc=et_content.getText().toString();
			if(AuditDesc==null||AuditDesc.equals(""))
			{
				Toast.makeText(orderAuditeDitialActivity.this, "请填写审核意见！", Toast.LENGTH_LONG).show();
				return;
			}

			AuditDesc = AuditDesc.replaceAll(" ","%20");
			url=UrlManager.SubmitAuditResult;
   			requestBeforeDialog(orderAuditeDitialActivity.this);
   			Map<String, String> map2 = new HashMap<String, String>();
   			map2.put("order_no", order_no);
   			map2.put("AuditResult", "1");
   			map2.put("AuditDesc", AuditDesc);
   			map2.put("account", sp.getString(Constants.sp_loginName, ""));
   			doRequest2(2, url, map2);
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
		case 0:                 //审核通过
			dataMap=result.getDataMap();
			initData();
			break;
		case 1:             //审核不同通过
		case 2: 
			Toast.makeText(this, "提交成功!",Toast.LENGTH_LONG).show();
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
			Toast.makeText(this, "查询失败:"+result.getDesc(),Toast.LENGTH_LONG).show();
			break;
		case 1:
		case 2:
			Toast.makeText(this, "提交失败:"+result.getDesc(),Toast.LENGTH_LONG).show();
			break;
		default:
			break;
		}
	}

}
