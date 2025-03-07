package com.magic.demo.processing.pac;

import com.magic.processing.MagicDataProcessing;
import com.magic.processing.disruptor.DisruptorConsumer;
import com.magic.processing.disruptor.DisruptorProducer;
import com.magic.processing.pac.MagicMonitor;

import java.util.concurrent.atomic.LongAdder;

public class OneToManyDemoDisruptor {

    public static volatile LongAdder count = new LongAdder();

    public static void main(String[] args) throws Exception {

        MagicDataProcessing
                .getDisruptorProducerAndConsumerManager()
                .addProducer(magicProducer)
                .addConsumer(disruptor -> {
                    // 并发写法（不重复消费）
                    disruptor.handleEventsWithWorkerPool(
                            createConsumer(),
                            createConsumer()
                    );

                    // 串联写法
                    // disruptor.handleEventsWithWorkerPool(createConsumer())
                    //         .then(createConsumer());

                    // 链内串行，多链并行（会重复消费）
                    // disruptor.handleEventsWithWorkerPool(createConsumer()).then(createConsumer());
                    // disruptor.handleEventsWithWorkerPool(createConsumer()).then(createConsumer());

                    // 菱形（C、D都执行完才到E）（会重复消费）
                    // final DisruptorConsumer c = createConsumer();
                    // final DisruptorConsumer d = createConsumer();
                    // final DisruptorConsumer e = createConsumer();
                    // disruptor.handleEventsWithWorkerPool(createConsumer()).then(c);
                    // disruptor.handleEventsWithWorkerPool(createConsumer()).then(d);
                    // disruptor.after(c, d).then(e);

                    // 分组不重复消费
                    // 组内竞争，组外串行：每个消息在每个分组中只有一个消费者能消费成功
                    // disruptor
                    //         .handleEventsWithWorkerPool(createConsumer(), createConsumer(), createConsumer())
                    //         .then(createConsumer(), createConsumer(), createConsumer())
                    //         .then(createConsumer());

                })
                .addMonitor(magicMonitor)
                .start()
                .listenShutdown(() -> {
                    System.out.println("shutdown other middle soft");
                });

    }


    static final DisruptorProducer magicProducer = new DisruptorProducer() {

        @Override
        public void producer() {
            count.increment();
            this.publish(count.longValue());

            if (count.longValue() >= 10000000) {
                this.shutDownNow();
            }
        }
    };

    public static DisruptorConsumer createConsumer() {
        return new DisruptorConsumer() {

            @Override
            public long getExecFrequencyLimit() {
                return -10;
            }

            @Override
            public void doRunner(String id, final Object data) {
                // System.out.println(data);
            }
        };
    }


    static final MagicMonitor magicMonitor = new MagicMonitor() {
        @Override
        public void monitor() {
            final long value = count.longValue();
            System.out.printf("经过%s秒，共处理%s条。速率：%s条/分钟%n", this.getTime(), value, ((value * 60) / this.getTime()));
        }

        @Override
        public void monitorShutdown() {
            final long value = count.longValue();
            System.out.printf("总耗时%s秒，共处理%s条。最后速率：%s条/分钟%n", this.getTime(), value, ((value * 60) / this.getTime()));
        }
    };

}
