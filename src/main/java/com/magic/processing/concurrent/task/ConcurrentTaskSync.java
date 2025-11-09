package com.magic.processing.concurrent.task;

import com.magic.processing.commons.helper.ProcessingHelper;
import com.magic.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 并发处理调用者指定的任务
 */
public class ConcurrentTaskSync {

    private Logger logger = LoggerFactory.getLogger(ConcurrentTaskSync.class);

    /**
     * 任务集合
     */
    private List<Runnable> concurrentTaskList = new ArrayList<>();

    /**
     * 超时时间
     */
    private long timeout;

    /**
     * 超时时间的单位
     */
    private TimeUnit timeUnit;

    public ConcurrentTaskSync() {
        this.timeout = 1;
        this.timeUnit = TimeUnit.MINUTES;
    }

    /**
     * 添加一个需要任务
     *
     * @param runnable           任务
     * @return
     */
    public ConcurrentTaskSync add(Runnable runnable) {
        concurrentTaskList.add(runnable);
        return this;
    }

    /**
     * 设置超时时间
     *
     * @param timeout
     * @return
     */
    public ConcurrentTaskSync setTimeout(long timeout) {
        this.timeout = timeout;
        return this;
    }

    /**
     * 设置超时时间的单位
     *
     * @param timeUnit
     * @return
     */
    public ConcurrentTaskSync setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
        return this;
    }

    /**
     * 开始执行任务
     */
    public void start() {
        // 如果任务为空就抛出异常
        if (CollectionUtils.isEmpty(concurrentTaskList)) {
            throw new NullPointerException("ConcurrentTaskSync concurrentTaskList is empty");
        }

        CountDownLatch count = new CountDownLatch(concurrentTaskList.size());

        // 将集合里的任务全部添加到线程池
        for (Runnable runnable : concurrentTaskList) {
            new Thread(() -> {
                try {
                    runnable.run();
                } catch (Throwable e) {
                    logger.error("ConcurrentTaskSync error, className:{}", runnable.getClass().getName(), e);
                } finally {
                    count.countDown();
                }
            }).start();
        }

        // 等所有线程执行结束后，或者超时后，再跳出此方法
        ProcessingHelper.runnerAwait(timeout, timeUnit, count);
    }
}
