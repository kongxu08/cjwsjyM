package com.cjwsjy.app.pedding;

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

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cjwsjy.app.adapter.LeaderSignListAdaper;
import com.cjwsjy.app.item.LeaderSignItem;

public class LeaderShipFragment extends Fragment
{
	private View parentView;
	
	private SharedPreferences sp;
	private EditText et_message;
	private ImageView iv_sign;
	private EditText et_updatemessage;
	private RelativeLayout rl_form;
	private RelativeLayout rl_form1;
	private String loginname="";
	
	private String appUrl="";
	private String taskId="";
	
	private TextView tv_signDate;
	private ImageView tv_username;
	
	private String retSignVerify;
	
	private boolean flag=false;
	
	private String signFlag="";
	
	//生成动态数组，加入数据 
	List<LeaderSignItem> listItems= new ArrayList<LeaderSignItem>();
	private LeaderSignListAdaper listItemAdapter;
	
	 @Override
	 public void onCreate(Bundle savedInstanceState) 
	 {
	  // TODO Auto-generated method stub
	  super.onCreate(savedInstanceState);
	  
	 }
	 
	 @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) 
	 {
		  parentView = inflater.inflate(R.layout.pedleadership_layout,container, false); 
		  
		  //获取用户登录名
		  sp = SmApplication.sp;
		  loginname =sp.getString("USERDATA.LOGIN.NAME", "");
		  
		 /* DBManager dbManager =SmApplication.dbManager;
		  List<DepartmentVO> deptList = dbManager.findUserOrgs(loginname);*/
		  List<DepartmentVO> deptList =((PeddingFrameActivity)getActivity()).getDeptList();
		  
		 //该用户属于院领导
		  String taskStep = ((PeddingFrameActivity)getActivity()).getTaskStep(); 
		  /*for(int i=0;i<deptList.size();i++){
			  if("0145864F-C617-4D02-943F-9D0AD0697C2C".equals(deptList.get(i).getDeptId())||
				 "57C28C07-154D-494C-93B7-A42253DF7063".equals(deptList.get(i).getDeptId())||
				 "2145814F-7EC1-4876-B988-8D6927671517".equals(deptList.get(i).getDeptId())){
				  flag=true;
				  break;
			  }
		  }*/
		  if("院领导批示".equals(taskStep)){
			   flag=true;
		  }

		  appUrl = UrlUtil.HOST;
		  taskId = ((PeddingFrameActivity)getActivity()).getSignTaskId();		  
		  
		  signFlag =((PeddingFrameActivity)getActivity()).getSignFlag();
		  
		  rl_form =(RelativeLayout)parentView.findViewById(R.id.rl_form);
		  if("已办".equals(signFlag)){
			  rl_form.setVisibility(View.GONE);
		  }
		  
		  //院领导签名
		  if(!flag){
			  rl_form.setVisibility(View.GONE);
		  }
		  
		  submitForm();
		  
		  initList();
		  
		 return parentView;
	 }
	 
	 public void submitForm(){
		 //新增意见
		 et_message = (EditText) parentView.findViewById(R.id.et_message);
		 //签字
		 iv_sign =(ImageView)parentView.findViewById(R.id.iv_sign);
		 
		 //修改意见输入框
		 et_updatemessage = (EditText) parentView.findViewById(R.id.et_updatemessage);
		
		 rl_form1 =(RelativeLayout)parentView.findViewById(R.id.rl_form1);
		 
		 tv_signDate =(TextView) parentView.findViewById(R.id.tv_signDate); 
		 tv_username =(ImageView) parentView.findViewById(R.id.tv_username); 
		// 点击事件
		iv_sign.setOnClickListener(new Button.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO 自动生成的方法存根
				String message =et_message.getText().toString().replace("\"", "&quot");
				et_updatemessage.setText(et_message.getText());
				rl_form.setVisibility(View.GONE);
				rl_form1.setVisibility(View.VISIBLE);
				et_updatemessage.setEnabled(false);

				// 通过url和请求参数获取到相应的数据
				//String textstr = et_message.getText().toString();
				Map<String, String> params = new HashMap<String, String>();
				params.put("taskId", taskId);
				params.put("userName", loginname);
				params.put("signText", message);

				try
				{
					// 请求
					String url = appUrl + "/CEGWAPServer/YQB/excuteUserSign";
					String returnStr = HttpClientUtil.HttpUrlConnectionPost(url, params, HttpClientUtil.DEFAULTENC);

					JSONObject jsonObj = new JSONObject(returnStr);

					tv_signDate.setText(jsonObj.getString("retSignTime"));

					retSignVerify = jsonObj.getString("retSignVerify");

					//////////////////////////////////
					String returnSignStr = UrlUtil.MY_SIGN+jsonObj.getString("retSignVerify")+"/"+loginname;
					//String returnSignStr = "http://digsign.cjwsjy.com.cn/UTCESealForCJWWebService/ShowImg.aspx?UserName=" + loginname + "&SignSeal="
					//		+ jsonObj.getString("retSignVerify") + "";

					byte[] data = ImageService.getImage(returnSignStr);
					Bitmap bitmap1 = BitmapFactory.decodeByteArray(data, 0, data.length); // 生成位图
					DisplayMetrics dm = getActivity().getResources().getDisplayMetrics();
					int width = (int)(bitmap1.getWidth()*(dm.density)*0.6);
					int height =(int)(bitmap1.getHeight()*(dm.density)*0.6);
					Bitmap bitmap2 =Bitmap.createScaledBitmap(bitmap1, width, height, false);
						
					if(!bitmap1.isRecycled()){
						bitmap1.recycle();
					}
					tv_username.setImageBitmap(bitmap2); // 显示图片
				}
				catch (Exception e)
				{
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}
			}
		});
		
		//重签事件
		ImageView iv_update =(ImageView) parentView.findViewById(R.id.iv_update); 
		iv_update.setOnClickListener(new Button.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				rl_form.setVisibility(View.VISIBLE);
				rl_form1.setVisibility(View.GONE);
			}
		});
		
		//提交
		ImageView iv_submit =(ImageView) parentView.findViewById(R.id.iv_submit); 
		 //点击事件
		iv_submit.setOnClickListener(new Button.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				try
				{
					String signStep = "院领导批示";
					
					String submit_message =et_updatemessage.getText().toString().replace("\"", "&quot");
					
					Map<String, String> params = new HashMap<String, String>();
					params.put("taskId", taskId);
					params.put("signStep", signStep);
					params.put("signPicUser", loginname);
					params.put("advice",submit_message);
					params.put("retSignVerify", retSignVerify);
					params.put("retSignTime", tv_signDate.getText().toString());

					String url = appUrl + "/CEGWAPServer/YQB/excuteUserSign";
					String returnStr = HttpClientUtil.HttpUrlConnectionPost(url, params, HttpClientUtil.DEFAULTENC);

					if ("发 送 成功!".equals(returnStr))
					{
						Intent intent = new Intent(getActivity(), UnFinishPeddingListActivity.class);
						startActivity(intent);
					}
					else
					{
						Toast.makeText(getActivity(), "失败代码110101",Toast.LENGTH_SHORT).show();
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
			ListView listView = (ListView) parentView.findViewById(R.id.leadershiplist);

			listItems = ((PeddingFrameActivity)getActivity()).getListLeaderItems();
			//生成适配器的Item和动态数组对应的元素 
			listItemAdapter = new LeaderSignListAdaper(getActivity(), listItems);

			listView.setAdapter(listItemAdapter);

			return listView;
		}
}
