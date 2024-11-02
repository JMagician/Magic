package com.magic.util;

import com.fasterxml.jackson.databind.ObjectMapper;
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
            ObjectMapper objectMapper = new ObjectMapper();
            if(obj instanceof String){
                return objectMapper.readValue(obj.toString(), cls);
            } else {
                return objectMapper.readValue(objectMapper.writeValueAsString(obj), cls);
            }
        } catch (Exception e){
            logger.error("An exception occurs when converting an object to another Java object through jackson", e);
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
            ObjectMapper objectMapper = new ObjectMapper();
            if(obj instanceof String){
                return obj.toString();
            } else {
                return objectMapper.writeValueAsString(obj);
            }
        } catch (Exception e){
            logger.error("Convert object to JSON string exception", e);
            return "";
        }
    }
}
