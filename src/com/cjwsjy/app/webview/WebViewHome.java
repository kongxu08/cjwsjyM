package com.cjwsjy.app.webview;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.cjwsjy.app.R;
import com.cjwsjy.app.SmApplication;
import com.cjwsjy.app.base.BaseActivity;
import com.cjwsjy.app.homeFragment.ActivityZhibo;
import com.cjwsjy.app.scanning.CaptureActivity;
import com.cjwsjy.app.utils.HttpDownloader;
import com.cjwsjy.app.utils.CallOtherOpeanFile;
import com.cjwsjy.app.utils.StringHelper;
import com.cjwsjy.app.utils.UrlUtil;
import com.cjwsjy.app.wholeally.activity.NetworkStateService;
import com.cjwsjy.app.wholeally.activity.QyVideoControlActivity;
import com.cjwsjy.app.wholeally.activity.QyVideoControlActivity2;
import com.wholeally.qysdk.QYSDK;
import com.wholeally.qysdk.QYSession;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * webView页面，用于首页的mobo
 */
public class WebViewHome extends BaseActivity {

	private WebView mWebView;

	private SharedPreferences sp;

	private int m_ShiLian = 0;
	private int retState;
	private int huadong = 0;
	private int m_isClose = 0;

	private String webUrl;
	private String m_UrlPrevious;
	private String titleName;
	private String m_loginname;
	private String m_jobnumber;
	private String m_ChannelName = "";
	private String m_IP_in = "";
	private String m_Port_in = "";
	private String m_IP_out = "";
	private String m_Port_out = "";
	private String m_Auth = "";
	private String m_callnumber;
	private String SDPATH;

	private Message msg;

	private ProgressDialog mDialog;
	private ProgressDialog mProgressDialog;

	private TextView m_tv_navtitle;
	public static QYSession session;
	private ProgressDialog m_progressDialog;

	private boolean isRedirect = false;// 是否有重定向的页面

	//记录手指按下时的坐标。
	private float xDown;
	private float yDown;
	private float xUp;
	private float yUp;

	private String fileUrl = null;
	private String attachmentName = null;

	public static QYSession session1;
	public static QYSession session2;


	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//requestWindowFeature(Window.FEATURE_PROGRESS);// 让进度条显示在标题栏上
		setContentView(R.layout.webview);

		m_ShiLian = 0;
		m_callnumber = "";
		String strs = "";
		sp = SmApplication.sp;
		m_loginname =sp.getString("USERDATA.LOGIN.NAME", "");
		m_jobnumber = sp.getString("USERDATA.USER.JOBNUMBER", "");
		m_ChannelName = sp.getString("ChannelName", "");
		m_IP_in = sp.getString("IP_in", "");
		m_Port_in = sp.getString("Port_in", "");
		m_IP_out = sp.getString("IP_out", "");
		m_Port_out = sp.getString("Port_out", "");
		m_Auth = sp.getString("Auth", "");

		SDPATH =sp.getString("SD_Path", "");

		if(SDPATH.length()==0) SDPATH = Environment.getExternalStorageDirectory() + "/Download/";

		//initBtn();
		webUrl = getIntent().getExtras().get("webUrl").toString();
		
		//是否滑动
		//strs = getIntent().getExtras().get("huadong").toString();
		strs = getIntent().getStringExtra("huadong");
		boolean bret = StringHelper.isEmpty(strs);
		if( bret==false ) 
		{
			huadong = Integer.parseInt(strs);
		}

		//是否关闭
		strs = getIntent().getStringExtra("isClose");
		bret = StringHelper.isEmpty(strs);
		if( bret==true )
		{
			m_isClose = 0;
		}
		else m_isClose = Integer.parseInt(strs);

		android.util.Log.d("cjwsjy", "------huadong=2"+huadong+"-------");
		
		//title
		m_tv_navtitle = (TextView)this.findViewById(R.id.tv_navtitle);
		if(getIntent().getExtras().get("titleName")!=null)
		{
			titleName = getIntent().getExtras().get("titleName").toString();
		}
		m_tv_navtitle.setText(titleName);
		
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

		//初始化视频
		Intent i = new Intent(WebViewHome.this, NetworkStateService.class);
		startService(i);
		//初始化SDK
		//QYSDK.InitSDK(4);

		//session1 = QYSDK.CreateSession(getApplicationContext());
		//session2 = QYSDK.CreateSession(getApplicationContext());

		getFilePath(WebViewHome.this,"Download");

	}

	public static String getFilePath(Context context, String dir)
	{
		String sdpath;
		String directoryPath="";

		sdpath = Environment.getExternalStorageState();
		if (sdpath.equals(Environment.MEDIA_MOUNTED) )
		{//判断外部存储是否可用
			directoryPath =context.getExternalFilesDir(dir).getAbsolutePath();
		}
		else
		{//没外部存储就使用内部存储
			directoryPath=context.getFilesDir()+File.separator+dir;
		}
		File file = new File(directoryPath);
		if(!file.exists()){//判断文件目录是否存在
			file.mkdirs();
		}
		return directoryPath;
	}

	private void initWebView() 
	{
		WebView webView = new WebView(WebViewHome.this);
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
				super.onReceivedTitle(view, title);
				//android.util.Log.d("cjwsjy", "------title="+title+"-------");
				m_tv_navtitle.setText(title);
				titleName = title;
			}
		});

		mWebView.getSettings().setDomStorageEnabled(true);
		mWebView.getSettings().setAppCacheMaxSize(1024*1024*8);
		String appCachePath = getApplicationContext().getCacheDir().getAbsolutePath();
		mWebView.getSettings().setAppCachePath(appCachePath);
		mWebView.getSettings().setAllowFileAccess(true);
		mWebView.getSettings().setAppCacheEnabled(true);


		mWebView.requestFocusFromTouch();

		webSettings.setJavaScriptEnabled(true);// 设置支持脚本
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		webSettings.setAppCacheEnabled(true);

		webSettings.setTextZoom(100);

		//扩大比例的缩放
		//webSettings.setUseWideViewPort(true);
		//自适应屏幕
		webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
		webSettings.setLoadWithOverviewMode(true);

		//mWebView.getSettings().setBuiltInZoomControls(true);// 设置支持缩放
		//mWebView.getSettings().setDefaultZoom(ZoomDensity.FAR);// 屏幕自适应网页(三星s4 4.2.2系统页面缩小了)
		mWebView.requestFocus();
		webSettings.setDefaultTextEncodingName("UTF-8");

		//与页面JS交互
		mWebView.addJavascriptInterface( new JavaScriptInterface(this), "AndroidFun" );
		
		//android.util.Log.d("cjwsjy", "------webUrl="+webUrl+"-------");
		
		mWebView.loadUrl(webUrl);
		
		mWebView.setWebViewClient(new WebViewClient() 
		{
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) 
			{
				android.util.Log.d("cjwsjy", "------Loading="+url+"-------");
				
				int result;
				String strurl;
				String suffix;

				//网上报销，进入bill-info页面时记录进来前的url
//				if( url.contains("/webApprove/bill-info") )
//				{
//					m_UrlPrevious=url;
//				}

				//关闭
//				result = url.indexOf("/WageSearchDsp/mvc/logout/");
//				if(result!=-1)
//				{
//					mWebView.loadUrl("about:blank");
//					WebViewHome.this.finish();
//					return true;
//				}

				//打电话
				result = url.indexOf("tel:");
				if(result!=-1)
				{
					m_callnumber = url;
					int ret = requestPermissions(Manifest.permission.CALL_PHONE,103);
					if(ret!=1) return true;

					Intent intent = new Intent();
					intent.setAction(Intent.ACTION_CALL);
					intent.setData(Uri.parse(url));
					startActivity(intent);
					return true;
				}

				//会议扫码
				result = url.indexOf("saoma:");
				if(result!=-1)
				{
					int ret = requestPermissions(Manifest.permission.CAMERA,104);
					if(ret!=1) return true;

					Intent intent = new Intent(WebViewHome.this, CaptureActivity.class);
					startActivity(intent);
					return true;
				}

				//会议直播
				//result = url.indexOf("zhibo:");
				result = url.indexOf("/jsp/zhibo");
				if(result!=-1)
				{
					int ret = requestPermissions(Manifest.permission.READ_PHONE_STATE, 105);
					if(ret!=1) return true;

					StartShiLian();

					return true;
				}
				
				//附件下载
				result = url.indexOf("fuckcy");
				if(result!=-1)
				{
					android.util.Log.d("cjwsjy", "------url="+url+"-------downurl");
					Toast.makeText(WebViewHome.this, "开始下载附件...",Toast.LENGTH_SHORT).show();
					
					//截断关键字
					strurl = url.substring(7, url.length());

					//得到附件文件后缀名
					result = strurl.lastIndexOf(".");
					suffix = strurl.substring(result+1, strurl.length());
					//下载附件
					downurls(strurl,suffix);

					view.stopLoading();
					return true;
				}

				android.util.Log.d("cjwsjy", "------url="+url+"-------downurl");

				//附件下载
				//判断url末尾是否有doc等附件
				result = parseurl(url);
				
				if( result==1 )
				{
					Toast.makeText(WebViewHome.this, "开始下载附件...",Toast.LENGTH_SHORT).show();
					result = url.lastIndexOf(".");
					suffix = url.substring(result+1, url.length());
					//下载附件
					downurls(url,suffix);
					
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
				android.util.Log.d("cjwsjy", "--------Started="+url+"-------");

				//网上报销，进入bill-info页面时不记录进来前的url
				boolean bret1 = url.contains("/webApprove/bill-info");
				boolean bret2 = url.contains("/webApprove/bill-list");
				if( bret1==true || bret2==true )
				{
					;
				}
				else m_UrlPrevious = url;

				showProgressDialog("正在加载 ，请等待...", true);
			}
			
			@Override
			public void onPageFinished(WebView view, String url) 
			{
				super.onPageFinished(view, url);
				closeProgressDialog();
				mWebView.getSettings().setBlockNetworkImage(false);

				int result = 0;
				//关闭
				result = url.indexOf("/WageSearchDsp/mvc/logout/");
				if(result!=-1)
				{
					mWebView.loadUrl("about:blank");
					WebViewHome.this.finish();
				}
			}

		});
	}

	private void showProgressDialog(String msg, boolean cancelable) 
	{
		if (mDialog == null && mWebView != null) {
			mDialog = new ProgressDialog(WebViewHome.this);
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

	/**
	 * 
	 * @author chenyu 判断url地址中末尾是否有doc等附件文件
	 * @time 2015年11月11日
	 * @param url
	 * @return 返回1 有附件，返回0 没有附件
	 */
	private int parseurl(String url) 
    {
    	int length = 0;
    	boolean bresult;
    	String strurl = "";
    	String strcut = "";
    	
    	strurl = url;
    	length = strurl.length();
    	
    	//取最后3位
    	strcut = strurl.substring( length-4, length );
    	
    	bresult = strcut.equals(".doc");
    	if( bresult==true ) return 1;
    	
    	bresult = strcut.equals(".ppt");
    	if( bresult==true ) return 1;
    	
    	bresult = strcut.equals(".xls");
    	if( bresult==true ) return 1;
    	
    	bresult = strcut.equals(".pdf");
    	if( bresult==true ) return 1;
    	
    	bresult = strcut.equals(".txt");
    	if( bresult==true ) return 1;
    	
    	//取最后4位
    	strcut = strurl.substring( length-5, length );
    	bresult = strcut.equals(".docx");
    	if( bresult==true ) return 1;
    	
    	bresult = strcut.equals(".pptx");
    	if( bresult==true ) return 1;
    	
    	bresult = strcut.equals(".xlsx");
    	if( bresult==true ) return 1;
    	
    	//没有附件
    	return 0;
    }
	
	//下载附件
	private int downurls(String url, String suffix)
	{
        String strurl = "";
        
        //编码转换
        try
		{
        	strurl = java.net.URLDecoder.decode(url,"UTF-8");
		}
		catch (UnsupportedEncodingException e)
		{
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}

		if( Build.VERSION.SDK_INT>=23 )
		{
			int Permission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
			if( Permission!=PackageManager.PERMISSION_GRANTED )
			{
				ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
				return 1013;
			}
		}

        DownAttachment(strurl, suffix);
          
        return 1;
	}

	Handler handler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			String strmsg = "";
			Intent intent;

			switch (msg.what)
			{
				case 1:
					// 下载成功
					//Toast.makeText(WebViewHome.this, "下载成功！", Toast.LENGTH_SHORT).show();

                    android.util.Log.d("cjwsjy", "------handleMessage-------");

                    closeProgressDialog();

                    //String sdPath = Environment.getExternalStorageDirectory() + "/Download" + "/com.cjwsjy.app/attachment/";
                    String sdPath = SDPATH+"/com.cjwsjy.app/attachment/";
                    File file2 = new File(sdPath + attachmentName);

                    CallOtherOpeanFile openfile = new CallOtherOpeanFile();
                    openfile.openFile(WebViewHome.this, file2);
					break;
				case 2:
					// 下载失败
                    closeProgressDialog();
					Toast.makeText(WebViewHome.this, "下载失败！", Toast.LENGTH_SHORT).show();
					break;
				case 103:  //成功
					//内网和外网只运行一个
					if(m_ShiLian==0)
					{
						m_ShiLian++;
						intent = new Intent(WebViewHome.this, QyVideoControlActivity2.class);
						intent.putExtra("channelName", m_ChannelName);
						intent.putExtra("session", "3");
						startActivity(intent);
					}
					break;
				case 104:  //失败
					strmsg = "登录失败:"+msg.obj.toString()+";或者ViewerLogin第一个或第二个参数错误";
					//Toast.makeText(WebViewCanteen.this, strmsg, Toast.LENGTH_SHORT).show();
					break;
				case 105:  //失败
					//Toast.makeText(WebViewCanteen.this, "服务器连接失败:"+msg.obj.toString(), Toast.LENGTH_SHORT).show();
					break;
				case 106:  //成功
					//内网和外网只运行一个
					if(m_ShiLian==0)
					{
						m_ShiLian++;
						intent = new Intent(WebViewHome.this, QyVideoControlActivity2.class);
						intent.putExtra("channelName",m_ChannelName);
						intent.putExtra("session","4");
						startActivity(intent);
					}
					break;
				case 107:  //失败
					strmsg = "登录失败:"+msg.obj.toString()+";或者ViewerLogin第一个或第二个参数错误";
					//Toast.makeText(WebViewCanteen.this, strmsg, Toast.LENGTH_SHORT).show();
					break;
				case 108:  //失败
					//Toast.makeText(WebViewCanteen.this, "服务器连接失败:"+msg.obj.toString(), Toast.LENGTH_SHORT).show();
					break;
				default:
					if( mDialog!=null ) { mDialog.cancel(); }

					Toast.makeText(WebViewHome.this.getApplicationContext(), "登录失败，失败类型1037",Toast.LENGTH_SHORT).show();
					break;
			}
		}
	};

	//下载附件
	private int DownAttachment( String url, String suffix )
	{
		String filepath = "";

        //showProgressDialog("正在下载 ，请等待...", true);

		fileUrl = url;
		attachmentName = getMD5Str(fileUrl)+"."+suffix;
		//filepath = Environment.getExternalStorageDirectory() +"/Download"+ "/com.cjwsjy.app/attachment/"+attachmentName;
		filepath = SDPATH+"/com.cjwsjy.app/attachment/"+attachmentName;
		File file = new File(filepath);
		if(!file.exists())
		{
            showProgressDialog("正在下载 ，请等待...", true);

			//开线程下载
			new Thread()
			{
				public void run()
				{
					//下载文件
					HttpDownloader httpDownLoader=new HttpDownloader();
					int result = httpDownLoader.downfile(fileUrl, "attachment/", attachmentName);

					Message msg = Message.obtain();
					if (result==0)
					{
						// 下载成功,安装....
						msg.obj = result;
						msg.what = 1;
					}
					else
					{
						// 提示用户下载失败.
						msg.what = 2;
					}
					handler.sendMessage(msg);
					//mProgressDialog.dismiss();
				};
			}.start();
	     }
	     else //下载完成打开附件
        {
            //String sdPath = Environment.getExternalStorageDirectory() + "/Download" + "/com.cjwsjy.app/attachment/";
			String sdPath = SDPATH+"/com.cjwsjy.app/attachment/"+attachmentName;

            File file2 = new File(sdPath + attachmentName);

            CallOtherOpeanFile openfile = new CallOtherOpeanFile();
            openfile.openFile(WebViewHome.this, file2);
        }
		return 1;
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
        	//Uri uri = Uri.parse("tel:"+phoneNum);
            //Intent intent_tel = new Intent(Intent.ACTION_CALL, uri);
            //startActivity(intent_tel);
        }
    }
	
	@Override
	protected void onStart()
	{
		// TODO 自动生成的方法存根
		super.onStart();
	}
	
	@Override
	protected void onResume() 
	{
		super.onResume();
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		
		//清缓存
		mWebView.clearCache(true);
		mWebView.clearHistory();
		mWebView.clearFormData();

		if(null!=session1){
			session1.Release();
			session1=null;
		}

		if(null!=session2){
			session2.Release();
			session2=null;
		}
	}
	
	@Override
	public void onBackPressed()
	{
		String url = "";
		String weburl = "";
		boolean bresult;

		//是否要直接关闭
		if( m_isClose==1 )
		{
			mWebView.loadUrl("about:blank");
			this.finish();
			return;
		}

		android.util.Log.i("cjwsjy", "------titleName="+titleName+"-------WebView");

		if( titleName.equals("出差审批登记") )
		{
			mWebView.loadUrl("about:blank");
			this.finish();
			return;
		}

		if( titleName.equals("领导动态") )
		{
			mWebView.loadUrl("about:blank");
			this.finish();
			return;
		}

		if( titleName.equals("员工动态") )
		{
			mWebView.loadUrl("about:blank");
			this.finish();
			return;
		}

		if( titleName.equals("资产设备") )
		{
			mWebView.loadUrl("about:blank");
			this.finish();
			return;
		}
		
		//会议管理
		weburl = mWebView.getUrl();
		if( weburl.contains("/reserve_HY/") || weburl.contains("/getMeeting/") || weburl.contains("/getMyList_HY/") )
		{
			url = UrlUtil.HOST+"/CEGWAPServer/HYManage/getIndex_HY/"+m_loginname;
			mWebView.loadUrl(url);
			//this.finish();
			return;
		}
		
		if( weburl.contains("/getIndex_HY/") )
		{
			mWebView.loadUrl("about:blank");
			this.finish();
			return;
		}
		
		// 会议管理  会议详细
//		if( weburl.contains("/getMeetingDetail/") )
//		{
//			url = UrlUtil.HOST+"/CEGWAPServer/HYManage/reserve_HY/"+m_loginname;
//			mWebView.loadUrl(url);
//			return;
//		}

		//设校审待办
		if( weburl.contains("/AuditForm/DB/") )
		{
			mWebView.loadUrl("about:blank");
			this.finish();
			return;
		}

		//设校审已办
		if( weburl.contains("/AuditForm/YB/") )
		{
			mWebView.loadUrl("about:blank");
			this.finish();
			return;
		}

		//投标备案待办
		if( weburl.contains("/BidRecordApply/DB/") )
		{
			mWebView.loadUrl("about:blank");
			this.finish();
			return;
		}

		//投标备案已办
		if( weburl.contains("/BidRecordApply/YB/") )
		{
			mWebView.loadUrl("about:blank");
			this.finish();
			return;
		}

		//互提资料待办
		if( weburl.contains("/CollaborationData/DB/") )
		{
			mWebView.loadUrl("about:blank");
			this.finish();
			return;
		}

		//互提资料已办
		if( weburl.contains("/CollaborationData/YB/") )
		{
			mWebView.loadUrl("about:blank");
			this.finish();
			return;
		}

		//设计资料验证待办
		if( weburl.contains("/DesignDataVerify/DB/") )
		{
			mWebView.loadUrl("about:blank");
			this.finish();
			return;
		}

		//设计资料验证已办
		if( weburl.contains("/DesignDataVerify/YB/") )
		{
			mWebView.loadUrl("about:blank");
			this.finish();
			return;
		}

		//出院成果待办
		if( weburl.contains("/ProjectOutReview/DB/") )
		{
			mWebView.loadUrl("about:blank");
			this.finish();
			return;
		}

		//出院成果已办
		if( weburl.contains("/ProjectOutReview/YB/") )
		{
			mWebView.loadUrl("about:blank");
			this.finish();
			return;
		}

		//图纸会签待办
		if( weburl.contains("/SignData/DB/") )
		{
			mWebView.loadUrl("about:blank");
			this.finish();
			return;
		}

		//图纸会签已办
		if( weburl.contains("/SignData/YB/") )
		{
			mWebView.loadUrl("about:blank");
			this.finish();
			return;
		}

		//子项目部组建待办
		if( weburl.contains("/SonDepartBuild/DB/") )
		{
			mWebView.loadUrl("about:blank");
			this.finish();
			return;
		}

		//子项目部组建已办
		if( weburl.contains("/SonDepartBuild/YB/") )
		{
			mWebView.loadUrl("about:blank");
			this.finish();
			return;
		}

        //我的发票抬头
        if( weburl.contains("/InvoiceController/getAll/") )
        {
            mWebView.loadUrl("about:blank");
            this.finish();
            return;
        }

		//外委(科研)待办
		if( weburl.contains("/outContractAuditKY") )
		{
			mWebView.loadUrl("about:blank");
			this.finish();
			return;
		}
		
		//外委(生产)待办
		if( weburl.contains("/outContractAuditSC") )
		{
			mWebView.loadUrl("about:blank");
			this.finish();
			return;
		}
		
		//付款待办
		if( weburl.contains("/ContractPaySH") )
		{
			mWebView.loadUrl("about:blank");
			this.finish();
			return;
		}
		
		//发文待办
		if (weburl.contains("getMyTaskList_FW"))
		{
			mWebView.loadUrl("about:blank");
			this.finish();
			return;
		}
		
		//收文待办
		if (weburl.contains("getMyTaskList_SW"))
		{
			mWebView.loadUrl("about:blank");
			this.finish();
			return;
		}
		
		//一般文件待办
		if (weburl.contains("getMyAuditList_YBWJ"))
		{
			mWebView.loadUrl("about:blank");
			this.finish();
			return;
		}
		
		//印章待办
		if (weburl.contains("getMyTaskList_YZ"))
		{
			mWebView.loadUrl("about:blank");
			this.finish();
			return;
		}

		//资质借用待办
		if (weburl.contains("qualification_DB"))
		{
			mWebView.loadUrl("about:blank");
			this.finish();
			return;
		}

		//收入合同待办
		if (weburl.contains("ProjectRiskEvaluation_DB"))
		{
			mWebView.loadUrl("about:blank");
			this.finish();
			return;
		}
		
		//会议申报待办
		if (weburl.contains("MeetingFee_DB"))
		{
			mWebView.loadUrl("about:blank");
			this.finish();
			return;
		}

		//劳务费待办
		if (weburl.contains("SkillAuditFee_DB"))
		{
			mWebView.loadUrl("about:blank");
			this.finish();
			return;
		}

		//保函保证金待办
		if (weburl.contains("BackLetter_DB"))
		{
			mWebView.loadUrl("about:blank");
			this.finish();
			return;
		}

		//项目登记待办
		if (weburl.contains("ProjectRegister_DB"))
		{
			mWebView.loadUrl("about:blank");
			this.finish();
			return;
		}

		//发票申请待办
		if (weburl.contains("InvoiceInfo_DB"))
		{
			mWebView.loadUrl("about:blank");
			this.finish();
			return;
		}

		//业务接待待办
		if (weburl.contains("/JD/getUserDBList/"))
		{
			mWebView.loadUrl("about:blank");
			this.finish();
			return;
		}

		//meeting/meetingIndex
		//我的会议
		if (weburl.contains("/meeting/meetingIndex/"))
		{
			mWebView.loadUrl("about:blank");
			this.finish();
			return;
		}

		//跳转到网上报销的首页
		//这个必须放在下面4个的前面
		if (weburl.contains("/webApprove/bill-info"))
		{
			url = m_UrlPrevious;
			//url = "http://moa.cispdr.com:8028/webApprove/?usercode="+m_jobnumber+"&billtype=2632";
			mWebView.loadUrl(url);
			//mWebView.loadUrl(m_UrlPrevious);
			return;
		}

		//跳转到网上报销的首页
		if (weburl.contains("/webApprove/bill-list"))
		{
			url = m_UrlPrevious;
			//url = "http://moa.cispdr.com:8028/webApprove/?usercode="+m_jobnumber+"&billtype=2631";
			//mWebView.loadUrl(url);
			mWebView.loadUrl(m_UrlPrevious);
			return;
		}

		//网上报销，一共4个
		if (weburl.contains("/webApprove/?usercode"))
		{
			mWebView.loadUrl("about:blank");
			this.finish();
			return;
		}

		if (weburl.contains("/webApprove/index"))
		{
			mWebView.loadUrl("about:blank");
			this.finish();
			return;
		}

		if (weburl.contains("/webApprove/bill"))
		{
			mWebView.loadUrl("about:blank");
			this.finish();
			return;
		}

		if (weburl.contains("/webApprove/search"))
		{
			mWebView.loadUrl("about:blank");
			this.finish();
			return;
		}

		if (mWebView.canGoBack())
		{
			if (weburl.toLowerCase().contains("main"))
			{ // 列表页直接退出
				// removeCookies();
				mWebView.loadUrl("about:blank");
				// mWebView.clearView();
				this.finish();
			}
			else if (weburl.toLowerCase().contains("fawen") && isRedirect)
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
			// removeCookies();
			mWebView.loadUrl("about:blank");
			// mWebView.clearView();
			this.finish();
		}

	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev)
	{
		int action = ev.getAction();
		int xmove = 0;
		int ymove = 0;
		int value = 0;
		switch(action)
		{
			case MotionEvent.ACTION_DOWN:
				xDown = ev.getRawX();
				yDown = ev.getRawY();
				break;
			case MotionEvent.ACTION_UP:
				if(huadong==0) break;

				xUp = ev.getRawX();
				yUp = ev.getRawY();

				xmove = (int)( xDown- xUp );
				ymove = (int)( yDown- yUp);
				if(xmove>0) break;

				xmove = Math.abs(xmove);
				ymove = Math.abs(ymove);
				if(xmove<150) break;  //最小移动距离

				value = xmove-ymove;
				if( value>0 )
				{
					onBackPressed();
					overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
					return false;
				}
				break;
			default:
				break;
			//onUserInteraction();
		}

		if( getWindow().superDispatchTouchEvent(ev) )
		{ //在这里交给View层处理
			return true;
		}
		return onTouchEvent(ev); // 如果View层没有处理，则在这里处理

		//return super.dispatchTouchEvent(ev);
	}

	private int requestPermissions(String strpermission, int code )
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
			case 103:  //电话
				if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
				{
					// 同意
					Intent intent = new Intent();
					intent.setAction(Intent.ACTION_CALL);
					intent.setData(Uri.parse(m_callnumber));
					startActivity(intent);
				}
				else
				{
					// 拒绝
					Toast.makeText(WebViewHome.this, "没有权限！", Toast.LENGTH_SHORT).show();
				}
				break;
			case 104:  //会议扫码
				if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
				{
					// 同意
					Intent intent = new Intent(WebViewHome.this, CaptureActivity.class);
					startActivity(intent);
				}
				else
				{
					// 拒绝
					Toast.makeText(WebViewHome.this, "没有权限！", Toast.LENGTH_SHORT).show();
				}
				break;
			case 105:  //会议直播
				if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
				{
					// 同意
					StartShiLian();
				}
				else
				{
					// 拒绝
					Toast.makeText(WebViewHome.this, "没有权限！", Toast.LENGTH_SHORT).show();
				}
				break;
			case 123:  //SD卡权限
				if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
				{
					// 同意
					//StartShiLian();
				}
				else
				{
					// 拒绝
					Toast.makeText(WebViewHome.this, "没有权限！", Toast.LENGTH_SHORT).show();
				}
				break;
			default:
				super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}

//	private void LoginAttempt()
//	{
//
//		int nport = 0;
//		String IPaddress;
//		String ports;
//		String appid;
//		String auth;
//
//		//初始化SDK
//		QYSDK.InitSDK(5);
//		//创建session会话
//		session = QYSDK.CreateSession( getApplicationContext() );
//
//		IPaddress = m_ChannelName;
//		ports = m_Port;
//
//		nport = Integer.valueOf(ports);
//		appid = "wholeally";
//		auth = m_Auth;
//
//		m_progressDialog = ProgressDialog.show(WebViewHome.this, "加载中...", "正  在  获  取  数  据 ...");
//		retState = session.SetServer(IPaddress,nport);//连接服务器大于或等于0为成功  否则为失败
//		if (retState >= 0)
//		{
//			//session会话登录 第二个参数为回调函数 ret大于或等于0为成功  否则为失败
//			// 测试用： wholeally    czFYScb5pAu+Ze7rXhGh/zURoROEIJ5JZnqf1q9hjlNQpfpixq+tzaIuQmoa2qa0Vgd/r1TPf+IQy3AED5xjo9iSSMjZjGIKZv8EsCI3VJc=
//			session.ViewerLogin(appid,auth, new QYSession.OnViewerLogin()
//			{
//				@Override
//				public void on(int ret)
//				{
//					System.out.println("===ret==:"+ret);
//					if (ret >= 0)
//					{
//						m_progressDialog.cancel();
//						android.util.Log.d("cjwsjy", "------登录成功-------7");
//
//						Intent intent = new Intent(WebViewHome.this,QyVideoControlActivity2.class);
//						intent.putExtra("channelName", m_ChannelName );
//						startActivity(intent);
//					}
//					else
//					{
//						m_progressDialog.cancel();
//						//showToast("登录失败:"+String.valueOf(ret)+";或者ViewerLogin第一个或第二个参数错误");
//						android.util.Log.d("cjwsjy", "------登录失败-------7");
//						new AlertDialog.Builder(WebViewHome.this)
//								.setTitle("提示")
//								.setMessage("无法连接服务器")
//								.setPositiveButton("确定", null)
//								.show();
//					}
//				}
//			});
//		}
//		else
//		{
//			m_progressDialog.cancel();
//			//showToast("服务器连接失败:"+String.valueOf(retState));
//		}
//	}

	private void StartShiLian()
	{
		m_ShiLian = 0;
		m_progressDialog = ProgressDialog.show(WebViewHome.this, "加载中...", "正  在  获  取  数  据 ...");

		//内网
		Thread loginThread1 = new Thread(new ThreadConnect1());
		loginThread1.start();

		//外网
		Thread loginThread2 = new Thread(new ThreadConnect2());
		loginThread2.start();
	}

	// 内网
	class ThreadConnect1 implements Runnable
	{
		@Override
		public void run()
		{
			int retState;
			int nport = 0;
			String IPaddress;
			String ports;
			String appid;
			String auth;

			IPaddress = m_IP_in;
			ports = m_Port_in;
			appid = "wholeally";
			auth = m_Auth;

			nport = Integer.parseInt(ports);

			retState = session1.SetServer(IPaddress, nport);//连接服务器大于或等于0为成功  否则为失败
			if (retState >= 0) {
				//session会话登录 第二个参数为回调函数 ret大于或等于0为成功  否则为失败
				session1.ViewerLogin(appid, auth, new QYSession.OnViewerLogin() {
					@Override
					public void on(int ret)
					{
						if (ret >= 0)
						{
							//登录成功
							msg = handler.obtainMessage();
							msg.what = 103;
							handler.sendMessage(msg);
						}
						else
						{
							//登录失败
							msg = handler.obtainMessage();
							msg.what = 104;
							msg.obj = String.valueOf(ret);
							handler.sendMessage(msg);
						}
					}
				});

			}
			else
			{
				Toast.makeText(WebViewHome.this, "服务器连接失败:" + String.valueOf(retState), Toast.LENGTH_SHORT).show();

				msg = handler.obtainMessage();
				msg.what = 105;
				msg.obj = String.valueOf(retState);
				handler.sendMessage(msg);
			}
		}
	}

	// 外网
	class ThreadConnect2 implements Runnable
	{
		@Override
		public void run()
		{
			int retState;
			int nport = 0;
			String IPaddress;
			String ports;
			String appid;
			String auth;

			IPaddress = m_IP_out;
			ports = m_Port_out;
			appid = "wholeally";
			auth = m_Auth;

			nport = Integer.parseInt(ports);

			retState = session2.SetServer(IPaddress, nport);//连接服务器大于或等于0为成功  否则为失败
			if (retState >= 0) {
				//session会话登录 第二个参数为回调函数 ret大于或等于0为成功  否则为失败
				session2.ViewerLogin(appid, auth, new QYSession.OnViewerLogin() {
					@Override
					public void on(int ret)
					{
						if (ret >= 0)
						{
							//登录成功
							msg = handler.obtainMessage();
							msg.what = 106;
							handler.sendMessage(msg);
						}
						else
						{
							//登录失败
							msg = handler.obtainMessage();
							msg.what = 107;
							msg.obj = String.valueOf(ret);
							handler.sendMessage(msg);
						}
					}
				});
			}
			else
			{
				Toast.makeText(WebViewHome.this, "服务器连接失败:" + String.valueOf(retState), Toast.LENGTH_SHORT).show();

				msg = handler.obtainMessage();
				msg.what = 108;
				msg.obj = String.valueOf(retState);
				handler.sendMessage(msg);
			}
		}
	}

}
