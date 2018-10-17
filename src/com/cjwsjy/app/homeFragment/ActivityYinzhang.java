package com.cjwsjy.app.homeFragment;

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
import java.util.Calendar;
import java.util.List;

public class ActivityYinzhang extends BaseActivity implements OnItemClickListener
{
	private SharedPreferences sp;
	private ThreadUtils m_threadlog;

	// 生成动态数组，加入数据
	List<FinishPeddingItem> listItems = new ArrayList<FinishPeddingItem>();

	//private AdaperXinwen listItemAdapter;
	private AdaperBaoxiao listItemAdapter;
	private String m_loginname;
	private String appUrl;
	private String m_yinzhang;

	ListView list;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_homelist);

		m_threadlog = ThreadUtils.getInstance();  // 得到单例对象
		
		sp = SmApplication.sp;
		m_loginname = sp.getString("USERDATA.LOGIN.NAME", "");
		
		appUrl = UrlUtil.HOST;

		m_yinzhang = getIntent().getStringExtra("number");

		list = (ListView) findViewById(R.id.homelist);
		
		TextView tv_navtitle = (TextView) findViewById(R.id.tv_navtitle);
		tv_navtitle.setText("印章管理");

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
		listItem1.setIv_icon1(R.drawable.home_yinzhang01);
		listItem1.setTv_title("印章待办");
		listItem1.setTv_date(m_yinzhang);
		listItems.add(listItem1);
		
		FinishPeddingItem listItem2 = new FinishPeddingItem();
		listItem2.setIv_icon1(R.drawable.home_yinzhang02);
		listItem2.setTv_title("印章已办");
		listItems.add(listItem2);
		
		// 生成适配器的Item和动态数组对应的元素
		listItemAdapter = new AdaperBaoxiao( ActivityYinzhang.this, listItems );
		// 添加并且显示
		list.setAdapter(listItemAdapter);

		list.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) 
			{
				String url;
				FinishPeddingItem item = (FinishPeddingItem) list.getItemAtPosition(position);
				String formId = item.getTv_formid();

				//获取当前年份
				Calendar cale = Calendar.getInstance();
				int nyear = cale.get(Calendar.YEAR);
				String m_year = Integer.toString(nyear);

				if( position==0 )
				{
					m_threadlog.setparm2("点击","印章管理-印章待办","印章待办-印章管理-首页");
					m_threadlog.writelog();

					url=appUrl+"/CEGWAPServer/GWManage/getMyTaskList_YZ/"+m_loginname;
					startWebView(url,"印章待办");
				}
				else if( position==1 )
				{
					m_threadlog.setparm2("点击","印章管理-印章已办", "印章已办-印章管理-首页");
					m_threadlog.writelog();

					url = appUrl+"/CEGWAPServer/GWManage/getMyAuditList_YZ/"+m_loginname+"/"+m_year;
					startWebView(url,"印章已办");
				}
			}
		});
		
		return list;
	}

	private void startWebView(String urlstr,String title)
	{
		Intent intent = new Intent();           	
		intent.setClass( ActivityYinzhang.this, WebViewHome.class );
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
