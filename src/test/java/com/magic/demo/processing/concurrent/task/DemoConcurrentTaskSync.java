package com.magic.demo.processing.concurrent.task;


import com.magic.processing.MagicDataProcessing;

import java.util.concurrent.TimeUnit;

public class DemoConcurrentTaskSync {

    public static void main(String[] args) {
        MagicDataProcessing.getConcurrentTaskSync()
                .setTimeout(1000) // 超时时间
                .setTimeUnit(TimeUnit.MILLISECONDS) // 超时时间的单位
                .add(() -> { // 添加一个任务

                    System.out.println("1");

                })
                .add(() -> { // 添加一个任务

                    System.out.println("2");
                })
                .start();
    }
}
