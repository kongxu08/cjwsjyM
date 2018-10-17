package com.cjwsjy.app.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 时间转换类工具
 * 
 */
public class DateUtil {

	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/**
	 * 根据传入的时间获取需要的时间
	 * 
	 * @param time
	 *            参数格式:yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public static String getDatestr(String time) {
		if (ValidUtil.isNullOrEmpty(time)) {
			return "";
		}
		String rs = "";
		try {
			String today = sdf.format(new Date());
			// 传入时间为今天的情况
			if (today.substring(0, 10).equals(time.substring(0, 10))) {
				int h = Integer.parseInt(time.substring(11, 13));
				if (h < 6) {
					rs = "凌晨";
				} else if (h >= 6 && h < 12) {
					rs = "上午";
				} else if (h >= 12 && h < 18) {
					rs = "下午";
				} else {
					rs = "晚上";
				}
				rs = rs + " " + time.substring(11, 16);
			} else {
				Date d1 = sdf.parse(today);
				Date d2 = sdf.parse(time);
				long diff = d1.getTime() - d2.getTime();
				int days = (int) (diff / (1000 * 60 * 60 * 24));
				// 传入时间为昨天的情况
				if (days == 1) {// 如果只相差一天
					int h = Integer.parseInt(time.substring(11, 13));
					if (h < 6) {
						rs = "凌晨";
					} else if (h >= 6 && h < 12) {
						rs = "上午";
					} else if (h >= 12 && h < 18) {
						rs = "下午";
					} else {
						rs = "晚上";
					}
					rs = "昨天" + " " + rs + time.substring(11, 16);
				} else {// 传入时间为更早的处理情况
					// int h = Integer.parseInt(time.substring(11, 13));
					// if(h < 6){
					// rs = "凌晨";
					// }else if(h >= 6 && h < 12){
					// rs = "上午";
					// }else if(h >= 12 && h < 18){
					// rs = "下午";
					// }else{
					// rs = "晚上";
					// }
					// rs = time.substring(0, 10) + " " + rs +
					// time.substring(11,
					// 16);
					rs = time.substring(0, 10);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rs;
	}

	public static String getNewDate() {
		String d = sdf.format(new Date());
		return d;
	}

	public static String format(Date date, String string) {
		// TODO Auto-generated method stub
		return new SimpleDateFormat(string).format(date);
	}
}
