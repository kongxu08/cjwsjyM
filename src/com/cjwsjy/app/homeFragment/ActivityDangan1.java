package com.cjwsjy.app.homeFragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cjwsjy.app.R;
import com.cjwsjy.app.SmApplication;
import com.cjwsjy.app.item.FinishPeddingItem;
import com.cjwsjy.app.outoffice.MoreListPopupWindow;
import com.cjwsjy.app.utils.HttpClientUtil;
import com.cjwsjy.app.utils.ThreadUtils;
import com.cjwsjy.app.utils.UrlUtil;
import com.cjwsjy.app.webview.WebViewHome3;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ActivityDangan1 extends Activity
{
    private SharedPreferences sp;
    private ThreadUtils m_threadlog;

    List<FinishPeddingItem> listItems = new ArrayList<FinishPeddingItem>();

    private AdaperItem3 listItemAdapter;
	
    private  TextView tv_sousuo;
    private  TextView tv_shoucang;
    private  TextView tv_jieyue;
    private  TextView tv_xiazai;//外出登记
    private  TextView tv_daiban;//更多
    private  TextView tv_dangan;
    private  TextView tv_faqi;
    private  TextView tv_shenpi;
    private  TextView tv_zaixian;

    ListView listview;
    
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
        setContentView(R.layout.activity_dangan1);

        String strurl = "";
        String strtext = "";

        m_threadlog = ThreadUtils.getInstance();  // 得到单例对象

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

        listview = (ListView) findViewById(R.id.list_dangan);

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

        initList();

        //开线程，发http请求，得到待办条数
        Thread loginThread = new Thread(new ThreadGetdata());
        loginThread.start();
    }

    public ListView initList()
    {
        FinishPeddingItem listItem1 = new FinishPeddingItem();
        listItem1.setIv_icon1(R.drawable.home_dangan01);
        listItem1.setTv_title("综合搜索");
        listItems.add(listItem1);

        FinishPeddingItem listItem2 = new FinishPeddingItem();
        listItem2.setIv_icon1(R.drawable.home_dangan02);
        listItem2.setTv_title("收藏夹");
        listItems.add(listItem2);

        FinishPeddingItem listItem3 = new FinishPeddingItem();
        listItem3.setIv_icon1(R.drawable.home_dangan05);
        listItem3.setTv_title("我的待办");
        listItem3.setTv_date("0");
        listItems.add(listItem3);

        FinishPeddingItem listItem4 = new FinishPeddingItem();
        listItem4.setIv_icon1(R.drawable.home_dangan03);
        listItem4.setTv_title("借阅申请单");
        listItem4.setTv_date("0");
        listItems.add(listItem4);

        FinishPeddingItem listItem5 = new FinishPeddingItem();
        listItem5.setIv_icon1(R.drawable.home_dangan04);
        listItem5.setTv_title("下载申请单");
        listItem5.setTv_date("0");
        listItems.add(listItem5);

        FinishPeddingItem listItem6 = new FinishPeddingItem();
        listItem6.setIv_icon1(R.drawable.home_dangan06);
        listItem6.setTv_title("电子档案下载");
        listItems.add(listItem6);

        FinishPeddingItem listItem7 = new FinishPeddingItem();
        listItem7.setIv_icon1(R.drawable.home_dangan07);
        listItem7.setTv_title("我的发起");
        listItems.add(listItem7);

        FinishPeddingItem listItem8 = new FinishPeddingItem();
        listItem8.setIv_icon1(R.drawable.home_dangan08);
        listItem8.setTv_title("我的审批");
        listItems.add(listItem8);

        FinishPeddingItem listItem9 = new FinishPeddingItem();
        listItem9.setIv_icon1(R.drawable.home_dangan09);
        listItem9.setTv_title("离线阅览");
        listItems.add(listItem9);

        // 生成适配器的Item和动态数组对应的元素
        listItemAdapter = new AdaperItem3( ActivityDangan1.this, listItems );
        listItemAdapter.setListView(listview);
        // 添加并且显示
        listview.setAdapter(listItemAdapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position, long id)
            {
                String url;
                FinishPeddingItem item = (FinishPeddingItem) listview.getItemAtPosition(position);
                String formId = item.getTv_formid();

                if (position == 0)
                {
                    m_threadlog.setparm2("点击","综合搜索-档案管理", "综合搜索-档案管理-首页");
                    m_threadlog.writelog();

                    url=""+appUrl+"/CEGWAPServer/DAManage/search/"+m_loginname;
                    Intent intent = new Intent();
                    intent.setClass(ActivityDangan1.this, WebViewHome3.class);
                    intent.putExtra("webUrl",url);
                    intent.putExtra("titleName","综合搜索");
                    startActivity(intent);
                }
                if( position==1 )
                {
                    m_threadlog.setparm2("点击","收藏夹-档案管理", "收藏夹-档案管理-首页");
                    m_threadlog.writelog();

                    url=""+appUrl+"/CEGWAPServer/DAManage/getCollectList/"+m_loginname;
                    Intent intent = new Intent();
                    intent.setClass(ActivityDangan1.this, WebViewHome3.class);
                    intent.putExtra("webUrl",url);
                    intent.putExtra("titleName","收藏夹");
                    startActivity(intent);
                }
                if( position==2 )
                {
                    m_threadlog.setparm2("点击","我的待办-档案管理", "我的待办-档案管理-首页");
                    m_threadlog.writelog();

                    url=""+appUrl+"/CEGWAPServer/DAManage/getToDoList/"+m_loginname;
                    Intent intent = new Intent();
                    intent.setClass(ActivityDangan1.this, WebViewHome3.class);
                    intent.putExtra("webUrl",url);
                    intent.putExtra("titleName","我的待办");
                    startActivity(intent);
                }
                if( position==3 )
                {
                    m_threadlog.setparm2("点击","借阅申请单-档案管理", "借阅申请单-档案管理-首页");
                    m_threadlog.writelog();

                    url=""+appUrl+"/CEGWAPServer/DAManage/SheetListNormal/"+m_loginname;
                    Intent intent = new Intent();
                    intent.setClass(ActivityDangan1.this, WebViewHome3.class);
                    intent.putExtra("webUrl",url);
                    intent.putExtra("titleName","借阅申请单");
                    startActivity(intent);
                }
                if( position==4 )
                {
                    m_threadlog.setparm2("点击","下载申请单-档案管理", "下载申请单-档案管理-首页");
                    m_threadlog.writelog();

                    url=""+appUrl+"/CEGWAPServer/DAManage/DownLoadList/"+m_loginname;
                    Intent intent = new Intent();
                    intent.setClass(ActivityDangan1.this, WebViewHome3.class);
                    intent.putExtra("webUrl",url);
                    intent.putExtra("titleName","下载申请单");
                    startActivity(intent);
                }
                if( position==5 )
                {
                    m_threadlog.setparm2("点击","电子档案下载-档案管理", "电子档案下载-档案管理-首页");
                    m_threadlog.writelog();

                    url=""+appUrl+"/CEGWAPServer/DAManage/DownLoadApplyPass/"+m_loginname;
                    Intent intent = new Intent();
                    intent.setClass(ActivityDangan1.this, WebViewHome3.class);
                    intent.putExtra("webUrl",url);
                    intent.putExtra("titleName","电子档案下载");
                    startActivity(intent);
                }
                if( position==6 )
                {
                    m_threadlog.setparm2("点击","我的发起-档案管理", "我的发起-档案管理-首页");
                    m_threadlog.writelog();

                    url=""+appUrl+"/CEGWAPServer/DAManage/myStartFlowList/"+m_loginname;
                    Intent intent = new Intent();
                    intent.setClass(ActivityDangan1.this, WebViewHome3.class);
                    intent.putExtra("webUrl",url);
                    intent.putExtra("titleName","我的发起");
                    startActivity(intent);
                }
                if( position==7 )
                {
                    m_threadlog.setparm2("点击","我的审批-档案管理", "我的审批-档案管理-首页");
                    m_threadlog.writelog();

                    url=""+appUrl+"/CEGWAPServer/DAManage/myApproveList/"+m_loginname;
                    Intent intent = new Intent();
                    intent.setClass(ActivityDangan1.this, WebViewHome3.class);
                    intent.putExtra("webUrl",url);
                    intent.putExtra("titleName","我的审批");
                    startActivity(intent);
                }
                if( position==8 )
                {
                    m_threadlog.setparm2("点击","离线阅览-档案管理", "离线阅览-档案管理-首页");
                    m_threadlog.writelog();

                    Intent intent = new Intent( ActivityDangan1.this, ActivityLixian.class);
                    startActivity(intent);
                }
            }
        });

        return listview;
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

        listItemAdapter.updateItem(2,m_daiban);
        listItemAdapter.updateItem(3,m_jieyue);
        listItemAdapter.updateItem(4,m_xiazai);
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
    	MoreListPopupWindow popupWindow = new MoreListPopupWindow(ActivityDangan1.this, 0, 0);
    	
    	// 显示窗口
    	popupWindow.showAtLocation(findViewById(R.id.re_outofoffice_index),
    		Gravity.NO_GRAVITY | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
    }

}