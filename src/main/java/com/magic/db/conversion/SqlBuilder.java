package com.magic.db.conversion;


/**
 * sql构造器
 */
public class SqlBuilder {

    /**
     * sql语句
     */
    private StringBuilder stringBuilder;

    /**
     * 创建一个sql构造器对象
     * @return
     */
    public static SqlBuilder builder(){
        SqlBuilder sqlBuilder = new SqlBuilder();
        sqlBuilder.stringBuilder = new StringBuilder();
        return sqlBuilder;
    }

    /**
     * 传入sql主体
     * @param sql
     * @return
     */
    public SqlBuilder init(String sql){
        stringBuilder.append(sql);
        return this;
    }

    /**
     * 添加where条件
     * @param sql
     * @param needAppend
     * @return
     */
    public SqlBuilder append(String sql, boolean needAppend){
        if(needAppend){
            stringBuilder.append(" ");
            stringBuilder.append(sql);
        }
        return this;
    }

    /**
     * 添加where条件
     * @param sql
     * @param needAppend
     * @param callBack
     * @return
     */
    public SqlBuilder append(String sql, boolean needAppend, CallBack callBack){
        append(sql, needAppend);
        if(needAppend){
            callBack.call();
        }
        return this;
    }

    /**
     * 获取完整的sql
     * @return
     */
    @Override
    public String toString(){
        return stringBuilder.toString();
    }

    /**
     * 回调函数
     */
    public interface CallBack {
        void call();
    }
}
