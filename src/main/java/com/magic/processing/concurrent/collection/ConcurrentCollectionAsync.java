package com.magic.processing.concurrent.collection;

import com.magic.processing.commons.helper.ProcessingHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

/**
 * 并发处理Collection里的元素（异步）
 */
public class ConcurrentCollectionAsync {

    /**
     * 异步任务集合
     */
    private List<Runnable> runnableList;

    public ConcurrentCollectionAsync() {
        runnableList = new ArrayList<>();
    }

    /**
     * 并发执行器
     */
    private ConcurrentCollectionSync concurrentCollectionSync = new ConcurrentCollectionSync();

    /**
     * 将一个Collection分成若干组，排队执行，每组内部并发执行
     * 异步执行
     * 默认分成10组，超时时间10分钟
     *
     * @param dataList                   数据集
     * @param concurrentCollectionRunner 执行器
     * @param <T>
     */
    public <T> ConcurrentCollectionAsync asyncRunner(Collection<T> dataList,
                                ConcurrentCollectionRunner<T> concurrentCollectionRunner) {
        return asyncRunner(dataList, concurrentCollectionRunner, 10, 10, TimeUnit.MINUTES);
    }

    /**
     * 将一个Collection分成若干组，排队执行，每组内部并发执行
     * 异步执行
     * 默认超时时间10分钟
     *
     * @param dataList                   数据集
     * @param concurrentCollectionRunner 执行器
     * @param <T>
     */
    public <T> ConcurrentCollectionAsync asyncRunner(Collection<T> dataList,
                                ConcurrentCollectionRunner<T> concurrentCollectionRunner,
                                int groupSize) {
        return asyncRunner(dataList, concurrentCollectionRunner, groupSize, 10, TimeUnit.MINUTES);
    }

    /**
     * 将一个Collection分成若干组，每一组分别用一个线程执行
     * 异步执行
     * 默认分成10组，超时时间10分钟
     *
     * @param dataList                        数据集
     * @param concurrentCollectionGroupRunner 执行器
     * @param <T>
     */
    public <T> ConcurrentCollectionAsync asyncGroupRunner(Collection<T> dataList,
                                     ConcurrentCollectionGroupRunner<T> concurrentCollectionGroupRunner) {
        return asyncGroupRunner(dataList, concurrentCollectionGroupRunner, 10, 10, TimeUnit.MINUTES);
    }

    /**
     * 将一个Collection分成若干组，每一组分别用一个线程执行
     * 异步执行
     * 默超时时间10分钟
     *
     * @param dataList                        数据集
     * @param concurrentCollectionGroupRunner 执行器
     * @param <T>
     */
    public <T> ConcurrentCollectionAsync asyncGroupRunner(Collection<T> dataList,
                                     ConcurrentCollectionGroupRunner<T> concurrentCollectionGroupRunner,
                                     int groupSize) {
        return asyncGroupRunner(dataList, concurrentCollectionGroupRunner, groupSize, 10, TimeUnit.MINUTES);
    }

    /**
     * 将一个Collection分成若干组，排队执行，每组内部并发执行
     * 异步执行
     *
     * @param dataList                   数据集
     * @param concurrentCollectionRunner 执行器
     * @param groupSize                  每组大小，这个大小就决定了会同时开几个线程
     * @param timeout                    每一组的超时时间，单位由unit参数设置
     * @param unit                       超时时间单位
     * @param <T>
     */
    public <T> ConcurrentCollectionAsync asyncRunner(Collection<T> dataList,
                                ConcurrentCollectionRunner<T> concurrentCollectionRunner,
                                int groupSize,
                                long timeout,
                                TimeUnit unit) {

        runnableList.add(() -> {
            concurrentCollectionSync.syncRunner(dataList, concurrentCollectionRunner, groupSize, timeout, unit);
        });

        return this;
    }

    /**
     * 将一个Collection分成若干组，每一组分别用一个线程执行
     * 异步执行
     *
     * @param dataList                        数据集
     * @param concurrentCollectionGroupRunner 执行器
     * @param groupSize                       每组多少条，它关系到能分成多少组，而组数就决定了会同时开几个线程
     * @param timeout                         每一组的超时时间，单位由unit参数设置
     * @param unit                            超时时间单位
     * @param <T>
     */
    public <T> ConcurrentCollectionAsync asyncGroupRunner(Collection<T> dataList,
                                     ConcurrentCollectionGroupRunner<T> concurrentCollectionGroupRunner,
                                     int groupSize,
                                     long timeout,
                                     TimeUnit unit) {

        runnableList.add(() -> {
            concurrentCollectionSync.syncGroupRunner(dataList, concurrentCollectionGroupRunner, groupSize, timeout, unit);
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
