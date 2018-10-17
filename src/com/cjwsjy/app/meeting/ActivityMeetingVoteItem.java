package com.cjwsjy.app.meeting;

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
import com.cjwsjy.app.utils.HttpClientUtil;
import com.cjwsjy.app.utils.UrlUtil;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

public class ActivityMeetingVoteItem extends BaseActivity implements OnScrollListener, OnItemClickListener
{
	private SharedPreferences sp;
		
	//每页显示条数
	private int MaxDataNum = 10;
		
	//生成动态数组，加入数据 
	List<FinishPeddingItem> listItems= new ArrayList<FinishPeddingItem>();
	private FinishPeddingListAdaper listItemAdapter;
		
	private int lastVisibleIndex;
	private LinearLayout loadingLayout;

	Handler handler = new Handler();
	ListView list;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.meeting_voteitem);

		TextView tv_navtitle = (TextView) findViewById(R.id.tv_navtitle);
		tv_navtitle.setText("我的会议");

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
		list = (ListView) findViewById(R.id.finishpeddinglist);
		int i = 0;
		int count = 10;
		try
		{
			JSONArray jsonObjs = new JSONArray();
			jsonObjs = getJsonObjs();

			count = jsonObjs.length();
			if( count==0 )
			{
				FinishPeddingItem listItem = new FinishPeddingItem();

				listItem.setIv_icon1(R.drawable.index_blank);
				listItem.setTv_title("您暂无签报已办事项");
				listItem.setTv_state("");
				listItem.setTv_date("");
				listItem.setIv_icon2(R.drawable.index_blank);
				listItem.setTv_formid("");

				listItems.add(listItem);
			}
			else
			{
				for( i=0; i<count; i++)
				{
					JSONObject jsonObj = jsonObjs.getJSONObject(i);

					String createDate = jsonObj.getString("CreateTime");

					createDate = createDate.split("T")[0];

					FinishPeddingItem listItem = new FinishPeddingItem();

					listItem.setIv_icon1(R.drawable.pedding_index04);
					listItem.setTv_title(jsonObj.getString("Title"));
					listItem.setTv_state(jsonObj.getString("Step"));
					listItem.setTv_date(createDate);
					listItem.setIv_icon2(R.drawable.pedding_icon1);
					listItem.setTv_formid(jsonObj.getString("FormID"));

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
		listItemAdapter = new FinishPeddingListAdaper(ActivityMeetingVoteItem.this, listItems);
		// 添加并且显示
		list.setAdapter(listItemAdapter);
		// list.setOnItemClickListener(this);
		list.setOnScrollListener(this);
		if( count>0 )
		{
			list.setOnItemClickListener(new OnItemClickListener()
			{
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id)
				{
					// TODO 自动生成的方法存根
					FinishPeddingItem item = (FinishPeddingItem) list.getItemAtPosition(position);
					String formId = item.getTv_formid();

					/*Intent intent = new Intent(FinishPeddingListActivity.this, PeddingFrameActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("formId", formId);
					// 已办签报-"已办"
					bundle.putString("signFlag", "已办");
					intent.putExtras(bundle);
					startActivity(intent);*/
				}
			});
		}
		return list;
	}
		
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
		    
	    //加载数据
	    public void loadMoreData() {
	
			int count = listItemAdapter.getCount();
			if (count + 10 < MaxDataNum) {
				// 每次加载10条
				String title = "ccca TLS 1.3.11 发布！";
				String description = "6666 源码";
				String author = "qqq";
				for (int i = count; i < count + 10; i++) {
					 FinishPeddingItem listItem = new FinishPeddingItem();
					  
					  listItem.setIv_icon1(R.drawable.pedding_index04);
					  listItem.setTv_title(title);
					  listItem.setTv_state(description);
					  listItem.setTv_date(author);
					  listItem.setIv_icon2(R.drawable.pedding_icon1);
					  listItem.setTv_formid(author);
					  listItems.add(listItem);
				}
			} else {
				// 数据已经不足10条
				String title = "333 TLS 1.3.11 发布！";
				String description = "777 源码";
				String author = "aa";

				for (int i = count; i < MaxDataNum; i++) {
					 FinishPeddingItem listItem = new FinishPeddingItem();
					  
					  listItem.setIv_icon1(R.drawable.pedding_index04);
					  listItem.setTv_title(title);
					  listItem.setTv_state(description);
					  listItem.setTv_date(author);
					  listItem.setIv_icon2(R.drawable.pedding_icon1);
					  listItem.setTv_formid(author);
					  listItems.add(listItem);
				}
			}
		}
	    
	    @Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			// TODO 自动生成的方法存根
			//Log.d("MainActivity", "TagTest:" + listItemAdapter.getCount());
			// 滑到底部后自动加载，判断listview已经停止滚动并且最后可视的条目等于adapter的条目
			if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
					&& lastVisibleIndex == listItemAdapter.getCount()) {
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						loadMoreData();
						listItemAdapter.notifyDataSetChanged();
					}
				}, 2000);
			}
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			// TODO 自动生成的方法存根
			lastVisibleIndex = firstVisibleItem + visibleItemCount - 1;
			// 所有的条目已经和最大条数相等，则移除底部的View
			if (totalItemCount == MaxDataNum + 1) {
				// listView.removeFooterView(moreView);
				list.removeFooterView(loadingLayout);
				Toast.makeText(getApplicationContext(), "数据全部加载完成，没有更多数据",
						Toast.LENGTH_LONG).show();
			}
		}


		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,long id) 
		{
			// TODO 自动生成的方法存根
		}
		
		
	 public  JSONArray getJsonObjs()
	 {
		 	//获取用户登录名
			sp = SmApplication.sp;
			String loginname =sp.getString("USERDATA.LOGIN.NAME", "");
			//String appUrl = com.cjwsjy.app.SmApplication.server_ip;
			String appUrl = UrlUtil.HOST;
			String url=appUrl+"/CEGWAPServer/YQB/getJoinList/"+loginname;
			String jsonStr1="";
		 
			//jsonStr =AppHttpClientUtil.submitRegister(url, "");
			//Map<String, Object> resultMap = HttpClientUtil.sendRequestFromHttpClient(url, null, "UTF-8");

			//jsonStr1 = String.valueOf(resultMap.get("HTTP.RESULT.VLUEKEY"));
			
			jsonStr1 = HttpClientUtil.HttpUrlConnectionGet(url, "UTF-8");
			jsonStr1 = "{\"data\":" + jsonStr1 + "}";
			
			JSONArray jsonObjs = new JSONArray();
			try {
				jsonObjs = new JSONObject(jsonStr1).getJSONArray("data");
			} catch (JSONException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
			
			return jsonObjs;
	 }
}
