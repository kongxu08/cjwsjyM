package com.cjwsjy.app.webview;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import com.cjwsjy.app.R;
import com.cjwsjy.app.base.BaseActivity;
import com.cjwsjy.app.utils.HttpDownloader;
import com.cjwsjy.app.utils.CallOtherOpeanFile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * webView页面，资产扫码用，标题随页面变化
 */
public class WebViewscan extends BaseActivity {

	private WebView mWebView;
	private String webUrl;
	private String titleName;
	private SharedPreferences sp;

	private TextView tv_navtitle;

	private ProgressDialog mDialog;

	private boolean isRedirect = false;// 是否有重定向的页面

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//requestWindowFeature(Window.FEATURE_PROGRESS);// 让进度条显示在标题栏上
		setContentView(R.layout.webview);

		//initBtn();
		webUrl = getIntent().getExtras().get("webUrl").toString();
		
		//title
		tv_navtitle = (TextView)this.findViewById(R.id.tv_navtitle);
		if(getIntent().getExtras().get("titleName")!=null)
		{
			titleName = getIntent().getExtras().get("titleName").toString();
		}
		tv_navtitle.setText(titleName);
		
		//后退
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
	}

	private void initWebView() 
	{
		WebView webView = new WebView(WebViewscan.this);
		mWebView = (WebView) findViewById(R.id.webView_home);
		WebSettings webSettings = mWebView.getSettings();
		
		webSettings.setUseWideViewPort(true);
		
		// ie useragent
		//Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; EmbeddedWB 14.52 from: http://www.bsalsa.com/ EmbeddedWB 14.52; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET4.0C; .NET4.0E; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)
		// chrome useragent
		//Mozilla/5.0 (Windows NT 5.2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2062.124 Safari/537.36
		String ua = "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; EmbeddedWB 14.52 from: http://www.bsalsa.com/ EmbeddedWB 14.52; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET4.0C; .NET4.0E; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)";
		webSettings.setUserAgentString(ua);
		webSettings.setBlockNetworkImage(false); 	//图片下载阻塞
		webSettings.setRenderPriority(RenderPriority.HIGH);//提高渲染的优先级

		mWebView.setWebChromeClient(new WebChromeClient() 
		{
			@Override
			public void onProgressChanged(WebView view, int progress) 
			{
				//setTitle("页面加载中，请稍候..." + progress + "%");
				setProgress(progress * 100);
				if (progress == 100) 
				{
					setTitle(R.string.app_name);
				}
			}

			@Override
			public void onReceivedTitle(WebView view, String title) 
			{
				super.onReceivedTitle(view, title);
			}
		});
		
		mWebView.requestFocusFromTouch();
		webSettings.setJavaScriptEnabled(true);// 设置支持脚本
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		webSettings.setAppCacheEnabled(true);
		mWebView.requestFocus();
		webSettings.setDefaultTextEncodingName("UTF-8");

		//与页面JS交互
		mWebView.addJavascriptInterface( new JavaScriptInterface(this), "AndroidFun" );
		
		mWebView.loadUrl(webUrl);
		
		mWebView.setWebViewClient(new WebViewClient() 
		{
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) 
			{
				int result = 0;
				android.util.Log.d("cjwsjy", "------Loading="+url+"-------scan");
				if (url.indexOf("fuckcy") != -1) 
				{
					//cuturl(url);
					view.stopLoading();
					return true;
				}

				//装柜
				result = url.indexOf("zhuangguichenggong:0");
				if( result!=-1 )
				{
					//关闭
					finish();
					view.stopLoading();
					return true;
				}

				view.loadUrl(url);
				return true;
			}
			
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) 
			{
				super.onPageStarted(view, url, favicon);
				showProgressDialog("正在加载 ，请等待...", true);
			}
			
			@Override
			public void onPageFinished(WebView view, String url) 
			{
				super.onPageFinished(view, url);
				
				titleName = view.getTitle();
				tv_navtitle.setText(titleName);
				
				closeProgressDialog();
				mWebView.getSettings().setBlockNetworkImage(false); 
			}
		});
	}

	//加载对话框
	private void showProgressDialog(String msg, boolean cancelable) 
	{
		if (mDialog == null && mWebView != null) {
			mDialog = new ProgressDialog(WebViewscan.this);
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
		if (mDialog != null && mDialog.isShowing()) {
			mDialog.dismiss();
			mDialog = null;
		}
	}
	
	private class JavaScriptInterface 
	{
		private Context mContext;

		public JavaScriptInterface(Context context) 
		{
			this.mContext = context;
        }

        @JavascriptInterface
        public void callPhone( String phoneNum )
		{
//        	Uri uri = Uri.parse("tel:"+phoneNum);
//            Intent intent_tel = new Intent(Intent.ACTION_CALL, uri);
//            startActivity(intent_tel);
        }
    }
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		
		//清缓存
		mWebView.clearCache(true);
		mWebView.clearHistory();
		mWebView.clearFormData();	
	}
	
	@Override
	public void onBackPressed()
	{
		if( titleName.equals("资产扫码") )
		{
			mWebView.loadUrl("about:blank");
			this.finish();
			return;
		}

        //收入合同待办
        if (mWebView.getUrl().contains("redirectMechine"))
        {
            mWebView.loadUrl("about:blank");
            this.finish();
            return;
        }

		if (mWebView.canGoBack())
		{
			if (mWebView.getUrl().toLowerCase().contains("main"))
			{ // 列表页直接退出
				mWebView.loadUrl("about:blank");
				this.finish();
			}
			else if (mWebView.getUrl().toLowerCase().contains("fawen") && isRedirect)
			{
				mWebView.goBackOrForward(-2);
			}
			else
			{
				mWebView.goBack();
			}
		}
		else
		{
			mWebView.loadUrl("about:blank");
			this.finish();
		}
	}

}
