package com.magic.demo.processing.concurrent.task;


import com.magic.processing.MagicDataProcessing;
import com.magic.processing.commons.enums.ConcurrentTaskResultEnum;

import java.util.concurrent.TimeUnit;

public class DemoConcurrentTaskSync {

    public static void main(String[] args) {
        MagicDataProcessing.getConcurrentTaskSync()
                .setTimeout(1000) // 超时时间
                .setTimeUnit(TimeUnit.MILLISECONDS) // 超时时间的单位
                .add(() -> { // 添加一个任务

                    System.out.println("1");

                    // 在这里可以写上任务的业务逻辑
                    Integer.parseInt("1");
                }, (result, e) -> {
                    // 此任务处理后的回调
                    System.out.println("1"+result);

                    if(result.equals(ConcurrentTaskResultEnum.FAIL)){
                        // 任务失败，此时e里面有详细的异常信息
                        e.printStackTrace();
                    } else if(result.equals(ConcurrentTaskResultEnum.SUCCESS)) {
                        // 任务成功，此时e是空的
                    }
                })
                .add(() -> { // 添加一个任务

                    // 在这里可以写上任务的业务逻辑
                    System.out.println("2");
                    Integer.parseInt("2A");
                }, (result, e) -> {
                    // 此任务处理后的回调
                    System.out.println("2"+result);

                    if(result.equals(ConcurrentTaskResultEnum.FAIL)){
                        // 任务失败，此时e里面有详细的异常信息
                        e.printStackTrace();
                    } else if(result.equals(ConcurrentTaskResultEnum.SUCCESS)) {
                        // 任务成功，此时e是空的
                    }
                })
                .start();
    }
}
