package com.magic.processing.disruptor;

import com.lmax.disruptor.RingBuffer;
import com.magic.processing.commons.TaskData;
import com.magic.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 生产者线程
 */
public abstract class DisruptorProducer implements Runnable {

    private Logger logger = LoggerFactory.getLogger(DisruptorProducer.class);

    /**
     * ID
     */
    private String id;

    /**
     * 是否要停止
     */
    private boolean shutdown;

    /**
     * 是否已停止
     */
    private boolean shutdowned;

    /**
     * 是否持续生产
     * 如果设置为false，那么producer方法只会执行一次，完成后本线程将直接结束
     * 如果设置为true，那么producer方法会一直循环执行
     */
    private boolean loop;


    private RingBuffer<DisruptorEvent> ringBuffer;

    public DisruptorProducer() {
        this.shutdown = false;
        this.loop = getLoop();
        this.id = getId();
        if (StringUtils.isEmpty(this.id)) {
            throw new NullPointerException("producer id cannot empty");
        }
    }


    /**
     * 给消费者投喂任务
     *
     * @param t
     */
    public void publish(Object t) {
        if (ringBuffer == null) {
            throw new NullPointerException("No ring buffers configured");
        }

        long sequence = ringBuffer.next();
        try {
            DisruptorEvent event = ringBuffer.get(sequence);
            event.setTaskData(new TaskData(id, t));
        } finally {
            ringBuffer.publish(sequence);
        }
    }

    /**
     * 生产数据
     */
    @Override
    public void run() {
        while (true) {
            if (shutdown) {
                this.shutdowned = true;
                break;
            }
            try {
                // 生产数据，并投喂给消费者
                producer();

                // 如果loop设置为false，代表只执行一次，所以直接跳出run方法
                if (!loop) {
                    return;
                }

            } catch (Exception e) {
                logger.error("DataProducer run error, id:{}", id, e);
            }
        }
    }

    /**
     * 停止数据生产
     */
    public void shutDownNow() {
        this.shutdown = true;
    }

    /**
     * 获取ID
     *
     * @return
     */
    public String getId() {
        return this.getClass().getName();
    }

    /**
     * 生产数据
     */
    public abstract void producer();

    /**
     * 是否持续生产
     * 如果设置为false，那么producer方法只会执行一次，完成后本线程将直接结束
     * 如果设置为true，那么producer方法会一直循环执行
     */
    public boolean getLoop() {
        return true;
    }

    public boolean isShutdowned() {
        return shutdowned;
    }

    public void setRingBuffer(final RingBuffer<DisruptorEvent> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }
}
