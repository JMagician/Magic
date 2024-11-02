package com.magic.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Map工具类
 */
public class MapsUtil {

    /**
     * 判断是否为空
     * @param dataMap
     * @return
     */
    public static boolean isEmpty(Map dataMap){
        if(dataMap == null || dataMap.size() == 0){
            return true;
        }

        return false;
    }

    /**
     * 判断是否不为空
     * @param dataMap
     * @return
     */
    public static boolean isNotEmpty(Map dataMap){
        return isEmpty(dataMap) == false;
    }

    /**
     * 将dataMap 分成 若干组，每组size条
     * @param dataMap 需要分组的Map
     * @param size 每组分多少条
     * @return
     * @param <K>
     * @param <V>
     */
    public static <K, V> List<Map<K, V>> partition(Map<K, V> dataMap, int size) {
        if(isEmpty(dataMap)){
            throw new NullPointerException("dataMap is empty");
        }
        if(size <= 0){
            throw new IllegalArgumentException("size cannot <= 0");
        }

        List<Map<K, V>> dataList = new ArrayList<>();

        Map<K, V> mapGroup = new HashMap<>();

        for(Map.Entry<K, V> entry : dataMap.entrySet()){
            if(mapGroup.size() < size){
                mapGroup.put(entry.getKey(), entry.getValue());
                continue;
            }

            dataList.add(mapGroup);

            mapGroup = new HashMap<>();
            mapGroup.put(entry.getKey(), entry.getValue());
        }

        if(mapGroup.size() > 0){
            dataList.add(mapGroup);
        }

        return dataList;
    }
}
