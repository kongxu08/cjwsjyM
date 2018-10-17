package com.cjwsjy.app.utils;

import java.util.regex.Pattern;

/**
 * 验证工具类
 * 
 */
public class ValidUtil {

	/*
	 * 验证手机号，11位，且必须含全是数字
	 */
	public static boolean isMoble(String mobile) {
		return mobile.matches("^\\d{11}$");
	}

	/*
	 * 验证是否为空
	 */
	public static boolean isNullOrEmpty(String s) {
		if ("null".equalsIgnoreCase(s) || s == null || "".equals(s.trim())) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判断是否为数字
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str) {
		if (isNullOrEmpty(str))
			return false;
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches();
	}

	/**
	 * 去掉手机号码的杂件
	 * 
	 * @return
	 */
	public static String getOnlyMobile(String mobile) {
		if (isNullOrEmpty(mobile))
			return "";
		int flag = 0;
		for (int i = 0; i < mobile.length(); i++) {
			if ("1".equals(mobile.charAt(i) + "")) {
				flag = i;
				break;
			}
		}
		mobile = mobile.substring(flag, mobile.length());// 去掉前面+86之类
		mobile = mobile.replace(" ", "").replace("-", "");// 去掉空格，去掉横线
		return mobile;
	}

	public static String null2Str(String a) {
		if (a == null) {
			return "";
		} else {
			return a;
		}
	}

	/**
	 * 是否完整
	 * 
	 * @param s
	 * @return
	 */
	public static boolean isComplete(String s) {
		return s.indexOf("http://") == 0;
	}
}
