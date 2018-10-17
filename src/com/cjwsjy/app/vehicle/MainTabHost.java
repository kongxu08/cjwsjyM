package com.cjwsjy.app.vehicle;



import com.cjwsjy.app.R;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.TextView;


/**
 * 底部tabhost的抽象基类
 * @author linchengliang
 *
 */
public abstract class MainTabHost extends TabActivity implements  OnKeyListener{
	private Activity thisActivity = this;
	
	private static TabHost tab_mTabHost;
	private TabWidget tab_mTabWidget;
	private LayoutInflater ly_mLayoutflater;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.maintabhost);
		
		ly_mLayoutflater = getLayoutInflater();
		tab_mTabHost = getTabHost();
		tab_mTabWidget = getTabWidget();
		prepare();
		initTabSpec();
	}
	
	/**
	 * 菜单按键事件
	 */
	@Override  
	public boolean onKey(View v, int keyCode, KeyEvent event) {  
       // TODO Auto-generated method stub  
       if ((keyCode == KeyEvent.KEYCODE_MENU)) {  
           	//Toast.makeText(thisActivity, "点击手机的菜单按键", Toast.LENGTH_SHORT).show();
            return true;  
        }  
       return false;  
	}  
	
	/**
	 * 创建菜单按钮
	 */
	@Override
	public abstract boolean onCreateOptionsMenu(Menu menu);
	
	/**
	 * 菜单点击事件
	 */
	@Override
	public abstract boolean onOptionsItemSelected(MenuItem item);
	
	
	/**
	 * 初始化tabhost里的tab
	 */
	private void initTabSpec() {
		int count = getTabItemCount();
		for (int i = 0; i < count; i++) {
			// set text view
			View tabItem = ly_mLayoutflater.inflate(R.layout.maintabitem, null);
			TextView tv_TabItem = (TextView) tabItem.findViewById(R.id.tab_item_tv);
			setTabItemTextView(tv_TabItem, i);
			// set id
			String l_tabItemId = getTabItemId(i);
			// set tab spec
			TabSpec tabSpec = tab_mTabHost.newTabSpec(l_tabItemId);
			tabSpec.setIndicator(tabItem);
			tabSpec.setContent(getTabItemIntent(i));
			tab_mTabHost.addTab(tabSpec);
		}
	}

	/** 在初始化界面之前调用 */
	protected void prepare() {
		// do nothing or you override it
	}
	
	/**
	 * 获取tab的个数
	 * @return
	 */
	protected int getTabCount() {
		return tab_mTabHost.getTabWidget().getTabCount();
	}

	/** 设置TabItem的图标和标题等*/
	abstract protected void setTabItemTextView(TextView textView, int position);

	abstract protected String getTabItemId(int position);
	
	abstract protected Intent getTabItemIntent(int position);

	abstract protected int getTabItemCount();
	
	/**
	 * 设置当前tab的索引
	 * @param index
	 */
	public static void setCurrentTab(int index) {
		tab_mTabHost.setCurrentTab(index);
	}
	
	/**
	 * 聚焦到当前的tab
	 * @param index
	 */
	protected void focusCurrentTab(int index) {
		tab_mTabWidget.focusCurrentTab(index);
	}

}
