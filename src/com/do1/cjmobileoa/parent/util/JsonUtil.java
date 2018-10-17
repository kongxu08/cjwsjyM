package com.do1.cjmobileoa.parent.util;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.ParseException;

/**
 * Json对象与String、bean对象的转换
 * @Copyright:   Copyright (c)2013  
 * @Company:     广州市道一信息有限公司
 * @author:      Mr.y  
 * @version:     1.0  
 * @date:        2013-10-15 下午3:25:26  
 *  
 * Modification History:  
 * Date         Author      Version     Description  
 * ------------------------------------------------------------------  
 */
public class JsonUtil {
	/**
	 * 把json对象解析成相应的bean对象
	 * 
	 * @param <T>
	 * @param jObject
	 *            json对象
	 * @param cls
	 * @return
	 * @throws Exception
	 */
	public static <T> T json2Bean(JSONObject jObject, Class<T> cls) throws Exception
	{
		T t = cls.newInstance();
		Field[] fields = cls.getDeclaredFields();
		for (Field field : fields)
		{
			Class<?> typeClass = field.getType();
			String fieldName = field.getName();
			field.setAccessible(true);
			// 为空
			if (jObject.isNull(fieldName))
			{
				// field.set(fieldName, null);
			}
			else if (typeClass == String.class)
			{
				String value = jObject.optString(fieldName);
				field.set(t, value);
			}
			else if (typeClass == int.class || typeClass == Integer.class)
			{
				int value = jObject.optInt(fieldName);
				field.setInt(t, value);
			}
			else if (typeClass == long.class || typeClass == Long.class)
			{
				long value = jObject.optLong(fieldName);
				field.setLong(t, value);
			}
			else if (typeClass == float.class || typeClass == Float.class)
			{
				Float value = (Float) jObject.opt(fieldName);
				field.setFloat(t, value);
			}
			else if (typeClass == double.class || typeClass == Double.class)
			{
				double value = jObject.optDouble(fieldName);
				field.setDouble(t, value);
			}
			else if (typeClass == java.util.Date.class || typeClass == java.sql.Date.class)
			{
				Object value = jObject.opt(fieldName);
				field.set(t, formatDate(value));
			}
			else if (typeClass == byte.class || typeClass == Byte.class)
			{
				byte value = (byte) jObject.optInt(fieldName);
				field.set(t, value);
			}
			else if (typeClass == List.class)
			{
				// field.set(t, null);
			}
			else
			{
				Object value = jObject.opt(fieldName);
				field.set(t, value);
			}
		}
		return t;
	}

	/**
	 * 把json格式的字符串解析成相应的bean对象
	 * 
	 * @param <T>
	 * @param json
	 *            json格式字符串
	 * @param cls
	 *            bean类
	 * @return
	 * @throws Exception
	 */
	public static <T> T json2Bean(String json, Class<T> cls) throws Exception {
		return json2Bean(new JSONObject(json), cls);
	}

	/**
	 * 把json格式的字符串解析成相应的bean对象
	 * 
	 * @param <T>
	 * @param jsons
	 *            多个json对象的格式字符串
	 * @param cls
	 *            bean类
	 * @return
	 * @throws Exception
	 */
	public static <T> List<T> json2Beans(String jsons, Class<T> cls)
			throws Exception {
		return jsonArray2Beans(new JSONArray(jsons), cls);
	}

	/**
	 * 把JSONArray解析成相应的bean对象
	 * 
	 * @param <T>
	 * @param jArray
	 *            JSONArray对象
	 * @param cls
	 *            bean类
	 * @return bean对象列表
	 * @throws Exception
	 */
	public static <T> List<T> jsonArray2Beans(JSONArray jArray, Class<T> cls)
			throws Exception {
		int arrayLen = jArray.length();
		List<T> jsonList = new ArrayList<T>(arrayLen);
		for (int i = 0; i < arrayLen; i++) {
			JSONObject jObject = jArray.getJSONObject(i);
			T bean = json2Bean(jObject, cls);
			jsonList.add(bean);
		}
		return jsonList;
	}

	/**
	 * 把JSONArray对象格式的字符串解析成相应的bean对象
	 * 
	 * @param <T>
	 * @param json
	 *            JSONArray对象格式字符串
	 * @param cls
	 *            bean类
	 * @return bean对象列表
	 * @throws Exception
	 */
	public static <T> List<T> jsonArray2Beans(String json, Class<T> cls)
			throws Exception {
		return jsonArray2Beans(new JSONArray(json), cls);
	}

	/**
	 * 把JSONArray对象格式的字符串解析成相应的List<Map<String, Object>>对象,给Adapter提供数据库源
	 * 
	 * @param json
	 *            JSONArray对象格式字符串
	 * @return map对象列表,map中含有bean对象的属性和值
	 * @throws Exception
	 */
	public static <T> List<Map<String, Object>> jsonArray2List(String json){
		try {
			return jsonArray2List(new JSONArray(json));
		} catch (Exception e) {
			e.printStackTrace();
		} 	
		return new ArrayList<Map<String,Object>>();
	}

	/**
	 * 把JSONArray对象解析成相应的List<Map<String, Object>>对象,给Adapter提供数据库源
	 * 
	 * @param
	 * @return map对象列表,map中含有bean对象的属性和值
	 * @throws Exception
	 */
	public static List<Map<String, Object>> jsonArray2List(JSONArray jArray){
		int arrayLen = jArray.length();
		List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>(
				arrayLen);
		for (int i = 0; i < arrayLen; i++) {
			JSONObject jObject = jArray.optJSONObject(i);
			Map<String, Object> map = new HashMap<String, Object>();
			map = json2Map(jObject);
			mapList.add(map);
		}
		return mapList;
	}

	/**
	 * 把JSONObject对象放入Map中
	 * 
	 *            json对象
	 * @return 含有bean对象属性和值的Map，key放属性，value放值
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> json2Map(JSONObject jObject) {
		if (jObject == null) {
			return new HashMap<String, Object>();
		}
		Map<String, Object> map = new HashMap<String, Object>();
		Iterator<String> it = jObject.keys();
		while (it.hasNext()) {
			String field = it.next();
			Object value = jObject.opt(field);
			// 把JSONObject NULL对象转换为null
			if (jObject.isNull(field)) {
				value = "";
			}
			map.put(field, value);
		}
		return map;
	}

	/**
	 * 把JSONObject对象放入Map中
	 * 
	 * @param json
	 *            json字符串
	 * @return 含有bean对象属性和值的Map，key放属性，value放值
	 * @throws Exception
	 */
	public static Map<String, Object> json2Map(String json) {
		try {
			return json2Map(new JSONObject(json));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return new HashMap<String, Object>();
	}

	// 时间格式处理
	private static Date formatDate(Object obj) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
		Date dDate = new Date();
		String sDate = obj.toString();
		int len = sDate.length();
		switch (len) {
		case 10:
			simpleDateFormat.applyPattern("yyyy-MM-dd");
			break;
		case 16:
			simpleDateFormat.applyPattern("yyyy-MM-dd HH:mm");
			break;
		case 19:
			simpleDateFormat.applyPattern("yyyy-MM-dd HH:mm:ss");
			break;
		case 21:
			simpleDateFormat.applyPattern("yyyy-MM-dd HH:mm:ss");
			break;
		default:
			return null;
		}

		try {
			dDate = simpleDateFormat.parse(sDate);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dDate;
	}
	
	/**
	 * map转换成json
	 * @param map
	 * @return
	 */
	public static String getJsonStr(Map<String, Object> map){
		JSONObject json = new JSONObject(map);
		return json.toString();
	}
}
