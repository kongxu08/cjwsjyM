package com.cjwsjy.app.vehicle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;


import com.cjwsjy.app.R;
import com.cjwsjy.app.utils.HttpClientUtil;

import android.R.integer;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * 选择车辆
 * 
 * @Company: 广州市道一信息有限公司
 * @author: liuwende
 * 
 */
public class selectVehiltTypeActivity extends BaseActivity2 implements
		OnClickListener {
	private Map dataMap = new  HashMap<String, Object>();
	private List<Map<String, Object>> datalist = new ArrayList<Map<String,Object>>();
	
   private LinearLayout vehile_type;
   
   private String vehicleUrl = com.cjwsjy.app.SmApplication.vehicle_ip;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_vehile);
		setHeadView(R.drawable.onclick_back_btn, "", "类型", 0, "", this, this, this);
		init();
		getDetail();
	}
	
	     public void init(){
	    	 vehile_type=(LinearLayout) findViewById(R.id.ll_vehile_type);
	     }
  
	     Map<String,String> map=new HashMap<String, String>();
	     public void initData(){
	    	 if(datalist.size()>0){
	    		 for (int i = 0; i < datalist.size(); i++) {
				
					View view=getLayoutInflater().inflate(R.layout.item_vehiel, null);
					TextView tv_item=(TextView) view.findViewById(R.id.vehile_name);
					 String vehile_name=(String) datalist.get(i).get("type_name");
					 String vehile_name_no=(String) datalist.get(i).get("type_code");
					tv_item.setText(vehile_name);
					vehile_type.addView(view);
					map.put(vehile_name, vehile_name_no);
					view.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							TextView tView=(TextView) arg0.findViewById(R.id.vehile_name);
							arg0.setBackgroundResource(R.drawable.bg_item_check);
							String name=tView.getText().toString();
							Intent intent = new Intent();  
							Bundle bundle = new Bundle();
							bundle.putString("type_name", name);  
							bundle.putString("vehile_name_no", map.get(name));  
				            intent.putExtras(bundle); 
							setResult(RESULT_OK, intent); 
							finish();
						}
					});
				}
	    	 }
	     }
	/*
	 * 获取订单详情
	 */
       public void getDetail()
	   {
		   requestBeforeDialog(selectVehiltTypeActivity.this);
		   Map<String, String> map = new HashMap<String, String>();
		   map.put("status", "0");
		   String url=UrlManager.appRemoteUrl+UrlManager.GET_VEHILE_TYPE;

		   //doRequest(0, url, map);
		   new ReqThread(0, url, map).start();
	   }

	class ReqThread extends Thread{
		private int requestId;
		private String url;
		private Map dataMap;
		public ReqThread(int requestId,  String url, Map dataMap){
			this.requestId = requestId;
			this.url = url;
			this.dataMap = dataMap;
		}
		@Override
		public void run()
		{
			int length = 0;
			String strurl;

			//strurl = "http://vms.cispdr.com:8080/"+url;
			//strurl = UrlManager.appRemoteUrl+url;
			//String resultStr = HttpClientUtil.HttpUrlConnectionPost(UrlManager.appRemoteUrl+url, dataMap,"UTF-8");
			String resultStr = HttpClientUtil.HttpUrlConnectionPost3(url, dataMap,"UTF-8");
			length = resultStr.length();
			if( length==0 )
			{
				baseHandler.obtainMessage(2, requestId, 0, null).sendToTarget();
				return;
			}

			String jsonStr = "{\"data\":" + resultStr + "}";

//        	Map<String, Object> resultMap = HttpClientUtil
//					.sendRequestFromHttpClient(UrlManager.appRemoteUrl+url, dataMap,
//							HttpClientUtil.DEFAULTENC);
//			String responseMsg = String.valueOf(resultMap.get("HTTP.RESULT.VLUEKEY"));
			//if (!StringHelper.isEmpty(responseMsg)) {
			try {
				JSONObject jsonObj = new JSONObject(resultStr);
				//JSONObject jsonObj = new JSONObject(jsonStr);
				//JSONObject jsonObj = new JSONObject(responseMsg);
				ResultObject obj = DefaultDataParser.getInstance().parseData3(jsonObj);
				try{
					if (obj.isSuccess()) {
						baseHandler.obtainMessage(0, requestId, 0, obj)
								.sendToTarget();
					} else {
						baseHandler.obtainMessage(1, requestId, 0, obj)
								.sendToTarget();
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
			//}
//		else{
//				baseHandler.obtainMessage(2, requestId, 0, null).sendToTarget();
//			}
		}
	}

	private Handler baseHandler = new Handler() {

		@Override
		public void handleMessage(android.os.Message msg)
		{
			requestAfterDialog();
			if (msg.what == 0)
			{
				onExecuteSuccess2(msg.arg1, (ResultObject) msg.obj);
			}
			else if (msg.what == 1)
			{
				onExecuteFail2(msg.arg1, (ResultObject) msg.obj);
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
			datalist=result.getListMap();
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
			Toast.makeText(this, "加载失败，请稍后再试",Toast.LENGTH_LONG).show();
			break;

		default:
			break;
		}
	}

	protected void onExecuteSuccess2(int requestId, ResultObject result) {
		// TODO Auto-generated method stub
		requestAfterDialog();
		switch (requestId) {
			case 0:
				datalist=result.getListMap();
				initData();
				break;

			default:
				break;
		}

	}

	protected void onExecuteFail2(int requestId, ResultObject result) {
		// TODO Auto-generated method stub
		requestAfterDialog();
		switch (requestId) {
			case 0:
				Toast.makeText(this, "加载失败，请稍后再试",Toast.LENGTH_LONG).show();
				break;

			default:
				break;
		}
	}
}
