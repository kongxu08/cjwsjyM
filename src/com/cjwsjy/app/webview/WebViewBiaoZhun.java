package com.cjwsjy.app.webview;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cjwsjy.app.R;
import com.cjwsjy.app.SmApplication;
import com.cjwsjy.app.base.BaseActivity;
import com.cjwsjy.app.utils.CallOtherOpeanFile;
import com.do1.cjmobileoa.db.DBManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * webView页面，资产扫码用，标题随页面变化
 */
public class WebViewBiaoZhun extends BaseActivity {

	private int m_downmode = 1;

	private String webUrl;
	private String fileUrl = "";
	private String titleName;
	private String attachmentName = "";

	private TextView tv_navtitle;
	private WebView mWebView;

	private DBManager db;
	private ProgressDialog mDialog;

	private RelativeLayout layout;

	private NotificationManager mNotifyManager;
	private NotificationCompat.Builder mBuilder;
	private Notification mnotify;

	private boolean isRedirect = false;// 是否有重定向的页面

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//requestWindowFeature(Window.FEATURE_PROGRESS);// 让进度条显示在标题栏上
		setContentView(R.layout.webview_biaozhun);

		db = SmApplication.dbManager;
		webUrl = getIntent().getExtras().get("webUrl").toString();

		layout = (RelativeLayout)findViewById(R.id.titletop);

		//title
		tv_navtitle = (TextView)this.findViewById(R.id.tv_navtitle);
		if(getIntent().getExtras().get("titleName")!=null)
		{
			titleName = getIntent().getExtras().get("titleName").toString();
		}
		tv_navtitle.setText(titleName);

		//隐藏标题栏
		//layout.setVisibility(View.GONE);

		//后退
		ImageView iv_back = (ImageView) this.findViewById(R.id.iv_back);
		iv_back.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				//onClickButton();
				onBackPressed();
			}
		});

		//搜索
		ImageView iv_serachs = (ImageView) this.findViewById(R.id.iv_add);
		iv_serachs.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				CallJavaScript3("123");
			}
		});

		//通知栏
		//定义一个PendingIntent点击Notification后启动一个Activity
		//Intent it = new Intent(WebViewBQ.this, OtherActivity.class);
		//PendingIntent pit = PendingIntent.getActivity(WebViewBQ.this, 0, it, 0);

		//获取状态通知栏管理
		mNotifyManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

		//实例化通知栏构造器NotificationCompat.Builder：
		mBuilder = new NotificationCompat.Builder(this);

		//对Builder进行相关的设置，比如标题，内容，图标，动作等！
//		mBuilder.setContentTitle("Picture Download")                        //标题
//				.setContentText("Download in progress")      //内容
//				.setSubText("——记住我叫叶良辰")                    //内容下面的一小段文字
//				.setTicker("收到叶良辰发送过来的信息~")             //收到信息后状态栏显示的文字信息
//				.setWhen(System.currentTimeMillis())           //设置通知时间
//				.setSmallIcon(R.drawable.ic_launcher)            //设置小图标
//				//.setLargeIcon(LargeBitmap)                     //设置大图标
//				.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)    //设置默认的三色灯与振动器
//				//.setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.biaobiao))  //设置自定义的提示音
//				//.setContentIntent(pit),                        //设置PendingIntent
//				.setAutoCancel(true);                           //设置点击后取消Notification


		mBuilder.setContentTitle("Picture Download")
				.setContentText("Download in progress")
				.setSmallIcon(R.drawable.ic_launcher);

		//调用Builder的build()方法为notification赋值
		mnotify = mBuilder.build();

		initWebView();
	}

	private void initWebView()
	{
		WebView webView = new WebView(WebViewBiaoZhun.this);
		mWebView = (WebView) findViewById(R.id.webView_BQ);
		WebSettings webSettings = mWebView.getSettings();

		mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);

		webSettings.setDomStorageEnabled(true);

		//设置页面自适应手机屏幕
		webSettings.setUseWideViewPort(true);
		webSettings.setLoadWithOverviewMode(true);

		//第二种方法，设置满屏显示
		//webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

		//不显示webview缩放按钮
		webSettings.setDisplayZoomControls(false);

		//String ua = "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; EmbeddedWB 14.52 from: http://www.bsalsa.com/ EmbeddedWB 14.52; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET4.0C; .NET4.0E; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)";
		//webSettings.setUserAgentString(ua);
		webSettings.setBlockNetworkImage(false); 	//图片下载阻塞

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

		//可以弹出虚拟键盘输入
		//mWebView.requestFocus();
		//mWebView.requestFocusFromTouch();

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
		webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
		webSettings.setAppCacheEnabled(true);
		webSettings.setDomStorageEnabled(true);

		webSettings.setDatabaseEnabled(true);
		String appCachePath = getApplicationContext().getCacheDir().getAbsolutePath();
		webView.getSettings().setAppCachePath(appCachePath);
		webView.getSettings().setAllowFileAccess(true);

		Log.d("cjwsjy", "------appCachePath="+appCachePath+"-------BQ");

		mWebView.loadUrl(webUrl);

		Log.d("cjwsjy", "------loadUrl="+webUrl+"-------BQ");

		mWebView.setWebViewClient(new WebViewClient()
		{
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url)
			{
				Log.d("cjwsjy", "------Loading="+url+"-------BQ");

				int result = 0;
				String strurl;

				//附件下载
				result = url.indexOf("xiazai123");
				if( result!=-1 )
				{
					//截断关键字
					strurl = url.substring(10, url.length());

					m_downmode = 1;
					//下载附件
					DownAttachment(strurl);

					//停止加载
					view.stopLoading();
					return true;
				}

				//预览附件
				result = url.indexOf("yulan123");
				if( result!=-1 )
				{
					//截断关键字
					strurl = url.substring(9, url.length());

					m_downmode = 2;
					//预览附件
					PreviewAttachment(strurl);

					//停止加载
					view.stopLoading();
					return true;
				}

				//删除附件
				result = url.indexOf("shanchu123");
				if( result!=-1 )
				{
					//截断关键字
					strurl = url.substring(11, url.length());
					//删除附件
					DeleteAttachment(strurl);

					//停止加载
					view.stopLoading();
					return true;
				}

				//隐藏标题栏
				//layout.setVisibility(View.GONE);

				/*result = url.indexOf("bz/phone/index");
				if( result!=-1 )
				{
					layout.setVisibility(View.VISIBLE);
				}

				result = url.indexOf("biaozhunsousuo");
				if( result!=-1 )
				{
					layout.setVisibility(View.GONE);
				}

				result = url.indexOf("shoucangjia");
				if( result!=-1 )
				{
					layout.setVisibility(View.GONE);
				}

				result = url.indexOf("lixianyuedu");
				if( result!=-1 )
				{
					layout.setVisibility(View.GONE);
				}*/

				view.loadUrl(url);
				return true;
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon)
			{
				super.onPageStarted(view, url, favicon);
				//Log.d("cjwsjy", "---------PageStarted="+url+"-------BQ");
				showProgressDialog("正在加载 ，请等待...", true);
			}

			@Override
			public void onPageFinished(WebView view, String url)
			{
				super.onPageFinished(view, url);

				//app的标题栏显示页面的标题
				//titleName = view.getTitle();
				//tv_navtitle.setText(titleName);

				closeProgressDialog();
				mWebView.getSettings().setBlockNetworkImage(false);
			}

			@Override
			public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error)
			{
				// TODO Auto-generated method stub
				handler.proceed();
			}
		});
	}

	//加载对话框
	private void showProgressDialog(String msg, boolean cancelable)
	{
		if (mDialog == null && mWebView != null) {
			mDialog = new ProgressDialog(WebViewBiaoZhun.this);
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

	//下载附件
	protected int DownAttachment(String url)
	{
		int result = 0;
		String strurl = "";
		String filepath = "";

		Log.d("cjwsjy", "------下载附件-------");

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

		fileUrl = strurl;

		//得到附件文件名
		result = strurl.lastIndexOf("/");
		attachmentName = strurl.substring(result+1, strurl.length());
		Log.d("cjwsjy", "---------下载附件"+attachmentName+"-------");

		mBuilder.setProgress(100, 0, false);
		mNotifyManager.notify(1, mBuilder.build());

		//提示框
		//showTips();

		startThreadDown();

		return 1;
	}

	//预览附件
	protected int PreviewAttachment(String url)
	{
		int result = 0;
		String strurl = "";
		String filepath = "";

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

		fileUrl = strurl;

		//得到附件文件名
		result = strurl.lastIndexOf("/");
		attachmentName = strurl.substring(result+1, strurl.length());

		//判断文件是否存在
		filepath = Environment.getExternalStorageDirectory() +"/Download/com.cjwsjy.app/attachment3/"+attachmentName;
		File file = new File(filepath);
		if(!file.exists())
		{
			//如果文件不存在，
			//开线程,下载文件
            Toast.makeText(WebViewBiaoZhun.this, "正在缓存文件！", Toast.LENGTH_SHORT).show();
			m_downmode = 2;
			startThreadDown();
		}
		else
		{
			//文件存在，直接打开
			String sdPath = Environment.getExternalStorageDirectory() + "/Download/com.cjwsjy.app/attachment3/";
			File file2 = new File(sdPath + attachmentName);

			CallOtherOpeanFile openfile = new CallOtherOpeanFile();
			openfile.openFile(WebViewBiaoZhun.this, file2);
		}
		return 1;
	}

	//删除附件
	protected int DeleteAttachment(String url)
	{
		int result = 0;
		String strurl = "";
		String filepath = "";

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

		fileUrl = strurl;

		//得到附件文件名
		//result = strurl.lastIndexOf("/");
		//attachmentName = strurl.substring(result+1, strurl.length());
		attachmentName = fileUrl;
		Log.d("cjwsjy", "---------删除附件="+attachmentName+"-------");

		//判断文件是否存在
		filepath = Environment.getExternalStorageDirectory() +"/Download/com.cjwsjy.app/attachment3/"+attachmentName;
		//filepath = Environment.getExternalStorageDirectory() + "/bzAPP/Attach/"+attachmentName;
		File file = new File(filepath);
		if(file.exists())
		{
			//删除文件
			//file.delete();
			try
			{
				file.delete();
			}
			catch (Exception e)
			{
			}
			Toast.makeText(WebViewBiaoZhun.this, "删除文件成功！", Toast.LENGTH_SHORT).show();

			//在数据库中删除
			db.DeleteBiaozhun(attachmentName);

			//调用页面JavaScript函数，把附件文件名给页面
			CallJavaScript2(url);
		}
		else
		{
			Toast.makeText(WebViewBiaoZhun.this, "删除文件失败！文件未找到", Toast.LENGTH_SHORT).show();
		}

		return 1;
	}

	private void startThreadDown()
	{
		new Thread()
		{
			public void run()
			{
				fileUrl=fileUrl.replace(" ", "%20");
				Log.d("cjwsjy", "------name="+fileUrl+"-------downurl");

				//下载文件
				String filepath = Environment.getExternalStorageDirectory() +"/Download/com.cjwsjy.app/attachment3/";
				int result = down_file(fileUrl, filepath, attachmentName);

				Message msg = Message.obtain();
				if (result==0)
				{
					// 下载成功
					//mBuilder.setContentText("Download complete")
							//.setProgress(0,0,false);
					//发送通知
					//mNotifyManager.notify(1, mBuilder.build());

					if( m_downmode==1 )
					{
						//添加到数据库
						db.insertBiaozhun(attachmentName);

						msg.obj = result;
						msg.what = 1;
					}
					else if(m_downmode==2)
					{
						//添加到数据库
						db.insertBiaozhun(attachmentName);

						//下载成功后，打开该文件
						msg.obj = result;
						msg.what = 2;
					}
				}
				else
				{
					// 下载失败.
					if( m_downmode==1 ) msg.what = 10;

					//预览时，下载失败
					if( m_downmode==2 ) msg.what = 11;
				}
				handler.sendMessage(msg);
			};
		}.start();
	}

	Handler handler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
				case 1:
					// 下载成功
					Toast.makeText(WebViewBiaoZhun.this, "下载成功！", Toast.LENGTH_SHORT).show();

					//调用页面JavaScript函数，把附件文件名给页面
					CallJavaScript(fileUrl);

					break;
				case 2:
					//预览
					// 下载成功后打开该文件
					String sdPath = Environment.getExternalStorageDirectory() + "/Download/com.cjwsjy.app/attachment3/";
					File file2 = new File(sdPath + attachmentName);

					CallOtherOpeanFile openfile = new CallOtherOpeanFile();
					openfile.openFile(WebViewBiaoZhun.this, file2);

					break;
				case 10:
					Toast.makeText(WebViewBiaoZhun.this, "下载失败！", Toast.LENGTH_SHORT).show();
					CallJavaScript("failed");
					break;
				case 11:
					//预览，下载失败
					Toast.makeText(WebViewBiaoZhun.this, "获取文件失败！", Toast.LENGTH_SHORT).show();
					break;
				default:
					break;
			}
		}
	};

	protected int down_file(String urlStr, String path, String fileName)
	{
		try
		{
			boolean interceptFlag = false;
			int progress = 0;
			URL url = new URL(urlStr);
			//URL url = new URL("http://skype.gmw.cn/software/windows/SkypeSetupFull.7.25.99.106.exe");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.connect();

			int length = conn.getContentLength();
			InputStream is = conn.getInputStream();

			File file = new File(path);
			if (!file.exists())
			{
				file.mkdir();
			}

			String apkFile = path + fileName;
			File ApkFile = new File(apkFile);
			FileOutputStream fos = new FileOutputStream(ApkFile);

			int count = 0;
			int progress2 = 0;
			//缓存
			byte buf[] = new byte[1024];
			//写入到文件中
			do
			{
				int numread = is.read(buf);
				count += numread;
				//计算进度条位置
				progress = (int) (((float) count / length) * 100);
				if( progress>progress2 )
				{
					// 更新进度
					mBuilder.setProgress(100, progress, false);
					mBuilder.setContentText(  progress+"/%"  );
					mNotifyManager.notify(1, mBuilder.build());
					progress2 = progress;
				}
				if (numread <= 0)
				{
					// 下载完成通知安装
					//mHandler.sendEmptyMessage(DOWN_OVER);
					break;
				}
				fos.write(buf, 0, numread);
			}
			while (!interceptFlag);// 点击取消就停止下载.

			mBuilder.setContentText("Download complete")
					.setProgress(0,0,false);
			mNotifyManager.notify(1, mBuilder.build());
			fos.close();
			is.close();
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
			return 1;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return 1;
		}
		// 取消下载对话框显示
		//downloadDialog.dismiss();
		return 0;
	}

	protected void CallJavaScript(String filename)
	{
		String call;

		Log.d("cjwsjy", "------DOWNFromPhone-------");
		//call = "javascript:DOWNFromPhone('aachenchen123')";
		call = "javascript:DOWNFromPhone('"+filename+"')";

		mWebView.loadUrl(call);
	}

	protected void CallJavaScript2(String filename)
	{
		String call;

		Log.d("cjwsjy", "------Deletename="+filename+"-------DeleteFromPhone");
		//call = "javascript:DeleteFromPhone('aachenchen123')";
		call = "javascript:DeleteFromPhone('"+filename+"')";

		mWebView.loadUrl(call);
	}

	protected void CallJavaScript3(String filename)
	{
		String call;

		call = "javascript:searchHead()";

		mWebView.loadUrl(call);
	}

	private void showTips()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setTitle("提示");
		builder.setMessage("开始下载文件");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				// 如果没有网络连接，则进入网络设置界面
				//startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
			}
		});

		builder.create();
		builder.show();
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

	protected void onClickButton()
	{
		String call = "javascript:sayHello()";

		call = "javascript:test('aachenchen123')";
		//call = "javascript:test(\"" + "content" + "\")";
		//call = "javascript:toastMessage(\"" + "content" + "\")";
		//call = "javascript:sumToJava(1,2)";

		mWebView.loadUrl(call);
	}

	@Override
	public void onBackPressed()
	{
		String urlstr = mWebView.getUrl();
		//Log.d("cjwsjy", "--------Back="+urlstr+"-------BQ");
		if(urlstr.contains("bz/phone/webroot/demo/shenpixiangqing"))
		{
			Log.d("cjwsjy", "--------goBack=-------BQ");
			mWebView.goBack();
		}
		/*if( titleName.equals("资产扫码") )
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
		}*/
		else
		{
			mWebView.loadUrl("about:blank");
			this.finish();
		}
	}

}
