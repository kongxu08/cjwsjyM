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
public class selectAddress3Activity extends BaseActivity2 implements
		OnClickListener {
    private EditText et_address;
    private Button btn_ok;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_address3);
    	setHeadView(R.drawable.onclick_back_btn, "", "途经地点", 0, "", this, this, this);
		init();
	   }
	
	     public void init(){
	    	 et_address=(EditText) findViewById(R.id.et_address3);
	    	 if(!this.getIntent().getStringExtra("word").contains("请输入")){
	    	 et_address.setText(this.getIntent().getStringExtra("word"));
	    	 }
	    	 btn_ok=(Button) findViewById(R.id.btn_ok);
	    	 btn_ok.setOnClickListener(this);
	     }
  
       
	  @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		  Intent intent = new Intent();  
		  Bundle bundle =new Bundle();
		 
		switch (v.getId()) {
		case R.id.leftImage:
			if (selectAddress3Activity.this.getCurrentFocus() != null)
			{
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(selectAddress3Activity.this.getCurrentFocus().getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
			}
			finish();
			break;
		
		case R.id.btn_ok:
			String address=et_address.getText().toString();
			if(!ValidUtil.isNullOrEmpty(address)){
				bundle.putString("address", address);  
				 setResult(0x003, intent);
	            intent.putExtras(bundle); 
	            if (selectAddress3Activity.this.getCurrentFocus() != null)
				{
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(selectAddress3Activity.this.getCurrentFocus().getWindowToken(),
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
