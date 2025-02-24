package com.magic.processing.pac;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
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
import java.util.function.Consumer;

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

    private Consumer<Disruptor<MagicEvent>> consumerLambda;
    private Disruptor<MagicEvent> disruptor;
    private RingBuffer<MagicEvent> ringBuffer;

    public MagicProducerAndConsumerManager(final long startTime) {
        this.startTime = startTime;
    }

    /**
     * 添加一个消费者
     *
     * @param consumerLambda
     * @return
     */
    public MagicProducerAndConsumerManager addConsumer(Consumer<Disruptor<MagicEvent>> consumerLambda) {
        this.consumerLambda = consumerLambda;
        return this;
    }

    /**
     * 添加一个生产者
     *
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
     *
     * @throws Exception
     */
    public MagicProducerAndConsumerManager start() throws Exception {
        int processors = Runtime.getRuntime().availableProcessors();
        int activeThreads = Thread.activeCount();
        int coefficient = 2; // 系数
        int maxThreads = processors * coefficient + activeThreads;

        logger.info("当前系统可用处理器数量:{}", processors);
        logger.info("当前系统最大线程数:{}", maxThreads);

        // 初始化消费者线程池
        consumersPoolExecutor = new ThreadPoolExecutor(maxThreads,
                maxThreads,
                1,
                TimeUnit.MINUTES,
                new LinkedBlockingQueue<>());

        // final WaitStrategy waitStrategy = new BlockingWaitStrategy();
        final YieldingWaitStrategy waitStrategy = new YieldingWaitStrategy();
        // Create Disruptor
        disruptor = new Disruptor<>(
                MagicEvent::new,
                1024, // Ring buffer size
                consumersPoolExecutor,
                ProducerType.MULTI,
                waitStrategy
        );

        // 消费 Disruptor
        consumerLambda.accept(disruptor);

        // Start disruptor
        disruptor.start();

        // 创建RingBuffer
        ringBuffer = disruptor.getRingBuffer();


        // 初始化生产者线程池
        producersPoolExecutor = new ThreadPoolExecutor(producers.size(),
                producers.size(),
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

        // 开启生产者线程
        final Map<String, Object> idMap = new HashMap<>();
        for (MagicProducer producer : producers) {

            checkId(idMap, producer.getId());

            producer.setRingBuffer(ringBuffer);
            producersPoolExecutor.submit(producer);
        }


        return this;
    }

    /**
     * 校验ID，避免出现相同的ID
     *
     * @param idMap
     * @param id
     * @throws Exception
     */
    private void checkId(Map<String, Object> idMap, String id) throws Exception {
        // 这里做了一个校验，避免出现相同的ID
        if (idMap.get(id) != null) {
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
                if (producersPoolExecutor.awaitTermination(1, TimeUnit.SECONDS)) break;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            logger.info("等待 producer 线程池关闭...");
        }
        logger.info("producer 线程池已经关闭！");

        disruptor.shutdown();
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
     * 停止指定的监控者
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
                    if (monitorPoolExecutor.awaitTermination(1, TimeUnit.SECONDS)) break;
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
     */
    public void shutdownAllProducer() {
        for (MagicProducer producer : producers) {
            producer.shutDownNow();
        }

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
                if (producersPoolExecutor.awaitTermination(1, TimeUnit.SECONDS)) break;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            logger.info("等待 producer 线程池关闭...");
        }
        logger.info("producer 线程池已经关闭！");
    }

    /**
     * 停止指定的消费者
     */
    public void shutdownAllConsumer() {
        consumersPoolExecutor.shutdown();
        while (true) {
            try {
                if (consumersPoolExecutor.awaitTermination(1, TimeUnit.SECONDS)) break;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            logger.info("等待 consumer 线程池关闭...");
        }
        logger.info("consumer 线程池已经关闭！");
    }
}
