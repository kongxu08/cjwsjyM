package com.cjwsjy.app.homeFragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cjwsjy.app.R;
import com.cjwsjy.app.SmApplication;
import com.cjwsjy.app.base.BaseActivity;
import com.cjwsjy.app.item.FinishPeddingItem;
import com.cjwsjy.app.news.ActivityNews;
import com.cjwsjy.app.news.ActivityNewsSearch;
import com.cjwsjy.app.utils.ThreadUtils;
import com.cjwsjy.app.utils.UrlUtil;
import com.cjwsjy.app.webview.WebViewHome;

import java.util.ArrayList;
import java.util.List;

import com.cjwsjy.app.wholeally.activity.QyDeviceActivity;
import com.cjwsjy.app.wholeally.activity.QyVideoControlActivity;
import com.wholeally.qysdk.QYSDK;
import com.wholeally.qysdk.QYSession;
import com.wholeally.qysdk.QYSession.OnViewerLogin;

public class ActivityZhibo extends BaseActivity implements OnItemClickListener
{
	private int retState;
	private String m_loginname;
	private String m_ChannelName;
	private String appUrl;

	private SharedPreferences sp;
	private ThreadUtils m_threadlog;

	// 生成动态数组，加入数据
	private List<FinishPeddingItem> listItems = new ArrayList<FinishPeddingItem>();
	private ListView list;

	private AdaperXinwen listItemAdapter;

	//public static QYSession session;
	//private ProgressDialog m_progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
		setContentView(R.layout.activity_homelist);

		m_threadlog = ThreadUtils.getInstance();  // 得到单例对象
		
		sp = SmApplication.sp;
		m_loginname = sp.getString("USERDATA.LOGIN.NAME", "");
		m_ChannelName = sp.getString("ChannelName", "");
		
		appUrl = UrlUtil.HOST;
		 
		list = (ListView) findViewById(R.id.homelist);
		
		TextView tv_navtitle = (TextView) findViewById(R.id.tv_navtitle);
		tv_navtitle.setText("新闻中心");

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
		FinishPeddingItem listItem1 = new FinishPeddingItem();
		listItem1.setIv_icon1(R.drawable.home_xinwen01);
		listItem1.setTv_title("直播通道 1");
		listItems.add(listItem1);
		
		FinishPeddingItem listItem2 = new FinishPeddingItem();
		listItem2.setIv_icon1(R.drawable.home_xinwen01);
		listItem2.setTv_title("直播通道 2");
		listItems.add(listItem2);

//		FinishPeddingItem listItem3 = new FinishPeddingItem();
//		listItem3.setIv_icon1(R.drawable.home_xinwen03);
//		listItem3.setTv_title("企业新闻");
//		listItems.add(listItem3);
//
//		FinishPeddingItem listItem4 = new FinishPeddingItem();
//		listItem4.setIv_icon1(R.drawable.home_xinwen04);
//		listItem4.setTv_title("新闻搜索");
//		listItems.add(listItem4);
		
		// 生成适配器的Item和动态数组对应的元素
		listItemAdapter = new AdaperXinwen( ActivityZhibo.this, listItems );
		// 添加并且显示
		list.setAdapter(listItemAdapter);
		// list.setOnItemClickListener(this);
		// list.setOnScrollListener(this);

		list.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) 
			{
				String url;
				FinishPeddingItem item = (FinishPeddingItem) list.getItemAtPosition(position);
				String formId = item.getTv_formid();

				if (position == 0)
				{
					m_threadlog.setparm2("点击","新闻中心-通知公告",  "通知公告-新闻中心-首页");
					m_threadlog.writelog();

	        		url = appUrl+"/CEGWAPServer/XW/GetIPSList_GGB2/";
	        		Intent intent = new Intent( ActivityZhibo.this, ActivityNews.class);
	        		intent.putExtra("URL",url);
	        		intent.putExtra("titleName","通知公告");
	        		intent.putExtra("Parameter","0");
					startActivity(intent);
				}
				if( position==1 )
				{
					m_threadlog.setparm2("点击","会议直播-我的会议", "会议直播-我的会议-首页");
					m_threadlog.writelog();

					LoginAttempt();
				}
				if( position==2 )
				{
					m_threadlog.setparm2("点击","新闻中心-企业新闻", "企业新闻-新闻中心-首页");
					m_threadlog.writelog();

	        		url = appUrl+"/CEGWAPServer/XW/GetIPSList_QYXW2/";
	        		Intent intent = new Intent( ActivityZhibo.this, ActivityNews.class);
	        		intent.putExtra("URL",url); 
	        		intent.putExtra("titleName","企业新闻");
	        		intent.putExtra("Parameter","0");
					startActivity(intent);
				}
				if( position==3 )
				{
					m_threadlog.setparm2("点击","新闻中心-新闻搜索", "新闻搜索-新闻中心-首页");
					m_threadlog.writelog();

	        		Intent intent = new Intent( ActivityZhibo.this, ActivityNewsSearch.class);
					startActivity(intent);
				}
			}
		});
		
		return list;
	}

	private void LoginAttempt()
	{

		/*int nport = 0;
		String IPaddress;
		String ports;
		String appid;
		String auth;

		//初始化SDK
		QYSDK.InitSDK(5);
		//创建session会话
		session = QYSDK.CreateSession( getApplicationContext() );

		//IPaddress = "117.28.255.16";
		IPaddress = "218.106.125.147";
		ports = "39100";  //39100  18081

		nport = Integer.valueOf(ports);
		appid = "wholeally";
		//auth = "czFYScb5pAu+Ze7rXhGh/+KgxMFQtw/4EkOwlq9xjm9GZ87T7kinBeiME6d8w4HzZNmnJIpQUIK7ZEM8xbu+utMwoI8ilfYVP3APq+CfVaHkMAVHncHZlw==";
		auth = "czFYScb5pAu+Ze7rXhGh/+DZG+EYxzXl6mU9JCMJ/F/rvEuURddsP/hN/Xzyf48WRweoOAiaI9vmlrBaCMrtn9FAt75ccbDU";

		m_progressDialog = ProgressDialog.show(ActivityZhibo.this, "加载中...", "正  在  获  取  数  据 ...");
		retState = session.SetServer(IPaddress,nport);//连接服务器大于或等于0为成功  否则为失败
		if (retState >= 0)
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
						//Toast.makeText( MainActivity.this, "登录成功", Toast.LENGTH_LONG).show();
						android.util.Log.d("cjwsjy", "------登录成功-------7");

						//Intent intent = new Intent( ActivityZhibo.this, QyDeviceActivity.class);
						//startActivity(intent);

						//String strname = "1000000002001";
						Intent intent = new Intent(ActivityZhibo.this,QyVideoControlActivity.class);
						intent.putExtra("channelName", m_ChannelName );
						startActivity(intent);
					}
					else
					{
						m_progressDialog.cancel();
						//showToast("登录失败:"+String.valueOf(ret)+";或者ViewerLogin第一个或第二个参数错误");
						android.util.Log.d("cjwsjy", "------登录失败-------7");
						new AlertDialog.Builder(ActivityZhibo.this)
								.setTitle("提示")
								.setMessage("无法连接服务器")
								.setPositiveButton("确定", null)
								.show();
					}
				}
			});
		}
		else
		{
			m_progressDialog.cancel();
			//showToast("服务器连接失败:"+String.valueOf(retState));
		}*/
	}

	private void startWebView(String urlstr,String title)
	{
		Intent intent = new Intent();           	
		intent.setClass( ActivityZhibo.this, WebViewHome.class );
		intent.putExtra("webUrl",urlstr);
		intent.putExtra("titleName",title);
		startActivity(intent);
	}
	
	// 自动加载布局
	public LinearLayout initLayout() 
	{
		LinearLayout loadingLayout = null;

		return loadingLayout;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,long id) 
	{
		// TODO 自动生成的方法存根
	}

}
