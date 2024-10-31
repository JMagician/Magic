package com.magician.tools.processing.pac;

import com.magician.tools.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 生产者线程
 */
public abstract class MagicianProducer implements Runnable {

    private Logger logger = LoggerFactory.getLogger(MagicianProducer.class);

    /**
     * ID
     */
    private String id;

    /**
     * 它所对应的消费者
     */
    private List<MagicianConsumer> consumers;

    /**
     * 他所对应的空闲消费者
     */
    private List<MagicianConsumer> freeConsumers;

    /**
     * 是否要停止
     */
    private boolean shutdown;

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

    public MagicianProducer(){
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
     * @param consumers
     */
    public void addConsumer(List<MagicianConsumer> consumers){
        this.consumers = consumers;
        for(MagicianConsumer consumer : this.consumers){
            consumer.initProducerTaskCount(id);
        }
    }

    /**
     * 给消费者投喂任务
     * @param t
     */
    public void publish(Object t){
        if(freeConsumers == null || freeConsumers.size() == 0){
            throw new NullPointerException("");
        }
        for(MagicianConsumer consumer : freeConsumers){
            consumer.addTask(new TaskData(id, t));
        }
    }

    /**
     * 生产数据
     */
    @Override
    public void run(){
        freeConsumers = consumers;

        while (shutdown == false){
            try {
                // 生产数据，并投喂给消费者
                producer();

                // 如果loop设置为false，代表只执行一次，所以直接跳出run方法
                if(loop == false){
                    return;
                }

                // 一轮投喂结束后，检测有没有空闲消费者，如果没有就阻塞在这，直到有空闲消费者出现
                await();
            } catch (Exception e){
                logger.error("DataProducer run error, id:{}", id, e);
            }
        }
    }

    /**
     * 检测有没有空闲消费者，如果没有就阻塞在这，直到有空闲消费者出现
     */
    private void await(){
        while (true) {
            try {
                // 检测有没有空闲消费者
                freeConsumers = new ArrayList<>();
                for (MagicianConsumer consumer : consumers) {
                    if (consumer.isPending(id)) {
                        freeConsumers.add(consumer);
                    } else if (allFree) {
                        // 如果allFree设置为true，那么只要发现了不空闲的消费者就会继续等待
                        // 直到所有消费者都空闲了才进行下一轮
                        freeConsumers = new ArrayList<>();
                        break;
                    }
                }

                // 如果这里能拿到空闲的消费者，就直接进行下一轮
                // 而且生产者发布的数据只会推送到这几个消费者里面
                // 这是为了照顾消费者的消费能力，如果不管不顾随便投喂，会导致队列积压，造成内存溢出
                if (freeConsumers.size() > 0) {
                    break;
                }

                // 没有就阻塞100毫秒，然后再检测一次
                Thread.sleep(100);
                logger.info("DataProducer execProducer awaiting, id:{}......", id);
            } catch (Exception e){
                logger.error("DataProducer execProducer await error, id:{}", id, e);
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
}
