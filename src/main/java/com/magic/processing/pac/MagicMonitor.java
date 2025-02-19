package com.magic.processing.pac;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 监控线程
 */
public abstract class MagicMonitor implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(MagicMonitor.class);


    /**
     * 是否要停止
     */
    private boolean shutdown;

    /**
     * 是否要停止
     */
    private boolean shutdowned;

    // 记录任务执行开始时间
    private long startTime;

    public MagicMonitor() {
        this.speed = getSpeed();
        // 记录任务执行开始时间
        startTime = System.currentTimeMillis();
    }

    /**
     * 速率
     */
    private long speed;

    /**
     * 生产数据
     */
    @Override
    public void run() {
        try {
            Thread.sleep(speed);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        while (true) {
            if (shutdown) {
                this.shutdowned = true;
                break;
            }
            try {
                // 生产数据，并投喂给消费者
                monitor();

                Thread.sleep(speed);
            } catch (Exception e) {
                logger.error("Data monitor run error", e);
            }
        }
    }


    /**
     * 监控数据
     */
    public abstract void monitor();
    public abstract void monitorShutdown();

    public long getSpeed() {
        return 1* 1000;
    }
    public long getTime() {
        return (System.currentTimeMillis() - startTime) /1000;
    }

    public boolean isShutdowned() {
        return shutdowned;
    }


    /**
     * 停止数据生产
     */
    public void shutDownNow() {
        this.shutdown = true;
    }
}
