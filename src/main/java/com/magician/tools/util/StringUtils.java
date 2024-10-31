package com.magician.tools.util;

/**
 * 字符串工具类
 */
public class StringUtils {

    /**
     * 判断是否为空
     * @param s
     * @return
     */
    public static boolean isEmpty(String s){
        if(s == null || s.trim().equals("")){
            return true;
        }
        return false;
    }

    /**
     * 判断是否不为空
     * @param s
     * @return
     */
    public static boolean isNotEmpty(String s){
        return isEmpty(s) == false;
    }
}
