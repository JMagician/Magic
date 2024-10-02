package com.github.yuyenews.concurrent.demo.processing.collcetion;

import com.github.yuyenews.concurrent.MagicianConcurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DemoSyncProcessingCollection {

    public static void main(String[] args) {
        // 假如有一个List需要并发处理里面的元素
        List<String> dataList = new ArrayList<>();

        // 只需要将他传入syncRunner方法即可，每个参数的具体含义可以参考文档
        MagicianConcurrent.getConcurrentCollectionSync().syncRunner(dataList, data -> {

            // 这里可以拿到List里的元素，进行处理
            System.out.println(data);

        }, 10, 1, TimeUnit.MINUTES);

        // 也可以用syncGroupRunner方法，每个参数的具体含义可以参考文档
        MagicianConcurrent.getConcurrentCollectionSync().syncGroupRunner(dataList, data -> {

            // 这里是每一组List
            for(String item : data){
                // 这里可以拿到List里的元素，进行处理
                System.out.println(data);
            }

        }, 10, 1, TimeUnit.MINUTES);
    }
}
