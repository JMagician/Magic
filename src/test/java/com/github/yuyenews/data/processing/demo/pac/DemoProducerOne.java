package com.github.yuyenews.data.processing.demo.pac;


import com.magician.tools.processing.pac.MagicianProducer;

public class DemoProducerOne extends MagicianProducer {

    @Override
    public void producer() {
        for (int i = 0; i < 20; i++) {
            try {
                this.publish("000" + i);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
