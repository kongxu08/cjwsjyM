package com.cjwsjy.app.vehicle;

/**
 * 该主框架主要采用tabhost的技术方式实现tab页面的切换，
 * 每一个tab页面切换后对应一个activity
 * @author panmp
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cjwsjy.app.R;

import android.R.integer;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

public class MainUITabHostActivity extends MainTabHost
{
	private Activity thisActivity = this;
	
	List<MainTabItem> tab_mItems; //tab的具体
	List<TextView> tabList =new ArrayList<TextView>();
	
	//Handler mHandler = new Handler();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setCurrentTab(0);
	}
	
	public static void setTab(){
		setCurrentTab(1);
	}
	/**在初始化TabWidget前调用
	 * 和TabWidget有关的必须在这里初始化*/
	@Override
	protected void prepare() {
			
		MainTabItem tab1 = new MainTabItem(
 			    "0",
 				"申请",	// title
 				R.drawable.main_tab1_button,	// icon
 				R.drawable.main_tab_bg,	// background
 				new Intent(this, vehileApplyActivity.class));	// intent
     
		MainTabItem tab2 = new MainTabItem(
				"1",
				"变更",
				R.drawable.main_tab2_button,
				R.drawable.main_tab_bg,
				new Intent(this, orderChangeActivity.class)
				);
		
		MainTabItem tab3 = new MainTabItem(
				"2",
				"查询",
				R.drawable.main_tab3_button,
				R.drawable.main_tab_bg,
				new Intent(this, orderQueryActivity.class));
		MainTabItem tab4 = new MainTabItem(
				"3",
				"评价",
				R.drawable.main_tab4_button,
				R.drawable.main_tab_bg,
				new Intent(this, vehileCommentActivity.class));
		
		tab_mItems = new ArrayList<MainTabItem>();
		tab_mItems.add(tab1);
		tab_mItems.add(tab2);
		tab_mItems.add(tab3);
		tab_mItems.add(tab4);

		// 设置分割线
		TabWidget tabWidget = getTabWidget();
		
		//设置切换事件
		setOnTabChangedListener();
//		tabWidget.setDividerDrawable(R.drawable.main_tab_cutline_click);
	}
	
	/**tab的title，icon，边距设定等等*/
	@SuppressLint("NewApi")
	@Override
	protected void setTabItemTextView(TextView textView, int position) {
	//	textView.setPadding(3, 3, 3, 3);
	//	textView.set
		textView.setGravity(Gravity.CENTER);
		textView.setText(tab_mItems.get(position).getTitle());
		textView.setTypeface(Typeface.SANS_SERIF);
		textView.setBackgroundResource(tab_mItems.get(position).getBg());
		//textView.setCompoundDrawablesWithIntrinsicBounds(0, tab_mItems.get(position).getIcon(), 0, 0);
		Resources res =this.getResources();
		 Drawable myImage = res.getDrawable(tab_mItems.get(position).getIcon());
		 myImage.setBounds(0, 10, myImage.getMinimumWidth(), myImage.getMinimumHeight());
		textView.setCompoundDrawables(null, myImage, null, null);
		
		tabList.add(textView);
	}
	
	/**
	 * 触发选项事件
	 */
	public void setOnTabChangedListener(){
		final TabHost mTabHost = getTabHost();
		mTabHost.setOnTabChangedListener(new OnTabChangeListener() { 
            @Override
            public void onTabChanged(String tabId) {
            	Log.i("pmp","出发tabhost的切换事件-->"+tabId);
            	for(int i=0;i<tabList.size();i++){
					TextView textView =tabList.get(i);
					if(i==Integer.parseInt(tabId)){
						textView.setTextColor(getResources().getColor(R.color.tab_btn_on));
					}else{
						textView.setTextColor(getResources().getColor(R.color.tab_btn));
					}
				}
             }
        }); 
	}

	@Override
	protected String getTabItemId(int position) {
		
		return tab_mItems.get(position).getTabId();
	}
	
	/**点击tab时触发的事件*/
	@Override
	protected Intent getTabItemIntent(int position) {
		return tab_mItems.get(position).getIntent();
	}

	@Override
	protected int getTabItemCount() {
		return tab_mItems.size();
	}
	
	/**
	 * 重写创建菜单按钮
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		return true;
	}
	
	/**
	 * 重写系统菜单
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		return true;
	}
	
	@Override
	public void onStart() {
		super.onStart();
	}
	
	
	@Override
	protected void onNewIntent(Intent intent) {
        String action = intent.getAction();
	}

	@Override
	public void onStop() {
		super.onStop();
	}
	
	@Override
    public void onDestroy() {
      //  Utils.setLogText(getApplicationContext(), Utils.logStringCache);
        super.onDestroy();
    }	
    
	// 点击返回按钮
		@Override
		public void onBackPressed() {
			finish();
		}

}
