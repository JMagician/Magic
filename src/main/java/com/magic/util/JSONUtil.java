package com.magic.util;

import com.alibaba.fastjson2.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * JSON 工具类
 */
public class JSONUtil {

    private static Logger logger = LoggerFactory.getLogger(JSONUtil.class);

    /**
     * 将JSON字符串或者一个实体对象转化成指定类型的实体对象
     * @param obj
     * @param cls
     * @param <T>
     * @return
     */
    public static  <T> T toJavaObject(Object obj, Class<T> cls) {
        if(obj == null){
            return null;
        }
        try {
            if(obj instanceof String){
                return JSON.parseObject(obj.toString(), cls);
            } else {
                return JSON.parseObject(JSON.toJSONString(obj), cls);
            }
        } catch (Exception e){
            logger.error("JSONUtil toJavaObject error", e);
            return null;
        }
    }

    /**
     * 将JSON字符串或者一个实体对象转化成Map集合
     * @param obj
     * @return
     */
    public static Map<String, Object> toMap(Object obj) {
        if(obj instanceof Map){
            return (Map<String, Object>) obj;
        }
        Map<String, Object> map = toJavaObject(obj, HashMap.class);
        if(map == null){
            return new HashMap<>();
        }
        return map;
    }

    /**
     * 将任意对象转化成JSON字符串
     * @param obj
     * @return
     */
    public static String toJSONString(Object obj) {
        if(obj == null){
            return "";
        }
        try {
            if(obj instanceof String){
                return obj.toString();
            } else {
                return JSON.toJSONString(obj);
            }
        } catch (Exception e){
            logger.error("Convert object to JSON string exception", e);
            return "";
        }
    }
}
