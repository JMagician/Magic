package com.magician.tools.util;

import java.util.ArrayList;
import java.util.Collection;

/**
 * 集合工具类
 */
public class CollectionUtils {

    /**
     * 判断是否为空
     * @param collection
     * @return
     */
    public static boolean isEmpty(Collection collection){
        if(collection == null || collection.size() == 0){
            return true;
        }
        return false;
    }

    /**
     * 判断是否不是空
     * @param collection
     * @return
     */
    public static boolean isNotEmpty(Collection collection){
        return isEmpty(collection) == false;
    }

    /**
     * 将list分成若干组，每组size条
     *
     * @param list 需要分组的集合
     * @param size 每组分多少条
     * @return
     * @param <T>
     */
    public static <T> Collection<Collection<T>> partition(Collection<T> list, int size) {
        if(isEmpty(list)){
            throw new NullPointerException("list is null");
        }

        if(size <= 0){
            throw new IllegalArgumentException("size cannot <= 0");
        }
        Collection<Collection<T>> array = new ArrayList<>();

        Collection<T> groupItem = new ArrayList<>();
        for(T t : list){
            if(groupItem.size() < size){
                groupItem.add(t);
                continue;
            }
            array.add(groupItem);

            groupItem = new ArrayList<>();
            groupItem.add(t);
        }

        if(groupItem.size() > 0){
            array.add(groupItem);
        }

        return array;
    }
}
