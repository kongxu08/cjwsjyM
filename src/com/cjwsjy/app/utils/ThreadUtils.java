package com.cjwsjy.app.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ThreadUtils
{
	int m_turnoff = 0;

	private String m_model;
	private String m_operate;
	private String m_content;
	private String m_login;
	private String m_userid;
	private String m_phone;
	
	private static ThreadUtils threadutil = null;// 声明一个Emperor类的引用

	private ThreadUtils() {// 将构造方法私有
    }
	
	public static ThreadUtils getInstance() 
	{// 实例化引用
        if (threadutil == null) 
        {
        	threadutil = new ThreadUtils();
        }
        return threadutil;
    }

	//传递记录日志
	public void setturnoff( int value )
	{
		m_turnoff = value;
	}

	//传递参数
	public void setparm1( String login,String userid,String phone )
	{
		m_login = login;
		m_userid = userid;
		m_phone = phone;
	}
	
	//传递参数
	public void setparm2(String operate,String model,String content )
	{
		m_operate = operate;
		m_model = model;
		m_content = content;
	}
	
	//写日志
	public void writelog()
	{
		//日志
		Thread ThreadLog = new Thread(new ThreadLog());
		ThreadLog.start();
    }
	
	// LoginThread线程类
	class ThreadLog implements Runnable
	{
		@Override
		public void run()
		{
			SaveLog4();
		}
	}

	private int SaveLog()
	{
		int result = 0;
		long time = 0;
		String url = "";
		String strtime = "";
		String jsonstr = "";
		String appUrl = "";
		String strcontent = "";
		JSONArray jsonArray;
		JSONObject jsonObject;

		appUrl = UrlUtil.HOST;
		appUrl = "http://10.6.180.39:8080";

		jsonArray = new JSONArray();
		jsonObject = new JSONObject();
		time = System.currentTimeMillis();
		strtime = Long.toString( time );
		strcontent = m_content+"-"+m_phone;

		if( m_turnoff==1 ) return 1;

		try
		{
			jsonObject.put("userName", m_login);
			jsonObject.put("operateTime", strtime);
			jsonObject.put("logLevel", "INFO");
			jsonObject.put("appType", "Android");
			jsonObject.put("modelName", m_model );
			jsonObject.put("operateName", m_operate );
			jsonObject.put("object", "");
			jsonObject.put("content", strcontent );
			jsonObject.put("errorMessage", "");
			jsonObject.put("result", "Success");
			
			jsonArray.put(jsonObject);
			
			jsonstr = jsonArray.toString();

			url =  appUrl+"/CEGWAPServer/saveLogs/"+m_userid;

			android.util.Log.i("cjwsjy", "------writelog1="+url+"-------ThreadUtils");
			android.util.Log.i("cjwsjy", "------writelog2="+jsonstr+"-------ThreadUtils");

			result = HttpClientUtil.httpPostWithJSON( url, jsonstr );

			//result = HttpClientUtil.HttpUrlConnectionPost6( url, jsonstr );
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		return 1;
	}

	private int SaveLog2()
	{
		int result = 0;
		long time = 0;
		String url = "";
		String strtime = "";
		String jsonstr = "";
		String appUrl = "";
		String strcontent = "";
		Map<String, String> dataMap = new HashMap<String, String>();


		android.util.Log.i("cjwsjy", "------SaveLog=2-------ThreadU");

		appUrl = UrlUtil.HOST;
		time = System.currentTimeMillis();
		strtime = Long.toString( time );
		strcontent = m_content+"-"+m_phone;

		if( m_turnoff==1 ) return 1;

		try
		{
			dataMap.put("userName", m_login);
			dataMap.put("operateTime", strtime);
			dataMap.put("logLevel", "INFO");
			dataMap.put("appType", "Android");
			dataMap.put("modelName", m_model );
			dataMap.put("operateName", m_operate );
			dataMap.put("object", "");
			dataMap.put("content", strcontent );
			dataMap.put("errorMessage", "");
			dataMap.put("result", "Success");

			url =  appUrl+"/CEGWAPServer/saveLogs/"+m_userid;

			HttpClientUtil.HttpUrlConnectionGet2(url, dataMap,"UTF-8");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return 1;
	}

	//POST  map
	private int SaveLog3()
	{
		int result = 0;
		long time = 0;
		String url = "";
		String strtime = "";
		String jsonstr = "";
		String appUrl = "";
		String strcontent = "";
		Map<String, String> dataMap = new HashMap<String, String>();

		android.util.Log.i("cjwsjy", "------SaveLog=2-------ThreadU");

		appUrl = UrlUtil.HOST;
		appUrl = "http://10.6.180.39:8080";
		//appUrl = "http://129.28.10.69:8080";
		time = System.currentTimeMillis();
		strtime = Long.toString( time );
		strcontent = m_content+"-"+m_phone;

		if( m_turnoff==1 ) return 1;

		try
		{
			dataMap.put("userName", m_login);
			dataMap.put("operateTime", strtime);
			dataMap.put("logLevel", "INFO");
			dataMap.put("appType", "Android");
			dataMap.put("modelName", m_model );
			dataMap.put("operateName", m_operate );
			dataMap.put("object", "");
			dataMap.put("content", strcontent );
			dataMap.put("errorMessage", "");
			dataMap.put("result", "Success");

			url =  appUrl+"/CEGWAPServer/saveLogs/"+m_userid;
			//url = "http://129.28.10.69:8080/Canteens/canteen/saveLogs/"+m_userid;

			android.util.Log.i("cjwsjy", "------SaveLog="+url+"-------ThreadU");

			HttpClientUtil.HttpUrlConnectionPost3(url, dataMap,"UTF-8");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return 1;
	}

	// POST jsonArray
	private int SaveLog4()
	{
		//int result = 0;
		long time = 0;
		String url = "";
		String strtime = "";
		String result;
		String jsonstr = "";
		String appUrl = "";
		String strcontent = "";
		JSONArray jsonArray;
		JSONObject jsonObject;

		appUrl = UrlUtil.HOST;

		jsonArray = new JSONArray();
		jsonObject = new JSONObject();
		time = System.currentTimeMillis();
		strtime = Long.toString( time );
		strcontent = m_content+"-"+m_phone;

		if( m_turnoff==1 ) return 1;

		try
		{
			jsonObject.put("userName", m_login);
			jsonObject.put("operateTime", strtime);
			jsonObject.put("logLevel", "INFO");
			jsonObject.put("appType", "Android");
			jsonObject.put("modelName", m_model );
			jsonObject.put("operateName", m_operate );
			jsonObject.put("object", "");
			jsonObject.put("content", strcontent );
			jsonObject.put("errorMessage", "");
			jsonObject.put("result", "Success");

			jsonArray.put(jsonObject);

			jsonstr = jsonArray.toString();

			url =  appUrl+"/CEGWAPServer/saveLogs/"+m_userid;

			result = HttpClientUtil.HttpUrlConnectionPostLog( url, jsonstr);
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}

		return 1;
	}
}

