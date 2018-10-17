package com.cjwsjy.app.Canteen;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cjwsjy.app.R;
import com.cjwsjy.app.SmApplication;
import com.cjwsjy.app.homeFragment.AdaperBaoxiao;
import com.cjwsjy.app.item.FinishPeddingItem;
import com.cjwsjy.app.utils.HttpClientUtil;
import com.cjwsjy.app.utils.ThreadUtils;
import com.cjwsjy.app.utils.UrlUtil;
import com.cjwsjy.app.webview.WebViewHome;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ActivityCanteenMain extends FragmentActivity implements OnItemClickListener
{
	private int m_index;
	private int currentTabIndex;

	private String m_loginname;
	private String m_jobnumber;
	private String appUrl;
	private String m_year;
	private String m_baoxiao1;
	private String m_baoxiao2;
	private String m_baoxiao3;
	private String m_baoxiao4;

	private String[] titles;
	private String[] fragmentsName;

	private SharedPreferences sp;

	private Button[] mTabs;
	private TextView[] textviews;
	private ImageView[] imagebuttons;
	private Fragment[] m_fragments;
	private FragmentCanteen1 fragment_1;
	private FragmentCanteen2 fragment_2;
	private FragmentCanteen3 fragment_3;
	private FragmentCanteen4 fragment_4;


	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature( Window.FEATURE_NO_TITLE );
		setContentView(R.layout.activity_canteen_main);

		m_index = 0;
		currentTabIndex = 0;
		
		sp = SmApplication.sp;
		m_loginname = sp.getString("USERDATA.LOGIN.NAME", "");
		m_jobnumber = sp.getString("USERDATA.USER.JOBNUMBER", "");

		appUrl = UrlUtil.HOST;

		fragment_1 = new FragmentCanteen1();
		fragment_2 = new FragmentCanteen2();
		fragment_3 = new FragmentCanteen3();
		fragment_4 = new FragmentCanteen4();

		//标题栏
		TextView tv_navtitle = (TextView) findViewById(R.id.tv_navtitle);
		tv_navtitle.setText("长江美食");

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

		initTab();
	}

	public int initTab()
	{
		m_fragments = new Fragment[] { fragment_1,fragment_2, fragment_3,fragment_4 };
		fragmentsName=new String[] {"FragmentHome","DialFragment","FragmentPhoneBook","FragmentPlate","FragmentEmployeeInfo"};
		titles = new String[] {"首页","智能拨号","通讯录","应用"};

		mTabs = new Button[4];
		mTabs[0] = (Button) findViewById(R.id.btn_conversation);
		mTabs[1] = (Button) findViewById(R.id.btn_address_list);
		mTabs[2] = (Button) findViewById(R.id.btn_find);
		mTabs[3] = (Button) findViewById(R.id.btn_profile);
		// select first tab
		mTabs[0].setSelected(true);

		// 添加显示第一个fragment
		getSupportFragmentManager().beginTransaction()
				.add(R.id.fragment_container, m_fragments[0],fragmentsName[0])
				.add(R.id.fragment_container, m_fragments[1],fragmentsName[1])
				.add(R.id.fragment_container, m_fragments[2],fragmentsName[2])
				.add(R.id.fragment_container, m_fragments[3],fragmentsName[3])
				.addToBackStack(fragmentsName[1])
				.hide(m_fragments[1])
				.addToBackStack(fragmentsName[2])
				.hide(m_fragments[2])
				.addToBackStack(fragmentsName[3])
				.hide(m_fragments[3])
				.show(m_fragments[0]).commit();
		return 1;
	}

	public void onTabClicked(View view)
	{
		switch (view.getId()) {
			case R.id.btn_conversation:
				m_index = 0;
				break;
			case R.id.btn_address_list:
				m_index = 1;
				break;
			case R.id.btn_find:
				m_index = 2;
				break;
			case R.id.btn_profile:
				m_index = 3;
				break;
		}

		if( currentTabIndex!=m_index )
		{
			FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
			trx.hide(m_fragments[currentTabIndex]);
			if (!m_fragments[m_index].isAdded())
			{
				trx.add(R.id.fragment_container, m_fragments[m_index]);
			}
			trx.show(m_fragments[m_index]).commit();
		}
		mTabs[currentTabIndex].setSelected(false);
		// set current tab selected
		mTabs[m_index].setSelected(true);
		currentTabIndex = m_index;
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
