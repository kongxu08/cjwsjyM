package com.cjwsjy.app.vehicle;

public class ShareType {
	
	/**
	 * 请求ID
	 */
	public static String CMD_ID = "id";

	/**
	 * 接收状态，用于修改indexActivity左上角显示的文字内容
	 */
	public static String receiveType = "receiveType";
	
	/**
	 * 判断用户是否登录成功过,如果登录成功过,没有点击注销,
	 * 没有在其他手机上登录,那么下次进来就默认自动登陆,直接进入首页
	 */
	public static String isLogin = "isLogin";
	public static String mobile = "mobile";
	public static String userId = "userId";
	public static String personName = "personName";
	public static String key = "key";
	public static String deviceToken = "deviceToken";
	public static String isFirstLogin = "isFirstLogin";//是否是第一次登录
	public static String loginVersion = "loginVersion";//用于升级后是否要展示引导页
	
	/**
	 * 第一次进入页面要指导用户操作
	 */
	public static String guideSetting = "guideSetting";
	public static String guideMyInfo = "guideMyInfo";
	public static String guidePersonInfo = "guidePersonInfo";
	public static String guideAuth = "guideAuth";
	public static String guideChat = "guideChat";
	
	public static String guideSettingVersion = "guideSettingVersion";
	public static String guideMyInfoVersion = "guideMyInfoVersion";
	public static String guidePersonInfoVersion = "guidePersonInfoVersion";
	public static String guideAuthVersion = "guideAuthVersion";
	public static String guideChatVersion = "guideChatVersion";
	
	
	/**
	 * 存首页的对话
	 */
	public static String indexChat = "indexChat";
	
	/**
	 * 联系人版本
	 */
	public static String CONTACT_VERSON = "contactVerson";
	/**
	 * 联系人最新版本
	 */
	public static String CONTACT_VERSON_TOP = "contactVersonTop";
	
	/**
	 * 组织机构版本
	 */
	public static String NODE_VERSON = "nodeVerson";
	/**
	 * 组织机构最新版本
	 */
	public static String NODE_VERSON_TOP = "nodeVersonTop";
	
	/**
	 * 联系人输入导入状态  0:表示导入未完成；1：表示导入完成
	 */
	public static String IMPORT_PERSON_STATUS = "importPersonStatus";
	/***
	 * person_NODE 表导入本地数据库的状态
	 */
	public static String IMPORT_PERSON_NODE_STATUS = "importPersonNodeStatus";
	
	/***
	 * TB_IM_NODE_表导入的状态
	 */
	public static String IMPORT_TB_IM_NODE_STATUS="importTb_Im_Node";
	
	/***
	 * TB_IM_PERSON_EXT表导入的状态
	 */
	public static String IMPORT_PERSON_EXT_STATUS="importTB_IM_PERSON_EXT";
	
	/***
	 * PERSON 表的数据总数和 导入了多少条到本地数据库
	 */
	public static String IMPORT_PERSON_MAX = "importPersonMax";
	public static String IMPORT_PERSON_PROPCESS = "importPersonPropcess";
	/**
	 * PERSON 表 当前请求到第几页数据  和 页的总数
	 */
	public static String IMPORT_PERSON_CURRENTPAGE = "importPersonCurrentPage";
	public static String IMPORT_PERSON_MAXPAGE = "importPersonMaxPage";
	/**
	 * PERSON_EXT 表 当前请求到第几页数据  和 页的总数
	 */
	public static String IMPORT_PERSON_EXT_CURRENTPAGE = "importPersonExtCurrentPage";
	public static String IMPORT_PERSON_EXT_MAXPAGE = "importPersonExtMaxPage";
	
	/***
	 * PERSON_NODE 表的数据总数和 导入了多少条到本地数据库
	 */
	public static String IMPORT_PERSON_NODE_MAX = "importPersonNodeMax";
	public static String IMPORT_PERSON_NODE_PROPCESS = "importPersonNodePropcess";
	public static String IMPORT_TB_GROUP_PERSON_PROPCESS = "importTbGroupPersonPropcess";
	public static String START_IMPORT_DATA="startImportData";
	public static String IMPORT_PERSON_FLAG = "importPersonFalg";
	public static String START_IMPORT_DATA2="startImportData2";
	public static String IMPORT_PERSON_REQUSET_PAGE = "importPersonRequestPage";
	public static String IMPORT_PERSON_PAGE = "importPersonPage";
	public static String IMPORT_PERSON_EXT_PAGE = "importPersonExtPage";
	/**
	 * 是否获取地理位置
	 * 2:不在提醒（ 默认不获取）
	 * 1：不再提醒（默认获取）
	 * 0：提醒
	 */
	public static String IS_GET_LOCATION = "isGetLocation";
}
