package com.magic.processing.pac;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.LifecycleAware;
import com.lmax.disruptor.WorkHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 消费者线程
 */
public abstract class MagicConsumer implements WorkHandler<MagicEvent>, EventHandler<MagicEvent>, LifecycleAware {

    private static Logger logger = LoggerFactory.getLogger(MagicConsumer.class);

    /**
     * 频率限制，run方法里有详细说明
     */
    private long execFrequencyLimit;

    public MagicConsumer() {
        this.execFrequencyLimit = getExecFrequencyLimit();
        if (this.execFrequencyLimit < 0) {
            this.execFrequencyLimit = 0;
        }
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onShutdown() {
    }

    @Override
    public void onEvent(MagicEvent t, long sequence, boolean b) throws Exception {
        onEvent(t);
    }

    /**
     * 消费任务队列
     */
    @Override
    public void onEvent(MagicEvent context) throws Exception {

        try {
            // take一个任务
            final TaskData task = context.getTaskData();
            if (task == null) {
                return;
            }

            /* *********** 开始执行任务 ********** */

            // 记录任务执行开始时间
            long startTime = System.currentTimeMillis();

            // 执行任务
            doRunner(task.getProducerId(), task.getData());

            /*
             * 如果任务执行的耗时小于execFrequencyLimit，则等待execFrequencyLimit毫秒后再消费下一个任务
             *
             * 首先这是一个生产者和消费者多对多的模型结构，我们以一个生产者对多个消费者来举例
             * 生产者生产的数据只有一份，但是他会投喂给多个消费者
             * 而我们之所以要配置多个消费者，是因为需要他们执行不同的业务逻辑
             * 多个消费者执行的业务逻辑不同，也就意味着他们需要的数据大概率会不同
             *
             * 比如消费者A需要处理男性的数据，消费者B需要处理女性的数据
             * 如果生产者刚好连续投喂了几批男性的数据，那么这会导致消费者B筛选不到女性数据，那么他就不会处理业务逻辑了
             * 这么一来，消费者B的执行速度会非常快，而他的快意味着execTask(task)会快速结束
             * 这个速度如果过快，会导致本while循环过快，从而引起CPU占用率过大，所以必须加以限制
             *
             * 千万不要小看这个问题，本人曾经在实战中亲测过，做不做这个限制，CPU的占有率会达到10倍的差距
             * 当然了，这跟消费者的业务逻辑还是有一定关系的，具体情况具体看待
             *
             */
            if (execFrequencyLimit > 0 && (System.currentTimeMillis() - startTime) < execFrequencyLimit) {
                Thread.sleep(execFrequencyLimit);
            }
        } catch (Exception e) {
            logger.error("DataConsumer run error", e);
        }
    }

    /**
     * 获取执行频率
     *
     * @return
     */
    public long getExecFrequencyLimit() {
        return 10;
    }

    /**
     * 执行任务
     *
     * @param id
     * @param data
     */
    public abstract void doRunner(String id, Object data);

}
