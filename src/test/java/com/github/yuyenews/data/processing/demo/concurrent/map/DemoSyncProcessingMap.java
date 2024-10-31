package com.github.yuyenews.data.processing.demo.concurrent.map;

import com.magician.tools.processing.MagicianDataProcessing;

import java.util.HashMap;
import java.util.Map;

public class DemoSyncProcessingMap {

    public static void main(String[] args) {
        Map<String, Object> dataMap = new HashMap<>();

        MagicianDataProcessing.getConcurrentMapSync().syncRunner(dataMap, (key, value) -> {

        });

        MagicianDataProcessing.getConcurrentMapSync().syncGroupRunner(dataMap, data -> {
            // 这里是每一组Map
            for(Map.Entry<String, Object> entry : data.entrySet()){
                // 这里可以拿到Map里的每一个元素
                System.out.println(entry.getKey());
                System.out.println(entry.getValue());
            }
        });
    }
}
