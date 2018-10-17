package com.cjwsjy.app.vehicle;

import android.content.Context;
import android.widget.Toast;

/**
 * 提示信息包装类
 * @Copyright:   Copyright (c)2013  
 * @Company:     广州市道一信息有限公司
 * @author:      Mr.y  
 * @version:     1.0  
 * @date:        2013-10-15 下午3:26:09  
 *  
 * Modification History:  
 * Date         Author      Version     Description  
 * ------------------------------------------------------------------  
 * 2013-10-15      Mr.y          1.0       1.0 Version
 */
public class ToastUtil {
	public static void showLongMsg(Context context, String msg){
		showMsg(context, msg, Toast.LENGTH_LONG);
	}
	
	public static void showMsg(Context context, String msg, int duration){
		Toast.makeText(context, msg, duration).show();
	}
	
	public static void showShortMsg(Context context, String msg){
		showMsg(context, msg, Toast.LENGTH_SHORT);
	}
}
