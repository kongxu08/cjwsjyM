package com.cjwsjy.app.vehicle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;

import com.cjwsjy.app.R;
import com.cjwsjy.app.utils.ValidUtil;
import com.do1.cjmobileoa.parent.callback.ResultObject;


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
public class selectReasonActivity extends BaseActivity2 implements
		OnClickListener {
    private EditText et_address;
    private Button btn_ok;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reason);
    	setHeadView(R.drawable.onclick_back_btn, "", "用车事由", 0, "", this, this, this);
		init();
	   }
	
	     public void init(){
	    	 et_address=(EditText) findViewById(R.id.et_reason);
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
			if (selectReasonActivity.this.getCurrentFocus() != null)
			{
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(selectReasonActivity.this.getCurrentFocus().getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
			}
			finish();
			break;
		
		case R.id.btn_ok:
			String address=et_address.getText().toString();
			if(!ValidUtil.isNullOrEmpty(address)){
				bundle.putString("reason", address);  
				 setResult(0x004, intent);
	            intent.putExtras(bundle); 
	            if (selectReasonActivity.this.getCurrentFocus() != null)
				{
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(selectReasonActivity.this.getCurrentFocus().getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
				}
				finish();
			}else{
				Toast.makeText(this, "请填写是由",Toast.LENGTH_LONG).show();
			}
			break;
		default:
			break;
		}
	}
}
