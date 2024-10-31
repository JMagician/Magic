package com.magician.tools.db.model;

/**
 * 条件对象
 */
public class Condition {

    private String key;

    private Object[] val;

    public static final String NOT_WHERE = "6ca6d99a-2ca3-4734-921d-f3718bb7e179";

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object[] getVal() {
        return val;
    }

    public void setVal(Object[] val) {
        this.val = val;
    }

    /**
     * 创建并获取一个条件对象
     * @param key
     * @param val
     * @return
     */
    public static Condition get(String key, Object... val){
        Condition condition = new Condition();
        condition.setKey(key);
        condition.setVal(val);
        return condition;
    }
}
