package com.cjwsjy.app.webview;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
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

import com.cjwsjy.app.R;
import com.cjwsjy.app.SmApplication;
import com.cjwsjy.app.base.BaseActivity;
import com.cjwsjy.app.utils.CallOtherOpeanFile;
import com.cjwsjy.app.utils.HttpDownloader;
import com.cjwsjy.app.utils.StringHelper;
import com.cjwsjy.app.utils.UrlUtil;
import com.do1.cjmobileoa.db.DBManager;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * webView页面，用于档案管理
 * 加入到了沉浸式状态栏
 */
public class WebViewHome3 extends BaseActivity {

	private WebView mWebView;
	private String webUrl;
	private String titleName;
	private SharedPreferences sp;

	private int m_flagDown = 0;
	private int huadong = 0;
	private int m_isClose = 0;
	private String m_loginname;
	private ProgressDialog mDialog;
	private ProgressDialog mProgressDialog;

	private DBManager db;

	private boolean isRedirect = false;// 是否有重定向的页面

	//记录手指按下时的坐标。
	private float xDown;
	private float yDown;
	private float xUp;
	private float yUp;

	private String fileUrl = null;
	private String attachmentName = null;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//requestWindowFeature(Window.FEATURE_PROGRESS);// 让进度条显示在标题栏上
		setContentView(R.layout.webview);

		db = SmApplication.dbManager;

		String strs = "";
		 sp = SmApplication.sp;
		 m_loginname =sp.getString("USERDATA.LOGIN.NAME", "");

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

		//android.util.Log.d("cjwsjy", "------huadong="+huadong+"-------");

		//title
		TextView tv_navtitle = (TextView)this.findViewById(R.id.tv_navtitle);
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
		WebView webView = new WebView(WebViewHome3.this);
		mWebView = (WebView) findViewById(R.id.webView_home);
		WebSettings webSettings = mWebView.getSettings();

		webSettings.setUseWideViewPort(true);

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
				//android.util.Log.d("cjwsjy", "------title="+title+"-------");
			}
		});

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

				int result = 0;
				int result2 = 0;
				String strurl = "";
				String strurl2 = "";
				String suffix = "";
				String strname = "";
				String strname2 = "";

				//打电话
				result = url.indexOf("tel:");
				if(result!=-1)
				{
					Intent intent = new Intent();
					intent.setAction(Intent.ACTION_CALL);
					intent.setData(Uri.parse(url));
					startActivity(intent);
					return true;
				}

				//档案 在线查看 下载文件后打开文件
				result = url.indexOf("online:");
				if(result!=-1)
				{
					//截断关键字
					strurl = url.substring(7, url.length());

					//拆分url和文件名
					result = strurl.lastIndexOf(";");
					strurl2 = strurl.substring(0, result);
					strname = strurl.substring(result+1, strurl.length());

					try
					{
						strname2 = java.net.URLDecoder.decode(strname,"UTF-8");
					}
					catch (UnsupportedEncodingException e)
					{
						// TODO 自动生成的 catch 块
						e.printStackTrace();
					}

					//下载附件
					downDangan(strurl2,strname2,3);

					view.stopLoading();
					return true;
				}

				//档案 下载 下载文件后不打开文件
				result = url.indexOf("download:");
				if(result!=-1)
				{
					//截断关键字
					strurl = url.substring(9, url.length());

					//拆分url和文件名
					result = strurl.lastIndexOf(";");
					strurl2 = strurl.substring(0, result);
					strname = strurl.substring(result+1, strurl.length());

					try
					{
						strname2 = java.net.URLDecoder.decode(strname,"UTF-8");
					}
					catch (UnsupportedEncodingException e)
					{
						// TODO 自动生成的 catch 块
						e.printStackTrace();
					}

					downDangan(strurl2,strname2,4);

					return true;
				}

				//附件下载
				result = url.indexOf("fuckcy");
				if(result!=-1)
				{
					android.util.Log.d("cjwsjy", "------url="+url+"-------downurl");
					Toast.makeText(WebViewHome3.this, "开始下载附件...",Toast.LENGTH_SHORT).show();

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

				//附件下载
				//判断url末尾是否有doc等附件
				result = parseurl(url);

				if( result==1 )
				{
					Toast.makeText(WebViewHome3.this, "开始下载附件...",Toast.LENGTH_SHORT).show();
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
				android.util.Log.d("cjwsjy", "------Started="+url+"-------");
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
					WebViewHome3.this.finish();
				}
			}

		});
	}

	private void showProgressDialog(String msg, boolean cancelable)
	{
		if (mDialog == null && mWebView != null) {
			mDialog = new ProgressDialog(WebViewHome3.this);
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

        DownAttachment(strurl, suffix);

        return 1;
	}

	//下载附件 档案
	private int downDangan(String url, String name, int flag)
	{
		int nresult = 0;
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

		//DownAttachment(strurl, suffix);

		String filepath = "";

		m_flagDown = flag;
		fileUrl = url;
		//查找该文件是否已经下载
		attachmentName = name;
		filepath = Environment.getExternalStorageDirectory() +"/Download"+ "/com.cjwsjy.app/DangAn/"+attachmentName;
		File file = new File(filepath);

		nresult = db.findDanganByOldName(attachmentName);

		if(nresult==0)
		//if(!file.exists())
		{
			//没有下载过，下载文件
			showProgressDialog("正在下载 ，请等待...", true);

			//开线程下载
			new Thread()
			{
				public void run()
				{
					//下载文件
					HttpDownloader httpDownLoader=new HttpDownloader();
					int result = httpDownLoader.downfile(fileUrl, "DangAn/", attachmentName);

					Message msg = Message.obtain();
					if (result==0)
					{
						// 下载成功
						if( m_flagDown==3 )  //档案 在线查看
						{
							msg.obj = result;
							msg.what = 3;
						}
						else if( m_flagDown==4 )  //档案 下载
						{
							msg.obj = result;
							msg.what = 4;
						}
					}
					else
					{
						// 提示用户下载失败.
						msg.what = 2;
					}
					handler.sendMessage(msg);
				};
			}.start();
		}
		else //已经下载过，打开附件
		{
			if( m_flagDown==3 )
			{
				//档案 在线查看
				String sdPath = Environment.getExternalStorageDirectory() + "/Download" + "/com.cjwsjy.app/DangAn/"+attachmentName;
				OpenAttachment(sdPath);
			}
			else if( m_flagDown==4 )
			{
				//档案 下载
				Toast.makeText(WebViewHome3.this, "该文件已下载", Toast.LENGTH_SHORT).show();
			}

		}
		return 1;

	}

	Handler handler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			String sdPath;

			closeProgressDialog();
			switch (msg.what)
			{
				case 1:
					// 下载成功
					//Toast.makeText(WebViewHome.this, "下载成功！", Toast.LENGTH_SHORT).show();

                    android.util.Log.d("cjwsjy", "------handleMessage-------");

                    //closeProgressDialog();

                    sdPath = Environment.getExternalStorageDirectory() + "/Download" + "/com.cjwsjy.app/attachment/"+attachmentName;
					OpenAttachment(sdPath);

					break;
				case 2:
					// 下载失败
                    //closeProgressDialog();
					Toast.makeText(WebViewHome3.this, "下载失败！", Toast.LENGTH_SHORT).show();
					break;
				case 3:
					// 档案 在线查看
					//添加到数据库
					db.insertDangan(attachmentName);

					sdPath = Environment.getExternalStorageDirectory() + "/Download" + "/com.cjwsjy.app/DangAn/"+attachmentName;
					OpenAttachment(sdPath);

					break;
				case 4:
					// 档案 下载
					//closeProgressDialog();
					db.insertDangan(attachmentName);
					Toast.makeText(WebViewHome3.this, "下载成功！", Toast.LENGTH_SHORT).show();
					break;
				default:
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
		filepath = Environment.getExternalStorageDirectory() +"/Download"+ "/com.cjwsjy.app/attachment/"+attachmentName;
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
            String sdPath = Environment.getExternalStorageDirectory() + "/Download" + "/com.cjwsjy.app/attachment/"+attachmentName;
			OpenAttachment(sdPath);
        }
		return 1;
	}

	private int OpenAttachment(String filePath)
	{
		File file2 = new File(filePath);

		CallOtherOpeanFile openfile = new CallOtherOpeanFile();
		openfile.openFile(WebViewHome3.this, file2);

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
	}
	
	@Override
	public void onBackPressed()
	{
		String url = "";
		String url2 = "";
		boolean bresult;

		//是否要直接关闭
		if( m_isClose==1 )
		{
			mWebView.loadUrl("about:blank");
			this.finish();
			return;
		}

		if( titleName.equals("出差审批登记") )
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
		url2 = mWebView.getUrl();
		if( url2.contains("/reserve_HY/") || url2.contains("/getMeeting/") || url2.contains("/getMyList_HY/") )
		{
			url = UrlUtil.HOST+"/CEGWAPServer/HYManage/getIndex_HY/"+m_loginname;
			mWebView.loadUrl(url);
			//this.finish();
			return;
		}
		
		if( url2.contains("/getIndex_HY/") )
		{
			mWebView.loadUrl("about:blank");
			this.finish();
			return;
		}
		
		// 会议管理  会议详细
//		if( mWebView.getUrl().contains("/getMeetingDetail/") )
//		{
//			url = UrlUtil.HOST+"/CEGWAPServer/HYManage/reserve_HY/"+m_loginname;
//			mWebView.loadUrl(url);
//			return;
//		}
		
		//外委(科研)待办
		if( url2.contains("/outContractAuditKY") )
		{
			mWebView.loadUrl("about:blank");
			this.finish();
			return;
		}
		
		//外委(生产)待办
		if( url2.contains("/outContractAuditSC") )
		{
			mWebView.loadUrl("about:blank");
			this.finish();
			return;
		}
		
		//付款待办
		if( url2.contains("/ContractPaySH") )
		{
			mWebView.loadUrl("about:blank");
			this.finish();
			return;
		}
		
		//发文待办
		if (url2.contains("getMyTaskList_FW"))
		{
			mWebView.loadUrl("about:blank");
			this.finish();
			return;
		}
		
		//收文待办
		if (url2.contains("getMyTaskList_SW"))
		{
			mWebView.loadUrl("about:blank");
			this.finish();
			return;
		}
		
		//一般文件待办
		if (url2.contains("getMyAuditList_YBWJ"))
		{
			mWebView.loadUrl("about:blank");
			this.finish();
			return;
		}
		
		//印章待办
		if (url2.contains("getMyTaskList_YZ"))
		{
			mWebView.loadUrl("about:blank");
			this.finish();
			return;
		}

		//资质借用待办
		if (url2.contains("qualification_DB"))
		{
			mWebView.loadUrl("about:blank");
			this.finish();
			return;
		}

		//收入合同待办
		if (url2.contains("ProjectRiskEvaluation_DB"))
		{
			mWebView.loadUrl("about:blank");
			this.finish();
			return;
		}
		
		//会议申报待办
		if (url2.contains("MeetingFee_DB"))
		{
			mWebView.loadUrl("about:blank");
			this.finish();
			return;
		}

		//劳务费待办
		if (url2.contains("SkillAuditFee_DB"))
		{
			mWebView.loadUrl("about:blank");
			this.finish();
			return;
		}

		//保函保证金待办
		if (url2.contains("BackLetter_DB"))
		{
			mWebView.loadUrl("about:blank");
			this.finish();
			return;
		}

		//项目登记待办
		if (url2.contains("ProjectRegister_DB"))
		{
			mWebView.loadUrl("about:blank");
			this.finish();
			return;
		}

		//发票申请待办
		if (url2.contains("InvoiceInfo_DB"))
		{
			mWebView.loadUrl("about:blank");
			this.finish();
			return;
		}

		//档案管理-我的待办
		if (url2.contains("/DAManage/getToDoList/"))
		{
			mWebView.loadUrl("about:blank");
			this.finish();
			return;
		}

		//档案管理-借阅申请单
		if (url2.contains("/DAManage/SheetListNormal/"))
		{
			mWebView.loadUrl("about:blank");
			this.finish();
			return;
		}

		//档案管理-下载申请单
		if (url2.contains("/DAManage/DownLoadList/"))
		{
			mWebView.loadUrl("about:blank");
			this.finish();
			return;
		}

		//网上报销，一共4个
		//差旅费报销单，一般报销单，差旅费借款单，一般借款单
		if (url2.contains("/webApprove/?usercode"))
		{
			mWebView.loadUrl("about:blank");
			this.finish();
			return;
		}

		if (mWebView.canGoBack())
		{
			if (mWebView.getUrl().toLowerCase().contains("main"))
			{ // 列表页直接退出
				// removeCookies();
				mWebView.loadUrl("about:blank");
				// mWebView.clearView();
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

}
