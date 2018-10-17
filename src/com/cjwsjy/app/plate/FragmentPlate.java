package com.cjwsjy.app.plate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

//import org.apache.http.cookie.Cookie;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cjwsjy.app.R;
import com.cjwsjy.app.SmApplication;
import com.cjwsjy.app.homeFragment.ActivityBaoxiao;
import com.cjwsjy.app.homeFragment.ActivityBiaozhun;
import com.cjwsjy.app.homeFragment.ActivityDangan1;
import com.cjwsjy.app.pedometer.ActivityPedometer2;
import com.cjwsjy.app.scanning.CaptureActivity;
import com.cjwsjy.app.utils.ThreadUtils;
import com.cjwsjy.app.utils.UrlUtil;
import com.cjwsjy.app.news.ActivityNews;
import com.cjwsjy.app.news.ActivityNewsSearch;
import com.cjwsjy.app.pedding.FinishPeddingListActivity;
import com.cjwsjy.app.pedding.UnFinishPeddingListActivity;
import com.cjwsjy.app.vehicle.ActivityVehcilelogo;
import com.cjwsjy.app.vehicle.logoDispatchActivity;
import com.cjwsjy.app.webview.Activity_BQ;
import com.cjwsjy.app.webview.WebViewHome;
import com.cjwsjy.app.webview.WebViewBQ;

import com.sqk.GridView.Grid_Item;
import com.sqk.GridView.NewGridAdaper2;

public class FragmentPlate extends Fragment  
{
	private boolean m_isDispatcher;
	private GridView gridView;
	private GridView grid_plate2;
	private GridView grid_plate3;
	private GridView grid_plate4;
	private GridView grid_plate5;
	private GridView grid_plate6;
	private GridView grid_plate7;
	private GridView grid_plate8;
	private GridView grid_plate9;
	private GridView grid_plate10;
	private GridView grid_plate11;
	private GridView grid_plate12;
	private GridView grid_plate13;
	private GridView grid_plate14;
	private GridView grid_plate15;
	private GridView grid_plate16;
	private GridView grid_plate17;

	private int m_nbaobei;

	private int m_Leader;
	private int m_User;
	private int m_OutExtra;

	private String download_url = null;
	private String m_userId;
	private String m_loginname;
	private String m_jobnumber;
	private String m_model;
	private String m_operate;
	private String m_content;
	private String m_year;
	private String appUrl;
	private String url;
	
	private SharedPreferences sp;
	private ProgressDialog mDialog;
	private ThreadUtils m_threadlog;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) 
	{
		return inflater.inflate(R.layout.fragment_plate, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		m_threadlog = ThreadUtils.getInstance();  // 得到单例对象
		//获取用户登录名
		sp = SmApplication.sp;
		m_loginname = sp.getString("USERDATA.LOGIN.NAME", "");
		m_userId = sp.getString("USERDATA.USER.ID", "");
		m_jobnumber = sp.getString("USERDATA.USER.JOBNUMBER", "");
		m_isDispatcher = sp.getBoolean("isDispatcher", false);

		appUrl = UrlUtil.HOST;

		//获取当前年份
		Calendar cale = Calendar.getInstance();
		int nyear = cale.get(Calendar.YEAR);
		m_year = Integer.toString(nyear);

		//appUrl = com.cjwsjy.app.SmApplication.server_ip;

		gridView = (GridView) getView().findViewById(R.id.grid_plate);
		
		grid_plate2 =(GridView) getView().findViewById(R.id.grid_plate2);
		grid_plate3 =(GridView) getView().findViewById(R.id.grid_plate3);
		grid_plate4 =(GridView) getView().findViewById(R.id.grid_plate4);
		grid_plate5 =(GridView) getView().findViewById(R.id.grid_plate5);
		grid_plate6 =(GridView) getView().findViewById(R.id.grid_plate6);
		grid_plate7 =(GridView) getView().findViewById(R.id.grid_plate7);
		grid_plate8 =(GridView) getView().findViewById(R.id.grid_plate8);
		grid_plate9 =(GridView) getView().findViewById(R.id.grid_plate9);
		grid_plate10 =(GridView) getView().findViewById(R.id.grid_plate10);
		grid_plate11 =(GridView) getView().findViewById(R.id.grid_plate11);
		grid_plate12 =(GridView) getView().findViewById(R.id.grid_plate12);
		grid_plate13 =(GridView) getView().findViewById(R.id.grid_plate13);
		grid_plate14 =(GridView) getView().findViewById(R.id.grid_plate14);
		grid_plate15 =(GridView) getView().findViewById(R.id.grid_plate15);
		grid_plate16 =(GridView) getView().findViewById(R.id.grid_plate16);
		grid_plate17 =(GridView) getView().findViewById(R.id.grid_plate17);

		m_Leader = sp.getInt("isLeader", 0);
		m_User = sp.getInt("isUser", 0);
		m_OutExtra = sp.getInt("isOutExtra", 0);

		//出差显示3行，显示报备申请，报备列表
		int nheight = 0;
		//String baobei = sp.getString("USERDATA.BAOBEI.ID", "0");
		//m_nbaobei=Integer.parseInt(baobei);
		m_nbaobei = m_OutExtra;
		if (m_nbaobei==1)
		{
			ViewGroup.LayoutParams lp = gridView.getLayoutParams();
			nheight = lp.height;
			lp.height = nheight+nheight/2;
			gridView.setLayoutParams(lp);
		}

		//初始化GridView
		initGridView();
	}

	private void initGridView()
	{
		List<Grid_Item> lists1 = new ArrayList<Grid_Item>();
		List<Grid_Item> lists2 = new ArrayList<Grid_Item>();
		List<Grid_Item> lists3 = new ArrayList<Grid_Item>();
		List<Grid_Item> lists4 = new ArrayList<Grid_Item>();
		List<Grid_Item> lists5 = new ArrayList<Grid_Item>();
		List<Grid_Item> lists6 = new ArrayList<Grid_Item>();
		List<Grid_Item> lists7 = new ArrayList<Grid_Item>();
		List<Grid_Item> lists8 = new ArrayList<Grid_Item>();
		List<Grid_Item> lists9 = new ArrayList<Grid_Item>();
		List<Grid_Item> lists10 = new ArrayList<Grid_Item>();
		List<Grid_Item> lists11 = new ArrayList<Grid_Item>();
		List<Grid_Item> lists12 = new ArrayList<Grid_Item>();
		List<Grid_Item> lists13 = new ArrayList<Grid_Item>();
		List<Grid_Item> lists14 = new ArrayList<Grid_Item>();
		List<Grid_Item> lists15 = new ArrayList<Grid_Item>();
		List<Grid_Item> lists16 = new ArrayList<Grid_Item>();
		List<Grid_Item> lists17 = new ArrayList<Grid_Item>();
		
		lists1.add(new Grid_Item(R.drawable.plate1, "我要出差"));
		lists1.add(new Grid_Item(R.drawable.plate2, "审批待办"));
		//lists1.add(new Grid_Item(R.drawable.plate3, "在岗查询"));
		lists1.add(new Grid_Item(R.drawable.plate4, "登记历史"));
		//lists1.add(new Grid_Item(R.drawable.plate5, "查找员工"));

		if (m_Leader==1)
		{
			lists1.add(new Grid_Item(R.drawable.plate98, "领导动态"));
		}
		if (m_User==1)
		{
			lists1.add(new Grid_Item(R.drawable.plate99, "本单位员工动态"));
		}

		if (m_nbaobei==1)
		{
			lists1.add(new Grid_Item(R.drawable.plate78, "报备列表"));
			lists1.add(new Grid_Item(R.drawable.plate78, "报备列表"));
		}
		else if(m_nbaobei==2)
		{
			lists1.add(new Grid_Item(R.drawable.plate79, "报备申请"));
			lists1.add(new Grid_Item(R.drawable.plate78, "报备列表"));
		}

		lists2.add(new Grid_Item(R.drawable.plate8, "发文待办"));
		lists2.add(new Grid_Item(R.drawable.plate9, "发文已办"));
		lists2.add(new Grid_Item(R.drawable.plate10, "收文待办"));
		lists2.add(new Grid_Item(R.drawable.plate11, "收文已办"));
		lists2.add(new Grid_Item(R.drawable.plate12, "一般文件待办"));
		lists2.add(new Grid_Item(R.drawable.plate13, "一般文件已办"));
		lists2.add(new Grid_Item(R.drawable.plate24, "经办文搜索"));

		lists11.add(new Grid_Item(R.drawable.plate6, "院签报待办"));
		lists11.add(new Grid_Item(R.drawable.plate7, "院签报已办"));

		lists14.add(new Grid_Item(R.drawable.plate76, "(政务)接待申请"));
		lists14.add(new Grid_Item(R.drawable.plate77, "(商务、外事)接待申请"));
		lists14.add(new Grid_Item(R.drawable.plate73, "业务接待待办"));
		lists14.add(new Grid_Item(R.drawable.plate74, "业务接待已办"));

		lists16.add(new Grid_Item(R.drawable.plate68, "一般借款单"));
		lists16.add(new Grid_Item(R.drawable.plate69, "差旅费借款单"));

		lists3.add(new Grid_Item(R.drawable.plate14, "通知公告"));
		lists3.add(new Grid_Item(R.drawable.plate15, "特别关注"));
		lists3.add(new Grid_Item(R.drawable.plate16, "企业新闻"));
		lists3.add(new Grid_Item(R.drawable.plate17, "新闻搜索"));
		
		lists4.add(new Grid_Item(R.drawable.plate19, "印章待办"));
		lists4.add(new Grid_Item(R.drawable.plate18, "印章已办"));

		lists15.add(new Grid_Item(R.drawable.plate75, "档案管理"));

		lists5.add(new Grid_Item(R.drawable.plate20, "设备扫码"));

		if(m_isDispatcher==true)
		{
			lists6.add(new Grid_Item(R.drawable.plate22, "车辆调度"));
		}
		else
		{
			lists6.add(new Grid_Item(R.drawable.plate22, "车辆管理"));
		}
		
		lists7.add(new Grid_Item(R.drawable.plate23, "订会议室"));
		lists7.add(new Grid_Item(R.drawable.plate56, "会议申报待办"));
		lists7.add(new Grid_Item(R.drawable.plate57, "会议申报已办"));
		lists7.add(new Grid_Item(R.drawable.plate58, "劳务费审批待办"));
		lists7.add(new Grid_Item(R.drawable.plate59, "劳务费审批已办"));
		
		//项目管理
		lists8.add(new Grid_Item(R.drawable.plate25, "外委(科研)待办"));
		lists8.add(new Grid_Item(R.drawable.plate26, "外委(科研)已办"));
		lists8.add(new Grid_Item(R.drawable.plate27, "外委(生产)待办"));
		lists8.add(new Grid_Item(R.drawable.plate28, "外委(生产)已办"));
		lists8.add(new Grid_Item(R.drawable.plate33, "付款审签待办"));
		lists8.add(new Grid_Item(R.drawable.plate34, "付款审签已办"));
		lists8.add(new Grid_Item(R.drawable.plate51, "资质借用待办"));
		lists8.add(new Grid_Item(R.drawable.plate52, "资质借用已办"));
		lists8.add(new Grid_Item(R.drawable.plate53, "收入合同待办"));
		lists8.add(new Grid_Item(R.drawable.plate54, "收入合同已办"));
		lists8.add(new Grid_Item(R.drawable.plate61, "保函保证金待办"));
		lists8.add(new Grid_Item(R.drawable.plate62, "保函保证金已办"));
		lists8.add(new Grid_Item(R.drawable.plate66, "项目登记待办"));
		lists8.add(new Grid_Item(R.drawable.plate67, "项目登记已办"));
		lists8.add(new Grid_Item(R.drawable.plate63, "发票申请待办"));
		lists8.add(new Grid_Item(R.drawable.plate64, "发票申请已办"));
		lists8.add(new Grid_Item(R.drawable.plate82, "设校审待办"));
		lists8.add(new Grid_Item(R.drawable.plate83, "设校审已办"));
		lists8.add(new Grid_Item(R.drawable.plate86, "投标备案申请待办"));
		lists8.add(new Grid_Item(R.drawable.plate87, "投标备案申请已办"));
		lists8.add(new Grid_Item(R.drawable.plate90, "互提资料待办"));
		lists8.add(new Grid_Item(R.drawable.plate91, "互提资料已办"));
		lists8.add(new Grid_Item(R.drawable.plate84, "设计资料验证待办"));
		lists8.add(new Grid_Item(R.drawable.plate85, "设计资料验证已办"));
		lists8.add(new Grid_Item(R.drawable.plate80, "出院成果待办"));
		lists8.add(new Grid_Item(R.drawable.plate81, "出院成果已办"));
		lists8.add(new Grid_Item(R.drawable.plate88, "图纸会签待办"));
		lists8.add(new Grid_Item(R.drawable.plate89, "图纸会签已办"));
		lists8.add(new Grid_Item(R.drawable.plate92, "子项目部组建待办"));
		lists8.add(new Grid_Item(R.drawable.plate93, "子项目部组建已办"));
		lists8.add(new Grid_Item(R.drawable.plate65, "经办搜索"));
		
		lists9.add(new Grid_Item(R.drawable.plate96, "服务电话"));
		lists9.add(new Grid_Item(R.drawable.plate97, "我的发票抬头"));
		//lists9.add(new Grid_Item(R.drawable.plate60, "薪资查询"));
		//lists9.add(new Grid_Item(R.drawable.plate60, "计步"));

		lists10.add(new Grid_Item(R.drawable.plate94, "科研待办"));
		lists10.add(new Grid_Item(R.drawable.plate95, "科研已办"));

		lists12.add(new Grid_Item(R.drawable.plate60, "薪资查询"));

		lists13.add(new Grid_Item(R.drawable.homepage_icon11, "规程规范"));

		lists17.add(new Grid_Item(R.drawable.plate101, "我的待办"));
		lists17.add(new Grid_Item(R.drawable.plate102, "回国登记"));
		lists17.add(new Grid_Item(R.drawable.plate103, "动态跟踪"));

		DisplayMetrics metrics = new DisplayMetrics();
		Display display = getActivity().getWindowManager().getDefaultDisplay();
		display.getMetrics(metrics);
		int pixelsX = metrics.widthPixels;
		
		//setpixels(pixelsX);
        
		NewGridAdaper2 adaper = new NewGridAdaper2( getActivity(), lists1, pixelsX );
		gridView.setAdapter(adaper);
		
		NewGridAdaper2 plate2Adaper = new NewGridAdaper2( getActivity(), lists2, pixelsX);
		grid_plate2.setAdapter(plate2Adaper);
		
		NewGridAdaper2 plate3Adaper = new NewGridAdaper2( getActivity(), lists3, pixelsX);
		grid_plate3.setAdapter(plate3Adaper);
		
		NewGridAdaper2 plate4Adaper = new NewGridAdaper2( getActivity(), lists4, pixelsX);
		grid_plate4.setAdapter(plate4Adaper);
		
		NewGridAdaper2 plate5Adaper = new NewGridAdaper2( getActivity(), lists5, pixelsX);
		grid_plate5.setAdapter(plate5Adaper);
		
		NewGridAdaper2 plate6Adaper = new NewGridAdaper2( getActivity(), lists6, pixelsX);
		grid_plate6.setAdapter(plate6Adaper);
		
		NewGridAdaper2 plate7Adaper = new NewGridAdaper2( getActivity(), lists7, pixelsX);
		grid_plate7.setAdapter(plate7Adaper);
		
		NewGridAdaper2 plate8Adaper = new NewGridAdaper2( getActivity(), lists8, pixelsX);
		grid_plate8.setAdapter(plate8Adaper);
		
		NewGridAdaper2 plate9Adaper = new NewGridAdaper2( getActivity(), lists9, pixelsX);
		grid_plate9.setAdapter(plate9Adaper);

		NewGridAdaper2 plate10Adaper = new NewGridAdaper2( getActivity(), lists10, pixelsX);
		grid_plate10.setAdapter(plate10Adaper);

		NewGridAdaper2 plate11Adaper = new NewGridAdaper2( getActivity(), lists11, pixelsX);
		grid_plate11.setAdapter(plate11Adaper);

		NewGridAdaper2 plate12Adaper = new NewGridAdaper2( getActivity(), lists12, pixelsX);
		grid_plate12.setAdapter(plate12Adaper);

		NewGridAdaper2 plate13Adaper = new NewGridAdaper2( getActivity(), lists13, pixelsX);
		grid_plate13.setAdapter(plate13Adaper);

		NewGridAdaper2 plate14Adaper = new NewGridAdaper2( getActivity(), lists14, pixelsX);
		grid_plate14.setAdapter(plate14Adaper);

		NewGridAdaper2 plate15Adaper = new NewGridAdaper2( getActivity(), lists15, pixelsX);
		grid_plate15.setAdapter(plate15Adaper);

		NewGridAdaper2 plate16Adaper = new NewGridAdaper2( getActivity(), lists16, pixelsX);
		grid_plate16.setAdapter(plate16Adaper);

		NewGridAdaper2 plate17Adaper = new NewGridAdaper2( getActivity(), lists17, pixelsX);
		grid_plate17.setAdapter(plate17Adaper);
		
		//添加消息处理
		gridView.setOnItemClickListener(gridViewListener);
		grid_plate2.setOnItemClickListener(grid_plate2Listener);
		grid_plate3.setOnItemClickListener(grid_plate3Listener);
		grid_plate4.setOnItemClickListener(grid_plate4Listener);
		grid_plate5.setOnItemClickListener(grid_plate5Listener);
		grid_plate6.setOnItemClickListener(grid_plate6Listener);
		grid_plate7.setOnItemClickListener(grid_plate7Listener);
		grid_plate8.setOnItemClickListener(grid_plate8Listener);
		grid_plate9.setOnItemClickListener(grid_plate9Listener);
		grid_plate10.setOnItemClickListener(grid_plate10Listener);
		grid_plate11.setOnItemClickListener(grid_plate11Listener);
		grid_plate12.setOnItemClickListener(grid_plate12Listener);
		grid_plate13.setOnItemClickListener(grid_plate13Listener);
		grid_plate14.setOnItemClickListener(grid_plate14Listener);
		grid_plate15.setOnItemClickListener(grid_plate15Listener);
		grid_plate16.setOnItemClickListener(grid_plate16Listener);
		grid_plate17.setOnItemClickListener(grid_plate17Listener);
	}
	
	private void setpixels(int pixelsX)
	{
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 250);
		
		//LayoutParams  lp =new LayoutParams(LayoutParams.MATCH_PARENT,250);
		
		android.util.Log.d("cjwsjy", "------pixelsX="+pixelsX+"-------setpixels");
		
		if(pixelsX<=800)
		{
			lp.height = 600;
			gridView.setLayoutParams(lp);
			
			lp.height = 620;
			grid_plate2.setLayoutParams(lp);
			
			lp.height = 250;
			grid_plate3.setLayoutParams(lp);
			
			lp.height = 160;
			grid_plate4.setLayoutParams(lp);
			
			lp.height = 160;
			grid_plate5.setLayoutParams(lp);
			
			lp.height = 160;
			grid_plate6.setLayoutParams(lp);
			
			lp.height = 160;
			grid_plate7.setLayoutParams(lp);
		}
		else if( pixelsX<=1200 )
		{
			android.util.Log.d("cjwsjy", "------1200-------setpixels");
			lp.height = 1600;
			gridView.setLayoutParams(lp);
			
			lp.height = 320;
			grid_plate2.setLayoutParams(lp);
			
			lp.height = 250;
			grid_plate3.setLayoutParams(lp);
			
			lp.height = 160;
			grid_plate4.setLayoutParams(lp);
			
			lp.height = 160;
			grid_plate5.setLayoutParams(lp);
			
			lp.height = 160;
			grid_plate6.setLayoutParams(lp);
			
			lp.height = 160;
			grid_plate7.setLayoutParams(lp);
		}
		if(pixelsX<=1440)
		{
			;
		}
	}
	
	private void startWebView(String urlstr,String title)
	{
		Intent intent = new Intent();           	
		intent.setClass(getActivity(), WebViewHome.class);
		intent.putExtra("webUrl",urlstr);
		intent.putExtra("titleName",title);
		startActivity(intent);
	}

	//点回退键，就关闭WebActivity
	private void startWebView2(String urlstr,String title)
	{
		Intent intent = new Intent();
		intent.setClass(getActivity(), WebViewHome.class);
		intent.putExtra("webUrl",urlstr);
		intent.putExtra("titleName",title);
		intent.putExtra("isClose","1");
		startActivity(intent);
	}

	//出差审批
    private  OnItemClickListener  gridViewListener = new OnItemClickListener() 
    {
        @Override
        public void onItemClick(AdapterView<?> arg0,View arg1,int arg2,long arg3)
        {
        	if( arg3==0)  //我要出差
            {
				m_threadlog.setparm2("点击","出差审批-我要出差", "我要出差-应用");
    			m_threadlog.writelog();
    			
        		url=appUrl+"/OutWeb/wepRegisterOutOfOffice/"+m_userId;
    			Intent intent = new Intent();
    			intent.setClass(getActivity(), WebViewHome.class);
    			intent.putExtra("webUrl",url);
    			intent.putExtra("titleName","出差审批");
    			startActivity(intent);
            }

        	else if( arg3==1)  //审批待办
            {
    			m_threadlog.setparm2("点击","出差审批-审批待办", "审批待办-应用");
    			m_threadlog.writelog();
    			
        		url=appUrl+"/OutWeb/wapgTasks/"+m_userId;
        		Intent intent = new Intent();
    			intent.setClass(getActivity(), WebViewHome.class);
    			intent.putExtra("webUrl",url);
    			intent.putExtra("titleName","出差审批");
    			startActivity(intent);
            }
            else if( arg3==2 )  //登记历史
            {
    			m_threadlog.setparm2("点击","出差审批-登记历史", "登记历史-应用");
    			m_threadlog.writelog();
    			
            	url=appUrl+"/OutWeb/wapqueryout/"+m_userId;
            	Intent intent = new Intent();           	
    			intent.setClass(getActivity(), WebViewHome.class);
    			intent.putExtra("webUrl",url);
    			intent.putExtra("titleName","出差审批");
    			startActivity(intent);
            }
			else if( arg3==3 ) //领导动态
			{
				if(m_Leader==1)
				{
					m_threadlog.setparm2("点击", "领导动态-出差审批", "领导动态-出差审批-应用");
					m_threadlog.writelog();

					url = appUrl + "/OutWeb/EmployeeDynamicState/getLeaderStatus/" + m_userId;
					Intent intent = new Intent();
					intent.setClass(getActivity(), WebViewHome.class);
					intent.putExtra("webUrl", url);
					intent.putExtra("titleName", "出差审批");
					startActivity(intent);
				}
				else
				{
					m_threadlog.setparm2("点击","员工动态-出差审批","员工动态-出差审批-应用");
					m_threadlog.writelog();

					url=appUrl+"/OutWeb/EmployeeDynamicState/getCompanyUserStatus/"+m_userId+"/"+m_Leader;
					Intent intent = new Intent();
					intent.setClass(getActivity(), WebViewHome.class);
					intent.putExtra("webUrl",url);
					intent.putExtra("titleName","出差审批");
					startActivity(intent);
				}
			}
            else if( arg3==4 ) //员工动态
            {
    			m_threadlog.setparm2("点击","员工动态-出差审批","员工动态-出差审批-应用");
    			m_threadlog.writelog();
    			
            	url=appUrl+"/OutWeb/EmployeeDynamicState/getCompanyUserStatus/"+m_userId+"/"+m_Leader;
            	Intent intent = new Intent();           	
    			intent.setClass(getActivity(), WebViewHome.class);
    			intent.putExtra("webUrl",url);
    			intent.putExtra("titleName","出差审批");
    			startActivity(intent);
            }
			else if( arg3==5 ) //报备申请
			{
				if (m_nbaobei==1) //报备列表
				{
					m_threadlog.setparm2("点击","报备列表-出差审批","报备列表-出差审批-应用");
					m_threadlog.writelog();

					url=appUrl+"/OutWeb/wapqueryReportList/"+m_userId;
					Intent intent = new Intent();
					intent.setClass(getActivity(), WebViewHome.class);
					intent.putExtra("webUrl",url);
					intent.putExtra("titleName","出差审批");
					startActivity(intent);
				}
				else
				{
					m_threadlog.setparm2("点击", "报备申请-出差审批", "报备申请-出差审批-应用");
					m_threadlog.writelog();

					url = appUrl + "/OutWeb/wapRegReport/" + m_userId;
					Intent intent = new Intent();
					intent.setClass(getActivity(), WebViewHome.class);
					intent.putExtra("webUrl", url);
					intent.putExtra("titleName", "出差审批登记");
					startActivity(intent);
				}
			}
			else if( arg3==6 ) //报备列表
			{
				m_threadlog.setparm2("点击","报备列表","报备列表-出差审批登记-应用");
				m_threadlog.writelog();

				url=appUrl+"/OutWeb/wapqueryReportList/"+m_userId;
				Intent intent = new Intent();
				intent.setClass(getActivity(), WebViewHome.class);
				intent.putExtra("webUrl",url);
				intent.putExtra("titleName","出差审批登记");
				startActivity(intent);
			}
        }
    };

    //公文管理
    private  OnItemClickListener  grid_plate2Listener = new OnItemClickListener() 
    {
        @Override
        public void onItemClick(AdapterView<?> arg0,View arg1,int arg2,long arg3)
        {
        	if( arg3==0)  //发文待办
            {
    			m_threadlog.setparm2("点击","公文管理-发文待办", "发文待办-应用");
    			m_threadlog.writelog();
    			
        		url = appUrl+"/CEGWAPServer/GWManage/getMyTaskList_FW/"+m_loginname;
        		startWebView(url,"发文待办");
            }
        	else if( arg3==1)  //发文已办
            {
    			m_threadlog.setparm2("点击","公文管理-发文已办", "发文已办-应用");
    			m_threadlog.writelog();
    			
        		url = appUrl+"/CEGWAPServer/GWManage/getMyAuditList_FW/"+m_loginname+"/"+m_year;
        		startWebView(url,"发文已办");
            }
        	else if( arg3==2)  //收文待办
            {
    			m_threadlog.setparm2("点击","公文管理-收文待办","收文待办-应用");
    			m_threadlog.writelog();
    			
        		url=appUrl+"/CEGWAPServer/GWManage/getMyTaskList_SW/"+m_loginname;
        		startWebView(url,"收文待办");
            }
        	else if( arg3==3)  //收文已办
            {	
    			m_threadlog.setparm2("点击","公文管理-收文已办", "收文已办-应用");
    			m_threadlog.writelog();
    			
        		url = appUrl+"/CEGWAPServer/GWManage/getMyAuditList_SW/"+m_loginname+"/"+m_year;
        		startWebView(url,"收文已办");
            }
        	else if( arg3==4)  //一般文件待办
            {	
    			m_threadlog.setparm2("点击","公文管理-一般文件待办","一般文件待办-应用");
    			m_threadlog.writelog();
    			
        		url=appUrl+"/CEGWAPServer/GWManage/getMyTaskList_YBWJ/"+m_loginname;
        		startWebView(url,"一般文件待办");
            }
        	else if( arg3==5)  //一般文件已办
            {	
    			m_threadlog.setparm2("点击","公文管理-一般文件已办","一般文件已办-应用");
    			m_threadlog.writelog();
    			
        		url = appUrl+"/CEGWAPServer/GWManage/getMyAuditList_YBWJ/"+m_loginname+"/"+m_year;
        		startWebView(url,"一般文件已办");
            }
        	else if( arg3==6)  //公文搜索
            {	
    			m_threadlog.setparm2("点击","公文管理-经办文搜索","经办文搜索-应用");
    			m_threadlog.writelog();
    			
        		//moa.cispdr.com/CEGWAPServer/GWManager/Search_GW.jsp?userName=niuxinqiang
        		url = appUrl+"/CEGWAPServer/GWManager/Search_GW.jsp?userName="+m_loginname;
        		startWebView(url,"经办文搜索");
            }
        }
    };
    
	//新闻通知
    private  OnItemClickListener  grid_plate3Listener = new OnItemClickListener() 
    {
        @Override
        public void onItemClick(AdapterView<?> arg0,View arg1,int arg2,long arg3)
        {         
        	if( arg3==0)  //通知公告
            {
    			m_threadlog.setparm2("点击","新闻中心-通知公告","通知公告-应用");
    			m_threadlog.writelog();
    			
        		url = appUrl+"/CEGWAPServer/XW/GetIPSList_GGB2/";
        		Intent intent = new Intent( getActivity(), ActivityNews.class);
        		intent.putExtra("URL",url); 
        		intent.putExtra("titleName","通知公告");
        		intent.putExtra("Parameter","0"); 
				startActivity(intent);
            }
        	else if( arg3==1)  //特别关注
            {	
    			m_threadlog.setparm2("点击","新闻中心-特别关注","特别关注-应用");
    			m_threadlog.writelog();
    			
        		url = appUrl+"/CEGWAPServer/XW/GetIPSList_TBGZ2/";
        		Intent intent = new Intent( getActivity(), ActivityNews.class);
        		intent.putExtra("URL",url); 
        		intent.putExtra("titleName","特别关注");
        		intent.putExtra("Parameter","0");
				startActivity(intent);
            }
        	else if( arg3==2)  //企业新闻
            {	
    			m_threadlog.setparm2("点击","新闻中心-企业新闻", "企业新闻-应用");
    			m_threadlog.writelog();
    			
        		url = appUrl+"/CEGWAPServer/XW/GetIPSList_QYXW2/";
        		Intent intent = new Intent( getActivity(), ActivityNews.class);
        		intent.putExtra("URL",url); 
        		intent.putExtra("titleName","企业新闻");
        		intent.putExtra("Parameter","0");
				startActivity(intent);
            }
        	else if( arg3==3)  //新闻搜索
            {	
    			m_threadlog.setparm2("点击","新闻中心-新闻搜索", "新闻搜索-应用");
    			m_threadlog.writelog();
    			
        		Intent intent = new Intent( getActivity(), ActivityNewsSearch.class);
				startActivity(intent);
        		//Toast.makeText( getActivity(), message, Toast.LENGTH_SHORT).show();
            }
        	else if( arg3==4 )  //新闻统计
            {	
        		//Intent intent = new Intent( getActivity(), FinishPeddingListActivity.class);
				//startActivity(intent);
        		//Toast.makeText( getActivity(), message, Toast.LENGTH_SHORT).show();
            }
        }
    };
    
	//印章管理
    private  OnItemClickListener  grid_plate4Listener = new OnItemClickListener() 
    {
        @Override
        public void onItemClick(AdapterView<?> arg0,View arg1,int arg2,long arg3)
        {
        	if( arg3==0)  //印章待办
            {
    			m_threadlog.setparm2("点击","印章管理-印章待办","印章待办-应用");
    			m_threadlog.writelog();
    			
        		url=appUrl+"/CEGWAPServer/GWManage/getMyTaskList_YZ/"+m_loginname;
        		startWebView(url,"印章待办");
            }
        	else if( arg3==1)  //印章已办
            {	
    			m_threadlog.setparm2("点击","印章管理-印章已办","印章已办-应用");
    			m_threadlog.writelog();
    			
        		url = appUrl+"/CEGWAPServer/GWManage/getMyAuditList_YZ/"+m_loginname+"/"+m_year;
        		startWebView(url,"印章已办");
            }
        }
    };
    
	//经营管理
    private  OnItemClickListener  grid_plate8Listener = new OnItemClickListener() 
    {
        @Override
        public void onItemClick(AdapterView<?> arg0,View arg1,int arg2,long arg3)
        {
        	if( arg3==0)  //外委(科研)待办
            {
    			m_threadlog.setparm2("点击","项目管理-外委(科研)待办", "外委(科研)待办-应用");
    			m_threadlog.writelog();
    			
    			url = appUrl+"/CEGWAPServer/XMManage/outContractAuditKY_DB/"+m_loginname;
        		startWebView(url,"外委(科研)待办");
            }
        	else if( arg3==1)  //外委(科研)已办
            {	
    			m_threadlog.setparm2("点击","项目管理-外委(科研)已办", "外委(科研)已办-应用");
    			m_threadlog.writelog();
    			
        		url = appUrl+"/CEGWAPServer/XMManage/outContractAuditKY_YB/"+m_loginname;
        		startWebView(url,"外委(科研)已办");
            }
        	else if( arg3==2)  //外委(生产)待办
            {	
    			m_threadlog.setparm2("点击","项目管理-外委(生产)待办", "外委(生产)待办-应用");
    			m_threadlog.writelog();
    			
        		url = appUrl+"/CEGWAPServer/XMManage/outContractAuditSC_DB/"+m_loginname;
        		startWebView(url,"外委(生产)待办");
            }
        	else if( arg3==3)  //外委(生产)已办
            {	
    			m_threadlog.setparm2("点击","项目管理-外委(生产)已办", "外委(生产)已办-应用");
    			m_threadlog.writelog();
    			
        		url = appUrl+"/CEGWAPServer/XMManage/outContractAuditSC_YB/"+m_loginname;
        		startWebView(url,"外委(生产)已办");
            }
        	else if( arg3==4 )  //付款审签待办
            {	
    			m_threadlog.setparm2("点击","项目管理-付款审签待办", "付款审签待办-应用");
    			m_threadlog.writelog();
    			
        		url = appUrl+"/CEGWAPServer/XMManage/ContractPaySH_DB/"+m_loginname;
        		startWebView(url,"付款审签待办");
            }
        	else if( arg3==5 )  //付款审签已办
            {
    			m_threadlog.setparm2("点击","项目管理-付款审签已办","付款审签已办-应用");
    			m_threadlog.writelog();
    			
        		url = appUrl+"/CEGWAPServer/XMManage/ContractPaySH_YB/"+m_loginname;
        		startWebView(url,"付款审签已办");
            }
			else if( arg3==6 )  //资质借用待办
			{
				m_threadlog.setparm2("点击","项目管理-资质借用待办","资质借用待办-应用");
				m_threadlog.writelog();

				url = appUrl+"/CEGWAPServer/XMManage/qualification_DB/"+m_loginname;
				startWebView(url,"资质借用待办");
			}
			else if( arg3==7 )  //资质借用已办
			{
				m_threadlog.setparm2("点击","项目管理-资质借用已办", "资质借用已办-应用");
				m_threadlog.writelog();

				url = appUrl+"/CEGWAPServer/XMManage/qualification_YB/"+m_loginname;
				startWebView(url,"资质借用已办");
			}
			else if( arg3==8 )  //收入合同待办
			{
				m_threadlog.setparm2("点击","项目管理-收入合同待办","收入合同待办-应用");
				m_threadlog.writelog();

				url = appUrl+"/CEGWAPServer/XMManage/ProjectRiskEvaluation_DB/"+m_loginname;
				startWebView(url,"收入合同待办");
			}
			else if( arg3==9 )  //收入合同已办
			{
				m_threadlog.setparm2("点击","项目管理-收入合同已办","收入合同已办-应用");
				m_threadlog.writelog();

				url = appUrl+"/CEGWAPServer/XMManage/ProjectRiskEvaluation_YB/"+m_loginname;
				startWebView(url,"收入合同已办");
			}
			else if( arg3==10 )  //保函保证金待办
			{
				m_threadlog.setparm2("点击","项目管理-保函保证金待办", "保函保证金待办-应用");
				m_threadlog.writelog();

				url = appUrl+"/CEGWAPServer/XMManage/BackLetter_DB/"+m_loginname;
				startWebView(url,"保函保证金待办");
			}
			else if( arg3==11 )  //保函保证金已办
			{
				m_threadlog.setparm2("点击","项目管理-保函保证金已办","保函保证金已办-应用");
				m_threadlog.writelog();

				url = appUrl+"/CEGWAPServer/XMManage/BackLetter_YB/"+m_loginname;
				startWebView(url,"保函保证金已办");
			}
			else if( arg3==12 )  //项目登记待办
			{
				m_threadlog.setparm2("点击","项目管理-项目登记待办","项目登记待办-应用");
				m_threadlog.writelog();

				url = appUrl+"/CEGWAPServer/XMManage/ProjectRegister_DB/"+m_loginname;
				startWebView(url,"项目登记待办");
			}
			else if( arg3==13 )  //项目登记已办
			{
				m_threadlog.setparm2("点击","项目管理-项目登记已办", "项目登记已办-应用");
				m_threadlog.writelog();

				url = appUrl+"/CEGWAPServer/XMManage/ProjectRegister_YB/"+m_loginname;
				startWebView(url,"项目登记已办");
			}
			else if( arg3==14 )  //发票申请待办
			{
				m_threadlog.setparm2("点击","项目管理-发票申请待办", "发票申请待办-应用");
				m_threadlog.writelog();

				url = appUrl+"/CEGWAPServer/XMManage/InvoiceInfo_DB/"+m_loginname;
				startWebView(url,"发票申请待办");
			}
			else if( arg3==15 )  //发票申请已办
			{
				m_threadlog.setparm2("点击","项目管理-发票申请已办", "发票申请已办-应用");
				m_threadlog.writelog();

				url = appUrl+"/CEGWAPServer/XMManage/InvoiceInfo_YB/"+m_loginname;
				startWebView(url,"发票申请已办");
			}
			else if( arg3==16 )  //设校审待办
			{
				m_threadlog.setparm2("点击","设校审待办-项目管理", "设校审待办-应用");
				m_threadlog.writelog();

				url = appUrl+"/CEGWAPServer/AuditForm/DB/"+m_userId;
				startWebView(url,"设校审待办");
			}
			else if( arg3==17 )  //设校审已办
			{
				m_threadlog.setparm2("点击","设校审已办-项目管理", "设校审已办-应用");
				m_threadlog.writelog();

				url = appUrl+"/CEGWAPServer/AuditForm/YB/"+m_userId;
				startWebView(url,"设校审已办");
			}
			else if( arg3==18 )  //投标备案待办
			{
				m_threadlog.setparm2("点击","投标备案待办-项目管理", "投标备案待办-应用");
				m_threadlog.writelog();

				url = appUrl+"/CEGWAPServer/BidRecordApply/DB/"+m_userId;
				startWebView(url,"投标备案待办");
			}
			else if( arg3==19 )  //投标备案已办
			{
				m_threadlog.setparm2("点击","投标备案已办-项目管理", "投标备案已办-应用");
				m_threadlog.writelog();

				url = appUrl+"/CEGWAPServer/BidRecordApply/YB/"+m_userId;
				startWebView(url,"投标备案已办");
			}
			else if( arg3==20 )  //互提资料待办
			{
				m_threadlog.setparm2("点击","互提资料待办-项目管理", "互提资料待办-应用");
				m_threadlog.writelog();

				url = appUrl+"/CEGWAPServer/CollaborationData/DB/"+m_userId;
				startWebView(url,"互提资料待办");
			}
			else if( arg3==21 )  //互提资料已办
			{
				m_threadlog.setparm2("点击","互提资料已办-项目管理", "互提资料已办-应用");
				m_threadlog.writelog();

				url = appUrl+"/CEGWAPServer/CollaborationData/YB/"+m_userId;
				startWebView(url,"投标备案已办");
			}
			else if( arg3==22 )  //设计资料验证待办
			{
				m_threadlog.setparm2("点击","设计资料验证待办-项目管理", "设计资料验证待办-应用");
				m_threadlog.writelog();

				url = appUrl+"/CEGWAPServer/DesignDataVerify/DB/"+m_userId;
				startWebView(url,"设计资料验证待办");
			}
			else if( arg3==23 )  //设计资料验证已办
			{
				m_threadlog.setparm2("点击","设计资料验证已办-项目管理", "设计资料验证已办-应用");
				m_threadlog.writelog();

				url = appUrl+"/CEGWAPServer/DesignDataVerify/YB/"+m_userId;
				startWebView(url,"设计资料验证已办");
			}
			else if( arg3==24 )  //出院成果待办
			{
				m_threadlog.setparm2("点击","出院成果待办-项目管理", "出院成果待办-应用");
				m_threadlog.writelog();

				url = appUrl+"/CEGWAPServer/ProjectOutReview/DB/"+m_userId;
				startWebView(url,"出院成果待办");
			}
			else if( arg3==25 )  //出院成果已办
			{
				m_threadlog.setparm2("点击","出院成果已办-项目管理", "出院成果已办-应用");
				m_threadlog.writelog();

				url = appUrl+"/CEGWAPServer/ProjectOutReview/YB/"+m_userId;
				startWebView(url,"出院成果已办");
			}
			else if( arg3==26 )  //图纸会签待办
			{
				m_threadlog.setparm2("点击","图纸会签待办-项目管理", "图纸会签待办-应用");
				m_threadlog.writelog();

				url = appUrl+"/CEGWAPServer/SignData/DB/"+m_userId;
				startWebView(url,"图纸会签待办");
			}
			else if( arg3==27 )  //图纸会签已办
			{
				m_threadlog.setparm2("点击","图纸会签已办-项目管理", "图纸会签已办-应用");
				m_threadlog.writelog();

				url = appUrl+"/CEGWAPServer/SignData/YB/"+m_userId;
				startWebView(url,"图纸会签已办");
			}
			else if( arg3==28 )  //子项目部组建待办
			{
				m_threadlog.setparm2("点击","子项目部组建待办-项目管理", "子项目部组建待办-应用");
				m_threadlog.writelog();

				url = appUrl+"/CEGWAPServer/SonDepartBuild/DB/"+m_userId;
				startWebView(url,"子项目部组建待办");
			}
			else if( arg3==29 )  //子项目部组建已办
			{
				m_threadlog.setparm2("点击","子项目部组建已办-项目管理", "子项目部组建已办-应用");
				m_threadlog.writelog();

				url = appUrl+"/CEGWAPServer/SonDepartBuild/YB/"+m_userId;
				startWebView(url,"子项目部组建已办");
			}
			else if( arg3==30 )  //经办搜索
			{
				m_threadlog.setparm2("点击","项目管理-经办搜索", "经办搜索-应用");
				m_threadlog.writelog();

				url = appUrl+"/CEGWAPServer/XMManage/search_XM/"+m_loginname;
				startWebView(url,"经办搜索");
			}
        }
    };
    
	//车辆管理
    private  OnItemClickListener  grid_plate6Listener = new OnItemClickListener() 
    {
        @Override
        public void onItemClick(AdapterView<?> arg0,View arg1,int arg2,long arg3)
        {     
        	if( arg3==0)  //车辆管理
            {
				if(m_isDispatcher==true)
				{
					m_threadlog.setparm2("点击","车辆管理-车辆调度", "车辆管理-应用");
					m_threadlog.writelog();

					Intent intent = new Intent(getActivity(), logoDispatchActivity.class);  //车辆调度
					startActivity(intent);
				}
				else
				{
					m_threadlog.setparm2("点击","车辆管理-车辆管理", "车辆管理-应用");
					m_threadlog.writelog();

					Intent intent = new Intent( getActivity(), ActivityVehcilelogo.class);  //车辆管理
					startActivity(intent);
				}
            }
        }
    };
    
	//会议管理
    private  OnItemClickListener  grid_plate7Listener = new OnItemClickListener() 
    {
        @Override
        public void onItemClick(AdapterView<?> arg0,View arg1,int arg2,long arg3)
        {     
        	if( arg3==0)  //订会议室
            {
    			m_threadlog.setparm2("点击","会议管理-订会议室", "订会议室-应用");
    			m_threadlog.writelog();

        		url = appUrl+"/CEGWAPServer/HYManage/getIndex_HY/"+m_loginname;
        		startWebView(url,"订会议室");
            }
        	else if( arg3==1)  //会议申报待办
            {
        		m_threadlog.setparm2("点击","会议管理-会议申报待办", "会议申报待办-应用");
    			m_threadlog.writelog();
    			
    			url = appUrl+"/CEGWAPServer/HYManage/MeetingFee_DB/"+m_loginname;
        		startWebView(url,"会议申报待办");
            }
        	else if( arg3==2)  //会议申报已办
            {
        		m_threadlog.setparm2("点击","会议管理-会议申报已办", "会议申报已办-应用");
    			m_threadlog.writelog();
    			
    			url = appUrl+"/CEGWAPServer/HYManage/MeetingFee_YB/"+m_loginname;
        		startWebView(url,"会议申报已办");
            }
        	else if( arg3==3)  //劳务费待办
            {
        		m_threadlog.setparm2("点击","会议管理-劳务费待办", "劳务费待办-应用");
    			m_threadlog.writelog();
    			
    			url = appUrl+"/CEGWAPServer/meeting/SkillAuditFee_DB/"+m_loginname;
        		startWebView(url,"劳务费审批待办");
            }
        	else if( arg3==4)  //劳务费已办
            {
        		m_threadlog.setparm2("点击","会议管理-劳务费已办", "劳务费已办-应用");
    			m_threadlog.writelog();
    			
    			url = appUrl+"/CEGWAPServer/meeting/SkillAuditFee_YB/"+m_loginname;
        		startWebView(url,"劳务费审批已办");
            }
        }
    };
    
	//资产管理
    private  OnItemClickListener  grid_plate5Listener = new OnItemClickListener() 
    {
        @Override
        public void onItemClick(AdapterView<?> arg0,View arg1,int arg2,long arg3)
        {         
        	if( arg3==0)  //设备扫码
            {
    			m_threadlog.setparm2("点击","资产管理-设备扫码", "设备扫码-应用");
    			m_threadlog.writelog();

				int ret = PermissionsRequest2(Manifest.permission.CAMERA,104);
				if(ret!=1) return;

        		Intent intent = new Intent( getActivity(), CaptureActivity.class);
        		intent.putExtra("titleName","设备扫码");
				startActivity(intent);
				
        		//Toast.makeText( getActivity(), "message", Toast.LENGTH_SHORT).show();
            }
        	else if( arg3==1)  //绑定设备
            {
        		//Intent intent = new Intent( getActivity(), FinishPeddingListActivity.class);
				//startActivity(intent);
        		Toast.makeText( getActivity(), "绑定设备", Toast.LENGTH_SHORT).show();
            }
        }
    };

	//便捷服务
	private  OnItemClickListener  grid_plate9Listener = new OnItemClickListener()
	{
		@Override
		public void onItemClick(AdapterView<?> arg0,View arg1,int arg2,long arg3)
		{
			if( arg3==0)  //便捷服务
			{
				m_threadlog.setparm2("点击","便捷服务-服务电话","服务电话-应用");
				m_threadlog.writelog();

				url = appUrl+"/CEGWAPServer/bianjiefuwu/"+m_loginname;
				startWebView(url,"服务电话");
			}
			else if( arg3==1)  //我的发票抬头
			{
				m_threadlog.setparm2("点击","便捷服务-我的发票抬头", "我的发票抬头-应用");
				m_threadlog.writelog();

				url = appUrl+"/CEGWAPServer/InvoiceController/getAll/"+m_userId;
				startWebView(url,"我的发票抬头");
			}
			else if( arg3==2 )  //计步
			{
				m_threadlog.setparm2("点击","计步","计步-应用");
				m_threadlog.writelog();

				//Intent intent = new Intent( getActivity(), ActivityPedometer2.class);
				//startActivity(intent);

				//url = "http://10.6.189.50:8028/testPortal/index-hasTouch.html";
				url = "http://10.6.189.12:82/portal/openbq?ObjectType=3&OpenType=Portal&ObjectID=1001A110000000000R7X&User=user09&Authkey=dc10561d1e88c9cbd0184654404fc69d";

				Intent intent = new Intent( getActivity(), Activity_BQ.class);
				intent.putExtra("titleName","市场经营商业分析");
				startActivity(intent);

//				Intent intent = new Intent();
//				intent.setClass(getActivity(), WebViewBQ.class);
//				intent.putExtra("webUrl",url);
//				intent.putExtra("titleName","按业务类型统计合同金额");
//				startActivity(intent);
			}
		}
	};

	//科研项目管理
	private  OnItemClickListener  grid_plate10Listener = new OnItemClickListener()
	{
		@Override
		public void onItemClick(AdapterView<?> arg0,View arg1,int arg2,long arg3)
		{
			if( arg3==0)  //科研已办
			{
				m_threadlog.setparm2("点击","科研已办-科研项目管理", "科研已办-应用");
				m_threadlog.writelog();

				url = "http://moa.cispdr.com:8087/KYManageAppWeb/ky/db/"+m_loginname;
				startWebView2(url,"科研已办");
			}
			else if( arg3==1)  //科研待办
			{
				m_threadlog.setparm2("点击","科研待办-科研项目管理", "科研待办-应用");
				m_threadlog.writelog();

				url = "http://moa.cispdr.com:8087/KYManageAppWeb/ky/ybList/"+m_loginname;
				startWebView2(url,"科研待办");
			}
			else if( arg3==2)  //一般报销单
			{
				m_threadlog.setparm2("点击","网上报销-一般报销单", "一般报销单-应用");
				m_threadlog.writelog();

				url = "http://moa.cispdr.com:8028/webApprove/?usercode="+m_jobnumber+"&billtype=2646";
				startWebView2(url,"一般报销单");
			}
			else if( arg3==3)  //差旅费报销单
			{
				m_threadlog.setparm2("点击","网上报销-差旅费报销单", "差旅费报销单-应用");
				m_threadlog.writelog();

				url = "http://moa.cispdr.com:8028/webApprove/?usercode="+m_jobnumber+"&billtype=2641";
				startWebView2(url,"差旅费报销单");
			}
		}
	};

	//院签报
	private  OnItemClickListener  grid_plate11Listener = new OnItemClickListener()
	{
		@Override
		public void onItemClick(AdapterView<?> arg0,View arg1,int arg2,long arg3)
		{
			if( arg3==0)  //院签报待办
			{
				m_threadlog.setparm2("点击","院签报-院签报待办", "院签报待办-应用");
				m_threadlog.writelog();

				Intent intent = new Intent( getActivity(), UnFinishPeddingListActivity.class);
				startActivity(intent);
			}
			else if( arg3==1)  //院签报已办
			{
				m_threadlog.setparm2("点击","院签报-院签报已办","院签报已办-应用");
				m_threadlog.writelog();

				Intent intent = new Intent( getActivity(), FinishPeddingListActivity.class);
				startActivity(intent);
			}
		}
	};

	//薪资查询
	private  OnItemClickListener  grid_plate12Listener = new OnItemClickListener()
	{
		@Override
		public void onItemClick(AdapterView<?> arg0,View arg1,int arg2,long arg3)
		{
			if( arg3==0)  //薪资查询
			{
				m_threadlog.setparm2("点击", "薪资查询", "薪资查询-应用");
				m_threadlog.writelog();

				url = "http://moa.cispdr.com:8087/WageSearchDsp/mvc/sms_verification/"+m_jobnumber;

				Intent intent = new Intent();
				intent.setClass(getActivity(), WebViewHome.class);
				intent.putExtra("webUrl",url);
				intent.putExtra("titleName","薪资查询");
				intent.putExtra("isClose","1");
				startActivity(intent);
			}
		}
	};

	//规程规范
	private  OnItemClickListener  grid_plate13Listener = new OnItemClickListener()
	{
		@Override
		public void onItemClick(AdapterView<?> arg0,View arg1,int arg2,long arg3)
		{
			if( arg3==0)  //规程规范
			{
				m_threadlog.setparm2("点击", "规程规范", "规程规范-应用");
				m_threadlog.writelog();

				Intent intent = new Intent( getActivity(), ActivityBiaozhun.class);
				startActivity(intent);
			}
		}
	};

	//业务接待
	private  OnItemClickListener  grid_plate14Listener = new OnItemClickListener()
	{
		@Override
		public void onItemClick(AdapterView<?> arg0,View arg1,int arg2,long arg3)
		{
			if( arg3==0)  //(政务)接待申请
			{
				m_threadlog.setparm2("点击", "(政务)接待申请", "(政务)接待申请-应用");
				m_threadlog.writelog();

				url = appUrl+"/CEGWAPServer/JD/createForm_zw/"+m_loginname;
				startWebView(url,"(政务)接待申请");
			}
			else if(arg3==1) //(商务，外事)接待申请
			{
				m_threadlog.setparm2("点击", "(商务、外事)接待申请", "(商务、外事)接待申请-应用");
				m_threadlog.writelog();

				url = appUrl+"/CEGWAPServer/JD/createForm_swws/"+m_loginname;
				startWebView(url,"(商务，外事)接待申请");
			}
			else if( arg3==2) //业务接待待办
			{
				m_threadlog.setparm2("点击", "业务接待待办", "业务接待待办-应用");
				m_threadlog.writelog();

				url = appUrl+"/CEGWAPServer/JD/getUserDBList/"+m_loginname;
				startWebView(url,"业务接待待办");
			}
			else if( arg3==3) //业务接待已办
			{
				m_threadlog.setparm2("点击", "业务接待已办", "业务接待已办-应用");
				m_threadlog.writelog();

				url = appUrl+"/CEGWAPServer/JD/getUserYBList/"+m_loginname+"/"+m_year;
				startWebView(url,"业务接待待办");
			}
		}
	};

	//档案管理
	private  OnItemClickListener  grid_plate15Listener = new OnItemClickListener()
	{
		@Override
		public void onItemClick(AdapterView<?> arg0,View arg1,int arg2,long arg3)
		{
			if( arg3==0)  //档案管理
			{
				m_threadlog.setparm2("点击", "档案管理", "档案管理-应用");
				m_threadlog.writelog();

				Intent intent = new Intent( getActivity(), ActivityDangan1.class);
				startActivity(intent);
			}
		}
	};

	//网上报销
	private  OnItemClickListener  grid_plate16Listener = new OnItemClickListener()
	{
		@Override
		public void onItemClick(AdapterView<?> arg0,View arg1,int arg2,long arg3)
		{
			if( arg3==0)  //一般借款单
			{
				m_threadlog.setparm2("点击", "一般借款单", "一般借款单-应用");
				m_threadlog.writelog();

				url = "http://moa.cispdr.com:8028/webApprove/?usercode="+m_jobnumber+"&billtype=2632";
				startWebView(url,"一般借款单");
			}
			else if( arg3==1)  //差旅费借款单
			{
				m_threadlog.setparm2("点击", "差旅费借款单", "差旅费借款单-应用");
				m_threadlog.writelog();

				url = "http://moa.cispdr.com:8028/webApprove/?usercode="+m_jobnumber+"&billtype=2631";
				startWebView(url,"差旅费借款单");
			}
		}
	};

	//出国
	private  OnItemClickListener  grid_plate17Listener = new OnItemClickListener()
	{
		@Override
		public void onItemClick(AdapterView<?> arg0,View arg1,int arg2,long arg3)
		{
			if( arg3==0)  //我的待办
			{
				m_threadlog.setparm2("点击", "我的待办-出国", "我的待办-出国-应用");
				m_threadlog.writelog();

				url = appUrl+"/CEGWAPServer/CGManage/getCGManage_DB/"+m_loginname;
				startWebView(url,"我的待办");
			}
			else if( arg3==1)  //回国登记
			{
				m_threadlog.setparm2("点击", "回国登记-出国", "回国登记-出国-应用");
				m_threadlog.writelog();

				url = appUrl+"/CEGWAPServer/CGManage/GoHomeRegister/"+m_loginname;
				startWebView(url,"回国登记");
			}
			else if( arg3==2)  //动态跟踪
			{
				m_threadlog.setparm2("点击", "动态跟踪-出国", "动态跟踪-出国-应用");
				m_threadlog.writelog();

				url = appUrl+"/CEGWAPServer/CGManage/DynamicTrack/"+m_loginname;
				startWebView(url,"动态跟踪");
			}
		}
	};

  	Handler downHandler = new Handler() 
	{
		@Override
		public void handleMessage(Message msg) 
		{
			//msg.what = 0;
			switch( msg.what ) 
			{
			case 0:
				//有新版本
				//UpdateManager upManager = new UpdateManager(getActivity(),download_url);
				//upManager.checkUpdateInfo();
				break;
			case 1:
				AlertDialog.Builder builder = new Builder(getActivity());
				builder.setTitle("消息提示");
				builder.setMessage("已经是最新版本，无需升级");
				builder.setPositiveButton("是", null);
				builder.show();
				break;
			case 2:
				// 没有客户端下载
				builder = new Builder(getActivity());
				builder.setTitle("消息提示");
				builder.setMessage("暂无客户端下载");
				builder.setPositiveButton("是", null);
				builder.show();
				break;
			case 100:
				Toast.makeText(getActivity(), "联系人数据已是最新", Toast.LENGTH_SHORT).show();
				mDialog.cancel();
				break ;
			case 101:
				mDialog.cancel();
				//showUpdateDialog();
				break;
			}
		}
	};

	private int PermissionsRequest2(String strpermission, int code )
	{
		if( Build.VERSION.SDK_INT<23 ) return 1;

		//判断该权限
		int Permission = ContextCompat.checkSelfPermission(getActivity(), strpermission);
		if( Permission!= PackageManager.PERMISSION_GRANTED )
		{
			//没有权限，申请权限
			ActivityCompat.requestPermissions(getActivity(),new String[]{ strpermission}, code);
			return 1014;
		}

		return 1;
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
	{
		switch (requestCode)
		{
			case 104:
				if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
				{
					// 同意
					Intent intent = new Intent( getActivity(), CaptureActivity.class);
					intent.putExtra("titleName","设备扫码");
					startActivity(intent);
				}
				else
				{
					// 拒绝
					Toast.makeText(getActivity(), "没有权限！", Toast.LENGTH_SHORT).show();
				}
				break;
			default:
				super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}
  	
}
