package com.cjwsjy.app.pedding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.cjwsjy.app.R;
import com.cjwsjy.app.SmApplication;
import com.cjwsjy.app.base.BaseActivity;
import com.cjwsjy.app.item.FinishPeddingItem;
import com.cjwsjy.app.utils.HttpClientUtil;
import com.cjwsjy.app.utils.UrlUtil;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class UnFinishPeddingListActivity extends BaseActivity {
		private SharedPreferences sp;
		
		ListView list;
		
	 	@Override
	    protected void onCreate(Bundle savedInstanceState) 
	    {
	        super.onCreate(savedInstanceState);
	        //requestWindowFeature(Window.FEATURE_NO_TITLE);
	        setContentView(R.layout.peddinglist_layout);
	        
//	        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
//				.detectDiskReads().detectDiskWrites().detectAll().penaltyLog().build());
//	        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
//				.detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
//				.penaltyLog().build());
	        
	       TextView tv_navtitle = (TextView)findViewById(R.id.tv_navtitle);
	       tv_navtitle.setText("院签报待办");
	       
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
	 
	    @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        // Inflate the menu; this adds items to the action bar if it is present.
	        getMenuInflater().inflate(R.menu.main, menu);
	        return true;
	    }
	    
	    public ListView initList(){
	    	// 绑定Layout里面的ListView
			  list = (ListView) findViewById(R.id.unfinishpeddinglist);
			  
			  //生成动态数组，加入数据 
			  ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>(); 
			  
			  //获取用户登录名
				sp = SmApplication.sp;
				String loginname =sp.getString("USERDATA.LOGIN.NAME", "");
				String appUrl = UrlUtil.HOST;
				String url=appUrl+"/CEGWAPServer/YQB/getPeddingList/"+loginname;
				String jsonStr="";

			android.util.Log.i("cjwsjy", "------url="+url+"-------Pedding");

				int count = 10;
				try 
				{
					jsonStr = HttpClientUtil.HttpUrlConnectionGet(url, "UTF-8");
					
					jsonStr = "{\"data\":" + jsonStr + "}";
					
					JSONArray jsonObjs = new JSONObject(jsonStr).getJSONArray("data");
					
				    //if(jsonObjs.length()<10){
				    	count =jsonObjs.length();
				    //}
					if(count==0)
					{
						  HashMap<String, Object> map = new HashMap<String, Object>(); 
						  map.put("ItemImage", R.drawable.index_blank);//图像资源的ID 
						  map.put("ItemTitle","您暂无签报待办事项"); 
						  map.put("createDate","");
						  map.put("ItemImage1", R.drawable.index_blank);
						  map.put("ItemFormId","");
						  map.put("ItemTaskId","");
						  listItem.add(map); 
					}
					else
					{
					  for(int i=0;i<count;i++) 
					  {
						  JSONObject jsonObj = jsonObjs.getJSONObject(i);
						  
						  String createDate =jsonObj.getString("CreateTime");
							
						  createDate =createDate.split("T")[0];
						  
						  HashMap<String, Object> map = new HashMap<String, Object>(); 
						  map.put("ItemImage", R.drawable.pedding_index04);//图像资源的ID 
						  map.put("ItemTitle",jsonObj.getString("Title")); 
						  map.put("createDate",createDate); 
						  map.put("ItemImage1", R.drawable.pedding_icon1);
						  map.put("ItemFormId",jsonObj.getString("FormID"));
						  map.put("ItemTaskId",jsonObj.getString("TaskID"));
						  map.put("ItemTaskStep", jsonObj.getString("TaskStep"));
						
						  listItem.add(map); 
					  }
					}
				} catch (Exception e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}

			  //生成适配器的Item和动态数组对应的元素 
			  SimpleAdapter listItemAdapter = new SimpleAdapter(this,listItem,//数据源
					  R.layout.unfinishpeddinglist_item,
					  new String[]{"ItemImage","ItemTitle","createDate","ItemImage1","ItemFormId","ItemTaskId"}, 
					  new int[] {R.id.iv_icon1,R.id.tv_title,R.id.tv_unfinishDate,R.id.iv_icon2,R.id.tv_formid,R.id.tv_taskid});
			 
			  //添加并且显示 
			  list.setAdapter(listItemAdapter);
			 if(count>0){ 
				  list.setOnItemClickListener(new OnItemClickListener(){
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO 自动生成的方法存根
					 	//View curr = parent.getChildAt((int) id);
	                    //TextView formIdView = (TextView)curr.findViewById(R.id.tv_formid);
	                    //TextView taskIdView = (TextView)curr.findViewById(R.id.tv_taskid);
	                    //String formId = formIdView.getText().toString();
	                    //String taskId = taskIdView.getText().toString();
	                    
						HashMap<String,String> map=(HashMap<String,String>)list.getItemAtPosition(position);
	                    String formId = map.get("ItemFormId");
	                    String taskId = map.get("ItemTaskId");
	                    String taskStep = map.get("ItemTaskStep");
	                    
						Intent intent = new Intent(UnFinishPeddingListActivity.this, PeddingFrameActivity.class);
						
						 Bundle bundle=new Bundle();
						 bundle.putString("formId", formId);
						 bundle.putString("taskId", taskId);
						 bundle.putString("taskStep", taskStep);
			          	 intent.putExtras(bundle);
						
						startActivity(intent);
					}
					 
				  });
			 }  
			  return list;
	    }
	   
}
