package com.cjwsjy.app.vehicle;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 验证图片路径工具类
 * @Copyright:   Copyright (c)2013  
 * @Company:     广州市道一信息有限公司
 * @author:      Mr.y  
 * @version:     1.0  
 * @date:        2013-10-15 下午3:25:54  
 *  
 * Modification History:  
 * Date         Author      Version     Description  
 * ------------------------------------------------------------------  
 * 2013-10-15      Mr.y          1.0       1.0 Version
 */
public class RegUtil {
	public static boolean isPictureUrl(String url){
		try {
				String searchImgReg2 = "(http://([\\w-]+\\.)+[\\w-]+(:[0-9]+)*(/[\\w-]+)*(/[\\w-]+\\.(jpeg|JPEG|bmp|BMP|jpg|JPG|png|PNG|gif|GIF)))";
			   Pattern p = Pattern.compile(searchImgReg2);
			   Matcher m = p.matcher(url);
			   if(m.find())
			   {
				   return true;
			   }
			   return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
