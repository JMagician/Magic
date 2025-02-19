package com.magic.processing.pac;

import com.magic.util.StringUtils;
import com.magic.processing.commons.enums.ProducerAndConsumerEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class MagicProducerAndConsumerManager {

    private static Logger logger = LoggerFactory.getLogger(MagicProducerAndConsumerManager.class);

    /**
     * 启动开始四件
     */
    private final long startTime;
    /**
     * 消费者下标索引
     */
    private int consumerIndex = 0;
    /**
     * 消费者数量
     */
    private int consumerSize = 0;
    /**
     * 消费者集合
     */
    private List<LinkedBlockingQueue<MagicConsumer>> consumerGroups = new ArrayList<>();
    private List<LinkedBlockingQueue<MagicConsumer>> backConsumerGroups = new ArrayList<>();

    /**
     * 生产者集合
     */
    private List<MagicProducer> producers = new ArrayList<>();

    /**
     * 监控
     */
    private MagicMonitor monitor;

    /**
     * 生产者线程池
     */
    private ThreadPoolExecutor producersPoolExecutor;

    /**
     * 消费者线程池
     */
    private ThreadPoolExecutor consumersPoolExecutor;

    /**
     * 监控线程池
     */
    private ThreadPoolExecutor monitorPoolExecutor;

    public MagicProducerAndConsumerManager(final long startTime) {
        this.startTime = startTime;
    }

    /**
     * 添加一个消费者
     * @param inputConsumers
     * @return
     */
    public MagicProducerAndConsumerManager addConsumer(MagicConsumer... inputConsumers) {
        consumerSize = consumerSize + inputConsumers.length;
        final LinkedBlockingQueue<MagicConsumer> consumers = new LinkedBlockingQueue<>();
        final LinkedBlockingQueue<MagicConsumer> backConsumers = new LinkedBlockingQueue<>();
        for (int i = 0; i < inputConsumers.length; i++) {
            final MagicConsumer inputConsumer = inputConsumers[i];
            inputConsumer.setIndex(consumerIndex, i);
            consumers.add(inputConsumer);
            backConsumers.add(inputConsumer);
        }
        consumerGroups.add(consumerIndex, consumers);
        backConsumerGroups.add(consumerIndex, backConsumers);
        consumerIndex++;
        return this;
    }

    /**
     * 添加一个生产者
     * @param producer
     * @return
     */
    public MagicProducerAndConsumerManager addProducer(MagicProducer producer) {
        producers.add(producer);
        return this;
    }

    /**
     * 添加一个监控
     *
     * @param monitor
     * @return
     */
    public MagicProducerAndConsumerManager addMonitor(MagicMonitor monitor) {
        this.monitor = monitor;
        return this;
    }


    /**
     * 执行
     * @throws Exception
     */
    public MagicProducerAndConsumerManager start() throws Exception {
        // 初始化生产者线程池
        producersPoolExecutor = new ThreadPoolExecutor(consumerSize,
                consumerSize,
                1,
                TimeUnit.MINUTES,
                new LinkedBlockingQueue<>());

        // 初始化消费者线程池
        consumersPoolExecutor = new ThreadPoolExecutor(consumerSize,
                consumerSize,
                1,
                TimeUnit.MINUTES,
                new LinkedBlockingQueue<>());

        if (monitor != null) {

            // 初始化消费者线程池
            monitorPoolExecutor = new ThreadPoolExecutor(1,
                    1,
                    1,
                    TimeUnit.MINUTES,
                    new LinkedBlockingQueue<>());

            // 开启监控线程
            monitorPoolExecutor.submit(monitor);
        }

        // 开启消费者线程
        Map<String, Object> idMap = new HashMap<>();
        for (final LinkedBlockingQueue<MagicConsumer> consumers : consumerGroups) {
            for (final MagicConsumer consumer : consumers) {
                checkId(idMap, consumer.getId());

                consumersPoolExecutor.submit(consumer);

            }
        }

        // 开启生产者线程
        idMap = new HashMap<>();
        for (MagicProducer producer : producers) {

            checkId(idMap, producer.getId());

            producer.addConsumerGroups(consumerGroups);
            producersPoolExecutor.submit(producer);
        }


        return this;
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


    public void listenShutdown(Runnable runnable) {
        while (true) {
            boolean isAllStop = true;
            for (MagicProducer producer : producers) {
                if (!producer.isShutdowned()) {
                    isAllStop = false;
                    break;
                }
            }
            if (isAllStop) {
                break;
            }
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        producersPoolExecutor.shutdown();
        while (true) {
            try {
                if (producersPoolExecutor.awaitTermination(1, TimeUnit.MINUTES)) break;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            logger.info("等待 producer 线程池关闭...");
        }
        logger.info("producer 线程池已经关闭！");

        shutdown(ProducerAndConsumerEnum.CONSUMER);
        shutdown(ProducerAndConsumerEnum.MONITOR);
        runnable.run();


        logger.info("总计耗时：{}ms!", (System.currentTimeMillis() - startTime));
    }

    /**
     * 立刻关闭生产者或消费者线程池
     *
     * @param producerAndConsumerEnum
     */
    private void shutdown(ProducerAndConsumerEnum producerAndConsumerEnum) {
        if (ProducerAndConsumerEnum.CONSUMER.equals(producerAndConsumerEnum)) {
            shutdownAllConsumer();
        } else if (ProducerAndConsumerEnum.PRODUCER.equals(producerAndConsumerEnum)) {
            shutdownAllProducer();
        } else if (ProducerAndConsumerEnum.MONITOR.equals(producerAndConsumerEnum)) {
            shutdownMonitor();
        } else if (ProducerAndConsumerEnum.ALL.equals(producerAndConsumerEnum)) {
            shutdownAllProducer();
            shutdownAllConsumer();
            shutdownMonitor();
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
     * 停止指定的监控者
     *
     */
    public void shutdownMonitor() {
        if (monitor != null) {
            monitor.shutDownNow();

            while (monitor.isShutdowned()) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            monitor.monitorShutdown();

            monitorPoolExecutor.shutdown();
            while (true) {
                try {
                    if (monitorPoolExecutor.awaitTermination(1, TimeUnit.MINUTES)) break;
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                logger.info("等待 monitor 线程池关闭...");
            }
            logger.info("monitor 线程池已经关闭！");
        }

    }

    /**
     * 停止指定的生产者
     *
     * @param id
     */
    public void shutdownProducer(String id) {
        for (MagicProducer producer : producers) {
            if (StringUtils.isEmpty(id) || id.equals(producer.getId())) {
                producer.shutDownNow();
            }
        }

        while (true) {
            boolean isAllStop = true;
            for (MagicProducer producer : producers) {
                if (StringUtils.isEmpty(id) || id.equals(producer.getId())) {
                    if (!producer.isShutdowned()) {
                        isAllStop = false;
                        break;
                    }
                }
            }
            if (isAllStop) {
                break;
            }
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        producersPoolExecutor.shutdown();
        while (true) {
            try {
                if (producersPoolExecutor.awaitTermination(1, TimeUnit.MINUTES)) break;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            logger.info("等待 producer 线程池关闭...");
        }
        logger.info("producer 线程池已经关闭！");
    }

    /**
     * 停止指定的消费者
     *
     * @param id
     */
    public void shutdownConsumer(String id) {
        for (final LinkedBlockingQueue<MagicConsumer> consumers : backConsumerGroups) {
            for (MagicConsumer consumer : consumers) {
                if (StringUtils.isEmpty(id) || id.equals(consumer.getId())) {
                    consumer.shutDownNow();
                }
            }
        }

        while (true) {
            boolean isAllStop = true;
            for (final LinkedBlockingQueue<MagicConsumer> consumers : backConsumerGroups) {
                for (MagicConsumer consumer : consumers) {
                    if (StringUtils.isEmpty(id) || id.equals(consumer.getId())) {
                        if (!consumer.isShutdowned()) {
                            isAllStop = false;
                            break;
                        }
                    }
                }
            }
            if (isAllStop) {
                break;
            }
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        consumersPoolExecutor.shutdown();
        while (true) {
            try {
                if (consumersPoolExecutor.awaitTermination(1, TimeUnit.MINUTES)) break;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            logger.info("等待 consumer 线程池关闭...");
        }
        logger.info("consumer 线程池已经关闭！");
    }
}
