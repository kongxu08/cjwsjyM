package com.do1.cjmobileoa.parent.callback;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.do1.cjmobileoa.parent.util.MapUtil;


/**
 * 保存一次操作结果经过解析后（json格式或xml等格式解析）的数据，有操作状态、返回描述、map类型数据、listMap类型数据和总页数
 * 并提供从map或listMap获取数据的简易方法
 * @Copyright:   Copyright (c)2013  
 * @Company:     广州市道一信息有限公司
 * @author:      Mr.y  
 * @version:     1.0  
 * @date:        2013-10-15 下午3:24:21  
 *  
 * Modification History:  
 * Date         Author      Version     Description  
 * ------------------------------------------------------------------  
 * 2013-10-15      Mr.y          1.0       1.0 Version
 */
final public class ResultObject implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8479156104744243924L;
	/** 操作成功的状态。true:表示操作成功，有返回数据，false表示操作不成功，返回数据可能为空 **/
	private boolean success;//
	private int code;// 状态码，，有时候返回未登录 时有用
	private String desc;// 返回描述
	private Map<String, Object> dataMap = new HashMap<String, Object>();// 单个map数据
	private List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();// 列表数据
	private int totalPage;// 总页数
	private int total;// 总页数
	private Object other;// 放置额外的数据
	private String cmdType;//消息头

	/**
	 * 操作成功的状态。true:表示操作成功，有返回数据，false表示操作不成功，返回数据可能为空
	 * 
	 * @return
	 */
	public boolean isSuccess() {
		return success;
	}

	/**
	 * 设置当前操作的返回状态 true:表示操作成功，把解析到的数据放入map数据或列表数据中， false表示操作不成功，返回数据可能为空
	 * 
	 * @param success
	 */
	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public int getCode() {
		return code;
	}

	public String getCmdType() {
		return cmdType;
	}

	public void setCmdType(String cmdType) {
		this.cmdType = cmdType;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public Map<String, Object> getDataMap() {
		return dataMap;
	}

	public void addDataMap(Map<String, Object> dataMap) {
		this.dataMap.putAll(dataMap);
	}

	/**
	 * 往dataMap中放入单个数据
	 * 
	 * @param key
	 * @param value
	 */
	public void put2Map(String key, Object value) {
		this.dataMap.put(key, value);
	}

	public List<Map<String, Object>> getListMap() {
		return listMap;
	}

	public void addListMap(List<Map<String, Object>> listMap) {
		this.listMap.addAll(listMap);
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	
	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	@Override
	public String toString() {
		return "ResultObject [valid=" + success + ", desc=" + desc
				+ ", dataMap=" + dataMap + ", listMap=" + listMap
				+ ", totalPage=" + totalPage + "]";
	}

	// ~ 从Map获取数据
	/**
	 * 返回map中的值,默认为
	 * 
	 * @param key
	 *            map中的key
	 * @param defaultValue
	 *            value为空时的默认值
	 * @return 类型同defaultValue
	 */
	public <T> T getValueFromMap(String key, T defaultValue) {
		return MapUtil.getValueFromMap(dataMap, key, defaultValue);
	}

	// ~ 从List获取数据

	/**
	 * 返回listMap中第index个map的特定key的值
	 * 
	 * @param index
	 *            listMap的索引
	 * @param key
	 *            map中的key
	 * @param defaultValue
	 *            value为空时的默认值
	 * @return 类型同defaultValue
	 */
	public <T> T getValueFromList(int index, String key, T defaultValue) {
		return MapUtil.getValueFromMap(listMap.get(index), key, defaultValue);
	}

	// 放置额外的数据
	public Object getOther() {
		return other;
	}

	public void setOther(Object other) {
		this.other = other;
	}

}
