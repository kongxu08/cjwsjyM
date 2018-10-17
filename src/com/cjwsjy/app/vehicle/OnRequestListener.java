package com.cjwsjy.app.vehicle;


/**
 * 网络请求的监听方法
 * @Copyright:   Copyright (c)2013  
 * @Company:     广州市道一信息有限公司
 * @author:      Mr.y  
 * @version:     1.0  
 * @date:        2013-10-15 下午3:24:12  
 *  
 * Modification History:  
 * Date         Author      Version     Description  
 * ------------------------------------------------------------------  
 * 2013-10-15      Mr.y          1.0       1.0 Version
 */
public interface OnRequestListener {
	/**
	 * 操作成功
	 * @param resultObject
	 */
	public void onExecuteSuccess(int requestId,ResultObject resultObject);
	/**
	 * 操作失败
	 * @param resultObject
	 */
	public void onExecuteFail(int requestId,ResultObject resultObject);
	/**
	 * 网络异常
	 */
	public void onNetworkError(int requestId);
}
