package com.cjwsjy.app.homeFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.cjwsjy.app.R;
import com.cjwsjy.app.SmApplication;
import com.cjwsjy.app.base.BaseActivity;
import com.cjwsjy.app.item.FinishPeddingItem;
import com.cjwsjy.app.pedding.FinishPeddingListActivity;
import com.cjwsjy.app.utils.HttpClientUtil;
import com.cjwsjy.app.utils.ThreadUtils;
import com.cjwsjy.app.utils.UrlUtil;
import com.cjwsjy.app.webview.WebViewHome;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

public class ActivityGongwen extends BaseActivity implements OnItemClickListener 
{
	private SharedPreferences sp;
	private ThreadUtils m_threadlog;

	// 生成动态数组，加入数据
	List<FinishPeddingItem> listItems = new ArrayList<FinishPeddingItem>();

	private AdaperBaoxiao listItemAdapter;
	private String m_loginname;
	private String appUrl;
	private String m_year;

	private String m_gongwen1;
	private String m_gongwen2;
	private String m_gongwen3;

	ListView list;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature( Window.FEATURE_NO_TITLE );
		setContentView(R.layout.activity_homelist);

		m_threadlog = ThreadUtils.getInstance();  // 得到单例对象

		m_loginname = SmApplication.sp.getString("USERDATA.LOGIN.NAME", "");
		
		//得到当前年
		//Time time = new Time("GMT+8");
		//time.setToNow();
		//int nyear = time.year;

		Calendar cale = Calendar.getInstance();
		int nyear = cale.get(Calendar.YEAR);
		m_year = Integer.toString(nyear);
		
		appUrl = UrlUtil.HOST;
		 
		list = (ListView) findViewById(R.id.homelist);
		
		TextView tv_navtitle = (TextView) findViewById(R.id.tv_navtitle);
		tv_navtitle.setText("公文管理");

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

		//开线程，得到待办条数
		Thread ThreadBaoxiao = new Thread(new ThreadHttp());
		ThreadBaoxiao.start();

		initList();
	}

	public ListView initList() 
	{
		FinishPeddingItem listItem6 = new FinishPeddingItem();
		listItem6.setIv_icon1(R.drawable.home_gongwen08);
		listItem6.setTv_title("收文待办");
		listItem6.setTv_date("0");
		listItems.add(listItem6);

		FinishPeddingItem listItem7 = new FinishPeddingItem();
		listItem7.setIv_icon1(R.drawable.home_gongwen07);
		listItem7.setTv_title("发文待办");
		listItem7.setTv_date("0");
		listItems.add(listItem7);

		FinishPeddingItem listItem8 = new FinishPeddingItem();
		listItem8.setIv_icon1(R.drawable.home_gongwen09);
		listItem8.setTv_title("一般文件待办");
		listItem8.setTv_date("0");
		listItems.add(listItem8);

		FinishPeddingItem listItem5 = new FinishPeddingItem();
		listItem5.setIv_icon1(R.drawable.home_gongwen05);
		listItem5.setTv_title("经办文搜索");
		listItems.add(listItem5);
		
		// 生成适配器的Item和动态数组对应的元素
		listItemAdapter = new AdaperBaoxiao( ActivityGongwen.this, listItems );
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

				if( position==0 )
				{
					m_threadlog.setparm2("点击","公文管理-收文待办","收文待办-公文管理-首页");
					m_threadlog.writelog();

					url=appUrl+"/CEGWAPServer/GWManage/getMyTaskList_SW/"+m_loginname;
					startWebView(url,"收文待办");
				}
				else if( position==1 )
				{
					m_threadlog.setparm2("点击","公文管理-发文待办", "发文待办-公文管理-首页");
					m_threadlog.writelog();

					url = appUrl+"/CEGWAPServer/GWManage/getMyTaskList_FW/"+m_loginname;
					startWebView(url,"发文待办");
				}
				else if( position==2 )
				{
					m_threadlog.setparm2("点击","公文管理-一般文件待办", "一般文件待办-公文管理-首页");
					m_threadlog.writelog();

					url = appUrl+"/CEGWAPServer/GWManage/getMyTaskList_YBWJ/"+m_loginname;
					startWebView(url,"一般文件待办");
				}
				else if( position==3 )
				{
					m_threadlog.setparm2("点击","公文管理-公文搜索", "公文搜索-公文管理-首页");
					m_threadlog.writelog();

	        		url = appUrl+"/CEGWAPServer/GWManager/Search_GW.jsp?userName="+m_loginname;
	        		startWebView(url,"公文搜索");
				}
			}
		});
		return list;
	}

	private void fillList()
	{
		listItems.clear();

		//差旅费报销单
		FinishPeddingItem listItem1 = new FinishPeddingItem();
		listItem1.setIv_icon1(R.drawable.home_gongwen08);
		listItem1.setTv_title("收文待办");
		listItem1.setTv_date(m_gongwen1);
		listItems.add(listItem1);

		//一般报销单
		FinishPeddingItem listItem2 = new FinishPeddingItem();
		listItem2.setIv_icon1(R.drawable.home_gongwen07);
		listItem2.setTv_title("发文待办");
		listItem2.setTv_date(m_gongwen2);
		listItems.add(listItem2);

		//差旅费借款单
		FinishPeddingItem listItem3 = new FinishPeddingItem();
		listItem3.setIv_icon1(R.drawable.home_gongwen09);
		listItem3.setTv_title("一般文件待办");
		listItem3.setTv_date(m_gongwen3);
		listItems.add(listItem3);

		FinishPeddingItem listItem4 = new FinishPeddingItem();
		listItem4.setIv_icon1(R.drawable.home_gongwen05);
		listItem4.setTv_title("经办文搜索");
		listItems.add(listItem4);

		listItemAdapter.notifyDataSetChanged();
	}

	class ThreadHttp implements Runnable
	{
		@Override
		public void run()
		{
			Getdaiban();
		}
	}

	//得到待办消息的条数
	private void Getdaiban()
	{
		boolean bresult = false;
		int j = 0;
		int i = 0;
		int length = 0;
		String titlestr;
		String countstr;
		JSONObject jsonObj;

		try
		{
			String url = appUrl + "/CEGWAPServer/RecordTotal/getMyTaskTotal_GW/" + m_loginname;
			String resultStr = HttpClientUtil.HttpUrlConnectionGet(url, "UTF-8");
			length = resultStr.length();
			if( length==0 ) return;

			JSONArray jsonObjs;
			jsonObjs = new JSONArray( resultStr );

			length = jsonObjs.length();
			for (i = 0; i < length; i++)
			{
				jsonObj = jsonObjs.getJSONObject(i);

				// 总数
				titlestr = jsonObj.getString("title");
				countstr = jsonObj.getString("count");
				bresult = titlestr.equals("收文待办");
				if( bresult==true )
				{
					m_gongwen1 = countstr;
					continue;
				}

				bresult = titlestr.equals("发文待办");
				if( bresult==true )
				{
					m_gongwen2 = countstr;
					continue;
				}

				bresult = titlestr.equals("一般文件待办");
				if( bresult==true )
				{
					m_gongwen3 = countstr;
					continue;
				}
			}

			Message msg;
			msg = handler.obtainMessage();
			msg.what = 0;
			handler.sendMessage(msg);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	Handler handler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{

			this.obtainMessage();
			switch (msg.what)
			{
				case 0:
					fillList();
					break;
				case 1:  //登录时，用户密码错误
					//mDialog.cancel();
					Toast.makeText(getApplicationContext(), msg.obj.toString(),Toast.LENGTH_SHORT).show();
					//mDialog.cancel();
					break;
				case 2:
					//mDialog.cancel();
					Toast.makeText(getApplicationContext(), "网络连接超时",Toast.LENGTH_SHORT).show();
					break;
				default:
					break;
			}
		}
	};

	private void startWebView(String urlstr,String title)
	{
		Intent intent = new Intent();           	
		intent.setClass( ActivityGongwen.this, WebViewHome.class );
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
