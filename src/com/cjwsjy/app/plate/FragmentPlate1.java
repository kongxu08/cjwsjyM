package com.cjwsjy.app.plate;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import com.cjwsjy.app.R;
import com.cjwsjy.app.SmApplication;
import com.cjwsjy.app.outoffice.OutOfficeActivity;
import com.cjwsjy.app.pedding.FinishPeddingListActivity;
import com.cjwsjy.app.pedding.PeddingActivity;
import com.cjwsjy.app.pedding.UnFinishPeddingListActivity;
import com.cjwsjy.app.utils.UrlUtil;
import com.cjwsjy.app.webview.WebViewHome;
import com.sqk.GridView.Grid_Item;
import com.sqk.GridView.NewGridAdaper;

public class FragmentPlate1 extends Fragment  
{
	private GridView gridView;
	private ListView listView;
	private List<Grid_Item1> lists;
	
	String loginname;
	String appUrl;
	String url;
	
	private SharedPreferences sp;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) 
	{
		return inflater.inflate(R.layout.fragment_plate2, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
		//获取用户登录名
		 sp = SmApplication.sp;
		 loginname =sp.getString("USERDATA.LOGIN.NAME", "");
	     appUrl = UrlUtil.HOST;

		//gridView = (GridView) getView().findViewById(R.id.grid_plateTest);
		
		listView =(ListView)getView().findViewById(R.id.grid_plateTest);
		
		//初始化GridView
		initGridView();
	}

	private void initGridView()
	{
		
		lists = new ArrayList<Grid_Item1>();
		List<Grid_Item> lists1 = new ArrayList<Grid_Item>();
		
		lists1.add(new Grid_Item(R.drawable.plate3, "我要出差"));
		lists1.add(new Grid_Item(R.drawable.plate4, "审批待办"));
		lists1.add(new Grid_Item(R.drawable.plate5, "在岗查询"));
		lists1.add(new Grid_Item(R.drawable.plate6, "登记历史"));
		lists1.add(new Grid_Item(R.drawable.plate7, "查找员工"));
		
		List<Grid_Item> lists2 = new ArrayList<Grid_Item>();
		lists2.add(new Grid_Item(R.drawable.plate8, "院签报待办"));
		lists2.add(new Grid_Item(R.drawable.plate9, "院签报已办"));
		
		List<Grid_Item> lists3 = new ArrayList<Grid_Item>();
		lists3.add(new Grid_Item(R.drawable.plate8, "更新"));
		lists3.add(new Grid_Item(R.drawable.plate9, "升级"));
		
		GridAdaperPlate adaper1 = new GridAdaperPlate(getActivity(), lists1);
		GridAdaperPlate adaper2 = new GridAdaperPlate(getActivity(), lists2);
		GridAdaperPlate adaper3 = new GridAdaperPlate(getActivity(), lists3);
		
		lists.add(new Grid_Item1("出差审批",adaper1));
		lists.add(new Grid_Item1("院签报",adaper2));
		lists.add(new Grid_Item1("功能",adaper3));
		lists.add(new Grid_Item1("测试",adaper3));
		
		NewListAdaper adaper = new NewListAdaper(getActivity(),lists);
		listView.setAdapter(adaper);

	}
	
}
