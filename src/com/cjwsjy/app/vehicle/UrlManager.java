package com.cjwsjy.app.vehicle;

import cmcc.gz.app.common.base.util.PropertyUtil;
/**
*
* url帮助类
*/
public class UrlManager {

	
	/**
	 * 用于文件拼装的url地址,例如image
	 */
	//public static final String appRemoteFileUrl = PropertyUtil.getPropertyValue("app.properties", "appRemoteFileUrl", "");
	/**
	 * 用于wap跳转路径拼装的url地址
	 */
	//public static final String appRemoteUrl = PropertyUtil.getPropertyValue("app.properties", "appRemoteUrl", "");
	public static final String appRemoteUrl = "http://vms.cispdr.com:8080/";
	
	/**
	 * 订单列表
	 */
	public static final String GET_ORDER_LIST= "Services/GetOrderList.aspx";
	/**
	 * 订单详情
	 */
	public static final String GET_ORDER_DETAIL= "Services/GetOrderById.aspx";
	/**
	 *取消 订单
	 */
	public static final String CANCEL_ORDER= "Services/Cancelorder.aspx";
	/**
	 *提交订单
	 */
	public static final String COMMIT_ORDER= "Services/SubmitCarApplication.aspx";
	/**
	 *变更订单
	 */
	public static final String CHANGE_ORDER= "Services/ModifyCarApplication.aspx";
	/**
	 *提交评价
	 */
	public static final String COMMIT_COMMENT= "Services/SubmitUserRating.aspx";
	/**
	 *获取车辆类型
	 */
	public static final String GET_VEHILE_TYPE= "Services/GetVehicleinfoType.aspx";
	/**
	 * 司机列表
	 */
	public static final String GET_DRIVER_LIST= "Services/GetdriverList.aspx";
	/**
	 * 车辆列表
	 */
	public static final String GET_VEHILE_LIST= "Services/GetVehicleList.aspx";
	/**
	 * 品牌列表
	 */
	public static final String GET_MODEL= "Services/getmodel.aspx";
	/**
	 * 排量列表
	 */
	public static final String GET_CER= "Services/GetDisplacement.aspx";
	/**
	 * 提交调度
	 */
	public static final String SUBMIT_DISPATCH= "Services/SubmitDispatchInfo.aspx";
	/**
	 * 获取部门
	 */
	public static final String GET_DEPARTMENT= "Services/getdeptex.aspx";
	/**
	 * 获取人员
	 */
	public static final String GET_USERLIST= "Services/GetUesrList.aspx";
	/**
	 * 判断是否调度员
	 */
	public static final String IS_DISPATCHER= "Services/IsDispatcher.aspx";
	/**
	 * 确认变更
	 */
	public static final String ConfirmChange= "Services/ConfirmChange.aspx";
	/**
	 * 确认取消
	 */
	public static final String ConfirmCancel= "Services/ConfirmCancel.aspx";
	/**
	 * 还车确认
	 */
	public static final String ConfirmCard= "Services/ConfirmCard.aspx";
	/**
	 * 提前完成
	 */
	public static final String CompletedAhead= "Services/CompletedAhead.aspx";
	
	/**
	 * 收集客户端信息接口
	 */
	public static final String clientCollects= "Services/clientCollects.aspx";
	
	/**
	 * 车辆定位接口
	 */
	public static final String PositionTextAPI= "Services/datavehicletextapi.aspx";
	
	/**
	 * 判断是否是审核员
	 */
	public static final String ISAUDITER= "Services/IsAuditer.aspx";
	
	/**
	 *提交审核意见
	 */
	public static final String SubmitAuditResult= "Services/SubmitAuditResult.aspx";
	/**
	 *获取常用地址
	 */
	public static final String GetCommonlocation= "Services/GetCommonlocation.aspx";

	/**
	 *获取推送消息详情
	 */
	public static final String GePushMsgById= "Services/GePushMsgById.aspx";
}
