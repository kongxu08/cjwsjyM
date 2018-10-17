package com.cjwsjy.app.pedding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.cjwsjy.app.R;
import com.cjwsjy.app.SmApplication;
import com.cjwsjy.app.adapter.DeptSignListAdaper;
import com.cjwsjy.app.item.DeptSignItem;
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
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class DeptFragment extends Fragment
{
	private View parentView;
	
	private SharedPreferences sp;
	private EditText dept_message;
	private ImageView dept_sign;
	private EditText dept_updatemessage;
	private RelativeLayout dept_form;
	private RelativeLayout dept_form1;
	private String loginname="";

	private String appUrl="";
	private String taskId="";
	
	private TextView dept_signDate;
	private ImageView dept_userName;
	
	private String retSignVerify;
	
	private boolean flag=false;
	
	private String formId ="";
	
	private String signFlag="";
	
	//生成动态数组，加入数据 
	List<DeptSignItem> listItems= new ArrayList<DeptSignItem>();
	private DeptSignListAdaper listItemAdapter;
	 @Override
	 public void onCreate(Bundle savedInstanceState) 
	 {
	  // TODO Auto-generated method stub
	  super.onCreate(savedInstanceState);
	  
	 }
	 
	 @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) 
	 {
		  parentView = inflater.inflate(R.layout.peddept_layout,container, false); 
		  
		 //获取用户登录名
		  sp = SmApplication.sp;
		  loginname =sp.getString("USERDATA.LOGIN.NAME", "");
		  
		 /* DBManager dbManager =SmApplication.dbManager;
		  List<DepartmentVO> deptList = dbManager.findUserOrgs(loginname);*/
		  List<DepartmentVO> deptList =((PeddingFrameActivity)getActivity()).getDeptList();
		  
		  //该用户属于综合部
		  String taskStep = ((PeddingFrameActivity)getActivity()).getTaskStep(); 
		 /* for(int i=0;i<deptList.size();i++){
			  if("BE2360A7-692C-4CEF-8D5F-85E0F253FEAB".equals(deptList.get(i).getDeptId())){
				  flag=true;
				  break;
			  }
		  }*/
		  if("综合管理部意见".equals(taskStep)){
			    flag=true;
		  }
		  
		  appUrl = UrlUtil.HOST;
		  
		  taskId = ((PeddingFrameActivity)getActivity()).getSignTaskId();
		  
		  //Toast.makeText(getActivity(), "xxx:"+taskId, Toast.LENGTH_SHORT).show(); 
		  signFlag =((PeddingFrameActivity)getActivity()).getSignFlag();
		  dept_form =(RelativeLayout)parentView.findViewById(R.id.dept_form);
		  if("已办".equals(signFlag)){
			  dept_form.setVisibility(View.GONE);
		  }
		  //综合部签名
		  if(!flag){
			  dept_form.setVisibility(View.GONE);
		  }
		  
		  submitForm();
		  
		  initList();
		  
		 return parentView;
	 }
	 
	 public void submitForm(){
		 //新增意见
		 dept_message = (EditText) parentView.findViewById(R.id.dept_message);
		 //签字
		 dept_sign =(ImageView)parentView.findViewById(R.id.dept_sign);
		 
		 //修改意见输入框
		 dept_updatemessage = (EditText) parentView.findViewById(R.id.dept_updatemessage);
		
		 dept_form1 =(RelativeLayout)parentView.findViewById(R.id.dept_form1);
		 
		 dept_signDate =(TextView) parentView.findViewById(R.id.dept_signdate); 
		 dept_userName =(ImageView) parentView.findViewById(R.id.dept_userName); 
		 
		// 点击事件
		dept_sign.setOnClickListener(new Button.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO 自动生成的方法存根
				String message =dept_message.getText().toString().replace("\"", "&quot");
				dept_updatemessage.setText(dept_message.getText());
				dept_form.setVisibility(View.GONE);
				dept_form1.setVisibility(View.VISIBLE);
				dept_updatemessage.setCursorVisible(false);

				// 通过url和请求参数获取到相应的数据
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

					dept_signDate.setText(jsonObj.getString("retSignTime"));

					retSignVerify = jsonObj.getString("retSignVerify");

					////////////////////////////////////////
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
					dept_userName.setImageBitmap(bitmap2); // 显示图片

				}
				catch (Exception e)
				{
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}
			}
		});
		
		//重签事件
		ImageView dept_update =(ImageView) parentView.findViewById(R.id.dept_update); 
		dept_update.setOnClickListener(new Button.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				dept_form.setVisibility(View.VISIBLE);
				dept_form1.setVisibility(View.GONE);
			}
		});
		
		// 提交
		ImageView dept_submit = (ImageView) parentView.findViewById(R.id.dept_submit);
		// 点击事件
		dept_submit.setOnClickListener(new Button.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				String signStep = "综合管理部意见";
				
				String submit_message =dept_updatemessage.getText().toString().replace("\"", "&quot");
				
				Map<String, String> params = new HashMap<String, String>();
				params.put("taskId", taskId);
				params.put("signStep", signStep);
				params.put("signPicUser", loginname);
				params.put("advice", submit_message);
				params.put("retSignVerify", retSignVerify);
				params.put("retSignTime", dept_signDate.getText().toString());

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
						Toast.makeText(getActivity(), "失败代码110102",Toast.LENGTH_SHORT).show();
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
			ListView listView = (ListView) parentView.findViewById(R.id.deptlist);
			  
			listItems = ((PeddingFrameActivity)getActivity()).getListDeptItems();	
			//生成适配器的Item和动态数组对应的元素 
			listItemAdapter = new DeptSignListAdaper(getActivity(), listItems);
			
			listView.setAdapter(listItemAdapter);

			return listView;
		}
}
