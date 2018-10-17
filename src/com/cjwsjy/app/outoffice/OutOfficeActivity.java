package com.cjwsjy.app.outoffice;

import com.cjwsjy.app.R;
import com.cjwsjy.app.SmApplication;
import com.cjwsjy.app.utils.ThreadUtils;
import com.cjwsjy.app.utils.UrlUtil;
import com.cjwsjy.app.webview.WebViewHome;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class OutOfficeActivity extends Activity 
{
    private SharedPreferences sp;
    private ThreadUtils m_threadlog;
	
    private  TextView tv_register;
    private  TextView tv_search;
    private  TextView tv_leader;
    private  TextView tv_user;
    private  TextView tv_outofoffice;
    private  TextView tv_baobeisq;
    private  TextView tv_baobeilb;
    
    private  TextView tv_outoffice;//外出登记
    private  TextView tv_more;//更多

    private int m_Leader = 0;
    private int m_User = 0;
    private int m_OutExtra = 0;

    private String loginname;
    private String userId;
    private String appUrl;
    private String baobei;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.outoffice_index);

        m_threadlog = ThreadUtils.getInstance();  // 得到单例对象

        tv_search =(TextView) findViewById(R.id.tv_search);
        tv_register =(TextView) findViewById(R.id.tv_register);
        tv_leader =(TextView) findViewById(R.id.tv_leader);
        tv_user =(TextView) findViewById(R.id.tv_user);
        tv_baobeisq =(TextView) findViewById(R.id.tv_baobeisq);
        tv_baobeilb =(TextView) findViewById(R.id.tv_baobeilb);
        tv_outofoffice =(TextView) findViewById(R.id.tv_outofoffice);
        
        tv_outoffice =(TextView) findViewById(R.id.tv_outoffice);
        tv_more = (TextView) findViewById(R.id.tv_more);
        
        appUrl = UrlUtil.HOST;
        //获取用户登录名
		sp = SmApplication.sp;
		loginname =sp.getString("USERDATA.LOGIN.NAME", "");
		userId = sp.getString("USERDATA.USER.ID", "");
        baobei = sp.getString("USERDATA.BAOBEI.ID", "");

        m_Leader = sp.getInt("isLeader", 0);
        m_User = sp.getInt("isUser", 0);
        m_OutExtra = sp.getInt("isOutExtra", 0);

        int nbaobei=Integer.parseInt(baobei);
        nbaobei = m_OutExtra;

        if(m_Leader==0)
        {
            tv_leader.setVisibility(View.GONE);
        }

        if(m_User==0)
        {
            tv_user.setVisibility(View.GONE);
        }

        if(nbaobei==1)
        {
            tv_baobeisq.setVisibility(View.GONE);
        }
        else if (nbaobei!=2)
        {
            tv_baobeisq.setVisibility(View.GONE);
            tv_baobeilb.setVisibility(View.GONE);
        }

        //我要出差
        tv_register.setOnClickListener(new OnClickListener() 
        {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                m_threadlog.setparm2("点击","我要出差", "我要出差-出差审批-首页");
                m_threadlog.writelog();

                String url=""+appUrl+"/OutWeb/wepRegisterOutOfOffice/"+userId;

                android.util.Log.i("cjwsjy", "------url="+url+"-------出差");

            	Intent intent = new Intent();           	
				intent.setClass(OutOfficeActivity.this, WebViewHome.class);
				intent.putExtra("webUrl",url);
				intent.putExtra("titleName","出差审批登记");
				startActivity(intent);
            }
        });
        
        //审批待办
        tv_outofoffice.setOnClickListener(new OnClickListener() {           
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                m_threadlog.setparm2("点击","审批待办", "审批待办-出差审批-首页");
                m_threadlog.writelog();

            	String url=""+appUrl+"/OutWeb/wapgTasks/"+userId;
            	Intent intent = new Intent();
				intent.setClass(OutOfficeActivity.this, WebViewHome.class);
				intent.putExtra("webUrl",url);
				intent.putExtra("titleName","出差审批登记"); 
				//intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
            }
        });
        
        //登记历史
        tv_search.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                m_threadlog.setparm2("点击","登记历史-出差审批", "在岗查询-出差审批-首页");
                m_threadlog.writelog();

            	String url= appUrl+"/OutWeb/wapqueryout/"+userId;
            	Intent intent = new Intent();
				intent.setClass(OutOfficeActivity.this, WebViewHome.class);
				intent.putExtra("webUrl",url);
				intent.putExtra("titleName","出差审批登记"); 
				startActivity(intent);
            }
        });

        //领导动态
        tv_leader.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                m_threadlog.setparm2("点击","领导动态-出差审批", "领导动态-出差审批-首页");
                m_threadlog.writelog();

                String url=""+appUrl+"/OutWeb/EmployeeDynamicState/getLeaderStatus/"+userId;
                Intent intent = new Intent();
                intent.setClass(OutOfficeActivity.this, WebViewHome.class);
                intent.putExtra("webUrl",url);
                intent.putExtra("titleName","领导动态");
                startActivity(intent);
            }
        });

        //本单位员工动态
        tv_user.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                m_threadlog.setparm2("点击","本单位员工动态-出差审批", "本单位员工动态-出差审批-首页");
                m_threadlog.writelog();

                String url=""+appUrl+"/OutWeb/EmployeeDynamicState/getCompanyUserStatus/"+userId+"/"+m_Leader;
                Intent intent = new Intent();
                intent.setClass(OutOfficeActivity.this, WebViewHome.class);
                intent.putExtra("webUrl",url);
                intent.putExtra("titleName","本单位员工动态");
                startActivity(intent);
            }
        });

        //报备申请
        tv_baobeisq.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                m_threadlog.setparm2("点击","报备申请", "报备申请-出差审批-首页");
                m_threadlog.writelog();

                String url=""+appUrl+"/OutWeb/wapRegReport/"+userId;
                Intent intent = new Intent();
                intent.setClass(OutOfficeActivity.this, WebViewHome.class);
                intent.putExtra("webUrl",url);
                intent.putExtra("titleName","出差审批登记");
                startActivity(intent);
            }
        });

        //报备列表
        tv_baobeilb.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                m_threadlog.setparm2("点击","报备列表", "报备列表-出差审批-首页");
                m_threadlog.writelog();

                String url=""+appUrl+"/OutWeb/wapqueryReportList/"+userId;
                Intent intent = new Intent();
                intent.setClass(OutOfficeActivity.this, WebViewHome.class);
                intent.putExtra("webUrl",url);
                intent.putExtra("titleName","出差审批登记");
                startActivity(intent);
            }
        });
        
        //我要出差
        /*tv_outoffice.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub	
                m_threadlog.setparm2("点击","我要出差2", "我要出差2-出差审批-首页");
                m_threadlog.writelog();

                String url=""+appUrl+"/OutWeb/wepRegisterOutOfOffice/"+userId;
            	Intent intent = new Intent();           	
				intent.setClass(OutOfficeActivity.this, WebViewHome.class);
				intent.putExtra("webUrl",url);
				intent.putExtra("titleName","出差审批登记"); 
				startActivity(intent);
            }
        });

        //更多操作
        tv_more.setOnClickListener(new OnClickListener() {           
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub	
                m_threadlog.setparm2("点击","更多操作", "更多操作-出差审批-首页");
                m_threadlog.writelog();

                showWin();
            }
        });*/
        
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
 		tv_navtitle.setText("出差审批登记");
    }

    private void showWin()
    {
    	MoreListPopupWindow popupWindow = new MoreListPopupWindow(OutOfficeActivity.this, 0, 0);
    	
    	// 显示窗口
    	popupWindow.showAtLocation(findViewById(R.id.re_outofoffice_index),
    		Gravity.NO_GRAVITY | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
    }

}