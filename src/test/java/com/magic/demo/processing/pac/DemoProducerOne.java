package com.magic.demo.processing.pac;


import com.magic.processing.pac.MagicProducer;

public class DemoProducerOne extends MagicProducer {

    @Override
    public void producer() {
        for (int i = 0; i < 2; i++) {
            try {
                this.publish("000" + i);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.shutDownNow();
    }
}
