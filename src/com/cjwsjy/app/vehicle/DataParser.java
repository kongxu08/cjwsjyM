package com.cjwsjy.app.vehicle;

import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.do1.cjmobileoa.parent.util.JsonUtil;
import com.do1.cjmobileoa.parent.util.MapUtil;

/**
 * Description:  数据解析策略类，在paserData方法中实现解析数据的策略，其他方法为其提供帮助
 * @Copyright:   Copyright (c)2013  
 * @Company:     广州市道一信息有限公司
 * @author:      Mr.y  
 * @version:     1.0  
 * @date:        2013-10-15 下午3:23:25  
 *  
 * Modification History:  
 * Date         Author      Version     Description  
 * ------------------------------------------------------------------  
 * 2013-10-15      Mr.y          1.0       1.0 Version
 */
public abstract class DataParser<T> {
	abstract public ResultObject parseData(T data);
	abstract public ResultObject parseData2(Map data);
	abstract public ResultObject parseData3( JSONObject jsonObj);
	protected static Map<String, Object> json2Map(JSONObject jObject) {
		return JsonUtil.json2Map(jObject);
	}

	protected static List<Map<String, Object>> jsonArray2List(JSONArray jArray) {
		return JsonUtil.jsonArray2List(jArray);
	}

	/**
	 * 提供从Map中获取数据的方法，
	 * 
	 * @param dataMap
	 *            Map
	 * @param key
	 * @param defaultValue
	 *            默认值
	 * @return
	 */
	protected static <T> T getValueFromMap(Map<String, Object> dataMap,
			String key, T defaultValue) {
		return MapUtil.getValueFromMap(dataMap, key, defaultValue);
	}
}
