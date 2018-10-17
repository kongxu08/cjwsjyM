package com.cjwsjy.app.vehicle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.do1.cjmobileoa.parent.callback.ResultObject;
import com.cjwsjy.app.R;

import android.R.integer;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;


/**
 * 申请详情
 * 
 * @Company: 广州市道一信息有限公司
 * @author: liuwende
 * 
 */
@SuppressLint("NewApi")
public class selectVehileByActivity extends BaseActivity2 implements OnClickListener {
	private Map dataMap = new  HashMap<String, Object>();
	private List<Map<String, Object>> datalist = new ArrayList<Map<String,Object>>();
	
	private LinearLayout ll_type;
	private LinearLayout ll_model;
	private LinearLayout ll_pailiang; 
	private LinearLayout ll_list;
	private TextView type_name;
	private TextView model_name;
	private TextView pailiang_name;
	
	private String Vehicle_type;
	private String model;
	private String displacement;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_selectvehileby);
		setHeadView(R.drawable.onclick_back_btn, "", "车辆筛选", R.drawable.onclick_new_apply_btn, "确定", this, this, this);
         
		init();
	}
	
	     public void init(){
	    	 ll_type=(LinearLayout) findViewById(R.id.ll_type);
	    	 ll_type.setOnClickListener(this);
	    	 ll_model=(LinearLayout) findViewById(R.id.ll_model);
	    	 ll_model.setOnClickListener(this);
	    	 ll_pailiang=(LinearLayout) findViewById(R.id.ll_pailiang);
	    	 ll_pailiang.setOnClickListener(this);
	    	 ll_list=(LinearLayout) findViewById(R.id.ll_list);
	    	 
	    	 type_name=(TextView) findViewById(R.id.type_name);
	    	 model_name=(TextView) findViewById(R.id.modle_name);
	    	 pailiang_name=(TextView) findViewById(R.id.pailiang_name);
	     }
	     
	     
	     public void initData(){
	    	 if(datalist.size()>0){
	    		 for (int i = 0; i < datalist.size(); i++) {
					final View view=getLayoutInflater().inflate(R.layout.item_vehiel, null);
					TextView tv_item=(TextView) view.findViewById(R.id.vehile_name);
					 final String vehile_name=(String) datalist.get(i).get("type_name");
					 final String vehile_name_no=(String) datalist.get(i).get("type_code");
					tv_item.setText(vehile_name);
					ll_list.addView(view);
					//map.put(vehile_name, vehile_name_no);
					view.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							view.setBackgroundResource(R.drawable.bg_item_check);
							Vehicle_type=vehile_name_no;
							type_name.setText(vehile_name);
							type_name.setTextColor(Color.parseColor("#ff8c03"));
							ll_list.removeAllViews();
						}
					});
				}
	    	 }
	     }
	     public void initData2(){
	    	 if(datalist.size()>0){
	    		 for (int i = 0; i < datalist.size(); i++) {
					final View view=getLayoutInflater().inflate(R.layout.item_vehiel, null);
					TextView tv_item=(TextView) view.findViewById(R.id.vehile_name);
					 final String model_names=datalist.get(i).get("model_name") != null ? datalist.get(i).get("model_name").toString() : "";
			          //String model_id=datalist.get(i).get("model_id") != null ? datalist.get(i).get("model_id").toString() : "";
					tv_item.setText(model_names);
					ll_list.addView(view);
					//map.put(vehile_name, vehile_name_no);
					view.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							model=model_names;
							model_name.setText(model_names);
							model_name.setTextColor(Color.parseColor("#ff8c03"));
							ll_list.removeAllViews();
						}
					});
				}
	    	 }
	     }
	     public void initData3(){
	    	 if(datalist.size()>0){
	    		 for (int i = 0; i < datalist.size(); i++) {
					final View view=getLayoutInflater().inflate(R.layout.item_vehiel, null);
					TextView tv_item=(TextView) view.findViewById(R.id.vehile_name);
					 final String Name=datalist.get(i).get("Name") != null ? datalist.get(i).get("Name").toString() : "";
			          final String ID=datalist.get(i).get("ID") != null ? datalist.get(i).get("ID").toString() : "";
					tv_item.setText(Name);
					ll_list.addView(view);
					//map.put(vehile_name, vehile_name_no);
					view.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							view.setBackgroundResource(R.drawable.bg_item_check);
							displacement=ID;
							pailiang_name.setText(Name);
							pailiang_name.setTextColor(Color.parseColor("#ff8c03"));
							ll_list.removeAllViews();
						}
					});
				}
	    	 }
	     }
       
	  @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		  //Map<String, String> map = new HashMap<String, String>();
		switch (v.getId())
		{
		case R.id.leftImage:
			finish();
			break;
		case R.id.rightImage:
			      Intent intent=new Intent();
		          Bundle bundle=new Bundle();
		          bundle.putString("Vehicle_type", Vehicle_type);  
		          bundle.putString("model", model);  
		          bundle.putString("displacement", displacement); 
		          intent.putExtras(bundle); 
		          setResult(0x001, intent); 
				  finish();
			break;
		case R.id.ll_type:
			ll_list.removeAllViews();
			requestBeforeDialog(selectVehileByActivity.this);
			Map<String, String> map = new HashMap<String, String>();
   			String url=UrlManager.GET_VEHILE_TYPE;
			map.put("status", "0");
   			doRequest(0, url, map);
			intent=new Intent(selectVehileByActivity.this, selectVehiltTypeActivity.class);
			startActivityForResult(intent, 0);
			break;
		case R.id.ll_model:
			ll_list.removeAllViews();
			requestBeforeDialog(selectVehileByActivity.this);
			 map = new HashMap<String, String>();
			 url = UrlManager.GET_MODEL;
			 map.put("status", "0");
   			doRequest(1, url, map);
			 intent=new Intent(selectVehileByActivity.this, selectVehileModelActivity.class);
			 startActivityForResult(intent, 0x001);
			break;
		case R.id.ll_pailiang:
			ll_list.removeAllViews();
			requestBeforeDialog(selectVehileByActivity.this);
			 map = new HashMap<String, String>();
			 url = UrlManager.GET_CER;
			 map.put("status", "0");
  			doRequest(2, url, map);
			intent=new Intent(selectVehileByActivity.this, selectVehilePailiangActivity.class);
			 startActivityForResult(intent, 0x001);
			break;
		default:
			break;
		}
	}
	
	  
	/*@Override
	protected void onExecuteSuccess(int requestId, ResultObject result) {
			// TODO Auto-generated method stub
			requestAfterDialog();
			switch (requestId) {
			case 0:
				datalist=result.getListMap();
				initData();
				break;
			case 1:
				datalist=result.getListMap();
				initData2();
				break;
			case 2:
				datalist=result.getListMap();
				initData3();
				break;

			default:
				break;
			}

		}
	  
	  
	@Override
	protected void onExecuteFail(int requestId, ResultObject result) {
		// TODO Auto-generated method stub
		super.onExecuteFail(requestId, result);
		Toast.makeText(selectVehileByActivity.this, "查询失败", 1).show();
	}*/
	  
	  
	  
	@SuppressLint("NewApi")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(data!=null){
		Bundle bundle = data.getExtras();
		switch (resultCode) {
		case RESULT_OK:
			if(requestCode==0){
				String vehile_name=bundle.getString("type_name","");
				String vehile_name_no=bundle.getString("vehile_name_no","");
				Vehicle_type=vehile_name_no;
				type_name.setText(vehile_name);
				type_name.setTextColor(Color.parseColor("#ff8c03"));
			}
			break;
		case 0x001:
			String name=bundle.getString("model_name","");
			String model_id=bundle.getString("model_id","");
			model=name;
			model_name.setText(name);
			model_name.setTextColor(Color.parseColor("#ff8c03"));
			break;
		case 0x002:
			break;
		case 0x003:
			if (bundle != null) {
				String Name=bundle.getString("Name","");
				String ID=bundle.getString("ID","");
				displacement=ID;
				pailiang_name.setText(Name);
				pailiang_name.setTextColor(Color.parseColor("#ff8c03"));
			}
			break;
					default:
			break;
		}

		super.onActivityResult(requestCode, resultCode, data);
	}
	}
}
