package com.cjwsjy.app.vehicle;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;
import com.cjwsjy.app.R;
import com.cjwsjy.app.SmApplication;
import com.cjwsjy.app.utils.HttpClientUtil;
import com.cjwsjy.app.utils.StringHelper;

import com.do1.cjmobileoa.parent.callback.DefaultDataParser;
import com.do1.cjmobileoa.parent.callback.ResultObject;

public class ActivityVehcilelogo extends Activity
{
	 private String account;
	 private SharedPreferences sp;
	 private Map dataMap = new  HashMap<String, Object>();
	 public static boolean isAuditer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_vehiclelogo);
//		sp=getSharedPreferences("userdata", 0);
//		//account=sp.getString(Constants.sp_loginName, "");
		 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vehiclelogo);

		sp= SmApplication.sp;
		account=sp.getString("USERDATA.LOGIN.NAME", "");
		start();
	}
	
	public void start()
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				try
				{
					Thread.sleep(3000);
					IsAuditer(); // 是否是审查员
				}
				catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}

 	/*
	 * 判断是否是审核员
	 */
	public void IsAuditer()
	{
		android.util.Log.i("cjwsjy", "------IsAuditer-------");
		Map<String, String> map = new HashMap<String, String>();
		String url = UrlManager.ISAUDITER;
		map.put("account", account);
		new ReqThread(0, url, map).start();
	}
	
	Handler handler=new Handler()
	{
 	   public void handleMessage(android.os.Message msg) 
 	   {
 		   if(msg.what==1)
 		   {
 			   isAuditer=true;
 		   }
 		   else
 		   {
 			   isAuditer=false;
 		   }

		   //if( account.equals("wuaiying") ) isAuditer = true;
		   //isAuditer = true;
 		   Intent intent=new Intent();
 		   intent.setClass(ActivityVehcilelogo.this, MainUITabHostActivity.class);
 		   startActivity(intent);
 		   finish();
 	   };
    };
    
    class ReqThread extends Thread
    {
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
			//是否是审核员
			int length = 0;
			String vehicleUrl = UrlManager.appRemoteUrl;
			String strurl = vehicleUrl + url;
			String resultStr = HttpClientUtil.HttpUrlConnectionPost2(strurl, dataMap, "UTF-8");
			android.util.Log.d("cjwsjy", "------resultStr=" + resultStr + "-------");
			length = resultStr.length();
			if (length == 0) return;

			try
			{
				JSONObject jsonObj = new JSONObject(resultStr);
				String isFlag = jsonObj.getString("data");

				Message msg = new Message();
				if ("True".equals(isFlag))
				{
					msg.what = 1;
				}
				else
				{
					msg.what = 0;
				}
				handler.handleMessage(msg);
			}
			catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
     	}
	}
}
