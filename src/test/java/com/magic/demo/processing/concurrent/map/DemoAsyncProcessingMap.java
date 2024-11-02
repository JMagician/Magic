package com.magic.demo.processing.concurrent.map;


import com.magic.processing.MagicDataProcessing;

import java.util.HashMap;
import java.util.Map;

public class DemoAsyncProcessingMap {

    public static void main(String[] args) {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("a", "aa");

        MagicDataProcessing.getConcurrentMapAsync().asyncRunner(dataMap, (key, value) -> {
            System.out.println(key + "-" + value);
        }).start();

        MagicDataProcessing.getConcurrentMapAsync().asyncGroupRunner(dataMap, data -> {
            System.out.println(data);
        }).start();
    }
}
