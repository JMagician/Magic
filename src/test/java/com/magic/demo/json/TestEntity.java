package com.magic.demo.json;

import com.alibaba.fastjson2.annotation.JSONField;

import java.math.BigDecimal;
import java.util.Date;

public class TestEntity {

    @JSONField(name = "aa")
    private String a;

    private int b;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date c;

    private BigDecimal d;

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

    public int getB() {
        return b;
    }

    public void setB(int b) {
        this.b = b;
    }

    public Date getC() {
        return c;
    }

    public void setC(Date c) {
        this.c = c;
    }

    public BigDecimal getD() {
        return d;
    }

    public void setD(BigDecimal d) {
        this.d = d;
    }
}
