package com.cjwsjy.app.pedometer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.cjwsjy.app.R;
import com.cjwsjy.app.SmApplication;
import com.cjwsjy.app.base.BaseActivity;
import com.cjwsjy.app.pedometerservice.StepService;
import com.cjwsjy.app.utils.ThreadUtils;
import com.cjwsjy.app.utils.UrlUtil;
import com.cjwsjy.app.webview.WebViewHome;

public class ActivityPedometer2 extends BaseActivity implements Handler.Callback
{
	//循环取当前时刻的步数中间的间隔时间
	private long TIME_INTERVAL = 500;

	private SharedPreferences sp;

	private Messenger messenger;
	private Messenger mGetReplyMessenger = new Messenger(new Handler(this));
	private Handler delayHandler;

	private int g_service = 0;
	private int g_stepcount1 = 0;
	private int g_stepcount2 = 0;

	//每页显示条数
	//private int m_isQiandao = 0;
	private String m_username;
	private String appUrl;
	private String url;

	private TextView textview1;
	private TextView textview2;
	private GridView gridView;
	private ThreadUtils m_threadlog;

	private SensorManager mSensorManager;
	private Sensor mCounterSensor;
	private Sensor mDetectorSensor;
	private StepDetector2 mStepDetector2;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_pedometer2);

		ImageView imageview;
		Button loginBtn1;
		Button loginBtn2;
		Button loginBtn3;

		// 得到单例对象
		m_threadlog = ThreadUtils.getInstance();

		sp = SmApplication.sp;
		m_username = sp.getString("USERDATA.LOGIN.NAME", "");

		TextView tv_navtitle = (TextView) findViewById(R.id.tv_navtitle);
		tv_navtitle.setText("计步器");

		// 后退
		ImageView iv_back = (ImageView) this.findViewById(R.id.iv_back);
		iv_back.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				finish();
			}
		});

		appUrl = UrlUtil.HOST;
		gridView = (GridView) findViewById(R.id.grid_plate);

		textview1 = (TextView)findViewById(R.id.text_pedometer1);
		textview2 = (TextView)findViewById(R.id.text_pedometer2);

		textview1.setText("STEP_COUNTER=");
		textview2.setText("STEP_DETECTOR=");

		delayHandler = new Handler(this);

		loginBtn1 = (Button) findViewById(R.id.button1);
		loginBtn2 = (Button) findViewById(R.id.button2);
		loginBtn3 = (Button) findViewById(R.id.button3);

		loginBtn1.setOnClickListener(new Button.OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				onclickbtn1();
			}
		});
		loginBtn2.setOnClickListener(new Button.OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				onclickbtn2();
			}
		});
		loginBtn3.setOnClickListener(new Button.OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				onclickbtn3();
			}
		});

		setupService();
		//startStepDetector();

		//点击进入会议界面
//		imageview.setOnClickListener(new OnClickListener()
//		{
//			public void onClick(View v)
//			{
//				//startStepDetector();
//			}
//		});
		//Thread threadhttp = new Thread(new ThreadHttp());
		//threadhttp.start();
		
		//String list = "";
		//String url = "http://w802804.cjwsjy.com.cn:8080/HYServer/mvc/meeting/meetingIssue";
		//list = HttpClientUtil.sendRequestFromHttpClientGet(url,"UTF-8");
		
		//initList();
		//initGridView();
	}

	private void onclickbtn1()
	{
		android.util.Log.d("cjwsjy", "---onclickbtn1-------ActivityPedometer");
		try
		{
			Message msg1 = Message.obtain(null, Constant.MSG_FROM_CLIENT1);
			msg1.replyTo = mGetReplyMessenger;
			messenger.send(msg1);
		}
		catch (RemoteException e)
		{
			e.printStackTrace();
		}
	}

	//启动服务
	private void onclickbtn2()
	{
		android.util.Log.d("cjwsjy", "---onclickbtn2-------ActivityPedometer");

		//当前是开启服务状态，返回
		if(g_service==1) return;

		Intent intent = new Intent(this, StepService.class);
		bindService(intent, conn, Context.BIND_AUTO_CREATE);
		startService(intent);

		g_service = 1;
	}

	//关闭服务
	private void onclickbtn3()
	{
		android.util.Log.d("cjwsjy", "---onclickbtn3-------ActivityPedometer");

		//当前是关闭状态，返回
		if(g_service==0) return;

		//先解除绑定
		unbindService(conn);

		//再关闭服务
		Intent intent = new Intent(this, StepService.class);
		stopService(intent);

		g_service = 0;
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
		// TODO 自动生成的方法存根
		super.onResume();
	}

	@Override
	protected void onPause()
	{
		// TODO 自动生成的方法存根
		super.onPause();
	}

	@Override
	protected void onDestroy()
	{
		// TODO 自动生成的方法存根
		super.onDestroy();

		if (mStepDetector2 != null)
		{
			mSensorManager.unregisterListener(mStepDetector2);
		}
	}

	ServiceConnection conn = new ServiceConnection()
	{
		@Override
		public void onServiceConnected(ComponentName name, IBinder service)
		{
			try
			{
				messenger = new Messenger(service);
				Message msg = Message.obtain(null, Constant.MSG_FROM_CLIENT);
				msg.replyTo = mGetReplyMessenger;
				messenger.send(msg);
			}
			catch (RemoteException e)
			{
				e.printStackTrace();
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {

		}
	};

	@Override
	public boolean handleMessage(Message msg)
	{
		int nstep = 0;
		String strtext = "";
		switch (msg.what)
		{
			case Constant.MSG_FROM_SERVER:
				//Log.v("xf","服务器返回的"+msg.getData().getInt("step"));
				nstep = msg.getData().getInt("step");
				strtext = Integer.toString(nstep)+"";
				textview2.setText( strtext );
				delayHandler.sendEmptyMessageDelayed(Constant.REQUEST_SERVER,TIME_INTERVAL);
				break;
			case Constant.MSG_FROM_SERVER2:
				//得到传感器的类型（加速度？还是计步）
				nstep = msg.getData().getInt("sensor");

				fromService2(nstep);

				break;
			case Constant.REQUEST_SERVER:
				try
				{
					Message msg1 = Message.obtain(null, Constant.MSG_FROM_CLIENT);
					msg1.replyTo = mGetReplyMessenger;
					messenger.send(msg1);
				}
				catch (RemoteException e)
				{
					e.printStackTrace();
				}

				break;
		}
		return false;
	}

	private void fromService2(int sensor)
	{
		if(sensor==1)
		{
			textview1.setText( "当前传感器：计步传感器" );
		}
		else
		{
			textview1.setText( "当前传感器：加速度传感器" );;
		}
	}

	private void setupService()
	{
		Intent intent = new Intent(this, StepService.class);
		bindService(intent, conn, Context.BIND_AUTO_CREATE);
		startService(intent);
		g_service = 1;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
