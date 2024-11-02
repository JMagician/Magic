package com.magic.processing.concurrent.map;

import com.magic.processing.commons.helper.ProcessingHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 并发处理Map里的元素（异步）
 */
public class ConcurrentMapAsync {

    /**
     * 异步任务集合
     */
    private List<Runnable> runnableList;

    public ConcurrentMapAsync() {
        runnableList = new ArrayList<>();
    }

    /**
     * 并发执行器
     */
    private ConcurrentMapSync concurrentMapSync = new ConcurrentMapSync();

    /**
     * 将一个Map分成若干组，排队执行，每组内部并发执行
     * 同步执行
     * 默认分成10组，超时时间10分钟
     *
     * @param dataMap             数据集
     * @param concurrentMapRunner 执行器
     * @param <K,                 V>
     */
    public <K, V> ConcurrentMapAsync asyncRunner(Map<K, V> dataMap,
                                   ConcurrentMapRunner<K, V> concurrentMapRunner) {
        return asyncRunner(dataMap, concurrentMapRunner, 10, 10, TimeUnit.MINUTES);
    }

    /**
     * 将一个Map分成若干组，排队执行，每组内部并发执行
     * 同步执行
     * 默认超时时间10分钟
     *
     * @param dataMap             数据集
     * @param concurrentMapRunner 执行器
     * @param groupSize           每组大小，这个大小就决定了会同时开几个线程
     * @param <K,                 V>
     */
    public <K, V> ConcurrentMapAsync asyncRunner(Map<K, V> dataMap,
                                   ConcurrentMapRunner<K, V> concurrentMapRunner,
                                   int groupSize) {
        return asyncRunner(dataMap, concurrentMapRunner, groupSize, 10, TimeUnit.MINUTES);
    }

    /**
     * 将一个Map分成若干组，每一组分别用一个线程执行
     * 同步执行
     * 默认分成10组，超时时间10分钟
     *
     * @param dataMap                  数据集
     * @param concurrentMapGroupRunner 执行器
     * @param <K,                      V>
     */
    public <K, V> ConcurrentMapAsync asyncGroupRunner(Map<K, V> dataMap,
                                        ConcurrentMapGroupRunner<K, V> concurrentMapGroupRunner) {
        return asyncGroupRunner(dataMap, concurrentMapGroupRunner, 10, 10, TimeUnit.MINUTES);
    }

    /**
     * 将一个Map分成若干组，每一组分别用一个线程执行
     * 同步执行
     * 默认超时时间10分钟
     *
     * @param dataMap                  数据集
     * @param concurrentMapGroupRunner 执行器
     * @param groupSize                每组大小，这个大小就决定了会同时开几个线程
     * @param <K,                      V>
     */
    public <K, V> ConcurrentMapAsync asyncGroupRunner(Map<K, V> dataMap,
                                        ConcurrentMapGroupRunner<K, V> concurrentMapGroupRunner,
                                        int groupSize) {
        return asyncGroupRunner(dataMap, concurrentMapGroupRunner, groupSize, 10, TimeUnit.MINUTES);
    }

    /**
     * 将一个Map分成若干组，每一组分别用一个线程执行
     * 同步执行
     *
     * @param dataMap                  数据集
     * @param concurrentMapGroupRunner 执行器
     * @param groupSize                每组多少条，它关系到能分成多少组，而组数就决定了会同时开几个线程
     * @param timeout                  每一组的超时时间，单位由unit参数设置
     * @param unit                     超时时间单位
     * @param <K,                      V>
     */
    public <K, V> ConcurrentMapAsync asyncGroupRunner(Map<K, V> dataMap,
                                        ConcurrentMapGroupRunner<K, V> concurrentMapGroupRunner,
                                        int groupSize,
                                        long timeout,
                                        TimeUnit unit) {
        runnableList.add(() -> {
            concurrentMapSync.syncGroupRunner(dataMap, concurrentMapGroupRunner, groupSize, timeout, unit);
        });

        return this;
    }


    /**
     * 将一个Map分成若干组，排队执行，每组内部并发执行
     * 同步执行
     *
     * @param dataMap             数据集
     * @param concurrentMapRunner 执行器
     * @param groupSize           每组大小，这个大小就决定了会同时开几个线程
     * @param timeout             每一组的超时时间，单位由unit参数设置
     * @param unit                超时时间单位
     * @param <K,                 V>
     */
    public <K, V> ConcurrentMapAsync asyncRunner(Map<K, V> dataMap,
                                   ConcurrentMapRunner<K, V> concurrentMapRunner,
                                   int groupSize,
                                   long timeout,
                                   TimeUnit unit) {
        runnableList.add(() -> {
            concurrentMapSync.syncRunner(dataMap, concurrentMapRunner, groupSize, timeout, unit);
        });

        return this;
    }

    /**
     * 启动所有异步任务
     */
    public void start(){
        ProcessingHelper.start(runnableList);
    }
}
