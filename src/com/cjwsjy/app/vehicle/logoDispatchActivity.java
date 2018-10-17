package com.cjwsjy.app.vehicle;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.view.Window;

import com.cjwsjy.app.R;
import com.cjwsjy.app.SmApplication;


public class logoDispatchActivity extends BaseActivity2{
	 private String account;
	 private SharedPreferences sp;
	 private Map dataMap = new  HashMap<String, Object>();
	 public static boolean isAuditer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_vehicle);
		 sp= SmApplication.sp;
		 account=sp.getString(Constants.sp_loginName, "");
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					Thread.sleep(3000);
					Intent intent=new Intent();
					intent.setClass(logoDispatchActivity.this, VehileDispatchActivity.class);
					startActivity(intent);
					finish();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}
      
     	
	       
	
}
