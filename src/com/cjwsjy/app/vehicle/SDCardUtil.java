package com.cjwsjy.app.vehicle;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Environment;

/**
 * Sd卡工具类
 * @Copyright:   Copyright (c)2013  
 * @Company:     广州市道一信息有限公司
 * @author:      Mr.y  
 * @version:     1.0  
 * @date:        2013-10-15 下午3:26:01  
 *  
 * Modification History:  
 * Date         Author      Version     Description  
 * ------------------------------------------------------------------  
 * 2013-10-15      Mr.y          1.0       1.0 Version
 */
public class SDCardUtil {
	
	public static String imageCache = Environment.getExternalStorageDirectory()
			+ File.separator + "minaim" + File.separator + "imageCache";
	
	/**
	 * sd卡是否可用
	 * @return true 如果sd卡可读可写
	 */
	public static boolean isAvailable() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

	/**
	 * 获取存放图片的地址
	 * @return
	 */
	public static File getPhotoDir() {
		File file = new File(Environment.getExternalStorageDirectory()
				+ File.separator + "minaim" + File.separator + "Camera");
		if (!file.exists() && !file.isDirectory()){
			file.mkdirs();
		}
		return file;
	}
	
	/**
	 * sd卡中的文件夹
	 * @return
	 */
	public static File getPackInSdcard() {
		File file = new File(Environment.getExternalStorageDirectory()
				+ File.separator + "minaim");
		if (!file.exists() && !file.isDirectory()){
			file.mkdirs();
		}
		return file;
	}
	
	/**
	 * 获取存放随手拍图片的地址
	 * @return
	 */
	public static File getTakingPhotoDir() {
		File file = new File(Environment.getExternalStorageDirectory()
				+ File.separator + "minaim" + File.separator + "picture");
		if (!file.exists() && !file.isDirectory()){
			file.mkdirs();
		}
		return file;
	}
	
	/**
	 * 获取存放随手拍图片的地址
	 * @return
	 */
	public static File getImageCacheDir() {
		File file = new File(Environment.getExternalStorageDirectory()
				+ File.separator + "minaim" + File.separator + "imageCache");
		if (!file.exists() && !file.isDirectory()){
			file.mkdirs();
		}
		return file;
	}
	
	/**
	 * 获取存放随手拍图片的地址
	 * @return
	 */
	public static File getVoiceDir() {
		File file = new File(Environment.getExternalStorageDirectory()
				+ File.separator + "minaim" + File.separator + "voice");
		if (!file.exists() && !file.isDirectory()){
			file.mkdirs();
		}
		return file;
	}
	
	/**
	 * 获取存放随手拍图片的地址
	 * @return
	 */
	public static File createTempFile(String path,String fileName) {
		File file = new File(path+fileName);
		if (!file.exists() && !file.isDirectory()){
			file.mkdirs();
		}
		return file;
	}
	
	/**
	 * 获取语音存放地址
	 * @return
	 */
	public static File getVoiceDirByPath(String filePath) {
		File file = new File(filePath);
		if (!file.exists() && !file.isDirectory()){
			file.mkdirs();
		}
		return file;
	}
	
	public static String getFileName(){
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
		return dateFormat.format(date);
		
	}
	
	public static String getPhotoFileName(){
		return "IMAG_" + getFileName() + ".jpg";
	}
	
	/**
     * 把字节数组保存为一个文件 
     * @param b
     * @param outputFile
     * @return
     */
    public static File getFileFromBytes(byte[] b, String outputFile) {   
        BufferedOutputStream stream = null;   
        File file = null;   
        try {   
            file = new File(outputFile); 
            FileOutputStream fstream = new FileOutputStream(file);   
            stream = new BufferedOutputStream(fstream);   
            stream.write(b);   
        } catch (Exception e) {   
            e.printStackTrace();   
        } finally {   
            if (stream != null) {   
                try {   
                    stream.close();   
                } catch (IOException e1) {   
                    e1.printStackTrace();   
                }   
            }   
        }   
        return file;   
    } 
    
	public static void delFolderImageCache(String folderPath) {
		try {
			delAllFile(folderPath); // 删除完里面所有内容
			String filePath = folderPath;
			filePath = filePath.toString();
			java.io.File myFilePath = new java.io.File(filePath);
			myFilePath.delete(); // 删除空文件夹
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static boolean delAllFile(String path) {
		boolean flag = false;
		File file = new File(path);
		if (!file.exists()) {
			return flag;
		}
		if (!file.isDirectory()) {
			return flag;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
				delFolderImageCache(path + "/" + tempList[i]);// 再删除空文件夹
				flag = true;
			}
		}
		return flag;
	}
}
