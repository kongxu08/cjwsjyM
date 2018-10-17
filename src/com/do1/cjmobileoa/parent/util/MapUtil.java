package com.do1.cjmobileoa.parent.util;

import java.util.Map;

/**
 * Map帮助类，帮助从map中获取数据，加上判空判断，减少由于此类操作可能造成的空指针异常
 * @Copyright:   Copyright (c)2013  
 * @Company:     广州市道一信息有限公司
 * @author:      Mr.y  
 * @version:     1.0  
 * @date:        2013-10-15 下午3:25:48  
 *  
 * Modification History:  
 * Date         Author      Version     Description  
 * ------------------------------------------------------------------  
 * 2013-10-15      Mr.y          1.0       1.0 Version
 */
public class MapUtil {
	
	/**
	 * 首页indexList中map对象的key
	 */
	public static String MAP_BELONG_USER = "belongUser";
	public static String MAP_CHATID = "chatId";
	public static String MAP_TARGET_ID = "targetId";
	public static String MAP_NAME = "name";
	public static String MAP_ISGROUP = "isGroup";
	public static String MAP_DATE = "date";
	public static String MAP_CONTENT = "content";
	public static String MAP_UNREAD = "unread";
	public static String RINGING = "ringing";
	public static String MAP_MESSAGE_TYPE = "messagetype";
	
	public static String MAP_ISTOP = "isTop";
	public static String MAP_ISCANDEL = "isCanDel";
	public static String MAP_BACKUP1 = "backUp1";
	public static String MAP_BACKUP2 = "backUp2";

	
	/**
	 * 从map中获取数据
	 * @param key
	 * @return
	 */
	public static  Object getValueFromMap(Map<String,Object> dataMap,String key){
		if (dataMap != null){
			return dataMap.get(key);
		}
		return null;
	}
	 
	 /**
	  * 返回map中的float值,默认为""
	  * @param key map中的key
	  * @param defaultValue value为空时的默认值
	  * @return T 返回同defaultValue类型的值
	  */
	 @SuppressWarnings("unchecked")
	public static <T> T getValueFromMap(Map<String,Object> dataMap,String key,T defaultValue){
		 Object value = getValueFromMap(dataMap,key);
		 if (value == null){
			 return defaultValue;
		 }else{
			 return (T)value;
		 }
	 }
	 
}
