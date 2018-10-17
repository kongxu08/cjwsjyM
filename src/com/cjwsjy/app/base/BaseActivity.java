package com.cjwsjy.app.base;

import com.cjwsjy.app.ActivityLogin2;
import com.cjwsjy.app.SmApplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class BaseActivity extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO 自动生成的方法存根
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onStart()
	{
		// TODO 自动生成的方法存根
		super.onStart();
	}

	@Override
	protected void onResume()
	{
		// TODO 自动生成的方法存根
		super.onResume();
		
		int home = 0;
		home = SmApplication.gethomekey();

		if(home==1)
		{
			Intent intent = new Intent(this, ActivityLogin2.class);
			startActivity(intent);
			//SmApplication.sethomekey(0,2);
		}
	}

	@Override
	protected void onPause()
	{
		// TODO 自动生成的方法存根
		super.onPause();
	}

	@Override
	protected void onStop()
	{
		// TODO 自动生成的方法存根
		super.onStop();
	}

	@Override
	protected void onDestroy()
	{
		// TODO 自动生成的方法存根
		super.onDestroy();
	}
	

}
