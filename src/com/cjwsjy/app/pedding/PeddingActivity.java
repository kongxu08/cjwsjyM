package com.cjwsjy.app.pedding;

import com.cjwsjy.app.R;
import com.cjwsjy.app.base.BaseActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class PeddingActivity extends BaseActivity {
	
	 	@Override
	    protected void onCreate(Bundle savedInstanceState) 
	    {
	        super.onCreate(savedInstanceState);
	        //requestWindowFeature(Window.FEATURE_NO_TITLE);
	        setContentView(R.layout.pedding_index);
	        
//	        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
//				.detectDiskReads().detectDiskWrites().detectAll().penaltyLog().build());
//	        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
//				.detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
//				.penaltyLog().build());
	        
	       TextView tv_navtitle = (TextView)findViewById(R.id.tv_navtitle);
	       tv_navtitle.setText("院签报");
	       
	       ImageView iv_back = (ImageView) this.findViewById(R.id.iv_back);
	       // 后退
			iv_back.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v){
					finish();
				}
			});
			
			 //已办
			TextView tv_finshpedding = (TextView) this.findViewById(R.id.tv_finshpedding);
			 //待办
			TextView tv_unfinshpedding = (TextView) this.findViewById(R.id.tv_unfinshpedding);
			 
			 tv_finshpedding.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v){
						Intent intent = new Intent( PeddingActivity.this, FinishPeddingListActivity.class);
						startActivity(intent);
					}
				});
			 
			 tv_unfinshpedding.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v){
						Intent intent = new Intent( PeddingActivity.this, UnFinishPeddingListActivity.class);
						startActivity(intent);
					}
				});
			 
	    }
 
}
