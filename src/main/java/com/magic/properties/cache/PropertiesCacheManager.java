package com.magic.properties.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 本地缓存管理
 */
public class PropertiesCacheManager {

    /**
     * 本地缓存数据
     */
    private static Map<String, String> propertiesMap = new ConcurrentHashMap<>();

    public static Map<String, String> getPropertiesMap() {
        return propertiesMap;
    }

    public static void add(String key, String value){
        propertiesMap.put(key, value);
    }
}
