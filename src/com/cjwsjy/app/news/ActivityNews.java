package com.cjwsjy.app.news;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cjwsjy.app.R;
import com.cjwsjy.app.SmApplication;
import com.cjwsjy.app.base.BaseActivity;
import com.cjwsjy.app.news.PullDownView.OnPullDownListener;
import com.cjwsjy.app.utils.HttpClientUtil;
import com.cjwsjy.app.utils.StringHelper;
import com.cjwsjy.app.utils.UrlUtil;
import com.cjwsjy.app.webview.WebViewHome;
import com.sqk.GridView.List_Item;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;

public class ActivityNews extends BaseActivity implements OnPullDownListener, OnItemClickListener
{
	private static final int WHAT_DID_LOAD_DATA = 0;
    private static final int WHAT_DID_REFRESH = 1;
    private static final int WHAT_DID_MORE = 2;
 
    private List<List_Item> listItems;
    private NewsListAdaper listAdaper;
    
    private ListView mListView;
 
    private PullDownView mPullDownView;
    private String appUrl;
    private String Parastr;
    private String Paraurl;
    private String url;
    private String titleName;
    private String loginname;
    
    private SharedPreferences sp;
    
    private int currentPage=1;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.pulldown);
        
        //获得上个Activity传递的参数
        Paraurl = getIntent().getExtras().get("URL").toString();
        titleName = getIntent().getExtras().get("titleName").toString();
        Parastr = getIntent().getExtras().get("Parameter").toString();
		
		//设置标题栏
		TextView tv_navtitle = (TextView) findViewById(R.id.tv_navtitle);
		tv_navtitle.setText(titleName);
		
		ImageView iv_back = (ImageView) this.findViewById(R.id.iv_back);
		// 后退
		iv_back.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				onBackClick();
			}
		});
		
        /*
         * 1.使用PullDownView
         * 2.设置OnPullDownListener
         * 3.从mPullDownView里面获取ListView
         */
		
		sp = SmApplication.sp;
		loginname =sp.getString("USERDATA.LOGIN.NAME", "");
		
		currentPage = 1;
		appUrl = UrlUtil.HOST;
		
		listItems = new ArrayList<List_Item>();
		
        mPullDownView = (PullDownView) findViewById(R.id.pull_down_view);
        mPullDownView.setOnPullDownListener(this);
        mListView = mPullDownView.getListView();
        mListView.setOnItemClickListener(this);
        
        listAdaper = new NewsListAdaper(ActivityNews.this, listItems);
        mListView.setAdapter(listAdaper);
 
        mPullDownView.enableAutoFetchMore(true, 1);
 
        loadData();
    }

	@Override
	public void onBackPressed()
	{
		onBackClick();
	}
	
	public void onBackClick()
	{
		boolean bresult = Parastr.equals("0");
		if( bresult==true )
		{
			finish();
		}
		else
		{
			//回退到搜索界面
			Intent intent = new Intent( ActivityNews.this, ActivityNewsSearch.class);
			startActivity(intent);
			finish();
		}
	}
	
	//时间戳的转换成字符串时间
	public static String getDate(String timestampString)
	{  
		//Long timestamp = Long.parseLong(timestampString)*1000;
		//String date = new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new java.util.Date(timestamp));
		Long timestamp = Long.parseLong(timestampString);
		String date = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date(timestamp));  
		return date;  
	}  
	
    private void loadData()
    {
        new Thread(new Runnable() 
        {
            @Override
            public void run() 
            {
                List<List_Item> itemlist = new ArrayList<List_Item>();
                
                url = Paraurl+currentPage;
                //String url = appUrl+"/CEGWAPServer/XW/"+Paraurl+"/"+currentPage;
        		String resultStr = HttpClientUtil.HttpUrlConnectionGet(url, "UTF-8");         
        	    
        	    String jsonStr = "{\"data\":" + resultStr + "}";
    			
        	    ParseJson(jsonStr,itemlist);
        	    
    			currentPage++;
                Message msg = mUIHandler.obtainMessage(WHAT_DID_LOAD_DATA);
                msg.obj = itemlist;
                msg.sendToTarget();
            }
        }).start();
    }
 
    @Override
    public void onRefresh() 
    {
        new Thread(new Runnable() 
        {
            @Override
            public void run() 
            {
                Message msg = mUIHandler.obtainMessage(WHAT_DID_REFRESH);
                
                List<List_Item> itemlist = new ArrayList<List_Item>();
                
        		currentPage = 1;
        		url = Paraurl+currentPage;
        		//String url = appUrl+"/CEGWAPServer/XW/"+Paraurl+"/"+currentPage;
        		//String url = appUrl+"/CEGWAPServer/XW/GetIPSList_GGB/1";
        		String resultStr = HttpClientUtil.HttpUrlConnectionGet(url, "UTF-8");
        	    String jsonStr = "{\"data\":" + resultStr + "}";
        	    
        	    ParseJson(jsonStr,itemlist);
    			
    			currentPage++;
    			msg.obj = itemlist;
                //msg.obj = "After refresh " + System.currentTimeMillis();
                msg.sendToTarget();
            }
        }).start();
    }
 
    @Override
    public void onMore() 
    {
        new Thread(new Runnable() 
        {
 
            @Override
            public void run() 
            {
                Message msg = mUIHandler.obtainMessage(WHAT_DID_MORE);
                
                List<List_Item> itemlist = new ArrayList<List_Item>();
                
                url = Paraurl+currentPage;
        		//String url = appUrl+"/CEGWAPServer/XW/"+Paraurl+"/"+currentPage;	  
        		String resultStr = HttpClientUtil.HttpUrlConnectionGet(url, "UTF-8");
        	    
        	    String jsonStr = "{\"data\":" + resultStr + "}";
    			if( resultStr.isEmpty()==false )
    			{
    				ParseJson(jsonStr,itemlist);
	    			currentPage++;
	    			
	    			msg.obj = itemlist;
    			}
    			else
    			{
    				 msg.obj = "数据加载完毕！" + System.currentTimeMillis();
    			}

                msg.sendToTarget();
            }
        }).start();
    }

	public void ParseJson( String jsonStr, List<List_Item> itemlist)
	{
		int i = 0;
		int length = 0;
		String strings2;
	    String strbuf;
	    
		try 
		{
			JSONArray jsonObjs = new JSONObject(jsonStr).getJSONArray("data");
			length = jsonObjs.length();
			for( i=0; i<length; i++ ) 
			{
				List_Item item = new List_Item();
				
				JSONObject jsonObj = jsonObjs.getJSONObject(i);

				//标题
				strings2 = jsonObj.getString("Title");
				item.setTv_title(strings2);
				
				//部门
				strings2 = jsonObj.getString("GroupName");
				item.setTv_source(strings2);
				
				//时间
				strbuf = jsonObj.getString("ShowTime");
				//strings2 = getDate(strbuf);
				item.setTv_time(strbuf);

				//guid
				strings2 = jsonObj.getString("InfoGuid");
				item.setTv_infoGuid(strings2);
				
				itemlist.add(item);
			}
		} 
		catch (JSONException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}

    private Handler mUIHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) 
        {
        	List<List_Item> itemlist = (List<List_Item>) msg.obj;
        	
            switch (msg.what) 
            {
                case WHAT_DID_LOAD_DATA:
                {
                    if(msg.obj != null)
                    {
                        if(!itemlist.isEmpty())
                        {
                        	listItems.addAll(itemlist);
                            listAdaper.notifyDataSetChanged();
                            //mAdapter.notifyDataSetChanged();
                        }
                    }
                    //告诉它数据加载完毕;
                    mPullDownView.notifyDidLoad();
                    break;
                }
                case WHAT_DID_REFRESH :
                {
                	if(msg.obj != null)
                	{
                        if(!itemlist.isEmpty())
                        {
                        	listItems.clear();
                        	listItems.addAll(itemlist);
                        	listAdaper.notifyDataSetChanged();
                        }
                    }
                    //告诉它更新完毕
                    mPullDownView.notifyDidRefresh();
                    break;
                }
 
                case WHAT_DID_MORE:
                {
                	if(msg.obj != null)
                	{
                        if(!itemlist.isEmpty())
                        {
                            listItems.addAll(itemlist);
                            listAdaper.notifyDataSetChanged();
                        }
                    }
                    // 告诉它获取更多完毕
                    mPullDownView.notifyDidMore();
                    break;
                }
            }
        }
    };

    //单击listview，进入新闻详细页面
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
    {
    	List_Item item = listItems.get(position);
    	String guid = item.getTv_infoGuid();

		String url=appUrl+"/CEGWAPServer/XW/GetIPSForm/"+loginname+"/"+guid;
		Intent intent = new Intent();           	
		intent.setClass(this, WebViewHome.class);
		intent.putExtra("webUrl",url);
		intent.putExtra("titleName",titleName);
		intent.putExtra("huadong","1");
		startActivity(intent);
		
        //Toast.makeText(this, "啊，你点中我了 " + position, Toast.LENGTH_SHORT).show();
    }
	

	
}
