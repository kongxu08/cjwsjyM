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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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
public class selectCountActivity extends BaseActivity2 implements
		OnClickListener {
    private EditText et_address;
    private Button btn_ok;
    private Button btn_4;
    private Button btn_5;
    private Button btn_6;
    private Button btn_7;
    private Button btn_8;
    private Button btn_9;
    private Button btn_10;
    private Button btn_11;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_counts);
		setHeadView(R.drawable.onclick_back_btn, "", "用车人数", 0, "", this, this, this);
		init();
	   }
	
	     public void init(){
	    	 et_address=(EditText) findViewById(R.id.et_address);
	    	 btn_ok=(Button) findViewById(R.id.btn_ok);
	    	 btn_ok.setOnClickListener(this);
	    	 btn_4=(Button) findViewById(R.id.btn_4);
	    	 btn_4.setOnClickListener(this);
	    	 btn_5=(Button) findViewById(R.id.btn_5);
	    	 btn_5.setOnClickListener(this);
	    	 btn_6=(Button) findViewById(R.id.btn_6);
	    	 btn_6.setOnClickListener(this);
	    	 
	    	 btn_7=(Button) findViewById(R.id.btn_7);
	    	 btn_7.setOnClickListener(this);
	    	 btn_8=(Button) findViewById(R.id.btn_8);
	    	 btn_8.setOnClickListener(this);
	    	 btn_9=(Button) findViewById(R.id.btn_9);
	    	 btn_9.setOnClickListener(this);
	    	 btn_10=(Button) findViewById(R.id.btn_10);
	    	 btn_10.setOnClickListener(this);
	    	 btn_11=(Button) findViewById(R.id.btn_11);
	    	 btn_11.setOnClickListener(this);
	     }
  
       
	  @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		  Intent intent =new Intent();  
		  Bundle bundle =new Bundle();
		  setResult(0x005, intent); 
		switch (v.getId()) {
		case R.id.leftImage:
			if (selectCountActivity.this.getCurrentFocus() != null)
			{
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(selectCountActivity.this.getCurrentFocus().getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
			}
			finish();
			break;
		case R.id.btn_4:
				bundle.putString("count", "4");  
	            intent.putExtras(bundle); 
			   finish();
			break;
		case R.id.btn_5:
			bundle.putString("count", "5");  
            intent.putExtras(bundle); 
			finish();
			break;
		case R.id.btn_6:
			bundle.putString("count", "6");  
            intent.putExtras(bundle); 
			finish();
			break;
		case R.id.btn_7:
			bundle.putString("count", "7");  
	            intent.putExtras(bundle); 
			finish();
			break;
		case R.id.btn_8:
			bundle.putString("count", "8");  
	            intent.putExtras(bundle); 
			finish();
			break;
		case R.id.btn_9:
			bundle.putString("count", "9");  
	         intent.putExtras(bundle); 
			finish();
			break;
		case R.id.btn_10:
			bundle.putString("count", "10"); 
	         intent.putExtras(bundle); 
			finish();
			break;
		case R.id.btn_11:
			bundle.putString("count", "11"); 
	         intent.putExtras(bundle); 
			finish();
			break;
		case R.id.btn_ok:
			String address=et_address.getText().toString();
			if(!ValidUtil.isNullOrEmpty(address)){
				bundle.putString("count",address); 
	            intent.putExtras(bundle); 
	            if (selectCountActivity.this.getCurrentFocus() != null)
				{
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(selectCountActivity.this.getCurrentFocus().getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
				}
				finish();
			}else{
				Toast.makeText(this, "请填写用车人数",Toast.LENGTH_LONG).show();
			}
			break;
		default:
			break;
		}
	}
}
