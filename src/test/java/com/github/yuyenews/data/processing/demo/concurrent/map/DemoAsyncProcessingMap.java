package com.github.yuyenews.data.processing.demo.concurrent.map;

import com.github.yuyenews.data.processing.MagicianDataProcessing;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class DemoAsyncProcessingMap {

    public static void main(String[] args) {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("a", "aa");

        MagicianDataProcessing.getConcurrentMapAsync().asyncRunner(dataMap, (key, value) -> {
            System.out.println(key + "-" + value);
        }).start();

        MagicianDataProcessing.getConcurrentMapAsync().asyncGroupRunner(dataMap, data -> {
            System.out.println(data);
        }).start();
    }
}
