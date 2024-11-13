package com.magic.demo.json;

import com.magic.util.JSONUtil;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

public class TestJsonUtil {

    public static void main(String[] args) {
        TestEntity testEntity = new TestEntity();
        testEntity.setA("!");
        testEntity.setB(22);
        testEntity.setC(new Date());
        testEntity.setD(BigDecimal.ONE);

        String s = JSONUtil.toJSONString(testEntity);

        Map map = JSONUtil.toMap(testEntity);

        TestEntity2 testEntity2 = JSONUtil.toJavaObject(map, TestEntity2.class);

        System.out.println("ok");
    }
}
