package com.github.yuyenews.concurrent.commons.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ProcessingHelper {

    private static Logger logger = LoggerFactory.getLogger(ProcessingHelper.class);

    /**
     * 等待线程执行结束
     *
     * @param timeout      每一组的超时时间，单位由unit参数设置
     * @param unit         超时时间单位
     * @param count
     * @param poolExecutor
     */
    public static void runnerAwait(long timeout, TimeUnit unit, CountDownLatch count, ThreadPoolExecutor poolExecutor) {
        try {
            if (timeout <= 0) {
                // 如果没有设置超时时间，则一直等待，直到线程全部完成为止
                count.await();
            } else {
                // 如果设置了等待时间，则等待相应的时间
                count.await(timeout, unit);
            }
        } catch (Exception e) {
            logger.error("Concurrent await error", e);
        }

        // 不管是超时未完成，还是已经执行完了，都要释放线程池
        poolExecutor.shutdownNow();
    }
}
