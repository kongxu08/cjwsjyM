package com.do1.cjmobileoa.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

  
public class DBHelper extends SQLiteOpenHelper {  
  
    private static final String DATABASE_NAME = "changjiang.db";  
    private static final int DATABASE_VERSION = 4;
      
    public DBHelper(Context context) {  
        //CursorFactory设置为null,使用默认值  
        super(context, DATABASE_NAME, null, DATABASE_VERSION);  
    }  
    
    //数据库第一次被创建时onCreate会被调用 
    @Override  
    public void onCreate(SQLiteDatabase db) 
    {
        android.util.Log.d("cjwsjy", "------DBHelper-------");
    	/**
    	 * 员工表
    	 */
    	db.execSQL("CREATE TABLE IF NOT EXISTS db_employee " +  
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "id VARCHAR(100)," +
                "department VARCHAR(100)," +
                "userId VARCHAR(100)," +
                "jobNumber VARCHAR(100)," +
                "photo VARCHAR(100)," +
                "postDuty VARCHAR(100)," +
                "username VARCHAR(100)," +
                "userDisplayName VARCHAR(100)," +
                "phoneNumber VARCHAR(100)," +
                "phoneShortNumber VARCHAR(100)," +
                "telephone VARCHAR(100)," +
                "email VARCHAR(100)," +
                "mobilePublic VARCHAR(100)," +
                "nameSpell VARCHAR(100)," +
                "nextDepartment VARCHAR(100)," +
                "preDepartment VARCHAR(100)," +
                "userSortIndex int," +
                "userTitle VARCHAR(100)," +
                "deptId VARCHAR(100)," +
                "mobileIphone VARCHAR(100)," +
                "mobileIphoneShortNumber VARCHAR(100)," +
                "createtime VARCHAR(100)," +
                "nameNumber VARCHAR(100)," +
                "pinyinNumber VARCHAR(100))"
                );
    	
    	//删除部门表
    	//db.execSQL("DROP TABLE IF EXISTS db_department");
    	
    	/**
    	 * 部门表
    	 */
    	db.execSQL("CREATE TABLE IF NOT EXISTS db_department " +  
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "id VARCHAR(100)," +
                "deptId VARCHAR(100)," +
                "deptName VARCHAR(100)," +
                "deptParentid VARCHAR(100)," +
                "isCompany VARCHAR(100)," +
                "isSubCompany VARCHAR(100)," +
                "webURL VARCHAR(100)," +
                "fax VARCHAR(100)," +
                "tel VARCHAR(100)," +
                "address VARCHAR(100)," +
                "state VARCHAR(100)," +
                "remark VARCHAR(100)," +
                "sortIndex int," +
                "orgCode VARCHAR(100)," +
                "deptDisplayname VARCHAR(100))"
                );
    	
    	//日志表
//    	db.execSQL("CREATE TABLE IF NOT EXISTS db_logtable" +  
//                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
//                "id VARCHAR(100)," +
//                "userid VARCHAR(100)," +
//                "userName VARCHAR(100)," +
//                "operateTime VARCHAR(100)," +
//                "logLevel VARCHAR(100)," +
//                "modelName VARCHAR(100)," +
//                "operateName VARCHAR(100)," +
//                "object VARCHAR(100)," +
//                "content VARCHAR(100)," +
//                "errorMessage VARCHAR(100)," +
//                "result VARCHAR(100))"
//                );
    	
    	//部门员工关联表
    	db.execSQL("CREATE TABLE IF NOT EXISTS db_UserOrganization " +  
		      "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
		      "id VARCHAR(100)," +
		      "userRID VARCHAR(100)," +
		      "orgRID VARCHAR(100)," +
		      "userTitle VARCHAR(100)," +
		      "userSort int)"
		      );
    	
    	/**
    	 * 历史记录表
    	 */
    	db.execSQL("CREATE TABLE IF NOT EXISTS db_history " +  
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "department VARCHAR(100)," +
                "userId VARCHAR(100)," +
                "userDisplayName VARCHAR(100)," +
                "phoneNumber VARCHAR(100)," +
                "email VARCHAR(100)," +
                "nameSpell VARCHAR(100)," +
                "createtime VARCHAR(100))"
                );  
    	
    	/**
    	 * 收藏表
    	 */
    	db.execSQL("CREATE TABLE IF NOT EXISTS db_collect " +  
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "userID VARCHAR(100)," +
                "userName VARCHAR(100)," +
                "userDisplayName VARCHAR(100)," +
                "jobNumber VARCHAR(100)," +
                "photo VARCHAR(100)," +
                "gender VARCHAR(10)," +
                "userState VARCHAR(100)," +
                "postDuty VARCHAR(100)," +
                "email VARCHAR(100)," +
                "imAccounts VARCHAR(100)," +
                "mobile VARCHAR(100)," +
                "mobileShortNumber VARCHAR(100)," +
                "mobileIphone VARCHAR(100)," +
                "mobileIphoneShortNumber VARCHAR(100)," +
                "telephone VARCHAR(100)," +
                "officeTelShort VARCHAR(100)," +
                "createtime VARCHAR(100))"
                );

        //标准管理，文件名表
        db.execSQL("CREATE TABLE IF NOT EXISTS db_biaozhun " +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "id VARCHAR(100)," +
                "oldName VARCHAR(500)," +
                "newName VARCHAR(500)," +
                "isDelete VARCHAR(100)," +
                "field1 VARCHAR(100)," +
                "field2 VARCHAR(100))"
        );

        //档案管理，下载的文件名表
        db.execSQL("CREATE TABLE IF NOT EXISTS db_dangan " +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "id VARCHAR(100)," +
                "oldName VARCHAR(500)," +
                "newName VARCHAR(500)," +
                "isDelete VARCHAR(100)," +
                "field1 VARCHAR(100)," +
                "field2 VARCHAR(100))"
        );
    }

    //如果DATABASE_VERSION值被改为2,系统发现现有数据库版本不同,即会调用onUpgrade  
    @Override  
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
    {
        //onCreate(db);

        if(oldVersion<4)
        {
            db.execSQL("CREATE TABLE IF NOT EXISTS db_biaozhun " +
                    "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "id VARCHAR(100)," +
                    "oldName VARCHAR(500)," +
                    "newName VARCHAR(500)," +
                    "isDelete VARCHAR(100)," +
                    "field1 VARCHAR(100)," +
                    "field2 VARCHAR(100))"
            );

            db.execSQL("CREATE TABLE IF NOT EXISTS db_dangan " +
                    "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "id VARCHAR(100)," +
                    "oldName VARCHAR(500)," +
                    "newName VARCHAR(500)," +
                    "isDelete VARCHAR(100)," +
                    "field1 VARCHAR(100)," +
                    "field2 VARCHAR(100))"
            );
        }

//    	if(oldVersion<2)
//            onUpgrade2(db);
//    	if(oldVersion<3)
//            onUpgrade3(db);
    }
    
    public void  onUpgrade2(SQLiteDatabase db)
    {

        db.execSQL("DELETE  FROM db_employee ");
        db.execSQL("ALTER TABLE db_employee ADD nameNumber VARCHAR(100)");
        db.execSQL("ALTER TABLE db_employee ADD pinyinNumber VARCHAR(100)");
    }
    
    public void  onUpgrade3(SQLiteDatabase db){
   	
    	db.execSQL("ALTER TABLE db_employee  RENAME TO db_employee1 ");;//先将表重命名
        db.execSQL("CREATE TABLE IF NOT EXISTS db_employee " +  
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "id VARCHAR(100)," +
                "department VARCHAR(100)," +
                "userId VARCHAR(100)," +
                "photo VARCHAR(100)," +
                "username VARCHAR(100)," +
                "userDisplayName VARCHAR(100)," +
                "phoneNumber VARCHAR(100)," +
                "phoneShortNumber VARCHAR(100)," +
                "telephone VARCHAR(100)," +
                "email VARCHAR(100)," +
                "mobilePublic VARCHAR(100)," +
                "nameSpell VARCHAR(100)," +
                "nextDepartment VARCHAR(100)," +
                "preDepartment VARCHAR(100)," +
                "userSortIndex int," +
                "userTitle VARCHAR(100)," +
                "deptId VARCHAR(100)," +
                "mobileIphone VARCHAR(100)," +
                "createtime VARCHAR(100)," +
                "nameNumber VARCHAR(100)," +
                "pinyinNumber VARCHAR(100))"
                );  
        db.execSQL("INSERT INTO db_employee  SELECT * FROM db_employee1 ");//将旧表的内容插
        db.execSQL("DROP TABLE db_employee1 ");//删除旧表
        
        
        db.execSQL("ALTER TABLE db_department  RENAME TO db_department1 ");;//先将表重命名
        db.execSQL("CREATE TABLE IF NOT EXISTS db_department " +  
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "id VARCHAR(100)," +
                "deptId VARCHAR(100)," +
                "deptName VARCHAR(100)," +
                "deptParentid VARCHAR(100)," +
                "sortIndex int," +
                "deptDisplayname VARCHAR(100))"
                );  
        db.execSQL("INSERT INTO db_department  SELECT * FROM db_department1 ");//将旧表的内容插
        db.execSQL("DROP TABLE db_department1 ");//将旧表的内容插
        
    }
    
    
}  