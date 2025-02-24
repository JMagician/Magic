package com.magic.demo.processing.pac;


import com.magic.processing.pac.MagicProducer;

import java.util.ArrayList;

public class DemoProducerTwo extends MagicProducer {

    @Override
    public void producer() {
        for (int i = 0; i < 2; i++) {
            try {
                this.publish(new ArrayList<>());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.shutDownNow();
    }
}
