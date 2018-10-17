package com.cjwsjy.app.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Intent;
import android.net.Uri;

public class HttpDownloader 
{
    private URL url=null;  
    FileUtils fileUtils=new FileUtils();
    
	public int downfile(String urlStr, String path, String fileName)
	{
		if (fileUtils.isFileExist(path + fileName))
		{
			return 1;
		}
		else
		{
			try
			{
				InputStream input = null;
				input = getInputStream(urlStr);
				if( input==null )  return -1;
				
				File resultFile = fileUtils.write2SDFromInput(path, fileName, input);
				if (resultFile == null)
				{
					return -1;
				}
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				return -1;
			}
		}
		return 0;
	}

    public int downfile2(String urlStr, String path, String fileName)
    {
        if (fileUtils.isFileExist(path + fileName))
        {
            return 1;
        }
        else
        {
            try
            {
                InputStream input = null;
                HttpURLConnection connection;

                urlStr=urlStr.replace(" ", "%20");
                url = new URL(urlStr);
                connection = (HttpURLConnection) url.openConnection();
//                connection.setRequestMethod("GET");
//                connection.setDoInput(true);
//                connection.setDoOutput(true);
//                connection.setUseCaches(false);
//                connection.setConnectTimeout(5000);
//                connection.setReadTimeout(5000);
                //实现连接
                //connection.connect();

                input = connection.getInputStream();

                //input = getInputStream(urlStr);

                if( input==null )  return -1;

                File resultFile = fileUtils.write2SDFromInput(path, fileName, input);
                if (resultFile == null)
                {
                    return -1;
                }
                connection.disconnect();
            }
            catch (MalformedURLException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return -1;
            }
        }
        return 0;
    }

	// 由于得到一个InputStream对象是所有文件处理前必须的操作，所以将这个操作封装成了一个方法
	public InputStream getInputStream(String urlStr) throws IOException
	{
		InputStream is = null;
		try
		{
			url = new URL(urlStr);
			HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
			is = urlConn.getInputStream();
		}
		catch (MalformedURLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return is;
	}
     
     //android打开文件
     public static Intent openFile(String filePath){
    	   
           File file = new File(filePath);
           if(!file.exists()) return null;
           /* 取得扩展名 */
           String end=file.getName().substring(file.getName().lastIndexOf(".") + 1,file.getName().length()).toLowerCase(); 
           /* 依扩展名的类型决定MimeType */
           if(end.equals("m4a")||end.equals("mp3")||end.equals("mid")||
                   end.equals("xmf")||end.equals("ogg")||end.equals("wav")){
               return getAudioFileIntent(filePath);
           }else if(end.equals("3gp")||end.equals("mp4")){
               return getAudioFileIntent(filePath);
           }else if(end.equals("jpg")||end.equals("gif")||end.equals("png")||
                   end.equals("jpeg")||end.equals("bmp")){
               return getImageFileIntent(filePath);
           }else if(end.equals("apk")){
               return getApkFileIntent(filePath);
           }else if(end.equals("ppt")){
               return getPptFileIntent(filePath);
           }else if(end.equals("xls")){
               return getExcelFileIntent(filePath);
           }else if(end.equals("doc")){
               return getWordFileIntent(filePath);
           }else if(end.equals("pdf")){
               return getPdfFileIntent(filePath);
           }else if(end.equals("chm")){
               return getChmFileIntent(filePath);
           }else if(end.equals("txt")){
               return getTextFileIntent(filePath,false);
           }else{
               return getAllIntent(filePath);
           }
       }
        
       //Android获取一个用于打开APK文件的intent
       public static Intent getAllIntent( String param ) {
    
           Intent intent = new Intent();  
           intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
           intent.setAction(android.content.Intent.ACTION_VIEW);  
           Uri uri = Uri.fromFile(new File(param ));
           intent.setDataAndType(uri,"*/*"); 
           return intent;
       }
       //Android获取一个用于打开APK文件的intent
       public static Intent getApkFileIntent( String param ) {
    
           Intent intent = new Intent();  
           intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
           intent.setAction(android.content.Intent.ACTION_VIEW);  
           Uri uri = Uri.fromFile(new File(param ));
           intent.setDataAndType(uri,"application/vnd.android.package-archive"); 
           return intent;
       }
    
       //Android获取一个用于打开VIDEO文件的intent
       public static Intent getVideoFileIntent( String param ) {
    
           Intent intent = new Intent("android.intent.action.VIEW");
           intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
           intent.putExtra("oneshot", 0);
           intent.putExtra("configchange", 0);
           Uri uri = Uri.fromFile(new File(param ));
           intent.setDataAndType(uri, "video/*");
           return intent;
       }
    
       //Android获取一个用于打开AUDIO文件的intent
       public static Intent getAudioFileIntent( String param ){
    
           Intent intent = new Intent("android.intent.action.VIEW");
           intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
           intent.putExtra("oneshot", 0);
           intent.putExtra("configchange", 0);
           Uri uri = Uri.fromFile(new File(param ));
           intent.setDataAndType(uri, "audio/*");
           return intent;
       }
    
       //Android获取一个用于打开Html文件的intent   
       public static Intent getHtmlFileIntent( String param ){
    
           Uri uri = Uri.parse(param ).buildUpon().encodedAuthority("com.android.htmlfileprovider").scheme("content").encodedPath(param ).build();
           Intent intent = new Intent("android.intent.action.VIEW");
           intent.setDataAndType(uri, "text/html");
           return intent;
       }
    
       //Android获取一个用于打开图片文件的intent
       public static Intent getImageFileIntent( String param ) {
    
           Intent intent = new Intent("android.intent.action.VIEW");
           intent.addCategory("android.intent.category.DEFAULT");
           intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
           Uri uri = Uri.fromFile(new File(param ));
           intent.setDataAndType(uri, "image/*");
           return intent;
       }
    
       //Android获取一个用于打开PPT文件的intent   
       public static Intent getPptFileIntent( String param ){  
    
           Intent intent = new Intent("android.intent.action.VIEW");   
           intent.addCategory("android.intent.category.DEFAULT");   
           intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);   
           Uri uri = Uri.fromFile(new File(param ));   
           intent.setDataAndType(uri, "application/vnd.ms-powerpoint");   
           return intent;   
       }   
    
       //Android获取一个用于打开Excel文件的intent   
       public static Intent getExcelFileIntent( String param ){  
    
           Intent intent = new Intent("android.intent.action.VIEW");   
           intent.addCategory("android.intent.category.DEFAULT");   
           intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);   
           Uri uri = Uri.fromFile(new File(param ));   
           intent.setDataAndType(uri, "application/vnd.ms-excel");   
           return intent;   
       }   
    
       //Android获取一个用于打开Word文件的intent   
       public static Intent getWordFileIntent( String param ){  
    
           Intent intent = new Intent("android.intent.action.VIEW");   
           intent.addCategory("android.intent.category.DEFAULT");   
           intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);   
           Uri uri = Uri.fromFile(new File(param ));   
           intent.setDataAndType(uri, "application/msword");   
           return intent;   
       }   
    
       //Android获取一个用于打开CHM文件的intent   
       public static Intent getChmFileIntent( String param ){   
    
           Intent intent = new Intent("android.intent.action.VIEW");   
           intent.addCategory("android.intent.category.DEFAULT");   
           intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);   
           Uri uri = Uri.fromFile(new File(param ));   
           intent.setDataAndType(uri, "application/x-chm");   
           return intent;   
       }   
    
       //Android获取一个用于打开文本文件的intent   
       public static Intent getTextFileIntent( String param, boolean paramBoolean){   
    
           Intent intent = new Intent("android.intent.action.VIEW");   
           intent.addCategory("android.intent.category.DEFAULT");   
           intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);   
           if (paramBoolean){   
               Uri uri1 = Uri.parse(param );   
               intent.setDataAndType(uri1, "text/plain");   
           }else{   
               Uri uri2 = Uri.fromFile(new File(param ));   
               intent.setDataAndType(uri2, "text/plain");   
           }   
           return intent;   
       }  
       //Android获取一个用于打开PDF文件的intent   
       public static Intent getPdfFileIntent( String param ){   
    
           Intent intent = new Intent("android.intent.action.VIEW");   
           intent.addCategory("android.intent.category.DEFAULT");   
           intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);   
           Uri uri = Uri.fromFile(new File(param ));   
           intent.setDataAndType(uri, "application/pdf");   
           return intent;   
       }    
       
}  
