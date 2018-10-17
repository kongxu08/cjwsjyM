package com.cjwsjy.app.main;

import java.io.File;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cjwsjy.app.ActivityLogin2;
import com.cjwsjy.app.R;
import com.cjwsjy.app.SmApplication;
import com.cjwsjy.app.dial.DialFragment;
import com.cjwsjy.app.custom.CustomDialog;
import com.cjwsjy.app.homeFragment.FragmentHome;
import com.cjwsjy.app.imagecache.ImageGazerUtil;
import com.cjwsjy.app.imagecache.LoaderImpl;
import com.cjwsjy.app.phonebook.FragmentEmployeeInfo;
import com.cjwsjy.app.phonebook.FragmentPhoneBook;
import com.cjwsjy.app.plate.FragmentPlate;
import com.cjwsjy.app.resideMenu.ResideMenu;
import com.cjwsjy.app.resideMenu.ResideMenuItem;
import com.cjwsjy.app.scanning.CaptureActivity;
import com.cjwsjy.app.utils.HttpClientUtil;
import com.cjwsjy.app.utils.StringHelper;
import com.cjwsjy.app.utils.ThreadUtils;
import com.cjwsjy.app.utils.UpdateManager;
import com.cjwsjy.app.utils.UrlUtil;

import com.do1.cjmobileoa.db.DBManager;
import com.do1.cjmobileoa.db.GetServiceData;
import com.do1.cjmobileoa.db.model.EmployeeVO;


public class MainActivity extends FragmentActivity implements OnClickListener
{
	private TextView[] textviews;
	
	//标题栏
	private TextView textview;
	
	private ImageView iv_add;
	private ImageView iv_back;
	private ImageView[] imagebuttons;

	private FragmentPhoneBook fragmentPB;
	private FragmentEmployeeInfo fragmentEI;
	private DialFragment DialFragment;
	private FragmentHome fragmentHome;

	private int nLoaderImpl;
	//用户头像下载是否成功
	private int bmpuser;

	private int m_contacts = 0;
	private int mark = 0;
	//标记是智能拨号到详细界面，还是从通讯录界面到详细界面
	private int phoneinfo = 0;
	//是否弹出Toast,提示当前是最新版
	private int m_sign = 0;

	private int m_MouthDay = 0;

	// fragment的index
	private int index;
	private int currentTabIndex;
	
	private static FragmentManager fMgr;
	private DBManager dbManager;
	private ProgressDialog mDialog;

    private String m_text = "";
	private String m_userid = "";
	private String m_loginName = "";
	private String m_DisplayName;
	private String m_jobnumber = "";
	private String m_model = "";
	private String m_operate = "";
	private String[] titles;
	private String[] fragmentsName;
	private Fragment[] fragments;
	
	private SharedPreferences sp;
	
	private int create = 0;
	public static int flag =-1;
    public static boolean isForeground = false;
    
    private String appUrl;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//setContentView(R.layout.activity_main);
		setContentView(R.layout.activity_main_material);
		
		//注册广播
		IntentFilter ifilter = new IntentFilter( Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        registerReceiver(mHomeKeyEventReceiver, ifilter );

		getAndroiodScreenProperty();
	}

	@Override
	protected void onStart()
	{
		// TODO 自动生成的方法存根
		super.onStart();

		bmpuser = 0;
		m_contacts = 0;
		nLoaderImpl = 0;
		m_MouthDay = 0;

		if(create==0)
		{
			android.util.Log.i("cjwsjy", "--------onStart-------main");

			sp = SmApplication.sp;
			m_MouthDay = sp.getInt("DATA_MOUTHDAY", 0);
			m_userid = sp.getString("USERDATA.USER.ID", "");
			m_loginName = sp.getString("USERDATA.LOGIN.NAME", "");
			m_DisplayName = sp.getString("USERDATA.DISPLAY.NAME", "");
			m_jobnumber = sp.getString("USERDATA.USER.JOBNUMBER", "");

			dbManager = SmApplication.dbManager;
			appUrl = UrlUtil.HOST;
			
			iv_back = (ImageView) this.findViewById(R.id.iv_back);
			iv_add = (ImageView) this.findViewById(R.id.iv_add);

			if(m_userid.length()==0)
			{
				android.util.Log.i("cjwsjy", "------userid=null-------MainActivity");

				EmployeeVO data;
				data = dbManager.getEmployeeByName(m_loginName);
				m_userid = data.getUserid();

				if(m_jobnumber.length()==0) m_jobnumber = data.getjobNumber();

				Editor editor = sp.edit();
				editor.putString("USERDATA.USER.ID", m_userid);
				editor.putString("USERDATA.USER.JOBNUMBER", m_jobnumber);
				editor.commit();
			}

			//String mes = "---user="+m_userid+"----";
			//Toast.makeText( MainActivity.this, mes, Toast.LENGTH_SHORT).show();

			// 侧滑
			setUpMenu();

			fragmentHome = new FragmentHome();
			DialFragment = new DialFragment();
			fragmentPB = new FragmentPhoneBook();
			fragmentEI = new FragmentEmployeeInfo();

            fragmentHome.Getmainactivity(MainActivity.this);
			DialFragment.setmainactivity(MainActivity.this);
			fragmentPB.getmainactivity(MainActivity.this);
			
			//初始化标题栏
			initTitle();

			//初始化底部tab控件
			initTab();
			
			fMgr = getSupportFragmentManager();
			
			textview = (TextView)this.findViewById(R.id.tv_title2);
			
			fragmentPB.settextview(textview);
			create++;

			//开线程，检查版本升级，下载用户头像
	        Thread upgradeThread = new Thread(new ThreadUpgrade());
	        upgradeThread.start();

			String versionapp = SmApplication.sharedProxy.getString("curVersion", "1.0");

			String model = Build.MODEL;//手机型号
			int version = Build.VERSION.SDK_INT;//SDK版本号
			String versionos = Build.VERSION.RELEASE;//Firmware/OS 版本号

			//String model2 = model.replaceAll( " ", "%20");
			String model2 = model.replaceAll( " ", "_");
			String strbuf = "Android-"+versionos+"-"+model2+"-"+versionapp;

			//把手机信息给后台
			PostInformation3(model2,"android-"+versionos,versionapp);
		}
	}

	//底部tab栏的适配
	public void getAndroiodScreenProperty()
	{
		int realHeight = 0;

		DisplayMetrics dm = new DisplayMetrics();
		WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		display.getMetrics(dm);

		int width = dm.widthPixels;// 屏幕宽度（像素）
		int height= dm.heightPixels; // 屏幕高度（像素）
		float density = dm.density;//屏幕密度（0.75 / 1.0 / 1.5）
		int densityDpi = dm.densityDpi;//屏幕密度dpi（120 / 160 / 240）

		//屏幕宽度算法:屏幕宽度（像素）/屏幕密度
		int screenWidth = (int) (width/density);//屏幕宽度(dp)
		int screenHeight = (int)(height/density);//屏幕高度(dp)
		//Log.e("123", screenWidth + "======" + screenHeight);

		Point realSize = new Point();
		try
		{
			Display.class.getMethod("getRealSize", Point.class).invoke(display, realSize);
			realHeight = realSize.y;
		}
		catch (Exception e)
		{
		}

//		if( height!=realHeight )
//		{
//			height = realHeight;
//		}

		int nheight = 0;
		int nheight2;
		LinearLayout LinearL;
		LinearLayout.LayoutParams lineparams;

		//标题栏是46dp，底下的tab栏也是46dp，再加状态栏，估算了一个22dp
		nheight2 = (int)(46*density*2+22*density);
		//nheight2 = (int)(46*density*2);
		nheight = height-nheight2;

		LinearL = (LinearLayout)findViewById(R.id.fragment_container2);
		lineparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, nheight);
		LinearL.setLayoutParams(lineparams);
	}

	/** 
     * 监听是否点击了home键将客户端推到后台 
     */  
    private BroadcastReceiver mHomeKeyEventReceiver = new BroadcastReceiver() 
    {
        String SYSTEM_REASON = "reason";  
        String SYSTEM_HOME_KEY = "homekey";
        String SYSTEM_RECENT_APP = "recentapps";  
           
        @Override  
        public void onReceive(Context context, Intent intent) 
        {
            String action = intent.getAction();  
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) 
            {
                String reason = intent.getStringExtra(SYSTEM_REASON);  
                if( reason.equals(SYSTEM_HOME_KEY) )
                {
                     //表示按了home键,程序到了后台  
                    //android.util.Log.d("cjwsjy", "------HOME_KEY-------");
                    SmApplication.sethomekey(1,2);
                }
                else if ( reason.equals( SYSTEM_RECENT_APP) )
                {
                	//android.util.Log.d("cjwsjy", "------RECENT_APP-------");
                    //表示长按home键,显示最近使用的程序列表  
                }
            }   
        }  
    };
    
	// LoginThread线程类
	/*class ThreadLog implements Runnable
	{
		@Override
		public void run()
		{
			SaveLog();
		}
	}*/
	
	//更新版本
	class ThreadUpgrade implements Runnable
	{
		@Override
		public void run()
		{
			boolean bresult1 = false;
            boolean bresult2 = false;
            boolean bresult3 = false;
            int i = 0;
            int j = 0;
			int result = 0;
            int length = 0;
			String strings;
			String newversion = "";
			String tagstr = "";
			String apkurl = "";
			String oldversion;
			String url;
            String key;
			String resultStr;
			String jsonstr;
            String text1 = "";
			String text2 = "";
			String versionold = "";
			String versionnew = "";
			JSONArray jsonArray;
			JSONObject jsonObject;

			//下载用户头像
			DownImg();

            //Message msg = downHandler.obtainMessage();
			Map<String, String> map = new HashMap<String, String>();
			map.put("versionCode", "2.3.4");  //该字段没有用

            //获取旧版本号
            oldversion = SmApplication.sharedProxy.getString("curVersion", "1.0");

            //请求软件版本
            url = appUrl+"/CEGWAPServer/checkAndoridVersion2";
			resultStr = HttpClientUtil.HttpUrlConnectionPost3(url, map,"UTF-8");

			//请求通讯录版本
			url = appUrl+"/CEGWAPServer/TXL/getTXLCode";
			versionnew = HttpClientUtil.HttpUrlConnectionGet(url, HttpClientUtil.DEFAULTENC);

			//获得本地通讯录的版本
			versionold = sp.getString("PHONE_BOOK_VERSION", "");
			//版本号是否为空
			bresult1 = versionold.equals("");
			//比较通讯录版本
			bresult2 = versionold.equals(versionnew);
			//通讯录更新没有完成
			bresult3 = sp.getBoolean("SERVICE_INIT_DATA", false);

			//第一次直接更新通讯录
			if( bresult1==true ) //m_contactsnull = 1;

			if( versionold.length()==0) m_contacts = 1;

			//if( true )
			if( bresult1==true || bresult2==false || bresult3==false)
			{
				//版本数据为空，表示是第一次，直接下载数据
				//版本号不一样，表示有新数据，更新数据
				//数据库初始化失败，重新下载数据
				//更新;
				m_contacts = 1;
			}

            if(resultStr==null) return;
            if(resultStr.length()==0) return;
            m_text = "";
            j = 1;

			//oldversion,newversion
			try
			{
                Message msg;
                jsonArray = new JSONArray(resultStr);
                length = jsonArray.length();
                for( i=0; i<length; i++ )
                {
                    jsonObject = jsonArray.getJSONObject(i);

                    Iterator<String> iterator = jsonObject.keys();
                    while (iterator.hasNext())
                    {
                        key = (String) iterator.next();
                        strings = jsonObject.getString(key);
                        result = 1;

                        bresult1 = key.equals("version");
                        bresult2 = key.equals("tag");
                        bresult3 = key.equals("APK_url");
                        if( bresult1==true )
                        {
                            newversion = strings;
                        }
                        else if( bresult2==true )
                        {
                            tagstr = strings;
                        }
                        else if( bresult3==true )
                        {
                            apkurl = strings;
                        }
                        else
                        {
							//text2 = text2+j+"."+strings+"\n";
                            m_text = m_text+j+"."+strings+"\n";
                            j++;
                        }
                    }
                }

                //比较软件版本
				//从登录进来的更新，不提示当前是最新版
				//从侧滑进来的更新，提示当前是最新版
                bresult1 = oldversion.equals(newversion);
                if( bresult1==true )
                {
                	//软件不升级
					//通讯录更新提示，首页进来的，一天只提示一次，侧滑进来的，每次提示
					if (m_contacts==1)
					{
						if(m_MouthDay==2)
						{
							//一天只提示一次，提示更新通讯录
							msg = downHandler.obtainMessage();
							msg.what = 4;
							msg.obj = apkurl;
							downHandler.SetText(m_text);
							downHandler.sendMessage(msg);
							return;
						}
						if(m_sign==1)
						{
							//侧滑进来的，每次提示更新通讯录
							msg = downHandler.obtainMessage();
							msg.what = 4;
							msg.obj = apkurl;
							downHandler.SetText(m_text);
							downHandler.sendMessage(msg);
							return;
						}
					}

					//提示当前是最新版
					msg = downHandler.obtainMessage();
					msg.what = 2;
					msg.obj = apkurl;
					downHandler.SetText(m_text);
					downHandler.sendMessage(msg);
					return;
				}

				//软件要升级
                //是否必须升级
                if( tagstr.toLowerCase().equals("yes") )
                {
                    //必须升级
                    msg = downHandler.obtainMessage();
                    msg.what = 0;
                    msg.obj = apkurl;
					downHandler.SetText(m_text);
                    downHandler.sendMessage(msg);
                }
                else
                {
                    //选择升级
                    msg = downHandler.obtainMessage();
                    msg.what = 1;
                    msg.obj = apkurl;
					downHandler.SetText(m_text);
                    downHandler.sendMessage(msg);
                }
			}
			catch (JSONException e)
			{
				e.printStackTrace();
			}
		}
	}

	private static class MyHandler extends Handler
	{
		String m_text;

		private final WeakReference<MainActivity> mActivity;

		public MyHandler(MainActivity activity) {
			mActivity = new WeakReference<MainActivity>(activity);
		}

		public void SetText(String strtext)
		{
			m_text = strtext;
		}

		@Override
		public void handleMessage(Message msg)
		{
			MainActivity activity = mActivity.get();
			if (activity != null)
			{
				UpdateManager upManager;

				switch( msg.what )
				{
					case 0:
						//必须升级
						upManager = new UpdateManager( activity, msg.obj.toString(),m_text );
						upManager.GetMainactivity(activity);
						upManager.checkUpdateInfo2();
						break;
					case 1:
						//选择升级
						upManager = new UpdateManager( activity, msg.obj.toString(),m_text );
						upManager.GetMainactivity(activity);
						upManager.checkUpdateInfo();
						break;
					case 2:
						//提示当前APP是最新版
						if(activity.m_sign==1) Toast.makeText( activity, "当前已经是最新版本", Toast.LENGTH_SHORT).show();
						break;
					case 3:
						//更新完成
						upManager = new UpdateManager( activity, msg.obj.toString(),m_text );
						upManager.checkUpdateInfo3();
						break;
					case 4:
						//更新通讯录
						activity.ShowDialogPhonebook();
						break;
				}
			}
		}
	}

	private final MyHandler downHandler = new MyHandler(this);

/*	Handler downHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg) 
		{
			UpdateManager upManager;
			
			switch( msg.what ) 
			{
			case 0:
				//必须升级
				upManager = new UpdateManager( MainActivity.this, msg.obj.toString(),m_text );
				upManager.GetMainactivity(MainActivity.this);
				upManager.checkUpdateInfo2();
				break;
			case 1:
				//选择升级
				upManager = new UpdateManager( MainActivity.this, msg.obj.toString(),m_text );
				upManager.GetMainactivity(MainActivity.this);
				upManager.checkUpdateInfo();
				break;
			case 2:
				//当前是最新版
				Toast.makeText( MainActivity.this, "当前已经是最新版本", Toast.LENGTH_SHORT).show();
				break;
			case 3:
				//更新完成
				upManager = new UpdateManager( MainActivity.this, msg.obj.toString(),m_text );
				upManager.checkUpdateInfo3();
				break;
			}
		}
	};*/
	
	@Override
	protected void onResume() 
	{
		super.onResume();
		
		int home = 0;
		home = SmApplication.gethomekey();

		if(home==1)
		{
			Intent intent = new Intent(this, ActivityLogin2.class);
			startActivity(intent);
		}

		
	}

	@Override
	protected void onPause() 
	{
	    super.onPause();
	    //android.util.Log.d("cjwsjy", "--------onPause-------main");
	}
	
	@Override
	protected void onDestroy() 
	{
		//unregisterReceiver(mMessageReceiver);
		super.onDestroy();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) 
		{
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void initTitle()
	{
		// 后退
		iv_back.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				//Toast.makeText(getApplicationContext(), "后退",Toast.LENGTH_SHORT).show();
				//MainActivity.this.onBackPressed();
				onBackClick();
				//showWeChatTitleDialog(MainActivity.this);
				//showAlertDialog();
			}
		});

		// 搜索
//		iv_search.setOnClickListener(new OnClickListener()
//		{
//			@Override
//			public void onClick(View v)
//			{
//				Toast.makeText(getApplicationContext(), "搜索",Toast.LENGTH_SHORT).show();
//			}
//		});

		// 菜单
	}

	private int PostInformation3(String str_machine, String str_os, String str_appversion)
	{
		int result = 0;
		long time = 0;
		String url = "";
		String strtime = "";
		String jsonstr = "";
		String appUrl = "";
		String str_createDate = "";
		JSONArray jsonArray;
		JSONObject jsonObject;

		appUrl = UrlUtil.HOST;

		jsonArray = new JSONArray();
		jsonObject = new JSONObject();
		time = System.currentTimeMillis();
		strtime = Long.toString(time);

		Calendar c = Calendar.getInstance();
		int mYear = c.get(Calendar.YEAR); //获取当前年份
		int mMonth = c.get(Calendar.MONTH)+1;//获取当前月份
		int mDay = c.get(Calendar.DAY_OF_MONTH);//获取当前月份的日期号码

		str_createDate = mYear+"-"+mMonth+"-"+mDay;

		String android_id = Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID);

		EmployeeVO data;
		data = dbManager.getEmployeeByName(m_loginName);

		//用户名 汉字
		String m_displayname = data.getUserDisplayName();

		String m_str_deviceToken = SmApplication.GetDeviceToken();

		try
		{
			jsonObject.put("userId", m_userid);
			jsonObject.put("userName", m_loginName);
			jsonObject.put("userDisplayName", m_displayname);
			jsonObject.put("devicesId", m_str_deviceToken);
			jsonObject.put("devicesType", "android");
			jsonObject.put("createDate", str_createDate);
			jsonObject.put("machine", str_machine);
			jsonObject.put("os", str_os);
			jsonObject.put("appversion", str_appversion);

			jsonstr = jsonObject.toString();

			url = appUrl+"/CEGWAPServer/AppPushController/userDevices";

			HttpClientUtil.HttpUrlConnectionPostLog(url, jsonstr);
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}

		return 1;
	}

	private void initTab()
    {
		//首页，拨号，通讯录，应用
		fragments = new Fragment[] { fragmentHome,DialFragment, fragmentPB, new FragmentPlate(),fragmentEI };  
        fragmentsName=new String[] {"FragmentHome","DialFragment","FragmentPhoneBook","FragmentPlate","FragmentEmployeeInfo"};
        titles = new String[] {"首页","智能拨号","通讯录","应用"};
        
        imagebuttons = new ImageView[4];
        imagebuttons[0] = (ImageView) findViewById(R.id.ib_weixin);
        imagebuttons[1] = (ImageView) findViewById(R.id.ib_contact_list);
        imagebuttons[2] = (ImageView) findViewById(R.id.ib_find);
        imagebuttons[3] = (ImageView) findViewById(R.id.ib_profile);
        imagebuttons[0].setSelected(true);
        
        textviews = new TextView[4];
        textviews[0] = (TextView) findViewById(R.id.tv_weixin);
        textviews[1] = (TextView) findViewById(R.id.tv_contact_list);
        textviews[2] = (TextView) findViewById(R.id.tv_find);
        textviews[3] = (TextView) findViewById(R.id.tv_profile);
        textviews[0].setTextColor(0xFF2A6FBA);
        
        // 添加显示第一个fragment
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, fragments[0],fragmentsName[0])
                .add(R.id.fragment_container, fragments[1],fragmentsName[1])
                .add(R.id.fragment_container, fragments[2],fragmentsName[2])
                .add(R.id.fragment_container, fragments[3],fragmentsName[3])
                .add(R.id.fragment_container, fragments[4],fragmentsName[4])
                .addToBackStack(fragmentsName[1])
                .hide(fragments[1])
                .addToBackStack(fragmentsName[2])
                .hide(fragments[2])
                .addToBackStack(fragmentsName[3])
                .hide(fragments[3])
                .addToBackStack(fragmentsName[4])
                .hide(fragments[4])
                .show(fragments[0]).commit();
        
        //imagebuttons[0].setSelected(true);
    }

	//底部tab单击响应
	public void onTabClicked(View view) 
    {
		//requestDrawOverLays();

		switch (view.getId())
        {
        case R.id.re_weixin:
            index = 0;
            break;
        case R.id.re_contact_list:
            index = 1;
            break;
        case R.id.re_find:
            index = 2;
            break;
        case R.id.re_profile:
            index = 3;
            break;
        }
		
		onTab(index);
    }

	public void onTab(int  indexIn) 
    {
		if (currentTabIndex != indexIn) 
        {
			FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
			// trx.hide(fragments[currentTabIndex]);

			for (Fragment f : fMgr.getFragments()) 
			{
				if (f != null) 
				{
					if (f.isVisible()) 
					{
						trx.hide(f);
					}
				}
			}

			if (!fragments[indexIn].isAdded()) 
			{
				trx.add(R.id.fragment_container, fragments[indexIn]);
			}
			trx.addToBackStack(fragmentsName[indexIn]);
			trx.show(fragments[indexIn]).commit();
			
			//设置标题栏
			textview.setText(titles[indexIn]);
        }
		
		imagebuttons[currentTabIndex].setSelected(false);
        // 把当前tab设为选中状态
        imagebuttons[indexIn].setSelected(true);
    
        textviews[currentTabIndex].setTextColor(0xFF999999);
        textviews[indexIn].setTextColor(0xFF2A6FBA);
        currentTabIndex = indexIn;
        
        ThreadUtils emperor1 = ThreadUtils.getInstance();  // 得到单例对象

        //写入日志
        //首页
        if(indexIn==0)
        {
        	//刷新待办消息
        	fragmentHome.ThreadRefresh(2);
			
			emperor1.setparm2("点击","首页","首页-tab");
	        emperor1.writelog();
        }
        
        //智能拨号
        if(indexIn==1)
        {
			emperor1.setparm2("点击","智能拨号","智能拨号-tab");
	        emperor1.writelog();
        }
        
        //通讯录
        if(indexIn==2)
        {
			emperor1.setparm2("点击","通讯录","通讯录-tab");
	        emperor1.writelog();
        }
        
        //应用
        if(indexIn==3)
        {
			emperor1.setparm2("点击","应用","应用-tab");
	        emperor1.writelog();
        }
    }
	
	public void onBackClick()
	{
		boolean results = false;
		int nresult = 0;
		Fragment fragm1;
		Fragment fragm2;
		Fragment fragm3;
		Fragment fragm4;
		Fragment fragm5;
		
		fragm1 = fMgr.findFragmentByTag("FragmentHome");  //首页
		fragm2 = fMgr.findFragmentByTag("DialFragment");  //智能拨号
		fragm3 = fMgr.findFragmentByTag("FragmentPhoneBook");  //通讯录
		fragm4 = fMgr.findFragmentByTag("FragmentPlate");  //应用
		fragm5 = fMgr.findFragmentByTag("FragmentEmployeeInfo");  //通讯录员工详细页面
		
		//首页，搜索，历史，弹出退出确认对话框
		results = fragm1.isHidden();
		results = fragm1.isVisible();
		if( fragm1!=null && fragm1.isVisible() )
		{
			//showExitAlert();
			showExitDialog();
		}
		else if( (fragm2.isHidden()==false) && fragm2.isVisible() )  //智能拨号
		{
			showExitDialog();
		}
		else if( (fragm4.isHidden()==false) && fragm4.isVisible() )  //应用
		{
			showExitDialog();
		}
		else  if( (fragm5.isHidden()==false) && fragm5.isVisible() )  //通讯录员工详细页面
		{
			FragmentTransaction trx = getSupportFragmentManager().beginTransaction();

			for (Fragment f : fMgr.getFragments()) 
			{
				if (f != null) 
				{
					if (f.isVisible()) 
					{
						trx.hide(f);
					}
				}
			}
			
			if( phoneinfo==1 )
			{
				if (!fragments[1].isAdded()) 
				{
					trx.add(R.id.fragment_container, fragments[1]);
				}
				trx.addToBackStack(fragmentsName[1]);
				trx.show(fragments[1]).commit();
			}
			else if( phoneinfo==2 )
			{
				//从收藏细览回退到通讯里刷新收藏列表
				if("1".equals(fragmentPB.collectFlag))
				{
					fragmentPB.getCollectList();
				}
				
				if (!fragments[2].isAdded()) 
				{
					trx.add(R.id.fragment_container, fragments[2]);
				}
				trx.addToBackStack(fragmentsName[2]);
				trx.show(fragments[2]).commit();
			}
		}
		else   //通讯录界面
		{
			//只有在搜索栏没值，当前页是一级界面时，才弹出退出框
			//回退的顺序，搜索栏先清空，界面再一级一级回退到顶层页面
			nresult = fragmentPB.goback();
			if(nresult==101)
			{
				showExitDialog();
			}
		}
	}
	
	//系统按钮，“回退”键
	@Override
	public void onBackPressed()
	{
		//super.onBackPressed();
		boolean bresult = false;
		bresult = resideMenu.isOpened();
		
		if( bresult==true )  
		{
			resideMenu.closeMenu();
		}
		else onBackClick();
	}

	/*public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		int num = 0;
		num = 2;
		if (keyCode == event.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN)
		{
			// TODO
			return true;
		}

		return super.onKeyDown(keyCode, event);
		//return false; //截断
	}*/

	public void showExitDialog() 
	{
		CustomDialog.Builder builder = new CustomDialog.Builder(this);
		builder.setMessage("确定退出吗？");
		builder.setTitle("退出");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int which) 
			{
				dialog.dismiss();
				//设置你的操作事项

				Thread threads = new Thread(new ThreadRemoveDevices());
				threads.start();

				MainActivity.this.finish();
				System.exit(0);
			}
		});

		builder.setNegativeButton("取消",new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which) 
			{
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

	private int PostInformationExit(String str_machine, String str_os, String str_appversion)
	{
		int result = 0;
		long time = 0;
		String url = "";
		String strtime = "";
		String jsonstr = "";
		String appUrl = "";
		String str_createDate = "";
		JSONArray jsonArray;
		JSONObject jsonObject;

		appUrl = UrlUtil.HOST;

		jsonArray = new JSONArray();
		jsonObject = new JSONObject();
		time = System.currentTimeMillis();
		strtime = Long.toString(time);

		Calendar c = Calendar.getInstance();
		int mYear = c.get(Calendar.YEAR); //获取当前年份
		int mMonth = c.get(Calendar.MONTH);//获取当前月份
		int mDay = c.get(Calendar.DAY_OF_MONTH);//获取当前月份的日期号码

		str_createDate = mYear+"-"+mMonth+"-"+mDay;

		try
		{
			jsonObject.put("userName", m_loginName);
			jsonObject.put("devicesType", "android");

			jsonstr = jsonObject.toString();

			url = appUrl+"/CEGWAPServer/AppPushController/removeDevices";
			HttpClientUtil.HttpUrlConnectionPostLog(url, jsonstr);
		}
		catch (JSONException e) {
			e.printStackTrace();
		}

		return 1;
	}

	public void showWeChatTitleDialog(Context context)
	{
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        dialogBuilder.create();
        
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_custom, null);
        
        dialogBuilder.setView(view);
        dialogBuilder.show() ;
	}
	
    /**
	 * fragment跳转
	 */
	public static void changeFrament(String hidFragment, Fragment fragment,
			String fragmentName, Integer btnId) 
	{
		FragmentTransaction ft = fMgr.beginTransaction();
		if (StringHelper.isEmpty(hidFragment)) 
		{
			popAllFragmentsExceptTheBottomOne();
			ft.hide(fMgr.findFragmentByTag("FragmentHome"));
		} 
		else 
		{
			ft.hide(fMgr.findFragmentByTag(hidFragment));
		}
		ft.add(R.id.fragment_container, fragment, fragmentName);
		ft.addToBackStack(fragmentName);
		ft.commit();
	}

	/**
	 * 从back stack弹出所有的fragment，保留首页的那个
	 */
	public static void popAllFragmentsExceptTheBottomOne() {
		for (int i = 0, count = fMgr.getBackStackEntryCount() - 1; i < count; i++) {
			fMgr.popBackStack();
		}
	}
	

	//开放外部接口，显示员工详细界面
	public void showEmployee(EmployeeVO ep, int info)
	{
		ThreadUtils emperor1 = ThreadUtils.getInstance();  // 得到单例对象

		if( info==1 )
		{
			emperor1.setparm2("点击", "员工详细", "从拨号进入");
			emperor1.writelog();
		}
		else if( info==2 )
		{
			emperor1.setparm2("点击", "员工详细", "从通讯录进入");
			emperor1.writelog();
		}

		phoneinfo = info;
		//设置标题栏
		textview.setText("联系人详情");
		
		fragmentEI.setEmployeeVO(ep);
		
		FragmentTransaction trx = getSupportFragmentManager().beginTransaction();	
		for (Fragment f : fMgr.getFragments()) 
		{
			if (f != null) 
			{
				if (f.isVisible()) 
				{
					trx.hide(f);
				}
			}
		}

		if (!fragments[4].isAdded()) 
		{
			trx.add(R.id.fragment_container, fragments[4]);
		}
		trx.addToBackStack(fragmentsName[4]);
		trx.show(fragments[4]).commit();
	}

	public String getFilePath(Context context, String dir)
	{
		String sdpath;
		String directoryPath="";

		sdpath = Environment.getExternalStorageState();
		if (sdpath.equals(Environment.MEDIA_MOUNTED) )
		{//SD卡可用
			directoryPath =context.getExternalFilesDir(dir).getAbsolutePath();
		}
		else
		{//没外部存储就使用内部存储
			directoryPath=context.getFilesDir()+ File.separator+dir;
		}

        Editor editor = sp.edit();
        editor.putString("SD_Path", directoryPath);
        editor.commit();

		File file = new File(directoryPath);
		if(!file.exists())
		{//判断文件目录是否存在
			file.mkdirs();
		}

		return directoryPath;
	}

	//for receive customer msg from jpush server
	private MessageReceiver mMessageReceiver;
	public static final String MESSAGE_RECEIVED_ACTION = "com.cjwsjy.app.MESSAGE_RECEIVED_ACTION";
	public static final String KEY_TITLE = "title";
	public static final String KEY_MESSAGE = "message";
	public static final String KEY_EXTRAS = "extras";
	
	public void registerMessageReceiver() 
	{
		mMessageReceiver = new MessageReceiver();
		IntentFilter filter = new IntentFilter();
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		filter.addAction(MESSAGE_RECEIVED_ACTION);
		registerReceiver(mMessageReceiver, filter);
	}

	public class MessageReceiver extends BroadcastReceiver 
	{
		@Override
		public void onReceive(Context context, Intent intent) 
		{
			if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) 
			{
              String messge = intent.getStringExtra(KEY_MESSAGE);
              String extras = intent.getStringExtra(KEY_EXTRAS);
              StringBuilder showMsg = new StringBuilder();
              showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
              if (!StringHelper.isEmpty(extras)) 
              {
            	  showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
              }
              setCostomMsg(showMsg.toString());
			}
		}
	}
	
	private void setCostomMsg(String msg)
	{
//		 if (null != msgText) 
//		 {
//			 msgText.setText(msg);
//			 msgText.setVisibility(android.view.View.VISIBLE);
//         }
	}
	
	// 侧滑
		private ResideMenu resideMenu;
		private ResideMenuItem itemHome;
		private ResideMenuItem itemProfile;
		private ResideMenuItem itemCalendar;
		private ResideMenuItem itemphone;
		private ResideMenuItem itemSettings;
		protected Bitmap bmp_UserPhoto;
		
		private  LoaderImpl impl;
		private Map<String,SoftReference<Bitmap>> sImageCache; 
		
		// 侧滑
		private void setUpMenu() 
		{
			// 获取当前登录用户登录名
			String userDisplayName = m_DisplayName;
			String jobNumber = m_jobnumber;
			
			//用户头像下载地址
			String resultStr = "";
			String photoUrl = "";
			
			//photoUrl = "http://app.cjwsjy.com.cn/UploadedFiles/Framework.Web/"+ loginname + ".jpg";
			photoUrl = UrlUtil.USER_IMAGE+"/"+m_loginName;
			//resultStr = HttpClientUtil.sendRequestFromHttpClientString2(photoUrl, null, "UTF-8");
			
			//初始化侧边栏
			resideMenu = new ResideMenu(this, bmp_UserPhoto, userDisplayName,jobNumber);
			resideMenu.setBackground(R.drawable.menu_01);
			resideMenu.attachToActivity(this);
			resideMenu.setMenuListener(menuListener);
			resideMenu.setScaleValue(0.6f);
			
			itemProfile = new ResideMenuItem(this, R.drawable.img_slider_help, "帮助");
			itemCalendar = new ResideMenuItem(this, R.drawable.img_slider_aboutus,"更新");
			itemphone = new ResideMenuItem(this, R.drawable.img_slider_aboutus,"更新通讯录");
			itemSettings = new ResideMenuItem(this, R.drawable.img_slider_exit,"退出");

			itemProfile.setOnClickListener(this);
			itemCalendar.setOnClickListener(this);
			itemphone.setOnClickListener(this);
			itemSettings.setOnClickListener(this);

			resideMenu.addMenuItem(itemProfile, ResideMenu.DIRECTION_RIGHT);
			resideMenu.addMenuItem(itemCalendar, ResideMenu.DIRECTION_RIGHT);
			//resideMenu.addMenuItem(itemphone, ResideMenu.DIRECTION_RIGHT);
			resideMenu.addMenuItem(itemSettings, ResideMenu.DIRECTION_RIGHT);
			//绑定右上角图标单击事件滑出侧边栏
			iv_add.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View view) 
				{
					//线程下载用户头像
					Thread thread = new Thread(new ThreadDonwImg());
					thread.start();

					resideMenu.openMenu(ResideMenu.DIRECTION_RIGHT,3);
				}
			});
		}
		
		private Bitmap getimageforurl(String strUrl)
		{
			String resultStr = null;
			Bitmap bitmap = null;
			
			if( Build.VERSION.SDK_INT>=23 )
			{
				int Permission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
	            if( Permission!=PackageManager.PERMISSION_GRANTED )
	            {
	                ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
	                return bitmap;
	            }
			}

			getFilePath(MainActivity.this,"Download");

			bitmap = impl.getBitmap(strUrl);
			
			return bitmap;
		}
		
		// 侧滑
		// What good method is to access resideMenu？
		public ResideMenu getResideMenu() {
			return resideMenu;
		}

		// 侧滑
		private ResideMenu.OnMenuListener menuListener = new ResideMenu.OnMenuListener() {
			@Override
			public void openMenu() {
				// Toast.makeText(mContext, "Menu is opened!",
				// Toast.LENGTH_SHORT).show();
			}

			@Override
			public void closeMenu() {
				// Toast.makeText(mContext, "Menu is closed!",
				// Toast.LENGTH_SHORT).show();
			}
		};

		// 侧滑
		private void changeFragment(Fragment targetFragment) 
		{
//			resideMenu.clearIgnoredViewList();
//
//			getSupportFragmentManager().beginTransaction()
//					.replace(R.id.main_fragment, targetFragment, "fragment")
//					.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
//					.commit();
		}

		// 侧滑
		@Override
		public void onClick(View view) 
		{
			// TODO 自动生成的方法存根
			if (view == itemHome) 
			{
				// changeFragment(new HomeFragment());
			}
			else if (view == itemProfile) 
			{
				Toast.makeText(getApplicationContext(), "努力开发中...", Toast.LENGTH_SHORT).show();
				// changeFragment(new ProfileFragment());
			}
			else if (view == itemCalendar) 
			{
				//更新
				//开线程，版本升级
				m_sign = 1;
				Thread upgradeThread = new Thread(new ThreadUpgrade());
				upgradeThread.start();
			}
			else if (view == itemphone) 
			{
				//通讯录更新
				//开线程，版本升级
				//Toast.makeText(getApplicationContext(), "努力开发中...", Toast.LENGTH_SHORT).show();
				updataPhonebook();
			} 
			else if (view == itemSettings) 
			{
				showExitDialog();
				//Toast.makeText(getApplicationContext(), "注销", Toast.LENGTH_SHORT).show();
				// changeFragment(new SettingsFragment());
			}

			resideMenu.closeMenu();
		}

	private void ShowDialogPhonebook() {
		//选择是否更新通讯录
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("更新");
		builder.setMessage("通讯录有新版本，是否同步？");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				updataPhonebook();
			}
		});

		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				//m_finish_phone = 1;
			}
		});

		builder.create();
		builder.show();
	}

	private void updataPhonebook()
	{
		mDialog = new ProgressDialog(MainActivity.this);
		mDialog.setTitle("更新");
		mDialog.setMessage("正在更新通讯录，请稍后...");
		mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mDialog.setProgress(100);
		mDialog.setCancelable(false);  // 设置是否可以通过点击Back键取消
		mDialog.setCanceledOnTouchOutside(false);  // 设置在点击Dialog外是否取消Dialog进度条
		mDialog.show();

		UrlUtil.g_count = 0;

		Editor editor = sp.edit();
		editor.putBoolean("SERVICE_INIT_DATA", false);
		editor.commit();

		//线程下载通讯录
		Thread phone = new Thread(new ThreadPhone());
		phone.start();

		//线程刷新进度条
		Thread Progress = new Thread(new ThreadProgress());
		Progress.start();

	}
		
		//下载通讯录
		class ThreadPhone implements Runnable
		{
			@Override
			public void run()
			{
				Message msg;
				String url;
				String versionold = "";
				String versionnew = "";
				boolean bresult1;
				boolean bresult2;
				boolean bresult3;
				
				//获得服务器通讯录的版本
				url = appUrl+"/CEGWAPServer/TXL/getTXLCode";
				versionnew = HttpClientUtil.HttpUrlConnectionGet(url, HttpClientUtil.DEFAULTENC);
				
				//版本数据为空，表示是第一次，直接下载数据
				//版本号不一样，表示有新数据，更新数据
				//数据库初始化失败，重新下载数据
				//更新;
					
				Boolean suss = GetServiceData.fillAllServiceData(dbManager);
				if( suss )
				{
					//下载成功
					Editor editor = sp.edit();
					editor.putBoolean("SERVICE_INIT_DATA", true);
					editor.commit();
				}
				else
				{
					//下载失败，下载失败的处理暂时和成功一样，后面需要再修改
					Editor editor = sp.edit();
					editor.putBoolean("SERVICE_INIT_DATA", false);
					editor.commit();
				}

				//保存通讯录版本
				Editor editor = sp.edit();
				editor.putString("PHONE_BOOK_VERSION", versionnew);
				editor.commit();
				
//				mDialog.dismiss();
//				
//				msg = downHandler.obtainMessage();
//				msg.what = 3;
//				msg.obj = "123";
//				downHandler.sendMessage(msg);
			}
		}

	//退出时调接口，给后台
	class ThreadRemoveDevices implements Runnable
	{
		@Override
		public void run()
		{
			String m_versionapp = SmApplication.sharedProxy.getString("curVersion", "1.0");

			String model = Build.MODEL;//手机型号
			int version = Build.VERSION.SDK_INT;//SDK版本号
			String versionos = Build.VERSION.RELEASE;//Firmware/OS 版本号

			//sString model2 = model.replaceAll( " ", "%20");
			String model2 = model.replaceAll( " ", "_");
			String strbuf = "Android-"+versionos+"-"+model2+"-"+m_versionapp;

			//退出时调接口
			PostInformationExit(model2,versionos,m_versionapp);

		}
	}

		class ThreadProgress implements Runnable
		{
			@Override
			public void run()
			{
				Message msg = downHandler.obtainMessage();
                try
                {
                    while( UrlUtil.g_count<100 )
                    {
                        // 由线程来控制进度。
                    	mDialog.setProgress(UrlUtil.g_count);
                        Thread.sleep(100);
                    }
                    mDialog.dismiss();
                    
                    msg.what = 3;
					msg.obj = "123";
  					downHandler.sendMessage(msg);
  					
					return;
                }
                catch (InterruptedException e)
                {
                	e.printStackTrace();
                }
			}
		}

	class ThreadDonwImg implements Runnable
	{
		@Override
		public void run()
		{
			DownImg();
		}
	}

	private int DownImg()
	{
		try
		{
			String photoUrl;
			photoUrl = UrlUtil.USER_IMAGE+"/"+m_loginName;

			//判断用户头像图片是否下载成功
			if(bmpuser==1) return 1;

			//LoaderImpl对象只new一次
			if(nLoaderImpl==0)
			{
				sImageCache = new HashMap<String,SoftReference<Bitmap>>();
				impl = new LoaderImpl(sImageCache);
				nLoaderImpl = 1;
			}

			Bitmap bmp = null;
			bmp = getimageforurl(photoUrl);

			//方图片转圆图片
			bmp = ImageGazerUtil.createCircleImage(bmp);

			// 下载失败，设置默认图片
			if( bmp==null )
			{
				bmp = BitmapFactory.decodeResource(
						MainActivity.this.getResources(),
						R.drawable.menu_03);
				bmp = ImageGazerUtil.createCircleImage(bmp);
				bmp_UserPhoto = bmp;
			}
			else  //显示头像
			{
				bmp_UserPhoto = bmp;
				resideMenu.setPhoto(bmp);
				bmpuser = 1;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return 1;
	}

	@TargetApi(Build.VERSION_CODES.M)
	public void requestDrawOverLays()
	{
		if (!Settings.canDrawOverlays(MainActivity.this))
		{
			Dialog noticeDialog;

			AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
			builder.setTitle("提示");
			builder.setMessage("打开显示在其他应用之上的权限，现在去设置");
			builder.setPositiveButton("取消", new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					dialog.dismiss();
				}
			});

			builder.setNegativeButton("确定", new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					dialog.dismiss();
					Toast.makeText(MainActivity.this, "can not DrawOverlays", Toast.LENGTH_SHORT).show();
					Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + MainActivity.this.getPackageName()));
					startActivityForResult(intent, 1234);
				}
			});

			noticeDialog = builder.create();
			noticeDialog.show();
		}
		else
		{
			// Already hold the SYSTEM_ALERT_WINDOW permission, do addview or something.
		}
	}

	@TargetApi(Build.VERSION_CODES.M)
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (requestCode == 1234)
		{
			if (!Settings.canDrawOverlays(this))
			{
				// SYSTEM_ALERT_WINDOW permission not granted...
				Toast.makeText(this, "Permission Denieddd by user.Please Check it in Settings", Toast.LENGTH_SHORT).show();
			}
			else
			{
				Toast.makeText(this, "Permission Allowed", Toast.LENGTH_SHORT).show();
				// Already hold the SYSTEM_ALERT_WINDOW permission, do addview or something.
			}
		}
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
					Intent intent = new Intent( this, CaptureActivity.class);
					intent.putExtra("titleName","设备扫码");
					startActivity(intent);
				}
				else
				{
					// 拒绝
					Toast.makeText(this, "没有权限！", Toast.LENGTH_SHORT).show();
				}
				break;
			default:
				super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}
}



