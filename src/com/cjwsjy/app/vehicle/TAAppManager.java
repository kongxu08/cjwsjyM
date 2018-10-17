package com.cjwsjy.app.vehicle;

import java.lang.ref.SoftReference;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;

import com.androidquery.callback.BitmapAjaxCallback;
import com.androidquery.util.AQUtility;
import com.do1.cjmobileoa.db.model.UserVO;

/**
 * activity 堆栈管理类
 *
 * @Copyright: Copyright (c)2013
 * @Company: 广州市道一信息有限公司
 * @author: Mr.y
 * @version: 1.0
 * @date: 2013-10-30 下午5:30:22
 * <p/>
 * Modification History:
 * Date         Author      Version     Description
 * ------------------------------------------------------------------
 * 2013-10-30      Mr.y          1.0       1.0 Version
 */
public class TAAppManager {
    //	private static Stack<Activity> activityStack;
    private static TAAppManager instance;
    private static Activity lastActivity;
    private static Map<String, SoftReference<Activity>> activityMap = new ConcurrentHashMap<String, SoftReference<Activity>>(50);

    private TAAppManager() {

    }

    /**
     * 单一实例
     */
    public static TAAppManager getAppManager() {
        if (instance == null) {
            instance = new TAAppManager();
        }
        return instance;
    }

    /**
     * 添加Activity到堆栈
     */
    public void addActivity(Activity activity) {
//		if (activityStack == null){
//			activityStack = new Stack<Activity>();
//		}
//		activityStack.add(activity);
        lastActivity = activity;
//        if (!activityMap.containsKey(activity.getClass().getName())) {
        SoftReference softReference = new SoftReference(activity);
        activityMap.put(activity.getClass().getName(), softReference);
//        }
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
//	public Activity currentActivity(){
//		Activity activity = activityStack.lastElement();
//		return activity;
//	}
    public Activity getActivity(String clzName) {
        SoftReference<Activity> softReference = activityMap.get(clzName);
        return softReference == null ? null : softReference.get();
    }

    public <T> T getActivity(Class<T> clz) {
        SoftReference<Activity> softReference = activityMap.get(clz.getName());
        return softReference == null ? null : (T) softReference.get();
    }

    public Activity getLastActivity() {
        return lastActivity;
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public void finishActivity() {
//		Activity activity = activityStack.lastElement();
        finishActivity(lastActivity);
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            activityMap.remove(activity.getClass().getName());
            activity.finish();
            activity = null;
        }
    }

    /**
     * 移除指定的Activity
     */
    public void removeActivity(Activity activity) {
        if (activity != null) {
            activityMap.remove(activity.getClass().getName());
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        for (SoftReference<Activity> softReference : activityMap.values()) {
            if (softReference.get().getClass().equals(cls)) {
                finishActivity(softReference.get());
            }
        }
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        for (SoftReference<Activity> softReference : activityMap.values()) {
            finishActivity(softReference.get());
        }
        activityMap.clear();
        lastActivity = null;
    }

    /**
     * 退出应用程序
     *
     * @param context      上下文
     * @param isBackground 是否开开启后台运行
     */
    public void AppExit(Context context, Boolean isBackground, boolean isLoginOut) {
        try {
            if (isLoginOut) {//如果是正常的登录退出，就清除掉数据，如果是异常退出，就不清除登录信息
                //清除登录信息
                BaseActivity.uservo = new UserVO();
//				Constants.sharedProxy.putBoolean(ShareType.isLogin, false);
//				Constants.sharedProxy.putString(ShareType.mobile, "");
//				Constants.sharedProxy.putString(ShareType.userId, "");
//				Constants.sharedProxy.putString(ShareType.personName, "");
                Constants.sharedProxy.remove(ShareType.isLogin);//是否登录
                Constants.sharedProxy.remove(ShareType.mobile);//缓存手机号码
                Constants.sharedProxy.remove(ShareType.userId);//缓存用户Id
                Constants.sharedProxy.remove(ShareType.personName);//缓存用户名称
                Constants.sharedProxy.remove(ShareType.key);//删除key
				Constants.sharedProxy.remove(ShareType.deviceToken);//删除token
                Constants.sharedProxy.commit();
                //清除联系人数据库(每次登录都从服务器获取最新的联系人数据)----不再删除联系人数据库
//                Constants.dbManager.deletePerson(null, null);
//                Constants.dbManager.delLogo();//删掉联系人头像

//                ServiceManager service = new ServiceManager(context);
//                service.stopService();

                long triggerSize = 2000000;
                long targetSize = 1000000;
                AQUtility.cleanCacheAsync(context, triggerSize, targetSize);
                BitmapAjaxCallback.clearCache();
//                Constants.dbManager.delLogo();
            }
//            Constants.sessionManager.disconnected();//关闭session
//            Constants.dbManager.closeDB();//关闭数据库连接
            finishAllActivity();
            ActivityManager activityMgr = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            activityMgr.restartPackage(context.getPackageName());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 注意，如果有后台程序运行，就不要杀死进程
            if (!isBackground) {
                System.exit(0);
            }
        }
    }

    /**
     * 判断当前应用程序处于前台还是后台
     * 该方法需要：android.permission.GET_TASKS权限
     *
     * @param context
     * @return
     */
    public boolean isApplicationBroughtToBackground(final Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断当前应用是处于前台还是处于后台运行
     * 该方法不需要权限
     *
     * @param context
     * @return
     */
    public boolean isBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
                    //Log.i(String.format("Background App:", appProcess.processName));
                    return true;
                } else {
                    //Log.i(String.format("Foreground App:", appProcess.processName));
                    return false;
                }
            }
        }
        return false;
    }
}