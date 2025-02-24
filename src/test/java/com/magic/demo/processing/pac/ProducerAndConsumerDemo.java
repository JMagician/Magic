package com.magic.demo.processing.pac;


import com.magic.processing.MagicDataProcessing;

public class ProducerAndConsumerDemo {

    public static void main(String[] args) throws Exception {

        DemoProducerOne producerOne = new DemoProducerOne();
        DemoProducerTwo producerTwo = new DemoProducerTwo();

        DemoConsumerOne one = new DemoConsumerOne();
        DemoConsumerTwo two = new DemoConsumerTwo();
        DemoConsumerThree three = new DemoConsumerThree();

        MagicDataProcessing.getProducerAndConsumerManager()
                .addProducer(producerOne)
                .addProducer(producerTwo)
                .addConsumer(disruptor ->
                        disruptor.handleEventsWithWorkerPool(one)
                                .then(two)
                                .then(three)
                )
                .start();

        new Thread(() -> {
            while (true) {

                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        while (true) {
            Thread.sleep(10000000000L);
        }

    }
}
