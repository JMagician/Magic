package com.magic.demo.processing.pac;

import com.magic.processing.MagicDataProcessing;
import com.magic.processing.pac.MagicConsumer;
import com.magic.processing.pac.MagicMonitor;
import com.magic.processing.pac.MagicProducer;

import java.util.UUID;
import java.util.concurrent.atomic.LongAdder;

public class OneToManyDemo {

    public static  volatile LongAdder count = new LongAdder();

    public static void main(String[] args) throws Exception {

        MagicDataProcessing
                .getProducerAndConsumerManager()
                .addProducer(magicProducer)
                .addConsumer(
                        createConsumer(),
                        createConsumer()
                )
                .addMonitor(magicMonitor)
                .start()
                .listenShutdown(() -> {
                    System.out.println("shutdown other middle soft");
                });

    }


    static final MagicProducer magicProducer = new MagicProducer() {

        @Override
        public void producer() {
            count.increment();
            this.publish(count.longValue());

            if (count.longValue() >= 10000000) {
                this.shutDownNow();
            }
        }
    };

    public static MagicConsumer createConsumer() {
        return new MagicConsumer() {

            @Override
            public long getExecFrequencyLimit() {
                return -10;
            }

            @Override
            public String initId() {
                return UUID.randomUUID().toString();
            }

            @Override
            public void doRunner(final Object data) {
                // System.out.println(data);
            }
        };
    }


    static final MagicMonitor magicMonitor = new MagicMonitor() {
        @Override
        public void monitor() {
            final long value = count.longValue();
            System.out.printf("经过%s秒，共处理%s条。速率：%s条/分钟%n", this.getTime(), value, ((value* 60) / this.getTime()));
        }

        @Override
        public void monitorShutdown() {
            final long value = count.longValue();
            System.out.printf("总耗时%s秒，共处理%s条。最后速率：%s条/分钟%n", this.getTime(), value, ((value* 60) / this.getTime()));
        }
    };

}
