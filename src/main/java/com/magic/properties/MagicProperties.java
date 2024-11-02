package com.magic.properties;

import com.magic.util.MapsUtil;
import com.magic.properties.cache.PropertiesCacheManager;
import com.magic.properties.enums.ReadMode;
import com.magic.properties.load.LoadProperties;
import com.magic.properties.load.PropertiesEach;

import java.util.Map;

/**
 * 模块入口
 * 从这里，从这里你可以轻松加载和读取properties文件
 */
public class MagicProperties {

    /**
     * 默认的文件编码
     */
    private static final String CHARSET_NAME = "UTF-8";

    /**
     * 加载properties文件类
     */
    private static LoadProperties loadProperties = new LoadProperties();

    /**
     * 根据key获取value，如果文件里没有，会自动去环境变量里找
     * @param key
     * @return
     */
    public static String get(String key){
        String value = PropertiesCacheManager.getPropertiesMap().get(key);
        if(value == null){
            value = System.getenv(key);
        }
        return value;
    }

    /**
     * 加载properties文件到本地缓存
     * @param path
     * @param readMode
     * @throws Exception
     */
    public static void load(String path, ReadMode readMode) throws Exception {
        loadProperties.load(path, readMode, CHARSET_NAME);
    }

    /**
     * 加载properties文件里的键值对到本地缓存中
     * @param path
     * @param readMode
     * @param charset
     * @throws Exception
     */
    public static void load(String path, ReadMode readMode, String charset) throws Exception {
        loadProperties.load(path, readMode, charset);
    }

    /**
     * 遍历properties里的键值对
     * @param propertiesEach
     */
    public static void forEach(PropertiesEach propertiesEach){
        if(MapsUtil.isEmpty(PropertiesCacheManager.getPropertiesMap())){
            throw new NullPointerException("propertiesMap is empty");
        }

        for(Map.Entry<String, String> entry : PropertiesCacheManager.getPropertiesMap().entrySet()){
            propertiesEach.each(entry.getKey(), entry.getValue());
        }
    }
}
