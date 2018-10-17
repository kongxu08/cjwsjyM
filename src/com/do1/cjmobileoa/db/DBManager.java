package com.do1.cjmobileoa.db;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.cjwsjy.app.utils.StringHelper;
import com.do1.cjmobileoa.db.model.BiaozhunVO;
import com.do1.cjmobileoa.db.model.CollectVO;
import com.do1.cjmobileoa.db.model.DepartmentEmployeeVO;
import com.do1.cjmobileoa.db.model.DepartmentVO;
import com.do1.cjmobileoa.db.model.EmployeeVO;
import com.do1.cjmobileoa.db.model.HistoryVO;

public class DBManager {
	private DBHelper helper;
	private SQLiteDatabase db;
	public static String EMPLOYEE = "db_employee";
	public static String DEPARTMENT = "db_department";
	public static String HISTORY = "db_history";
	public static String COLLECT = "db_collect";
	public static String USERORG  = "db_UserOrganization";

	public DBManager(Context context) 
	{
		helper = new DBHelper(context);
		
		// 因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0, mFactory);
		// 所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里
		db = helper.getWritableDatabase();
	}

	/**
	 * 查询部门信息
	 * 
	 * @return
	 */
	public DepartmentVO findDeptInfo(String deptId) {
		DepartmentVO data = null;
		Cursor c = null;
		try {
			String sql = "select * from " + DEPARTMENT + " where deptId = ?  ";
			c = db.rawQuery(sql, new String[] { deptId });
			while (c.moveToNext()) {
				data = new DepartmentVO();
				data.setId(c.getString(c.getColumnIndex("id")));
				data.setDeptDisplayname(c.getString(c
						.getColumnIndex("deptDisplayname")));
				data.setDeptId(c.getString(c.getColumnIndex("deptId")));
				data.setDeptName(c.getString(c.getColumnIndex("deptName")));
				data.setDeptParentid(c.getString(c
						.getColumnIndex("deptParentid")));
				data.setSortIndex(Integer.parseInt(c.getString(c
						.getColumnIndex("sortIndex"))));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (c != null) {
				c.close();
				c = null;
			}
		}
		return data;
	}

	/**
	 * 指定部门下搜索人员
	 */
	public List<EmployeeVO> searchByDept(String deptParentId, String name) {
		List<EmployeeVO> list = new ArrayList<EmployeeVO>();
		String sql = "select * from "
				+ EMPLOYEE
				+ " where department = ?  and userDisplayName like ? order by userSortIndex ";
		Cursor c = db.rawQuery(sql, new String[] { deptParentId,
				"%" + name + "%" });
		while (c.moveToNext()) {
			EmployeeVO data = getEmployeeVOData(c);
			list.add(data);
		}
		
		c.close();
		return list;
	}

	/***
	 * =====================================操作本地部门和人员
	 * 数据===================================================== 查询所有的公司 return
	 * 公司下的部门
	 */
	public List<DepartmentVO> findDepartments(String deptParentId) 
	{
		List<DepartmentVO> list = new ArrayList<DepartmentVO>();
		String sql = "select * from " + DEPARTMENT + " where deptParentid = ?  order by sortIndex ";
		Cursor c = db.rawQuery(sql, new String[] { deptParentId });

		while (c.moveToNext()) 
		{
			DepartmentVO data = new DepartmentVO();
			data.setId(c.getString(c.getColumnIndex("id")));
			data.setDeptDisplayname(c.getString(c.getColumnIndex("deptDisplayname")));
			data.setDeptId(c.getString(c.getColumnIndex("deptId")));
			data.setDeptName(c.getString(c.getColumnIndex("deptName")));
			data.setDeptParentid(c.getString(c.getColumnIndex("deptParentid")));
			data.setSortIndex(Integer.parseInt(c.getString(c.getColumnIndex("sortIndex"))));
			list.add(data);
		}
		c.close();
		return list;
	}

	/***
	 * 查询所有的员工
	 */
	public List<EmployeeVO> findAllEmployee() {

		List<EmployeeVO> list = new ArrayList<EmployeeVO>();
		String sql = "select * from " + EMPLOYEE + " order by userSortIndex  ";
		Cursor c = db.rawQuery(sql, null);
		while (c.moveToNext()) 
		{
			EmployeeVO data = getEmployeeVOData(c);
			list.add(data);
		}
		c.close();
		return list;
	}

	/***
	 * 根据部门id查询所有的员工 return 公司下的部门
	 */
	public List<EmployeeVO> findEmployeeBydept(String deptId, int debug)
	{
		List<EmployeeVO> listem = new ArrayList<EmployeeVO>();
		List<DepartmentEmployeeVO> listde = new ArrayList<DepartmentEmployeeVO>();
		
		String userid;
		String sql;
		Cursor c;
		
		//android.util.Log.d("cjwsjy", "------deptId="+deptId+"---de="+debug+"----findEmployeeBydept");
		//在关联表中查出符合条件的用户集合
		sql = "select * from " + "db_UserOrganization" + " where orgRID = ? order by userSort";
		c = db.rawQuery(sql, new String[]{ deptId });
		while (c.moveToNext())
		{
			DepartmentEmployeeVO data = getDepartmentEmpVOData(c);
			listde.add(data);
		}

		for( DepartmentEmployeeVO data : listde )
		{
			userid = data.getuserRID();
			sql = "select * from " + "db_employee" + " where userId = ?";
			c = db.rawQuery(sql, new String[]{ userid });
			
			while (c.moveToNext())
			{
				EmployeeVO data2 = getEmployeeVOData(c);
				listem.add(data2);
			}
		}
		c.close();
		
		return listem;
	}

	/***
	 * 根据id查询指定员工信息
	 */
	public EmployeeVO findEmployeeById(String userId) 
	{
		EmployeeVO data = new EmployeeVO();
		String sql = "select * from " + EMPLOYEE + " where userId = ? ";
		Cursor c = db.rawQuery(sql, new String[] { userId });
		while (c.moveToNext()) {
			data = getEmployeeVOData(c);
			break;
		}
		c.close();
		return data;
	}
	
	/***
	 * 根据username查询指定员工信息
	 */
	public EmployeeVO getEmployeeByName(String username) 
	{
		EmployeeVO data = new EmployeeVO();
		String sql = "select * from " + EMPLOYEE + " where username = ? ";
		Cursor c = db.rawQuery(sql, new String[] { username });

		while (c.moveToNext())
		{
			data = getEmployeeVOData(c);
			//userId =data.getUserid();
			break;
		}
		c.close();
		return data;
	}

	/***
	 * 根据电话号码查询指定员工信息
	 */
	public List<EmployeeVO> getEmployeeByPhone(String PhoneNumber)
	{
		List<EmployeeVO> list = new ArrayList<EmployeeVO>();
		EmployeeVO data = new EmployeeVO();

		String sql = "select * from " + EMPLOYEE + " where phoneNumber like ? or phoneShortNumber like ?"
					+"or telephone like ? or mobileIphone like ? or mobileIphoneShortNumber like ?";
		Cursor c = db.rawQuery(sql, new String[] { "%"+PhoneNumber+"%","%"+PhoneNumber+"%","%"+PhoneNumber+"%","%"+PhoneNumber+"%","%"+PhoneNumber+"%" });

		while (c.moveToNext())
		{
			data = getEmployeeVOData(c);

			android.util.Log.i("cjwsjy", "------name="+data.getUserDisplayName()+"-------dbmanager");
			list.add(data);
		}
		c.close();

		sql = "select * from " + EMPLOYEE + " where mobileIphone = ? ";

		//办公室电话
		sql = "select * from " + EMPLOYEE + " where telephone = ? ";

		//短号
		sql = "select * from " + EMPLOYEE + " where phoneShortNumber = ? ";

		sql = "select * from " + EMPLOYEE + " where mobileIphoneShortNumber = ? ";

		return list;
	}

	/***
	 * 查询机构数据根据id
	 */
	public DepartmentVO findOrganizationbyid(String deptId)
	{
		boolean bresult = false;
		DepartmentVO data = new DepartmentVO();
		String sql;
		Cursor c = null;
		
		while( bresult==false )
		{
			sql = "select * from " + DEPARTMENT + " where deptId = ? ";
			c = db.rawQuery(sql, new String[] { deptId });
			while (c.moveToNext()) 
			{
				data = getDepartmentVOData(c);
				break;
			}
			deptId = data.getDeptParentid();
			bresult = deptId.equals("A90AEAEC-E3D4-43DE-BB67-85407B57B171");
		}
		
		c.close();
		return data;
	}
	
	/***
	 * 查询部门数据根据id
	 */
	public DepartmentVO findDepartmentbyid(String deptId)
	{
		DepartmentVO data = new DepartmentVO();
		String sql = "select * from " + DEPARTMENT + " where deptId = ? ";
		Cursor c = db.rawQuery(sql, new String[] { deptId });
		while (c.moveToNext()) 
		{
			data = getDepartmentVOData(c);
			//userId =data.getUserid();
			break;
		}
		c.close();
		return data;
	}
	
	/***
	 * 根据userid查询指定员工的部门
	 */
	public DepartmentEmployeeVO findOrgsbyUserid(String userid)
	{
		DepartmentEmployeeVO data = new DepartmentEmployeeVO();
		String sql = "select * from " + USERORG + " where userRID = ? ";
		Cursor c = db.rawQuery(sql, new String[] { userid });
		while (c.moveToNext()) 
		{
			data = getDepartmentEmpVOData(c);
			//userId =data.getUserid();
			break;
		}
		c.close();
		return data;
	}
	
	/***
	 * 根据username查询指定员工的部门
	 */
	public List<DepartmentVO> findUserOrgs(String username){
		List<DepartmentVO> list = new ArrayList<DepartmentVO>();
		EmployeeVO user = getEmployeeByName(username);
		String userId = user.getUserid();
		String sql = "select * from db_department org , db_UserOrganization uo where  uo.userRID= ? and org.deptId=uo.orgRID ";
		Cursor c = db.rawQuery(sql, new String[] { userId });
		while (c.moveToNext()) {
			DepartmentVO data = getDepartmentVOData(c);
			list.add(data);
		}
		c.close();
		return list;
	} 
	
	public boolean isNumeric(String str)
	{
		for (int i = str.length(); --i >= 0;)
		{
			int chr = str.charAt(i);
			if (chr < 48 || chr > 57)
				return false;
		}
		return true;
	}
	
	public boolean isNum(String str)
	{
		return str.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$");
	}
	
	public boolean isEnglish(String str)
	{
		for (int i = 0; i < str.length(); i++) 
		{
            if (!(str.charAt(i) >= 'A' && str.charAt(i) <= 'Z')  
                    && !(str.charAt(i) >= 'a' && str.charAt(i) <= 'z')) 
            {
                return false;  
            }  
        }  
        return true; 
	}
	
	/***
	 * 根据名称（userDisplayName）模糊查询员工信息，返回查找到的结果集
	 */
	public List<EmployeeVO> findEmployeeByKey(String strkey) 
	{
		boolean bresult1 = false;
		boolean bresult2 = false;
		int count = 0;
		int leng = 0;
		List<EmployeeVO> list = new ArrayList<EmployeeVO>();
		String sql;
		Cursor c;
		Cursor c2;
		
		bresult1 = isNumeric(strkey);
		bresult2 = isEnglish(strkey);
		if( bresult1==true )
		{
			//数字
			leng = strkey.length();
			//长度大于3才查找
			if( leng<3 ) return list;

			sql = "select * from " + EMPLOYEE + " where phoneNumber like ? or mobileIphoneShortNumber like ? or mobileIphone like ? order by userSortIndex  ";
			c = db.rawQuery( sql,new String[]{ "%" + strkey + "%", "%" + strkey + "%", "%" + strkey + "%" });
			
//			sql = "select * from " + EMPLOYEE + " where phoneNumber like ?  order by userSortIndex  ";
//			c = db.rawQuery(sql,new String[] { "%" + strkey + "%" });
//			
//			sql = "select * from " + EMPLOYEE + " where mobileIphone like ?  order by userSortIndex  ";
//			c2 = db.rawQuery(sql,new String[] { "%" + strkey + "%" });
//			while (c2.moveToNext()) 
//			{
//				EmployeeVO data = getEmployeeVOData(c2);
//				list.add(data);
//			}
//			c2.close();
		}
		else if( bresult2==true )
		{
			//拼音
			leng = strkey.length();
			//长度大于3才查找
			if( leng<2 ) return list;
			sql = "select * from " + EMPLOYEE + " where email like ?  order by userSortIndex  ";
			c = db.rawQuery(sql,new String[] { "%" + strkey + "%" });
		}
		else
		{
			//汉字
			sql = "select * from " + EMPLOYEE + " where userDisplayName like ?  order by userSortIndex  ";
			c = db.rawQuery(sql,new String[] { "%" + strkey + "%" });
		}
		//String sql = "select * from " + EMPLOYEE + " where userDisplayName like ?  order by userSortIndex  ";
		//Cursor c = db.rawQuery(sql,new String[] { "%" + strkey + "%" });
		
		while (c.moveToNext()) 
		{
			EmployeeVO data = getEmployeeVOData(c);
			list.add(data);
		}

		c.close();
		return list;
	} 
	
	/***
	 * 根据名称（userDisplayName）模糊查询员工信息
	 */
	public List<EmployeeVO> findEmployeeByName(String userDisplayName) 
	{
		List<EmployeeVO> list = new ArrayList<EmployeeVO>();
		String sql = "select * from " + EMPLOYEE + " where userDisplayName like ?  order by userSortIndex  ";
		Cursor c = db.rawQuery(sql,new String[] { "%" + userDisplayName + "%" });
		while (c.moveToNext()) 
		{
			EmployeeVO data = getEmployeeVOData(c);
			list.add(data);
		}
		c.close();
		return list;
	}

	//返回成功还是失败
	public int findEmployeeByName2(String username)
	{
		int result = 0;
		EmployeeVO data = new EmployeeVO();
		String sql = "select * from " + EMPLOYEE + " where username = ? ";
		Cursor c = db.rawQuery(sql, new String[] { username });
		while (c.moveToNext())
		{
			result = 1;
			data = getEmployeeVOData(c);
			//userId =data.getUserid();
			break;
		}
		c.close();

		return result;
	}

	//往档案管理数据库中插入数据
	public int insertDangan(String username)
	{
		int result = 0;
		String strname = "";

		db.beginTransaction(); // 开始事务
		db.execSQL("INSERT INTO db_dangan VALUES(null,?,?,?,?,?,?)",
				new Object[] { "",username, username,"0","",""} );
		db.setTransactionSuccessful();
		db.endTransaction();

		return result;
	}

	//往档案管理数据库中修改数据
	public int updateDangan(String strid, String newname)
	{
		int result = 0;
		//String strid = "";
		String strname = "";

		//strid = Integer.toString(index);

		db.beginTransaction(); // 开始事务
		db.execSQL("UPDATE db_dangan SET newName=? where _id=?",new Object[] { newname, strid} );
		db.setTransactionSuccessful();
		db.endTransaction();

		return result;
	}

	//在档案管理数据库中删除数据
	public int DeleteDangan(String strid)
	{
		int result = 0;
		//String strid = "";
		String strname = "";

		//strid = Integer.toString(index);

		db.beginTransaction(); // 开始事务
		db.execSQL("DELETE FROM db_dangan where _id=?",new Object[] { strid} );
		db.setTransactionSuccessful();
		db.endTransaction();

		return result;
	}

	//返回 1 成功， 0 失败
	//查找档案管理数据库文件是否存在
	public int findDanganByOldName(String username)
	{
		int result = 0;
		String strname = "";
		String sql = "select * from db_dangan where oldname = ? ";
		Cursor c = db.rawQuery(sql, new String[] { username });
		while (c.moveToNext())
		{
			result = 1;
			strname = c.getString(c.getColumnIndex("oldName"));

			break;
		}
		c.close();

		return result;
	}

	//0 失败; 1 成功
	public int findDanganByNewName(String username)
	{
		int result = 0;
		String strname = "";
		String sql = "select * from db_dangan where newName = ? ";
		Cursor c = db.rawQuery(sql, new String[] { username });
		while (c.moveToNext())
		{
			result = 1;
			strname = c.getString(c.getColumnIndex("oldName"));

			break;
		}
		c.close();

		return result;
	}

	public String findDanganById(String strId)
	{
		int result = 0;
		String strname = "";
		String sql = "select * from db_dangan where _id = ? ";
		Cursor c = db.rawQuery(sql, new String[] { strId });
		while (c.moveToNext())
		{
			result = 1;
			strname = c.getString(c.getColumnIndex("oldName"));

			break;
		}
		c.close();

		return strname;
	}

	//查询档案管理数据库
	public List<BiaozhunVO> SelectDangan()
	{
		List<BiaozhunVO> listem = new ArrayList<BiaozhunVO>();

		//List<CollectVO> list = new ArrayList<CollectVO>();
		String sql = "select * from db_dangan";
		Cursor c = db.rawQuery(sql, null);
		while (c.moveToNext())
		{
			BiaozhunVO data = new BiaozhunVO();
			data.setId(c.getString(c.getColumnIndex("_id")));
			data.setOldname(c.getString(c.getColumnIndex("oldName")));
			data.setNewname(c.getString(c.getColumnIndex("newName")));
			data.setIsdelete(c.getString(c.getColumnIndex("isDelete")));

			listem.add(data);
		}
		c.close();
		return listem;
	}

	//返回 1 成功， 0 失败
	//查找标准管理数据库文件是否存在
	public int findBiaozhunByName(String username)
	{
		int result = 0;
		String strname = "";
		String sql = "select * from db_biaozhun where oldname = ? ";
		Cursor c = db.rawQuery(sql, new String[] { username });
		while (c.moveToNext())
		{
			result = 1;
			strname = c.getString(c.getColumnIndex("oldName"));

			break;
		}
		c.close();

		return result;
	}

	//往标准管理数据库中插入数据
	public int insertBiaozhun(String username)
	{
		int result = 0;
		String strname = "";

		db.beginTransaction(); // 开始事务
		db.execSQL("INSERT INTO db_biaozhun VALUES(null,?,?,?,?,?,?)",
				new Object[] { "",username, username,"0","",""} );
		db.setTransactionSuccessful();
		db.endTransaction();

		return result;
	}

	//往标准管理数据库中修改数据
	public int updateBiaozhun(int index, String newname)
	{
		int result = 0;
		String strid = "";
		String strname = "";

		strid = Integer.toString(index);

		db.beginTransaction(); // 开始事务
		db.execSQL("UPDATE db_biaozhun SET newName=? where _id=?",new Object[] { newname, strid} );
		db.setTransactionSuccessful();
		db.endTransaction();

		return result;
	}

	//往标准管理数据库中删除数据
	public int DeleteBiaozhun(String oldname )
	{
		int result = 0;
		String strname = "";

		db.beginTransaction(); // 开始事务
		db.execSQL("DELETE from db_biaozhun where oldName=?",new Object[] { oldname } );
		db.setTransactionSuccessful();
		db.endTransaction();

		return result;
	}

	//查询标准管理数据库
	public List<BiaozhunVO> findBiaozhun()
	{
		List<BiaozhunVO> listem = new ArrayList<BiaozhunVO>();

		//List<CollectVO> list = new ArrayList<CollectVO>();
		String sql = "select * from db_biaozhun";
		Cursor c = db.rawQuery(sql, null);
		while (c.moveToNext())
		{
			BiaozhunVO data = new BiaozhunVO();
			data.setId(c.getString(c.getColumnIndex("_id")));
			data.setOldname(c.getString(c.getColumnIndex("oldName")));
			data.setNewname(c.getString(c.getColumnIndex("newName")));
			data.setIsdelete(c.getString(c.getColumnIndex("isDelete")));

			listem.add(data);
		}
		c.close();
		return listem;
	}

	/***
	 * 根据名称（phoneNumber）模糊查询员工信息
	 * 
	 * @param currentPage
	 *            当前页 　　
	 * @param pageSize
	 *            每页显示的记录 　
	 */

	public List<EmployeeVO> findEmployeeByPhone(String phoneNumber,
			int currentPage, int pageSize) {

		if (StringHelper.isEmpty(phoneNumber))
			return new ArrayList<EmployeeVO>();

		int firstResult = (currentPage - 1) * pageSize;

		String sql = "select count(*) from " + EMPLOYEE
				+ " where phoneNumber like ?  ";
		Cursor c = db.rawQuery(sql, new String[] { "%" + phoneNumber + "%" });
		c.moveToFirst();
		int dataCount = c.getInt(0);

		List<EmployeeVO> list = new ArrayList<EmployeeVO>();
		sql = "select * from "
				+ EMPLOYEE
				+ " where phoneNumber like ? order by userSortIndex   limit ?,? ";
		c = db.rawQuery(
				sql,
				new String[] { "%" + phoneNumber + "%",String.valueOf(firstResult), String.valueOf(pageSize) });

		while (c.moveToNext()) {
			EmployeeVO data = getEmployeeVOData(c);
			data.setDataCount(dataCount);
			list.add(data);
		}
		// 不要关闭数据库
		// c.close();
		return list;
	}

	private DepartmentEmployeeVO getDepartmentEmpVOData(Cursor c) 
	{
		DepartmentEmployeeVO data = new DepartmentEmployeeVO();
		
		data.setid(c.getString(c.getColumnIndex("id")));
		data.setuserRID(c.getString(c.getColumnIndex("userRID")));
		data.setorgRID(c.getString(c.getColumnIndex("orgRID")));
		data.setuserTitle(c.getString(c.getColumnIndex("userTitle")));
		data.setuserSort(Integer.parseInt(c.getString(c.getColumnIndex("userSort"))));
		
		return data;
	}
	
	private DepartmentVO getDepartmentVOData(Cursor c) 
	{
		DepartmentVO data = new DepartmentVO();
		data.setId(c.getString(c.getColumnIndex("id")));
		data.setDeptId(c.getString(c.getColumnIndex("deptId")));
		data.setDeptName(c.getString(c.getColumnIndex("deptName")));
		data.setDeptParentid(c.getString(c.getColumnIndex("deptParentid")));
		data.setisCompany(c.getString(c.getColumnIndex("isCompany")));
		data.setisSubCompany(c.getString(c.getColumnIndex("isSubCompany")));
		data.setwebURL(c.getString(c.getColumnIndex("webURL")));
		data.setfax(c.getString(c.getColumnIndex("fax")));
		data.settel(c.getString(c.getColumnIndex("tel")));
		data.setaddress(c.getString(c.getColumnIndex("address")));
		data.setstate(c.getString(c.getColumnIndex("state")));
		data.setremark(c.getString(c.getColumnIndex("remark")));
		data.setSortIndex(Integer.parseInt(c.getString(c.getColumnIndex("sortIndex"))));
		data.setorgCode(c.getString(c.getColumnIndex("orgCode")));
		data.setDeptDisplayname(c.getString(c.getColumnIndex("deptDisplayname")));
		return data;
	}
	
	private EmployeeVO getEmployeeVOData(Cursor c) 
	{
		EmployeeVO data = new EmployeeVO();
		data.setId(c.getString(c.getColumnIndex("id")));
		data.setDepartment(c.getString(c.getColumnIndex("department")));
		data.setUserid(c.getString(c.getColumnIndex("userId")));
		data.setjobNumber(c.getString(c.getColumnIndex("jobNumber")));
		data.setPhoto(c.getString(c.getColumnIndex("photo")));
		data.setpostDuty(c.getString(c.getColumnIndex("postDuty")));
		data.setUsername(c.getString(c.getColumnIndex("username")));
		data.setUserDisplayName(c.getString(c.getColumnIndex("userDisplayName")));
		data.setPhoneNumber(c.getString(c.getColumnIndex("phoneNumber")));
		data.setPhoneShortNumber(c.getString(c.getColumnIndex("phoneShortNumber")));
		data.setTelephone(c.getString(c.getColumnIndex("telephone")));
		data.setEmail(c.getString(c.getColumnIndex("email")));
		data.setMobilePublic(c.getString(c.getColumnIndex("mobilePublic")));
		data.setNameSpell(c.getString(c.getColumnIndex("nameSpell")));
		data.setOfficeAddress(c.getString(c.getColumnIndex("nextDepartment")));
		//data.setNextDepartment(c.getString(c.getColumnIndex("nextDepartment")));
		data.setNextDepartment("");
		data.setPreDepartment(c.getString(c.getColumnIndex("preDepartment")));
		data.setUserSortIndex(Integer.parseInt(c.getString(c.getColumnIndex("userSortIndex"))));
		data.setUserTitle(c.getString(c.getColumnIndex("userTitle")));
		data.setDeptId(c.getString(c.getColumnIndex("deptId")));
		data.setmobile(c.getString(c.getColumnIndex("phoneNumber")));
		data.setmobileShortNumber(c.getString(c.getColumnIndex("phoneShortNumber")));
		data.setMobileIphone(c.getString(c.getColumnIndex("mobileIphone")));
		data.setmobileIphoneShortNumber(c.getString(c.getColumnIndex("mobileIphoneShortNumber")));
		data.setCreatetime(c.getString(c.getColumnIndex("createtime")));
		data.setNameNumber(c.getString(c.getColumnIndex("nameNumber")));
		data.setPinyinNumber(c.getString(c.getColumnIndex("pinyinNumber")));
		return data;
	}

	/***
	 * 查询所有的历史数据
	 */
	public List<HistoryVO> findHistory(int currentPage, int pageSize) {

		int firstResult = (currentPage - 1) * pageSize;

		String sql = "select count(*) from " + HISTORY;
		Cursor c = db.rawQuery(sql, null);
		c.moveToFirst();
		int dataCount = c.getInt(0);

		List<HistoryVO> list = new ArrayList<HistoryVO>();
		sql = "select * from " + HISTORY
				+ " order by createtime desc limit ?,? ";
		c = db.rawQuery(
				sql,
				new String[] { String.valueOf(firstResult),
						String.valueOf(pageSize) });
		while (c.moveToNext()) {
			HistoryVO data = new HistoryVO();
			data.setDepartment(c.getString(c.getColumnIndex("department")));
			data.setUserid(c.getString(c.getColumnIndex("userId")));
			data.setUserDisplayName(c.getString(c
					.getColumnIndex("userDisplayName")));
			data.setPhoneNumber(c.getString(c.getColumnIndex("phoneNumber")));
			data.setEmail(c.getString(c.getColumnIndex("email")));
			data.setNameSpell(c.getString(c.getColumnIndex("nameSpell")));
			data.setCreatetime(c.getString(c.getColumnIndex("createtime")));
			data.setDataCount(dataCount);
			list.add(data);
		}
		// c.close();
		return list;
	}

	/***
	 * 查询所有的收藏数据
	 */
	public List<EmployeeVO> findCollect() {
		
		List<EmployeeVO> listem = new ArrayList<EmployeeVO>();
		
		//List<CollectVO> list = new ArrayList<CollectVO>();
		String sql = "select * from " + COLLECT + " order by createtime desc ";
		Cursor c = db.rawQuery(sql, null);
		while (c.moveToNext()) 
		{
			//CollectVO data = new CollectVO();
			EmployeeVO data = new EmployeeVO();
			//data.setDepartment(c.getString(c.getColumnIndex("department")));
			data.setUserid(c.getString(c.getColumnIndex("userID")));
			data.setpostDuty(c.getString(c.getColumnIndex("postDuty")));
			data.setUsername(c.getString(c.getColumnIndex("userName")));
			data.setUserDisplayName(c.getString(c.getColumnIndex("userDisplayName")));
			data.setjobNumber(c.getString(c.getColumnIndex("jobNumber")));
			data.setPhoneNumber(c.getString(c.getColumnIndex("mobile")));
			data.setmobile(c.getString(c.getColumnIndex("mobile")));
			data.setMobileIphone(c.getString(c.getColumnIndex("mobileIphone")));
			data.setmobileIphoneShortNumber(c.getString(c.getColumnIndex("mobileIphoneShortNumber")));
			data.setTelephone(c.getString(c.getColumnIndex("telephone")));
			data.setEmail(c.getString(c.getColumnIndex("email")));
			data.setCreatetime(c.getString(c.getColumnIndex("createtime")));
			//list.add(data);
			listem.add(data);
		}
		c.close();
		//return list;
		return listem;
	}

	/**
	 * 添加多条员工数据
	 * 
	 * @param
	 */
	public void addEmployeeList(List<EmployeeVO> employeeVo) 
	{
		db.beginTransaction(); // 开始事务
		db.execSQL("delete  from " + EMPLOYEE);
		db.setTransactionSuccessful();
		db.endTransaction();
		
		db.beginTransaction(); // 开始事务
		try {
			for (EmployeeVO entity : employeeVo) 
			{
				db.execSQL(
						"INSERT INTO "
								+ EMPLOYEE
								+ " VALUES(null,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
						new Object[] { entity.getId(), 
								entity.getDepartment(),
								entity.getUserid(), 
								entity.getjobNumber(),
								entity.getPhoto(),
								entity.getpostDuty(),
								entity.getUsername(),
								entity.getUserDisplayName(),
								entity.getmobile(),  //phoneNumber
								entity.getmobileShortNumber(),  //phoneShortNumber
								entity.getTelephone(), 
								entity.getEmail(),
								entity.getMobilePublic(),
								entity.getNameSpell(),
								//entity.getNextDepartment(),
								entity.getOfficeAddress(),   //用"nextDepartment"字段存放"办公地址"
								entity.getPreDepartment(),
								entity.getUserSortIndex(),
								entity.getUserTitle(), 
								entity.getDeptId(),
								entity.getMobileIphone(),
								entity.getmobileIphoneShortNumber(),
								entity.getCreatetime(), 
								entity.getNameNumber(),
								entity.getPinyinNumber() });
			}
			db.setTransactionSuccessful(); // 设置事务成功完成
		} finally {
			db.endTransaction(); // 结束事务
		}
	}

	/**
	 * 添加部门员工数据
	 * 
	 * @param
	 */
	public void addAssociationList(List<DepartmentEmployeeVO> DepartmentEmpVo) 
	{
		db.beginTransaction(); // 开始事务
		db.execSQL("delete  from " + "db_UserOrganization");
		db.setTransactionSuccessful();
		db.endTransaction();
		db.beginTransaction(); // 开始事务
		try 
		{
			for (DepartmentEmployeeVO entity : DepartmentEmpVo) 
			{
				db.execSQL(
						"INSERT INTO "
								+ "db_UserOrganization"
								+ " VALUES(null,?,?,?,?,?)",
						new Object[] { entity.getid(),
								entity.getuserRID(),
								entity.getorgRID(),
								entity.getuserTitle(),
								entity.getuserSort()});
			}
			db.setTransactionSuccessful(); // 设置事务成功完成
		} finally {
			db.endTransaction(); // 结束事务
		}
	}
	
	/**
	 * 添加多条部门数据
	 * 
	 * @param departmentVo
	 */
	public void addDepartmentList(List<DepartmentVO> departmentVo) 
	{
		db.beginTransaction(); // 开始事务
		db.execSQL("delete  from " + "db_department");
		db.setTransactionSuccessful();
		db.endTransaction();

		db.beginTransaction(); // 开始事务
		try 
		{
			for (DepartmentVO entity : departmentVo) 
			{
				db.execSQL(
						"INSERT INTO " + "db_department"
								+ " VALUES(null,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
						new Object[] { entity.getId(), 
								entity.getDeptId(),
								entity.getDeptName(), 
								entity.getDeptParentid(),
								entity.getisCompany(),
								entity.getisSubCompany(),
								entity.getwebURL(),
								entity.getfax(),
								entity.gettel(),
								entity.getaddress(),
								entity.getstate(),
								entity.getremark(),
								entity.getSortIndex(),
								entity.getorgCode(),
								entity.getDeptDisplayname() });
			}
			db.setTransactionSuccessful(); // 设置事务成功完成
		} 
		finally 
		{
			db.endTransaction(); // 结束事务
		}
	}

	/**
	 * 添加一条日志信息
	 * 
	 * @param
	 */
	public void addLog(HistoryVO historyVo) 
	{
		db.beginTransaction(); // 开始事务
		
		try 
		{
			db.execSQL("INSERT INTO " + HISTORY + " VALUES(null,?,?,?,?,?,?,?)",
					new Object[] { historyVo.getDepartment(),
							historyVo.getUserid(),
							historyVo.getUserDisplayName(),
							historyVo.getPhoneNumber(), historyVo.getEmail(),
							historyVo.getNameSpell(), historyVo.getCreatetime() });
			
			db.setTransactionSuccessful(); // 设置事务成功完成
		} 
		finally 
		{
			db.endTransaction(); // 结束事务
		}
	}
	
	/**
	 * 添加一条历史记录信息
	 * 
	 * @param
	 */
	public void addHistory(HistoryVO historyVo) {
		db.beginTransaction(); // 开始事务
		try {
			db.execSQL(
					"INSERT INTO " + HISTORY + " VALUES(null,?,?,?,?,?,?,?)",
					new Object[] { historyVo.getDepartment(),
							historyVo.getUserid(),
							historyVo.getUserDisplayName(),
							historyVo.getPhoneNumber(), historyVo.getEmail(),
							historyVo.getNameSpell(), historyVo.getCreatetime() });
			db.setTransactionSuccessful(); // 设置事务成功完成
		} finally {
			db.endTransaction(); // 结束事务
		}
	}

	/**
	 * 删除单条的历史记录
	 * 
	 * @param userid
	 *            需要查用户id
	 */
	public void delHistoryForUserid(String userid) {
		db.beginTransaction(); // 开始事务
		db.execSQL("delete from " + HISTORY + " where userid = '" + userid
				+ "' ");
		db.setTransactionSuccessful();
		db.endTransaction();
	}
	
	/**
	 * 根据用户id查询收藏记录
	 */
	public int collectCount(String userId){
		String sql = "select count(*) from " + COLLECT + " where userid = ?  ";
		Cursor c = db.rawQuery(sql, new String[] { userId});
		c.moveToFirst();
		int dataCount = c.getInt(0);
		return dataCount;
	}
	
	/**
	 * 添加一条收藏记录信息
	 * 
	 * @param
	 */
	public void addCollect(CollectVO collectVo) {

		String sql = "select count(*) from " + COLLECT + " where userid = ?  ";
		Cursor c = db.rawQuery(sql, new String[] { collectVo.getUserID()});
		c.moveToFirst();
		int dataCount = c.getInt(0);
		if (dataCount > 0)
			return;

		db.beginTransaction(); // 开始事务
		try {
			db.execSQL(
					"INSERT INTO " + COLLECT + " VALUES(null,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
					new Object[] { collectVo.getUserID(),collectVo.getUserName(),
							collectVo.getUserDisplayName(),collectVo.getJobNumber(), 
							collectVo.getPhoto(),collectVo.getGender(), 
							collectVo.getUserState(),collectVo.getPostDuty(),
							collectVo.getEmail(),collectVo.getImAccounts(),
							collectVo.getMobile(),collectVo.getMobileShortNumber(),
							collectVo.getMobileIphone(),collectVo.getMobileIphoneShortNumber(),
							collectVo.getTelephone(),collectVo.getOfficeTelShort(),
							collectVo.getCreatetime()});
			db.setTransactionSuccessful(); // 设置事务成功完成
		} finally {
			db.endTransaction(); // 结束事务
		}
	}

	/**
	 * 检查是否已经收藏
	 * 
	 * @param userid
	 *            需要查用户id
	 */
	public int checkCollectForUserid(String userid) {
		String sql = "select count(*) from " + COLLECT + " where userid = ? ";
		Cursor c = db.rawQuery(sql, new String[] { userid });
		c.moveToFirst();
		int length = c.getInt(0);
		c.close();
		return length;
	}

	/**
	 * 删除单条的收藏记录
	 * 
	 * @param userid
	 *            需要查用户id
	 */
	public void delCollectForUserid(String userid) {
		db.beginTransaction(); // 开始事务
		db.execSQL("delete from " + COLLECT + " where userid = '" + userid
				+ "' ");
		db.setTransactionSuccessful();
		db.endTransaction();
	}

	/**
	 * 删除所有同步数据
	 * 
	 * @param
	 */
	public void deleteAlldata()
	{
		//清空部门表
		db.beginTransaction(); // 开始事务
		try
		{
			db.execSQL("delete from " + DEPARTMENT);
			db.setTransactionSuccessful();
		}
		finally
		{
			db.endTransaction(); // 结束事务
		}
		
		//清空员工表
		db.beginTransaction();
		try
		{
			db.execSQL("delete  from " + EMPLOYEE);
			db.setTransactionSuccessful();
		}
		finally
		{
			db.endTransaction();
		}
		
		//清空关联表
		db.beginTransaction();
		try
		{
			db.execSQL("delete  from db_UserOrganization");
			db.setTransactionSuccessful();
		}
		finally
		{
			db.endTransaction();
		}
	}

	/**
	 * 查询总记录数
	 * 
	 * @param tableName
	 *            需要查询的表名
	 */
	public int getCount(String tableName) {
		String sql = "select count(*) from " + tableName;
		Cursor c = db.rawQuery(sql, null);
		c.moveToFirst();
		int length = c.getInt(0);
		c.close();
		return length;
	}

	//
	// /**
	// * 分页查询
	// *
	// * @param currentPage
	// * 当前页
	// * @param pageSize
	// * 每页显示的记录
	// * @return 当前页的记录
	// */
	// public ArrayList<String> getAllItems(int currentPage, int pageSize) {
	// int firstResult = (currentPage - 1) * pageSize;
	// int maxResult = currentPage * pageSize;
	// SQLiteDatabase db = helper.getWritableDatabase();
	// String sql = "select * from database limit ?,?";
	// Cursor mCursor = db.rawQuery(
	// sql,
	// new String[] { String.valueOf(firstResult),
	// String.valueOf(maxResult) });
	// ArrayList<String> items = new ArrayList<String>();
	// int columnCount = mCursor.getColumnCount();
	// while (mCursor.moveToNext()) {
	// String item = mCursor.getString(mCursor
	// .getColumnIndexOrThrow(helper.FIELD_TITLE));
	// items.add(item);
	//
	// }
	// // 不要关闭数据库
	// return items;
	// }

	/**
	 * close database
	 */
	public void closeDB() {
		if (db != null && db.isOpen())
			db.close();
	}

	/**
	 * 数据库是否连接
	 * 
	 * @return
	 */
	public boolean isConnected() {
		return db != null && db.isOpen();
	}


}
