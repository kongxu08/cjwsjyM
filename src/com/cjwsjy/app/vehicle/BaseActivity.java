package com.cjwsjy.app.vehicle;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.com.do1.common.util.security.MD5;
import cn.com.do1.dqdp.android.common.ContactInfo;
import cn.com.do1.dqdp.android.common.ContactUtil;

import com.androidquery.AQuery;
import com.androidquery.callback.BitmapAjaxCallback;
import com.androidquery.util.AQUtility;

import com.do1.cjmobileoa.db.DBManager;
import com.do1.cjmobileoa.db.model.SimpleUserVO;
import com.do1.cjmobileoa.db.model.UserVO;
import com.do1.cjmobileoa.parent.util.JsonUtil;


/**
 * activity父类
 *
 * @Copyright: Copyright (c)2013
 * @Company: 广州市道一信息有限公司
 * @author: Mr.y
 * @version: 1.0
 * @date: 2013-10-15 下午3:27:04
 * <p/>
 * Modification History:
 * Date         Author      Version     Description
 * ------------------------------------------------------------------
 * 2013-10-15      Mr.y          1.0       1.0 Version
 */
public class BaseActivity extends Activity implements OnClickListener {
	public static final int LOCAL_WITH_DATA = 3023;   //本地图片标识符
    private static final int DATA_NOTIFY = 99999;
    public AQuery aq;
    public ProgressDialog dialog;
    public String SERVER_URL;
    public static UserVO uservo = new UserVO();
    public Constants constants;
    public static String key;//key

    public ContactUtil contactUtil;
    public DataParseUtil dataParse;
    public static String loginUserId="";
    public static int switType = 1; 
    
    public TextView mTv = null;

    /**
     * 该列表map对象拥有字段
     * Map<String, Object> map1 = new HashMap<String, Object>();
     * map1.put("chatId", "1");//聊天室ID
     * map1.put("name", "第一事业部");//聊天室名称
     * map1.put("date", "2013-09-11 12:12:12");//聊天室最后一条记录发表时间
     * map1.put("content", "这里的冬天不下雪，就像爱美的有残缺");
     * map1.put("unread", "3");//该聊天室未读消息
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        init();
//        HttpHelper.setHttpPort(getResources().getInteger(R.integer.http_port));
        super.onCreate(savedInstanceState);
        if(Constants.version==null)
            Constants.version=getVersion();
        System.setProperty("java.net.preferIPv6Addresses", "false");
        aq = new AQuery(this);
        contactUtil=new ContactUtil(this);
		dataParse = new DataParseUtil(this);
        SERVER_URL = getServerUrl();
		Constants.getAppManager().addActivity(this);

        loginUserId = Constants.sharedProxy.getString(ShareType.userId, "");
        
        if (Constants.dbManager == null || !Constants.dbManager.isConnected()) {
        	Constants.dbManager = new DBManager(this);
        }
        
//        mLocClient = ((Constants)getApplication()).mLocationClient;
		((Constants)getApplication()).mTv = mTv;
    }

    public Constants getTAApplication() {
    	if(constants == null){
    		constants = (Constants) getApplication();
    	}
        return constants;
    }



    /**
     * 获取cmdId
     * 每次加1
     */
    static public int getCmdId() {
        int a = 0;
        int id = Integer.parseInt(Constants.sharedProxy.getString(ShareType.CMD_ID, "0"));
        a = id + 1;
        Constants.sharedProxy.putString(ShareType.CMD_ID, a + "");
        Constants.sharedProxy.commit();
//        Log.e("======:" + Constants.sharedProxy.getString(ShareType.CMD_ID, "0"));
        return a;
    }

    /**
     * =================================================================
     * Http连接父类方法
     * =================================================================
     *
     * @return
     */
    public String getServerUrl() {
        return Constants.SERVER_URL;
//        String server = getString(R.string.server);
//        if ("2".equals(server)) {
//            return getString(R.string.server_url2);
//        } else if ("3".equals(server)) {
//            return getString(R.string.server_url3);
//        } else {
//            return getString(R.string.server_url);
//        }
    }

    public void requestBefore(Context context) {
        dialog = ProgressDialog.show(context, "温馨提示", "正在请求，请稍后...");
    }

    public void requestAfter() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public String getFullUrl(String url, Map<String, Object> map) {
        if (map != null) {
            for (Map.Entry<String, Object> m : map.entrySet()) {
                url += m.getKey() + "=" + m.getValue() + "&";
            }
        }
        return SERVER_URL + url;
    }

    /**
     * 根据URL进行Http Get请求
     *
     * @param url
     */
    public void asyncGetJson(final int requestId, String url, Map<String, Object> map) {
        requestBefore(this);
        url = getFullUrl(url, map);
        //Log.e(url);
        aq.progress(dialog).ajax(url, String.class, createAjaxCallBack(requestId));
    }

    /**
     * post请求
     *
     * @param url    请求url
     * @param params map键值对对象
     */
    public void asyncPostJson(final int requestId, String url, Map<String, Object> params) {
        aq.ajax(url, params, String.class, createAjaxCallBack(requestId));
    }

    /**
     * 将jsonObject对象解析到ResultObject对象中
     */
    public DataParser<String> mDataParserBase = new DataParser<String>() {
		@Override
        public ResultObject parseData(String data) {
            return DefaultDataParser.getInstance().parseData(data);
        }

		@Override
		public ResultObject parseData2(Map data) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public ResultObject parseData3(JSONObject jsonObj) {
			// TODO Auto-generated method stub
			return null;
		}

    };

    /**
     * 创建请求返回的对象
     *
     * @return
     */
    private DefaultAjaxCallBack<String> createAjaxCallBack(int requestId) {
        DefaultAjaxCallBack<String> ajaxCallBack = new DefaultAjaxCallBack<String>(requestId, mDataParserBase, mRequestListenerBase);
        return ajaxCallBack;
    }

    private OnRequestListener mRequestListenerBase = new OnRequestListener() {

		@Override
        public void onNetworkError(int requestId) {
            BaseActivity.this.onNetworkError(requestId);
        }

		@Override
        public void onExecuteSuccess(int requestId, ResultObject resultObject) {
            BaseActivity.this.onExecuteSuccess(requestId, resultObject);
        }

		@Override
        public void onExecuteFail(int requestId, ResultObject resultObject) {
            BaseActivity.this.onExecuteFail(requestId, resultObject);
        }
    };

    protected void onNetworkError(int requestId) {
        requestAfter();
        ToastUtil.showLongMsg(this, "网络连接失败，请检查网络");
    }

    ;

    protected void onExecuteSuccess(int requestId, ResultObject resultObject) {
        requestAfter();
    };

    protected void onExecuteFail(int requestId, ResultObject resultObject) {
        requestAfter();
//        ToastUtil.showLongMsg(this, resultObject.getDesc());
    };


//    /**
//     * 设置头部
//     *
//     * @param headView      头部布局
//     * @param leftDrawable  头部左边按钮，传0默认不显示按钮
//     * @param
//     * @param rightDrawable 头部右边按钮，传0默认不显示按钮
//     * @param leftListener  头部左边按钮监听器，传null默认为返回
//     * @param rightListener 头部右边按钮监听器
//     */
//    public void setHeadView(View headView, int leftDrawable, String leftText, String centerTitle, int rightDrawable, String rightText, OnClickListener leftListener, OnClickListener rightListener) {
//        if (headView != null) {
//
//            if (leftDrawable != 0) {
//                headView.findViewById(R.id.leftImage).setVisibility(View.VISIBLE);
//                ((TextView) headView.findViewById(R.id.leftImage)).setText(leftText);
//                headView.findViewById(R.id.leftImage).setBackgroundResource(leftDrawable);
//                headView.findViewById(R.id.leftImage).setOnClickListener(leftListener == null ? this : leftListener);
//            }else{
//            	headView.findViewById(R.id.leftImage).setVisibility(View.GONE);
//            }
//            if (rightDrawable != 0) {
//                headView.findViewById(R.id.rightImage).setVisibility(View.VISIBLE);
//                ((TextView) headView.findViewById(R.id.rightImage)).setText(rightText);
//                headView.findViewById(R.id.rightImage).setBackgroundResource(rightDrawable);
//                headView.findViewById(R.id.rightImage).setOnClickListener(rightListener);
//            }else{
//            	headView.findViewById(R.id.rightImage).setVisibility(View.GONE);
//            }
//            if (rightDrawable == R.drawable.btn_head_5 && centerTitle.length() > 6) {
//                centerTitle = centerTitle.substring(0, 5) + "...";
//            } else if (rightDrawable == R.drawable.btn_head_4 && centerTitle.length() > 7) {
//                centerTitle = centerTitle.substring(0, 6) + "...";
//            } else if (centerTitle.length() > 8) {
//                centerTitle = centerTitle.substring(0, 7) + "...";
//            }
//            ((TextView) headView.findViewById(R.id.centerTitle)).setText(centerTitle);
//        }
//    }

    @Override
    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.leftImage:
//                finish();
//                break;
//		case R.id.rightImage:
//			Intent intent = new Intent(BaseActivity.this,IndexActivity.class);
//			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//			startActivity(intent);
//			break;
//        }
    }

//    public String getUserLogoUrl(String userId){
//        return SERVER_URL + getString(R.string.headlogo_url) +userId + ".jpg";
//    }
    
    public String getPublicLogo(String publicId){
        return uservo.getFileServerUrl() + "/publicUserHeadImg/" + publicId + ".jpg";
    }

//    public void bindHeadLogo(final ImageView view) {
//        String url = getUserLogoUrl(uservo.getUserId());
//        Log.e("============url=============:" + url);
//        ImageViewTool.getAsyncImageBg(url, view, R.drawable.logo_default,false);
//    }
//    public void bindHeadLogo(final ImageView view,String userId) {
//        String url = getUserLogoUrl( userId );
//        ImageViewTool.getAsyncImageBg(url, view, R.drawable.logo_default,false);
//    }

    /**
     * 加密钥
     * @return
     */
    static public String getVerifyCode(){
    	try {
			return new MD5().getMD5ofStr(key + uservo.getDeviceToken());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
    	return "";
    }

    /**
     * 保存用户登录信息
     */
    static public void saveUser(ResultObject obj) {
        try {
        	key = obj.getDataMap().get("key") + "";
            Map<String, Object> map = JsonUtil.json2Map(obj.getDataMap().get("user") + "");
            uservo.setFileServerUrl(obj.getDataMap().get("fileServerUrl")+"");
            uservo.setAgreeShareXy(obj.getDataMap().get("agreeShareXy")+"");
            uservo.setAgreeUploadXy(obj.getDataMap().get("agreeUploadXy")+"");
            uservo.set_deviceType_desc(map.get("_deviceType_desc") + "");
            uservo.setDeptName(map.get("deptName") + "");
            uservo.set_status_desc(map.get("_status_desc") + "");
            uservo.setCreateTime(map.get("createTime") + "");
            uservo.setDeviceToken(map.get("deviceToken") + "");
            uservo.setDeviceType(map.get("deviceType") + "");
            uservo.setEmail(map.get("email") + "");
            uservo.setImUserId(map.get("imUserId") + "");
            uservo.setLastLoginTime(map.get("lastLoginTime") + "");
            uservo.setMobile(map.get("mobile") + "");
            uservo.setOrgId(map.get("orgId") + "");
            uservo.setOrgName(map.get("orgName") + "");
            uservo.setPassword(map.get("password") + "");
            uservo.setPersonId(map.get("personId") + "");
            uservo.setPersonName(map.get("personName") + "");
            uservo.setPosition(map.get("position") + "");
            uservo.setSex(map.get("sex") + "");
            uservo.setSexDesc(map.get("sexDesc") + "");
            uservo.setPicPath(map.get("picPath") + "");
            uservo.setShortMobile(map.get("shortMobile") + "");
            uservo.setShortMsg(map.get("shortMsg") + "");
            uservo.setStatus(map.get("status") + "");
            uservo.setUserId(map.get("userId") + "");
            uservo.setUserName(map.get("userName") + "");
            uservo.setBirthday(map.get("birthday") + "");
            uservo.setShowSecuLevel(map.get("showSecuLevel")+"");
            uservo.setPoint(map.get("point")+"");
            SimpleUserVO simple = new SimpleUserVO();
            simple.setEmail(map.get("email") + "");
            simple.setMobile(map.get("mobile") + "");
            simple.setPersonName(map.get("personName") + "");
            simple.setPicPath(map.get("picPath") + "");
            simple.setShortMobile(map.get("shortMobile") + "");
            simple.setUserId(map.get("userId") + "");
            simple.setUserName(map.get("userName") + "");
            simple.setUserCount(obj.getDataMap().get("userCount") + "");
            uservo.setSimpleVO(simple);
            
            JSONObject extObj = (JSONObject) obj.getDataMap().get("ext");
//            ExtVO extVo = new ExtVO();
//            extVo.setIndustry(extObj.optString("industry"));
//            extVo.setAreaDesc(extObj.optString("areaDesc"));
//            extVo.setIndustryDesc(extObj.optString("industryDesc"));
//            extVo.setCompanyDesc(extObj.optString("companyDesc"));
//            uservo.setExtVo(extVo);

            Constants.sharedProxy.putBoolean(ShareType.isLogin, true);
    		Constants.sharedProxy.putString(ShareType.mobile, map.get("mobile") + "");
    		Constants.sharedProxy.putString(ShareType.userId, map.get("userId") + "");
    		Constants.sharedProxy.putString(ShareType.personName, map.get("personName") + "");
    		Constants.sharedProxy.putString(ShareType.key, obj.getDataMap().get("key") + "");
    		Constants.sharedProxy.putString(ShareType.deviceToken, map.get("deviceToken") + "");
    		Constants.sharedProxy.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
//    protected void request(){
//    	if(!Constants.sessionManager.isConnect()){
//    		ToastUtil.showShortMsg(this, "网络连接失败");
//    		return;
//    	}
//    	if(ValidUtil.isNullOrEmpty(BaseActivity.uservo.getUserId())){
//    		ToastUtil.showShortMsg(this, "用户未登录");
//    		return;
//    	}
//    }


    // 判断版本格式,如果版本 > 2.3,就是用相应的程序进行处理,以便影响访问网络
    private static void init() {

//		String strVer = android.os.Build.VERSION.RELEASE; // 获得当前系统版本
//		strVer = strVer.substring(0, 3).trim(); // 截取前3个字符 2.3.3转换成2.3
//		float fv = Float.valueOf(strVer);
//		if (fv > 2.3) {
//			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
//					.detectDiskReads().detectDiskWrites().detectNetwork()
//					.penaltyLog().build());
//			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
//					.detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath()
//					.build());
//		}
    }

    /**
     * 退出应用程序
     *
     * @param isBackground 是否开开启后台运行,如果为true则为后台运行
     */
    public void exitApp(Boolean isBackground) {
        getTAApplication().exitApp(isBackground,true);
    }

    /**
     * 退出应用程序
     */
    public void exitApp() {
        getTAApplication().exitApp(false,true);
    }

    /**
     * 退出应用程序，且在后台运行
     */
//    public void exitAppToBackground(boolean isAutoLogin,int code) {
//        getTAApplication().exitApp(true,true);
//        Intent intent;
//        if("2".equals(Constants.appType)){
////        	intent = new Intent(this, ProLoginActivity.class);
//        }else{
//        	intent = new Intent(this, LoginActivity.class);
//        }
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent.putExtra("isLoginOut", true);
//        intent.putExtra("code", code+"");
//        intent.putExtra("isAutoLogin", isAutoLogin);
//        startActivity(intent);
//    }

    /**
     * map转换成json
     *
     * @param map
     * @return
     */
    public String getJsonStr(Map<String, Object> map) {
        return new JSONObject(map).toString();
    }

    public Handler backUI = new Handler() {
        @Override
		public void handleMessage(Message msg) {
            switch (msg.what) {
                case DATA_NOTIFY: {
                    String msgStr = msg.getData().getString("msg");
                    response(0, DefaultDataParser.getInstance().parseData(msgStr));
                    break;
                }
            }
        }
    };

//    public Handler dbHandler = new Handler() {
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case 0://未读消息插入数据库（indexActivity调用）
//                    final Map<String, Object> map = (Map<String, Object>) msg.obj;
//                    if(MessageType.TYPE_VOICE.equals(map.get("msgType")+"")){
//                    	new Thread(){
//        					@Override
//        					public void run() {
//        						boolean isPrivate = false;
//        				        SimpleDateFormat s = new SimpleDateFormat("yyyyMMdd");
//        						String dataString = s.format(new Date());
//        						String filePath = SDCardUtil.getVoiceDir() + "/";
//        						String strTempFile = "inquirer_";// 零时文件的前缀
//        						File mRecAudioPath = SDCardUtil.getVoiceDirByPath(filePath + dataString + File.separator);
//        						String url = "";
//        						if(map.get("msgContent").toString().contains(ChatUtil.privateKey)){
//        							try {
//        								String vopath = Des3.decode(map.get("msgContent").toString().replace(ChatUtil.privateKey, ""));
//        								url = getServerUrl()+ vopath;
//        								isPrivate = true;
//        							} catch (Exception e) {
//        								e.printStackTrace();
//        							}
//        						}else{
//        							url =  getServerUrl() + map.get("msgContent").toString();
//        						}
//        						File mRecAudioFile;
//        						String vpath = "";
//        						try{
//        							mRecAudioFile = File.createTempFile(strTempFile, ".amr", mRecAudioPath);
//        							vpath = mRecAudioFile.getAbsolutePath();
//        							Log.e("下载语音地址：" + url);
//        							File file = new HttpHelper().downloadFile(url, vpath);
//        							if(file != null && file.length() > 0){
//        								Log.e("接收到的语音地址：" + vpath);
//        								map.put("msgContent", isPrivate ? (ChatUtil.privateKey + Des3.encode(vpath)) : vpath);
//        							}else{//失败也要让用户看到有消息
//        								Log.e("语音下载失败，保存下载url:" + url);
//        								map.put("msgContent", isPrivate ? (ChatUtil.privateKey + Des3.encode(url)) : url);
//        							}
//        						}catch (Exception e) {//失败也要让用户看到有消息
//        							Log.e("语音下载抛出异常，保存下载url:" + url);
//        							map.put("msgContent", isPrivate ? (ChatUtil.privateKey + url) : url);
//        							e.printStackTrace();
//        						}
//        						insertChat(map);
//        					};
//        				}.start();
//                    }else{//图片语音消息
//                    	insertChat(map);
//                    }
//                    break;
//                case 1://修改某一个对话的未读状态，全部修改为已读（chatActivity调用）
//                    Constants.dbManager.updateChatRead(uservo.getUserId(),msg.obj + "");
//                    //修改首页显示数据的未读字段，把数据修改成0
//                    Constants.dbManager.updateCacheRead(uservo.getUserId(),msg.obj + "");
//                    break;
//            }
//        }
//
//        ;
//    };



    /**
     * 生成会话ID
     * 如果与该userId已经有对话，那么取用数据库之前的会话ID
     * 如果与该userId没有产生对话，那么创建一个新的userId
     *
     * @param
     * @return
     */
//    public static String createChatId(String targetId) {
//    	if(ValidUtil.isNullOrEmpty(targetId)){
//    		return UUID.randomUUID().toString();
//    	}
//        String id = Constants.dbManager.getChatIdBytargetId(uservo.getUserId(),targetId);
//        if (ValidUtil.isNullOrEmpty(id)) {
//            return UUID.randomUUID().toString();
//        }
//        return id;
//    }

    /**
     * 发送消息
     *
     * @param cmd           类型，消息头
     * @param cmdId         ID
     * @param broadcastName 广播名称
     * @param dataMap       消息实体
     */
//    public void send(String cmd, int cmdId, String broadcastName, Map<String, Object> dataMap) {
//        sessionManager.send(cmd, cmdId, broadcastName, dataMap);
//    }

    public void response(int requestId, ResultObject result) {

    }


    public void process(String msg) {
        Message message = new Message();
        message.what = DATA_NOTIFY;
        Bundle data = new Bundle();
        data.putString("msg", msg);
        message.setData(data);
        backUI.sendMessage(message);
    }

    /**
     * 获取头像方法
     * @param userId
     * @return
     */
//    public String getImgUrl(String userId){
//    	return getString(R.string.headlogo_url) + userId;
//    }

    /**
     * 获取版本号
    * @return 当前应用的版本号
    */
	public String getVersion() {
		try {
			PackageManager manager = this.getPackageManager();
			PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
			String version = info.versionName;
			return version;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public ProgressBar progressBar;
	public TextView proText,progress_tiltle;
	/**
	 * 导入到手机通讯录
	 */
	public void addToLocal(final List<Map<String, Object>> datalist,final boolean addNew) {
        //配置自己dialog的主题，不然默认会调用系统自带的主题
//        final DialogForBottom dialog = new DialogForBottom(BaseActivity.this, R.style.dialog);
//        dialog.setContentView(R.layout.dialog_clear);
//        ((TextView) dialog.findViewById(R.id.title)).setText("是否确定要将选择的联系人导入手机");
//        Button sureBtn = (Button) dialog.findViewById(R.id.sure);
//        sureBtn.setText("添加到本地通讯录");
//        sureBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//            	dialog.dismiss();
//            	importHandler.sendEmptyMessage(0);
//            	new Thread(){
//            		@Override
//            		public void run() {
//            			List<ContactInfo> infolist = dataParse.setMap2Info(datalist);
//                    	contactUtil.batchUpdateAndAddContact(importHandler,infolist, BaseActivity.this, true, addNew);
//            		};
//            	}.start();
//            }
//        });
//        Button cancelBtn = (Button) dialog.findViewById(R.id.cancle);
//        cancelBtn.setText("取消");
//        cancelBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialog.dismiss();
//            }
//        });
//        dialog.setCanceledOnTouchOutside(false);
//        Window window = dialog.getWindow();
//        window.setGravity(Gravity.BOTTOM);  //此处可以设置dialog显示的位置
//        window.setWindowAnimations(R.style.mystyle);  //添加动画
//        dialog.show();
//        //下面这段是设置弹出dialog宽度满屏
//        WindowManager windowManager = getWindowManager();
//        Display display = windowManager.getDefaultDisplay();
//        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
//        lp.width = (int) (display.getWidth()); //设置宽度
//        dialog.getWindow().setAttributes(lp);
        
        
//        final Dialog2Horbtn addDialog = new Dialog2Horbtn(this, R.style.dialog, "是否确定要将选择的联系人导入手机?");
//        addDialog.setCanceledOnTouchOutside(false);
//        addDialog.show();
//        addDialog.findViewById(R.id.sure).setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				addDialog.dismiss();
//				addDialogCallBack();
//            	importHandler.sendEmptyMessage(0);
//            	new Thread(){
//            		@Override
//            		public void run() {
//            			List<ContactInfo> infolist = dataParse.setMap2Info(datalist);
//                    	contactUtil.batchUpdateAndAddContact(importHandler,infolist, BaseActivity.this, true, addNew);
//            		};
//            	}.start();
//			}
//		});
//        
//        addDialog.findViewById(R.id.close).setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				addDialog.dismiss();
//				addDialogCallBack();
//			}
//		});
//        
//        addDialog.findViewById(R.id.cancel).setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				addDialog.dismiss();
//				addDialogCallBack();
//			}
//		});
    }
	
	/**
	 * 点击Adddialog之后调用的方法，（用于控制按钮的可点击性）
	 */
	public void addDialogCallBack(){
		
	}

	/**
	 * 进度条
	 */
//	public void showImportDialog(int size,final Object obj) {
//		dialogImport = new DialogImport(BaseActivity.this, R.style.myDialogTheme);
//		progressBar = (ProgressBar) dialogImport.findViewById(R.id.progressBar);
//		progress_tiltle = (TextView) dialogImport.findViewById(R.id.progress_tiltle);
//		progress_tiltle.setText("正在导入手机通讯录中.....");
//		proText = (TextView) dialogImport.findViewById(R.id.proText);
//		progressBar.setMax(size);
//		dialogImport.setCancelable(false);
//		dialogImport.show();
//		new Thread() {
//			@Override
//			public void run() {
//				List<ContactInfo> addlist = (List<ContactInfo>) obj;
//				contactUtil.batchInsertContact(importHandler, addlist);
//			}
//		}.start();
//	}

    /**
     * 展示dialog
     * @param title
     * @param flag 1：确定导入手机通讯录
     *             2：您有重复联系人更新
     */
//    public void showDialog(String title,String message,final int flag,final Object obj){
//    	Activity activity = Constants.mAppManager.getLastActivity();
//    	boolean isParent = false;
//    	if(activity instanceof IndexActivity ||
//    			activity instanceof ContactActivity ||
//    			activity instanceof AppListActivity ||
//    			activity instanceof PersonalMain){
//    		isParent = true;
//    	}
//    	dialog2Horbtn = new Dialog2Horbtn(isParent ? activity.getParent() : activity, R.style.dialog, title, message);
//    	dialog2Horbtn.show();
//    	dialog2Horbtn.findViewById(R.id.sure).setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog2Horbtn.dismiss();
//                if (flag == 1) {
//                    importHandler.sendEmptyMessageDelayed(7, 1000);//伪处理
//                } else if (flag == 2) {
//                    importHandler.obtainMessage(5, obj).sendToTarget();
//                    back2view();
//                }
//            }
//        });
//    	dialog2Horbtn.findViewById(R.id.cancle).setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog2Horbtn.dismiss();
//            }
//        });
//    	dialog2Horbtn.setCanceledOnTouchOutside(false);
//    }

    /**
     * 在点击确认前执行一个可以更新当前activity页面的方法
     */
    public void back2view(){

    }
    
    

//   protected Handler progressHandler = new Handler(){
//       public void handleMessage(Message msg) {
//           switch (msg.what){
//               case 0:
//                   AQuery progress = aq.id(R.id.progressLayout);
//                   if(progress.isExist()&&progress.getView().getVisibility()!=View.GONE){
//                       Editable temp = ((EditText) aq.id(R.id.id_progeress_index).getView()).getText();
//                       String text = temp==null?"":temp.toString();
//                       if(!ValidUtil.isNullOrEmpty(text)){
//                           if(text.equals((String)msg.obj)){
//                               aq.id(R.id.progressLayout).getView().setVisibility(View.GONE);
//                               ToastUtil.showShortMsg(BaseActivity.this,"操作时间已超过预期，当前操作转入后台运行，您可以进行其他操作或重新刷新页面");
//                           }
//                       }
//                   }
//                   break;
//           }
//       }
//   };

//    public void showProgeress(int timeOut){
//        aq.id(R.id.progressLayout).visible();
//        EditText tv = (EditText)aq.id(R.id.id_progeress_index).getView();
//        String index = String.valueOf(getCmdId());
//        tv.setText(index);
//        Message msg = new Message();
//        msg.what=0;
//        msg.obj=index;
//        progressHandler.sendMessageDelayed(msg, timeOut * 1000);
//    }
//    public void showProgeress(String msg,int timeOut){
//        aq.id(R.id.tv_text).text(msg);
//        showProgeress(timeOut);
//    }
//    public void hideProgress(){
//        aq.id(R.id.progressLayout).gone();
//    }
//
//    public Handler importHandler = new Handler() {
//        public void handleMessage(Message msg) {
//            if (msg.what == 0) {//准备导入
//                aq.id(R.id.tv_text).text(" 正在准备数据...");
//                showProgeress(60);
//            }else if(msg.what == 1){//显示导入数据进度条dialog(传入arg1<插入列表大小>，表示进度条的最大值；)
//            	aq.id(R.id.progressLayout).gone();
//            	if(msg.arg1 > 0){//如果arg1>0表示有要插入的数据，执行插入
//            		showImportDialog(msg.arg1,msg.obj);
//            	}else{//如果arg1==0，表示没有新增数据，需要传入msg.obj<List<ContactInfo> 更新列表>
//            		List<ContactInfo> updatelist = (List<ContactInfo>) msg.obj;
//            		if(updatelist != null && updatelist.size() > 0)
//            			importHandler.obtainMessage(4, msg.obj).sendToTarget();
//            		else{
//            			ToastUtil.showShortMsg(BaseActivity.this, "这些数据已经存在您的通讯录");
//                        if(Constants.getAppManager().getLastActivity() instanceof ChooseContactActivity)
//            			finish();
//            		}
//            	}
//        	}else if(msg.what == 2){//更新进度条，what=2，arg1=已经导入个数
//            	progressBar.setProgress(msg.arg1);
//            	proText.setText((int) ((float) msg.arg1 / msg.arg2 * 100) + "%     成功导入联系人" + msg.arg1 + "个");
//            }else if(msg.what == 3){//导入完成，传入msg.obj,如果obj为空，就表示没有要更新的数据，如果不为空，就表示有更新的数据，跳转到匹配页面
//            	if(dialogImport != null && dialogImport.isShowing()){
//                	dialogImport.dismiss();
//                	dialogImport = null;
//                }
//            	Toast.makeText(BaseActivity.this, "成功导入联系人" + msg.arg1 + "个", 1000).show();
//            	if(msg.obj != null){
//            		List<ContactInfo> updatelist = (List<ContactInfo>) msg.obj;
//            		if(updatelist.size() > 0)
//            			importHandler.obtainMessage(4, msg.obj).sendToTarget();
//            		else
//            			finish();
//    			}else{
//    				finish();
//    			}
//            }else if(msg.what == 4){//弹出覆盖联系人提示
//                 List<ContactInfo> infolist = (List<ContactInfo>) msg.obj;
//                 setOnActivityResult(infolist);
////            	showDialog("温馨提示", "您的手机通讯录中已有部分联系人，需要覆盖吗？", 2, msg.obj);
//            }else if(msg.what == 5){//跳转到update联系人页面
//            	List<ContactInfo> infolist = (List<ContactInfo>) msg.obj;
//            	setOnActivityResult(infolist);
//            }else if(msg.what == 7){
//            	aq.id(R.id.tv_text).text(" 正在准备数据...");
//				showProgeress(60);
//				new Thread(){
//					public void run() {
//						new SynPersonToLocal(BaseActivity.this).synPersonToLocal();
//					};
//				}.start();
//            }
//        }
//    };

    /**
     * 验证是否本地数据库已经更新完成
     * @return
     */
    public boolean valid(){
    	String type = Constants.sharedProxy.getString(ShareType.IMPORT_PERSON_STATUS, "0");
    	if("1".endsWith(type)){
    		return true;
    	}else{
//    		ToastUtil.showShortMsg(BaseActivity.this, "后台正在更新本地数据库，请稍后操作！您也可以直接进入"+Constants.appName+"后在通讯录页面进行导入通讯录操作。");
    		return false;
    	}
    }

    /**
     * 弹出不同列表
     * @param infolist
     */
    public void setOnActivityResult(List<ContactInfo> infolist){
    	List<Map<String, Object>> datalist = new ArrayList<Map<String,Object>>();
    	for(ContactInfo info : infolist){
    		Map<String, Object> map = new HashMap<String, Object>();
            String[] name = info.getName().split("\\*\\_\\*");
            map.put("personName", name[1]);
    		boolean hasMobile = info.getPhoneList() != null && info.getPhoneList().size() > 0;
            String[] phone = info.getPhoneList().get(0).number.split("\\*\\_\\*");
            map.put("mobile", hasMobile&& phone.length>1 ? phone[1] : "");
    		boolean hasEmail = info.getEmail() != null && info.getEmail().size() > 0;
//    		map.put("email", hasEmail ? info.getEmail().get(0).email.split("\\*\\_\\*")[1] : "");
            String[] dept = info.getDeptName().split("\\*\\_\\*");
            if (dept.length > 1) {
                map.put("deptName", dept[1]);
                info.setDeptName(dept[1]);
            } else
                info.setDeptName("");
            String[] position = info.getPosition().split("\\*\\_\\*");
            if (position.length > 1) {
                map.put("position", position[1]);
                info.setPosition(position[1]);
            } else
                info.setPosition("");
            String[] shortMobile = info.getShortMobile().split("\\*\\_\\*");
            if (shortMobile.length > 1) {
                map.put("shortMobile", shortMobile[1]);
                info.setShortMobile(shortMobile[1]);
            } else
                info.setShortMobile("");
            info.setName(name[1]);
            info.getPhoneList().get(0).number = phone.length > 1 ? phone[1] : "";

//    		ContactInfo local =new ContactInfo();
//    		if(local == null) continue;
    		map.put("oldpersonName", name[0]);
    		boolean oldhasMobile = info.getPhoneList() != null && info.getPhoneList().size() > 0;
    		map.put("oldmobile", oldhasMobile ? phone[0] : "");
//    		boolean oldhasEmail = local.getEmail() != null && local.getEmail().size() > 0;
//    		map.put("oldemail", oldhasEmail ? local.getEmail().get(0).email : "");
            if(dept.length>0)
    		map.put("olddeptName", dept[0]);
            if(position.length>0)
    		map.put("oldposition", position[0]);
            if(shortMobile.length>0)
    		map.put("oldshortMobile", shortMobile[0]);
    		datalist.add(map);
    	}

//    	ImportLocalActivity.datalist.clear();
//    	ImportLocalActivity.updatelist.clear();
//    	ImportLocalActivity.datalist.addAll(datalist);
//    	ImportLocalActivity.updatelist.addAll(infolist);
//    	Intent intent = new Intent(BaseActivity.this,ImportLocalActivity.class);
//    	startActivity(intent);
//    	finish();
    }
    
    public String getUrlByAppName(){
    	if("0".equals(Constants.appType)){//司聊
    		return "http://115.29.163.136:38080/imapk/minaim.apk";
    	}else if("1".equals(Constants.appType)){
    		return "http://im.do1.com.cn/";
    	}else{
    		return "http://im.do1.com.cn/";
    	}
    }
    
    public void gcRam(){
    	long triggerSize = 2000000; 
		long targetSize = 1000000;   
		System.gc(); 
		AQUtility.cleanCacheAsync(this, triggerSize, targetSize); 
		BitmapAjaxCallback.clearCache();
    }
    
    public boolean defaultShowDialog = true;
    public void startGetLocation(){
    	Constants.locateFlag = 0;
//    	showOrHideProgress(defaultShowDialog, " 正在定位...");
//		if (mLocClient != null && mLocClient.isStarted()){
//			BMapUtil.getInstance().setLocationOption(mLocClient);
//			mLocClient.requestLocation();	
//		}else{
//			BMapUtil.getInstance().setLocationOption(mLocClient);
//			mLocClient.start();
//		}
	}
	
	public void stopGetLocation(){
		defaultShowDialog = false;
//		showOrHideProgress(false," 正在获取数据...");
//		if(mLocClient != null && mLocClient.isStarted()){
//			mLocClient.stop();
//		}
	}
   
}
