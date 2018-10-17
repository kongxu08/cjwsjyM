package com.cjwsjy.app.meeting;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cjwsjy.app.ActivityLogin;
import com.cjwsjy.app.R;
import com.cjwsjy.app.SmApplication;
import com.cjwsjy.app.adapter.FinishPeddingListAdaper;
import com.cjwsjy.app.base.BaseActivity;
import com.cjwsjy.app.custom.CustomDialog;
import com.cjwsjy.app.item.FinishPeddingItem;
import com.cjwsjy.app.main.MainActivity;
import com.cjwsjy.app.scanning.CaptureActivity;
import com.cjwsjy.app.utils.HttpClientUtil;
import com.cjwsjy.app.utils.ThreadUtils;
import com.cjwsjy.app.utils.UrlUtil;
import com.cjwsjy.app.webview.WebViewHome;
import com.cjwsjy.app.wholeally.activity.QyDeviceActivity;
import com.sqk.GridView.Grid_Item;
import com.sqk.GridView.NewGridAdaper2;
import com.wholeally.qysdk.QYSDK;
import com.wholeally.qysdk.QYSession;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;

public class ActivityMeeting extends BaseActivity
{
	private SharedPreferences sp;
		
	//每页显示条数
	private int retState;
	private int m_isQiandao = 0;
	private String m_username;
	private String appUrl;
	private String url;

	private ProgressDialog m_progressDialog;
	public static QYSession session;

	//生成动态数组，加入数据 
	List<FinishPeddingItem> listItems= new ArrayList<FinishPeddingItem>();

	private GridView gridView;
	private ThreadUtils m_threadlog;

	Handler handler = new Handler();
	ListView m_list;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//setContentView(R.layout.finishpeddinglist_layout);
		setContentView(R.layout.activity_meeting);

		// 得到单例对象
		m_threadlog = ThreadUtils.getInstance();  
		
		sp = SmApplication.sp;
		m_username = sp.getString("USERDATA.LOGIN.NAME", "");
		//m_username = "chenmin";
		
		TextView tv_navtitle = (TextView) findViewById(R.id.tv_navtitle);
		tv_navtitle.setText("我的会议");

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

		appUrl = UrlUtil.HOST;
		appUrl = "http://moa.cispdr.com";
		gridView = (GridView) findViewById(R.id.grid_plate);
		
		Thread threadhttp = new Thread(new ThreadHttp());
		threadhttp.start();
		
		//String list = "";
		//String url = "http://w802804.cjwsjy.com.cn:8080/HYServer/mvc/meeting/meetingIssue";
		//list = HttpClientUtil.sendRequestFromHttpClientGet(url,"UTF-8");
		
		//initList();
		initGridView();
	}

	@Override
	protected void onStart()
	{
		// TODO 自动生成的方法存根
		super.onStart();
		
		String sdPath = Environment.getExternalStorageDirectory() + "/Download" +"/com.cjwsjy.app/attachment2/";
		File file1 = new File(sdPath);
		deleteAllFiles(file1);
	}

	@Override
	protected void onResume()
	{
		// TODO 自动生成的方法存根
		super.onResume();
		
		String sdPath = Environment.getExternalStorageDirectory() + "/Download" +"/com.cjwsjy.app/attachment2/";
		File file1 = new File(sdPath);
		deleteAllFiles(file1);
	}

	private void initGridView()
	{
		List<Grid_Item> lists1 = new ArrayList<Grid_Item>();
		
		lists1.add(new Grid_Item(R.drawable.plate35, "会议指南"));
		lists1.add(new Grid_Item(R.drawable.plate36, "参会名单"));
		lists1.add(new Grid_Item(R.drawable.plate37, "会议签到"));
		lists1.add(new Grid_Item(R.drawable.plate38, "签到信息"));
		lists1.add(new Grid_Item(R.drawable.plate39, "我的座位"));
		lists1.add(new Grid_Item(R.drawable.plate40, "会议资料"));
		lists1.add(new Grid_Item(R.drawable.plate45, "现场直播")); //分组讨论
		lists1.add(new Grid_Item(R.drawable.plate42, "会议服务"));
		lists1.add(new Grid_Item(R.drawable.plate43, "会议公告"));
		
		DisplayMetrics metrics = new DisplayMetrics();
		Display display = getWindowManager().getDefaultDisplay();
		display.getMetrics(metrics);
		
		int pixelsX = metrics.widthPixels;
		
		NewGridAdaper adaper = new NewGridAdaper( ActivityMeeting.this, lists1, pixelsX );
		gridView.setAdapter(adaper);
		
		//添加消息处理
		gridView.setOnItemClickListener(gridViewListener);
	}
	
	protected void deleteAllFiles(File root)
	{
		File files[] = root.listFiles();
		if (files == null) return;
		
		for (File f : files)
		{
			if (f.isDirectory())
			{ // 判断是否为文件夹
				deleteAllFiles(f);
				try
				{
					f.delete();
				}
				catch (Exception e)
				{
				}
			}
			else
			{
				if (f.exists())
				{ // 判断是否存在
					deleteAllFiles(f);
					try
					{
						f.delete();
					}
					catch (Exception e)
					{
					}
				}
			}
		}
	}
	
	//出差审批
    private  OnItemClickListener  gridViewListener = new OnItemClickListener() 
    {
        @Override
        public void onItemClick(AdapterView<?> arg0,View arg1,int arg2,long arg3)
        {
        	if( arg3==0)  //会议指南
            {
    			m_threadlog.setparm2("会议指南", "点击", "年终会议");
    			m_threadlog.writelog();
    			
    			url = appUrl+":8087/HYServer/jsp/zhinan.jsp";
        		startWebView(url,"会议指南");
            }
        	else if( arg3==1 )  //参会名单
        	{
    			m_threadlog.setparm2("参会名单", "点击", "年终会议");
    			m_threadlog.writelog();
    			
    			url = appUrl+":8087/HYServer/mvc/meeting/mingdan";
        		startWebView(url,"参会名单");
        	}
        	else if( arg3==2 )  //会议签到
        	{
        		m_threadlog.setparm2("会议签到", "点击", "年终会议");
    			m_threadlog.writelog();
    			
    			if( m_isQiandao==0 )
    			{
	        		Intent intent = new Intent( ActivityMeeting.this, CaptureActivity.class);
	        		intent.putExtra("titleName","会议签到");
					startActivity(intent);
    			}
    			else
    			{
    				new AlertDialog.Builder(ActivityMeeting.this)
    				 .setTitle("提示") 
    				 .setMessage("您已签到")
    				 .setPositiveButton("确定", null) 
    				 .show(); 
    			}
        	}
        	else if( arg3==3 )  //签到信息
        	{
        		m_threadlog.setparm2("签到信息", "点击", "年终会议");
    			m_threadlog.writelog();
    			
    			url = appUrl+":8087/HYServer/mvc/meeting/userList";
        		startWebView(url,"签到信息");
        	}
        	else if( arg3==4 )  //我的座位
        	{
        		m_threadlog.setparm2("我的座位", "点击", "年终会议");
    			m_threadlog.writelog();
    			
        		url = appUrl+":8087/HYServer/mvc/meeting/userPlace/"+m_username;
        		startWebView(url,"我的座位");
        	}
        	else if( arg3==5 )  //会议资料
        	{
        		m_threadlog.setparm2("会议资料", "点击", "年终会议");
    			m_threadlog.writelog();
    			
    			Intent intent = new Intent( ActivityMeeting.this, ActivityMeetingziliao.class);
				startActivity(intent);
        	}
        	else if( arg3==6 )  //分组讨论
        	{
        		m_threadlog.setparm2("现场直播", "点击", "年终会议");
    			m_threadlog.writelog();
    			
        		//url = appUrl+":8087/HYServer/jsp/fenzhu.jsp";
        		//startWebView(url,"现场直播");

				int ret = PermissionsRequest2(Manifest.permission.READ_PHONE_STATE, 104);
				if(ret!=1) return;

				attemptLogin();
        	}
        	else if( arg3==7 )  //会议服务
        	{
        		m_threadlog.setparm2("会议服务", "点击", "年终会议");
    			m_threadlog.writelog();

        		url = appUrl+":8087/HYServer/jsp/fuwu.jsp";
        		startWebView(url,"会议服务");
        	}
        	else if( arg3==8 )
        	{
        		m_threadlog.setparm2("会议公告", "点击", "年终会议");
    			m_threadlog.writelog();

        		url = appUrl+":8087/HYServer/jsp/gonggao.jsp";
        		startWebView(url,"会议公告");
        	}
        	else if( arg3==9 )
        	{
        		Toast.makeText( ActivityMeeting.this, "第9项", Toast.LENGTH_LONG).show();
        	}
        }
    };

	// 是否签到线程类
	class ThreadHttp implements Runnable
	{
		@Override
		public void run()
		{
			int i = 0;
			int length = 0;
			String url = "";
			String count = "";
			String jsonStr1="";
			String jsonStr2="";
			
	    	// 请求
			url = appUrl + ":8087/HYServer/mvc/meeting/isSigned/"+m_username;
			jsonStr1 = HttpClientUtil.HttpUrlConnectionGet(url, "UTF-8");
			jsonStr2 = "{\"data\":[" + jsonStr1 + "]}";

			try 
			{
				JSONArray jsonObjs = new JSONObject(jsonStr2).getJSONArray("data");
				length = jsonObjs.length();
				for( i=0; i<length; i++ ) 
				{
					JSONObject jsonObj = jsonObjs.getJSONObject(i);

					count = jsonObj.getString("issign");
				}
			}
			catch (JSONException e) 
			{
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}

			if( count.equals("1") ) m_isQiandao = 1;
			else m_isQiandao = 0;
		}
	}

	private void attemptLogin()
	{

		int nport = 0;
		String IPaddress;
		String ports;
		String appid;
		String auth;

		//初始化SDK
		QYSDK.InitSDK(5);
		//创建session会话
		session = QYSDK.CreateSession(getApplicationContext());

		//IPaddress = "117.28.255.16";
		IPaddress = "218.106.125.147";
		ports = "39100";  //39100  18081

		nport = Integer.valueOf(ports);
		appid = "wholeally";
		//auth = "czFYScb5pAu+Ze7rXhGh/+KgxMFQtw/4EkOwlq9xjm9GZ87T7kinBeiME6d8w4HzZNmnJIpQUIK7ZEM8xbu+utMwoI8ilfYVP3APq+CfVaHkMAVHncHZlw==";
		auth = "czFYScb5pAu+Ze7rXhGh/+DZG+EYxzXl6mU9JCMJ/F/rvEuURddsP/hN/Xzyf48WRweoOAiaI9vmlrBaCMrtn9FAt75ccbDU";

		m_progressDialog = ProgressDialog.show(ActivityMeeting.this, "加载中...", "正  在  获  取  数  据 ...");
		retState = session.SetServer(IPaddress,nport);//连接服务器大于或等于0为成功  否则为失败
		if ( retState>=0 )
		{
			//session会话登录 第二个参数为回调函数 ret大于或等于0为成功  否则为失败
			// 测试用： wholeally    czFYScb5pAu+Ze7rXhGh/zURoROEIJ5JZnqf1q9hjlNQpfpixq+tzaIuQmoa2qa0Vgd/r1TPf+IQy3AED5xjo9iSSMjZjGIKZv8EsCI3VJc=
			session.ViewerLogin(appid,auth, new QYSession.OnViewerLogin()
			{
				@Override
				public void on(int ret)
				{
					System.out.println("===ret==:"+ret);
					if (ret >= 0)
					{
						m_progressDialog.cancel();
						//showToast("登录成功");
						//Toast.makeText( ActivityMeeting.this, "登录成功", Toast.LENGTH_LONG).show();
						android.util.Log.d("cjwsjy", "------登录成功-------7");

						Intent intent = new Intent(ActivityMeeting.this, QyDeviceActivity.class);
						startActivity(intent);
					}
					else
					{
						m_progressDialog.cancel();
						//Toast.makeText( ActivityMeeting.this, "登录失败", Toast.LENGTH_LONG).show();
						//showToast("登录失败:"+String.valueOf(ret)+";或者ViewerLogin第一个或第二个参数错误");
						android.util.Log.d("cjwsjy", "------登录失败-------7");
						showdialog2("登录失败");
					}
				}
			});
		}
		else
		{
			m_progressDialog.cancel();
			//showToast("服务器连接失败:"+String.valueOf(retState));
		}
	}

	private int showdialog2(String strtext)
	{
		CustomDialog.Builder builder = new CustomDialog.Builder(this);
		builder.setMessage(strtext);
		builder.setTitle("提示");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
				//设置你的操作事项
				//ActivityMeeting.this.finish();
			}
		});

		builder.create().show();
		return 1;
	}

	private int PermissionsRequest2(String strpermission, int code )
	{
		if( Build.VERSION.SDK_INT<23 ) return 1;

		//判断该权限
		int Permission = ContextCompat.checkSelfPermission(ActivityMeeting.this, strpermission);
		if( Permission!= PackageManager.PERMISSION_GRANTED )
		{
			//没有权限，申请权限
			ActivityCompat.requestPermissions(ActivityMeeting.this,new String[]{ strpermission}, code);
			return 1014;
		}

		return 1;
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
	{
		switch (requestCode)
		{
			case 104:
				if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
				{
					// 同意
					attemptLogin();
				}
				else
				{
					// 拒绝
					Toast.makeText(ActivityMeeting.this, "没有权限！", Toast.LENGTH_SHORT).show();
				}
				break;
			default:
				super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private void startWebView(String urlstr,String title)
	{
		Intent intent = new Intent();
		intent.setClass(ActivityMeeting.this, WebViewHome.class);
		intent.putExtra("webUrl",urlstr);
		intent.putExtra("titleName",title);
		startActivity(intent);
	}

}
