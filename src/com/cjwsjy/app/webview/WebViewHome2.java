package com.cjwsjy.app.webview;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.cjwsjy.app.R;
import com.cjwsjy.app.SmApplication;
import com.cjwsjy.app.base.BaseActivity;
import com.cjwsjy.app.homeFragment.AdaperLixian;
import com.cjwsjy.app.item.FinishPeddingItem;
import com.cjwsjy.app.utils.HttpDownloader;
import com.cjwsjy.app.utils.CallOtherOpeanFile;
import com.cjwsjy.app.utils.StringHelper;
import com.cjwsjy.app.utils.ThreadUtils;
import com.cjwsjy.app.utils.UrlUtil;
import com.do1.cjmobileoa.db.DBManager;
import com.do1.cjmobileoa.db.model.BiaozhunVO;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * webView页面，用于会议管理，加载cookies数据
 */
public class WebViewHome2 extends BaseActivity {

	private WebView mWebView;
	private String webUrl;
	private String titleName;
	private String m_SDCardPath;
	private SharedPreferences sp;
	static final String mimeType = "text/html";
	static final String encoding = "utf-8";

	private TextView titleView;

	private String filePath;
	private ProgressDialog mDialog;
	private boolean isDownload = false;

	private boolean isRedirect = false;// 是否有重定向的页面

	//private FileUtil fileUitl;
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//requestWindowFeature(Window.FEATURE_PROGRESS);// 让进度条显示在标题栏上
		setContentView(R.layout.webview);
				
		ImageView iv_back = (ImageView) this.findViewById(R.id.iv_back);
        // 后退
		iv_back.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				onBackPressed();
			}
		});
		
		sp = SmApplication.sp;
		
		webUrl = getIntent().getExtras().get("webUrl").toString();
		titleName = getIntent().getExtras().get("titleName").toString();

		//title
		TextView tv_navtitle = (TextView)this.findViewById(R.id.tv_navtitle);
		tv_navtitle.setText(titleName);
		
		initWebView();
	}

	private void initWebView() 
	{
		WebView webView = new WebView(WebViewHome2.this);
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
				// Activity和Webview根据加载程度决定进度条的进度大小
				// 当加载到100%的时候 进度条自动消失
				// setTitle("Loading...");
				// setProgress(progress * 1000);
				// WebViewHome.this.getWindow().setFeatureInt(
				// Window.FEATURE_PROGRESS, progress * 100);
				// super.onProgressChanged(view, progress);

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
				//titleView.setText(title);
				super.onReceivedTitle(view, title);
			}
		});
		
		mWebView.requestFocusFromTouch();
		webSettings.setJavaScriptEnabled(true);// 设置支持脚本
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		webSettings.setAppCacheEnabled(true);
		mWebView.getSettings().setBuiltInZoomControls(true);// 设置支持缩放
		//mWebView.getSettings().setDefaultZoom(ZoomDensity.FAR);// 屏幕自适应网页(三星s4 4.2.2系统页面缩小了)
		mWebView.requestFocus();
		webSettings.setDefaultTextEncodingName("UTF-8");

		webSettings.setTextZoom(100);

		synCookies(WebViewHome2.this, webUrl);
		
//		String cookies = sp.getString("HTTP.COOKIE", "");
//		if (StringHelper.isEmpty(cookies))
//		{
//			String errorHtml = "<html><body><h1 style='color:#290EC9;font-size:16pt;'>离线登陆下无法使用该功能！</h1></body></html>";
//			mWebView.loadData(errorHtml, "text/html; charset=UTF-8", null);
//			return;
//		}
		
		//与页面JS交互
		mWebView.addJavascriptInterface( new JavaScriptInterface(this), "AndroidFun" );
		
		android.util.Log.d("cjwsjy", "------webUrl="+webUrl+"-------");
		
		mWebView.loadUrl(webUrl);
		
		mWebView.setWebViewClient(new WebViewClient() 
		{
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) 
			{
				//android.util.Log.d("cjwsjy", "------huiyi="+url+"-------Loading");
				
				if (url.indexOf("fuckcy") != -1) 
				{
					cuturl(url);
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
				closeProgressDialog();
				mWebView.getSettings().setBlockNetworkImage(false); 
			}

		});
	}

	private void showProgressDialog(String msg, boolean cancelable) 
	{
		if (mDialog == null && mWebView != null) {
			mDialog = new ProgressDialog(WebViewHome2.this);
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
	
	/**
	 * 给webView的链接添加cookies参数，cookies是登录时就获取的
	 */
	public void synCookies(Context context, String url) 
	{
		String cookies = sp.getString("HTTP.COOKIE", "");
		if (!StringHelper.isEmpty(cookies)) 
		{
			CookieSyncManager.createInstance(context);
			CookieManager cookieManager = CookieManager.getInstance();
			cookieManager.setAcceptCookie(true);
			if(needRemove())
			{
//				System.out.println("removeSessionCookie");
				cookieManager.removeSessionCookie();// 移除
			}
			cookieManager.setCookie("http://moa.cjwsjy.com.cn", cookies);// cookies是在HttpClient中获得的cookie
			CookieSyncManager.getInstance().sync();
		}
	}
	
	public boolean needRemove()
	{
		String str = "xiaomi,lenovo,samsung";
//		System.out.println("产品型号：" + android.os.Build.MODEL);
//        System.out.println("厂家名称: " + android.os.Build.MANUFACTURER);
        String pro = android.os.Build.MANUFACTURER.toLowerCase();
        if(str.indexOf(pro)>-1){
        	return false;
        }else{
        	return true;
        }
	}
	
	public void cuturl(String url)
	{
        String strurl = null;
        String attachment = "";
        try
		{
        	strurl = java.net.URLDecoder.decode(url,"UTF-8");
		}
		catch (UnsupportedEncodingException e)
		{
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
        
        if(strurl.contains("fuckcy"))
        {
        	attachment = strurl.substring(7, strurl.length());
        	DownAttachment(attachment);
        	return;
        };
	}
	
	//下载附件
	public void DownAttachment(String url)
	{
		
		String fileUrl = null;
		String attachmentName = null;
		
		fileUrl = url;
		attachmentName = getMD5Str(fileUrl);
		File file=new File(Environment.getExternalStorageDirectory() +"/Download"+ "/com.cjwsjy.app/attachment/"+attachmentName);      
		if(!file.exists())
		{
		   HttpDownloader httpDownLoader=new HttpDownloader(); 

           int result=httpDownLoader.downfile(fileUrl, "attachment/", attachmentName);
			if (result == 0)
			{
				Toast.makeText(WebViewHome2.this, "下载成功！", Toast.LENGTH_SHORT).show();
			}
			else if (result == -1)
			{
				Toast.makeText(WebViewHome2.this, "下载失败！", Toast.LENGTH_SHORT).show();
				return;
			}
	     }	
	     //下载完成打开附件
		String sdPath = Environment.getExternalStorageDirectory() + "/Download" +"/com.cjwsjy.app/attachment/";

		
		File file2 = new File(sdPath+attachmentName);
        //if(file.exists())
        CallOtherOpeanFile openfile = new CallOtherOpeanFile();
		openfile.openFile(WebViewHome2.this, file2);
	}
	
	/**
	 * MD5 加密
	 */
	private static String getMD5Str(String str)
	{
		MessageDigest messageDigest = null;
		try
		{
			messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.reset();
			messageDigest.update(str.getBytes("UTF-8"));
		}
		catch (NoSuchAlgorithmException e)
		{
			System.out.println("NoSuchAlgorithmException caught!");
			return null;
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
			return null;
		}

		byte[] byteArray = messageDigest.digest();
		StringBuffer md5StrBuff = new StringBuffer();
		for (int i = 0; i < byteArray.length; i++)
		{
			if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
				md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
			else
				md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
		}

		return md5StrBuff.toString();
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
        	Uri uri = Uri.parse("tel:"+phoneNum);
            Intent intent_tel = new Intent(Intent.ACTION_CALL, uri);
            startActivity(intent_tel);
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
//		if (mWebView.getUrl().toLowerCase().contains("hygl/main"))
//		{
//			android.util.Log.d("cjwsjy", "------finish-------Back");
//			
//			mWebView.loadUrl("about:blank");
//			this.finish();
//			return;
//		}
		
		if (mWebView.canGoBack())
		{
			if (mWebView.getUrl().toLowerCase().contains("main"))
			{ // 列表页直接退出
				removeCookies();
				//mWebView.clearView();
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
			removeCookies();
			//mWebView.clearView();
			mWebView.loadUrl("about:blank");
			this.finish();
		}
	}
	
	public void removeCookies()
	{
		CookieSyncManager.createInstance(WebViewHome2.this);
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.removeSessionCookie();
		cookieManager.removeAllCookie();
	}

	public static class ActivityListLixian extends BaseActivity implements AdapterView.OnItemClickListener
    {
        private SharedPreferences sp;
        private ThreadUtils m_threadlog;

        // 生成动态数组，加入数据
        List<FinishPeddingItem> listItems = new ArrayList<FinishPeddingItem>();

        private AdaperLixian listItemAdapter;
        private String m_loginname;
        private String appUrl;
		private String m_SDCardPath;

        ListView list;
		private DBManager db;

        @Override
        protected void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.activity_homelist);

			db = SmApplication.dbManager;
            m_threadlog = ThreadUtils.getInstance();  // 得到单例对象

            sp = SmApplication.sp;
            m_loginname = sp.getString("USERDATA.LOGIN.NAME", "");

			m_SDCardPath = sp.getString("USERDATA.SDCARD.PATH", "");

            appUrl = UrlUtil.HOST;

            list = (ListView) findViewById(R.id.homelist);

            TextView tv_navtitle = (TextView) findViewById(R.id.tv_navtitle);
            tv_navtitle.setText("规程规范");

            ImageView iv_back = (ImageView) this.findViewById(R.id.iv_back);
            // 后退
            iv_back.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    finish();
                }
            });

            initList();
        }

        public ListView initList()
        {
			boolean bresult = false;
			String strid = "";
            String strname = "";
			String strdelete = "";
			List<BiaozhunVO> employees1;

			employees1 = new ArrayList<BiaozhunVO>();

			//从数据库中查找
			employees1 = db.findBiaozhun();

			for (BiaozhunVO entity1 : employees1)
			{
				strid = entity1.getId();
				strname = entity1.getNewname();
				strdelete = entity1.getIsdelete();

				//是否已经删除
				bresult = strdelete.equals("1");
				if(bresult==true) continue;

				FinishPeddingItem listItem = new FinishPeddingItem();
				listItem.setTv_title(strname);
				listItems.add(listItem);
			}

            // 生成适配器的Item和动态数组对应的元素
            listItemAdapter = new AdaperLixian( ActivityListLixian.this, listItems );
			listItemAdapter.setListView(list);
            // 添加并且显示
            list.setAdapter(listItemAdapter);
            list.setOnItemClickListener(this);

            return list;
        }

        @Override
        public void onBackPressed()
        {
            onBackClick();
        }

        public void onBackClick()
        {
                //回退到搜索界面
                //Intent intent = new Intent( ActivityListLixian.this, ActivityBiaozhun.class);
                //startActivity(intent);
                finish();
        }

        //时间戳的转换成字符串时间
        public static String getDate(String timestampString)
        {
            //Long timestamp = Long.parseLong(timestampString)*1000;
            //String date = new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new java.util.Date(timestamp));
            Long timestamp = Long.parseLong(timestampString);
            String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date(timestamp));
            return date;
        }

        //单击listview，进入新闻详细页面
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            FinishPeddingItem item = listItems.get(position);
            String name = item.getTv_title();

			String sdPath = m_SDCardPath + "/Download/com.cjwsjy.app/attachment3/";
			sdPath = sdPath+name;
            File file2 = new File(sdPath);

			//Log.i("cjwsjy", "--------filePath="+sdPath+"-------");

            CallOtherOpeanFile openfile = new CallOtherOpeanFile();
            openfile.openFile(ActivityListLixian.this, file2);
        }



    }
}
