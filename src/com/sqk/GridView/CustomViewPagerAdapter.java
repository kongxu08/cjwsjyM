package com.sqk.GridView;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class CustomViewPagerAdapter extends FragmentPagerAdapter {
    // 页面列表
    private List<Fragment> fragmentList;
    
    public CustomViewPagerAdapter(FragmentManager fm){
    	super(fm);
    }
    
	public CustomViewPagerAdapter(FragmentManager fm,List<Fragment> fragmentList) {
        super(fm);
        this.fragmentList = fragmentList;
   }
	@Override
   public Fragment getItem(int arg0) {
        return fragmentList.get(arg0);
   }
	
	@Override
   public int getCount() {
        return fragmentList.size();
   }
	
	@Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }
}
