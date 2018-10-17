package com.do1.cjmobileoa.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cjwsjy.app.SmApplication;
import com.cjwsjy.app.utils.HttpClientUtil;
import com.cjwsjy.app.utils.StringHelper;
import com.cjwsjy.app.utils.UrlUtil;
import com.do1.cjmobileoa.db.model.DepartmentEmployeeVO;
import com.do1.cjmobileoa.db.model.DepartmentVO;
import com.do1.cjmobileoa.db.model.EmployeeVO;
import com.do1.cjmobileoa.parent.util.JsonUtil;

/**
 * 加载数据的帮助类，，只要是用于数据的同步，获得服务端的所有数据
 */
public class GetServiceData 
{
	public static String http_result_key = "HTTP.RESULT.VLUEKEY";

	public static boolean fillAllServiceData(DBManager db) 
	{
		boolean result = false;
		
		// 通过接口地址获取服务端所有数据
		deleteAllData(db);
		UrlUtil.g_count =+5;
		
		String url = UrlUtil.HOST+"/CEGWAPServer/txl.json";
		//String url = SmApplication.server_ip+"/CEGWAPServer/txl.json";
		String resultStr = HttpClientUtil.HttpUrlConnectionGet(url, "UTF-8");
		//String resultStr = String.valueOf(resultMap.get(http_result_key));

		UrlUtil.g_count++;
		if (StringHelper.isEmpty(resultStr))
		{
			android.util.Log.d("cjwsjy", "------false-------fillAllServiceData");
			return false;
		}
		
		try 
		{
			//部门数据
			JSONObject jsonObj = new JSONObject(resultStr);
			JSONArray datas = jsonObj.getJSONArray("orgList");
			// 获得部门数据
			result = getDepartmentDate(db,datas);
			UrlUtil.g_count++;
			
			//员工数据
			datas = jsonObj.getJSONArray("userList");
			UrlUtil.g_count++;
			
			// 获得所有员工数据
			result = getEmployeeDate(db,datas);
			UrlUtil.g_count++;
			
			//关联表数据
			datas = jsonObj.getJSONArray("user_orgList");
			// 获得员工关联表
			result = getAssociationDate(db,datas);
			UrlUtil.g_count++;
		}
		catch (JSONException e) {
			// TODO Auto-generated catch block
			// Log.e("数据解析异常" + e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// Log.e("数据异常" + e);
		}
		android.util.Log.d("cjwsjy", "------end-------fillAllServiceData");
		// TODO ZHQ
		if(result)
		{
	    	SmApplication.initEmployee();
		}
		
		return result;

	}

	public static void deleteAllData(DBManager db) 
	{
		db.deleteAlldata();
	}

	public static boolean getDepartmentDate( DBManager db, JSONArray datas ) 
	{
		try
		{
			int i = 0;
			int length = 0;
			
			length = datas.length();
			
			List<DepartmentVO> departmentVos = new ArrayList();
			for( i=0; i<length; i++) 
			{
				DepartmentVO datadp = new DepartmentVO();
				
				JSONObject entity = datas.getJSONObject(i);
				//departmentVos.add(JsonUtil.json2Bean(entity,DepartmentVO.class));
				
				fillDepartmentDate(datadp,entity);
				departmentVos.add(datadp);
			}
			
			if( departmentVos.size()>0 ) 
			{
				db.addDepartmentList(departmentVos);
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			// Log.e("数据解析异常" + e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// Log.e("数据异常" + e);
		}

		return true;
	}

	//填充部门数据
	private static boolean fillDepartmentDate( DepartmentVO dp, JSONObject entity )
	{
		int value = 0;
		String data;
		
		try
		{
			data = entity.getString("DEPTID");
			dp.setDeptId(data);
			
			data = entity.getString("NAME");
			dp.setDeptName(data);
			
			data = entity.getString("DISPLAYNAME");
			dp.setDeptDisplayname(data);
			
			data = entity.getString("PARENTID");
			dp.setDeptParentid(data);
			
			data = entity.getString("ISCOMPANY");
			dp.setisCompany(data);
			
			data = entity.getString("ISSUBCOMPANY");
			dp.setisSubCompany(data);
			
			data = entity.getString("WEBURL");
			dp.setwebURL(data);
			
			data = entity.getString("FAX");
			dp.setfax(data);
			
			data = entity.getString("TEL");
			dp.settel(data);
			
			data = entity.getString("ADDRESS");
			dp.setaddress(data);
			
			data = entity.getString("STATE");
			dp.setstate(data);
			
			data = entity.getString("REMARK");
			dp.setremark(data);
			
			value = entity.getInt("SORT");
			dp.setSortIndex(value);
			
			data = entity.getString("ORGCODE");
			dp.setorgCode(data);
			
		}
		catch (JSONException e)
		{
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
        
		return true;
	}
	
	// 获得员工信息数据
	public static boolean getEmployeeDate( DBManager db, JSONArray datas ) 
	{
		try
		{
			int i = 0;
			int length = 0;
			int size = 0;

			length = datas.length();

			// 遍历
			List<EmployeeVO> employeeVos = new ArrayList();
			for (i = 0; i < length; i++)
			{
				EmployeeVO datadp = new EmployeeVO();
				JSONObject entity = datas.getJSONObject(i);

				// 取值
				fillEmployeeDate(datadp, entity);
				// 加入list
				employeeVos.add(datadp);
			}

			if (employeeVos.size() > 0)
			{
				db.addEmployeeList(employeeVos);
			}

			if (employeeVos.size() > 0)
			{
				// 记录createtime，当做数据的版本号
				String ct = employeeVos.get(0).getCreatetime();
				// TODO ZHQ
				SmApplication.sharedProxy.putString("createtime", ct);
				SmApplication.sharedProxy.commit();
			}
		}
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			// Log.e("数据解析异常" + e);
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			// Log.e("数据解密异常" + e);
		}

		return true;
	}

	//填充员工数据
	private static boolean fillEmployeeDate(EmployeeVO dp, JSONObject entity)
	{
		int value = 0;
		String data;

		try
		{
			
			data = entity.getString("OFFICEADDRESS");
			dp.setOfficeAddress(data);
			
			data = entity.getString("USERID");
			dp.setUserid(data);

			data = entity.getString("USERNAME");
			dp.setUsername(data);

			data = entity.getString("USERDISPLAYNAME");
			dp.setUserDisplayName(data);

			data = entity.getString("JOBNUMBER");
			dp.setjobNumber(data);

			data = entity.getString("PHOTO");
			dp.setPhoto(data);

			data = entity.getString("GENDER");
			dp.setgender(data);

			data = entity.getString("USERSTATE");
			dp.setuserState(data);

			data = entity.getString("POSTDUTY");
			dp.setpostDuty(data);

			data = entity.getString("EMAIL");
			dp.setEmail(data);

			data = entity.getString("IMACCOUNTS");
			dp.setimAccounts(data);

			data = entity.getString("MOBILE");
			dp.setmobile(data);

			data = entity.getString("MOBILESHORTNUMBER");
			dp.setmobileShortNumber(data);

			data = entity.getString("MOBILEIPHONE");
			dp.setMobileIphone(data);

			data = entity.getString("MOBILEIPHONESHORTNUMBER");
			dp.setmobileIphoneShortNumber(data);
			
			data = entity.getString("TELEPHONE");
			dp.setTelephone(data);
			
			data = entity.getString("OFFICETELSHORT");
			dp.setofficeTelShort(data);

		}
		catch (JSONException e)
		{
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}

		return true;
	}

	//员工和部门关联表
	public static boolean  getAssociationDate( DBManager db, JSONArray datas )
	{
		try
		{
			int i = 0;
			int length = 0;

			length = datas.length();

			// 遍历
			List<DepartmentEmployeeVO> departempVos = new ArrayList();
			for (i = 0; i < length; i++)
			{
				DepartmentEmployeeVO datadp = new DepartmentEmployeeVO();
				JSONObject entity = datas.getJSONObject(i);

				// 取值
				fillAssociationDate(datadp, entity);
				// 加入list
				departempVos.add(datadp);
			}

			if (departempVos.size() > 0)
			{
				db.addAssociationList(departempVos);
			}
		}
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			// Log.e("数据解析异常" + e);
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			// Log.e("数据解密异常" + e);
		}
		return true;
	}
	
	//填充部门员工数据
	private static boolean fillAssociationDate(DepartmentEmployeeVO de, JSONObject entity)
	{
		int value = 0;
		String data;

		try
		{
			data = entity.getString("USER_RID");
			de.setuserRID(data);

			data = entity.getString("ORG_RID");
			de.setorgRID(data);

			data = entity.getString("USERTITLE");
			de.setuserTitle(data);
			
			value = entity.getInt("USERSORT");
			de.setuserSort(value);
		}
		catch (JSONException e)
		{
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return true;
	}
		
	/**
	 * 循环获取下级部门
	 */
	private static void getLevelDepData(List<DepartmentVO> entitys, JSONArray datas)
	{
		for (int idx = 0; idx < datas.length(); idx++)
		{
			JSONObject jso;
			try
			{
				jso = (JSONObject) datas.get(idx);
				entitys.add(JsonUtil.json2Bean(jso, DepartmentVO.class));

				JSONArray depLists = jso.getJSONArray("depList");
				if (depLists.length() > 0)
				{
					getLevelDepData(entitys, depLists);
				}
			}
			catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
