package com.do1.cjmobileoa.db.model;

//TODO zhq
//import com.do1.cjmobileoa.apptool.Constants;
//import com.do1.cjmobileoa.apptool.ValidUtil;

/**
 * 保存登录返回信息
 */
public class UserVO {

	private String fileServerUrl; // 文件服务器地址
	private String userId; // 用户ID
	private String userName; // 用户名称
	private String _status_desc; // 状态描述-中文
	private String status; // 状态 0：启动；1：禁用
	private String createTime; // 创建时间
	private String lastLoginTime; // 最后登录时间
	private String shortMsg; // 心情
	private String deptName; // 部门名称
	private String deviceToken; // 设备唯一号
	private String _deviceType_desc; // 设备类型描述
	private String deviceType; // 设备类型（imei）
	private String password; // 密码
	private String personId; // 用户ID
	private String personName; // 用户名称
	private String mobile; // 手机号码
	private String shortMobile; // 短号
	private String imUserId; // 用户ID
	private String orgId; // 机构ID
	private String orgName; // 机构名称
	private String picPath; // 头像
	private String email; // 邮箱
	private String position; // 职位
	private String sex; // 性别
	private String sexDesc; // 性别
	private SimpleUserVO simpleVO; // 简洁用户信息
	private String showSecuLevel; // 用户的级别
	private String agreeShareXy; // 更新同意共享位置配置0:同意；1：不同意
	private String agreeUploadXy; // 更新同意上传位置配置0:同意；1：不同意
	private String point; // 积分
	private String birthday; // 生日

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getPoint() {
		return point;
	}

	public void setPoint(String point) {
		this.point = point;
	}

	public String getAgreeShareXy() {
		return agreeShareXy;
	}

	public void setAgreeShareXy(String agreeShareXy) {
		this.agreeShareXy = agreeShareXy;
	}

	public String getAgreeUploadXy() {
		return agreeUploadXy;
	}

	public void setAgreeUploadXy(String agreeUploadXy) {
		this.agreeUploadXy = agreeUploadXy;
	}

	public String getFileServerUrl() {
		return fileServerUrl;
	}

	public void setFileServerUrl(String fileServerUrl) {
		this.fileServerUrl = fileServerUrl;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public SimpleUserVO getSimpleVO() {
		return simpleVO;
	}

	public void setSimpleVO(SimpleUserVO simpleVO) {
		this.simpleVO = simpleVO;
	}

	public String get_deviceType_desc() {
		return _deviceType_desc;
	}

	public void set_deviceType_desc(String _deviceType_desc) {
		this._deviceType_desc = _deviceType_desc;
	}

	public String getSexDesc() {
		return sexDesc;
	}

	public void setSexDesc(String sexDesc) {
		this.sexDesc = sexDesc;
	}

	public String get_status_desc() {
		return _status_desc;
	}

	public void set_status_desc(String _status_desc) {
		this._status_desc = _status_desc;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getDeviceToken() {
		// TODO zhq
		// if(ValidUtil.isNullOrEmpty(deviceToken)){
		// deviceToken=Constants.deviceId;
		// return Constants.deviceId;
		// }
		return deviceToken;
	}

	public void setDeviceToken(String deviceToken) {
		this.deviceToken = deviceToken;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getImUserId() {
		return imUserId;
	}

	public void setImUserId(String imUserId) {
		this.imUserId = imUserId;
	}

	public String getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(String lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPersonId() {
		return personId;
	}

	public void setPersonId(String personId) {
		this.personId = personId;
	}

	public String getPersonName() {
		return personName;
	}

	public void setPersonName(String personName) {
		this.personName = personName;
	}

	public String getPicPath() {
		return picPath;
	}

	public void setPicPath(String picPath) {
		this.picPath = picPath;
	}

	public String getShortMobile() {
		return shortMobile;
	}

	public void setShortMobile(String shortMobile) {
		this.shortMobile = shortMobile;
	}

	public String getShortMsg() {
		return shortMsg;
	}

	public void setShortMsg(String shortMsg) {
		this.shortMsg = shortMsg;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getShowSecuLevel() {
		return showSecuLevel;
	}

	public void setShowSecuLevel(String showSecuLevel) {
		this.showSecuLevel = showSecuLevel;
	}

}
