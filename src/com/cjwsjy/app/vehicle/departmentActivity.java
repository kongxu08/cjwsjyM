package com.cjwsjy.app.vehicle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cjwsjy.app.utils.HttpClientUtil;
import com.cjwsjy.app.utils.StringHelper;
import com.do1.cjmobileoa.parent.callback.DefaultDataParser;
import com.do1.cjmobileoa.parent.callback.ResultObject;
import com.cjwsjy.app.R;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
/**
 * 变更申请
 * @author think
 *
 */

@SuppressLint("NewApi")
public class departmentActivity extends BaseListActivity2 implements OnClickListener{
	private boolean isResume = false;
	private LinearLayout ll_emp;
	//private String responseMsg = "";
	private JSONObject jsonObj = null;
	private  List<Map<String, Object>> emplist = new ArrayList<Map<String,Object>>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setHeadView(R.drawable.onclick_back_btn, "", "部门选择", 0, "", this, this, this);
		String dept_nameString=this.getIntent().getStringExtra("dept_name");
		if(dept_nameString!=null&&!dept_nameString.equals(""))
		{
			setHeadView(R.drawable.onclick_back_btn, "", dept_nameString, 0, "", this, this, this);
		}
		init();
		Intent intent=this.getIntent();
		String id=intent.getStringExtra("dept_parentid");
		vehileApply_dispatchActivity.activityList.add(this);
		requestBeforeDialog(this);
	}
	
	public void init(){
		listview.setMode(Mode.PULL_FROM_END);
		ll_emp=(LinearLayout) findViewById(R.id.ll_emp);
		
		getempList();
	}
	
	
	
	public void getempList(){
			final Map<String, String> map = new HashMap<String, String>();
			map.put("deptid", vehileApply_dispatchActivity.parentId);
   			new Thread(new Runnable() {
				@Override
				public void run() {
	    			Message msg = handler.obtainMessage();
	    			String urlsString = UrlManager.appRemoteUrl+UrlManager.GET_USERLIST;
					int length = 0;

	    			//Map<String, Object> resultMap = HttpClientUtil.sendRequestFromHttpClient(urlsString, map, HttpClientUtil.DEFAULTENC);
					String resultStr = HttpClientUtil.HttpUrlConnectionGet2(urlsString, map,"UTF-8");
					//String resultStr = HttpClientUtil.HttpUrlConnectionPost4(urlsString, map,"UTF-8");
					length = resultStr.length();
					//if( length==0 ) return;

	    			//responseMsg = String.valueOf(resultMap.get(Constants.http_result_key));
	    			
	    				if( length!=0 )
						{
	    					try {
								//String jsonStr = "{\"data\":" + resultStr + "}";
	    						//jsonObj = new JSONObject(jsonStr);
								jsonObj = new JSONObject(resultStr);
	    						String success = jsonObj.getString("result_code");
	    						if (success.equals("1"))
								{
	    							//JSONObject jsonObj = new JSONObject(jsonStr);
	    							ResultObject obj = DefaultDataParser.getInstance().parseData3(jsonObj);
	    							msg.what = 0;
	    							msg.obj=obj;
	    							handler.sendMessage(msg);
	    						} else {
	    							msg.what = 1;
	    							handler.sendMessage(msg);
	    						}
	    					} catch (JSONException e) {
	    						// TODO Auto-generated catch block
	    						e.printStackTrace();
	    					} catch (Exception e) {
	    						// TODO Auto-generated catch block
	    						e.printStackTrace();
	    					}
	    				}
						else
						{
	    					msg.what =1;
	    					handler.sendMessage(msg);
	    				}
	    			} 
	    		
			}).start();
			
	}
	
	  // Handler
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			requestAfterDialog();
			switch (msg.what) {
			case 0:
				//成功
				ResultObject obj =(ResultObject) msg.obj;
				emplist=obj.getListMap();
				doSearch();
				break;
			case 1:
				//失败
				break;
			}
		}

	};
	
	@Override
	public void handleItemView(BaseAdapter adapter, int position,
			View itemView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ImageView image= (ImageView) itemView.findViewById(R.id.image);
		TextView department_name=(TextView) itemView.findViewById(R.id.department_name);
		
		if(datalist.get(position).get("name")==null){
		department_name.setText(datalist.get(position).get("dept_name") != null ? datalist.get(position).get("dept_name").toString() : "");
	    String hasLow=(String) datalist.get(position).get("isContainLower");
	    if(hasLow.equals("1")){
	    	//有下一级
	    	image.setBackgroundResource(R.drawable.jt_down);
	    }else{
	    	image.setBackgroundResource(R.drawable.circle);
	    }
		}else{
			department_name.setText(datalist.get(position).get("name") != null ? datalist.get(position).get("name").toString() : "");
			image.setBackgroundResource(R.drawable.login_user_icon);
		}
	}

	@Override
	protected void itemClick(AdapterView<?> arg0, View view, int position, long id)
	{
		// TODO Auto-generated method stub
		super.itemClick(arg0, view, position, id);
		position = position - 1;
		if (datalist.get(position).get("name") == null)
		{
			String dept_parentid = datalist.get(position).get("dept_parentid") != null ? datalist.get(position).get("dept_parentid").toString() : "";
			String dept_id = datalist.get(position).get("dept_id") != null ? datalist.get(position).get("dept_id").toString() : "";
			String dept_name = datalist.get(position).get("dept_name") != null ? datalist.get(position).get("dept_name").toString() : "";
			String hasLow = (String) datalist.get(position).get("isContainLower");
			Intent intent = null;
			if (hasLow.equals("1"))
			{
				//有下一级
				vehileApply_dispatchActivity.parentId = dept_id;
				intent = new Intent(departmentActivity.this, departmentActivity.class);
				intent.putExtra("dept_parentid", dept_parentid);
				intent.putExtra("dept_name", dept_name);
			}
			else
			{
				intent = new Intent(departmentActivity.this, EmployeeSelectsActivity.class);
				intent.putExtra("dept_id", dept_id);
				intent.putExtra("dept_name", dept_name);
			}
			startActivity(intent);

		}
		else
		{
			//选择人员
			vehileApply_dispatchActivity.empName = datalist.get(position).get("name") != null ? datalist.get(position).get("name").toString() : "";
			vehileApply_dispatchActivity.empName2 = (String) datalist.get(position).get("username");
			vehileApply_dispatchActivity.empDept = (String) datalist.get(position).get("deptName");
			for (int i = 0; i < vehileApply_dispatchActivity.activityList.size(); i++) {
				vehileApply_dispatchActivity.activityList.get(i).finish();
			}
		}
	}
	
	    
	    @Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.leftImage:
				finish();
				break;
			}
			}
	    
	    
	@Override
	public void initParams() {
		// TODO Auto-generated method stub
		parentResId = R.layout.activity_deparment_list;
		listItemResId = R.layout.item_department;
		from = new String[] { "Plate_No" };
		ids = new int[] { R.id.vehile_no};
		url = UrlManager.appRemoteUrl + UrlManager.GET_DEPARTMENT;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("parentid", vehileApply_dispatchActivity.parentId);
		map = params;
	//	isRequest=true;
	}
	/*@Override
	protected void onExecuteSuccess(int requestId, ResultObject result) {
		// TODO Auto-generated method stub
		super.onExecuteSuccess(requestId, result);
		isResume = true;
		switch (requestId) {
		case 1098:
			datalist.addAll(emplist);
			adapter.notifyDataSetChanged();
			break;

		default:
			break;
		}
	}

	@Override
	protected void onExecuteFail(int requestId, ResultObject result) {
		// TODO Auto-generated method stub
		super.onExecuteFail(requestId, result);
		isResume = true;
	}*/
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (isResume) {
			requestBeforeDialog(this);
			doSearch();
		}
	//	doSearch();
	}
}
