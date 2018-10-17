package com.cjwsjy.app.plate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.cjwsjy.app.utils.StringHelper;
import com.cjwsjy.app.utils.UpdateManager;
import com.cjwsjy.app.utils.UrlUtil;
import com.cjwsjy.app.webview.WebViewHome;
import com.cjwsjy.app.pedding.PeddingActivity;
import com.cjwsjy.app.outoffice.OutOfficeActivity;
import com.cjwsjy.app.pedding.FinishPeddingListActivity;
import com.cjwsjy.app.pedding.UnFinishPeddingListActivity;
import com.do1.cjmobileoa.db.GetServiceData;

import com.sqk.GridView.Grid_Item;
import com.sqk.GridView.NewGridAdaper;

public class FragmentPlate2 extends Fragment  
{
//	private GridView gridView;
//	private GridView grid_plate2;
//	private GridView grid_plate3;
//	private GridView grid_plate4;
//	
//	private List<Grid_Item> lists;
	
	private GridView gridView;
	private ListView listView;
	private List<Grid_Item1> lists;
	
	private String download_url = null;
	private String loginname;
	private String appUrl;
	private String url;

	private SharedPreferences sp;
	
	private ProgressDialog mDialog;
	
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

		 listView =(ListView)getView().findViewById(R.id.grid_plateTest);
		 
//		gridView = (GridView) getView().findViewById(R.id.grid_plate);
//		
//		grid_plate2 =(GridView) getView().findViewById(R.id.grid_plate2);
//		grid_plate3 =(GridView) getView().findViewById(R.id.grid_plate3);
//		grid_plate4 =(GridView) getView().findViewById(R.id.grid_plate4);
		
		//初始化GridView
		initGridView();
	}

	private void initGridView()
	{
		lists = new ArrayList<Grid_Item1>();
		List<Grid_Item> lists1 = new ArrayList<Grid_Item>();
		List<Grid_Item> lists2 = new ArrayList<Grid_Item>();
		List<Grid_Item> lists3 = new ArrayList<Grid_Item>();
		List<Grid_Item> lists4 = new ArrayList<Grid_Item>();
		
		lists1.add(new Grid_Item(R.drawable.plate3, "我要出差"));
		lists1.add(new Grid_Item(R.drawable.plate4, "审批待办"));
		lists1.add(new Grid_Item(R.drawable.plate5, "在岗查询"));
		lists1.add(new Grid_Item(R.drawable.plate6, "登记历史"));
		lists1.add(new Grid_Item(R.drawable.plate7, "查找员工"));
		
		lists2.add(new Grid_Item(R.drawable.plate8, "院签报待办"));
		lists2.add(new Grid_Item(R.drawable.plate9, "院签报已办"));

		lists3.add(new Grid_Item(R.drawable.plate8, "更新"));
		lists3.add(new Grid_Item(R.drawable.plate8, "公文"));
		
		lists4.add(new Grid_Item(R.drawable.plate8, "发文待办"));
		lists4.add(new Grid_Item(R.drawable.plate8, "发文已办"));
		lists4.add(new Grid_Item(R.drawable.plate8, "收文待办"));
		lists4.add(new Grid_Item(R.drawable.plate8, "收文已办"));
		lists4.add(new Grid_Item(R.drawable.plate8, "一般文件待办"));
		lists4.add(new Grid_Item(R.drawable.plate8, "一般文件已办"));
		lists4.add(new Grid_Item(R.drawable.plate8, "印章待办"));
		lists4.add(new Grid_Item(R.drawable.plate8, "印章已办"));
		     
		GridAdaperPlate adaper1 = new GridAdaperPlate(getActivity(), lists1);
		GridAdaperPlate adaper2 = new GridAdaperPlate(getActivity(), lists2);
		GridAdaperPlate adaper3 = new GridAdaperPlate(getActivity(), lists3);
		GridAdaperPlate adaper4 = new GridAdaperPlate(getActivity(), lists4);
		
		lists.add(new Grid_Item1("出差审批",adaper1));
		lists.add(new Grid_Item1("院签报",adaper2));
		lists.add(new Grid_Item1("功能",adaper3));
		lists.add(new Grid_Item1("公文管理",adaper4));
		
		NewListAdaper adaper = new NewListAdaper(getActivity(),lists);
		listView.setAdapter(adaper);
	}
	
	//出差审批
    private  OnItemClickListener  gridViewListener = new OnItemClickListener() 
    {
        @Override
        public void onItemClick(AdapterView<?> arg0,View arg1,int arg2,long arg3)
        {
        	if( arg3==0)  //我要出差
            {
        		url=appUrl+"/OutWeb/registerOutOfOffice?userName="+loginname;
    			Intent intent = new Intent();
    			intent.setClass(getActivity(), WebViewHome.class);
    			intent.putExtra("webUrl",url);
    			intent.putExtra("titleName","出差审批登记");
    			startActivity(intent);
            }
        	else if( arg3==1)  //审批待办
            {	
        		url=appUrl+"/OutWeb/gTasks?userName="+loginname;
        		Intent intent = new Intent();           	
    			intent.setClass(getActivity(), WebViewHome.class);
    			intent.putExtra("webUrl",url);
    			intent.putExtra("titleName","出差审批登记"); 
    			startActivity(intent);
            }
            else if( arg3==2 )  //在岗查询
            {
            	url=appUrl+"/OutWeb/queryAttentions?userName="+loginname;
            	Intent intent = new Intent();           	
    			intent.setClass(getActivity(), WebViewHome.class);
    			intent.putExtra("titleName","出差审批登记"); 
    			intent.putExtra("webUrl",url);
    			startActivity(intent);
            }
            else if( arg3==3 )  //登记历史
            {
            	url=appUrl+"/OutWeb/queryOutOfOffice?userName="+loginname;
            	Intent intent = new Intent();           	
    			intent.setClass(getActivity(), WebViewHome.class);
    			intent.putExtra("webUrl",url);
    			intent.putExtra("titleName","出差审批登记");
    			startActivity(intent);
            }
            else if( arg3==4 ) //查找员工
            {
            	url=appUrl+"/OutWeb/getAllCompany?userName="+loginname;
            	Intent intent = new Intent();
    			intent.setClass(getActivity(), WebViewHome.class);
    			intent.putExtra("webUrl",url);
    			intent.putExtra("titleName","出差审批登记");
    			startActivity(intent);
            }
        }
    };

    //院签报
    private  OnItemClickListener  grid_plate2Listener = new OnItemClickListener() 
    {
        @Override
        public void onItemClick(AdapterView<?> arg0,View arg1,int arg2,long arg3)
        {         
        	if( arg3==0)  //院签报待办
            {	
        		Intent intent = new Intent( getActivity(), UnFinishPeddingListActivity.class);
				startActivity(intent);
            }
        	else if( arg3==1)  //院签报已办
            {	
        		Intent intent = new Intent( getActivity(), FinishPeddingListActivity.class);
				startActivity(intent);
        		//Toast.makeText( getActivity(), message, Toast.LENGTH_SHORT).show();
            }
        }
    };
    
	//更新
    private  OnItemClickListener  grid_plate3Listener = new OnItemClickListener() 
    {
        @Override
        public void onItemClick(AdapterView<?> arg0,View arg1,int arg2,long arg3)
        {         
        	if( arg3==0)  //更新
            {
//        		Thread downLoadThread = new Thread(mdownApkRunnable);
//				downLoadThread.start();
				
        		//Toast.makeText( getActivity(), "message", Toast.LENGTH_SHORT).show();
            }
        	else if( arg3==1)  //公文
            {	
        		url = "http://10.6.177.135:8080/CEGWAPServer/GWManage/getMyTaskList_FW/niuxinqiang";
  
    			Intent intent = new Intent();
    			intent.setClass(getActivity(), WebViewHome.class);
    			intent.putExtra("webUrl",url);
    			intent.putExtra("titleName","出差审批登记");
    			startActivity(intent);
            }
        }
    };
    
	//公文管理
    private  OnItemClickListener  grid_plate4Listener = new OnItemClickListener() 
    {
        @Override
        public void onItemClick(AdapterView<?> arg0,View arg1,int arg2,long arg3)
        {         
        	loginname = "niuxinqiang";
        	
        	if( arg3==0)  //发文待办
            {
        		url = appUrl+"/CEGWAPServer/GWManage/getMyTaskList_FW/"+loginname;
    			Intent intent = new Intent();
    			intent.setClass(getActivity(), WebViewHome.class);
    			intent.putExtra("webUrl",url);
    			intent.putExtra("titleName","出差审批登记");
    			startActivity(intent);
            }
        	else if( arg3==1)  //收文已办
            {	
        		url = appUrl+"/CEGWAPServer/GWManage/getMyAuditList_FW/"+loginname+"/2015";
    			Intent intent = new Intent();
    			intent.setClass(getActivity(), WebViewHome.class);
    			intent.putExtra("webUrl",url);
    			intent.putExtra("titleName","出差审批登记");
    			startActivity(intent);
            }
        	else if( arg3==2)  //公文
            {	
        		url = "http://10.6.177.135:8080/CEGWAPServer/GWManage/getMyTaskList_FW/niuxinqiang";
    			Intent intent = new Intent();
    			intent.setClass(getActivity(), WebViewHome.class);
    			intent.putExtra("webUrl",url);
    			intent.putExtra("titleName","出差审批登记");
    			startActivity(intent);
            }
        }
    };
    
  /*//软件升级
  	private Runnable mdownApkRunnable = new Runnable()
  	{
  		@Override
  		public void run()
  		{
  			boolean bresult = false;
  			Message msg = downHandler.obtainMessage();

  			//获取服务器版本
  			Map<String, String> map = GetServiceData.softwareUp();
  			if( map == null ) return;

  			String result_code = map.get("result_code");
  			if (StringHelper.equals(result_code, "200"))
  			{
  				//和本地版本比较
  				String version = map.get("version");
  				String localVersion = SmApplication.sharedProxy.getString("curVersion", "1.0");
  				bresult = localVersion.equals(version);
  				
  				if( bresult==false )
  				{
  					//有新版本，下载升级
  					download_url = map.get("download_url");
  					download_url = "http://10.6.177.135:8082/ussp.server.prototype/assets/javascript/cjwsjy_2.5.3.apk";
  					msg.what = 0;
  					downHandler.sendMessage(msg);
  				}
  				else
  				{
  					msg.what = 1;  //最新版，无须升级
  					downHandler.sendMessage(msg);
  				}
  			}
  			else
  			{
  				msg.what = 2;  //沒有客户端下载
  				downHandler.sendMessage(msg);
  			}
  		}
  	};*/

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
				break ;
			}
		}
	};
  	
}
