package com.cjwsjy.app.webview;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
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
import com.cjwsjy.app.custom.CustomDialog;
import com.cjwsjy.app.scanning.CaptureActivity;
import com.cjwsjy.app.utils.CallOtherOpeanFile;
import com.cjwsjy.app.wholeally.activity.NetworkStateService;
import com.cjwsjy.app.wholeally.activity.QyDeviceActivity;
import com.cjwsjy.app.wholeally.activity.QyVideoControlActivity;
import com.cjwsjy.app.wholeally.activity.QyVideoControlActivity2;
import com.do1.cjmobileoa.db.DBManager;
import com.do1.cjmobileoa.db.model.DepartmentEmployeeVO;
import com.do1.cjmobileoa.db.model.DepartmentVO;
import com.do1.cjmobileoa.db.model.EmployeeVO;
import com.wholeally.qysdk.QYSDK;
import com.wholeally.qysdk.QYSession;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * webView页面，食堂使用，初始化时会调JavaScript函数
 */
public class WebViewCanteen extends BaseActivity {

	private int m_downmode = 0;

	private int m_ShiLian = 0;

	private String webUrl;
	private String fileUrl = "";
	private String m_userId;
	private String m_telnumber;
	private String m_JavaScript;
	private String titleName;
	private String attachmentName = "";
	private String m_ChannelName = "";

	private SharedPreferences sp;
	private TextView tv_navtitle;
	private WebView mWebView;

	private Message msg;
	//public static QYSession session;
	public static QYSession session1;
	public static QYSession session2;
	private ProgressDialog m_progressDialog;
	private DBManager db;
	private ProgressDialog mDialog;

	private RelativeLayout layout;

	private NotificationManager mNotifyManager;
	private NotificationCompat.Builder mBuilder;
	private Notification mnotify;

	private boolean isRedirect = false;// 是否有重定向的页面

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//requestWindowFeature(Window.FEATURE_PROGRESS);// 让进度条显示在标题栏上
		setContentView(R.layout.webview_bq);

		m_downmode = 0;
		db = SmApplication.dbManager;
		webUrl = getIntent().getExtras().get("webUrl").toString();

		sp = SmApplication.sp;
		m_userId = sp.getString("USERDATA.USER.ID", "");

		layout = (RelativeLayout) findViewById(R.id.titletop);

		//title
		tv_navtitle = (TextView) this.findViewById(R.id.tv_navtitle);
		if (getIntent().getExtras().get("titleName") != null) {
			titleName = getIntent().getExtras().get("titleName").toString();
		}
		tv_navtitle.setText(titleName);

		//隐藏标题栏
		//layout.setVisibility(View.GONE);

		//后退
		ImageView iv_back = (ImageView) this.findViewById(R.id.iv_back);
		iv_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//onClickButton();
				onBackPressed();
			}
		});

		//搜索
		ImageView iv_serachs = (ImageView) this.findViewById(R.id.iv_add);
		iv_serachs.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CallJavaScript3("123");
			}
		});

		//通知栏
		//定义一个PendingIntent点击Notification后启动一个Activity
		//Intent it = new Intent(WebViewBQ.this, OtherActivity.class);
		//PendingIntent pit = PendingIntent.getActivity(WebViewBQ.this, 0, it, 0);

		//获取状态通知栏管理
		mNotifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		//实例化通知栏构造器NotificationCompat.Builder：
		mBuilder = new NotificationCompat.Builder(this);


		mBuilder.setContentTitle("Picture Download")
				.setContentText("Download in progress")
				.setSmallIcon(R.drawable.ic_launcher);

		//调用Builder的build()方法为notification赋值
		mnotify = mBuilder.build();

		initWebView();

		String company;
		String orgid;
		EmployeeVO employee;

		employee = db.findEmployeeById(m_userId);
		DepartmentEmployeeVO orgsVO = db.findOrgsbyUserid(m_userId);

		orgid = orgsVO.getorgRID();
		DepartmentVO deptVO = db.findDepartmentbyid(orgid);
		company = deptVO.getDeptDisplayname();

		//公司名
		//deptVO = db.findOrganizationbyid(deptVO.getDeptParentid());
		//company = deptVO.getDeptDisplayname();

		//m_JavaScript = "{\"OFFICEADDRESS\":\"\",\"MOBILEIPHONE\":\"18502775907\",\"TELEPHONE\":\"\",\"USERDISPLAYNAME\":\"许力\",\"POSTDUTY\":\"\",\"USERNAME\":\"xuli2\",\"IMACCOUNTS\":\"sip:xuli2@cjwsjy.com.cn\",\"GENDER\":\"男\",\"title\":\"\",\"MOBILEIPHONESHORTNUMBER\":\"775907\",\"EMAIL\":\"xuli2@cjwsjy.com.cn\",\"PHOTO\":\"\\/UploadedFiles\\/Framework.Web\\/xuli2.jpg\",\"officeName\":\"宏数公司技术部\",\"companyName\":\"宏数公司\",\"MOBILE\":\"18502775907\",\"USERID\":\"1A2C9262-94D6-4F5A-ACB4-F7038681AFC3\",\"MOBILESHORTNUMBER\":\"775907\",\"OFFICETELSHORT\":\"\",\"JOBNUMBER\":\"802804\"}";

		m_JavaScript = "{\"MOBILEIPHONE\":\"" + employee.getMobileIphone() + "\",";
		m_JavaScript = m_JavaScript + "\"USERID\":\"" + m_userId + "\",";
		m_JavaScript = m_JavaScript + "\"USERNAME\":\"" + employee.getUsername() + "\",";
		m_JavaScript = m_JavaScript + "\"USERDISPLAYNAME\":\"" + employee.getUserDisplayName() + "\",";
		m_JavaScript = m_JavaScript + "\"JOBNUMBER\":\"" + employee.getjobNumber() + "\",";
		//m_JavaScript = m_JavaScript + "\"PHOTO\":\"" + employee.getPhoto() + "\",";
		//m_JavaScript = m_JavaScript + "\"GENDER\":\"" + employee.getPhoneNumber() + "\",";
		//m_JavaScript = m_JavaScript + "\"USERSTATE\":\"" + employee.getPhoneNumber() + "\",";
		m_JavaScript = m_JavaScript + "\"POSTDUTY\":\"" + employee.getpostDuty() + "\",";
		m_JavaScript = m_JavaScript + "\"EMAIL\":\"" + employee.getEmail() + "\",";
		//m_JavaScript = m_JavaScript + "\"IMACCOUNTS\":\"" + employee.getPhoneNumber() + "\",";
		m_JavaScript = m_JavaScript + "\"MOBILE\":\"" + employee.getPhoneNumber() + "\",";
		m_JavaScript = m_JavaScript + "\"MOBILESHORTNUMBER\":\"" + employee.getPhoneShortNumber() + "\",";
		m_JavaScript = m_JavaScript + "\"MOBILEIPHONESHORTNUMBER\":\"" + employee.getmobileIphoneShortNumber() + "\",";
		m_JavaScript = m_JavaScript + "\"TELEPHONE\":\"" + employee.getPhoneNumber() + "\",";
		m_JavaScript = m_JavaScript + "\"OFFICETELSHORT\":\"" + employee.getofficeTelShort() + "\",";
		m_JavaScript = m_JavaScript + "\"OFFICEADDRESS\":\"" + employee.getOfficeAddress() + "\",";
		m_JavaScript = m_JavaScript + "\"officeName\":\"" + company + "\"";
		m_JavaScript = m_JavaScript + "}";

		android.util.Log.i("cjwsjy", "--------m_JavaScript=" + m_JavaScript + "-------");

        //初始化视频
        Intent i = new Intent(WebViewCanteen.this, NetworkStateService.class);
        startService(i);
        //初始化SDK
        QYSDK.InitSDK(4);

        session1 = QYSDK.CreateSession(getApplicationContext());
		session2 = QYSDK.CreateSession(getApplicationContext());
	}

	private void initWebView() {
		WebView webView = new WebView(WebViewCanteen.this);
		mWebView = (WebView) findViewById(R.id.webView_BQ);
		WebSettings webSettings = mWebView.getSettings();

		mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);

		webSettings.setDomStorageEnabled(true);

		webSettings.setTextZoom(100);

		//设置页面自适应手机屏幕
		webSettings.setUseWideViewPort(false);
		webSettings.setLoadWithOverviewMode(true);

		//第二种方法，设置满屏显示
		//webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

		//不显示webview缩放按钮
		webSettings.setDisplayZoomControls(false);

		//String ua = "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; EmbeddedWB 14.52 from: http://www.bsalsa.com/ EmbeddedWB 14.52; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET4.0C; .NET4.0E; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)";
		//webSettings.setUserAgentString(ua);
		webSettings.setBlockNetworkImage(false);    //图片下载阻塞

		mWebView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int progress) {
				//setTitle("页面加载中，请稍候..." + progress + "%");
				setProgress(progress * 100);
				if (progress == 100) {
					setTitle(R.string.app_name);
				}
			}

			@Override
			public void onReceivedTitle(WebView view, String title) {
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

		mWebView.loadUrl(webUrl);

		Log.d("cjwsjy", "------loadUrl=" + webUrl + "-------BQ");

		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				Log.d("cjwsjy", "------Loading=" + url + "-------BQ");

				int result = 0;
				String strurl;

				//装柜
				result = url.indexOf("shaomazhuanggui:");
				if (result != -1) {
					view.stopLoading();

					int ret = requestPermissions(Manifest.permission.CAMERA, 104);
					if (ret != 1) return true;

					url = "zhuanggui";
					Intent intent = new Intent(WebViewCanteen.this, CaptureActivity.class);
					intent.putExtra("canteen", url);
					startActivity(intent);
					return true;
				}

				//取货
				result = url.indexOf("shaomaquhuo:");
				if (result != -1) {
					view.stopLoading();

					int ret = requestPermissions(Manifest.permission.CAMERA, 105);
					if (ret != 1) return true;

					url = "quhuo";
					Intent intent = new Intent(WebViewCanteen.this, CaptureActivity.class);
					intent.putExtra("canteen", url);
					startActivity(intent);
					return true;
				}

				//电话
				result = url.indexOf("tel:");
				if (result != -1) {
					m_telnumber = url;
					int ret = requestPermissions(Manifest.permission.CALL_PHONE, 103);
					if (ret != 1) return true;

					Intent intent = new Intent();
					intent.setAction(Intent.ACTION_CALL);
					intent.setData(Uri.parse(url));
					startActivity(intent);

					//停止加载
					view.stopLoading();
					return true;
				}

				//食堂视频二楼
				result = url.indexOf("/shitangshipin1");
				if (result != -1) {
					m_ChannelName = "1000000012001";
//孔雀
// 					m_ChannelName = "1000000001001";
					int ret = requestPermissions(Manifest.permission.READ_PHONE_STATE, 106);
					if (ret != 1) return true;

					StartShiLian();

					//停止加载
					view.stopLoading();
					return true;
				}

				//食堂视频三楼
				result = url.indexOf("/shitangshipin2");
				if (result != -1) {
					m_ChannelName = "1000000012002";
					int ret = requestPermissions(Manifest.permission.READ_PHONE_STATE, 106);
					if (ret != 1) return true;

					StartShiLian();

					//停止加载
					view.stopLoading();
					return true;
				}

				//食堂视频四楼
				result = url.indexOf("/shitangshipin3");
				if (result != -1) {
					m_ChannelName = "1000000012003";
					int ret = requestPermissions(Manifest.permission.READ_PHONE_STATE, 106);
					if (ret != 1) return true;

					StartShiLian();

					//停止加载
					view.stopLoading();
					return true;
				}

				view.loadUrl(url);
				return true;
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				//Log.d("cjwsjy", "---------PageStarted="+url+"-------BQ");
				showProgressDialog("正在加载 ，请等待...", true);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);

				closeProgressDialog();
				mWebView.getSettings().setBlockNetworkImage(false);

				//调页面JavaScript函数
				if (m_downmode == 0) {
					CallJavaScript(m_JavaScript);
					m_downmode++;
				}
			}

			@Override
			public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
				// TODO Auto-generated method stub
				handler.proceed();
			}
		});
	}

	public void onResume()
	{
		super.onResume();
	}

	//加载对话框
	private void showProgressDialog(String msg, boolean cancelable) {
		if (mDialog == null && mWebView != null) {
			mDialog = new ProgressDialog(WebViewCanteen.this);
			mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置风格为圆形进度条
			mDialog.setMessage(msg);
			mDialog.setIndeterminate(false);// 设置进度条是否为不明确
			mDialog.setCancelable(cancelable);// 设置进度条是否可以按退回键取消
			// WebViewHome.this
			mDialog.setCanceledOnTouchOutside(false);
			mDialog.show();
		} else {
			mDialog.setMessage(msg);
			mDialog.show();
		}
	}

	private void closeProgressDialog() {
		if (mDialog != null && mDialog.isShowing()) {
			mDialog.dismiss();
			mDialog = null;
		}
	}

	//删除附件
	protected int DeleteAttachment(String url) {
		int result = 0;
		String strurl = "";
		String filepath = "";

		//编码转换
		try {
			strurl = java.net.URLDecoder.decode(url, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}

		fileUrl = strurl;

		//得到附件文件名
		//result = strurl.lastIndexOf("/");
		//attachmentName = strurl.substring(result+1, strurl.length());
		attachmentName = fileUrl;
		Log.d("cjwsjy", "---------删除附件=" + attachmentName + "-------");

		//判断文件是否存在
		filepath = Environment.getExternalStorageDirectory() + "/Download/com.cjwsjy.app/attachment3/" + attachmentName;
		//filepath = Environment.getExternalStorageDirectory() + "/bzAPP/Attach/"+attachmentName;
		File file = new File(filepath);
		if (file.exists()) {
			//删除文件
			//file.delete();
			try {
				file.delete();
			} catch (Exception e) {
			}
			Toast.makeText(WebViewCanteen.this, "删除文件成功！", Toast.LENGTH_SHORT).show();

			//在数据库中删除
			db.DeleteBiaozhun(attachmentName);

			//调用页面JavaScript函数，把附件文件名给页面
			CallJavaScript2(url);
		} else {
			Toast.makeText(WebViewCanteen.this, "删除文件失败！文件未找到", Toast.LENGTH_SHORT).show();
		}

		return 1;
	}

	protected void CallJavaScript(String filename) {
		String call;

		Log.i("cjwsjy", "------CallJavaScript-------");

		//call = "javascript:getUserInfo('aachenchen123')";
		call = "javascript:getUserInfo('" + filename + "')";

		mWebView.loadUrl(call);
	}

	protected void CallJavaScript2(String filename) {
		String call;

		Log.d("cjwsjy", "------Deletename=" + filename + "-------DeleteFromPhone");
		//call = "javascript:DeleteFromPhone('aachenchen123')";
		call = "javascript:DeleteFromPhone('" + filename + "')";

		mWebView.loadUrl(call);
	}

	protected void CallJavaScript3(String filename) {
		String call;

		call = "javascript:searchHead()";

		mWebView.loadUrl(call);
	}

	private void StartShiLian()
	{
		m_ShiLian = 0;
		m_progressDialog = ProgressDialog.show(WebViewCanteen.this, "加载中...", "正  在  获  取  数  据 ...");

		Thread loginThread1 = new Thread(new ThreadConnect1());
		loginThread1.start();

//		Thread loginThread2 = new Thread(new ThreadConnect2());
//		loginThread2.start();
	}

	private void LoginShiLian(String IPaddress, int ports) {
		/*int retState;
		int nport = 0;
		//String IPaddress;
		//String ports;
		String appid;
		String auth;

		Intent i = new Intent(this, NetworkStateService.class);
		startService(i);
		//初始化SDK
		QYSDK.InitSDK(4);
		//创建session会话
		session = QYSDK.CreateSession(getApplicationContext());

		//IPaddress = "117.28.255.16";
		//IPaddress = "218.106.125.147";  //外网
		//IPaddress="172.16.13.70";  //内网
		//IPaddress="zzh.cjwsjy.com.cn";	//域名
		//ports = "39100";

		nport = ports;
		appid = "wholeally";
		auth = "czFYScb5pAu+Ze7rXhGh/+MesDXVJaszykybiYXfmlUG5hUR8MkXH/MBaOy+I6hqSFLfYeF/Q/Or/s3BO8pI4ROo63Hq79pf";

		m_progressDialog = ProgressDialog.show(WebViewCanteen.this, "加载中...", "正  在  获  取  数  据 ...");
		retState = session.SetServer(IPaddress, nport);//连接服务器大于或等于0为成功  否则为失败
		if (retState >= 0) {
			//session会话登录 第二个参数为回调函数 ret大于或等于0为成功  否则为失败
			session.ViewerLogin(appid, auth, new QYSession.OnViewerLogin() {
				@Override
				public void on(int ret) {
					if (ret >= 0) {
						//登录成功
						m_progressDialog.cancel();
						Intent intent = new Intent(WebViewCanteen.this, QyVideoControlActivity2.class);
						intent.putExtra("channelName",m_ChannelName);
						startActivity(intent);
					} else {
						//登录失败
						m_progressDialog.cancel();

						if (m_ShiLian == 0) {
							LoginShiLian("218.106.125.147", 39100);  //外网
							m_ShiLian = 1;
						} else
							Toast.makeText(WebViewCanteen.this, "登录失败:" + String.valueOf(ret) + ";或者ViewerLogin第一个或第二个参数错误", Toast.LENGTH_SHORT).show();
					}
				}
			});
		}
		else
		{
			m_progressDialog.cancel();
			Toast.makeText(WebViewCanteen.this, "服务器连接失败:" + String.valueOf(retState), Toast.LENGTH_SHORT).show();
			//return 1013;

		}*/
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

			//创建session会话
			//session1 = QYSDK.CreateSession(getApplicationContext());

			//IPaddress = "117.28.255.16";
			//IPaddress = "218.106.125.147";  //外网
			//IPaddress="172.16.13.70";  //内网
			//IPaddress="zzh.cjwsjy.com.cn";	//域名
			//ports = "39101";

			IPaddress="10.6.191.138";
			nport = 39100;
			appid = "wholeally";
			//auth = "czFYScb5pAu+Ze7rXhGh/+MesDXVJaszykybiYXfmlUG5hUR8MkXH/MBaOy+I6hqSFLfYeF/Q/Or/s3BO8pI4ROo63Hq79pf";
			auth = "czFYScb5pAu+Ze7rXhGh/6tBJYEbmP2yykybiYXfmlUG5hUR8MkXH/MBaOy+I6hqSFLfYeF/Q/Or/s3BO8pI4ROo63Hq79pf";

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
				Toast.makeText(WebViewCanteen.this, "服务器连接失败:" + String.valueOf(retState), Toast.LENGTH_SHORT).show();

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

			//创建session会话
			//session2 = QYSDK.CreateSession(getApplicationContext());

			//IPaddress = "117.28.255.16";
			IPaddress = "218.106.125.147";  //外网
			//IPaddress="172.16.13.70";  //内网
			//IPaddress="zzh.cjwsjy.com.cn";	//域名
			//ports = "39100";

			nport = 39100;
			appid = "wholeally";
			auth = "czFYScb5pAu+Ze7rXhGh/+MesDXVJaszykybiYXfmlUG5hUR8MkXH/MBaOy+I6hqSFLfYeF/Q/Or/s3BO8pI4ROo63Hq79pf";

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
				Toast.makeText(WebViewCanteen.this, "服务器连接失败:" + String.valueOf(retState), Toast.LENGTH_SHORT).show();

				msg = handler.obtainMessage();
				msg.what = 108;
				msg.obj = String.valueOf(retState);
				handler.sendMessage(msg);
			}
		}
	}

	// Handler
	Handler handler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			String strmsg = "";
			Intent intent;

            android.util.Log.i("cjwsjy", "------msg.what="+msg.what+"-------");

			this.obtainMessage();
			m_progressDialog.cancel();
			switch (msg.what)
			{
				case 103:  //成功
					//内网和外网只运行一个
					if(m_ShiLian==0)
					{
						m_ShiLian++;
						intent = new Intent(WebViewCanteen.this, QyVideoControlActivity2.class);
						intent.putExtra("channelName", m_ChannelName);
						intent.putExtra("session", "1");
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
						intent = new Intent(WebViewCanteen.this, QyVideoControlActivity2.class);
						intent.putExtra("channelName",m_ChannelName);
						intent.putExtra("session","2");
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

					Toast.makeText(getApplicationContext(), "登录失败，失败类型1037",Toast.LENGTH_SHORT).show();
					break;
			}
		}
	};

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
		String url;

		switch (requestCode)
		{
            case 103:  //电话
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    // 同意
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_CALL);
                    intent.setData(Uri.parse(m_telnumber));
                    startActivity(intent);
                }
                else
                {
                    // 拒绝
                    showdialog("没有权限");
                }
                break;
			case 104:  //装柜
				if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
				{
					// 同意
					url = "zhuanggui";
					Intent intent = new Intent(WebViewCanteen.this, CaptureActivity.class);
					intent.putExtra("canteen",url);
					startActivity(intent);
				}
				else
				{
					// 拒绝
					showdialog("没有权限");
				}
				break;
			case 105:  //取货
				if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
				{
					// 同意
					url = "quhuo";
					Intent intent = new Intent(WebViewCanteen.this, CaptureActivity.class);
					intent.putExtra("canteen",url);
					startActivity(intent);
				}
				else
				{
					// 拒绝
					showdialog("没有权限");
				}
				break;
			case 106:  //食堂视频
				if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
				{
					// 同意
					StartShiLian();
				}
				else
				{
					// 拒绝
					showdialog("没有权限");
				}
				break;
			default:
				super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
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

	private int showdialog(String content)
	{
		CustomDialog.Builder builder = new CustomDialog.Builder(this);
		builder.setMessage(content);
		builder.setTitle("提示");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
				//设置你的操作事项
				//WebViewCanteen.this.finish();
			}
		});

		builder.create().show();
		return 1;
	}

	@Override
	public void onBackPressed()
	{
		String urlstr = mWebView.getUrl();

        //android.util.Log.i("cjwsjy", "--------url="+urlstr+"-------onBackPressed");

		//菜谱
		if( urlstr.contains("/STManage/caipu/") )
		{
			mWebView.loadUrl("about:blank");
			this.finish();
			return;
		}

        //点评
        if (urlstr.contains("/STManage/dianping/"))
        {
            mWebView.loadUrl("about:blank");
            this.finish();
            return;
        }

		//预定
		if (urlstr.contains("/STManage/yuding_room/"))
		{
			mWebView.loadUrl("about:blank");
			this.finish();
			return;
		}
        if (urlstr.contains("/STManage/yuding_dish/"))
        {
            mWebView.loadUrl("about:blank");
            this.finish();
            return;
        }

		//我的
		if (urlstr.contains("/STManage/my_account/"))
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

		//mWebView.goBack();
	}

}
