package com.cjwsjy.app.vehicle;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.cjwsjy.app.R;
import com.cjwsjy.app.utils.HttpClientUtil;
import com.cjwsjy.app.utils.ValidUtil;
import com.cjwsjy.app.vehicle.BaseAdapterWrapper.ItemViewHandler;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 列表继承类
 * @Copyright:   Copyright (c)2013  
 * @Company:     广州市道一信息有限公司
 * @author:      Mr.y  
 * @version:     1.0  
 * @date:        2014-6-19 上午10:14:10  
 *  
 * Modification History:  
 * Date         Author      Version     Description  
 * ------------------------------------------------------------------  
 * 2014-6-19      Mr.y          1.0       1.0 Version
 */
abstract public class BaseListActivity4 extends BaseActivity2 implements ItemViewHandler{

	public PullToRefreshListView listview;
	public BaseAdapterWrapper adapter;
	public List<Map<String, Object>> datalist = new ArrayList<Map<String,Object>>();

	//自定义变量
	private int currentPage = 1;//当前页数
	//private int pageSize = 10;//每页请求条数
	private int totalPage = 0;//总页数
	public boolean isReflesh = false;//是否在请求数据
	public boolean isRequest = false;//是否需要请求数据

	//初始化数据
	public int parentResId = R.layout.list_view_default;//布局文件
	public int listItemResId;//item布局
	public String[] from;
	public int[] ids;
	public String url;
	public Map<String, Object> map = new HashMap<String, Object>();//参数

	/**
	 * 初始化数据
	 * parentResId-整个布局文件
	 * listItemResId-item布局文件
	 * from-数据源
	 * ids-数据源指向ID
	 * url-请求地址
	 * map-请求参数
	 */
	abstract public void initParams();//设置布局文件抽象方法

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initParams();
		setContentView(parentResId);

		aq = new AQuery(this);
		initItems();
		request(1098);
		//request(1001);
	}

	void initItems(){
		listview = (PullToRefreshListView) findViewById(R.id.list_view);
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				itemClick(arg0, arg1, arg2++, arg3);
			}
		});
		listview.setOnRefreshListener(new OnRefreshListener2<ListView>(){
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
            	pullDown();
            	baselistHandler.sendEmptyMessageAtTime(1, 1500);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
            	pullUp();
            	baselistHandler.sendEmptyMessageAtTime(2, 1500);
            }
        });
	}

	protected void itemClick(AdapterView<?> arg0, View view, int position,long id) {}
	protected void pullDown(){}
	protected void pullUp(){}

	private Handler baselistHandler = new Handler()
	{
		@Override
		public void handleMessage(android.os.Message msg)
		{
			if(msg.what == 1)
			{
				if(isReflesh)
				{
					listview.onRefreshComplete();
				}
				else
				{
					request(1098);
				}
			}
			else if(msg.what == 2)
			{
				if(currentPage < totalPage && !isReflesh)
				{
					currentPage++;
					map.put("current_page", currentPage+"");
					request(1099);
				}
				else
				{
					listview.onRefreshComplete();
				}
			}
		};
	};


	@Override
	protected void onExecuteSuccess(int requestId, ResultObject result)
	{
		super.onExecuteSuccess(requestId, result);
		listview.onRefreshComplete();
		if(result.getListMap().size()==0)
		{
			aq.id(R.id.nullText).visible();
			aq.id(android.R.id.list).gone();
			return;
		}
		if (requestId == 1098)
		{//第一次请求
			if(ValidUtil.isNumeric(result.getTotalPage()+""))
			{
				totalPage = result.getTotalPage();
			}
			datalist.clear();
			initFistList();
		}

		datalist.addAll(result.getListMap());
		if(datalist.size() == 0){
			aq.id(R.id.nullText).visible();
			aq.id(android.R.id.list).gone();
		}else{
			aq.id(R.id.nullText).gone();
			aq.id(android.R.id.list).visible();
		}

		if(adapter == null){
			SimpleAdapter sadapter = new SimpleAdapter(this, datalist, listItemResId, from, ids);
			adapter = new BaseAdapterWrapper(sadapter, this);
			listview.setAdapter(adapter);
		}else{
			adapter.notifyDataSetChanged();
		}
		isReflesh = false;
	}

	protected void onExecuteSuccess2(int requestId, ResultObject result)
	{
		super.onExecuteSuccess(requestId, result);
		listview.onRefreshComplete();
		if(result.getListMap().size()==0)
		{
			aq.id(R.id.nullText).visible();
			aq.id(android.R.id.list).gone();
			return;
		}
		if (requestId == 1098)
		{//第一次请求
			if(ValidUtil.isNumeric(result.getTotalPage()+""))
			{
				totalPage = result.getTotalPage();
			}
			datalist.clear();
			initFistList();
		}

		datalist.addAll(result.getListMap());
		if(datalist.size() == 0)
		{
			aq.id(R.id.nullText).visible();
			aq.id(android.R.id.list).gone();
		}
		else
		{
			aq.id(R.id.nullText).gone();
			aq.id(android.R.id.list).visible();
		}

		if(adapter == null)
		{
			SimpleAdapter sadapter = new SimpleAdapter(this, datalist, listItemResId, from, ids);
			adapter = new BaseAdapterWrapper(sadapter, this);
			listview.setAdapter(adapter);
		}
		else
		{
			adapter.notifyDataSetChanged();
		}
		isReflesh = false;
	}

	/**
	 * 给datalist赋值位置为0的数据（如果有需要）
	 */
	public void initFistList(){

	}

	/**
	 * 重新请求
	 */
	public void doSearch(){
		request(1098);
	}

    public void doSearch2(int parm)
    {
        if(isRequest)
        {
            return;
        }
        isReflesh = true;
        currentPage=1;
        map.put("current_page", currentPage+"");
		map.put("status", Integer.toString(parm));
        //	map.put("rows", pageSize);

		//String strurl = UrlManager.appRemoteUrl+url;
        String strurl = "http://vms.cispdr.com:8080/Services/GetOrderList.aspx?current_page=1&status="+parm+"&getType=3";
        doRequest3(1098, strurl, map, 1 );
    }

	/**
	 * 如果requestId=1098，表示第一次请求，如果为1099表示第二次情i去
	 * @param requestId
	 */
	public void request(int requestId)
	{
		if(isRequest)
		{
			return;
		}
		isReflesh = true;
		if(requestId==1098) currentPage=1;
		map.put("current_page", currentPage+"");
	//	map.put("rows", pageSize);

		if(requestId==1001)
		{
			//POST方式传递参数 获得json
			doRequest3(1098, url, map, 2 );
		}
		else if(requestId==1099)
		{
			//POST方式传递参数 获得json
			doRequest2(1099, url, map );
		}
		else
		{
			//GET方式获得json
			doRequest2(1098, url, map );
		}
	}

	public void doRequest2( int requestId,  String url,  Map dataMap)
	{
		new ReqThread(requestId, url, dataMap).start();
	}

	//GET方式获取json
    public void doRequest3( int requestId,  String url,  Map dataMap, int parm)
    {
        new ReqThread3(requestId, url, dataMap,parm).start();
    }

	class ReqThread extends Thread
	{
		private int requestId;
		private String url;
		private Map dataMap;
		public ReqThread(int requestId,  String url, Map dataMap){
			this.requestId = requestId;
			this.url = url;
			this.dataMap = dataMap;
		}
		@Override
		public void run()
		{
			int length = 0;
			String strurl;

			strurl = url;
			String resultStr = HttpClientUtil.HttpUrlConnectionPost6(strurl, dataMap,"UTF-8",107);
			//String resultStr = HttpClientUtil.HttpUrlConnectionGet2(strurl, dataMap,"UTF-8");
			length = resultStr.length();
			if( length==0 )
			{
				baseHandler.obtainMessage(2, requestId, 0, null).sendToTarget();
				return;
			}

			//String jsonStr = "{\"data\":" + resultStr + "}";

			try
			{
				JSONObject jsonObj = new JSONObject(resultStr);
				//JSONObject jsonObj = new JSONObject(jsonStr);
				//JSONObject jsonObj = new JSONObject(responseMsg);
				ResultObject obj = DefaultDataParser.getInstance().parseData3(jsonObj);
				try
				{
					if (obj.isSuccess())
					{
						baseHandler.obtainMessage(0, requestId, 0, obj)
								.sendToTarget();
					}
					else
					{
						baseHandler.obtainMessage(1, requestId, 0, obj)
								.sendToTarget();
					}

				}
				catch(Exception e)
				{
					baseHandler.obtainMessage(1, requestId, 0, obj)
							.sendToTarget();
				}
			}
			catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

    class ReqThread3 extends Thread
    {
        private int requestId;
		private int type;
        private String url;
        private Map dataMap;

        public ReqThread3(int requestId,  String url, Map dataMap, int parm)
        {
            this.requestId = requestId;
            this.url = url;
            this.dataMap = dataMap;
			this.type = requestId;
        }

        @Override
        public void run()
        {
            int length = 0;
			String resultStr = "";

			resultStr = HttpClientUtil.HttpUrlConnectionGet(url,"UTF-8");
			//resultStr = HttpClientUtil.HttpUrlConnectionPost5(url,dataMap,"UTF-8",101);
            length = resultStr.length();
            if( length==0 )
            {
                baseHandler.obtainMessage(2, requestId, 0, null).sendToTarget();
                return;
            }

            String jsonStr = "{\"data\":" + resultStr + "}";

            try
            {
                JSONObject jsonObj = new JSONObject(resultStr);
                //JSONObject jsonObj = new JSONObject(jsonStr);
                //JSONObject jsonObj = new JSONObject(responseMsg);
                ResultObject obj = DefaultDataParser.getInstance().parseData3(jsonObj);
                try
                {
                    if (obj.isSuccess())
                    {
                        baseHandler.obtainMessage(0, requestId, 0, obj).sendToTarget();
                    }
                    else
                    {
                        baseHandler.obtainMessage(1, requestId, 0, obj).sendToTarget();
                    }

                }
                catch(Exception e)
                {
                    baseHandler.obtainMessage(1, requestId, 0, obj).sendToTarget();
                }
            }
            catch (JSONException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (Exception e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

	private Handler baseHandler = new Handler()
	{
		@Override
		public void handleMessage(android.os.Message msg)
		{
			requestAfterDialog();
			if (msg.what == 0)
			{
				onExecuteSuccess2(msg.arg1, (ResultObject)msg.obj);
			}
			else if (msg.what == 1)
			{
				//onExecuteFail2(msg.arg1, (com.cjwsjy.app.vehicle.ResultObject) msg.obj);
				datalist.clear();
			}
			else if (msg.what == 2)
			{
				Toast.makeText(getApplicationContext(), "暂无数据", Toast.LENGTH_SHORT).show();
			}
		};
	};
}
