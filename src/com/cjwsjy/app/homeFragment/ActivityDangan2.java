package com.cjwsjy.app.homeFragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.Spanned;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cjwsjy.app.R;
import com.cjwsjy.app.SmApplication;
import com.cjwsjy.app.outoffice.MoreListPopupWindow;
import com.cjwsjy.app.utils.HttpClientUtil;
import com.cjwsjy.app.utils.UrlUtil;
import com.cjwsjy.app.webview.WebViewHome3;

import org.json.JSONArray;
import org.json.JSONObject;

public class ActivityDangan2 extends Activity
{
    private SharedPreferences sp;
	
    private  TextView tv_sousuo;
    private  TextView tv_shoucang;
    private  TextView tv_jieyue;
    private  TextView tv_xiazai;//外出登记
    private  TextView tv_daiban;//更多
    private  TextView tv_dangan;
    private  TextView tv_faqi;
    private  TextView tv_shenpi;
    private  TextView tv_zaixian;
    
    private String m_loginname;
    private String userId;
    private String appUrl;
    private String m_jieyue = "0";
    private String m_xiazai = "0";
    private String m_daiban = "0";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_dangan2);

        String strurl = "";
        String strtext = "";

        tv_sousuo =(TextView) findViewById(R.id.tv_sousuo);
        tv_shoucang =(TextView) findViewById(R.id.tv_shoucang);
        tv_jieyue =(TextView) findViewById(R.id.tv_jieyue);
        tv_xiazai =(TextView) findViewById(R.id.tv_xiazai);
        tv_daiban = (TextView) findViewById(R.id.tv_daiban);
        tv_dangan = (TextView) findViewById(R.id.tv_dangan);
        tv_faqi = (TextView) findViewById(R.id.tv_faqi);
        tv_shenpi = (TextView) findViewById(R.id.tv_shenpi);
        tv_zaixian = (TextView) findViewById(R.id.tv_zaixian);
        
        appUrl = UrlUtil.HOST;
        //获取用户登录名
		sp = SmApplication.sp;
        m_loginname =sp.getString("USERDATA.LOGIN.NAME", "");
		userId = sp.getString("USERDATA.USER.ID", "");

        strtext = "借阅申请单(<font color='#ff0000'>0</font>条)";
        Spanned span = Html.fromHtml(strtext);
        tv_jieyue.setText(span);

        strtext = "下载申请单(<font color='#ff0000'>0</font>条)";
        span = Html.fromHtml(strtext);
        tv_xiazai.setText(span);

        strtext = "我的待办(<font color='#ff0000'>0</font>条)";
        span = Html.fromHtml(strtext);
        tv_daiban.setText(span);

        //综合搜索
        tv_sousuo.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
            	String url=""+appUrl+"/CEGWAPServer/DAManage/search/"+m_loginname;
            	Intent intent = new Intent();           	
				intent.setClass(ActivityDangan2.this, WebViewHome3.class);
				intent.putExtra("webUrl",url);
				intent.putExtra("titleName","综合搜索");
				startActivity(intent);
            }
        });
        
        //收藏夹
        tv_shoucang.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
            	String url=""+appUrl+"/CEGWAPServer/DAManage/getCollectList/"+m_loginname;
            	Intent intent = new Intent();
				intent.setClass(ActivityDangan2.this, WebViewHome3.class);
				intent.putExtra("webUrl",url);
				intent.putExtra("titleName","收藏夹");
				startActivity(intent);
            }
        });
        
        //借阅申请单
        tv_jieyue.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
            	//Intent intent = new Intent(OutOfficeActivity.this, RegisterActivity.class);
            	String url=""+appUrl+"/CEGWAPServer/DAManage/SheetListNormal/"+m_loginname;
            	Intent intent = new Intent();
				intent.setClass(ActivityDangan2.this, WebViewHome3.class);
				intent.putExtra("webUrl",url);
				intent.putExtra("titleName","借阅申请单");
				startActivity(intent);
            }
        });
        
        //下载申请单
        tv_xiazai.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub	
            	String url=""+appUrl+"/CEGWAPServer/DAManage/DownLoadList/"+m_loginname;
            	Intent intent = new Intent();           	
				intent.setClass(ActivityDangan2.this, WebViewHome3.class);
				intent.putExtra("webUrl",url);
				intent.putExtra("titleName","下载申请单");
				startActivity(intent);
            }
        });

        //我的待办
        tv_daiban.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub	
                String url=""+appUrl+"/CEGWAPServer/DAManage/getToDoList/"+m_loginname;
                Intent intent = new Intent();
                intent.setClass(ActivityDangan2.this, WebViewHome3.class);
                intent.putExtra("webUrl",url);
                intent.putExtra("titleName","我的待办");
                startActivity(intent);
            }
        });

        //电子档案下载
        tv_dangan.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub
                String url=""+appUrl+"/CEGWAPServer/DAManage/DownLoadApplyPass/"+m_loginname;
                Intent intent = new Intent();
                intent.setClass(ActivityDangan2.this, WebViewHome3.class);
                intent.putExtra("webUrl",url);
                intent.putExtra("titleName","电子档案下载");
                startActivity(intent);
            }
        });

        //我的发起
        tv_faqi.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub
                String url=""+appUrl+"/CEGWAPServer/DAManage/myStartFlowList/"+m_loginname;
                Intent intent = new Intent();
                intent.setClass(ActivityDangan2.this, WebViewHome3.class);
                intent.putExtra("webUrl",url);
                intent.putExtra("titleName","我的发起");
                startActivity(intent);
            }
        });

        //我的审批
        tv_shenpi.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub
                String url=""+appUrl+"/CEGWAPServer/DAManage/myApproveList/"+m_loginname;
                Intent intent = new Intent();
                intent.setClass(ActivityDangan2.this, WebViewHome3.class);
                intent.putExtra("webUrl",url);
                intent.putExtra("titleName","我的审批");
                startActivity(intent);
            }
        });

        //在线查看
        tv_zaixian.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub
//                String url=""+appUrl+"/CEGWAPServer/DAManage/getToDoList/"+m_loginname;
//                Intent intent = new Intent();
//                intent.setClass(ActivityDangan2.this, WebViewHome3.class);
//                intent.putExtra("webUrl",url);
//                intent.putExtra("titleName","我的待办");
//                startActivity(intent);
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
 		tv_navtitle.setText("档案管理");

        //开线程，发http请求，得到待办条数
        Thread loginThread = new Thread(new ThreadGetdata());
        loginThread.start();
    }

    class ThreadGetdata implements Runnable
    {
        @Override
        public void run()
        {
            boolean bresult = false;
            int j = 0;
            int i = 0;
            int length = 0;
            String titlestr;
            String countstr;
            JSONObject jsonObj;

            try
            {
                String url = appUrl + "/CEGWAPServer/DAManage/getListNumber/" + m_loginname;
                String resultStr = HttpClientUtil.HttpUrlConnectionGet(url, "UTF-8");
                length = resultStr.length();
                if( length==0 ) return;

                JSONArray jsonObjs;
                jsonObjs = new JSONArray( resultStr );

                length = jsonObjs.length();
                for (i = 0; i < length; i++)
                {
                    jsonObj = jsonObjs.getJSONObject(i);

                    // 总数
                    titlestr = jsonObj.getString("title");
                    countstr = jsonObj.getString("count");
                    bresult = titlestr.equals("借阅申请单总条数");
                    if( bresult==true )
                    {
                        m_jieyue = countstr;
                        continue;
                    }

                    bresult = titlestr.equals("下载申请单总条数");
                    if( bresult==true )
                    {
                        m_xiazai = countstr;
                        continue;
                    }

                    bresult = titlestr.equals("档案管理待办");
                    if( bresult==true )
                    {
                        m_daiban = countstr;
                        continue;
                    }
                }

                Message msg;
                msg = handler.obtainMessage();
                msg.what = 0;
                handler.sendMessage(msg);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {

            this.obtainMessage();
            switch (msg.what)
            {
                case 0:
                    fillList();
                    break;
                case 1:  //登录时，用户密码错误
                    //mDialog.cancel();
                    Toast.makeText(getApplicationContext(), msg.obj.toString(),Toast.LENGTH_SHORT).show();
                    //mDialog.cancel();
                    break;
                case 2:
                    //mDialog.cancel();
                    Toast.makeText(getApplicationContext(), "网络连接超时",Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    private void fillList()
    {
        String strtext = "";

        //Spanned span = Html.fromHtml("北京市发布霾黄色预警，<font color='#ff0000'>外出携带好</font>口罩");
        //tv_jieyue.setText(span);

        strtext = "借阅申请单(<font color='#ff0000'>"+m_jieyue+"</font>条)";
        Spanned span = Html.fromHtml(strtext);
        tv_jieyue.setText(span);

        strtext = "下载申请单(<font color='#ff0000'>"+m_xiazai+"</font>条)";
        span = Html.fromHtml(strtext);
        tv_xiazai.setText(span);

        strtext = "我的待办(<font color='#ff0000'>"+m_daiban+"</font>条)";
        span = Html.fromHtml(strtext);
        tv_daiban.setText(span);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        //开线程，发http请求，刷新待办条数
        Thread loginThread = new Thread(new ThreadGetdata());
        loginThread.start();
    }

    private void showWin()
    {
    	MoreListPopupWindow popupWindow = new MoreListPopupWindow(ActivityDangan2.this, 0, 0);
    	
    	// 显示窗口
    	popupWindow.showAtLocation(findViewById(R.id.re_outofoffice_index),
    		Gravity.NO_GRAVITY | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
    }

}