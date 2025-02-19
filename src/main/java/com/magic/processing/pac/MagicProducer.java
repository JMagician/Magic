package com.magic.processing.pac;

import com.magic.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 生产者线程
 */
public abstract class MagicProducer implements Runnable {

    private Logger logger = LoggerFactory.getLogger(MagicProducer.class);

    /**
     * ID
     */
    private String id;

    /**
     * 它所对应的消费者
     */
    private List<LinkedBlockingQueue<MagicConsumer>> consumerGroups;
    private final List<LinkedBlockingQueue<MagicConsumer>> freeConsumerGroups = new ArrayList<>();


    /**
     * 是否要停止
     */
    private boolean shutdown;

    /**
     * 是否已停止
     */
    private boolean shutdowned;


    /**
     * 是否等所有消费者都空了以后，才进行下一轮
     * 这个配置是跟loop配合使用的，如果loop为fale，那么这个配置将没有意义
     */
    private boolean allFree;

    /**
     * 是否持续生产
     * 如果设置为false，那么producer方法只会执行一次，完成后本线程将直接结束
     * 如果设置为true，那么producer方法会一直循环执行
     */
    private boolean loop;

    public MagicProducer(){
        this.shutdown = false;
        this.loop = getLoop();
        this.allFree = getAllFree();
        this.id = getId();
        if(StringUtils.isEmpty(this.id)){
            throw new NullPointerException("producer id cannot empty");
        }
    }

    /**
     * 添加消费者
     *
     * @param consumerGroups
     */
    public void addConsumerGroups(List<LinkedBlockingQueue<MagicConsumer>> consumerGroups) {
        this.consumerGroups = consumerGroups;

        for (final LinkedBlockingQueue<MagicConsumer> consumerGroup : consumerGroups) {
            freeConsumerGroups.add(new LinkedBlockingQueue<>());

            for (final MagicConsumer consumer : consumerGroup) {
                consumer.initProducerTaskCount(id);
            }
        }
    }

    /**
     * 给消费者投喂任务
     * @param t
     */
    public void publish(Object t){
        if (consumerGroups == null || consumerGroups.isEmpty()) {
            throw new NullPointerException("");
        }

        for (int i = 0; i < consumerGroups.size(); i++) {
            final LinkedBlockingQueue<MagicConsumer> consumerGroup = consumerGroups.get(i);

            try {
                if (allFree) {
                    final LinkedBlockingQueue<MagicConsumer> freeConsumerGroup = freeConsumerGroups.get(i);
                    if (consumerGroup.isEmpty()) {
                        while (!freeConsumerGroup.isEmpty()) {
                            consumerGroup.put(freeConsumerGroup.take());
                        }
                    }

                    if (!consumerGroup.isEmpty()) {
                        MagicConsumer magicConsumer = consumerGroup.take();
                        freeConsumerGroup.put(magicConsumer);
                        magicConsumer.addTask(new TaskData(id, t));
                    }
                } else {
                    MagicConsumer magicConsumer = consumerGroup.take();
                    magicConsumer.addTask(new TaskData(id, t));
                    consumerGroup.put(magicConsumer);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 生产数据
     */
    @Override
    public void run(){
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
    public void shutDownNow(){
        this.shutdown = true;
    }

    /**
     * 获取ID
     * @return
     */
    public String getId(){
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
    public boolean getLoop(){
        return true;
    }

    /**
     * 是否等所有消费者都空了以后，才进行下一轮
     * 这个配置是跟loop配合使用的，如果loop为fale，那么这个配置将没有意义
     * @return
     */
    public boolean getAllFree(){
        return false;
    }

    public boolean isShutdowned() {
        return shutdowned;
    }
}
