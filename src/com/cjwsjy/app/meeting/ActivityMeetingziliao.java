package com.cjwsjy.app.meeting;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cjwsjy.app.R;
import com.cjwsjy.app.SmApplication;
import com.cjwsjy.app.adapter.FinishPeddingListAdaper;
import com.cjwsjy.app.base.BaseActivity;
import com.cjwsjy.app.item.FinishPeddingItem;
import com.cjwsjy.app.utils.CallOtherOpeanFile;
import com.cjwsjy.app.utils.HttpClientUtil;
import com.cjwsjy.app.utils.HttpDownloader;
import com.cjwsjy.app.utils.StringHelper;
import com.cjwsjy.app.utils.UrlUtil;
import com.cjwsjy.app.webview.WebViewHome;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;

public class ActivityMeetingziliao extends BaseActivity
{
	private SharedPreferences sp;
		
	//每页显示条数
	private int MaxDataNum = 10;
		
	//生成动态数组，加入数据 
	List<ListItem> listItems= new ArrayList<ListItem>();
	private ListAdaperMeeting listItemAdapter;
		
	private int lastVisibleIndex;
	private LinearLayout loadingLayout;
	private String m_username;

	Handler handler = new Handler();
	ListView listview;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.finishpeddinglist_layout);

		sp = SmApplication.sp;
		m_username = sp.getString("USERDATA.LOGIN.NAME", "");
		
		TextView tv_navtitle = (TextView) findViewById(R.id.tv_navtitle);
		tv_navtitle.setText("会议资料");

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
		// 绑定Layout里面的ListView
		listview = (ListView) findViewById(R.id.finishpeddinglist);
		int i = 0;
		int count = 10;
		String appUrl = UrlUtil.HOST;
		String url = "";
		String jsonStr1="";
		String jsonStr2="";
		String data1 = "";
		String data2 = "";
		String data3 = "";
		
		try
		{
			//http请求获得json
			JSONArray jsonObjs = new JSONArray();
			JSONArray jsonarray2 = new JSONArray();
			//jsonObjs = getJsonObjs();
			
			url = appUrl+":8087/HYServer/mvc/meeting/summary";
			jsonStr1 = HttpClientUtil.HttpUrlConnectionGet(url, "UTF-8");
			//jsonStr2 = "{\"data\":[" + jsonStr1 + "]}";

			JSONObject jsonObj2 = new JSONObject(jsonStr1);
			JSONObject jsonObj3 = new JSONObject();
			jsonarray2 = jsonObj2.getJSONArray("fj");
			
			count = jsonarray2.length();
			if(count==0)
			{
				ListItem listItem = new ListItem();
				listItem.setTv_title("暂无资料");
				listItems.add(listItem);
			}
			else
			{
				for( i=0; i<count; i++)
			
			{
				jsonObj3 = jsonarray2.getJSONObject(i);

				data1 = jsonObj3.getString("mz");  //名字
				data2 = jsonObj3.getString("dzmz");  //地址名字
				data3 = jsonObj3.getString("dx");  //大小
				
				ListItem listItem = new ListItem();

				listItem.setTv_title(data1);
				listItem.setTv_date(data2);
				listItem.setTv_state(data3);

				listItems.add(listItem);
			}
			}
		}
		catch (Exception e)
		{
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}

		// 生成适配器的Item和动态数组对应的元素
		listItemAdapter = new ListAdaperMeeting(ActivityMeetingziliao.this, listItems);
		// 添加并且显示
		listview.setAdapter(listItemAdapter);
		// list.setOnItemClickListener(this);
		listview.setOnItemClickListener(new OnItemClickListener()
		{	 
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) 
            {
                // TODO Auto-generated method stub
            	boolean bool = false;
            	int result = 0;
            	int leng = 0;
            	String suffix = "";
            	String downurl = "";
            	
            	String address = listItems.get(arg2).getTv_date();
            	bool = StringHelper.isEmpty(address);
            	if(bool==true) return;
            	
            	leng = address.length();
            	if(leng==0) return;
            	
            	Toast.makeText(ActivityMeetingziliao.this, "开始打开附件...", Toast.LENGTH_SHORT).show();
            	
            	//String address = listItems.get(arg2).getTv_date();
            	downurl = "http://moa.cispdr.com:8087/HYServer/download/"+address;
            	
				//得到附件文件后缀名
				result = address.lastIndexOf(".");
				suffix = address.substring(result+1, address.length());
				//下载附件
				downurls(downurl,suffix);
            }
		});

		return listview;
	}
	
	private int downurls(String url, String suffix)
	{
        String strurl = "";
        
        //编码转换
        try
		{
        	strurl = java.net.URLDecoder.decode(url,"UTF-8");
		}
		catch (UnsupportedEncodingException e)
		{
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
        
        DownAttachment(strurl, suffix);
          
        return 1;
	}
	
	//下载附件
	private int DownAttachment( String url, String suffix )
	{
		
		String fileUrl = null;
		String attachmentName = null;
		String filepath = "";
		
		fileUrl = url;
		attachmentName = getMD5Str(fileUrl)+"."+suffix;
		filepath = Environment.getExternalStorageDirectory() +"/Download"+ "/com.cjwsjy.app/attachment2/"+attachmentName;
		File file=new File(filepath);      
		if(!file.exists())
		{
		   HttpDownloader httpDownLoader=new HttpDownloader(); 
		   
           int result=httpDownLoader.downfile(fileUrl, "attachment2/", attachmentName);
			if (result == 0)
			{
				Toast.makeText(ActivityMeetingziliao.this, "打开附件成功！", Toast.LENGTH_SHORT).show();
			}
			else if (result == -1)
			{
				Toast.makeText(ActivityMeetingziliao.this, "打开附件失败！", Toast.LENGTH_SHORT).show();
				return 2;
			}
	     }	
	     //下载完成打开附件
		String sdPath = Environment.getExternalStorageDirectory() + "/Download" +"/com.cjwsjy.app/attachment2/";
		
		File file2 = new File(sdPath+attachmentName);
        //if(file.exists())
        CallOtherOpeanFile openfile = new CallOtherOpeanFile();
		openfile.openFile(ActivityMeetingziliao.this, file2);
		return 1;
	}
	
	/**
	 * MD5 加密
	 */
	private static String getMD5Str(String str)
	{
		MessageDigest messageDigest = null;
		try
		{
			messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.reset();
			messageDigest.update(str.getBytes("UTF-8"));
		}
		catch (NoSuchAlgorithmException e)
		{
			System.out.println("NoSuchAlgorithmException caught!");
			return null;
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
			return null;
		}

		byte[] byteArray = messageDigest.digest();
		StringBuffer md5StrBuff = new StringBuffer();
		for (int i = 0; i < byteArray.length; i++)
		{
			if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
				md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
			else
				md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
		}

		return md5StrBuff.toString();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
