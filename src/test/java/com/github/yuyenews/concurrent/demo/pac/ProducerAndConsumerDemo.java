package com.github.yuyenews.concurrent.demo.pac;

import com.github.yuyenews.concurrent.MagicianConcurrent;
import com.github.yuyenews.concurrent.pac.MagicianConsumer;
import com.github.yuyenews.concurrent.pac.MagicianProducer;

import java.util.List;


public class ProducerAndConsumerDemo {

    public static void main(String[] args) throws Exception {

        DemoProducerOne producerOne = new DemoProducerOne();
        DemoProducerTwo producerTwo = new DemoProducerTwo();

        DemoConsumerOne one = new DemoConsumerOne();
        DemoConsumerTwo two = new DemoConsumerTwo();
        DemoConsumerThree three = new DemoConsumerThree();

        MagicianConcurrent.getProducerAndConsumerManager()
                .addProducer(producerOne)
                .addProducer(producerTwo)
                .addConsumer(one)
                .addConsumer(two)
                .addConsumer(three)
                .start();

        new Thread(()->{
            while (true){

                try {
                    System.out.println("taskCount1, 1p:"+one.getProducerTaskCount(producerOne.getId()) +", 2p:"+one.getProducerTaskCount(producerTwo.getId()));
                    System.out.println("taskCount2, 1p:"+two.getProducerTaskCount(producerOne.getId()) +", 2p:"+two.getProducerTaskCount(producerTwo.getId()));
                    System.out.println("taskCount3, 1p:"+three.getProducerTaskCount(producerOne.getId()) +", 2p:"+three.getProducerTaskCount(producerTwo.getId()));
                    System.out.println("----------------------------------------");
                    Thread.sleep(1000);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();

        while (true){
            Thread.sleep(10000000000L);
        }

    }
}
