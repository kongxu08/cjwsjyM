package com.cjwsjy.app.pedding;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.cjwsjy.app.R;
import com.cjwsjy.app.SmApplication;
import com.cjwsjy.app.utils.HttpClientUtil;
import com.cjwsjy.app.utils.ImageService;
import com.cjwsjy.app.utils.UrlUtil;
import com.do1.cjmobileoa.db.DBManager;
import com.do1.cjmobileoa.db.model.DepartmentVO;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cjwsjy.app.adapter.ConSignListAdaper;
import com.cjwsjy.app.item.ConSignItem;

public class ConSignFragment extends Fragment
{
	private View parentView;
	
	private SharedPreferences sp;
	private EditText con_message;
	private ImageView con_sign;
	private EditText con_updatemessage;
	private RelativeLayout con_form;
	private RelativeLayout con_form1;
	private String loginname="";
	private String appUrl="";
	private String taskId="";
	
	private TextView con_signDate;
	private ImageView con_username;
	
	private String retSignVerify;
	
	private boolean flag=true;
	
	private String signFlag="";
	
	//生成动态数组，加入数据 
	List<ConSignItem> listItems= new ArrayList<ConSignItem>();
	private ConSignListAdaper listItemAdapter;
	
	 @Override
	 public void onCreate(Bundle savedInstanceState) 
	 {
	  // TODO Auto-generated method stub
	  super.onCreate(savedInstanceState);
	  
	 }
	 
	 @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) 
	 {
		  parentView = inflater.inflate(R.layout.pedconsign_layout,container, false); 
		  
		  //获取用户登录名
		  sp = SmApplication.sp;
		  loginname =sp.getString("USERDATA.LOGIN.NAME", "");
		  
		  /*DBManager dbManager =SmApplication.dbManager; 
		  List<DepartmentVO> deptList = dbManager.findUserOrgs(loginname);*/
		  List<DepartmentVO> deptList =((PeddingFrameActivity)getActivity()).getDeptList();
		  
		  //该用户不属于院领导和综合部
		  String taskStep = ((PeddingFrameActivity)getActivity()).getTaskStep(); 
		 /* for(int i=0;i<deptList.size();i++){
			  if("0145864F-C617-4D02-943F-9D0AD0697C2C".equals(deptList.get(i).getDeptId())||
				 "57C28C07-154D-494C-93B7-A42253DF7063".equals(deptList.get(i).getDeptId())||
				 "2145814F-7EC1-4876-B988-8D6927671517".equals(deptList.get(i).getDeptId())||
				 "BE2360A7-692C-4CEF-8D5F-85E0F253FEAB".equals(deptList.get(i).getDeptId())){
				  flag=false;
				  break;
			  }
		  }*/

		  if( "会签".equals(taskStep) ) flag = true;
		  else flag = false;

		  appUrl = UrlUtil.HOST;;
		  
		  taskId = ((PeddingFrameActivity)getActivity()).getSignTaskId();

		  signFlag =((PeddingFrameActivity)getActivity()).getSignFlag();
		  con_form =(RelativeLayout)parentView.findViewById(R.id.con_form);
		  if("已办".equals(signFlag)){
			  con_form.setVisibility(View.GONE);
		  }
		  //会签签名
		  if(!flag){
			  con_form.setVisibility(View.GONE);
		  }
		  
		  submitForm();
		  
		  initList();
		  
		 return parentView;
	 }
	 
	 @SuppressLint("NewApi")
	public void submitForm(){
		 //新增意见
		 con_message = (EditText) parentView.findViewById(R.id.con_message);
		 //签字
		 con_sign =(ImageView)parentView.findViewById(R.id.con_sign);
		 
		/* con_message.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO 自动生成的方法存根
				
				int textLength =con_message.getText().length();
				
				int width =con_message.getWidth();
				//计算行数
				int textSize =1;
				if(textLength>0){
					textSize=textLength/width;
				}
				if(textSize>0){
					con_message.setLines(textSize+1);
				} 	
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO 自动生成的方法存根
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO 自动生成的方法存根
				
			}
		 }); */
		 
		 //修改意见输入框
		 con_updatemessage = (EditText) parentView.findViewById(R.id.con_updatemessage);
		 
		 con_form1 =(RelativeLayout)parentView.findViewById(R.id.con_form1);
		 
		 con_signDate =(TextView) parentView.findViewById(R.id.con_signDate); 
		 con_username =(ImageView) parentView.findViewById(R.id.con_username); 
		 
		// 点击事件
		con_sign.setOnClickListener(new Button.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO 自动生成的方法存根
				String message =con_message.getText().toString().replace("\"", "&quot");
				
				con_updatemessage.setText(con_message.getText());
				con_form.setVisibility(View.GONE);
				con_form1.setVisibility(View.VISIBLE);
				con_updatemessage.setCursorVisible(false);

				Map<String, String> params = new HashMap<String, String>();
				params.put("taskId", taskId);
				params.put("userName", loginname);
				params.put("signText", message);

				String jsonstr = "";
				JSONObject jsonObject;
				jsonObject = new JSONObject();

				try
				{
					// 请求
					String url = appUrl+"/CEGWAPServer/YQB/excuteUserSign";

					//url = "http://10.6.180.79:8080/CEGWAPServer/YQB/excuteUserSign";
					String returnStr = "";
					returnStr = HttpClientUtil.HttpUrlConnectionPost(url, params, HttpClientUtil.DEFAULTENC);

					//-----------------------------------------
//					jsonObject.put("taskId", taskId);
//					jsonObject.put("userName", loginname);
//					jsonObject.put("signText", message);
//
//					jsonstr = jsonObject.toString();
//
//					returnStr = HttpClientUtil.HttpUrlConnectionPostLog(url, jsonstr);

					//android.util.Log.i("cjwsjy", "----------return="+returnStr+"-------");

					//-----------------------------------------

					JSONObject jsonObj = new JSONObject(returnStr);
					con_signDate.setText(jsonObj.getString("retSignTime"));
					retSignVerify = jsonObj.getString("retSignVerify");

					//////////////////////////////////////////////
					String returnSignStr = UrlUtil.MY_SIGN+jsonObj.getString("retSignVerify")+"/"+loginname;

					byte[] data = ImageService.getImage(returnSignStr);
					Bitmap bitmap1 = BitmapFactory.decodeByteArray(data, 0, data.length); // 生成位图
					
					//URL imageUrl = new URL(returnSignStr);
					//Bitmap bitmap =BitmapFactory.decodeStream(imageUrl.openStream());
					DisplayMetrics dm = getActivity().getResources().getDisplayMetrics();
					int width = (int)(bitmap1.getWidth()*(dm.density)*0.6);
					int height =(int)(bitmap1.getHeight()*(dm.density)*0.6);
					Bitmap bitmap2 =Bitmap.createScaledBitmap(bitmap1, width, height, false);
					
					if(!bitmap1.isRecycled())
					{
						bitmap1.recycle();
					}	
					con_username.setImageBitmap(bitmap2); // 显示图片
					
				}
				catch (Exception e)
				{
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}
			}
		});
		
		//重签事件
		ImageView con_update =(ImageView) parentView.findViewById(R.id.con_update); 
		con_update.setOnClickListener(new Button.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				con_form.setVisibility(View.VISIBLE);
				con_form1.setVisibility(View.GONE);
			}
		});
		
		//提交
		ImageView con_submit =(ImageView) parentView.findViewById(R.id.con_submit); 
		 //点击事件
		con_submit.setOnClickListener(new Button.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				String signStep = "会签";
				
				String submit_message =con_updatemessage.getText().toString().replace("\"", "&quot");
				
				Map<String, String> params = new HashMap<String, String>();
				params.put("taskId", taskId);
				params.put("signStep", signStep);
				params.put("signPicUser", loginname);
				params.put("advice", submit_message);
				params.put("retSignVerify", retSignVerify);
				params.put("retSignTime", con_signDate.getText().toString());

				try
				{
					String url = appUrl + "/CEGWAPServer/YQB/commitTask";
					String returnStr = HttpClientUtil.HttpUrlConnectionPost(url, params, HttpClientUtil.DEFAULTENC);

					if ("发 送 成功!".equals(returnStr))
					{
						Intent intent = new Intent(getActivity(), UnFinishPeddingListActivity.class);
						startActivity(intent);
					}
					else
					{

					}

				}
				catch (Exception e)
				{
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}

			}
		});
	 }
	 
	public ListView initList()
	{
		ListView listView = (ListView) parentView.findViewById(R.id.consignlist);

		listItems = ((PeddingFrameActivity)getActivity()).getListConItems();
		
		listItemAdapter = new ConSignListAdaper(getActivity(), listItems);
		
		listView.setAdapter(listItemAdapter);

		return listView;
	}
}
