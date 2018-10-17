package com.cjwsjy.app.homeFragment;

import java.util.ArrayList;
import java.util.List;

import com.cjwsjy.app.R;
import com.cjwsjy.app.SmApplication;
import com.cjwsjy.app.base.BaseActivity;
import com.cjwsjy.app.item.FinishPeddingItem;
import com.cjwsjy.app.utils.HttpClientUtil;
import com.cjwsjy.app.utils.ThreadUtils;
import com.cjwsjy.app.utils.UrlUtil;
import com.cjwsjy.app.webview.WebViewHome;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

public class ActivityXiangmu extends BaseActivity implements OnItemClickListener 
{
	private SharedPreferences sp;
	private ThreadUtils m_threadlog;

	// 生成动态数组，加入数据
	List<FinishPeddingItem> listItems = new ArrayList<FinishPeddingItem>();

	//private AdaperXinwen listItemAdapter;
	private AdaperBaoxiao listItemAdapter;
	private String m_loginname;
	private String m_userId;
	private String appUrl;

	private String m_xiangmu1 = "0";
	private String m_xiangmu2 = "0";
	private String m_xiangmu3 = "0";
	private String m_xiangmu4 = "0";
	private String m_xiangmu5 = "0";
	private String m_xiangmu6 = "0";
	private String m_xiangmu7 = "0";
	private String m_xiangmu8 = "0";
	private String m_xiangmu9 = "0";
	private String m_xiangmu10 = "0";
	private String m_xiangmu11 = "0";
	private String m_xiangmu12 = "0";
	private String m_xiangmu13 = "0";
	private String m_xiangmu14 = "0";
	private String m_xiangmu15 = "0";


	private ListView list;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature( Window.FEATURE_NO_TITLE );
		setContentView(R.layout.activity_homelist);

		m_threadlog = ThreadUtils.getInstance();  // 得到单例对象
		
		sp = SmApplication.sp;
		m_loginname = sp.getString("USERDATA.LOGIN.NAME", "");
		m_userId = sp.getString("USERDATA.USER.ID", "");
		
		appUrl = UrlUtil.HOST;
		 
		list = (ListView) findViewById(R.id.homelist);
		
		TextView tv_navtitle = (TextView) findViewById(R.id.tv_navtitle);
		tv_navtitle.setText("项目管理");

		ImageView iv_back = (ImageView) this.findViewById(R.id.iv_back);
		// 后退
		iv_back.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				finish();
			}
		});

		//开线程，得到待办条数
		Thread ThreadBaoxiao = new Thread(new ThreadHttp());
		ThreadBaoxiao.start();

		initList();
	}

	public ListView initList() 
	{
		FinishPeddingItem listItem1 = new FinishPeddingItem();
		listItem1.setIv_icon1(R.drawable.home_xiangmu06);
		listItem1.setTv_title("外委(科研)待办");
		listItem1.setTv_date("0");
		listItems.add(listItem1);


		FinishPeddingItem listItem3 = new FinishPeddingItem();
		listItem3.setIv_icon1(R.drawable.home_xiangmu07);
		listItem3.setTv_title("外委(生产)待办");
		listItem3.setTv_date("0");
		listItems.add(listItem3);


		FinishPeddingItem listItem5 = new FinishPeddingItem();
		listItem5.setIv_icon1(R.drawable.home_xiangmu05);
		listItem5.setTv_title("付款审签待办");
		listItem5.setTv_date("0");
		listItems.add(listItem5);

		FinishPeddingItem listItem7 = new FinishPeddingItem();
		listItem7.setIv_icon1(R.drawable.home_xiangmu08);
		listItem7.setTv_title("资质借用待办");
		listItem7.setTv_date("0");
		listItems.add(listItem7);

		FinishPeddingItem listItem9 = new FinishPeddingItem();
		listItem9.setIv_icon1(R.drawable.home_xiangmu10);
		listItem9.setTv_title("收入合同待办");
		listItem9.setTv_date("0");
		listItems.add(listItem9);

		FinishPeddingItem listItem11 = new FinishPeddingItem();
		listItem11.setIv_icon1(R.drawable.home_xiangmu12);
		listItem11.setTv_title("保函保证金待办");
		listItem11.setTv_date("0");
		listItems.add(listItem11);

		FinishPeddingItem listItem12 = new FinishPeddingItem();
		listItem12.setIv_icon1(R.drawable.home_xiangmu14);
		listItem12.setTv_title("项目登记待办");
		listItem12.setTv_date("0");
		listItems.add(listItem12);

		FinishPeddingItem listItem13 = new FinishPeddingItem();
		listItem13.setIv_icon1(R.drawable.home_xiangmu15);
		listItem13.setTv_title("发票申请待办");
		listItem13.setTv_date("0");
		listItems.add(listItem13);

		FinishPeddingItem listItem15 = new FinishPeddingItem();
		listItem15.setIv_icon1(R.drawable.home_xiangmu16);
		listItem15.setTv_title("设校审待办");
		listItem15.setTv_date("0");
		listItems.add(listItem15);

		FinishPeddingItem listItem16 = new FinishPeddingItem();
		listItem16.setIv_icon1(R.drawable.home_xiangmu17);
		listItem16.setTv_title("投标备案申请待办");
		listItem16.setTv_date("0");
		listItems.add(listItem16);

		FinishPeddingItem listItem17 = new FinishPeddingItem();
		listItem17.setIv_icon1(R.drawable.home_xiangmu21);
		listItem17.setTv_title("互提资料待办");
		listItem17.setTv_date("0");
		listItems.add(listItem17);

		FinishPeddingItem listItem18 = new FinishPeddingItem();
		listItem18.setIv_icon1(R.drawable.home_xiangmu19);
		listItem18.setTv_title("设计资料验证待办");
		listItem18.setTv_date("0");
		listItems.add(listItem18);

		FinishPeddingItem listItem19 = new FinishPeddingItem();
		listItem19.setIv_icon1(R.drawable.home_xiangmu20);
		listItem19.setTv_title("出院成果评审待办");
		listItem19.setTv_date("0");
		listItems.add(listItem19);

		FinishPeddingItem listItem20 = new FinishPeddingItem();
		listItem20.setIv_icon1(R.drawable.home_xiangmu18);
		listItem20.setTv_title("成果会签待办");
		listItem20.setTv_date("0");
		listItems.add(listItem20);

		FinishPeddingItem listItem21 = new FinishPeddingItem();
		listItem21.setIv_icon1(R.drawable.home_xiangmu22);
		listItem21.setTv_title("子项目部组建待办");
		listItem21.setTv_date("0");
		listItems.add(listItem21);

		FinishPeddingItem listItem14 = new FinishPeddingItem();
		listItem14.setIv_icon1(R.drawable.home_xiangmu12);
		listItem14.setTv_title("经办搜索");
		listItems.add(listItem14);
		
		// 生成适配器的Item和动态数组对应的元素
		listItemAdapter = new AdaperBaoxiao( ActivityXiangmu.this, listItems );
		// 添加并且显示
		list.setAdapter(listItemAdapter);

		list.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) 
			{
				String url;
				FinishPeddingItem item = (FinishPeddingItem) list.getItemAtPosition(position);
				String formId = item.getTv_formid();

				if (position == 0)
				{
					m_threadlog.setparm2("点击","项目管理-外委(科研)待办",  "付款审签待办-项目管理-首页");
					m_threadlog.writelog();

					url = appUrl+"/CEGWAPServer/XMManage/outContractAuditKY_DB/"+m_loginname;
					startWebView(url, "外委(科研)待办");
				}
				else if( position==1 )
				{
					m_threadlog.setparm2("点击","项目管理-外委(生产)待办", "付款审签待办-项目管理-首页");
					m_threadlog.writelog();

					url = appUrl+"/CEGWAPServer/XMManage/outContractAuditSC_DB/"+m_loginname;
					startWebView(url,"外委(生产)待办");
				}
				else if( position==2 )
				{
					m_threadlog.setparm2("点击","项目管理-付款审签待办", "付款审签待办-项目管理-首页");
					m_threadlog.writelog();

					url = appUrl+"/CEGWAPServer/XMManage/ContractPaySH_DB/"+m_loginname;
					startWebView(url,"付款审签待办");
				}
				else if( position==3 )
				{
					m_threadlog.setparm2("点击","项目管理-资质借用待办", "资质借用待办-项目管理-首页");
					m_threadlog.writelog();

					url = appUrl+"/CEGWAPServer/XMManage/qualification_DB/"+m_loginname;
					startWebView(url,"资质借用待办");
				}
				else if( position==4 )
				{
					m_threadlog.setparm2("点击","项目管理-收入合同待办", "收入合同待办-项目管理-首页");
					m_threadlog.writelog();

					url = appUrl+"/CEGWAPServer/XMManage/ProjectRiskEvaluation_DB/"+m_loginname;
					startWebView(url,"收入合同待办");
				}
				else if( position==5 )
				{
					m_threadlog.setparm2("点击","项目管理-保函保证金待办", "保函保证金待办-项目管理-首页");
					m_threadlog.writelog();

					url = appUrl+"/CEGWAPServer/XMManage/BackLetter_DB/"+m_loginname;
					startWebView(url,"保函保证金待办");
				}
				else if( position==6 )
				{
					m_threadlog.setparm2("点击","项目管理-项目登记待办","项目登记待办-项目管理-首页");
					m_threadlog.writelog();

					url = appUrl+"/CEGWAPServer/XMManage/ProjectRegister_DB/"+m_loginname;
					startWebView(url,"项目登记待办");
				}
				else if( position==7 )
				{
					m_threadlog.setparm2("点击","项目管理-发票申请待办","发票申请待办-项目管理-首页");
					m_threadlog.writelog();

					url = appUrl+"/CEGWAPServer/XMManage/InvoiceInfo_DB/"+m_loginname;
					startWebView(url,"发票申请待办");
				}
				else if( position==8 )
				{
					m_threadlog.setparm2("点击","项目管理-设校审待办","设校审待办-项目管理-首页");
					m_threadlog.writelog();

					url = appUrl+"/CEGWAPServer/AuditForm/DB/"+m_userId;
					startWebView(url,"设校审待办");
				}
				else if( position==9 )
				{
					m_threadlog.setparm2("点击","投标备案申请待办-项目管理","投标备案申请待办-项目管理-首页");
					m_threadlog.writelog();

					url = appUrl+"/CEGWAPServer/BidRecordApply/DB/"+m_userId;
					startWebView(url,"投标备案申请待办");
				}
				else if( position==10 )
				{
					m_threadlog.setparm2("点击","互提资料待办-项目管理","互提资料待办-项目管理-首页");
					m_threadlog.writelog();

					url = appUrl+"/CEGWAPServer/CollaborationData/DB/"+m_userId;
					startWebView(url,"互提资料待办");
				}
				else if( position==11 )
				{
					m_threadlog.setparm2("点击","设计资料验证待办-项目管理","设计资料验证待办-项目管理-首页");
					m_threadlog.writelog();

					url = appUrl+"/CEGWAPServer/DesignDataVerify/DB/"+m_userId;
					startWebView(url,"设计资料验证待办");
				}
				else if( position==12 )
				{
					m_threadlog.setparm2("点击","出院成果待办-项目管理","出院成果待办-项目管理-首页");
					m_threadlog.writelog();

					url = appUrl+"/CEGWAPServer/ProjectOutReview/DB/"+m_userId;
					startWebView(url,"出院成果待办");
				}
				else if( position==13 )
				{
					m_threadlog.setparm2("点击","成果会签待办-项目管理","成果会签待办-项目管理-首页");
					m_threadlog.writelog();

					url = appUrl+"/CEGWAPServer/SignData/DB/"+m_userId;
					startWebView(url,"成果会签待办");
				}
				else if( position==14 )
				{
					m_threadlog.setparm2("点击","子项目部组建待办-项目管理","子项目部组建待办-项目管理-首页");
					m_threadlog.writelog();

					url = appUrl+"/CEGWAPServer/SonDepartBuild/DB/"+m_userId;
					startWebView(url,"子项目部组建待办");
				}

				else if( position==15 )
				{
					m_threadlog.setparm2("点击","项目管理-经办搜索", "经办搜索-项目管理-首页");
					m_threadlog.writelog();

					url = appUrl+"/CEGWAPServer/XMManage/search_XM/"+m_loginname;
					startWebView(url,"经办搜索");
				}
			}
		});
		return list;
	}

	private void fillList()
	{
		listItems.clear();

		FinishPeddingItem listItem1 = new FinishPeddingItem();
		listItem1.setIv_icon1(R.drawable.home_xiangmu06);
		listItem1.setTv_title("外委(科研)待办");
		listItem1.setTv_date(m_xiangmu1);
		listItems.add(listItem1);


		FinishPeddingItem listItem3 = new FinishPeddingItem();
		listItem3.setIv_icon1(R.drawable.home_xiangmu07);
		listItem3.setTv_title("外委(生产)待办");
		listItem3.setTv_date(m_xiangmu2);
		listItems.add(listItem3);


		FinishPeddingItem listItem5 = new FinishPeddingItem();
		listItem5.setIv_icon1(R.drawable.home_xiangmu05);
		listItem5.setTv_title("付款审签待办");
		listItem5.setTv_date(m_xiangmu3);
		listItems.add(listItem5);

		FinishPeddingItem listItem7 = new FinishPeddingItem();
		listItem7.setIv_icon1(R.drawable.home_xiangmu08);
		listItem7.setTv_title("资质借用待办");
		listItem7.setTv_date(m_xiangmu4);
		listItems.add(listItem7);

		FinishPeddingItem listItem9 = new FinishPeddingItem();
		listItem9.setIv_icon1(R.drawable.home_xiangmu10);
		listItem9.setTv_title("收入合同待办");
		listItem9.setTv_date(m_xiangmu5);
		listItems.add(listItem9);

		FinishPeddingItem listItem11 = new FinishPeddingItem();
		listItem11.setIv_icon1(R.drawable.home_xiangmu12);
		listItem11.setTv_title("保函保证金待办");
		listItem11.setTv_date(m_xiangmu6);
		listItems.add(listItem11);

		FinishPeddingItem listItem12 = new FinishPeddingItem();
		listItem12.setIv_icon1(R.drawable.home_xiangmu14);
		listItem12.setTv_title("项目登记待办");
		listItem12.setTv_date(m_xiangmu7);
		listItems.add(listItem12);

		FinishPeddingItem listItem13 = new FinishPeddingItem();
		listItem13.setIv_icon1(R.drawable.home_xiangmu15);
		listItem13.setTv_title("发票申请待办");
		listItem13.setTv_date(m_xiangmu8);
		listItems.add(listItem13);

		FinishPeddingItem listItem15 = new FinishPeddingItem();
		listItem15.setIv_icon1(R.drawable.home_xiangmu16);
		listItem15.setTv_title("设校审待办");
		listItem15.setTv_date(m_xiangmu9);
		listItems.add(listItem15);

		FinishPeddingItem listItem16 = new FinishPeddingItem();
		listItem16.setIv_icon1(R.drawable.home_xiangmu17);
		listItem16.setTv_title("投标备案申请待办");
		listItem16.setTv_date(m_xiangmu10);
		listItems.add(listItem16);

		FinishPeddingItem listItem17 = new FinishPeddingItem();
		listItem17.setIv_icon1(R.drawable.home_xiangmu21);
		listItem17.setTv_title("互提资料待办");
		listItem17.setTv_date(m_xiangmu11);
		listItems.add(listItem17);

		FinishPeddingItem listItem18 = new FinishPeddingItem();
		listItem18.setIv_icon1(R.drawable.home_xiangmu19);
		listItem18.setTv_title("设计资料验证待办");
		listItem18.setTv_date(m_xiangmu12);
		listItems.add(listItem18);

		FinishPeddingItem listItem19 = new FinishPeddingItem();
		listItem19.setIv_icon1(R.drawable.home_xiangmu20);
		listItem19.setTv_title("出院成果评审待办");
		listItem19.setTv_date(m_xiangmu13);
		listItems.add(listItem19);

		FinishPeddingItem listItem20 = new FinishPeddingItem();
		listItem20.setIv_icon1(R.drawable.home_xiangmu18);
		listItem20.setTv_title("图纸会签待办");
		listItem20.setTv_date(m_xiangmu14);
		listItems.add(listItem20);

		FinishPeddingItem listItem21 = new FinishPeddingItem();
		listItem21.setIv_icon1(R.drawable.home_xiangmu22);
		listItem21.setTv_title("子项目部组建待办");
		listItem21.setTv_date(m_xiangmu15);
		listItems.add(listItem21);

		FinishPeddingItem listItem14 = new FinishPeddingItem();
		listItem14.setIv_icon1(R.drawable.home_xiangmu12);
		listItem14.setTv_title("经办搜索");
		listItems.add(listItem14);

		listItemAdapter.notifyDataSetChanged();;
	}

	class ThreadHttp implements Runnable
	{
		@Override
		public void run()
		{
			Getdaiban();
		}
	}

	//得到待办消息的条数
	private void Getdaiban()
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
			//String url = appUrl + "/CEGWAPServer/RecordTotal/getMyTaskTotal_XM/" + m_loginname;
			String url = appUrl+"/CEGWAPServer/RecordTotal/getMyTaskTotal_XM/" + m_loginname;
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
				bresult = titlestr.equals("外委(科研)待办");
				if( bresult==true )
				{
					m_xiangmu1 = countstr;
					continue;
				}

				bresult = titlestr.equals("外委(生产)待办");
				if( bresult==true )
				{
					m_xiangmu2 = countstr;
					continue;
				}

				bresult = titlestr.equals("付款审签待办");
				if( bresult==true )
				{
					m_xiangmu3 = countstr;
					continue;
				}

				bresult = titlestr.equals("资质借用待办");
				if( bresult==true )
				{
					m_xiangmu4 = countstr;
					continue;
				}

				bresult = titlestr.equals("收入合同风险评估待办");
				if( bresult==true )
				{
					m_xiangmu5 = countstr;
					continue;
				}

				bresult = titlestr.equals("保函保证金申请待办");
				if( bresult==true )
				{
					m_xiangmu6 = countstr;
					continue;
				}

				bresult = titlestr.equals("项目登记待办");
				if( bresult==true )
				{
					m_xiangmu7 = countstr;
					continue;
				}

				bresult = titlestr.equals("发票申请待办");
				if( bresult==true )
				{
					m_xiangmu8 = countstr;
					continue;
				}

				bresult = titlestr.equals("设计校审待办");
				if( bresult==true )
				{
					m_xiangmu9 = countstr;
					continue;
				}
				bresult = titlestr.equals("投标备案申请待办");
				if( bresult==true )
				{
					m_xiangmu10 = countstr;
					continue;
				}
				bresult = titlestr.equals("资料互提待办");
				if( bresult==true )
				{
					m_xiangmu11 = countstr;
					continue;
				}
				bresult = titlestr.equals("设计资料验证待办");
				if( bresult==true )
				{
					m_xiangmu12 = countstr;
					continue;
				}
				bresult = titlestr.equals("出院成果审查待办");
				if( bresult==true )
				{
					m_xiangmu13 = countstr;
					continue;
				}
				bresult = titlestr.equals("图纸会签待办");
				if( bresult==true )
				{
					m_xiangmu14 = countstr;
					continue;
				}
				bresult = titlestr.equals("子项目部组建待办");
				if( bresult==true )
				{
					m_xiangmu15 = countstr;
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

	private void startWebView(String urlstr,String title)
	{
		Intent intent = new Intent();           	
		intent.setClass( ActivityXiangmu.this, WebViewHome.class );
		intent.putExtra("webUrl",urlstr);
		intent.putExtra("titleName",title);
		startActivity(intent);
	}
	
	// 自动加载布局
	public LinearLayout initLayout() 
	{
		LinearLayout loadingLayout = null;

		return loadingLayout;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,long id) 
	{
		// TODO 自动生成的方法存根
	}

}
