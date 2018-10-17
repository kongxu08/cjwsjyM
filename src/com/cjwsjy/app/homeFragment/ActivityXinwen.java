package com.cjwsjy.app.homeFragment;

import java.util.ArrayList;
import java.util.List;

import com.cjwsjy.app.R;
import com.cjwsjy.app.SmApplication;
import com.cjwsjy.app.base.BaseActivity;
import com.cjwsjy.app.item.FinishPeddingItem;
import com.cjwsjy.app.news.ActivityNews;
import com.cjwsjy.app.news.ActivityNewsSearch;
import com.cjwsjy.app.utils.ThreadUtils;
import com.cjwsjy.app.utils.UrlUtil;
import com.cjwsjy.app.webview.WebViewHome;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionInflater;
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

public class ActivityXinwen extends BaseActivity implements OnItemClickListener 
{
	private SharedPreferences sp;
	private ThreadUtils m_threadlog;

	// 生成动态数组，加入数据
	List<FinishPeddingItem> listItems = new ArrayList<FinishPeddingItem>();

	private AdaperXinwen listItemAdapter;
	private String m_loginname;
	private String appUrl;

	ListView list;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
		setContentView(R.layout.activity_homelist);

		//Transition transitionSlideRight = TransitionInflater.from(this).inflateTransition(R.transition.slide_right);
		//getWindow().setEnterTransition(transitionSlideRight);
		//getWindow().setExitTransition(transitionSlideRight);

		m_threadlog = ThreadUtils.getInstance();  // 得到单例对象
		
		sp = SmApplication.sp;
		m_loginname = sp.getString("USERDATA.LOGIN.NAME", "");
		
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
		listItem1.setTv_title("通知公告");
		listItems.add(listItem1);
		
		FinishPeddingItem listItem2 = new FinishPeddingItem();
		listItem2.setIv_icon1(R.drawable.home_xinwen02);
		listItem2.setTv_title("特别关注");
		listItems.add(listItem2);

		FinishPeddingItem listItem3 = new FinishPeddingItem();
		listItem3.setIv_icon1(R.drawable.home_xinwen03);
		listItem3.setTv_title("企业新闻");
		listItems.add(listItem3);
		
		FinishPeddingItem listItem4 = new FinishPeddingItem();
		listItem4.setIv_icon1(R.drawable.home_xinwen04);
		listItem4.setTv_title("新闻搜索");
		listItems.add(listItem4);
		
		// 生成适配器的Item和动态数组对应的元素
		listItemAdapter = new AdaperXinwen( ActivityXinwen.this, listItems );
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
	        		Intent intent = new Intent( ActivityXinwen.this, ActivityNews.class);
	        		intent.putExtra("URL",url);
	        		intent.putExtra("titleName","通知公告");
	        		intent.putExtra("Parameter","0");
					startActivity(intent);
				}
				if( position==1 )
				{
					m_threadlog.setparm2("点击","新闻中心-特别关注", "特别关注-新闻中心-首页");
					m_threadlog.writelog();

	        		url = appUrl+"/CEGWAPServer/XW/GetIPSList_TBGZ2/";
	        		Intent intent = new Intent( ActivityXinwen.this, ActivityNews.class);
	        		intent.putExtra("URL",url); 
	        		intent.putExtra("titleName","特别关注");
	        		intent.putExtra("Parameter","0");
					startActivity(intent);
				}
				if( position==2 )
				{
					m_threadlog.setparm2("点击","新闻中心-企业新闻", "企业新闻-新闻中心-首页");
					m_threadlog.writelog();

	        		url = appUrl+"/CEGWAPServer/XW/GetIPSList_QYXW2/";
	        		Intent intent = new Intent( ActivityXinwen.this, ActivityNews.class);
	        		intent.putExtra("URL",url); 
	        		intent.putExtra("titleName","企业新闻");
	        		intent.putExtra("Parameter","0");
					startActivity(intent);
				}
				if( position==3 )
				{
					m_threadlog.setparm2("点击","新闻中心-新闻搜索", "新闻搜索-新闻中心-首页");
					m_threadlog.writelog();

	        		Intent intent = new Intent( ActivityXinwen.this, ActivityNewsSearch.class);
					startActivity(intent);
				}
			}
		});
		
		return list;
	}

	private void startWebView(String urlstr,String title)
	{
		Intent intent = new Intent();           	
		intent.setClass( ActivityXinwen.this, WebViewHome.class );
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
