package com.cjwsjy.app.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import cmcc.gz.app.common.base.util.PropertyUtil;

import android.content.Context;

public class AppProperties {
    
	public static String getproperties(Context c){
		String url="";
		try {
			Properties pro = new Properties(); 
			//InputStream in = PropertyUtil.class.getResourceAsStream("/assets/app.properties");
			InputStream in =c.getAssets().open("app.properties");
			pro.load(in);
			url = pro.getProperty("appUrl");
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return url;
	}
}
