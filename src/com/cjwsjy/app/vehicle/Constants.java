package com.cjwsjy.app.vehicle;

import java.io.File;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.UUID;

import android.app.ActivityManager;
import android.app.Application;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.Vibrator;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.Display;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import cn.com.do1.dqdp.android.SharedPreferencesProxy;

import com.androidquery.callback.BitmapAjaxCallback;
import com.androidquery.util.AQUtility;
import com.do1.cjmobileoa.db.DBManager;
import com.do1.cjmobileoa.db.model.UserVO;

/**
 * application
 *
 * @Copyright: Copyright (c)2013
 * @Company: 广州市道一信息有限公司
 * @author: Mr.y
 * @version: 1.0
 * @date: 2013-10-15 下午3:29:09
 * <p/>
 * Modification History:
 * Date         Author      Version     Description
 * ------------------------------------------------------------------
 * 2013-10-15      Mr.y          1.0       1.0 Version
 */
public class Constants extends Application {

    public static final String SHARED_NAME = "db_cjmobileoa";
    public static  String version = "1.0" ;
    private MyUncaughtExceptionHandler mDefaultHandler;
    public static String SERVER_URL;
    public static String PHOTO_URL;
    public static SharedPreferencesProxy sharedProxy;
    public static int SCR_WIDTH = 0;//屏幕高宽
    public static int SCR_HEIGHT = 0;//屏幕高宽
    public static String deviceId; // 设备号
    public static DBManager dbManager;
    public static Vibrator vibrator;
    public static PowerManager.WakeLock wakeLock = null;
    public static TAAppManager mAppManager; 
    public static String appType;
    
    public static String sp_loginName = "USERDATA.LOGIN.NAME";
    public static String sp_password = "USERDATA.LOGIN.PASSWORD";
    public static String sp_userName = "USERDATA.USER.NAME"; 
    
    public static String http_result_key = "HTTP.RESULT.VLUEKEY";
    public static String http_cookie_key = "HTTP.COOKIE";
    public static String http_cookie_count = "HTTP.COOKIE.COUNT";
    public static String upload_file_path =  Environment.getExternalStorageDirectory().getAbsolutePath()+"/changjiang/download";
    
    
    public static String deptParentid = "A90AEAEC-E3D4-43DE-BB67-85407B57B171";
    
    //百度地图
    //本地debug.store密钥
//    public static final String strKey = "0ExtLLmto7e3dEbOyvzRaAHg";
    //do1store的密钥
    public static final String strKey = "gMzoArcYaZDrdtwyRcwy5Oik";
    
    public static int locateFlag = 0;//0表示发送地址定位；1：表示附近定位;2:表示后台上传位置定位；3：
    public TextView mTv; 
	private String mData;  
//	public MyLocationListenner myListener = new MyLocationListenner();
	public static double latitude = 0;//纬度
	public static double lontitude = 0;//经度
	
    @Override
    public void onCreate() {

        super.onCreate();
        mDefaultHandler = new MyUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(mDefaultHandler);
//        SERVER_URL = getServerUrl();
        PHOTO_URL=SERVER_URL;
        sharedProxy = SharedPreferencesProxy.getInstance(this, SHARED_NAME);
        WindowManager windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        Display dispaly = windowManager.getDefaultDisplay();
        SCR_WIDTH = dispaly.getWidth();
        SCR_HEIGHT = dispaly.getHeight();
        if(SCR_WIDTH >= 1080){
        	StaticValueUtil.FACE_SIZE = 70;
        	StaticValueUtil.IMP_WIDTH = 255;
        	StaticValueUtil.IMP_WIDTH_FOR_DIALOG = 285;
        	StaticValueUtil.INIT_VOICE_MIN_WIDTH = 0;
        	StaticValueUtil.INIT_VOICE_A_SECOND_WIDTH = 25;
        	StaticValueUtil.INIT_VOICE_MAX_WIDTH = 500;
        	StaticValueUtil.IMAGE_ARG1 = 210;
        	StaticValueUtil.IMAGE_ARG2 = 280;
        }else if(SCR_WIDTH >= 800){
        	StaticValueUtil.FACE_SIZE = 60;
        	StaticValueUtil.IMP_WIDTH = 255;
        	StaticValueUtil.IMP_WIDTH_FOR_DIALOG = 285;
        	StaticValueUtil.INIT_VOICE_MIN_WIDTH = 0;
        	StaticValueUtil.INIT_VOICE_A_SECOND_WIDTH = 25;
        	StaticValueUtil.INIT_VOICE_MAX_WIDTH = 500;
        	StaticValueUtil.IMAGE_ARG1 = 210;
        	StaticValueUtil.IMAGE_ARG2 = 280;
        }else{
        	StaticValueUtil.FACE_SIZE = 50;
        	StaticValueUtil.IMP_WIDTH = 180;
        	StaticValueUtil.IMP_WIDTH_FOR_DIALOG = 200;
        	StaticValueUtil.INIT_VOICE_MIN_WIDTH = 0;
        	StaticValueUtil.INIT_VOICE_A_SECOND_WIDTH = 20;
        	StaticValueUtil.INIT_VOICE_MAX_WIDTH = 400;
        	StaticValueUtil.IMAGE_ARG1 = 150;
        	StaticValueUtil.IMAGE_ARG2 = 200;
        }

        TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        deviceId = telephonyManager.getDeviceId();
       // initEngineManager(this);
        
        getAppManager();
        sharedProxy.commit();
        if(Constants.mAppManager.isApplicationBroughtToBackground(this)){
        	initApp();
        }
    }

    public void initApp() {
        if (dbManager == null || !dbManager.isConnected()) {
            dbManager = new DBManager(this);
        }
        getAppManager();
        sharedProxy.commit();
    }

   public void initConnect(){
//       initEngineManager(this);
       dbManager = new DBManager(this);
//		deleteDatabase("minaim.db");

       vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);//震动
   }
   

    public static void acquireWakeLock(Context ctx) {
        if (null == wakeLock) {
            PowerManager pm = (PowerManager) ctx.getSystemService(Context.POWER_SERVICE);
//            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "Do1EimConnectionService");
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK , "Do1EimConnectionService");
            if (null !=  wakeLock) {
                wakeLock.acquire();
            }
        }
    }

    public static void releaseWakeLock(){
        if(wakeLock!=null){
            wakeLock.release();
            wakeLock=null;
        }
    }

    /**
     * 得到activity管理类
     *
     * @return
     */
    public static TAAppManager getAppManager() {
        if (mAppManager == null) {
            mAppManager = TAAppManager.getAppManager();
        }
        return mAppManager;
    }

    public Context getContext() {
        return this.getContext();
    }

//    public String getServerUrl() {
//        switch (serverLocation) {
//            case 0: {
//                switch (Integer.parseInt(appType)) {
//                    case 0:
//                        return "http://115.29.163.136:8080/im/";
//                    case 1:
//                    case 2:
//                    case 3:
//                        return "http://115.29.179.39:8080/im/";
//                }
//            }
//            case 1: {
//                return "http://113.240.224.196:8080/im/";
//            }
//        }
//        Log.e("minaim", "错误，没有指定正确的服务器地址，将会导至上传文件等操作失败");
//        return "";
////        String server = getString(R.string.server);
////        if ("2".equals(server)) {
////            return getString(R.string.server_url2);
////        } else if ("3".equals(server)) {
////            return getString(R.string.server_url3);
////        } else {
////            return getString(R.string.server_url);
////        }
//    }



    /**
     * 退出应用程序
     *
     * @param isBackground 是否开开启后台运行,如果为true则为后台运行
     */
    public void exitApp(Boolean isBackground, boolean isLoginOut) {
		if(isLoginOut){//如果是正常的登录退出，就清除掉数据，如果是异常退出，就不清除登录信息
			//清除登录信息
			BaseActivity.uservo = new UserVO();
			Constants.sharedProxy.putBoolean(ShareType.isLogin, false);
			Constants.sharedProxy.putString(ShareType.mobile, "");
			Constants.sharedProxy.commit();
			//清除联系人数据库(每次登录都从服务器获取最新的联系人数据)
//			dbManager.deletePerson(null, null);
		}
//		sessionManager.disconnected();//关闭session
		dbManager.closeDB();//关闭数据库连接
        releaseWakeLock();
        mAppManager.AppExit(this, isBackground, isLoginOut);
    }

    /**
     * 统一捕捉异常处理
     * User:YanFangqin
     * Date:2013-5-2
     * ProjectName:thzhd
     */
    private class MyUncaughtExceptionHandler implements
            UncaughtExceptionHandler {
        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            ex.printStackTrace();
            Log.e("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
            Log.e("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
            Log.e("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
            Log.e("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
            if (!handleException(ex) && mDefaultHandler != null) {
                Log.e("????????????????????????????????????");
                Log.e("????????????????????????????????????");
                Log.e("????????????????????????????????????");
                Log.e("????????????????????????????????????");
                // 如果用户没有处理则让系统默认的异常处理器来处理
                mDefaultHandler.uncaughtException(thread, ex);
            } else {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Log.d("捕获全局异常信息:" + e.getMessage());
                }
                // 杀死线程，退出应用。
//				Intent intent = new Intent(Constants.this,DownLoadService.class);
//				Constants.this.stopService(intent);
                sendReport(ex);
                exitApp(false, false);

                Log.e("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                Log.e("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                Log.e("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                Log.e("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                Log.e("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                int version = android.os.Build.VERSION.SDK_INT;
                ActivityManager activityMgr = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                if (version <= 7) {
                    try {
                        activityMgr.restartPackage(getPackageName());
                    } catch (Exception e) {
                    }
                } else {
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
                System.exit(0);
            }
        }

        private void sendReport(final Throwable ex) {
            try {
                File temp = File.createTempFile(UUID.randomUUID().toString(), "temp");
                PrintWriter writer = new PrintWriter(temp);
//                TelephonyManager phoneMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                writer.write(String.format("错误发生时间%s：;\r\n名称：%s;机型：%s;版本号：%s;核心版本号：%s；厂商：%s;产品：%s;im版本：%s\r\n",
                        cn.com.do1.common.util.DateUtil.formartCurrentDateTime(),
                        Build.DEVICE,
                        Build.MODEL,
                        Build.VERSION.SDK,
                        Build.VERSION.RELEASE,
                        Build.MANUFACTURER,
                        Build.PRODUCT,
                        version));
                ex.printStackTrace(writer);
                writer.flush();
                writer.close();
                Log.e(temp.getPath());
                sharedProxy.putString("errorReport", temp.getPath());
                sharedProxy.commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * @param
         * @param ex
         * @return true:如果处理了该异常信息;否则返回false.
         */
        private boolean handleException(final Throwable ex) {
            if (ex == null) {
                return false;
            }
            // 使用Toast来显示异常信息
            new Thread() {
                @Override
                public void run() {
                    Looper.prepare();
                    Toast.makeText(Constants.this, "sorry，程序出现异常，即将退出应用！",Toast.LENGTH_LONG).show();
                    Looper.loop();
                }
            }.start();
            return true;
        }
    }

    Handler uploadHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {

            return true;
        }
    });

    /**
     * aquery的内存控制
     */
    @Override
    public void onLowMemory() {
        long triggerSize = 2000000;
        long targetSize = 1000000;
        AQUtility.cleanCacheAsync(this, triggerSize, targetSize);
        BitmapAjaxCallback.clearCache();
    }
    
    
	
	// 常用事件监听，用来处理通常的网络错误，授权验证错误等
//    public static class MyGeneralListener implements MKGeneralListener {
//        
//        @Override
//        public void onGetNetworkState(int iError) {
//            if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
////                Toast.makeText(Constants.getInstance().getApplicationContext(), "您的网络出错啦！",
////                    Toast.LENGTH_LONG).show();
//                Log.e("您的网络出错啦！");
//            }
//            else if (iError == MKEvent.ERROR_NETWORK_DATA) {
////                Toast.makeText(Constants.getInstance().getApplicationContext(), "输入正确的检索条件！",
////                        Toast.LENGTH_LONG).show();
//                Log.e("输入正确的检索条件！");
//            }
//            // ...
//        }

//        @Override
//        public void onGetPermissionState(int iError) {
//        	//非零值表示key验证未通过
//            if (iError != 0) {
//                //授权Key错误：
////                Toast.makeText(Constants.getInstance().getApplicationContext(), 
////                        "请在 DemoApplication.java文件输入正确的授权Key,并检查您的网络连接是否正常！error: "+iError, Toast.LENGTH_LONG).show();
//                Log.e("请在 DemoApplication.java文件输入正确的授权Key,并检查您的网络连接是否正常！error: "+iError);
//            }else{
////            	Toast.makeText(Constants.getInstance().getApplicationContext(), 
////                        "key认证成功", Toast.LENGTH_LONG).show();
//            	Log.e("key认证成功！");
//            }
//        }
//    }
    
    /**
	 * 显示字符串
	 * @param str
	 */
	public void logMsg(String str) {
		try {
			mData = str;
			if ( mTv != null )
				mTv.setText(mData);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 监听函数，又新位置的时候，格式化成字符串，输出到屏幕中
	 */
//	public class MyLocationListenner implements BDLocationListener {
//		@Override
//		public void onReceiveLocation(BDLocation location) {
//			if (location == null)
//				return ;
//			StringBuffer sb = new StringBuffer(256);
//			sb.append("time : ");
//			sb.append(location.getTime());
//			sb.append("\nerror code : ");
//			sb.append(location.getLocType());
//			sb.append("\nlatitude : ");
//			sb.append(location.getLatitude());
//			sb.append("\nlontitude : ");
//			sb.append(location.getLongitude());
//			if(locateFlag == 1 || locateFlag == 2){
//				latitude = location.getLatitude();
//				lontitude = location.getLongitude();
//			}
//			sb.append("\nradius : ");
//			sb.append(location.getRadius());
//			if (location.getLocType() == BDLocation.TypeGpsLocation){
//				sb.append("\nspeed : ");
//				sb.append(location.getSpeed());
//				sb.append("\nsatellite : ");
//				sb.append(location.getSatelliteNumber());
//			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation){
////				sb.append("\n省：");
////				sb.append(location.getProvince());
////				sb.append("\n市：");
////				sb.append(location.getCity());
////				sb.append("\n区/县：");
////				sb.append(location.getDistrict());
//				sb.append("\naddr : ");
//				sb.append(location.getAddrStr());
//			}
//			sb.append("\nsdk version : ");
//			sb.append(mLocationClient.getVersion());
//			sb.append("\nisCellChangeFlag : ");
//			sb.append(location.isCellChangeFlag());
////			logMsg(sb.toString());
//			Log.e(sb.toString());
//			
//			if(locateFlag == 2 && lontitude > 0 && latitude > 0){//上传定位信息
//				Map<String, Object> map = new HashMap<String, Object>();
//				map.put("lng", lontitude);
//				map.put("lat", latitude);
//				sessionManager.send(ReceiviType.PING, BaseActivity.getCmdId(), "com.do1.minaim.activity.common.PingThread", map);
//			}else{
//				String mLainfo = location.getAddrStr() + "," + location.getLongitude() + "," + location.getLatitude() + ",";
//				Intent push = new Intent();
//			    push.setAction(BroadcastType.ChatMapMsg); 
//			    push.putExtra("mLainfo", mLainfo);
//			    push.putExtra("longitude", location.getLongitude());
//			    push.putExtra("latitude", location.getLatitude());
//			    Constants.this.sendBroadcast(push);
//			}
//			
//			if(getGpsEnabled()){//如果打开了GPS，就关闭
//				toggleGPS();
//			}
//		}
//		
//		public void onReceivePoi(BDLocation poiLocation) {
//			if (poiLocation == null){
//				return ; 
//			}
//			StringBuffer sb = new StringBuffer(256);
//			sb.append("Poi time : ");
//			sb.append(poiLocation.getTime());
//			sb.append("\nerror code : "); 
//			sb.append(poiLocation.getLocType());
//			sb.append("\nlatitude : ");
//			sb.append(poiLocation.getLatitude());
//			sb.append("\nlontitude : ");
//			sb.append(poiLocation.getLongitude());
//			sb.append("\nradius : ");
//			sb.append(poiLocation.getRadius());
//			if (poiLocation.getLocType() == BDLocation.TypeNetWorkLocation){
//				sb.append("\naddr : ");
//				sb.append(poiLocation.getAddrStr());
//			} 
//			if(poiLocation.hasPoi()){
//				sb.append("\nPoi:");
//				sb.append(poiLocation.getPoi());
//			}else{				
//				sb.append("noPoi information");
//			}
//			logMsg(sb.toString());
//		}
//	}
	
	private void toggleGPS() {
		Intent gpsIntent = new Intent();
		gpsIntent.setClassName("com.android.settings",
				"com.android.settings.widget.SettingsAppWidgetProvider");
		gpsIntent.addCategory("android.intent.category.ALTERNATIVE");
		gpsIntent.setData(Uri.parse("custom:3"));
		try {
			PendingIntent.getBroadcast(this, 0, gpsIntent, 0).send();
		} catch (CanceledException e) {
			e.printStackTrace();
		}
	}
	
	private boolean getGpsEnabled(){
		return Settings.Secure.isLocationProviderEnabled(getContentResolver(), LocationManager.GPS_PROVIDER);
	}
    
}
