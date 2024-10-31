package com.github.yuyenews.data.processing.demo.concurrent.collcetion;


import com.magician.tools.processing.MagicianDataProcessing;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DemoAsyncProcessingCollection {

    public static void main(String[] args) {

        // 假如有一个List需要并发处理里面的元素
        List<String> dataList = new ArrayList<>();
        dataList.add("a");

        // 只需要将他传入syncRunner方法即可，每个参数的具体含义可以参考文档
        MagicianDataProcessing.getConcurrentCollectionAsync()
                .asyncRunner(dataList, data -> {

                    // 这里可以拿到List里的元素，进行处理
                    System.out.println(data);

                }, 10, 1, TimeUnit.MINUTES)
                .start();


        // 也可以用syncGroupRunner方法，每个参数的具体含义可以参考文档
        MagicianDataProcessing.getConcurrentCollectionAsync()
                .asyncGroupRunner(dataList, data -> {

                    // 这里可以拿到List里的元素，进行处理
                    System.out.println(data);

                }, 10, 1, TimeUnit.MINUTES)
                .start();

        System.out.println(1);
    }
}
