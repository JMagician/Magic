package com.magic.db.model;

/**
 * 处理完SQL里的占位符和参数以后，返回的对象
 */
public class SqlBuilderModel {

    private String sql;

    private Object[] params;

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }
}
