package com.weishao.springtoken.utils;

import org.springframework.util.DigestUtils;

/**
 * MD5哈希工具类型
 * 
 * @author Tang
 *
 */
public class MD5ToolUtil {

	public static String MD5Code(String str) {
		return DigestUtils.md5DigestAsHex(str.getBytes());
	}
	
}
