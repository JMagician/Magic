package com.magic.processing.concurrent.collection;


import java.util.Collection;

/**
 * 执行器，每次执行一组数据
 * @param <T>
 */
public interface ConcurrentCollectionGroupRunner<T> {

    void run(Collection<T> t) throws Throwable;
}
