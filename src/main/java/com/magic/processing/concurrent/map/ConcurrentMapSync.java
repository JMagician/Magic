package com.magic.processing.concurrent.map;

import com.magic.processing.commons.helper.ProcessingHelper;
import com.magic.util.MapsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 并发处理Map里的元素（同步）
 */
public class ConcurrentMapSync {

    private Logger logger = LoggerFactory.getLogger(ConcurrentMapSync.class);

    /**
     * 将一个Map分成若干组，排队执行，每组内部并发执行
     * 同步执行
     * 默认分成10组，超时时间10分钟
     *
     * @param dataMap             数据集
     * @param concurrentMapRunner 执行器
     * @param <K,                 V>
     */
    public <K, V> void syncRunner(Map<K, V> dataMap,
                                  ConcurrentMapRunner<K, V> concurrentMapRunner) {
        syncRunner(dataMap, concurrentMapRunner, 10, 10, TimeUnit.MINUTES);
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
    public <K, V> void syncRunner(Map<K, V> dataMap,
                                  ConcurrentMapRunner<K, V> concurrentMapRunner,
                                  int groupSize) {
        syncRunner(dataMap, concurrentMapRunner, groupSize, 10, TimeUnit.MINUTES);
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
    public <K, V> void syncGroupRunner(Map<K, V> dataMap,
                                       ConcurrentMapGroupRunner<K, V> concurrentMapGroupRunner) {
        syncGroupRunner(dataMap, concurrentMapGroupRunner, 10, 10, TimeUnit.MINUTES);
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
    public <K, V> void syncGroupRunner(Map<K, V> dataMap,
                                       ConcurrentMapGroupRunner<K, V> concurrentMapGroupRunner,
                                       int groupSize) {
        syncGroupRunner(dataMap, concurrentMapGroupRunner, groupSize, 10, TimeUnit.MINUTES);
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
    public <K, V> void syncGroupRunner(Map<K, V> dataMap,
                                       ConcurrentMapGroupRunner<K, V> concurrentMapGroupRunner,
                                       int groupSize,
                                       long timeout,
                                       TimeUnit unit) {
        try {
            List<Map<K, V>> dataGroup = MapsUtil.partition(dataMap, groupSize);

            ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(dataGroup.size(),
                    dataGroup.size(),
                    1,
                    TimeUnit.MINUTES,
                    new LinkedBlockingQueue<>());

            CountDownLatch count = new CountDownLatch(poolExecutor.getCorePoolSize());

            // 给每一组都开一个线程，并发执行
            for (Map<K, V> dataItem : dataGroup) {
                poolExecutor.submit(() -> {
                    try {
                        concurrentMapGroupRunner.run(dataItem);
                    } catch (Throwable e) {
                        logger.error("ConcurrentMapSync syncGroupRunner error", e);
                    } finally {
                        count.countDown();
                    }
                });
            }

            // 等所有线程执行结束后，或者超时后，再跳出此方法
            ProcessingHelper.runnerAwait(timeout, unit, count, poolExecutor);

        } catch (Exception e) {
            logger.error("ConcurrentMapSync syncGroupRunner error", e);
            throw e;
        }
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
    public <K, V> void syncRunner(Map<K, V> dataMap,
                                  ConcurrentMapRunner<K, V> concurrentMapRunner,
                                  int groupSize,
                                  long timeout,
                                  TimeUnit unit) {

        try {
            List<Map<K, V>> dataGroup = MapsUtil.partition(dataMap, groupSize);


            for (Map<K, V> dataItem : dataGroup) {

                ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(dataItem.size(),
                        dataItem.size(),
                        1,
                        TimeUnit.MINUTES,
                        new LinkedBlockingQueue<>());

                CountDownLatch count = new CountDownLatch(poolExecutor.getCorePoolSize());

                // 给组内的每一个元素都开一个线程，并发执行
                for (Map.Entry<K, V> entry : dataItem.entrySet()) {
                    poolExecutor.submit(() -> {
                        try {
                            concurrentMapRunner.run(entry.getKey(), entry.getValue());
                        } catch (Throwable e) {
                            logger.error("ConcurrentMapSync syncRunner error", e);
                        } finally {
                            count.countDown();
                        }
                    });
                }

                // 等所有线程执行结束后，或者超时后，再执行下一组
                ProcessingHelper.runnerAwait(timeout, unit, count, poolExecutor);
            }
        } catch (Exception e) {
            logger.error("ConcurrentMapSync syncRunner error", e);
            throw e;
        }
    }
}
