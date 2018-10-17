package com.cjwsjy.app.pedometer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.cjwsjy.app.R;
import com.cjwsjy.app.SmApplication;
import com.cjwsjy.app.base.BaseActivity;
import com.cjwsjy.app.utils.HttpClientUtil;
import com.cjwsjy.app.utils.ThreadUtils;
import com.cjwsjy.app.utils.UrlUtil;
import com.cjwsjy.app.webview.WebViewHome;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ActivityPedometer extends BaseActivity implements SensorEventListener
{
	private SharedPreferences sp;

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
		setContentView(R.layout.activity_pedometer);

		ImageView imageview;

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

		imageview = (ImageView) this.findViewById(R.id.iv_outoffoffice);

		textview1 = (TextView)findViewById(R.id.text_pedometer1);
		textview2 = (TextView)findViewById(R.id.text_pedometer2);

		textview1.setText("STEP_COUNTER=");
		textview2.setText("STEP_DETECTOR=");


		startStepDetector();

		//点击进入会议界面
		imageview.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				startStepDetector();
			}
		});
		//Thread threadhttp = new Thread(new ThreadHttp());
		//threadhttp.start();
		
		//String list = "";
		//String url = "http://w802804.cjwsjy.com.cn:8080/HYServer/mvc/meeting/meetingIssue";
		//list = HttpClientUtil.sendRequestFromHttpClientGet(url,"UTF-8");
		
		//initList();
		//initGridView();
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy)
	{
		// accuracy に変更があった時の処理
	}

	@Override
	public void onSensorChanged(SensorEvent event)
	{
		Sensor sensor = event.sensor;
		float[] values = event.values;
		long timestamp = event.timestamp;
		int ntype = 0;

		ntype = sensor.getType();
		//TYPE_STEP_COUNTER
		if( ntype==Sensor.TYPE_STEP_COUNTER )
		{
			// 今までの歩数
			//Log.d("type_step_counter", String.valueOf(values[0]));
			g_stepcount2++;
			textview1.setText("STEP_COUNTER=" + g_stepcount2 + "歩");
		}

		if( ntype==Sensor.TYPE_STEP_DETECTOR )
		{
			// ステップを検知した場合にアクセス
			//Log.d("type_detector_counter", String.valueOf(values[0]));
			g_stepcount1++;
			textview2.setText("STEP_DETECTOR=" + g_stepcount1 + "歩");

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
		// TODO 自动生成的方法存根
		super.onResume();

		//注册事件 监听器
		mSensorManager.registerListener (this, mCounterSensor, SensorManager.SENSOR_DELAY_NORMAL);
		mSensorManager.registerListener(this, mDetectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	protected void onPause()
	{
		// TODO 自动生成的方法存根
		super.onPause();

		//解除
		mSensorManager.unregisterListener(this,mCounterSensor);
		mSensorManager.unregisterListener(this,mDetectorSensor);
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

	private void startStepDetector()
	{
		Sensor sensor;

		//获取传感器管理器的实例
		//获取传感器的服务，初始化传感器
		mSensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);

		//获得加速度传感器
		sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		//获得计步传感器 总数
		mCounterSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

		//获得计步传感器 瞬时
		mDetectorSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

		//此方法用来注册，只有注册过才会生效，参数：SensorEventListener的实例，Sensor的实例，更新速率
		//注册传感器，注册监听器
		mStepDetector2 = new StepDetector2(this);
		mSensorManager.registerListener(mStepDetector2, sensor,SensorManager.SENSOR_DELAY_FASTEST);
	}

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
		intent.setClass(ActivityPedometer.this, WebViewHome.class);
		intent.putExtra("webUrl",urlstr);
		intent.putExtra("titleName",title);
		startActivity(intent);
	}

}
