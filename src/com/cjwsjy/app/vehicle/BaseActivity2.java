package com.cjwsjy.app.vehicle;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.net.URLEncoder;
import java.net.URLDecoder;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import cmcc.gz.app.common.base.bean.FileBean;
import cmcc.gz.app.common.base.bean.RequestBean;
import cmcc.gz.app.common.base.task.BaseFileUploadTask;
import cmcc.gz.app.common.base.task.BaseTask;
import cmcc.gz.app.common.base.util.AndroidUtils;

import com.androidquery.AQuery;
import com.cjwsjy.app.R;
import com.cjwsjy.app.SmApplication;
import com.cjwsjy.app.utils.HttpClientUtil;
import com.cjwsjy.app.utils.StringHelper;
import com.cjwsjy.app.utils.ValidUtil;
import com.google.gson.Gson;

/**
 *基础的activity类
 * 
 */
public class BaseActivity2 extends cmcc.gz.app.common.base.activity.BaseActivity implements OnClickListener {
	public AQuery aq;
	public  ProgressDialog dialog;
	public String SERVER_URL;
	public SmApplication appManager;
	protected CustomProgressDialog progressDialog = null;
	public boolean isShowDialog = true;//是否显示提示框
	public boolean isCancel = true;//是否能够取消加载框
	public Context context;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		aq = new AQuery(this);
		appManager = (SmApplication) getApplication();
	}

	public void requestBeforeDialog(Context context)
	{
	//	dialog = ProgressDialog.show(context, "温馨提示", "正在请求，请稍后...");
		dialog = new ProgressDialog(context);
		dialog.setMessage("正在加载，请等待...");
		dialog.setCancelable(true);// 设置是否可以通过点击Back键取消
		dialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
		dialog.show();
	}

	public void requestAfterDialog()
	{
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
	}

	public String getFullUrl(String url, Map<String, Object> map) {
		if (map != null) {
			for (Map.Entry<String, Object> m : map.entrySet()) {
				url += m.getKey() + "=" + m.getValue() + "&";
			}
		}
		return SERVER_URL + url;
	}

	public void startProgressDialog(String msg, boolean flag) {
		if (progressDialog == null) {
			progressDialog = CustomProgressDialog.createDialog(this);
			progressDialog.setMessage(msg);
			progressDialog.setCancelable(flag);
		}

		progressDialog.show();
	}

	protected void stopProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}


	private OnRequestListener mRequestListenerBase = new OnRequestListener() {

		@Override
		public void onNetworkError(int requestId) {
			BaseActivity2.this.onNetworkError(requestId);
		}

		@Override
		public void onExecuteSuccess(int requestId, ResultObject resultObject) {
			BaseActivity2.this.onExecuteSuccess(requestId, resultObject);
		}

		@Override
		public void onExecuteFail(int requestId, ResultObject resultObject) {
			BaseActivity2.this.onExecuteFail(requestId, resultObject);
		}
	};

	protected void onNetworkError(int requestId) {
		requestAfterDialog();
		ToastUtil.showLongMsg(this, "网络连接失败，请检查网络");
	};


	/**
	 * 设置头部
	 *
	 * @param leftDrawable
	 *            头部左边按钮，传0默认不显示按钮
	 * @param rightDrawable
	 *            头部右边按钮，传0默认不显示按钮
	 * @param leftListener
	 *            头部左边按钮监听器，传null默认为返回
	 * @param rightListener
	 *            头部右边按钮监听器
	 */
	public void setHeadView(int leftDrawable, String leftText,
			String centerTitle, int rightDrawable, String rightText,
			OnClickListener leftListener, OnClickListener rightListener , OnClickListener rightListener2) {
		View headView = findViewById(R.id.headLayout2);
		if (headView != null)
		{
			if (leftDrawable != 0 || !ValidUtil.isNullOrEmpty(leftText))
			{
				headView.findViewById(R.id.leftImage).setVisibility(View.VISIBLE);
				((TextView) headView.findViewById(R.id.leftImage)).setText(leftText);
				if (leftDrawable != 0)
					headView.findViewById(R.id.leftImage).setBackgroundResource(leftDrawable);
				headView.findViewById(R.id.leftImage).setOnClickListener(leftListener == null ? this : leftListener);
			}
			else
			{
				headView.findViewById(R.id.leftImage).setVisibility(View.GONE);
			}
			if (rightDrawable != 0 || !ValidUtil.isNullOrEmpty(rightText)) {
				headView.findViewById(R.id.rightImage).setVisibility(
						View.VISIBLE);
				((TextView) headView.findViewById(R.id.rightImage))
						.setText(rightText);
				headView.findViewById(R.id.rightImage).setOnClickListener(
						rightListener);
				if (rightDrawable != 0)
					headView.findViewById(R.id.rightImage)
							.setBackgroundResource(rightDrawable);
			} else {
				headView.findViewById(R.id.rightImage).setVisibility(View.GONE);
			}
			
			((TextView) headView.findViewById(R.id.centerTitle))
					.setText(centerTitle);
		}
	}

	@Override
	public void onClick(View v) {
	}

	/**
	 * map转换成json
	 * 
	 * @param map
	 * @return
	 */
	public String getJsonStr(Map<String, Object> map) {
		return new JSONObject(map).toString();
	}

	/**
	 * 获取版本号
	 * 
	 * @return 当前应用的版本号
	 */
	public String getVersion() {
		try {
			PackageManager manager = this.getPackageManager();
			PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
			String version = info.versionName;
			return version;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
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
			if(requestId==10)
			{
				resultStr = HttpClientUtil.HttpUrlConnectionGet(strurl, "UTF-8");
			}
			else if(requestId==11)
			{
				resultStr = HttpClientUtil.HttpUrlConnectionGet2(strurl, dataMap,"UTF-8");
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

			//String jsonStr = "{\"data\":" + resultStr + "}";

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
			//}
//		else{
//				baseHandler.obtainMessage(2, requestId, 0, null).sendToTarget();
//			}
        }  
	}
	/**
	 * 请求
	 * 
	 * @param url
	 *            服务器地址（只需要服务器后面那一段）
	 * @param dataMap
	 *            参数
	 */
	public void doRequest(final int requestId, final String url, final Map dataMap) {
		

		Log.e(url+"请求参数:>>>>", dataMap != null ? dataMap.toString():"null");
		new ReqThread(requestId,  url, dataMap).start();  
		
		/*
		Log.e(url+"请求参数:>>>>", dataMap != null ? dataMap.toString():"null");
		RequestBean requestBean = new RequestBean();
		requestBean.setReqUrl(url);// 访问服务器的URL
		requestBean.setReqParamMap(dataMap);
		
		BaseTask baseTask = new BaseTask() {
			@Override
			protected void onPreExecute() {// before
				super.onPreExecute();
			}
     
			@Override
			protected void onPostExecute(Map result) {
				super.onPostExecute(result);
				 Log.e("result:>>>>", result.toString());
				isShowDialog = true;
				isCancel = true;
			
				try {
					ResultObject obj = DefaultDataParser.getInstance()
							.parseData2(result);
					
					if (obj.isSuccess()) {
						baseHandler.obtainMessage(0, requestId, 0, obj)
								.sendToTarget();
					} else {
						baseHandler.obtainMessage(1, requestId, 0, obj)
								.sendToTarget();
					}
			} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		
		};
		baseTask.execute(requestBean);
	
	*/}
	private void onNetWork(){
		Toast.makeText(getApplicationContext(), "网络连接超时",
				Toast.LENGTH_SHORT).show();
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

	protected void onExecuteSuccess(int requestId, ResultObject result) {
		requestAfterDialog();
	};

	protected void onExecuteFail(int requestId, ResultObject result)
	{
		requestAfterDialog();
		Toast.makeText(this, result.getDesc(), Toast.LENGTH_LONG).show();
	}
}
