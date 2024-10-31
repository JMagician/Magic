package com.magician.tools.db.conversion;


import com.magician.tools.db.model.Condition;
import com.magician.tools.util.JSONUtil;
import com.magician.tools.db.model.SqlBuilderModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SQL转化器
 */
public class SqlConversion {

    /**
     * 根据条件构造器生成sql和参数
     * @param sql
     * @param conditions
     * @return
     */
    public static SqlBuilderModel getSql(StringBuffer sql, List<Condition> conditions){
        SqlBuilderModel sqlBuilderModel = new SqlBuilderModel();
        List<Object> params = new ArrayList<>();
        for (Condition condition : conditions) {
            if (condition.getVal() == null) {
                continue;
            }
            sql.append(" ");
            sql.append(condition.getKey());

            if (isNotWhere(condition.getVal())) {
                continue;
            }
            for(Object arg : condition.getVal()){
                params.add(arg);
            }
        }

        sqlBuilderModel.setSql(sql.toString());
        sqlBuilderModel.setParams(params.toArray());
        return sqlBuilderModel;
    }

    /**
     * 是否是WHERE条件
     * @param val
     * @return
     */
    private static boolean isNotWhere(Object[] val){
        if(val.length == 1 && val[0].equals(Condition.NOT_WHERE)) {
            return true;
        }
        return false;
    }

    /**
     * 根据实体类型的参数生成sql和参数
     * 里面会将{}占位符替换成?，将实体类型的参数转化成Object[]
     *
     * @param sql
     * @param param
     * @return
     * @throws Exception
     */
    public static SqlBuilderModel builderSql(String sql, Object param) throws Exception {

        SqlBuilderModel sqlBuilderModel = new SqlBuilderModel();

        if (param == null) {
            throw new NullPointerException("param cannot null");
        }

        if (param instanceof Object[]) {
            sqlBuilderModel.setSql(sql);
            sqlBuilderModel.setParams((Object[]) param);
            return sqlBuilderModel;
        }

        Map<String, Object> jsonObject = JSONUtil.toMap(param);

        List<Object> params = new ArrayList<>();

        sql = formatMatcher(sql,params,jsonObject);

        sqlBuilderModel.setSql(sql);
        sqlBuilderModel.setParams(params.toArray());

        return sqlBuilderModel;
    }

    /**
     * 将SQL里的{}占位符替换成?
     * @param sql
     * @param params
     * @param jsonObject
     * @return
     */
    private static String formatMatcher(String sql,List<Object> params, Map<String, Object> jsonObject){
        Pattern pattern = Pattern.compile("(\\{((?!}).)*\\})");
        Matcher matcher = pattern.matcher(sql);
        while (matcher.find()) {
            String matcherName = matcher.group();
            sql = sql.replace(matcherName,"?");
            String filedName = matcherName.replace("{","").replace("}","");
            params.add(jsonObject.get(filedName));
        }
        return sql;
    }
}
