package com.magic.processing.concurrent.collection;

import com.magic.processing.commons.helper.ProcessingHelper;
import com.magic.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 并发处理Collection里的元素（同步）
 */
public class ConcurrentCollectionSync {

    private Logger logger = LoggerFactory.getLogger(ConcurrentCollectionSync.class);

    /**
     * 将一个Collection分成若干组，排队执行，每组内部并发执行
     * 同步执行
     * 默认分成10组，超时时间10分钟
     *
     * @param dataList                   数据集
     * @param concurrentCollectionRunner 执行器
     * @param <T>
     */
    public <T> void syncRunner(Collection<T> dataList,
                               ConcurrentCollectionRunner<T> concurrentCollectionRunner) {
        syncRunner(dataList, concurrentCollectionRunner, 10, 10, TimeUnit.MINUTES);
    }

    /**
     * 将一个Collection分成若干组，排队执行，每组内部并发执行
     * 同步执行
     * 默认超时时间10分钟
     *
     * @param dataList                   数据集
     * @param concurrentCollectionRunner 执行器
     * @param groupSize                  每组大小，这个大小就决定了会同时开几个线程
     * @param <T>
     */
    public <T> void syncRunner(Collection<T> dataList,
                               ConcurrentCollectionRunner<T> concurrentCollectionRunner,
                               int groupSize) {
        syncRunner(dataList, concurrentCollectionRunner, groupSize, 10, TimeUnit.MINUTES);
    }

    /**
     * 将一个Collection分成若干组，每一组分别用一个线程执行
     * 同步执行
     * 默认分成10组，超时时间10分钟
     *
     * @param dataList                        数据集
     * @param concurrentCollectionGroupRunner 执行器
     * @param <T>
     */
    public <T> void syncGroupRunner(Collection<T> dataList,
                                    ConcurrentCollectionGroupRunner<T> concurrentCollectionGroupRunner) {
        syncGroupRunner(dataList, concurrentCollectionGroupRunner, 10, 10, TimeUnit.MINUTES);
    }

    /**
     * 将一个Collection分成若干组，每一组分别用一个线程执行
     * 同步执行
     * 默认超时时间10分钟
     *
     * @param dataList                        数据集
     * @param concurrentCollectionGroupRunner 执行器
     * @param groupSize                       每组大小，这个大小就决定了会同时开几个线程
     * @param <T>
     */
    public <T> void syncGroupRunner(Collection<T> dataList,
                                    ConcurrentCollectionGroupRunner<T> concurrentCollectionGroupRunner,
                                    int groupSize) {
        syncGroupRunner(dataList, concurrentCollectionGroupRunner, groupSize, 10, TimeUnit.MINUTES);
    }

    /**
     * 将一个Collection分成若干组，每一组分别用一个线程执行
     * 同步执行
     *
     * @param dataList                        数据集
     * @param concurrentCollectionGroupRunner 执行器
     * @param groupSize                       每组多少条，它关系到能分成多少组，而组数就决定了会同时开几个线程
     * @param timeout                         每一组的超时时间，单位由unit参数设置
     * @param unit                            超时时间单位
     * @param <T>
     */
    public <T> void syncGroupRunner(Collection<T> dataList,
                                    ConcurrentCollectionGroupRunner<T> concurrentCollectionGroupRunner,
                                    int groupSize,
                                    long timeout,
                                    TimeUnit unit) {

        try {
            // 将集合分成若干组，每组元素由调用者指定
            Collection<Collection<T>> dataGroup = CollectionUtils.partition(dataList, groupSize);


            CountDownLatch count = new CountDownLatch(dataGroup.size());

            // 给每一组都开一个线程，并发执行
            for (Collection<T> dataItem : dataGroup) {
                new Thread(() -> {
                    try {
                        concurrentCollectionGroupRunner.run(dataItem);
                    } catch (Throwable e) {
                        logger.error("ConcurrentProcessing syncGroupRunner error", e);
                    } finally {
                        count.countDown();
                    }
                }).start();
            }

            // 等所有线程执行结束后，或者超时后，再跳出此方法
            ProcessingHelper.runnerAwait(timeout, unit, count);
        } catch (Exception e) {
            logger.error("ConcurrentProcessingSync syncGroupRunner error", e);
            throw e;
        }
    }

    /**
     * 将一个Collection分成若干组，排队执行，每组内部并发执行
     * 同步执行
     *
     * @param dataList                   数据集
     * @param concurrentCollectionRunner 执行器
     * @param groupSize                  每组大小，这个大小就决定了会同时开几个线程
     * @param timeout                    每一组的超时时间，单位由unit参数设置
     * @param unit                       超时时间单位
     * @param <T>
     */
    public <T> void syncRunner(Collection<T> dataList,
                               ConcurrentCollectionRunner<T> concurrentCollectionRunner,
                               int groupSize,
                               long timeout,
                               TimeUnit unit) {

        try {
            // 将集合分成若干组，每组大小由调用者指定
            Collection<Collection<T>> dataGroup = CollectionUtils.partition(dataList, groupSize);

            for (Collection<T> dataItem : dataGroup) {

                CountDownLatch count = new CountDownLatch(dataItem.size());

                // 给组内的每一个元素都开一个线程，并发执行
                for (T item : dataItem) {
                    new Thread(() -> {
                        try {
                            concurrentCollectionRunner.run(item);
                        } catch (Throwable e) {
                            logger.error("ConcurrentProcessing syncRunner error", e);
                        } finally {
                            count.countDown();
                        }
                    }).start();
                }

                // 等所有线程执行结束后，或者超时后，再执行下一组
                ProcessingHelper.runnerAwait(timeout, unit, count);
            }
        } catch (Exception e) {
            logger.error("ConcurrentProcessingSync syncRunner error", e);
            throw e;
        }
    }
}
