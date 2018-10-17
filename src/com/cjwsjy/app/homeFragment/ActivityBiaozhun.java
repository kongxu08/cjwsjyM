package com.cjwsjy.app.homeFragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.cjwsjy.app.R;
import com.cjwsjy.app.SmApplication;
import com.cjwsjy.app.outoffice.MoreListPopupWindow;
import com.cjwsjy.app.utils.UrlUtil;
import com.cjwsjy.app.webview.WebViewBQ;
import com.cjwsjy.app.webview.WebViewBiaoZhun;
import com.cjwsjy.app.webview.WebViewHome2;

public class ActivityBiaozhun extends Activity
{
    private SharedPreferences sp;
	
    private  TextView tv_register;
    private  TextView tv_search;
    private  TextView tv_outofoffice;
    private  TextView tv_lixian;
    
    private  TextView tv_outoffice;//外出登记
    private  TextView tv_more;//更多
    
    private String loginname;
    private String userId;
    private String appUrl;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_biaozhun);
        
        tv_register =(TextView) findViewById(R.id.tv_register);
        tv_search =(TextView) findViewById(R.id.tv_guanli);
        tv_outofoffice =(TextView) findViewById(R.id.tv_outofoffice);
        tv_lixian =(TextView) findViewById(R.id.tv_lixian);
        
        appUrl = UrlUtil.HOST;
        //获取用户登录名
		sp = SmApplication.sp;
		loginname =sp.getString("USERDATA.LOGIN.NAME", "");
		userId = sp.getString("USERDATA.USER.ID", "");
		
        //标准搜索
        tv_register.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                String url = "http://bzm.cjwsjy.com.cn:8090/bz/phone/webroot/demo/biaozhunsousuo_content.html?userId="+userId+"&phone=true";

                Intent intent = new Intent();
                intent.setClass(ActivityBiaozhun.this, WebViewBiaoZhun.class);
                intent.putExtra("webUrl",url);
                intent.putExtra("titleName","标准搜索");
                startActivity(intent);
            }
        });
        
        //个人收藏
        tv_outofoffice.setOnClickListener(new OnClickListener() {           
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                String url = "http://bzm.cjwsjy.com.cn:8090/bz/phone/webroot/demo/shoucangjia_content.html?userId="+userId+"&phone=true";

                Intent intent = new Intent();
                intent.setClass(ActivityBiaozhun.this, WebViewBiaoZhun.class);
                intent.putExtra("webUrl",url);
                intent.putExtra("titleName","个人收藏");
                startActivity(intent);
            }
        });
        
        //文件管理
        tv_search.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String url = "http://bzm.cjwsjy.com.cn:8090/bz/phone/webroot/demo/lixianyuedu_content.html?userId="+userId+"&phone=true";

                Intent intent = new Intent();
                intent.setClass(ActivityBiaozhun.this, WebViewBQ.class);
                intent.putExtra("webUrl",url);
                intent.putExtra("titleName","文件管理");
                startActivity(intent);
            }
        });

        //离线阅览
        tv_lixian.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //Intent intent = new Intent( ActivityBiaozhun.this, ActivityLixian.class);
                //startActivity(intent);

                String url = appUrl+"/CEGWAPServer/XW/GetIPSList_GGB2/";
                Intent intent = new Intent( ActivityBiaozhun.this, WebViewHome2.ActivityListLixian.class);
                intent.putExtra("URL",url);
                intent.putExtra("titleName","通知公告");
                intent.putExtra("Parameter","0");
                startActivity(intent);
            }
        });
        
        // 后退
        ImageView iv_back = (ImageView) this.findViewById(R.id.iv_back);
 		iv_back.setOnClickListener(new OnClickListener()
 		{
 			@Override
 			public void onClick(View v)
 			{
 				finish();
 			}
 		});
 		
 		//title
 		TextView tv_navtitle = (TextView)this.findViewById(R.id.tv_navtitle);
 		tv_navtitle.setText("规程规范");
    }

    private void showWin()
    {
    	MoreListPopupWindow popupWindow = new MoreListPopupWindow(ActivityBiaozhun.this, 0, 0);
    	
    	// 显示窗口
    	popupWindow.showAtLocation(findViewById(R.id.re_outofoffice_index),
    		Gravity.NO_GRAVITY | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
    }
}