package com.github.yuyenews.concurrent.job;

import com.github.yuyenews.concurrent.commons.enums.JobEnum;
import com.github.yuyenews.concurrent.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 任务管理器
 */
public class MagicianJobManager {

    /**
     * 消费者集合
     */
    private List<MagicianConsumer> consumers = new ArrayList<>();

    /**
     * 生产者集合
     */
    private List<MagicianProducer> producers = new ArrayList<>();

    /**
     * 生产者线程池
     */
    private ThreadPoolExecutor producersPoolExecutor;

    /**
     * 消费者线程池
     */
    private ThreadPoolExecutor consumersPoolExecutor;

    /**
     * 添加一个消费者
     * @param consumer
     * @return
     */
    public MagicianJobManager addConsumer(MagicianConsumer consumer) {
        consumers.add(consumer);
        return this;
    }

    /**
     * 添加一个生产者
     * @param producer
     * @return
     */
    public MagicianJobManager addProducer(MagicianProducer producer) {
        producers.add(producer);
        return this;
    }

    /**
     * 执行
     * @throws Exception
     */
    public void start() throws Exception {
        // 初始化生产者线程池
        producersPoolExecutor = new ThreadPoolExecutor(consumers.size(),
                consumers.size(),
                1,
                TimeUnit.MINUTES,
                new LinkedBlockingQueue<>());

        // 初始化消费者线程池
        consumersPoolExecutor = new ThreadPoolExecutor(consumers.size(),
                consumers.size(),
                1,
                TimeUnit.MINUTES,
                new LinkedBlockingQueue<>());

        // 开启消费者线程
        Map<String, Object> idMap = new HashMap<>();
        for (MagicianConsumer consumer : consumers) {
            checkId(idMap, consumer.getId());

            consumersPoolExecutor.submit(consumer);
        }

        // 开启生产者线程
        idMap = new HashMap<>();
        for (MagicianProducer producer : producers) {

            checkId(idMap, producer.getId());

            producer.addConsumer(consumers);
            producersPoolExecutor.submit(producer);
        }
    }

    /**
     * 校验ID，避免出现相同的ID
     * @param idMap
     * @param id
     * @throws Exception
     */
    private void checkId(Map<String, Object> idMap, String id) throws Exception {
        // 这里做了一个校验，避免出现相同的ID
        if(idMap.get(id) != null){
            throw new Exception("duplicate producer id exists");
        }
        idMap.put(id, true);
    }

    /**
     * 立刻关闭生产者或消费者线程池
     * @param jobEnum
     */
    public void shutdown(JobEnum jobEnum) {
        if (JobEnum.CONSUMER.equals(jobEnum)) {
            shutdownAllConsumer();
        } else if (JobEnum.PRODUCER.equals(jobEnum)) {
            shutdownAllProducer();
        } else if (JobEnum.ALL.equals(jobEnum)) {
            shutdownAllProducer();
            shutdownAllConsumer();
        }
    }

    /**
     * 停止所有生产者
     */
    public void shutdownAllProducer(){
        shutdownProducer(null);
    }

    /**
     * 停止所有消费者
     */
    public void shutdownAllConsumer(){
        shutdownConsumer(null);
    }

    /**
     * 停止指定的生产者
     * @param id
     */
    public void shutdownProducer(String id){
        for(MagicianProducer producer : producers){
            if(StringUtils.isEmpty(id) || id.equals(producer.getId())){
                producer.shutDownNow();
            }
        }

        producersPoolExecutor.shutdownNow();
    }

    /**
     * 停止指定的消费者
     * @param id
     */
    public void shutdownConsumer(String id){
        for(MagicianConsumer consumer : consumers){
            if(StringUtils.isEmpty(id) || id.equals(consumer.getId())){
                consumer.shutDownNow();
            }
        }

        consumersPoolExecutor.shutdownNow();
    }
}
