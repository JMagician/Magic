package com.magic.demo.processing.pac;


import com.magic.processing.pac.MagicConsumer;

public class DemoConsumerOne extends MagicConsumer {

    @Override
    public long getExecFrequencyLimit() {
        return 500;
    }

    @Override
    public void doRunner(Object data) {
        try {
            Thread.sleep(1000);

            System.out.println("dataType: " + data + "_" + data.getClass().getSimpleName());
            System.out.println("taskCount: " + this.getTaskCount());
            System.out.println("data:" + data);
            System.out.println("------------------------------------------------");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
