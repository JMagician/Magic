package com.magic.demo.db;

import com.magic.db.conversion.SqlBuilder;

import java.util.ArrayList;
import java.util.List;

public class SqlBuilderTest {

    public static void main(String[] args) {

        ParamPO paramPO = new ParamPO();
        paramPO.setAge(10);
        paramPO.setHeight(1);

        List<Object> params = new ArrayList<>();

        SqlBuilder sqlBuilder = SqlBuilder.builder()
                .init("select * from user_info where 1=1")
                .append("and age > {age}", paramPO.getAge() > 0, ()->{
                    params.add("123");
                })
                .append("and name = {name}", paramPO.getName() != null)
                .append("and height > {height}", paramPO.getHeight() != null);

        System.out.println(sqlBuilder.toString());
        System.out.println(params);
    }

    static class ParamPO {
        private String name;
        private int age;
        private Integer height;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public Integer getHeight() {
            return height;
        }

        public void setHeight(Integer height) {
            this.height = height;
        }
    }
}
