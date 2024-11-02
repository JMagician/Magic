package com.magic.demo.properties;

import com.magic.properties.MagicProperties;
import com.magic.properties.enums.ReadMode;

public class MagicPropertiesDemo {

    public static void main(String[] args) {
        try {
            MagicProperties.load("/home/yuye/test.properties", ReadMode.LOCAL, "UTF-8");

            MagicProperties.forEach((key, value)->{
                System.out.println(key);
                System.out.println(value);
            });
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
