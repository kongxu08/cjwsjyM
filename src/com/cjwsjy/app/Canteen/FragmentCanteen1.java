package com.cjwsjy.app.Canteen;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.support.v4.app.Fragment;

import com.cjwsjy.app.R;
import com.cjwsjy.app.SmApplication;
import com.cjwsjy.app.phonebook.EmployeePopupWindow;
import com.cjwsjy.app.utils.DateUtil;
import com.cjwsjy.app.utils.PhoneUtils;
import com.cjwsjy.app.utils.ValidUtil;
import com.do1.cjmobileoa.db.DBManager;
import com.do1.cjmobileoa.db.model.EmployeeVO;
import com.do1.cjmobileoa.db.model.HistoryVO;

import java.util.Date;

/**
 * 员工详细信息页面
 * 
 * 除了智能包好界面，其他 界面都在使用
 */
public class FragmentCanteen1 extends Fragment
{
	private String mCurrentUrl = "";

	private Activity m_activity;
	private DBManager db;
	private EmployeeVO employee;
	private ImageButton keep;
	private Context mContext;

	private Bitmap m_image = null;
	private ImageView iv_Image;
	private WebView m_webView;
	private CookieManager cookie;
	private ProgressBar mProgress;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.fragment_canteen_1, container,false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);

		mContext = getActivity();

		db = SmApplication.dbManager;

		m_activity = getActivity();

		m_webView = (WebView) getView().findViewById(R.id.webView_home);
		WebSettings webSettings = m_webView.getSettings();

		initWebView();

		m_webView.loadUrl("http://food.cjwsjy.com.cn:8080/Canteens/canteen/print");
	}

	/*
	 * 初始化联系人信息
	 */
	public void initView()
	{
		iv_Image = (ImageView)getView().findViewById(R.id.iv_user);

	}

	/**
	 * 初始化浏览器设置信息
	 */
	private void initWebView()
	{
		cookie = CookieManager.getInstance();
		WebSettings webSettings = m_webView.getSettings();
		webSettings.setJavaScriptEnabled(true); // 启用支持javascript
		webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);// 优先使用缓存
		webSettings.setAllowFileAccess(true);// 可以访问文件
		webSettings.setBuiltInZoomControls(true);// 支持缩放
		if (android.os.Build.VERSION.SDK_INT >= 11)
		{
			webSettings.setPluginState(PluginState.ON);
			webSettings.setDisplayZoomControls(false);// 支持缩放
		}
		m_webView.setWebViewClient(new MyWebViewClient());
		m_webView.setWebChromeClient(new MyWebChromeClient());
	}

	private class MyWebChromeClient extends WebChromeClient
	{
		@Override
		public void onReceivedTitle(WebView view, String title)
		{
			super.onReceivedTitle(view, title);
			onWebTitle(view, title);
		}

		@Override
		public void onReceivedIcon(WebView view, Bitmap icon)
		{
			super.onReceivedIcon(view, icon);
			onWebIcon(view, icon);
		}

		@Override
		public void onProgressChanged(WebView view, int newProgress) { // 进度
			super.onProgressChanged(view, newProgress);
			if (newProgress > 90) {
				//mProgress.setVisibility(View.GONE);
			}
		}
	}

	private class MyWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			onUrlLoading(view, url);
			boolean flag = super.shouldOverrideUrlLoading(view, url);
			mCurrentUrl = url;
			return flag;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			onUrlFinished(view, url);
		}
	}

	/**
	 * 当前WebView显示页面的标题
	 *
	 * @param view  WebView
	 * @param title web页面标题
	 */
	protected void onWebTitle(WebView view, String title)
	{
		if (m_activity != null && m_webView != null)
		{ // 必须做判断，由于webview加载属于耗时操作，可能会本Activity已经关闭了才被调用
			//((BaseActivity) m_activity).setActionBarTitle(m_webView.getTitle());
		}
	}

	/**
	 * 当前WebView显示页面的图标
	 *
	 * @param view WebView
	 * @param icon web页面图标
	 */
	protected void onWebIcon(WebView view, Bitmap icon) {
	}


	/**
	 * 载入链接之前会被调用
	 *
	 * @param view WebView
	 * @param url  链接地址
	 */
	protected void onUrlLoading(WebView view, String url) {
		//mProgress.setVisibility(View.VISIBLE);
		//cookie.setCookie(url, AccountHelper.getCookie());
	}

	/**
	 * 链接载入成功后会被调用
	 *
	 * @param view WebView
	 * @param url  链接地址
	 */
	protected void onUrlFinished(WebView view, String url) {
		mCurrentUrl = url;
		//mProgress.setVisibility(View.GONE);
	}

	private void showWin(int source, EmployeeVO employee)
	{
		EmployeePopupWindow menuWindow = new EmployeePopupWindow(
				this.getActivity(), 0, source, employee);

		// 显示窗口
		menuWindow.showAtLocation(
				this.getActivity().findViewById(R.id.ft_employeeInfo),
				Gravity.NO_GRAVITY | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
	{
		android.util.Log.d("cjwsjy", "------code="+requestCode+"-------");
	    switch (requestCode)
	    {
	        case 103:  //打电话
	            if( grantResults[0]==PackageManager.PERMISSION_GRANTED)
	            {
	                //同意
	            	showWin(EmployeePopupWindow.SOURCE_TEL, employee);
	            }
	            else
	            {
	                //拒绝
	                Toast.makeText(this.getActivity(), "没有权限，不能打电话", Toast.LENGTH_SHORT) .show();
	            }
	            break;
	        case 104:  //发短信
	            if( grantResults[0]==PackageManager.PERMISSION_GRANTED)
	            {
	                //同意
	            	showWin(EmployeePopupWindow.SOURCE_SMS, employee);
	            }
	            else
	            {
	                //拒绝
	                Toast.makeText(this.getActivity(), "没有权限，不能发短信", Toast.LENGTH_SHORT) .show();
	            }
	        	break;
	        case 105://通讯录
	            if( grantResults[0]==PackageManager.PERMISSION_GRANTED)
	            {
	                //同意
					if(PhoneUtils.queryContact(mContext,employee.getPhoneNumber()))
					{
						PhoneUtils.insertContact(mContext,
								employee.getUserDisplayName(),
								employee.getPhoneNumber(), employee.getMobileIphone(), employee.getmobileIphoneShortNumber(),employee.getEmail(),m_image,null);
						Builder builder = new Builder(getActivity());
						builder.setTitle("消息提示");
						builder.setMessage("添加成功！");
						builder.setPositiveButton("确定", null);
						builder.show();
					}else{
						Builder builder = new Builder(getActivity());
						builder.setTitle("消息提示");
						builder.setMessage("通讯录中存在该号码！");
						builder.setPositiveButton("确定", null);
						builder.show();
					}
	            }
	            else 
	            {
	                //拒绝
	                Toast.makeText(this.getActivity(), "没有权限，不能添加到通讯录", Toast.LENGTH_SHORT) .show();
	            }
	        	break;
	        default:
	            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	    }
	}
}
