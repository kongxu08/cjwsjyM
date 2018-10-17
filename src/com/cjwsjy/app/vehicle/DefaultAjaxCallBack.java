package com.cjwsjy.app.vehicle;

import java.net.URLDecoder;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

/**
 * 提供处理网络请求的模板方法，
 * 可以调用setOnRequestListener来委托一个OnRequestListener来处理网络请求
 * 不建议继承该类
 * @Copyright:   Copyright (c)2013  
 * @Company:     广州市道一信息有限公司
 * @author:      Mr.y  
 * @version:     1.0  
 * @date:        2013-10-15 下午3:23:58  
 *  
 * Modification History:  
 * Date         Author      Version     Description  
 * ------------------------------------------------------------------  
 * 2013-10-15      Mr.y          1.0       1.0 Version
 */
public class DefaultAjaxCallBack<T> extends AjaxCallback<T> implements
		OnRequestListener {
	private DataParser<T> mParser;
	private OnRequestListener mListener;
	private int requestId;

	public DefaultAjaxCallBack(int requestId,DataParser<T> parser) {
		this.requestId = requestId;
		this.mParser = parser;
	}
	
	public DefaultAjaxCallBack(int requestId, DataParser<T> parser,OnRequestListener listener) {
		this.requestId = requestId;
		this.mParser = parser;
		this.mListener = listener;
	}
	
	public void setOnRequestListener(OnRequestListener listener){
		this.mListener = listener;
	}
	
	@Override
	public void onExecuteSuccess(int requestId,ResultObject resultObject) {
		if (mListener !=null){
			mListener.onExecuteSuccess(requestId,resultObject);
		}
	}

	@Override
	public void onExecuteFail(int requestId,ResultObject resultObject) {
		if (mListener !=null){
			mListener.onExecuteFail(requestId,resultObject);
		}
	}

	@Override
	public void onNetworkError(int requestId) {
		if (mListener !=null){
			mListener.onNetworkError(requestId);
		}
	}

	/**
	 * 模板方法,调用解析器解析数据并分发结果
	 */
	@Override
	final public void callback(String url, T object, AjaxStatus status) {
		//Log.e("DefaultAjaxCallBack", "请求结果：" + (object == null ? "null" : URLDecoder.decode(object.toString())));
		if (status.getCode() == AjaxStatus.NETWORK_ERROR) {
			onNetworkError(requestId);
			return;
		}

		if (mParser != null) {
			ResultObject resultObject = mParser.parseData(object);
			if (resultObject == null || !resultObject.isSuccess()) {
				onExecuteFail(requestId,resultObject);
			} else {
				onExecuteSuccess(requestId,resultObject);
			}
		}

	}
}