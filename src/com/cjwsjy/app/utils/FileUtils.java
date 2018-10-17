package com.cjwsjy.app.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import com.cjwsjy.app.SmApplication;

public class FileUtils {

	private String SDPATH;
	private SharedPreferences sp;

	public String getSDPATH() {
		return SDPATH;
	}
	public FileUtils()
	{
		//得到当前外部存储设备的目录
		// /SDCARD

		sp = SmApplication.sp;
		SDPATH =sp.getString("SD_Path", "");

		if(SDPATH.length()==0) SDPATH = Environment.getExternalStorageDirectory() + "/Download/";

	}

	public static String getFilePath(Context context, String dir)
	{
		String sdpath;
		String directoryPath="";

		sdpath = Environment.getExternalStorageState();
		if (sdpath.equals(Environment.MEDIA_MOUNTED) )
		{//判断外部存储是否可用
			directoryPath =context.getExternalFilesDir(dir).getAbsolutePath();
		}
		else
		{//没外部存储就使用内部存储
			directoryPath=context.getFilesDir()+File.separator+dir;
		}
		File file = new File(directoryPath);
		if(!file.exists()){//判断文件目录是否存在
			file.mkdirs();
		}
		return directoryPath;
	}

	/**
	 * 在SD卡上创建文件
	 * 
	 * @throws IOException
	 */
	public File creatSDFile(String fileName) throws IOException {
		File file = new File(SDPATH +"/com.cjwsjy.app/" + fileName);
		file.createNewFile();
		return file;
	}
	
	/**
	 * 在SD卡上创建目录
	 * 
	 * @param dirName
	 */
	public File creatSDDir(String dirName) 
	{	
		File dir1 = new File(SDPATH + "/com.cjwsjy.app/");
		if(!isFileExist(SDPATH + "/com.cjwsjy.app/")){
			dir1.mkdir();
		}
		File dir = new File(SDPATH+"/com.cjwsjy.app/" + dirName);
		if(!isFileExist(SDPATH+"/com.cjwsjy.app/" + dirName)){
			dir.mkdir();
		}		
		return dir;
	}

	/**
	 * 判断SD卡上的文件夹是否存在
	 */
	public boolean isFileExist(String folderName){
		File file = new File(folderName);
		return file.exists();
	}
	
	/**
	 * 将一个InputStream里面的数据写入到SD卡中
	 */
	public File write2SDFromInput(String path,String fileName,InputStream input)
	{
		File file = null;
		OutputStream output = null;
		
		try
		{
			creatSDDir(path);
			file = creatSDFile(path + fileName);
			output = new FileOutputStream(file);
			byte buffer [] = new byte[4 * 1024];

			int length = 0;
            while((length=(input.read(buffer)))!=-1)
			{
                  output.write(buffer,0,length);  
            } 
			output.flush();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			try{
				output.close();
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		return file;
	}
}