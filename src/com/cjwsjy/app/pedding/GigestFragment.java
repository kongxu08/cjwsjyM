package com.cjwsjy.app.pedding;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.cjwsjy.app.R;
import com.cjwsjy.app.SmApplication;
import com.cjwsjy.app.utils.CallOtherOpeanFile;
import com.cjwsjy.app.utils.HttpClientUtil;
import com.cjwsjy.app.utils.HttpDownloader;
import com.cjwsjy.app.utils.UrlUtil;
import com.cjwsjy.app.webview.WebViewHome;
import com.do1.cjmobileoa.db.DBManager;

public class GigestFragment extends Fragment
{
	private View parentView;
	
	private SharedPreferences sp;
	
	private  ListView  fileList;
	 @Override
	 public void onCreate(Bundle savedInstanceState) 
	 {
	  // TODO Auto-generated method stub
	  super.onCreate(savedInstanceState);
	   
	 //String formId = ((PeddingFrameActivity)getActivity()).getFormId(); 
	 //Toast.makeText(getActivity(), formId, Toast.LENGTH_SHORT).show();
	 }
	 
	 @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) 
	 {
		  parentView = inflater.inflate(R.layout.pedgigest_layout,container, false); 
		  	
		  String jsonStr = ((PeddingFrameActivity)getActivity()).getJsonStr();
		  jsonStr = "{\"data\":" + jsonStr + "}";
			
		try {
			JSONArray jsonObjs = new JSONObject(jsonStr).getJSONArray("data");
			
			JSONArray jsonObjs1 = jsonObjs.getJSONArray(0);				
			
			JSONObject jsonObj = jsonObjs1.getJSONObject(0);
		   
			//主呈
		   TextView textView2 =(TextView)parentView.findViewById(R.id.textView2);
		   //事由
		   TextView textView4 =(TextView)parentView.findViewById(R.id.textView4);
		   //呈报单位
		   TextView textView6 =(TextView)parentView.findViewById(R.id.textView6); 
		   //审核
		   TextView textView8 =(TextView)parentView.findViewById(R.id.textView8); 
		   //拟稿人
		   TextView textView10 =(TextView)parentView.findViewById(R.id.textView10); 
		   //电话
		   TextView textView12 =(TextView)parentView.findViewById(R.id.textView12); 
		   
		   String event =jsonObj.getString("SY");
		   int  eventLines=event.length()/20;
		   String ccbbm =jsonObj.getString("CBDW");
		   int  ccbbmLines=ccbbm.length()/20;
		   
		   textView2.setText(jsonObj.getString("ZC"));
		   
		   textView4.setLines(eventLines+1);
		   textView4.setText(jsonObj.getString("SY"));
		   
		   textView6.setLines(ccbbmLines+1);
		   textView6.setText(jsonObj.getString("CBDW"));
		   textView8.setText(jsonObj.getString("SH"));
		  
		   textView12.setText(jsonObj.getString("DH"));
		   
		   textView10.setText(jsonObj.getString("NG"));
		   
		// 绑定Layout里面的ListView
		   fileList = (ListView)parentView.findViewById(R.id.fileList);
		 //生成动态数组，加入数据 
		   ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>(); 
		   
		   JSONArray jsonObjs2 = jsonObjs.getJSONArray(1);
		   
		    //附件
			TextView textView13 =(TextView)parentView.findViewById(R.id.textView13);
			if(jsonObjs2.length()==0){
				textView13.setVisibility(View.GONE);
				fileList.setVisibility(View.GONE);
			}
		   
			java.text.DecimalFormat   df   =new   java.text.DecimalFormat("#.0");  
			
		   if(jsonObjs2.length()>0){
			   for(int i=0;i<jsonObjs2.length();i++){
				   JSONObject jsonObj2 = jsonObjs2.getJSONObject(i);
				   
				 String attSize = jsonObj2.getString("AttSize");
				 String fileSize1 =jsonObj2.getString("AttSize");
				 String attid =jsonObj2.getString("AttID");
				 double  fileSize =  Double.valueOf(attSize);
				 fileSize = (fileSize/1024);
				 attSize =df.format(fileSize)+"K";
				 if(fileSize>1000){
					 fileSize = (fileSize/1024);
					 attSize =df.format(fileSize)+"M";
				 }
				 
				   HashMap<String, Object> map = new HashMap<String, Object>(); 
				   map.put("fileName",jsonObj2.getString("AttName"));
				   map.put("fileUrl",jsonObj2.getString("AttUrl"));
				   map.put("fileSize",attSize);
				   map.put("fileSize1",fileSize1);
				   map.put("AttID",attid);
				   listItem.add(map);
  
			   }
		   }else{
			   HashMap<String, Object> map = new HashMap<String, Object>(); 
			   map.put("fileName","");
			   map.put("fileUrl","");
			   map.put("fileSize","");
			   map.put("fileSize1","");
			   map.put("AttID","");
			   listItem.add(map); 
		   }
		   
		 //生成适配器的Item和动态数组对应的元素 
		 SimpleAdapter listItemAdapter = new SimpleAdapter(getActivity(),listItem,R.layout.filelistitem,new String[]{"fileName","fileUrl","fileSize","fileSize1"},new int[] {R.id.fileName,R.id.fileUrl,R.id.fileSize,R.id.fileSize1});
		//添加并且显示 
		 fileList.setAdapter(listItemAdapter);
		 
		 fileList.setOnItemClickListener(new OnItemClickListener(){
				@SuppressWarnings("unchecked")
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// TODO 自动生成的方法存根
					//View curr = parent.getChildAt((int) id);
					//TextView fileNameView =(TextView) curr.findViewById(R.id.fileName);
					HashMap<String,String> map=(HashMap<String,String>)fileList.getItemAtPosition(position);  			
					String fileName=map.get("fileName"); 
					String fileUrl =map.get("fileUrl");
					String fileSize =map.get("fileSize");
					String fileSize1 =map.get("fileSize1");
					String attid2 =map.get("AttID");
					double fileLen=1.0;
					
					if(fileSize.contains("M")){
						fileSize =fileSize.substring(0, fileSize.length()-1);
						fileLen = Double.valueOf(fileSize);
					}
					if(fileLen>10.0){
						Toast.makeText(getActivity(), "文件过大，不支持在线预览！", Toast.LENGTH_SHORT).show();
					}else{
						String[] arr =fileName.split("\\.");
						String prefileName = arr[0];
						String suffixfileName = arr[1];
						String attachmentName = attid2+"."+suffixfileName;
						//String attachmentName = prefileName+"-"+fileSize1+"."+suffixfileName;
						fileUrl = UrlUtil.ATTACHMENT+attid2+"/"+suffixfileName;
						//下载附件
						File file=new File(Environment.getExternalStorageDirectory() +"/Download"+ "/com.cjwsjy.app/attachment/"+attachmentName);      
						if(!file.exists())
						{
						   HttpDownloader httpDownLoader=new HttpDownloader(); 

			               int result=httpDownLoader.downfile(fileUrl, "attachment/", attachmentName);
			               if(result==0)  
			                  {  
			                      Toast.makeText(getActivity(), "下载成功！", Toast.LENGTH_SHORT).show();  
			                  }  
			                  else if(result==-1){  
			                      Toast.makeText(getActivity(), "下载失败！", Toast.LENGTH_SHORT).show();
			                      return;
			                  } 
					     }						
					     //下载完成打开附件
						String sdPath = Environment.getExternalStorageDirectory() + "/Download" +"/com.cjwsjy.app/attachment/";
						//Intent intent =HttpDownloader.openFile(sdPath+attachmentName);						
						//GigestFragment.this.getActivity().startActivity(intent);
						File file2 = new File(sdPath+attachmentName);
				        //if(file.exists())
				        CallOtherOpeanFile openfile = new CallOtherOpeanFile();
						openfile.openFile(GigestFragment.this.getActivity(), file2);
					}						
				}
		 });		
		} catch (JSONException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
			
	    return parentView;
	 }
	 
}
