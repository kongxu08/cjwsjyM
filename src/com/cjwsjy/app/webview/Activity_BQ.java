package com.cjwsjy.app.webview;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cjwsjy.app.R;
import com.cjwsjy.app.base.BaseActivity;
import com.cjwsjy.app.homeFragment.MyPagerAdapter;

//import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.ArrayList;
import java.util.List;

/**
 * webView页面，资产扫码用，标题随页面变化
 */
public class Activity_BQ extends BaseActivity
{
	private String webUrl;
	private String titleName;
	private SharedPreferences sp;

	private TextView tv_navtitle;

	private ProgressDialog mDialog;

	private ViewPager mViewPager;
	private List<View> viewList;

	private WebView webview1;
	private WebView webview2;
	private WebView webview3;
	private WebView webview4;
	private WebView webview5;
	private WebView webview6;

	private ProgressBar bar;

	private ImageView imageView;
	private ImageView[] imageViews;

	private LinearLayout group;

	//private KProgressHUD hud;

	private boolean isRedirect = false;// 是否有重定向的页面

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_bq);

		bar = (ProgressBar)findViewById(R.id.progressBar_bq);
		//标题栏
		tv_navtitle = (TextView)this.findViewById(R.id.tv_navtitle);
		if(getIntent().getExtras().get("titleName")!=null)
		{
			titleName = getIntent().getExtras().get("titleName").toString();
		}
		tv_navtitle.setText(titleName);

		//标题栏上的后退
		ImageView iv_back = (ImageView) this.findViewById(R.id.iv_back);
		iv_back.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				onBackPressed();
			}
		});

		initWebView();

		//hud = KProgressHUD.create(this).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE);
		//hud.setAnimationSpeed(1);

//		hud = KProgressHUD.create(this)
//				.setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
//				.setLabel("正在加载...")
//				.setCancellable(true);
	}

	private void initWebView()
	{
		//WebView webview1;
		//WebView webview2;
		//WebView webview3;

		mViewPager = (ViewPager) findViewById(R.id.viewpager_bq);
		group = (LinearLayout) findViewById(R.id.viewGroup);

		LayoutInflater mInflater = getLayoutInflater().from(this);

		View view1 = mInflater.inflate(R.layout.webview_bq2, null);
		View view2 = mInflater.inflate(R.layout.webview_bq2, null);
		View view3 = mInflater.inflate(R.layout.webview_bq2, null);
		View view4 = mInflater.inflate(R.layout.webview_bq2, null);
		View view5 = mInflater.inflate(R.layout.webview_bq2, null);
		View view6 = mInflater.inflate(R.layout.webview_bq2, null);

		webview1 = (WebView) view1.findViewById(R.id.webView_BQ);
		webview2 = (WebView) view2.findViewById(R.id.webView_BQ);
		webview3 = (WebView) view3.findViewById(R.id.webView_BQ);
		webview4 = (WebView) view4.findViewById(R.id.webView_BQ);
		webview5 = (WebView) view5.findViewById(R.id.webView_BQ);
		webview6 = (WebView) view6.findViewById(R.id.webView_BQ);

		//webview1.clearFocus();
		//webview2.clearFocus();
		//webview3.clearFocus();

		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(webview1.getWindowToken(),0);

		//设置webview的属性
		//只在第一个界面显示loading框
		setwebview2(webview1);

		setwebview1(webview2);
		setwebview1(webview3);
		setwebview1(webview4);
		setwebview1(webview5);
		setwebview1(webview6);

//		webview1.loadUrl("http://10.6.189.12:82/portal/openbq?ObjectType=3&OpenType=Portal&ObjectID=1001A110000000000R7X&User=user09&Authkey=dc10561d1e88c9cbd0184654404fc69d");
//		webview2.loadUrl("http://10.6.189.12:82/portal/openbq?ObjectType=3&OpenType=Portal&ObjectID=1001A110000000000Q0D&User=user09&Authkey=dc10561d1e88c9cbd0184654404fc69d");
//		webview3.loadUrl("http://10.6.189.12:82/portal/openbq?ObjectType=3&OpenType=Portal&ObjectID=1001A110000000000Q0E&User=user09&Authkey=dc10561d1e88c9cbd0184654404fc69d");
//		webview4.loadUrl("http://10.6.189.12:82/portal/openbq?ObjectType=3&OpenType=Portal&ObjectID=1001A110000000000Q0F&User=user09&Authkey=dc10561d1e88c9cbd0184654404fc69d");
//		webview5.loadUrl("http://10.6.189.12:82/portal/openbq?ObjectType=3&OpenType=Portal&ObjectID=1001A110000000000VVX&User=user09&Authkey=dc10561d1e88c9cbd0184654404fc69d");
//		webview6.loadUrl("http://10.6.189.12:82/portal/openbq?ObjectType=3&OpenType=Portal&ObjectID=1001A110000000000W1T&User=user09&Authkey=dc10561d1e88c9cbd0184654404fc69d");

		//添加页面数据
		viewList = new ArrayList<View>();
		viewList.add(view1);
		viewList.add(view2);
		viewList.add(view3);
		viewList.add(view4);
		viewList.add(view5);
		viewList.add(view6);

		//实例化适配器
		//mViewPager.setAdapter(new MyViewPagerAdapter(viewList));
		//mViewPager.setCurrentItem(0); //设置默认当前页

		//View view = viewList.get(0);

		MyPagerAdapter pagerAdapter = new MyPagerAdapter(viewList);
		mViewPager.setAdapter(pagerAdapter);
		mViewPager.setCurrentItem(0);

		imageViews = new ImageView[6];
		for (int i = 0; i < 6; i++)
		{
			imageView = new ImageView(Activity_BQ.this);

			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(30, 30);
			lp.setMargins(10, 0, 10, 20);

			imageView.setLayoutParams(lp);
			imageView.setPadding(40, 0, 40, 0);

			imageViews[i] = imageView;

			if (i == 0)
			{
				// 默认选中第一张图片
				imageViews[i].setBackgroundResource(R.drawable.page_indicator);
			}
			else
			{
				imageViews[i].setBackgroundResource(R.drawable.page_indicator_focused);
			}

			group.addView(imageViews[i]);
		}

		mViewPager.addOnPageChangeListener(new GuidePageChangeListener());

		webview1.loadUrl("http://10.6.191.38:82/portal/openbq?ObjectType=3&OpenType=Portal&ObjectID=1001A110000000000R7X&User=chenyu2&Authkey=038ced78079b52b3340edbcd7b779b5d");

	}

	// 指引页面更改事件监听器
	private class GuidePageChangeListener implements ViewPager.OnPageChangeListener
	{
		@Override
		public void onPageScrollStateChanged(int arg0)
		{

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2)
		{

		}

		@Override
		public void onPageSelected(int arg0)
		{
			//android.util.Log.d("cjwsjy", "------arg0="+arg0+"-------PageChange");
			for (int i = 0; i < 6; i++)
			{
				if (arg0 == i)
				{
					imageViews[i].setBackgroundResource(R.drawable.page_indicator);
				}
				else
				{
					imageViews[i].setBackgroundResource(R.drawable.page_indicator_focused);
				}
			}
		}
	}

	private void setwebview1(WebView webviewIn)
	{
		WebSettings webSettings = webviewIn.getSettings();

		webSettings.setDomStorageEnabled(true);

		//设置页面自适应手机屏幕
		webSettings.setUseWideViewPort(true);
		webSettings.setLoadWithOverviewMode(true);

		//不显示webview缩放按钮
		webSettings.setDisplayZoomControls(false);

		// 设置支持JavaScript
		webSettings.setJavaScriptEnabled(true);
		//支持通过JS打开新窗口
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

		//使用缓存
		webSettings.setAppCacheEnabled(true);
		//UTF-8编码
		webSettings.setDefaultTextEncodingName("UTF-8");

		webSettings.setSupportZoom(true);
		webSettings.setBuiltInZoomControls(true);
		webSettings.setUseWideViewPort(true);
		webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
		webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
		webSettings.setLoadWithOverviewMode(true);
		webSettings.setAppCacheEnabled(true);
		webSettings.setDomStorageEnabled(true);

		webviewIn.setWebViewClient(new WebViewClient()
		{
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url)
			{
				Log.d("cjwsjy", "------Loading="+url+"-------BQ1");
				view.loadUrl(url);
				return true;
			}
		});

	}

	private void setwebview2(WebView webviewIn)
	{
		WebSettings webSettings = webviewIn.getSettings();

		webSettings.setDomStorageEnabled(true);

		//设置页面自适应手机屏幕
		webSettings.setUseWideViewPort(true);
		webSettings.setLoadWithOverviewMode(true);

		//不显示webview缩放按钮
		webSettings.setDisplayZoomControls(false);

		// 设置支持JavaScript
		webSettings.setJavaScriptEnabled(true);
		//支持通过JS打开新窗口
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

		//使用缓存
		webSettings.setAppCacheEnabled(true);
		//UTF-8编码
		webSettings.setDefaultTextEncodingName("UTF-8");

		webSettings.setSupportZoom(true);
		webSettings.setBuiltInZoomControls(true);
		webSettings.setUseWideViewPort(true);
		webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
		webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
		webSettings.setLoadWithOverviewMode(true);
		webSettings.setAppCacheEnabled(true);
		webSettings.setDomStorageEnabled(true);

		webviewIn.setWebViewClient(new WebViewClient()
		{
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url)
			{
				Log.d("cjwsjy", "------Loading="+url+"-------BQ2");
				view.loadUrl(url);
				return true;
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon)
			{
				super.onPageStarted(view, url, favicon);
				//showProgressDialog("正在加载 ，请等待...", true);

				//显示loading框
				showLoadingDialog();
			}

			@Override
			public void onPageFinished(WebView view, String url)
			{
				super.onPageFinished(view, url);

				//closeLoadingDialog();

				//closeProgressDialog();
				webview2.loadUrl("http://10.6.191.38:82/portal/openbq?ObjectType=3&OpenType=Portal&ObjectID=1001A110000000000Q0D&User=chenyu2&Authkey=038ced78079b52b3340edbcd7b779b5d");
				webview3.loadUrl("http://10.6.191.38:82/portal/openbq?ObjectType=3&OpenType=Portal&ObjectID=1001A110000000000Q0E&User=chenyu2&Authkey=038ced78079b52b3340edbcd7b779b5d");
				webview4.loadUrl("http://10.6.191.38:82/portal/openbq?ObjectType=3&OpenType=Portal&ObjectID=1001A110000000000Q0F&User=chenyu2&Authkey=038ced78079b52b3340edbcd7b779b5d");
				webview5.loadUrl("http://10.6.191.38:82/portal/openbq?ObjectType=3&OpenType=Portal&ObjectID=1001A110000000000VVX&User=chenyu2&Authkey=038ced78079b52b3340edbcd7b779b5d");
				webview6.loadUrl("http://10.6.191.38:82/portal/openbq?ObjectType=3&OpenType=Portal&ObjectID=1001A110000000000W1T&User=chenyu2&Authkey=038ced78079b52b3340edbcd7b779b5d");
			}
		});

		//final ProgressBar bar = (ProgressBar)findViewById(R.id.progressBar_bq);
		webviewIn.setWebChromeClient(new WebChromeClient()
		{
			@Override
			public void onProgressChanged(WebView view, int progress)
			{
				if( progress==100 )
				{
					closeLoadingDialog();
					bar.setVisibility(View.GONE);//INVISIBLE  GONE

				}
				else
				{
					if( bar.getVisibility()==View.GONE )
					{
						bar.setVisibility(View.VISIBLE);
					}
					bar.setProgress(progress);
				}

//				android.util.Log.d("cjwsjy", "------progress="+progress+"-------ProgressBar");
//				bar.setProgress(progress);
//				if( progress>=100 )
//				{
//					bar.setVisibility(View.GONE);
//				}

				super.onProgressChanged(view, progress);
			}

			@Override
			public void onReceivedTitle(WebView view, String title)
			{
				super.onReceivedTitle(view, title);
			}
		});

	}

	private void showLoadingDialog()
	{
		//hud = KProgressHUD.create(this).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE);
		//scheduleDismiss();
		//hud.show();
	}

	private void closeLoadingDialog()
	{
		android.util.Log.d("cjwsjy", "------closeLoadingDialog-------");
		//hud.dismiss();
	}

	private void scheduleDismiss() {
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				//hud.dismiss();
			}
		}, 2000);
	}

	//加载对话框
	private void showProgressDialog(String msg, boolean cancelable) 
	{
		if (mDialog == null && webview1 != null) {
			mDialog = new ProgressDialog(Activity_BQ.this);
			mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置风格为圆形进度条
			mDialog.setMessage(msg);
			mDialog.setIndeterminate(false);// 设置进度条是否为不明确
			mDialog.setCancelable(cancelable);// 设置进度条是否可以按退回键取消
												// WebViewHome.this
			mDialog.setCanceledOnTouchOutside(false);
			mDialog.show();
		}else{
			mDialog.setMessage(msg);
			mDialog.show();
		}
	}

	private void closeProgressDialog() 
	{
		if (mDialog != null && mDialog.isShowing())
		{
			mDialog.dismiss();
			mDialog = null;
		}
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
	}
	
	@Override
	public void onBackPressed()
	{
		webview1.loadUrl("about:blank");
		webview2.loadUrl("about:blank");
		webview3.loadUrl("about:blank");
		this.finish();
	}

}
