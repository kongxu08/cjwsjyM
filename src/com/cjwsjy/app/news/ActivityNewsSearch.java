package com.cjwsjy.app.news;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cjwsjy.app.ActivityLogin;
import com.cjwsjy.app.R;
import com.cjwsjy.app.base.BaseActivity;
import com.cjwsjy.app.main.MainActivity;
import com.cjwsjy.app.news.PullDownView.OnPullDownListener;
import com.cjwsjy.app.utils.HttpClientUtil;
import com.cjwsjy.app.utils.StringHelper;
import com.cjwsjy.app.utils.UrlUtil;
import com.cjwsjy.app.webview.WebViewHome;
import com.sqk.GridView.List_Item;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;

public class ActivityNewsSearch extends BaseActivity
{
    private String appUrl;
    
    private Button searchBtn;
    
    private EditText edit_title;
    private EditText edit_author;
    private EditText edit_department;
    
    private String str_title;
    private String str_author;
    private String str_department;

    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.news_search);
        
        edit_title = (EditText) findViewById(R.id.et_newsTitle);
        edit_author = (EditText) findViewById(R.id.et_newsAuthor);
        edit_department = (EditText) findViewById(R.id.et_publishDept);
        
        searchBtn = (Button) findViewById(R.id.btn_search);

		//设置标题栏
		TextView tv_navtitle = (TextView) findViewById(R.id.tv_navtitle);
		tv_navtitle.setText("新闻搜索");
		
		//取文本框数据
		
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
		
		//搜索
		searchBtn.setOnClickListener(new Button.OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				str_title = edit_title.getText().toString().trim();
		        str_author = edit_author.getText().toString().trim();
		        str_department = edit_department.getText().toString().trim();
		        
				if( str_title.length()<=0 && str_author.length()<=0 && str_department.length()<=0 )
				{
					Toast.makeText( ActivityNewsSearch.this, "搜索内容不能为空，请重新输入", Toast.LENGTH_SHORT).show();
				}
				else gotoNewsActivity();
			}
		});
		
		appUrl = UrlUtil.HOST;
    }
	
	private void gotoNewsActivity()
	{
		//跳转
		String url=appUrl+"/CEGWAPServer/XW/GetIPSList_Search2?title="+str_title+"&author="+str_author+"&deptname="+str_department+"&page=";
		Intent intent = new Intent();
		intent.setClass(this, ActivityNews.class);
		intent.putExtra("URL",url); 
		intent.putExtra("titleName","新闻搜索");
		intent.putExtra("Parameter","1");
		startActivity(intent);

		finish();
	}

//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
//    {
//    	List_Item item = listItems.get(position);
//    	String guid = item.getTv_infoGuid();
//
//		String url=appUrl+"/CEGWAPServer/XW/GetIPSForm/"+guid;
//		Intent intent = new Intent();           	
//		intent.setClass(this, WebViewHome.class);
//		intent.putExtra("webUrl",url);
//		intent.putExtra("titleName",titleName); 
//		startActivity(intent);
//		
//        //Toast.makeText(this, "啊，你点中我了 " + position, Toast.LENGTH_SHORT).show();
//    }
	

	
}
