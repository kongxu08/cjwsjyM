package com.cjwsjy.app.vehicle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;

import com.cjwsjy.app.R;
import com.cjwsjy.app.utils.ValidUtil;


import android.R.integer;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;


/**
 * 选择车辆
 * 
 * @Company: 广州市道一信息有限公司
 * @author: liuwende
 * 
 */
public class selectAddressActivity extends BaseActivity2 implements
		OnClickListener {
    private EditText et_address;
    private Button btn_ok;
    private LinearLayout llLayout;
    private GridLayout gl_address;
    private View footer;
	public List<Map<String, Object>> addressList = new ArrayList<Map<String,Object>>();
	
	private String vehicleUrl = com.cjwsjy.app.SmApplication.vehicle_ip;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_address);
		setHeadView(R.drawable.onclick_back_btn, "", "上车地点", 0, "", this, this, this);
		init();
		getAddress();
	   }
	
	     public void init(){
	    	 gl_address = (GridLayout) findViewById(R.id.gl_address);
	    	 et_address=(EditText) findViewById(R.id.et_address);
	    	 if(!this.getIntent().getStringExtra("word").contains("请输入")){
	    	 et_address.setText(this.getIntent().getStringExtra("word"));
	    	 }
	    	 btn_ok=(Button) findViewById(R.id.btn_ok);
	    	 btn_ok.setOnClickListener(this);
	     }
	     
	     private void initView(){
		 		for (int i = 0; i < addressList.size(); i++) {
		 			footer = getLayoutInflater().inflate(R.layout.item_address, null);
		 			Button btn_address=(Button) footer.findViewById(R.id.btn_address);
		 			ImageView img_address=(ImageView) footer.findViewById(R.id.img_address);
		 		    String imgPath=(String) addressList.get(i).get("Img");
		 			final String address=(String) addressList.get(i).get("Text");
		 			btn_address.setText(address);
		 		//	 ImageViewTool.getAsyncImageBgReal(imgPath, img_address, 0);
		 			btn_address.setOnClickListener(new OnClickListener() {
		 				@Override
		 				public void onClick(View v) {
		 					//点击进入详情
		 					Intent intent =new Intent();  
		 					Bundle bundle =new Bundle();
		 					setResult(0x001, intent); 
		 					bundle.putString("address", address);  
		 		            intent.putExtras(bundle); 
		 				    finish();
		 				}
		 			});
		 			gl_address.addView(footer);
		 		}
		 	}

	public void getAddress()
	{
		requestBeforeDialog(this);
		Map<String, String> map = new HashMap<String, String>();
		//String url=vehicleUrl+UrlManager.GetCommonlocation;
		String url = UrlManager.GetCommonlocation;
		doRequest(10, url, map);
	}

	@Override
				protected void onExecuteSuccess(int requestId, ResultObject result) {
					// TODO Auto-generated method stub
					requestAfterDialog();
					switch (requestId) {
					case 10:
						addressList=result.getListMap();
						initView();
						break;
					default:
						break;
					}

				}

				@Override
				protected void onExecuteFail(int requestId, ResultObject result) {
					// TODO Auto-generated method stub
					requestAfterDialog();
					switch (requestId) {
					case 0:
						Toast.makeText(this, "查询失败:"+result.getDesc(),Toast.LENGTH_LONG).show();
						break;
					default:
						break;
					}
				}
			    	
       
	  @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		  Intent intent =new Intent();  
		  Bundle bundle =new Bundle();
		  setResult(0x001, intent); 
		switch (v.getId()) {
		case R.id.leftImage:
			if (selectAddressActivity.this.getCurrentFocus() != null)
			{
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(selectAddressActivity.this.getCurrentFocus().getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
			}
			finish();
			break;
		case R.id.btn_ok:
			String address=et_address.getText().toString();
			if(!ValidUtil.isNullOrEmpty(address)){
				bundle.putString("address", address);  
	            intent.putExtras(bundle); 
	            if (selectAddressActivity.this.getCurrentFocus() != null)
				{
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(selectAddressActivity.this.getCurrentFocus().getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
				}
				finish();
			}else{
				Toast.makeText(this, "请填写地点",Toast.LENGTH_LONG).show();
			}
			break;
		default:
			break;
		}
	}
}
