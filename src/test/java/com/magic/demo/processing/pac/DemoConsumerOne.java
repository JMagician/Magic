package com.magic.demo.processing.pac;


import com.magic.processing.pac.MagicConsumer;

public class DemoConsumerOne extends MagicConsumer {

    @Override
    public long getExecFrequencyLimit() {
        return 500;
    }

    @Override
    public void doRunner(String id, Object data) {
        try {
            Thread.sleep(1000);

            System.out.printf("1: id: %s, data: %s\n------------------------------------------------\n", id, data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
