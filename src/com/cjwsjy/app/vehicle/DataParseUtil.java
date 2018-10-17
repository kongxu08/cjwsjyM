package com.cjwsjy.app.vehicle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Handler;
import cn.com.do1.dqdp.android.common.ContactInfo;
import cn.com.do1.dqdp.android.common.ContactUtil;

import com.cjwsjy.app.utils.ValidUtil;

public class DataParseUtil {

	public ContactUtil contactUtil;
	public Context context;
	public DataParseUtil(Context context){
		this.context = context;
		contactUtil = new ContactUtil(context);
	}
	
	public List<Map<String, Object>> mapList = new ArrayList<Map<String,Object>>();
	public List<ContactInfo> infolist = new ArrayList<ContactInfo>();
	
	/**
	 * 解析数据（传入result）
	 * @param result
	 */
	public void analyData(final ResultObject result,final Handler h){
    	new Thread() {
            @Override
            public void run() {
            	mapList.clear();
            	infolist.clear();
            	mapList =result.getListMap();
                for (int i = 0; i < mapList.size(); i++) {
                    Map<String,Object> map = mapList.get(i);
                    String name = map.get("personName")+"";
                    ContactInfo info = new ContactInfo(name);//名称
                    info.setDeptName(map.get("deptName")+"");//部门
                    info.setPosition(map.get("position")+"");//职位
                    info.setShortMobile(map.get("shortMobile") + "");//短号
                    
                    //手机号码  
                    List<ContactInfo.PhoneInfo> phoneList = new ArrayList<ContactInfo.PhoneInfo>();
                    ContactInfo.PhoneInfo phone = new ContactInfo.PhoneInfo();
                    phone.number = map.get("mobile") + "";
                    phoneList.add(phone);
                    info.setPhoneList(phoneList);
                    
                    //邮箱
                    List<ContactInfo.EmailInfo> emailList = new ArrayList<ContactInfo.EmailInfo>();
                    ContactInfo.EmailInfo email = new ContactInfo.EmailInfo();
                    email.email = map.get("email") + "";
                    emailList.add(email);
                    info.setEmail(emailList);
                    
                    infolist.add(info);
                }
//                spitList(infolist);
                h.sendEmptyMessage(4);
            }
        }.start();
    }
	
	/**
	 * 解析数据(传入list)
	 * @param result
	 */
	public void analyDataList(final List<Map<String, Object>> list,final Handler h){
    	new Thread() {
            @Override
            public void run() {
            	mapList.clear();
            	mapList.addAll(list);
                List<ContactInfo> infolist = new ArrayList<ContactInfo>();
                for (int i = 0; i < mapList.size(); i++) {
                    Map<String,Object> map = mapList.get(i);
                    if(BaseActivity.uservo.getMobile().equals(map.get("mobile")+"")){
                    	continue;
                    }
                    String name = map.get("personName")+"";
                    ContactInfo info = new ContactInfo(name);//名称
                    info.setDeptName(map.get("deptName")+"");//部门
                    info.setPosition(map.get("position")+"");//职位
                    info.setShortMobile(map.get("shortMobile") + "");//短号                    
                    
                    //手机号码
                    List<ContactInfo.PhoneInfo> phoneList = new ArrayList<ContactInfo.PhoneInfo>();
                    ContactInfo.PhoneInfo phone = new ContactInfo.PhoneInfo();
                    phone.number = map.get("mobile") + "";
                    phoneList.add(phone);
                    info.setPhoneList(phoneList);
                    
                    //邮箱
                    List<ContactInfo.EmailInfo> emailList = new ArrayList<ContactInfo.EmailInfo>();
                    ContactInfo.EmailInfo email = new ContactInfo.EmailInfo();
                    email.email = map.get("email") + "";
                    emailList.add(email);
                    info.setEmail(emailList);
                    
                    infolist.add(info);
                }
                spitList(infolist);
                
                h.sendEmptyMessage(4);
            }
        }.start();
    }
    
    public List<ContactInfo> addlist = new ArrayList<ContactInfo>();
    public List<ContactInfo> updatelist = new ArrayList<ContactInfo>();
    
    /**
     * 解析列表
     * @param list
     */
    public void spitList(List<ContactInfo> list){
    	addlist.clear();
    	updatelist.clear();
    	List<ContactInfo> locallist = contactUtil.queryContactAll();
    	
//    	List<ContactInfo> addlist = new ArrayList<ContactInfo>();
//        List<ContactInfo> updatelist = new ArrayList<ContactInfo>();
    	
    	for(ContactInfo listinfo : list){
    		int count = 0;
    		for(ContactInfo localinfo : locallist){
    			if(localinfo != null && localinfo.getPhoneList() != null && localinfo.getPhoneList().size() > 0){
    				String listMobile = listinfo.getPhoneList().get(0).number;
    				String localMobile = localinfo.getPhoneList().get(0).number;
    				
    				String repsmobile = ValidUtil.getOnlyMobile(listMobile);
					String recmobile = ValidUtil.getOnlyMobile(localMobile);
    				if(repsmobile.equals(recmobile) && repsmobile.length() > 0){
    					count = 1; 
    					listinfo.getPhoneList().get(0).number = localMobile;
    					updatelist.add(listinfo);
    					break;
    				}
    			}
    		}
    		if(count == 0){
    			addlist.add(listinfo);
    		}
    	}
    	
    	//Log.e("SynPersonToLocal", "新增记录条数：" + addlist.size() + "条");
    	//Log.e("SynPersonToLocal", "修改记录条数：" + updatelist.size() + "条");
    	/**
    	 * 插入联系人
    	 */
//    	for(ContactInfo info : addlist){
//    		contactUtil.addLocalContact(info);
//    	}
    	
//    	/**
//    	 * 修改联系人
//    	 */
//    	for(ContactInfo info : updatelist){
//    		contactUtil.updateAndAddContact(info);
//    	}
    	
    }
    
    /**
     * 获取列表展示的list
     * @return
     */
    public List<Map<String, Object>> getDataList(){
    	List<Map<String, Object>> datalist = new ArrayList<Map<String,Object>>();
    	if(updatelist.size() > 0){
    		//匹配
    		datalist.clear();
    		for(ContactInfo info : updatelist){
    			String infomobile = info.getPhoneList().get(0).number.replace(" ", "");
    			for(Map<String, Object> map : mapList){
    	    		String mapmobile = map.get("mobile")+"";

    	    		String repsmobile = ValidUtil.getOnlyMobile(infomobile);
					String recmobile = ValidUtil.getOnlyMobile(mapmobile);
    				if(repsmobile.equals(recmobile) && repsmobile.length() > 0){
    					map.put("oldMobile", info.getPhoneList().get(0));
    					map.put("oldShortMobile", info.getShortMobile());
    					map.put("oldPersonName", info.getName());
    					boolean hasEmail = info.getEmail() != null && info.getEmail().size() > 0;
    					map.put("oldEmail", hasEmail ? info.getEmail().get(0) : "");
    					map.put("oldPosition", info.getPosition());
    					map.put("oldDepart", info.getDeptName());
    					datalist.add(map);
    	    			break;
    				}
    			}
    		}
    	}
    	return datalist;
    }
    
    /**
     * 将list转换成info
     * @param list
     */
    public List<ContactInfo> setMap2Info(List<Map<String, Object>> list){
    	List<ContactInfo> infolist = new ArrayList<ContactInfo>();
        for (int i = 0; i < list.size(); i++) {
            Map<String,Object> map = list.get(i);
            if(BaseActivity.uservo.getMobile().equals(map.get("mobile")+"")){
            	continue;
            }
            String name = map.get("personName")+"";
            ContactInfo info = new ContactInfo(name);//名称
            info.setDeptName(map.get("deptName")+"");//部门
            info.setPosition(map.get("position")+"");//职位
            info.setShortMobile(map.get("shortMobile") + "");//短号
            
            //手机号码
            List<ContactInfo.PhoneInfo> phoneList = new ArrayList<ContactInfo.PhoneInfo>();
            ContactInfo.PhoneInfo phone = new ContactInfo.PhoneInfo();
            phone.number = map.get("mobile") + "";
            phoneList.add(phone);
            info.setPhoneList(phoneList);
            
            //邮箱
            List<ContactInfo.EmailInfo> emailList = new ArrayList<ContactInfo.EmailInfo>();
            ContactInfo.EmailInfo email = new ContactInfo.EmailInfo();
            email.email = map.get("email") + "";
            emailList.add(email);
            info.setEmail(emailList);
            
            infolist.add(info);
        }
        return infolist;
    }
}
