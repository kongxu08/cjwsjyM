package com.cjwsjy.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;
import cn.com.do1.dqdp.android.SharedPreferencesProxy;

import com.cjwsjy.app.dial.GetPinYin4j;
import com.cjwsjy.app.dial.SearchFilterUtil;
import com.cjwsjy.app.utils.StringHelper;
import com.cjwsjy.app.utils.UrlUtil;
import com.do1.cjmobileoa.db.DBManager;
import com.do1.cjmobileoa.db.model.DepartmentEmployeeVO;
import com.do1.cjmobileoa.db.model.DepartmentVO;
import com.do1.cjmobileoa.db.model.EmployeeVO;
import com.do1.cjmobileoa.db.model.SUserVO;
/*import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;*/

/**
 * 
 * 全局变量类，，
 */
//public class SmApplication extends BaseApplication {
public class SmApplication extends Application 
{
	private static String m_str_deviceToken;

	private String version_sdk;
	
	public static int mark = 1;
	protected static int home = 0;

	//public static String server_ip = "http://10.6.189.50:80";
	public static String vehicle_ip = "http://vms.cispdr.com:8080/";
	public static  DBManager dbManager;
	public  final String SHARED_NAME = "db_cjmobileoa";
	public static  SharedPreferencesProxy sharedProxy;
	public  static SharedPreferences sp;
	public  static String key[] = { "1", "2", "3", "4","5", "6","7", "8", "9","*","0","#" };
	public  static Map<String, List<SUserVO>> filterMaps = new HashMap<String, List<SUserVO>>();

	public static void init()
	{
		filterMaps = new HashMap<String, List<SUserVO>>();
		for (int i = 0; i < key.length; i++) 
		{
			for (int j = 0; j < key.length; j++) 
			{
				String ss=key[i]+key[j];
				filterMaps.put(ss, new ArrayList<SUserVO>());
			}
		}
	}

	@Override
	public void onCreate() 
	{
		super.onCreate();
		
		//极光推送
        //JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
        //JPushInterface.init(this);     		// 初始化 JPush

		//友盟推送
		/*PushAgent mPushAgent = PushAgent.getInstance(this);
		//注册推送服务，每次调用register方法都会回调该接口
		mPushAgent.register(new IUmengRegisterCallback()
		{
			@Override
			public void onSuccess(String deviceToken) {
				//注册成功会返回device token
				m_str_deviceToken = deviceToken;
				android.util.Log.i("cjwsjy", "--------deviceToken1="+deviceToken+"-------");
				android.util.Log.i("cjwsjy", "--------deviceToken2="+m_str_deviceToken+"-------");
			}

			@Override
			public void onFailure(String s, String s1) {

			}
		});*/
        
		init();
		if (dbManager == null || !dbManager.isConnected()) {
			dbManager = new DBManager(this);
		}
		
        sharedProxy = SharedPreferencesProxy.getInstance(this, SHARED_NAME);
		sp = getSharedPreferences("userdata", 0);
		PackageManager manager = this.getPackageManager();
		PackageInfo info;
		try 
		{
			info = manager.getPackageInfo(this.getPackageName(), 0);
			String version = info.versionName;
			sharedProxy.putString("curVersion", version);

//			Editor editor = sp.edit();
//			editor.putString("curVersion", version);
//			editor.commit();
		}
		catch (NameNotFoundException e) 
		{
			e.printStackTrace();
		}
		sharedProxy.commit();
		getCached();
		
		this.registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks()
		{
			
			@Override
			public void onActivityStopped(Activity activity)
			{
				// TODO 自动生成的方法存根
				
			}
			
			@Override
			public void onActivityStarted(Activity activity)
			{
				// TODO 自动生成的方法存根
				//android.util.Log.d("cjwsjy", "------onActivityStarted-------Application");
				//android.util.Log.d("cjwsjy", "------mark++-------Application");
				//mark++;
			}
			
			@Override
			public void onActivitySaveInstanceState(Activity activity, Bundle outState)
			{
				// TODO 自动生成的方法存根
			}
			
			@Override
			public void onActivityResumed(Activity activity)
			{
				// TODO 自动生成的方法存根
				//android.util.Log.d("cjwsjy", "------onActivityResumed-------Application");
			}
			
			@Override
			public void onActivityPaused(Activity activity)
			{
				// TODO 自动生成的方法存根
				//android.util.Log.d("cjwsjy", "------onActivityPaused-------Application");
				String name = activity.getLocalClassName();

				if (activity instanceof ActivityLogin)
				{
					mark = 1;
				}
				else if( activity instanceof AppStart )
				{
					mark = 1;
				}
				else mark = 0;
			}
			
			@Override
			public void onActivityDestroyed(Activity activity)
			{
				// TODO 自动生成的方法存根
				
			}
			
			@Override
			public void onActivityCreated(Activity activity, Bundle savedInstanceState)
			{
				// TODO 自动生成的方法存根
				
			}
		  });
	}

	
	public static void initEmployee() 
	{
		init();
		long start = System.currentTimeMillis();
		Log.d("zhq", "进来初始化initEmployee");
		// TODO Auto-generated method stub
		List<EmployeeVO> allEmployee = dbManager.findAllEmployee();
		
		int size = allEmployee.size();
		int i = 0;
		int j = 1;
		int con = 0;
		int nbuf = 0;
		boolean result = false;
		boolean result2 = false;
		String px = "";
		String userid = "";
		String orgid = "";
		String fieldstr;
		String fieldstr2;
		String DeptParentid = "";
		
		//排序
		Collections.sort(allEmployee);
        
		con = size/90;
		android.util.Log.d("cjwsjy", "--------size="+size+"-------initEmployee");
		// 根据 不同电话号码分别创建多个VO
		for( EmployeeVO emVo : allEmployee ) 
		{
			if( i==con*j ) 
			{
				UrlUtil.g_count++;
				j++;
			}

			SUserVO sVO = new SUserVO();
			sVO.clear();
			
			fieldstr = emVo.getUserid();
			result = StringHelper.isEmpty(fieldstr);
			if(result==false) sVO.setUserId(fieldstr);
			
			fieldstr = emVo.getUserDisplayName();
			result = StringHelper.isEmpty(fieldstr);
			if(result==false) sVO.setName(fieldstr);
			
			fieldstr = emVo.getUsername();
			result = StringHelper.isEmpty(fieldstr);
			if(result==false) sVO.setPinyin(fieldstr);

			//获得二级单位名称
			//获得岗位
			userid = emVo.getUserid();
			DepartmentEmployeeVO orgsVO = dbManager.findOrgsbyUserid(userid);

			//android.util.Log.d("cjwsjy", "--------userid="+userid+"-------SmApplication");

			//部门
			/*orgid = orgsVO.getorgRID();
			if( orgid!=null )
			{
				if( orgid.length()!=0 )
				{
					DepartmentVO deptVO = dbManager.findDepartmentbyid(orgid);

					//android.util.Log.d("cjwsjy", "----------DeptParentid="+deptVO.getDeptParentid()+"-------SmApplication");

					DeptParentid = deptVO.getDeptParentid();
					if( DeptParentid!=null && DeptParentid.length()!=0 )
					{
						//机构
						deptVO = dbManager.findOrganizationbyid(DeptParentid);
						fieldstr = deptVO.getDeptDisplayname();

						result = StringHelper.isEmpty(fieldstr);
						if (result == false) sVO.setOrganization(fieldstr);
						else sVO.setOrganization("");
					}
					else sVO.setOrganization("");
				}
				else sVO.setOrganization("");
			}
			else sVO.setOrganization("");*/

			sVO.setOrganization("");

			fieldstr = emVo.getMobileIphone();
			fieldstr2 = emVo.getPhoneNumber();

			//优先显示移动办公
			result = StringHelper.isEmpty(fieldstr);
			result2 = StringHelper.isEmpty(fieldstr2);
			if( result==false ) 
			{
				sVO.setMobile(fieldstr);
			}
			else if( result2==false )
			{
				sVO.setMobile(fieldstr2);
			}
			
			sVO.setMobile_type("手机");



			//根据名字得到拼音
			fieldstr = emVo.getUserDisplayName();
			px = GetPinYin4j.makeStringByStringSet( GetPinYin4j.getPinyin(fieldstr) );
			//fieldstr = GetPinYin4j.getPinyinHeadNum(px);
			//fieldstr = GetPinYin4j.getPinyinNum(px);
			sVO.setNameNumber(GetPinYin4j.getPinyinHeadNum(px));
			sVO.setPinyinNumber(GetPinYin4j.getPinyinNum(px));
			
			bulidIndex(sVO);

			i++;
		}
		allEmployee.clear();
		allEmployee=null;
		
		long end = System.currentTimeMillis();
		Log.d("zhq from db", String.valueOf(end - start));
		
		//缓存起来
		Editor editor = sp.edit();
		editor.putString("filterMaps",StringHelper.objectToString(filterMaps));
		editor.commit();
		UrlUtil.g_count = 100;
	}
	
	private static  void bulidIndex(SUserVO sVO)
	{
		// 建index，根据首数字分类
		if(filterMaps==null||filterMaps.isEmpty())
		{
			init();
		}
		for (int i = 0; i < key.length; i++) 
		{
			for (int j = 0; j < key.length; j++) 
			{
				String ss=key[i]+key[j];
				if (SearchFilterUtil.contains(sVO,ss))
					filterMaps.get(ss).add(sVO);
			}
		}
	}

	private  void getCached() {
		new Thread(){
			@Override
			public void run() {
				long start1 = System.currentTimeMillis();
				String ss = sp.getString("filterMaps", "");

				filterMaps = (Map<String, List<SUserVO>>) StringHelper.StringToObject(ss);
				if ( filterMaps == null || filterMaps.isEmpty())
				{
					initEmployee();
					Editor editor = sp.edit();
					editor.putString("filterMaps", StringHelper.objectToString(filterMaps));
					editor.commit();
				}

				long end1 = System.currentTimeMillis();
				Log.d("zhq from mem", String.valueOf(end1 - start1));
			}}.start();
	}

	public static String GetDeviceToken()
	{
		return m_str_deviceToken;
	}

	public static int gethomekey() 
	{
		return home;
	}
	
	public static void sethomekey(int num, int debug) 
	{
		//home = num;
	}

    public String getsdk()
    {
        return version_sdk;    
    }    
    
    public void setsdk(String s)
    {
        this.version_sdk = s;    
    }    
    
}
