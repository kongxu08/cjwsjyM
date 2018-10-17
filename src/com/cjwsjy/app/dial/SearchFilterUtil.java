package com.cjwsjy.app.dial;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cjwsjy.app.utils.StringHelper;
import com.do1.cjmobileoa.db.model.SUserVO;

import android.text.TextUtils;
import android.util.Log;

public class SearchFilterUtil {

//	/**
//	 * 按号码-拼音搜索联系人
//	 * 
//	 * @param str
//	 */
//	public static List<SUserVO> filterContact(String searchStr) {
//		List<SUserVO> resultContants = new ArrayList<SUserVO>();
//		List<SUserVO> allEmployee = SmApplication.allSUserVO;
//
//		resultContants.addAll(filterContact(allEmployee, searchStr));
//
//		return resultContants;
//	}

	/**
	 * 按号码-拼音搜索联系人
	 * 
	 * @param str
	 */
	public static List<SUserVO> filterContact(final List<SUserVO> filterContants,String searchStr) 
	{
		//boolean result;
		//int index = 0;
		//String string1 = "";
		List<SUserVO> contacts = new ArrayList<SUserVO>();
		
		// 如果搜索条件以0 1 + # * 开头则按号码搜索   非拼音按键
		if (searchStr.startsWith("0") || searchStr.startsWith("1")
				|| searchStr.startsWith("+") || searchStr.contains("#")
				|| searchStr.contains("*")) 
		{
			for (SUserVO entity : filterContants) 
			{
				if (entity.getMobile().contains(searchStr)) 
				{
					SUserVO newEntity=entity.clone();
					newEntity.setMatcher_type(SUserVO.matcher_type_mobile);
					newEntity.setSearchStr(searchStr);
					contacts.add(newEntity);
					//result = contacts.contains(newEntity);
					//if(result==false) contacts.add(newEntity);

				}
			}
			return contacts;
		}
		
       //   其他情况  安
		for (SUserVO entity : filterContants) 
		{
			/*if( searchStr.length() <= 3 && entity.getNameNumber().contains(searchStr)) 
			{
				// 搜索条件大于3个字符将不按拼音首字母查询
				SUserVO newEntity=entity.clone();
				newEntity.setMatcher_type(SUserVO.matcher_type_nameNumber);
				newEntity.setSearchStr(searchStr);
				contacts.add(newEntity);
			}
			else if (entity.getPinyinNumber().contains(searchStr)) */
			//string1 = entity.getPinyinNumber();
			//index = string1.indexOf(searchStr);
			
			//if(index==0)
			if (entity.getPinyinNumber().contains(searchStr)) 
			{
				// 根据全拼查询
				SUserVO newEntity=entity.clone();
				newEntity.setMatcher_type(SUserVO.matcher_type_pinyinNumber);
				newEntity.setSearchStr(searchStr);
				contacts.add(newEntity);
			} 
			else if (entity.getMobile().contains(searchStr)) 
			{
				//根据电话号码
				SUserVO newEntity=entity.clone();
				newEntity.setMatcher_type(SUserVO.matcher_type_mobile);
				newEntity.setSearchStr(searchStr);
				contacts.add(newEntity);
			} 
		}
		return contacts;
	}

	public static boolean contains(SUserVO vo,String searchStr) 
	{
		try
		{
			if (vo.getPinyinNumber().contains(searchStr)) {
				return true;
			}
			if (vo.getMobile().contains(searchStr)) {
				return true;
			}
		}
		catch (Exception e)
		{
			return false;
		}

		return false;
	}

	/**
	 * 根据拼音搜索
	 * 
	 * @param str
	 *            正则表达式
	 * @return
	 */
	private static boolean containsPinyin(String str, SUserVO entity,
			String search) {
		if (StringHelper.isEmpty(entity.getPinyin())) {
			return false;
		}
		// 搜索条件大于3个字符将不按拼音首字母查询
		if (search.length() <= 3 && entity.getNameNumber().contains(search)) {
			return true;
		}

		// 根据全拼查询
		if (entity.getPinyinNumber().contains(search)) {
//		// TODO ZHQ why？
//			String repStr = str.replace("-", "");
//			Pattern pattern = Pattern.compile(repStr, Pattern.CASE_INSENSITIVE);
//			Matcher matcher = pattern.matcher(entity.getPinyin().replaceAll(
//					" ", ""));
//			if (matcher.find() && matcher.group().charAt(0) >= 'A'
//					&& matcher.group().charAt(0) <= 'Z') {
//				return true;
//			}
			
			
			return true;
		}

		return false;

	}

//	public static String getGroupStr(String formatPinyin, String searchStr) {
//		String groupStr = "";
//
//		if (searchStr.startsWith("0") && searchStr.startsWith("1")
//				|| searchStr.startsWith("+") || searchStr.contains("#")
//				|| searchStr.contains("*")) {
//			return groupStr;
//		}
//
//		StringBuffer sb = new StringBuffer();
//		// 获取每一个数字对应的字母列表并以'-'隔开
//		for (int i = 0; i < searchStr.length(); i++) {
//			sb.append((searchStr.charAt(i) <= '9' && searchStr.charAt(i) >= '0') ? GetPinYin4j.STRS[searchStr
//					.charAt(i) - '0'] : searchStr.charAt(i));
//			if (i != searchStr.length() - 1) {
//				sb.append("-");
//			}
//		}
//
//		if (searchStr.length() <= 3) {
//			Pattern pattern = Pattern.compile("^"
//					+ sb.toString().toUpperCase().replace("-", "[*+#a-z]*"));
//			Matcher matcher = pattern.matcher(formatPinyin);
//			if (matcher.find()) {
//				String tempStr = matcher.group();
//				for (int i = 0; i < tempStr.length(); i++) {
//					if (tempStr.charAt(i) >= 'A' && tempStr.charAt(i) <= 'Z') {
//						groupStr = groupStr + tempStr.charAt(i);
//					}
//				}
//				return groupStr;
//			}
//		}
//
//		String repStr = sb.toString().replace("-", "");
//		Pattern pattern = Pattern.compile(repStr, Pattern.CASE_INSENSITIVE);
//		Matcher matcher = pattern.matcher(formatPinyin);
//		if (matcher.find() && matcher.group().charAt(0) >= 'A'
//				&& matcher.group().charAt(0) <= 'Z') {
//			groupStr = matcher.group();
//		}
//
//		return groupStr;
//	}

}
