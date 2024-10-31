package com.github.yuyenews.data.processing.demo.pac;

import com.magician.tools.processing.pac.MagicianConsumer;

public class DemoConsumerThree extends MagicianConsumer {

    @Override
    public void doRunner(Object data) {
        try {
            Thread.sleep(10000);

            System.out.println("dataType3: " + data + "_" + data.getClass().getSimpleName());
            System.out.println("taskCount3: " + this.getTaskCount());
            System.out.println("data3:" + data);
            System.out.println("3------------------------------------------------");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
