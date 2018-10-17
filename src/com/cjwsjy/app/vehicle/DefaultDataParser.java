package com.cjwsjy.app.vehicle;

import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.R.integer;
import android.content.Context;

import com.google.gson.Gson;






/**
 * 数据解析
 * @Copyright:   Copyright (c)2013  
 * @Company:     广州市道一信息有限公司
 * @author:      Mr.y  
 * @version:     1.0  
 * @date:        2013-10-15 下午3:24:05  
 *  
 * Modification History:  
 * Date         Author      Version     Description  
 * ------------------------------------------------------------------  
 * 2013-10-15      Mr.y          1.0       1.0 Version
 */
public class DefaultDataParser extends DataParser<String> {
	
	private static DefaultDataParser sInstance = null;

	@Override
	public ResultObject parseData(String json) {
		ResultObject result = new ResultObject();
		try {
		
			
			Map<String, Object> resultMap = json2Map(new JSONObject(json));
			for (String key : resultMap.keySet()) {
				if ("code".equals(key)) {// code
					int code = Integer.parseInt(getValueFromMap(resultMap, key, -1)+"");
					result.setCode(code);
					result.setSuccess(0 == code);
				} else if ("desc".equals(key)) {// desc
					String desc = getValueFromMap(resultMap, key, "");
					result.setDesc(desc);
				} else if ("cmdType".equals(key)) {// desc
					String cmdType = getValueFromMap(resultMap, key, "");
					result.setCmdType(cmdType);
				} else if ("pageInfo".equals(key)) {// desc
					Object pageObj = getValueFromMap(resultMap, key, null);
					if(pageObj instanceof JSONObject){
						Map<String, Object> pageMap = json2Map((JSONObject) pageObj);
						int totalPage = Integer.parseInt(pageMap.get("totalPages")+"");
						result.setTotalPage(totalPage);  
					}
				} else if ("data".equals(key)) {// data
					Object dataObject = getValueFromMap(resultMap, key, null);
					if (dataObject instanceof JSONArray) {// listMap
						List<Map<String, Object>> listMap = jsonArray2List((JSONArray) dataObject);
						result.addListMap(listMap);
					} else if (dataObject instanceof JSONObject) {// Map
						Map<String, Object> dataMap = json2Map((JSONObject) dataObject);
						result.addDataMap(dataMap);
					}
				} else {
					Object value = getValueFromMap(resultMap, key, null);
					result.put2Map(key, value);
				}
			}
		}  catch (Exception e) {
			e.printStackTrace();
		}
		return result;

	}

	public static DataParser<String> getInstance() {
		if (sInstance == null) {
			sInstance = new DefaultDataParser();
		}
		return sInstance;
	}

	@SuppressWarnings("unchecked")
	public ResultObject parseData2(Map json) {
		ResultObject result = new ResultObject();
		try {
			Map<String, Object> resultMap = json;
			for (String key : resultMap.keySet()) {
				if ("result_code".equals(key)) {// code  
				//	Boolean code = getValueFromMap(resultMap, key, false);
					int code = Integer.parseInt(getValueFromMap(resultMap, key, -1)+"");
					result.setSuccess(code==1);
				} else if ("msg".equals(key)) {// desc
					String msg = getValueFromMap(resultMap, key, "");
					result.setDesc(msg);
				} else if ("status".equals(key)) {// desc
					int status = Integer.parseInt(resultMap.get("status")+"");
					result.setCode(status);
				}else if ("data".equals(key)) {// data
					if(json.get(key).toString().startsWith("[")){//列表
						
						result.addListMap((List<Map<String, Object>>) json.get(key));
				
						}else{
							result.addDataMap( (Map<String, Object>) json.get(key));
						}
					
				}
			}
		}  catch (Exception e) {
			e.printStackTrace();
		}
		return result;

	}

	@Override
	public ResultObject parseData3(JSONObject jsonObj) {
		
		
		ResultObject result = new ResultObject();
		try {
			Map<String, Object> resultMap = json2Map(jsonObj);
			for (String key : resultMap.keySet()) {
				if ("result_code".equals(key)) {// code  
					int code = Integer.parseInt(getValueFromMap(resultMap, key, -1)+"");
					result.setSuccess(code==1);
				} else if ("desc".equals(key)) {// desc
					String msg = getValueFromMap(resultMap, key, "");
					result.setDesc(msg);
				} else if ("total_num".equals(key)) { 
					int total_num =Integer.parseInt(getValueFromMap(resultMap, key, -1)+"");
					int totalPage=total_num/20;
					if(total_num%20>0){
						totalPage+=1;
					}
					result.setTotalPage(totalPage);
				} else if ("status".equals(key)) {// desc
					int status = Integer.parseInt(resultMap.get("status")+"");
					result.setCode(status);
				}else if ("data".equals(key)) {// data
					if(resultMap.get(key).toString().startsWith("[")){//列表
						Object dataObject = getValueFromMap(resultMap, key, null);
						if (dataObject instanceof JSONArray) {// listMap
							List<Map<String, Object>> listMap = jsonArray2List((JSONArray) dataObject);
							result.addListMap(listMap);
						} else if (dataObject instanceof JSONObject) {// Map
							Map<String, Object> dataMap = json2Map((JSONObject) dataObject);
							result.addDataMap(dataMap);
						}
					}else if(!"".equals(resultMap.get(key).toString())){//列表
						Object dataObject = getValueFromMap(resultMap, key, null);
						Map<String, Object> dataMap = json2Map((JSONObject) dataObject);
						result.addDataMap(dataMap);
					}
				}else {
					Object value = getValueFromMap(resultMap, key, null);
					result.put2Map(key, value);
				}
			}
		}  catch (Exception e) {
			e.printStackTrace();
		}
		return result;

	}



	
}
