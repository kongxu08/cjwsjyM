package com.cjwsjy.app.vehicle;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.json.JSONObject;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.cjwsjy.app.R;
import com.cjwsjy.app.SmApplication;
import com.cjwsjy.app.utils.HttpClientUtil;
import com.cjwsjy.app.utils.StringHelper;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
/**
 * 变更申请
 * @author think
 *
 */

@SuppressLint("NewApi")
public class selectVehicleActivity extends BaseListActivity3 implements OnClickListener{
	private EditText search_text;
	private String Vehicle_type;
	private String model;
	private String m_strObject;
	private SharedPreferences sp;
	private String responseMsg = "";
	private JSONObject jsonObj = null;
	public static String plate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setHeadView(R.drawable.onclick_back_btn, "", "车辆选择", R.drawable.onclick_new_apply_btn, "筛选", this, this, this);
		init();
		requestBeforeDialog(this);
		sp = SmApplication.sp;
	}
	
	public void init()
	{
		String buffer1 = "";

		m_strObject = "";
		listview.setMode(Mode.PULL_FROM_END);
		search_text=(EditText) findViewById(R.id.search_text);
		search_text.addTextChangedListener(new TextWatcher() {  
			  private String temp;  
			  @Override  
			   public void onTextChanged(CharSequence s, int start, int before, int count) {  
			     // TODO Auto-generated method stub                
			         }  
		         @Override  
			           public void beforeTextChanged(CharSequence s, int start, int count,  
			                   int after) {  
		            }  
					@Override
					public void afterTextChanged(Editable arg0) {  
			               // TODO Auto-generated method stub  
			              temp = search_text.getText().toString();  
			              blurSearch(temp);
			         }
			         });

		buffer1 = sHA1(this);

		findViewById(R.id.search_img).setOnClickListener(this);
	}

	public static String sHA1(Context context)
	{
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(
					context.getPackageName(), PackageManager.GET_SIGNATURES);
			byte[] cert = info.signatures[0].toByteArray();
			MessageDigest md = MessageDigest.getInstance("SHA1");
			byte[] publicKey = md.digest(cert);
			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < publicKey.length; i++) {
				String appendString = Integer.toHexString(0xFF & publicKey[i])
						.toUpperCase(Locale.US);
				if (appendString.length() == 1)
					hexString.append("0");
				hexString.append(appendString);
				hexString.append(":");
			}
			String result = hexString.toString();
			return result.substring(0, result.length()-1);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void blurSearch(String state){
	    Map<String, Object> params = new HashMap<String, Object>();
		params.put("keyword", state);
		params.put("status", "1");
		params.put("CurrentLoginUserAccount", sp.getString(Constants.sp_loginName, ""));
		map = params;
		doSearch();
 }
 
	@Override
	public void handleItemView(BaseAdapter adapter, int position, View itemView, ViewGroup parent)
	{
		// TODO Auto-generated method stub

		TextView vehile_no=(TextView) itemView.findViewById(R.id.vehile_no);
		TextView vehile_name=(TextView) itemView.findViewById(R.id.vehile_name);
		TextView vehile_type=(TextView) itemView.findViewById(R.id.vehile_type);
		TextView vehile_count=(TextView) itemView.findViewById(R.id.vehile_count);
		
		vehile_no.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);   //下划线
		final String vno=(String) datalist.get(position).get("Plate_No");
		vehile_no.setText(datalist.get(position).get("Plate_No") != null ? datalist.get(position).get("Plate_No").toString() : "");
		vehile_no.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {                       //点击进入车辆定位
				// TODO Auto-generated method stub
				/*Intent intent=new Intent(selectVehicleActivity.this, VehileMapActivity.class);
				startActivity(intent);*/
				upLoadId(vno);
				plate=vno;
			}
		});
		
		vehile_name.setText(datalist.get(position).get("model") != null ? datalist.get(position).get("model").toString() : "");
		 int car_type=0;
    	 String vehicleTypeString=(String) datalist.get(position).get("Vehicle_type");
    	 vehile_type.setText(vehicleTypeString);
   
		vehile_count.setText(datalist.get(position).get("set_passengers") != null ? datalist.get(position).get("set_passengers").toString() : "");
	}

	  public void upLoadId(final String v_no)
	  {
		  requestBeforeDialog(selectVehicleActivity.this);

	    	new Thread(new Runnable() {
				@Override
				public void run()
				{
					int length = 0;
					Map<String, String> map = new HashMap<String, String>();
		   			String url=UrlManager.appRemoteUrl+UrlManager.PositionTextAPI;
	    			Message msg = handler.obtainMessage();
	    			map.put("v_no", v_no);
	    			//Map<String, Object> resultMap = HttpClientUtil.sendRequestFromHttpClient(url, map, HttpClientUtil.DEFAULTENC);
					String resultStr = HttpClientUtil.HttpUrlConnectionGet2(url, map,"UTF-8");
					length = resultStr.length();
					if( length==0 ) return;

					msg.arg1 = 1;
					//msg.obj=responseMsg;
					msg.obj = resultStr;
					handler.handleMessage(msg);
				}
			}).start();
	    }

	private int requestPermissions2(String strpermission, int code )
	{
		if( Build.VERSION.SDK_INT<23 ) return 1;

		//判断该权限
		int Permission = ContextCompat.checkSelfPermission(this, strpermission);
		if( Permission!=PackageManager.PERMISSION_GRANTED )
		{
			//没有权限，申请权限
			ActivityCompat.requestPermissions(this,new String[]{ strpermission}, code);
			return 1013;
		}

		return 1;
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
	{
		switch (requestCode)
		{
			case 103:
				if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
				{
					// 同意
					//insertDummyContact();
					MapActivity2(m_strObject);
				}
				else
				{
					// 拒绝
					Toast.makeText(selectVehicleActivity.this, "WRITE_CONTACTS Denied", Toast.LENGTH_SHORT).show();
				}
				break;
			default:
				super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}

		Handler handler = new Handler()
		{
			public void handleMessage(Message msg)
			{
				requestAfterDialog();
				switch (msg.arg1)
				{
				case 1:
					m_strObject = (String) msg.obj;
					int ret = requestPermissions2(Manifest.permission.ACCESS_FINE_LOCATION,103);
					if(ret!=1) return;
					MapActivity2(m_strObject);

					break;

				default:
					break;
				}
			};
		};

	protected void MapActivity2(String result)
	{
		//String result=(String) msg.obj;
		if( result!=null && result.contains("%GPS"))
		{
			result=result.substring(result.indexOf("%GPS"));
			String[] str=result.split(",");
			String  jingdu=str[3];
			String  weidu=str[4];
			double  jing=Double.parseDouble(jingdu);
			double  wei=Double.parseDouble(weidu);
			String  desc="";
			for (int i = 20; i < str.length; i++) {
				desc+=str[i];
			}
			desc=desc.replace("\\n%\"}", "");
			desc=desc.replace("\"}", "");
			//Intent intent=new Intent(selectVehicleActivity.this, VehileMapActivity.class);
			Intent intent=new Intent(selectVehicleActivity.this, VehileMapActivity2.class);
			intent.putExtra("jing", jing);
			intent.putExtra("wei", wei);//plate
			intent.putExtra("v_no", plate);
			intent.putExtra("desc", desc);
			startActivity(intent);
		}
		else
		{
			Toast.makeText(selectVehicleActivity.this, "该车未装有GPS定位。。", Toast.LENGTH_SHORT).show();
			return;
		}
	}

	    @Override
	       protected void itemClick(AdapterView<?> arg0, View view, int position,
			long id) {
	          // TODO Auto-generated method stub
	          super.itemClick(arg0, view, position, id);
	          view.setBackgroundResource(R.drawable.bg_item_check);
	          position=position-1;
	          String Plate_No=datalist.get(position).get("Plate_No") != null ? datalist.get(position).get("Plate_No").toString() : "";
	          Intent intent=new Intent();
	          Bundle bundle=new Bundle();
	          bundle.putString("Plate_No", Plate_No);  
	          intent.putExtras(bundle);
	          setResult(0x002, intent);
			  finish();
      }
	
	    
	    @Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.leftImage:
				finish();
				break;
			case R.id.rightImage:
				Log.d("cjwsjy", "------selectVehicleActivity-------");
				 Intent intent=new Intent(selectVehicleActivity.this, selectVehileByActivity.class);
				 startActivityForResult(intent, 0);
				break;
			}
			}
	    
	    
	@Override
	public void initParams() {
		// TODO Auto-generated method stub
		parentResId = R.layout.activity_vehicle_list;
		listItemResId = R.layout.item_vehicle;
		from = new String[] { "Plate_No" };
		ids = new int[] { R.id.vehile_no};
		url = UrlManager.GET_VEHILE_LIST;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("status", "1");
		sp = SmApplication.sp;
		params.put("CurrentLoginUserAccount", sp.getString(Constants.sp_loginName, ""));
		map = params;
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//doSearch();
		request(1001);
	}
	
	@SuppressLint("NewApi")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(data!=null){
		Bundle bundle = data.getExtras();
		switch (resultCode) {
		case 0x001:
			String vehile_name_no=bundle.getString("Vehicle_type","");
			String model_name=bundle.getString("model","");
			String displacement_id=bundle.getString("displacement","");
			model=model_name;
			Vehicle_type=vehile_name_no;
		    Map<String, Object> params = new HashMap<String, Object>();
			params.put("model", model);
			params.put("Vehicle_type", Vehicle_type);
			params.put("displacement", displacement_id);
			params.put("status", "1");
			params.put("CurrentLoginUserAccount", sp.getString(Constants.sp_loginName, ""));
			map = params;
			doSearch();
			break;
			  default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	}
	
	

}
