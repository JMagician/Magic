package com.github.yuyenews.data.processing.demo.pac;


import com.magician.tools.processing.pac.MagicianProducer;

import java.util.ArrayList;

public class DemoProducerTwo extends MagicianProducer {

    @Override
    public void producer() {
        for (int i = 0; i < 20; i++) {
            try {
                this.publish(new ArrayList<>());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
