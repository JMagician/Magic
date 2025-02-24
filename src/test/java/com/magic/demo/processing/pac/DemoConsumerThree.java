package com.magic.demo.processing.pac;

import com.magic.processing.pac.MagicConsumer;

public class DemoConsumerThree extends MagicConsumer {

    @Override
    public void doRunner(String id, Object data) {
        try {
            Thread.sleep(1000);

            System.out.printf("3: id: %s, data: %s\n------------------------------------------------\n", id, data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
