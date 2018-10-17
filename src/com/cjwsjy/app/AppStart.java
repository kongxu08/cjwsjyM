package com.cjwsjy.app;

import com.cjwsjy.app.homeFragment.ActivityDangan1;
import com.cjwsjy.app.main.MainActivity;
import com.cjwsjy.app.meeting.ActivityMeetingdetail1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * 应用启动界面
 *    
 */
public class AppStart extends Activity
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		// 设置 检查策略
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectAll().penaltyLog().build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().build());

		final View view = View.inflate(this, R.layout.app_start, null);
		setContentView(view);
		
		TextView textview = (TextView) findViewById(R.id.textView1);
		
		String version = SmApplication.sharedProxy.getString("curVersion", "1.0");
		String strver = "版本："+version;
		textview.setText(strver);
		
		// 渐变展示启动屏
		AlphaAnimation aa = new AlphaAnimation(0.5f, 1.0f);
		aa.setDuration(800);
		view.startAnimation(aa);
		aa.setAnimationListener(new AnimationListener()
		{
			@Override
			public void onAnimationEnd(Animation arg0)
			{
				redirectTo();
			}

			@Override
			public void onAnimationRepeat(Animation animation)
			{
			}

			@Override
			public void onAnimationStart(Animation animation)
			{
			}
		});
	}

	@Override
	protected void onResume() 
	{
		super.onResume();
	}

	@Override
	protected void onPause() 
	{
	    super.onPause();
	}
	
	/**
	* 跳转到...
	*/
	private void redirectTo()
	{
		int sign = 0;

		sign = SmApplication.mark;
		if (sign == 1)
		{
			Intent intent = new Intent(this, ActivityLogin.class);
			startActivity(intent);
			finish();
		}
		else
		{
			Intent intent = new Intent(this, ActivityLogin2.class);
			startActivity(intent);
			finish();
		}
	}
}
