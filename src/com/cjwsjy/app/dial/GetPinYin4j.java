package com.cjwsjy.app.dial;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import android.content.ContentValues;

import com.cjwsjy.app.utils.StringHelper;


public class GetPinYin4j {
	
	private String[] letters = { "a", "b", "c", "d", "e", "f", "g", "h",
		"j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "w", "x",
		"y", "z" };
	
	public static String STRS[] = { "", "", "[abc]", "[def]", "[ghi]", "[jkl]",
		"[mno]", "[pqrs]", "[tuv]", "[wxyz]" };
//	/**
//	 * 将汉字转换为全拼
//	 * @param src
//	 * @return String
//	 */
//	public static String getPinYin(String src) {
//		char[] t1 = null;
//		t1 = src.toCharArray();
//		String[] t2 = new String[t1.length];
//		// 设置汉字拼音输出的格式
//		HanyuPinyinOutputFormat t3 = new HanyuPinyinOutputFormat();
//		t3.setCaseType(HanyuPinyinCaseType.UPPERCASE);
//		t3.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
//		t3.setVCharType(HanyuPinyinVCharType.WITH_V);
//		String t4 = "";
//		int t0 = t1.length;
//		try {
//			for (int i = 0; i < t0; i++) {
//				// 判断能否为汉字字符
//				if (Character.toString(t1[i]).matches("[\\u4E00-\\u9FA5]+")) {
//					t2 = PinyinHelper.toHanyuPinyinStringArray(t1[i], t3);// 将汉字的几种全拼都存到t2数组中
//					t4 += t2[0];
//				} else {
//					// 如果不是汉字字符，间接取出字符并连接到字符串t4后
//					t4 += Character.toString(t1[i]).toUpperCase();
//				}
//			}
//		} catch (BadHanyuPinyinOutputFormatCombination e) {
//			e.printStackTrace();
//		}
//		return t4;
//	}
	
	
	/**
	 * 字符串集合转换字符串(逗号分隔)
	 * 
	 * @author wyh
	 * @param stringSet
	 * @return
	 */
	public static String makeStringByStringSet(Set<String> stringSet) {
		StringBuilder str = new StringBuilder();
		int i = 0;
		for (String s : stringSet) {
			if (i == stringSet.size() - 1) {
				str.append(s);
			} else {
				str.append(s + ",");
			}
			i++;
		}
		return str.toString();
	}


	/**
	 * 获取拼音集合
	 * 
	 * @author wyh
	 * @param src
	 * @return Set<String>
	 */
	public static Set<String> getPinyin(String src) {
		if (src != null && !src.trim().equalsIgnoreCase("")) {
			char[] srcChar;
			srcChar = src.toCharArray();
			// 汉语拼音格式输出类
			HanyuPinyinOutputFormat hanYuPinOutputFormat = new HanyuPinyinOutputFormat();

			// 输出设置，大小写，音标方式等
			hanYuPinOutputFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
			hanYuPinOutputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
			hanYuPinOutputFormat.setVCharType(HanyuPinyinVCharType.WITH_V);

			String[][] temp = new String[src.length()][];
			for (int i = 0; i < srcChar.length; i++) {
				char c = srcChar[i];
				// 是中文或者a-z或者A-Z转换拼音(我的需求，是保留中文或者a-z或者A-Z)
				if (String.valueOf(c).matches("[\\u4E00-\\u9FA5]+")) {
					try {
						temp[i] = PinyinHelper.toHanyuPinyinStringArray(
								srcChar[i], hanYuPinOutputFormat);
					} catch (BadHanyuPinyinOutputFormatCombination e) {
						e.printStackTrace();
					}
				} else if (((int) c >= 65 && (int) c <= 90)
						|| ((int) c >= 97 && (int) c <= 122)) {
					temp[i] = new String[] { String.valueOf(srcChar[i]) };
				} else {
					temp[i] = new String[] { "" };
				}
			}
			String[] pingyinArray = Exchange(temp);
			Set<String> pinyinSet = new HashSet<String>();
			for (int i = 0; i < pingyinArray.length; i++) {
				String pingyinStr = pingyinArray[i];
				pinyinSet.add(pingyinStr);
			}
			return pinyinSet;
		}
		return null;
	}

	/**
	 * 递归
	 * 
	 * @author 
	 * @param strJaggedArray
	 * @return
	 */
	public static String[] Exchange(String[][] strJaggedArray) {
		String[][] temp = DoExchange(strJaggedArray);
		return temp[0];
	}

	/**
	 * 递归
	 * 
	 * @author 
	 * @param strJaggedArray
	 * @return
	 */
	private static String[][] DoExchange(String[][] strJaggedArray) {
		int len = strJaggedArray.length;
		if (len >= 2) {
			int len1 = strJaggedArray[0].length;
			int len2 = strJaggedArray[1].length;
			int newlen = len1 * len2;
			String[] temp = new String[newlen];
			int Index = 0;
			for (int i = 0; i < len1; i++) {
				for (int j = 0; j < len2; j++) {
					String str1 = strJaggedArray[0][i] ;
					String str2 = strJaggedArray[1][j] ;
					
					if(str1.length() > 1 && str2.length() > 1 ){
						str1 = str1.replaceFirst(str1.substring(0, 1),str1.substring(0, 1).toUpperCase());
						str2 = str2.replaceFirst(str2.substring(0, 1),str2.substring(0, 1).toUpperCase());
					}
					
					temp[Index] = str1 + " " + str2;
					Index++;
				}
			}
			String[][] newArray = new String[len - 1][];
			for (int i = 2; i < len; i++) {
				newArray[i - 1] = strJaggedArray[i];
			}
			newArray[0] = temp;
			return DoExchange(newArray);
		} else {
			return strJaggedArray;
		}
	}

	/**
	 * 提取每个汉字的首字母
	 * @param str
	 * @return String
	 */
	public static String getPinYinHeadChar(String str) {
		String convert = "";
		for (int j = 0; j < str.length(); j++) {
			char word = str.charAt(j);
			// 提取汉字的首字母
			String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);
			if (pinyinArray != null) {
				convert += pinyinArray[0].charAt(0);
			} else {
				convert += word;
			}
		}
		return convert;
	}

	/**
	 * 将字符串转换成ASCII码
	 * @param cnStr
	 * @return String
	 */
	public static String getCnASCII(String cnStr) {
		StringBuffer strBuf = new StringBuffer();
		// 将字符串转换成字节序列
		byte[] bGBK = cnStr.getBytes();
		for (int i = 0; i < bGBK.length; i++) {
			// 将每个字符转换成ASCII码
			strBuf.append(Integer.toHexString(bGBK[i] & 0xff));
		}
		return strBuf.toString();
	}
	
	public static List<ContentValues> orderByPinyin(List<ContentValues> targetArr,String key){//小到大的排序
		ContentValues temp = new ContentValues();
        for(int i = 0; i < targetArr.size(); i++){
            for(int j = i;j<targetArr.size();j++){
                if(targetArr.get(i).get(key).toString().compareToIgnoreCase(targetArr.get(j).get(key)+"") > 0){

                   //方法一：
                    temp = targetArr.get(i);
                    targetArr.set(i, targetArr.get(j));
                    targetArr.set(j, temp);
                    //targetArr.get(i) = targetArr.get(j);
                    //targetArr.get(j) = temp;

                    /*//方法二:
                    targetArr[i] = targetArr[i] + targetArr[j];
                    targetArr[j] = targetArr[i] - targetArr[j];
                    targetArr[i] = targetArr[i] - targetArr[j];*/
                   }
             }
        }
        return targetArr;
    }
	
	/**
	 * 数字对应的T9字母数组
	 * @param digit
	 * @return char[]
	 */
	public static char[] digit2Char(int digit) {
		char[] cs = null;
		switch (digit) {
		case 0:
			cs = new char[] {};
			break;
		case 1:
			break;
		case 2:
			cs = new char[] { 'a', 'b', 'c' };
			break;
		case 3:
			cs = new char[] { 'd', 'e', 'f' };
			break;
		case 4:
			cs = new char[] { 'g', 'h', 'i' };
			break;
		case 5:
			cs = new char[] { 'j', 'k', 'l' };
			break;
		case 6:
			cs = new char[] { 'm', 'n', 'o' };
			break;
		case 7:
			cs = new char[] { 'p', 'q', 'r', 's' };
			break;
		case 8:
			cs = new char[] { 't', 'u', 'v' };
			break;
		case 9:
			cs = new char[] { 'w', 'x', 'y', 'z' };
			break;
		}
		return cs;
	}
	
	
	/**
	 * 字母对应的T9数字
	 * @param firstAlpha
	 * @return char
	 */
	public static char getOneNumFromAlpha(char firstAlpha) {
		switch (firstAlpha) {
		case 'a':
		case 'b':
		case 'c':
			return '2';
		case 'd':
		case 'e':
		case 'f':
			return '3';
		case 'g':
		case 'h':
		case 'i':
			return '4';
		case 'j':
		case 'k':
		case 'l':
			return '5';
		case 'm':
		case 'n':
		case 'o':
			return '6';
		case 'p':
		case 'q':
		case 'r':
		case 's':
			return '7';
		case 't':
		case 'u':
		case 'v':
			return '8';
		case 'w':
		case 'x':
		case 'y':
		case 'z':
			return '9';
		default:
			return '0';
		}
	}
	
	
	/**
	 * 字符串字母对应的T9数字
	 * @param pingyin
	 * @return String
	 */
	public static String getPinyinNum(String pingyin) {
		pingyin = pingyin.replaceAll(" ", "");
		if (pingyin != null && pingyin.length() != 0) {
			int len = pingyin.length();
			char[] nums = new char[len];
			for (int i = 0; i < len; i++) {
				String tmp = pingyin.substring(i);
				nums[i] = getOneNumFromAlpha(tmp.toLowerCase().charAt(0));
			}
			return new String(nums);
		}
		return null;
	}
	
	
	/**
	 * 字符串首字母对应的T9数字
	 * @param pingyin
	 * @return String
	 */
	public static String getPinyinHeadNum(String pingyin) {
		int i = 0;
		String pinyinNum = "";
		if (pingyin != null && pingyin.length() != 0) {
			for(String st:pingyin.split(",")){
				String onnum = "";
				for(String st2 : st.split(" ")){
					
					if(StringHelper.isEmpty(st2))
						continue;
					
					onnum += getPinyinNum(st2.trim().charAt(0)+"");
				}
				pinyinNum += onnum;
				if (i != pingyin.split(",").length-1 ) {
					pinyinNum += ",";
				}
				i++;
			}
		}
		return pinyinNum;
	}
	
	

	public static void main(String[] args) {
		String cnStr = "陈好很好,深圳是个很美很发达的城市";
		System.out.println(makeStringByStringSet(getPinyin(cnStr)));
		System.out.println(getPinYinHeadChar(cnStr));
		System.out.println(getCnASCII(cnStr));
	}
}