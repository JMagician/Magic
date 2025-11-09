package com.magic.util;

import com.alibaba.fastjson2.annotation.JSONField;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据库参数工具类
 */
public class DBParamUtil {

    /**
     * 将参数转化成Map，统一处理
     * @param data
     * @return
     * @throws IllegalAccessException
     */
    public static Map<String, Object> getParamMap(Object data) throws IllegalAccessException {
        if(data instanceof Map){
            return (Map<String, Object>) data;
        }

        Map<String, Object> paramMap = new HashMap<>();

        Class cls = data.getClass();

        while (!cls.equals(Object.class)){
            paramMap.putAll(getMap(cls, data));
            cls = cls.getSuperclass();

            if(cls == null){
                break;
            }
        }

        return paramMap;
    }

    /**
     * 根据class和它的对象创建一个map
     * @param cls
     * @param obj
     * @return
     * @throws IllegalAccessException
     */
    private static Map<String, Object> getMap(Class cls, Object obj) throws IllegalAccessException {
        Map<String, Object> paramMap = new HashMap<>();

        for (Field field : cls.getDeclaredFields()) {
            field.setAccessible(true);
            Object value = field.get(obj);
            if (value == null) {
                continue;
            }

            String fieldName = field.getName();

            JSONField jsonField = field.getAnnotation(JSONField.class);
            if(jsonField != null && StringUtils.isNotEmpty(jsonField.name())){
                fieldName = jsonField.name();
            }

            paramMap.put(fieldName, value);
        }

        return paramMap;
    }
}
