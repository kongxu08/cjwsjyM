package com.cjwsjy.app.homeFragment;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cjwsjy.app.Canteen.ActivityCanteenMain;
import com.cjwsjy.app.R;
import com.cjwsjy.app.SmApplication;
import com.cjwsjy.app.imagecache.LoaderImpl;
import com.cjwsjy.app.item.FinishPeddingItem;
import com.cjwsjy.app.main.MainActivity;
import com.cjwsjy.app.meeting.ActivityMeeting;
import com.cjwsjy.app.news.ActivityNews;
import com.cjwsjy.app.outoffice.OutOfficeActivity;
import com.cjwsjy.app.pedding.PeddingActivity;
import com.cjwsjy.app.pedding.UnFinishPeddingListActivity;
import com.cjwsjy.app.scanning.CaptureActivity;
import com.cjwsjy.app.utils.HttpClientUtil;
import com.cjwsjy.app.utils.ThreadUtils;
import com.cjwsjy.app.utils.UrlUtil;
import com.cjwsjy.app.webview.WebViewBQ;
import com.cjwsjy.app.webview.WebViewCanteen;
import com.cjwsjy.app.webview.WebViewHome;
import com.cjwsjy.app.webview.WebViewHome2;
import com.cjwsjy.app.webview.WebViewHome3;
import com.cjwsjy.app.wholeally.activity.NetworkStateService;
import com.cjwsjy.app.wholeally.activity.QyDeviceActivity;
import com.sqk.GridView.Grid_Item;
import com.sqk.GridView.NewGridAdaper;

import com.wholeally.qysdk.QYSDK;
import com.wholeally.qysdk.QYSession;

import com.cjwsjy.app.wholeally.activity.NetworkStateService;
import com.cjwsjy.app.wholeally.activity.QyDeviceActivity;
import com.cjwsjy.app.wholeally.activity.QyVideoControlActivity;
import com.cjwsjy.app.wholeally.activity.QyVideoControlActivity2;


public class FragmentHome  extends Fragment
{
	private int retState;
	private int expand = 0;
	private int m_Leader = 0;
	private int m_User = 0;
	private int m_OutExtra = 0;
	private int m_ShiLian = 0;

	private Message msg;

	private ViewPager viewPager;
	private LinearLayout group;
	private View view1;
	private View view2;
	private View view3;
	private GridView gridView1;
	private GridView gridView2;
	private GridView gridView3;
	private ListView m_listview;
	private ImageView imageView;
	private ImageView imageViewMeeting;
	private ImageView image4;
	private ImageView[] imageViews;
	
	private AdaperDaiban adapterdaiban;
	private List<ItemDaiban> listItem1 = new ArrayList<ItemDaiban>();
	//private List<Grid_Item> lists;
	
	private HashMap<String, Integer> mHashMap;
	private List<Map.Entry<String, Integer>> mHashMapEntryList;
	
	private TextView tv_textall;
	private TextView[] tv_text;
	private TextView[] tv_count;

	private SharedPreferences sp;

	private String[] str_count;

	private String appUrl;
	private String m_urlCanteen;
	private String m_userId;
	private String m_loginname;
	private String m_jobnumber;
	private String m_model;
	private String m_operate;
	private String m_content;
    private String m_phone;
	private String m_zhibopic;
	private String m_dahuipic1;
	private String strbuf;
	private String m_year;
	private String m_yinzhang = "0";
	private String m_ChannelName = "";
	private String m_IP_in = "";
	private String m_Port_in = "";
	private String m_IP_out = "";
	private String m_Port_out = "";
	private String m_Auth = "";

	private ProgressDialog mDialog;

	//public static QYSession session;
	private ProgressDialog m_progressDialog;

	private MainActivity mainactivity;

	public static QYSession session1;
	public static QYSession session2;
	
	int RefreshMutex = 0;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) 
	{
		return inflater.inflate(R.layout.fragment_home2, container, false);
	}

	/* （非 Javadoc）
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) 
	{
		super.onActivityCreated(savedInstanceState);

		int ret;
		String isOpen = "";
		String isMeeting = "";
		String isZhibo = "";

		Bitmap bitmap = null;

		viewPager = (ViewPager)getView().findViewById(R.id.viewpager);
		group = (LinearLayout) getView().findViewById(R.id.viewGroup);
		//LayoutInflater flater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		//view1 = flater.inflate(R.layout.home_fragment_page1 ,null);
		//view2 = flater.inflate(R.layout.home_fragment_page2 ,null);
		
		//gridView1 = (GridView) view1.findViewById(R.id.main_grid2);
		//gridView2 = (GridView) view2.findViewById(R.id.main_grid3);
		
		gridView3 = (GridView) getView().findViewById(R.id.grid_plate);
		
		appUrl = UrlUtil.HOST;
		m_urlCanteen = UrlUtil.urlCanteen;

        String m_versionapp = SmApplication.sharedProxy.getString("curVersion", "1.0");  //APP版本
        String model = Build.MODEL;//手机型号
        String versionos = Build.VERSION.RELEASE;  // Android 版本号

        //String model2 = model.replaceAll( " ", "%20");
        m_phone = "-Android-"+versionos+"-"+model+"-"+m_versionapp;

		//获取用户登录名
		sp = SmApplication.sp;
		m_userId = sp.getString("USERDATA.USER.ID", "");
		m_loginname =sp.getString("USERDATA.LOGIN.NAME", "");
		m_jobnumber = sp.getString("USERDATA.USER.JOBNUMBER", "");
		isOpen = sp.getString("isOpen", "");
		isMeeting = sp.getString("isMeeting", "");
		isZhibo = sp.getString("isZhibo", "");
		m_ChannelName = sp.getString("ChannelName", "");
		m_IP_in = sp.getString("IP_in", "");
		m_Port_in = sp.getString("Port_in", "");
		m_IP_out = sp.getString("IP_out", "");
		m_Port_out = sp.getString("Port_out", "");
		m_Auth = sp.getString("Auth", "");
		m_zhibopic = sp.getString("zhibopic", "");
		m_dahuipic1 = sp.getString("dahuipic1", "");


		android.util.Log.i("cjwsjy", "------isOpen="+isOpen+"-------");
		android.util.Log.i("cjwsjy", "------isMeeting="+isMeeting+"-------");
		android.util.Log.i("cjwsjy", "------isZhibo="+isZhibo+"-------");
		//得到当前年
		//Time time = new Time("GMT+8");
		//time.setToNow();
		//int nyear = time.year;

		Calendar cale = Calendar.getInstance();
		int nyear = cale.get(Calendar.YEAR);
		int nmouth = cale.get(Calendar.MONTH);
		m_year = Integer.toString(nyear);
		
		str_count = new String[] {"0","0","0","0","0","0","0","0","0","0"};
		tv_count = new TextView[9];
		tv_text = new TextView[9];
		
		m_listview = (ListView) getView().findViewById(R.id.listviewd2);

		imageViewMeeting = (ImageView) getView().findViewById(R.id.iv_image);
		image4 = (ImageView) getView().findViewById(R.id.iv_image4);
		image4.setImageResource(R.drawable.index_02_open);

		//是否开启会议
		boolean bresult = isOpen.equals("1");
		//bresult = false;
		if( bresult==true )
		{
			bresult = isMeeting.equals("1");
			if(bresult==true)  //参会人员
			{
				ret = PermissionsRequest2(Manifest.permission.READ_EXTERNAL_STORAGE,105);
				if(ret==1)
				{
					//下载图片
					if(m_dahuipic1!=null)
					{
						if( m_dahuipic1.length()!=0 )
						{
							//GetBitmapFromUrl(m_dahuipic1);
							SetBitmapMeeting(m_dahuipic1);
						}
					}
					//SetBitmapMeeting(m_dahuipic1);
				}
			}
			else  //非参会人员
			{
				bresult = isZhibo.equals("1");
				if(bresult==true)
				{
					ret = PermissionsRequest2(Manifest.permission.READ_EXTERNAL_STORAGE, 106);
					if (ret == 1)
					{
						if(m_zhibopic!=null)
						{
							if( m_zhibopic.length()!=0 )
							{
								//GetBitmapFromUrl(m_dahuipic1);
								SetBitmapZhibo(m_zhibopic);
							}
						}

					}
				}
			}
		}

		tv_text[0] = (TextView) getView().findViewById(R.id.et_text1);
		tv_text[1] = (TextView) getView().findViewById(R.id.et_text2);
		tv_text[2] = (TextView) getView().findViewById(R.id.et_text3);
		tv_text[3] = (TextView) getView().findViewById(R.id.et_text4);
		tv_text[4] = (TextView) getView().findViewById(R.id.et_text5);
		tv_text[5] = (TextView) getView().findViewById(R.id.et_text6);
		tv_text[6] = (TextView) getView().findViewById(R.id.et_text7);
		tv_text[7] = (TextView) getView().findViewById(R.id.et_text8);
		tv_text[8] = (TextView) getView().findViewById(R.id.et_text9);
		
		tv_textall = (TextView) getView().findViewById(R.id.tv_text2);
		
		tv_count[0] = (TextView) getView().findViewById(R.id.tv_count1);
		tv_count[1] = (TextView) getView().findViewById(R.id.tv_count2);
		tv_count[2] = (TextView) getView().findViewById(R.id.tv_count3);
		tv_count[3] = (TextView) getView().findViewById(R.id.tv_count4);
		tv_count[4] = (TextView) getView().findViewById(R.id.tv_count5);
		tv_count[5] = (TextView) getView().findViewById(R.id.tv_count6);
		tv_count[6] = (TextView) getView().findViewById(R.id.tv_count7);
		tv_count[7] = (TextView) getView().findViewById(R.id.tv_count8);
		tv_count[8] = (TextView) getView().findViewById(R.id.tv_count9);
		
		str_count[0] = sp.getString("ALL_COUNT", "");
		str_count[1] = sp.getString("CHUCHAI_COUNT", "");
		str_count[2] = sp.getString("SHOUWEN_COUNT", "");
		str_count[3] = sp.getString("FAWEN_COUNT", "");
		str_count[4] = sp.getString("YIBAN_COUNT", "");
		str_count[5] = sp.getString("QIANBAO_COUNT", "");
		str_count[6] = sp.getString("YINZHANG_COUNT", "");
		
		mHashMap = new HashMap<String, Integer>();
		
		//待办事宜，下滑
		LinearLayout ll = (LinearLayout) getView().findViewById(R.id.ll_layout);
		ll.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Resources res;
				res = getResources();
				
				LinearLayout.LayoutParams params;
				RelativeLayout rl;
				rl = (RelativeLayout) getView().findViewById(R.id.rl_layout);

				float scale = getActivity().getResources().getDisplayMetrics().density;
				int height1 = (int) (120 * scale + 0.5f); //48dp
				int height2 = (int) (250 * scale + 0.5f); //48dp
				
				if(expand==0)
				{
					//展开
					params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height2);
					//params = (LinearLayout.LayoutParams)rl.getLayoutParams();
					//params = new RelativeLayout.LayoutParams(wide, height);
					//params.weight;
					rl.setLayoutParams(params);
					image4.setImageResource(R.drawable.index_02);
					expand = 1;
				}
				else
				{
					//收起
					params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height1);
					rl.setLayoutParams(params);
					image4.setImageResource(R.drawable.index_02_open);
					expand = 0;
				}
				
				//刷新待办消息
				ThreadRefresh(3);
			}
		});

		//初始化待办列表
		initListView();
		
		//初始化GridView
		initGridView();


	}
	
	private int initListView()
	{
		ItemDaiban ItemDaiban1 = new ItemDaiban();
		ItemDaiban1.setTv_title("出差审批待办");
		ItemDaiban1.setTv_date("0");
		listItem1.add(ItemDaiban1);
		
		ItemDaiban ItemDaiban2 = new ItemDaiban();
		ItemDaiban2.setTv_title("收文待办");
		ItemDaiban2.setTv_date("0");
		listItem1.add(ItemDaiban2);
		
		ItemDaiban ItemDaiban3 = new ItemDaiban();
		ItemDaiban3.setTv_title("发文待办");
		ItemDaiban3.setTv_date("0");
		listItem1.add(ItemDaiban3);
		
		ItemDaiban ItemDaiban4 = new ItemDaiban();
		ItemDaiban4.setTv_title("一般文件待办");
		ItemDaiban4.setTv_date("0");
		listItem1.add(ItemDaiban4);
		
		ItemDaiban ItemDaiban5 = new ItemDaiban();
		ItemDaiban5.setTv_title("院签报待办");
		ItemDaiban5.setTv_date("0");
		listItem1.add(ItemDaiban5);
		
		ItemDaiban ItemDaiban6 = new ItemDaiban();
		ItemDaiban6.setTv_title("印章待办");
		ItemDaiban6.setTv_date("0");
		listItem1.add(ItemDaiban6);
		
		ItemDaiban ItemDaiban7 = new ItemDaiban();
		ItemDaiban7.setTv_title("外委(科研)待办");
		ItemDaiban7.setTv_date("0");
		listItem1.add(ItemDaiban7);
		
		ItemDaiban ItemDaiban8 = new ItemDaiban();
		ItemDaiban8.setTv_title("外委(生产)待办");
		ItemDaiban8.setTv_date("0");
		listItem1.add(ItemDaiban8);
		
		ItemDaiban ItemDaiban9 = new ItemDaiban();
		ItemDaiban9.setTv_title("付款审签待办");
		ItemDaiban9.setTv_date("0");
		listItem1.add(ItemDaiban9);
		
		// 生成适配器的Item和动态数组对应的元素
		adapterdaiban = new AdaperDaiban( getActivity(), listItem1 );
		m_listview.setAdapter(adapterdaiban);
		
		m_listview.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) 
			{
				ItemDaiban item = (ItemDaiban) m_listview.getItemAtPosition(position);
				String title = item.getTv_title();

				onClickListView(title);
			}
		});
		
		return 1;
	}

	public SharedPreferences GetSP()
	{
		return sp;
	}

	//参会人员
	private int SetBitmapMeeting(String strUrl)
	{
		int height;
		int widthScreen;
		Bitmap bitmap;
		Bitmap BitmapResized;

		//获得图片
		bitmap = GetBitmapFromUrl(strUrl);

		DisplayMetrics dm = new DisplayMetrics();
		WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		display.getMetrics(dm);

		widthScreen = dm.widthPixels;  //屏幕宽度（像素）

		//image的高度
		RelativeLayout relatLayout;
		relatLayout = (RelativeLayout) getView().findViewById(R.id.rl_image);
		height = relatLayout.getLayoutParams().height;

		BitmapResized = Bitmap.createScaledBitmap(bitmap,widthScreen,height, true);

		//显示会议图片
		imageViewMeeting.setImageBitmap(BitmapResized);

		//点击进入会议界面
		imageViewMeeting.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String url;
				url = "http://moa.cispdr.com:8087/HYServer/mvc/meeting/meetingIndex/"+m_loginname;
				gotowebview(url,"我的会议");
			}
		});
		return 1;
	}

	//非参会人员
	private int SetBitmapZhibo(String strUrl)
	{
		int height;
		int widthScreen;
		Bitmap bitmap;
		Bitmap bitmapResized;

		//初始化视频
		Intent i = new Intent(getActivity(), NetworkStateService.class);
		getActivity().startService(i);
		//初始化SDK
		QYSDK.InitSDK(4);

		session1 = QYSDK.CreateSession(getActivity().getApplicationContext());
		session2 = QYSDK.CreateSession(getActivity().getApplicationContext());

		//获得图片
		bitmap = GetBitmapFromUrl(strUrl);

		DisplayMetrics dm = new DisplayMetrics();
		WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		display.getMetrics(dm);

		widthScreen = dm.widthPixels;  //屏幕宽度（像素）

		//image的高度
		RelativeLayout relatLayout;
		relatLayout = (RelativeLayout) getView().findViewById(R.id.rl_image);
		height = relatLayout.getLayoutParams().height;

		bitmapResized = Bitmap.createScaledBitmap(bitmap,widthScreen,height, true);

		//显示会议图片
		imageViewMeeting.setImageBitmap(bitmapResized);

		android.util.Log.i("cjwsjy", "------zhibo-------");

		//点击进入会议界面
		imageViewMeeting.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				//m_ChannelName = "1000000012001";
				int ret = PermissionsRequest2(Manifest.permission.READ_PHONE_STATE, 107);
				if (ret != 1) return;

				StartShiLian();

			}
		});
		return 1;
	}

	private void StartShiLian()
	{
		m_ShiLian = 0;
		m_progressDialog = ProgressDialog.show(getActivity(), "加载中...", "正  在  获  取  数  据 ...");

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

//			IPaddress = "172.16.13.70";
//			ports = "39101";
//			appid = "wholeally";
//			auth = "czFYScb5pAu+Ze7rXhGh/+MesDXVJaszykybiYXfmlUG5hUR8MkXH/MBaOy+I6hqSFLfYeF/Q/Or/s3BO8pI4ROo63Hq79pf";

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
				Toast.makeText(getActivity(), "服务器连接失败:" + String.valueOf(retState), Toast.LENGTH_SHORT).show();

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

//			IPaddress = "218.106.125.147";
//			ports = "39100";
//			appid = "wholeally";
//			auth = "czFYScb5pAu+Ze7rXhGh/+MesDXVJaszykybiYXfmlUG5hUR8MkXH/MBaOy+I6hqSFLfYeF/Q/Or/s3BO8pI4ROo63Hq79pf";

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
				Toast.makeText(getActivity(), "服务器连接失败:" + String.valueOf(retState), Toast.LENGTH_SHORT).show();

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
						intent = new Intent(getActivity(), QyVideoControlActivity2.class);
						intent.putExtra("channelName", m_ChannelName);
						intent.putExtra("session", "5");
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
						intent = new Intent(getActivity(), QyVideoControlActivity2.class);
						intent.putExtra("channelName",m_ChannelName);
						intent.putExtra("session","6");
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

					Toast.makeText(getActivity().getApplicationContext(), "登录失败，失败类型1037",Toast.LENGTH_SHORT).show();
					break;
			}
		}
	};

	private Bitmap GetBitmapFromUrl(String strUrl)
	{
		Bitmap bitmap;

		LoaderImpl impl;
		Map<String,SoftReference<Bitmap>> sImageCache;
		sImageCache = new HashMap<>();
		impl = new LoaderImpl(sImageCache);

		bitmap = impl.getBitmap(strUrl);

		return bitmap;
	}

	//开放外部接口，得到主activity
	public void Getmainactivity( MainActivity ma )
	{
		mainactivity = ma;
	}

	private int onClickListView(String strtext)
	{
		boolean bresult;
		String url;
		String title;

		ThreadUtils emperor1 = ThreadUtils.getInstance();  // 得到单例对象

		title = "公文管理待办";
		bresult = strtext.equals(title);
		if( bresult==true )
		{
			emperor1.setparm2("点击","公文管理待办","公文管理待办-首页");
			emperor1.writelog();

			Intent intent = new Intent( getActivity(), ActivityGongwen.class);
			startActivity(intent);
			return 1;
		}

		title = "项目管理待办";
		bresult = strtext.equals(title);
		if( bresult==true )
		{
			emperor1.setparm2("点击","项目管理待办","项目管理待办-首页");
			emperor1.writelog();

			Intent intent = new Intent( getActivity(), ActivityXiangmu.class);
			startActivity(intent);
			return 1;
		}

		title = "会议管理待办";
		bresult = strtext.equals(title);
		if( bresult==true )
		{
			emperor1.setparm2("点击","会议申报","会议申报-首页");
			emperor1.writelog();

			Intent intent = new Intent( getActivity(), ActivityHuiyi.class);
			startActivity(intent);
			return 1;
		}

		title = "科研项目管理待办";
		bresult = strtext.equals(title);
		if( bresult==true )
		{
			emperor1.setparm2("点击","科研项目管理待办","科研项目管理待办-首页");
			emperor1.writelog();

			url="http://moa.cispdr.com:8087/KYManageAppWeb/ky/db/"+m_loginname;
			gotowebview(url,title);
			return 1;
		}

		title = "出差审批待办";
		bresult = strtext.equals(title);
		if( bresult==true )
		{
			emperor1.setparm2("点击","出差审批待办","首页-出差审批待办");
			emperor1.writelog();

			url=appUrl+"/OutWeb/wapgTasks/"+m_userId;

			gotowebview(url,"出差审批登记");
			return 1;
		}

		title = "收文待办";
		bresult = strtext.equals(title);
		if( bresult==true )
		{
			emperor1.setparm2("点击","收文待办","首页-收文待办");
			emperor1.writelog();

			url=appUrl+"/CEGWAPServer/GWManage/getMyTaskList_SW/"+m_loginname;
			gotowebview(url,title);
			return 1;
		}

		title = "发文待办";
		bresult = strtext.equals(title);
		if( bresult==true )
		{
			emperor1.setparm2("点击","发文待办","首页-发文待办");
			emperor1.writelog();

			url=appUrl+"/CEGWAPServer/GWManage/getMyTaskList_FW/"+m_loginname;
			gotowebview(url,title);
			return 1;
		}
		
		title = "一般文件待办";
		bresult = strtext.equals(title);
		if( bresult==true )
		{
			emperor1.setparm2("点击","一般文件待办","首页-一般文件待办");
			emperor1.writelog();

			url=appUrl+"/CEGWAPServer/GWManage/getMyTaskList_YBWJ/"+m_loginname;
			gotowebview(url,title);
			return 1;
		}
		
		title = "院签报待办";
		bresult = strtext.equals(title);
		if( bresult==true )
		{
			emperor1.setparm2("点击","院签报待办","首页-院签报待办");
			emperor1.writelog();

			Intent intent = new Intent( getActivity(), UnFinishPeddingListActivity.class);
			startActivity(intent);
			return 1;
		}
		
		title = "印章待办";
		bresult = strtext.equals(title);
		if( bresult==true )
		{
			emperor1.setparm2("点击","印章待办","首页-印章待办");
			emperor1.writelog();

			url=appUrl+"/CEGWAPServer/GWManage/getMyTaskList_YZ/"+m_loginname;
			gotowebview(url,title);
			return 1;
		}
		
		//外委(科研)待办
		title = "外委(科研)待办";
		bresult = strtext.equals(title);
		if( bresult==true )
		{
			emperor1.setparm2("点击","外委(科研)待办","首页-外委(科研)待办");
			emperor1.writelog();

			url = appUrl+"/CEGWAPServer/XMManage/outContractAuditKY_DB/"+m_loginname;
			gotowebview(url,title);
			return 1;
		}
		
		//外委(生产)待办
		title = "外委(生产)待办";
		bresult = strtext.equals(title);
		if( bresult==true )
		{
			emperor1.setparm2("点击","外委(生产)待办","首页-外委(生产)待办");
			emperor1.writelog();

			url = appUrl+"/CEGWAPServer/XMManage/outContractAuditSC_DB/"+m_loginname;
			gotowebview(url,title);
			return 1;
		}
		
		//付款审签待办
		title = "付款审签待办";
		bresult = strtext.equals(title);
		if( bresult==true )
		{
			emperor1.setparm2("点击","付款审签待办","首页-付款审签待办");
			emperor1.writelog();

			url = appUrl+"/CEGWAPServer/XMManage/ContractPaySH_DB/"+m_loginname;
			gotowebview(url,title);
			return 1;
		}
		
		//收入合同风险评估待办
		title = "收入合同风险评估待办";
		bresult = strtext.equals(title);
		if( bresult==true )
		{
			emperor1.setparm2("点击","收入合同风险评估待办","首页-收入合同风险评估待办");
			emperor1.writelog();

			url = appUrl+"/CEGWAPServer/XMManage/ProjectRiskEvaluation_DB/"+m_loginname;
			gotowebview(url,title);
			return 1;
		}
		
		//资质借用待办
		title = "资质借用待办";
		bresult = strtext.equals(title);
		if( bresult==true )
		{
			emperor1.setparm2("点击","资质借用待办","首页-资质借用待办");
			emperor1.writelog();

			url = appUrl+"/CEGWAPServer/XMManage/qualification_DB/"+m_loginname;
			gotowebview(url,title);
			return 1;
		}

		//保函保证金申请待办
		title = "保函保证金申请待办";
		bresult = strtext.equals(title);
		if( bresult==true )
		{
			emperor1.setparm2("点击","保函保证金申请待办","首页-保函保证金申请待办");
			emperor1.writelog();

			url = appUrl+"/CEGWAPServer/XMManage/BackLetter_DB/"+m_loginname;
			gotowebview(url,title);
			return 1;
		}

		//会议申报待办
		title = "会议申报待办";
		bresult = strtext.equals(title);
		if( bresult==true )
		{
			emperor1.setparm2("点击","会议申报待办","首页-会议申报待办");
			emperor1.writelog();

			url = appUrl+"/CEGWAPServer/HYManage/MeetingFee_DB/"+m_loginname;
			gotowebview(url,title);
			return 1;
		}

		//劳务费审批待办
		title = "劳务费审批待办";
		bresult = strtext.equals(title);
		if( bresult==true )
		{
			emperor1.setparm2("点击","劳务费审批待办","首页-劳务费审批待办");
			emperor1.writelog();

			url = appUrl+"/CEGWAPServer/meeting/SkillAuditFee_DB/"+m_loginname;
			gotowebview(url,title);
			return 1;
		}

		//发票申请待办
		title = "发票申请待办";
		bresult = strtext.equals(title);
		if( bresult==true )
		{
			emperor1.setparm2("点击","发票申请待办","首页-发票申请待办");
			emperor1.writelog();

			url = appUrl+"/CEGWAPServer/XMManage/InvoiceInfo_DB/"+m_loginname;
			gotowebview(url,title);
			return 1;
		}

		//项目登记待办
		title = "项目登记待办";
		bresult = strtext.equals(title);
		if( bresult==true )
		{
			emperor1.setparm2("点击","项目登记待办","首页-项目登记待办");
			emperor1.writelog();

			url = appUrl+"/CEGWAPServer/XMManage/ProjectRegister_DB/"+m_loginname;
			gotowebview(url,title);
			return 1;
		}

		//网上报销待办
		title = "网上报销待办";
		bresult = strtext.equals(title);
		if( bresult==true )
		{
			emperor1.setparm2("点击","网上报销待办","首页-网上报销待办");
			emperor1.writelog();

			Intent intent = new Intent( getActivity(), ActivityBaoxiao.class);
			startActivity(intent);
			return 1;
		}

		//业务接待待办
		title = "业务接待待办";
		bresult = strtext.equals(title);
		if( bresult==true )
		{
			emperor1.setparm2("点击","业务接待待办","首页-业务接待待办");
			emperor1.writelog();

			url = appUrl+"/CEGWAPServer/JD/getUserDBList/"+m_loginname;
			gotowebview(url,title);
			return 1;
		}

		//档案管理待办
		title = "档案管理待办";
		bresult = strtext.equals(title);
		if( bresult==true )
		{
			emperor1.setparm2("点击","档案管理待办","首页-档案管理待办");
			emperor1.writelog();

			url = appUrl+"/CEGWAPServer/DAManage/getToDoList/"+m_loginname;
			gotowebview(url,title);

			return 1;
		}

		title = "出国管理待办";
		bresult = strtext.equals(title);
		if( bresult==true )
		{
			emperor1.setparm2("点击","出国管理待办","首页-出国管理待办");
			emperor1.writelog();

			url = appUrl+"/CEGWAPServer/CGManage/getCGManage_DB/"+m_loginname;
			gotowebview(url,title);
			return 1;
		}

		return 1;
	}
	
	private void gotowebview(String url,String title)
	{
		Intent intent = new Intent();           	
		intent.setClass(getActivity(), WebViewHome.class);
		intent.putExtra("webUrl",url);
		intent.putExtra("titleName",title);
		startActivity(intent);
	}
	
    private Handler mRefreshHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) 
        {
            switch (msg.what) 
            {
                case 0:
                {
                	boolean bret = false;
            		int i = 0;
            		int size = 0;
            		int value = 0;
            		String strkey;
            		String strvalue;
            		
            		if(mHashMapEntryList==null) break;

            		bret = mHashMapEntryList.isEmpty();
            		size = mHashMapEntryList.size();
            		if(size==0) break;
            		
            		listItem1.clear();
            		for( i=0; i<mHashMapEntryList.size(); i++ )
            		{
            			strkey = mHashMapEntryList.get(i).getKey();
            			value = mHashMapEntryList.get(i).getValue();
            			strvalue = Integer.toString(value);
            			
            			ItemDaiban ItemDaiban = new ItemDaiban();
            			ItemDaiban.setTv_title(strkey);
            			ItemDaiban.setTv_date(strvalue);
            			listItem1.add(ItemDaiban);
            			
            			//tv_text[i].setHint(strkey);
            			//tv_count[i].setText( Integer.toString(value) );
            		}
            		
            		adapterdaiban.notifyDataSetChanged();
            		 
            		strbuf = "共"+str_count[0]+"条待办";
            		tv_textall.setText(strbuf);

            		RefreshMutex = 0;
                    break;
                }
                case 1 :
                {
                    break;
                }
                default:
                	break;
            }
        }
    };
    
    public void ThreadRefresh(int debug)
	{
    	if(RefreshMutex==0)
    	{
    		RefreshMutex = 1;
    		Thread refreshThread = new Thread(mRefreshRunnable);
    		refreshThread.start();
    	}
	}
	
    private Runnable mRefreshRunnable = new Runnable()
    {
    	@Override
        public void run() 
        {
    		Getdaiban();
        	
        	Message msg = mRefreshHandler.obtainMessage();
        	msg.what = 0;
            msg.sendToTarget();
        }
    };
    
	//刷新待办消息的条数
	private void Getdaiban()
	{
		boolean bresult = false;
		int i = 0;
		int length = 0;
		String titlestr;
		String countstr;
		JSONObject jsonObj;

		//清空map
		mHashMap.clear();
		
		try
		{
			String url = appUrl+"/CEGWAPServer/RecordTotal/getMyTaskTotal/" + m_loginname+"/"+m_jobnumber;
			String resultStr = HttpClientUtil.HttpUrlConnectionGet(url, "UTF-8");
			length = resultStr.length();

			if( length==0 ) return;

			String jsonStr = "{\"data\":" + resultStr + "}";

			JSONArray jsonObjs = new JSONObject(jsonStr).getJSONArray("data");
			length = jsonObjs.length();
			for (i = 0; i < length; i++)
			{
				jsonObj = jsonObjs.getJSONObject(i);

				// 总数
				titlestr = jsonObj.getString("title");
				countstr = jsonObj.getString("count");

				bresult = titlestr.equals("印章待办");
				if( bresult==true )
				{
					m_yinzhang = countstr;
				}

				bresult = titlestr.equals("总数");
				if( bresult==true )
				{
					str_count[0] = countstr;
					//mHashMap.put( "总数", Integer.parseInt(countstr) );
				}
				else
				{
					mHashMap.put( titlestr, Integer.parseInt(countstr) );
				}
			}
			
			mHashMapEntryList = new ArrayList<Map.Entry<String,Integer>>(mHashMap.entrySet());
			//排序
			Collections.sort(mHashMapEntryList, new Comparator<Map.Entry<String,Integer>>() 
			{
			      @Override
			      public int compare(Map.Entry<String,Integer> entry1, Map.Entry<String,Integer> entry2) 
			      {
			    	  int value1 = 0;
			    	  int value2 = 0;
			    	  value1 = entry1.getValue();
			    	  value2 = entry2.getValue();
			    	  return value2 - value1;
			    	  //return firstMapEntry.getKey().compareTo(secondMapEntry.getKey());
			      }
			});
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void onResume() 
	{
		super.onResume();
		//android.util.Log.d("cjwsjy", "------onResume-------Home");
		
		ThreadRefresh(1);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		//Log.d("TagTest","onPause");
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();

		if(null!=session1){
			session1.Release();
			session1=null;
		}

		if(null!=session2){
			session2.Release();
			session2=null;
		}

	}

	private void initGridView()
	{
		LayoutInflater flater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		view1 = flater.inflate(R.layout.home_fragment_page1 ,null);
		view2 = flater.inflate(R.layout.home_fragment_page2 ,null);
		view3 = flater.inflate(R.layout.home_fragment_page2 ,null);

		gridView1 = (GridView) view1.findViewById(R.id.main_grid2);
		gridView2 = (GridView) view2.findViewById(R.id.main_grid3);
		gridView3 = (GridView) view3.findViewById(R.id.main_grid3);

		//第一页
		ArrayList<Grid_Item> lists = new ArrayList<Grid_Item>();
		lists.add(new Grid_Item(R.drawable.homepage_icon01, "新闻中心"));
		lists.add(new Grid_Item(R.drawable.homepage_icon02, "公文管理"));
		lists.add(new Grid_Item(R.drawable.homepage_icon03, "项目管理"));
		lists.add(new Grid_Item(R.drawable.homepage_icon05, "印章管理"));
		lists.add(new Grid_Item(R.drawable.index_16, "院签报"));
		lists.add(new Grid_Item(R.drawable.homepage_icon04, "出差审批"));
		lists.add(new Grid_Item(R.drawable.homepage_icon06, "订会议室"));
		lists.add(new Grid_Item(R.drawable.homepage_icon09, "会议申报"));

		//第二页
		ArrayList<Grid_Item> lists1 = new ArrayList<Grid_Item>();
		lists1.add(new Grid_Item(R.drawable.homepage_icon13, "业务接待"));
		lists1.add(new Grid_Item(R.drawable.plate55, "网上报销"));
		lists1.add(new Grid_Item(R.drawable.homepage_icon11, "规程规范"));
		lists1.add(new Grid_Item(R.drawable.plate60, "薪资查询"));
		//lists1.add(new Grid_Item(R.drawable.plate20, "设备扫码"));
		lists1.add(new Grid_Item(R.drawable.plate75, "档案管理"));
		lists1.add(new Grid_Item(R.drawable.homepage_icon15, "餐饮服务"));
		lists1.add(new Grid_Item(R.drawable.plate96, "服务电话"));
		lists1.add(new Grid_Item(R.drawable.homepage_icon08, "更多"));

		//第三页
		//ArrayList<Grid_Item> lists2 = new ArrayList<Grid_Item>();
		//lists2.add(new Grid_Item(R.drawable.homepage_icon08, "更多"));

		DisplayMetrics metrics = new DisplayMetrics();
		Display display = getActivity().getWindowManager().getDefaultDisplay();
		display.getMetrics(metrics);
		
		int pixelsX = metrics.widthPixels;
		
		NewGridAdaper adaper = new NewGridAdaper( getActivity(), lists, pixelsX );
		gridView1.setAdapter(adaper);
        
		NewGridAdaper adaper1 = new NewGridAdaper( getActivity(), lists1, pixelsX );
		gridView2.setAdapter(adaper1);

//		NewGridAdaper adaper2 = new NewGridAdaper( getActivity(), lists2, pixelsX );
//		gridView3.setAdapter(adaper2);
		
		//添加消息处理
		gridView1.setOnItemClickListener(gridViewListener1);
		gridView2.setOnItemClickListener(gridViewListener2);
		//gridView3.setOnItemClickListener(gridViewListener3);
		
		List<View> mViews = new ArrayList<View>();
		mViews.add(view1);
		mViews.add(view2);
		//mViews.add(view3);

		MyPagerAdapter pagerAdapter = new MyPagerAdapter(mViews);
		viewPager.setAdapter(pagerAdapter);
		viewPager.setCurrentItem(0);
		
		imageViews = new ImageView[2];
		for (int i=0; i<2; i++)
		{
			imageView = new ImageView(this.getActivity());

			// imageView.setLayoutParams(new LayoutParams(20,20));
			//
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(20, 20);
			lp.setMargins(10, 0, 10, 20);
			imageView.setLayoutParams(lp);

			imageView.setPadding(40, 0, 40, 0);
			imageViews[i] = imageView;

			if( i==0 )
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
		//viewPager.setOnPageChangeListener(new GuidePageChangeListener());
		viewPager.addOnPageChangeListener(new GuidePageChangeListener());

	}
	
	// 指引页面更改事件监听器
	private class GuidePageChangeListener implements OnPageChangeListener
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
			for (int i = 0; i < 2; i++)
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

	//gridview点击事件
	private  OnItemClickListener  gridViewListener1 = new OnItemClickListener()
	{
		@Override
		public void onItemClick(AdapterView<?> arg0,View arg1,int arg2,long arg3)
		{
			int ret;
			String url = "";

			ThreadUtils emperor1 = ThreadUtils.getInstance();  // 得到单例对象

			//展开状态下，收起待办列表
			if( expand==1 )
			{
				Resources res;
				RelativeLayout rl;
				LinearLayout.LayoutParams params;

				res = getResources();
				rl = (RelativeLayout) getView().findViewById(R.id.rl_layout);

				float scale = getActivity().getResources().getDisplayMetrics().density;
				int height1 = (int) (120 * scale + 0.5f); //48dp

				//收起
				params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height1);
				rl.setLayoutParams(params);
				image4.setImageResource(R.drawable.index_02_open);
				expand = 0;
				return;
			}

			if( arg3==0 )  //新闻中心
			{
				emperor1.setparm2("点击","新闻中心","新闻中心-首页");
				emperor1.writelog();

				Intent intent = new Intent( getActivity(), ActivityXinwen.class);
				startActivity(intent);

				//FloatView();

				//ret = PermissionsRequest2(android.Manifest.permission.SYSTEM_ALERT_WINDOW,110);
				//if(ret!=1) return;

//				Intent intent = new Intent( getActivity(), ActivityCanteenMain.class);
//				startActivity(intent);
			}
			else if( arg3==1 )  //公文管理
			{
				emperor1.setparm2("点击","公文管理","公文管理-首页");
				emperor1.writelog();

				Intent intent = new Intent( getActivity(), ActivityGongwen.class);
				startActivity(intent);
			}
			else if( arg3==2 )  //项目管理
			{
				emperor1.setparm2("点击","项目管理","项目管理-首页");
				emperor1.writelog();

				Intent intent = new Intent( getActivity(), ActivityXiangmu.class);
				startActivity(intent);
			}
			else if( arg3==3 )  //印章管理
			{
				emperor1.setparm2("点击","印章管理","印章管理-首页");
				emperor1.writelog();

				Intent intent = new Intent( getActivity(), ActivityYinzhang.class);
				intent.putExtra("number",m_yinzhang);
				startActivity(intent);
			}
			else if( arg3==4 )  //院签报
			{
				emperor1.setparm2("点击","院签报","院签报-首页");
				emperor1.writelog();

				Intent intent = new Intent( getActivity(), PeddingActivity.class);
				startActivity(intent);
			}
			else if( arg3==5 )  //出差审批
			{
				emperor1.setparm2("点击","出差审批","出差审批-首页");
				emperor1.writelog();

				Intent intent = new Intent( getActivity(), OutOfficeActivity.class);
				startActivity(intent);
			}
			else if( arg3==6 )  //订会议室
			{
				emperor1.setparm2("点击","订会议室","订会议室-首页");
				emperor1.writelog();

				url = appUrl+"/CEGWAPServer/HYManage/getIndex_HY/"+m_loginname;
				gotowebview(url,"会议管理");
			}
			else if( arg3==7 )  //会议申报
			{
				emperor1.setparm2("点击","会议申报","会议申报-首页");
				emperor1.writelog();

				Intent intent = new Intent( getActivity(), ActivityHuiyi.class);
				startActivity(intent);
			}
		}
	};

	//GridView 2级页面
    private  OnItemClickListener  gridViewListener2 = new OnItemClickListener() 
    {
        @Override
        public void onItemClick(AdapterView<?> arg0,View arg1,int arg2,long arg3)
        {            	
        	String url;

            android.util.Log.d("cjwsjy", "------arg3="+arg3+"-------");

			ThreadUtils emperor1 = ThreadUtils.getInstance();  // 得到单例对象
        	//展开状态下，收起待办列表
			if( expand==1 )
			{
				Resources res;
				RelativeLayout rl;
				LinearLayout.LayoutParams params;
				
				res = getResources();
				rl = (RelativeLayout) getView().findViewById(R.id.rl_layout);

				float scale = getActivity().getResources().getDisplayMetrics().density;
				int height1 = (int) (120 * scale + 0.5f); //48dp
				
				//收起
				params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height1);
				rl.setLayoutParams(params);
				image4.setImageResource(R.drawable.index_02_open);
				expand = 0;
				return;
			}

			if( arg3==0)  //业务接待
			{
				emperor1.setparm2("点击","业务接待","业务接待-首页");
				emperor1.writelog();

				Intent intent = new Intent( getActivity(), ActivityYewu.class);
				startActivity(intent);
			}
        	else if( arg3==1)  //网上报销
            {
				emperor1.setparm2("点击","网上报销","网上报销-首页");
				emperor1.writelog();

				Intent intent = new Intent( getActivity(), ActivityBaoxiao.class);
				startActivity(intent);
            }
			else if( arg3==2)  //规程规范
			{
				emperor1.setparm2("点击","规程规范","规程规范-首页");
				emperor1.writelog();

				Intent intent = new Intent( getActivity(), ActivityBiaozhun.class);
				startActivity(intent);
			}
        	else if( arg3==3)  //薪资查询
            {
				emperor1.setparm2("点击","薪资查询","薪资查询-首页");
				emperor1.writelog();

				url = "http://moa.cispdr.com:8087/WageSearchDsp/mvc/sms_verification/"+m_jobnumber;

				Intent intent = new Intent();
				intent.setClass(getActivity(), WebViewHome.class);
				intent.putExtra("webUrl",url);
				intent.putExtra("titleName","薪资查询");
				intent.putExtra("isClose","1");
				startActivity(intent);
            }
        	else if( arg3==4)  //档案管理
            {
				emperor1.setparm2("点击","档案管理","档案管理-首页");
				emperor1.writelog();

				Intent intent = new Intent( getActivity(), ActivityDangan1.class);
				startActivity(intent);

//				int ret = PermissionsRequest2(Manifest.permission.CAMERA,104);
//				if(ret!=1) return;
//
//				Intent intent = new Intent( getActivity(), CaptureActivity.class);
//				intent.putExtra("titleName","设备扫码");
//				startActivity(intent);
            }
			else if( arg3==5)  //餐饮服务
			{
				emperor1.setparm2("点击","餐饮服务","餐饮服务-首页");
				emperor1.writelog();

				onClickCanteen("1");
			}
			else if( arg3==6)  //服务电话
			{
				emperor1.setparm2("点击","服务电话","服务电话-首页");
				emperor1.writelog();

				url = appUrl+"/CEGWAPServer/bianjiefuwu/"+m_loginname;
				gotowebview(url,"服务电话");
			}
			else if( arg3==7)  //更多
			{
				emperor1.setparm2("点击","更多","更多-首页");
				emperor1.writelog();

				mainactivity.onTab(3);
			}
        }
    };

	//GridView 3级页面
	private  OnItemClickListener  gridViewListener3 = new OnItemClickListener()
	{
		@Override
		public void onItemClick(AdapterView<?> arg0,View arg1,int arg2,long arg3)
		{
			String url;

			android.util.Log.d("cjwsjy", "------arg3="+arg3+"-------");

			ThreadUtils emperor1 = ThreadUtils.getInstance();  // 得到单例对象
			//展开状态下，收起待办列表
			if( expand==1 )
			{
				Resources res;
				RelativeLayout rl;
				LinearLayout.LayoutParams params;

				res = getResources();
				rl = (RelativeLayout) getView().findViewById(R.id.rl_layout);

				float scale = getActivity().getResources().getDisplayMetrics().density;
				int height1 = (int) (120 * scale + 0.5f); //48dp

				//收起
				params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height1);
				rl.setLayoutParams(params);
				image4.setImageResource(R.drawable.index_02_open);
				expand = 0;
				return;
			}

			if( arg3==0)  //餐饮服务
			{
				emperor1.setparm2("点击","更多","更多-首页");
				emperor1.writelog();

				mainactivity.onTab(3);
			}
			else if( arg3==1)  //网上报销
			{
				emperor1.setparm2("点击","网上报销","网上报销-首页");
				emperor1.writelog();

				Intent intent = new Intent( getActivity(), ActivityBaoxiao.class);
				startActivity(intent);
			}
		}
	};

    private int onClickCanteen(String strtext)
    {
        String url;

        Thread thread_http = new Thread(new ThreadHttp());
        thread_http.start();

        return 1;
    }

	private int FloatView()
	{
		Context contextfragm = getActivity().getApplicationContext();
		Button bb = new Button(contextfragm);
		WindowManager wm=(WindowManager)contextfragm.getSystemService(Context.WINDOW_SERVICE);
		WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();

		/**
		 *以下都是WindowManager.LayoutParams的相关属性
		 * 具体用途请参考SDK文档
		 */
		wmParams.type=WindowManager.LayoutParams.TYPE_PHONE;   //这里是关键，你也可以试试2003
		wmParams.format=1;
		/**
		 *这里的flags也很关键
		 *代码实际是wmParams.flags |= FLAG_NOT_FOCUSABLE;
		 *40的由来是wmParams的默认属性（32）+ FLAG_NOT_FOCUSABLE（8）
		 */
		wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;;
		wmParams.width=400;
		wmParams.height=400;
		wm.addView(bb, wmParams);  //创建View

		return 1;
	}

    class ThreadHttp implements Runnable
    {
        @Override
        public void run()
        {
			boolean bresult = false;
			int i = 0;
			int length = 0;
            Message msg;
            String url;
            String strresult;
			String Permissionstr;
			String Msgstr;
			JSONObject jsonObj;

			try
			{
				//是否有权限
				url = m_urlCanteen + "/MessApp/ZhiPanController/checkPermission/" + m_jobnumber;

				android.util.Log.i("cjwsjy", "------url="+url+"-------canteen");

				strresult = HttpClientUtil.HttpUrlConnectionGet(url, "UTF-8");
				if (strresult == null)
				{
					//没有权限
					msg = handlerhome.obtainMessage();
					msg.what = 2;
					handlerhome.sendMessage(msg);
					return;
				}

				length = strresult.length();
				if (length == 0)
				{
					//没有权限
					msg = handlerhome.obtainMessage();
					msg.what = 2;
					handlerhome.sendMessage(msg);
					return;
				}

				jsonObj = new JSONObject(strresult);
				Permissionstr = jsonObj.getString("hasPermission");
				if (Permissionstr.equals("1"))
				{
					//有权限
					msg = handlerhome.obtainMessage();
					msg.what = 1;
					handlerhome.sendMessage(msg);
					return;
				}

				//没有权限
				if (Permissionstr.equals("0"))
				{
					Msgstr = jsonObj.getString("msg");

					msg = handlerhome.obtainMessage();
					msg.what = 0;
					msg.obj = Msgstr;
					handlerhome.sendMessage(msg);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
				msg = handlerhome.obtainMessage();
				msg.what = 2;
				handlerhome.sendMessage(msg);
			}
        }
    }

    //出差权限请求
	class ThreadOutOffice implements Runnable
	{
		@Override
		public void run()
		{
			boolean bresult = false;
			int i = 0;
			int length = 0;
			Message msg;
			String url;
			String strresult;
			String Permissionstr;
			String Msgstr;
			JSONObject jsonObj;

			try
			{
				//出差权限
				url = appUrl+"/OutWeb/EmployeeDynamicState/checkPermission/"+m_userId;
				strresult = HttpClientUtil.HttpUrlConnectionGet(url, "UTF-8");

				android.util.Log.i("cjwsjy", "------url="+url+"-------outoffice");

				//为空
				if (strresult == null)
				{
					//没有权限
					msg = handlerOutOffice.obtainMessage();
					msg.what = 2;
					handlerOutOffice.sendMessage(msg);
					return;
				}

				//为0
				length = strresult.length();
				if (length == 0)
				{
					//没有权限
					msg = handlerOutOffice.obtainMessage();
					msg.what = 2;
					handlerOutOffice.sendMessage(msg);
					return;
				}

				jsonObj = new JSONObject(strresult);
				Permissionstr = jsonObj.getString("showLeader");
				if (Permissionstr.equals("1"))
				{
					//有权限
					m_Leader = 1;
				}

				Permissionstr = jsonObj.getString("showCompanyUser");
				if (Permissionstr.equals("1"))
				{
					//有权限
					m_User = 1;
				}

				Permissionstr = jsonObj.getString("outExtraMenuType");
				if (Permissionstr.equals("1"))
				{
					//有权限
					m_OutExtra = 1;
				}

				msg = handlerOutOffice.obtainMessage();
				msg.what = 1;
				handlerOutOffice.sendMessage(msg);

			}
			catch (Exception e)
			{
				e.printStackTrace();
				msg = handlerOutOffice.obtainMessage();
				msg.what = 2;
				handlerOutOffice.sendMessage(msg);
			}
		}
	}

    Handler handlerhome = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            String url;
            this.obtainMessage();

			android.util.Log.i("cjwsjy", "----------msg.what="+msg.what+"-------canteen");

            switch (msg.what)
            {
                case 0:
					url = msg.obj.toString();
					Toast.makeText(getActivity(), url,Toast.LENGTH_SHORT).show();
                    break;
                case 1:  //有权限
					url = m_urlCanteen + "/MessApp/STManage/caipu/"+m_jobnumber+"/"+m_loginname+"/0";

					//url = "http://10.6.180.79:8080/MessApp/STManage/caipu/"+m_jobnumber+"/"+m_loginname+"/0";
					Intent intent = new Intent();
					intent.setClass(getActivity(), WebViewCanteen.class);
					intent.putExtra("webUrl",url);
					intent.putExtra("titleName","餐饮服务");
					startActivity(intent);

                    break;
                case 2:
                    Toast.makeText(getActivity(), "没有权限",Toast.LENGTH_SHORT).show();
                    break;
                default:

                    break;
            }
        }
    };

	private static class HandlerOutOffice extends Handler
	{
		private final WeakReference<FragmentHome> mFragment;

		public HandlerOutOffice(FragmentHome fragment) {
			mFragment = new WeakReference<FragmentHome>(fragment);
		}

		@Override
		public void handleMessage(Message msg)
		{
			FragmentHome fragment = mFragment.get();
			if (fragment != null)
			{
				switch( msg.what )
				{
					case 0:
						//必须升级
						break;
					case 1:
						//成功
						SharedPreferences.Editor editor = fragment.GetSP().edit();
						editor.putInt("isLeader", fragment.m_Leader);
						editor.putInt("isUser", fragment.m_User);
						editor.putInt("isOutExtra", fragment.m_OutExtra);
						editor.commit();

						break;
					case 2:
						//提示当前APP是最新版
						break;
				}
			}
		}
	}

	private final HandlerOutOffice handlerOutOffice = new HandlerOutOffice(this);

	private void attemptLogin()
	{

		/*int nport = 0;
		String IPaddress;
		String ports;
		String appid;
		String auth;

		//初始化SDK
		QYSDK.InitSDK(5);
		//创建session会话
		session = QYSDK.CreateSession(getActivity().getApplicationContext());

		//IPaddress = "117.28.255.16";
		IPaddress = "218.106.125.147";
		ports = "39100";  //39100  18081

		nport = Integer.valueOf(ports);
		appid = "wholeally";
		//auth = "czFYScb5pAu+Ze7rXhGh/+KgxMFQtw/4EkOwlq9xjm9GZ87T7kinBeiME6d8w4HzZNmnJIpQUIK7ZEM8xbu+utMwoI8ilfYVP3APq+CfVaHkMAVHncHZlw==";
		auth = "czFYScb5pAu+Ze7rXhGh/+DZG+EYxzXl6mU9JCMJ/F/rvEuURddsP/hN/Xzyf48WRweoOAiaI9vmlrBaCMrtn9FAt75ccbDU";

		m_progressDialog = ProgressDialog.show(getActivity(), "加载中...", "正  在  获  取  数  据 ...");
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

						Intent intent = new Intent(getActivity(), QyDeviceActivity.class);
						startActivity(intent);
					}
					else
					{
						m_progressDialog.cancel();
						//showToast("登录失败:"+String.valueOf(ret)+";或者ViewerLogin第一个或第二个参数错误");
						android.util.Log.d("cjwsjy", "------登录失败-------7");
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

	private int PermissionsRequest2(String strpermission, int code )
	{
		if( Build.VERSION.SDK_INT<23 ) return 1;

		//判断该权限
		int Permission = ContextCompat.checkSelfPermission(getActivity(), strpermission);
		if( Permission!= PackageManager.PERMISSION_GRANTED )
		{
			//没有权限，申请权限
			ActivityCompat.requestPermissions(getActivity(),new String[]{ strpermission}, code);
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
					Intent intent = new Intent( getActivity(), CaptureActivity.class);
					intent.putExtra("titleName","设备扫码");
					startActivity(intent);
				}
				else
				{
					// 拒绝
					Toast.makeText(getActivity(), "没有权限！", Toast.LENGTH_SHORT).show();
				}
				break;
			case 105:
				if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
				{
					// 同意
					SetBitmapMeeting(m_dahuipic1);
				}
				else
				{
					// 拒绝
					Toast.makeText(getActivity(), "没有权限！", Toast.LENGTH_SHORT).show();
				}
				break;
			case 106:
				if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
				{
					// 同意
					SetBitmapMeeting(m_zhibopic);
				}
				else
				{
					// 拒绝
					Toast.makeText(getActivity(), "没有权限！", Toast.LENGTH_SHORT).show();
				}
				break;
			default:
				super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}
}
