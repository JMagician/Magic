package com.magic.processing.pac;

import com.magic.processing.commons.TaskData;
import com.magic.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 消费者线程
 */
public abstract class MagicConsumer implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(MagicConsumer.class);

    /**
     * perantIndex
     */
    private int perantIndex;
    /**
     * index
     */
    private int index;

    /**
     * 任务队列
     */
    private LinkedBlockingQueue<TaskData> blockingQueue;

    /**
     * ID
     */
    private String id;

    /**
     * 频率限制，run方法里有详细说明
     */
    private long execFrequencyLimit;

    /**
     * 是否要停止
     */
    private boolean shutdown;

    /**
     * 是否要停止
     */
    private boolean shutdowned;

    /**
     * 队列最大值
     */
    private int queueCapacity;

    /**
     * 每个生产者投喂进来的任务数量（剩余量）
     */
    private Map<String, AtomicLong> producerTaskCount;

    public MagicConsumer(){
        this.queueCapacity = getQueueCapacity();
        this.blockingQueue = new LinkedBlockingQueue<>(queueCapacity);
        this.producerTaskCount = new ConcurrentHashMap<>();
        this.shutdown = false;
        this.execFrequencyLimit = getExecFrequencyLimit();
        if (this.execFrequencyLimit < 0) {
            this.execFrequencyLimit = 0;
        }

        this.id = initId();
        if (StringUtils.isEmpty(this.id)) {
            throw new NullPointerException("consumer id cannot empty");
        }
    }

    /**
     * 初始化生产者投喂的任务剩余量
     * @param producerId
     */
    public void initProducerTaskCount(String producerId){
        this.producerTaskCount.put(producerId, new AtomicLong(0));
    }

    /**
     * 获取总任务量剩余数
     * @return
     */
    public int getTaskCount(){
        return blockingQueue.size();
    }

    /**
     * 添加任务
     * @param taskData
     */
    public void addTask(TaskData taskData) throws InterruptedException {
        this.blockingQueue.put(taskData);
        this.producerTaskCount.get(taskData.getProducerId()).incrementAndGet();
    }

    /**
     * 检测producerId对应的生产者，是否可以对其投喂任务
     * @param producerId
     * @return
     */
    public boolean isPending(String producerId){
        return this.blockingQueue.size() == 0 || this.producerTaskCount.get(producerId).get() <= 0;
    }

    /**
     * 获取producerId对应的生产者，投喂的任务量剩余数
     * @param producerId
     * @return
     */
    public Long getProducerTaskCount(String producerId){
        return this.producerTaskCount.get(producerId).get();
    }

    /**
     * 消费任务队列
     */
    @Override
    public void run() {
        while (true) {

            if (shutdown && getTaskCount() == 0) {
                this.shutdowned = true;
                break;
            }
            try {
                // 发送心跳通知
                pulse(this.id);

                // take一个任务
                TaskData task = blockingQueue.poll(100, TimeUnit.MILLISECONDS);
                if (task == null) {
                    continue;
                }

                /* *********** 开始执行任务 ********** */

                // 记录任务执行开始时间
                long startTime = System.currentTimeMillis();

                // 执行任务
                execTask(task);

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
    }

    /**
     * 执行任务
     * @param taskData
     */
    private void execTask(TaskData taskData){
        // 本任务对应的生产者，投喂到这里面的任务剩余量，减少一个
        AtomicLong taskCount = producerTaskCount.get(taskData.getProducerId());
        if(taskCount.get() > 0){
            taskCount.decrementAndGet();
            logger.info("MagicianConsumer take one, producerId:{}", taskData.getProducerId());
        }

        // 执行任务
        doRunner(taskData.getData());
    }

    /**
     * 停止本消费者
     */
    public void shutDownNow(){
        this.shutdown = true;
    }

    public String initId() {
        return this.getClass().getName();
    }

    /**
     * 获取ID
     *
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     * 心跳
     * @param id
     */
    public void pulse(String id){};

    /**
     * 获取执行频率
     * @return
     */
    public long getExecFrequencyLimit(){
        return 10;
    }

    /**
     * 执行任务
     * @param data
     */
    public abstract void doRunner(Object data);


    /**
     * 队列最大值
     */
    public int getQueueCapacity() {
        return 100;
    }

    public void setIndex(final int perantIndex, final int index) {
        this.perantIndex = perantIndex;
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public int getPerantIndex() {
        return perantIndex;
    }

    public boolean isShutdowned() {
        return shutdowned;
    }
}
