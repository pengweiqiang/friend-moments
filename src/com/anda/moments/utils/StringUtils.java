package com.anda.moments.utils;

import android.annotation.SuppressLint;
import java.security.MessageDigest;
import java.text.DecimalFormat;

/**
 * 字符串操作工具包
 */
@SuppressLint("DefaultLocale")
public class StringUtils {
	
	/**
	 * 判断给定字符串是否空白串。 空白串是指由空格、制表符、回车符、换行符组成的字符串 若输入字符串为null或空字符串，返回true
	 * 
	 * @param input
	 * @return boolean
	 */
	public static boolean isEmpty(String input) {
		if (input == null || "".equals(input))
			return true;
		
		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 字符串转整数
	 * 
	 * @param str
	 * @param defValue
	 * @return
	 */
	public static int toInt(String str, int defValue) {
		try {
			return Integer.parseInt(str);
		} catch (Exception e) {
		}
		return defValue;
	}
	
	/**
	 * 对象转整数
	 * 
	 * @param obj
	 * @return 转换异常返回 0
	 */
	public static int toInt(Object obj) {
		if (obj == null)
			return 0;
		return toInt(obj.toString(), 0);
	}
	
	/**
	 * 对象转整数
	 * 
	 * @param obj
	 * @return 转换异常返回 0
	 */
	public static double toDouble(String Str) {
		if (Str == null)
			return 0;
		return Double.parseDouble(Str);
	}
	
	/**
	 * 对象转整数
	 * 
	 * @param obj
	 * @return 转换异常返回 0
	 */
	public static long toLong(String obj) {
		try {
			return Long.parseLong(obj);
		} catch (Exception e) {
		}
		return 0;
	}
	
	/**
	 * 字符串转布尔值
	 * 
	 * @param b
	 * @return 转换异常返回 false
	 */
	public static boolean toBool(String b) {
		try {
			return Boolean.parseBoolean(b);
		} catch (Exception e) {
		}
		return false;
	}
	
	/**
	 * 字符串md5加密
	 * 
	 * @param paramString
	 * @return
	 */
	public static String md5(String paramString) {
		String returnStr;
		try {
			MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
			localMessageDigest.update(paramString.getBytes());
			returnStr = byteToHexString(localMessageDigest.digest());
			return returnStr;
		} catch (Exception e) {
			return paramString;
		}
	}
	
	/**
	 * 将指定byte数组转换成16进制字符串
	 * 
	 * @param b
	 * @return
	 */
	public static String byteToHexString(byte[] b) {
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			hexString.append(hex.toUpperCase());
		}
		return hexString.toString();
	}
	
	public static String getFormatSize(String aSize) {
		float size = Float.parseFloat(aSize);
		DecimalFormat df = new DecimalFormat("#.##");
		if (size <= 0) {
			return "0";
		}
		if (size < 1024) {
			return df.format(size) + "B";
		} else if (size <= 1024 * 1024) {
			return df.format(size / 1024f) + "KB";
		} else if (size <= 1024 * 1024 * 1024) {
			return df.format(size / 1024 / 1024f) + "MB";
		} else if (size <= 1024 * 1024 * 1024 * 1024) {
			return df.format(size / 1024 / 1024 / 1024f) + "GB";
		} else {
			return "0";
		}
	}
	public static String ToDBC(String input) {
		if(isEmpty(input)){
			return "";
		}
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {              
        if (c[i] == 12288) {                 
        c[i] = (char) 32;                  
        continue;
         }
         if (c[i] > 65280 && c[i] < 65375)
            c[i] = (char) (c[i] - 65248);
        }
        return new String(c);
    }  
}