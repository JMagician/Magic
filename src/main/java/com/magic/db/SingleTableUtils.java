package com.magic.db;

import com.magic.db.conversion.ConditionBuilder;
import com.magic.db.conversion.SqlConversion;
import com.magic.db.model.Condition;
import com.magic.db.model.SqlBuilderModel;
import com.magic.util.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 操作数据库单表的工具类
 */
public class SingleTableUtils {

    private static Logger logger = LoggerFactory.getLogger(SingleTableUtils.class);

    /**
     * 不需要写SQL，单表查询
     *
     * @param tableName
     * @param conditionBuilder
     * @param cls
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> List<T> select(JdbcTemplate jdbcTemplate, String tableName, ConditionBuilder conditionBuilder, Class<T> cls) {
        try {
            StringBuffer sql = new StringBuffer();
            sql.append("select * from ");
            sql.append(tableName);

            List<Map<String, Object>> result = null;

            List<Condition> conditions = conditionBuilder.build();
            if (conditions != null && conditions.size() > 0) {
                sql.append(" where ");
                SqlBuilderModel sqlBuilderModel = SqlConversion.getSql(sql, conditions);

                if (logger.isDebugEnabled()) {
                    logger.debug("select, sql:{}, params:{}", sqlBuilderModel.getSql(), JSONUtil.toJSONString(sqlBuilderModel.getParams()));
                }
                result = jdbcTemplate.queryForList(sqlBuilderModel.getSql(), sqlBuilderModel.getParams());
            } else {
                if (logger.isDebugEnabled()) {
                    logger.debug("select, sql:{}", sql);
                }
                result = jdbcTemplate.queryForList(sql.toString());
            }

            List<T> resultList = new ArrayList<>();
            for (Map<String, Object> item : result) {
                resultList.add(JSONUtil.toJavaObject(item, cls));
            }
            return resultList;

        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 不需要写SQL，单表更新
     *
     * @param tableName
     * @param data
     * @param conditionBuilder
     * @return
     * @throws Exception
     */
    public static int update(JdbcTemplate jdbcTemplate, String tableName, Object data, ConditionBuilder conditionBuilder) throws Exception {
        List<Condition> conditions = conditionBuilder.build();
        if (conditions == null || conditions.size() < 1) {
            throw new Exception("For the sake of safety, please write sql for unconditional modification operations.");
        }
        try {
            Map<String, Object> paramMap = JSONUtil.toMap(data);

            StringBuffer sql = new StringBuffer();
            sql.append("update ");
            sql.append(tableName);
            sql.append(" set ");

            List<Object> paramList = new ArrayList<>();

            Boolean first = false;
            for (Map.Entry<String, Object> item : paramMap.entrySet()) {
                if (item.getValue() == null) {
                    continue;
                }
                if (first) {
                    sql.append(",");
                }
                sql.append(item.getKey());
                sql.append(" = ?");
                paramList.add(item.getValue());

                first = true;
            }
            sql.append(" where ");
            SqlBuilderModel sqlBuilderModel = SqlConversion.getSql(sql, conditions);
            for (Object item : sqlBuilderModel.getParams()) {
                paramList.add(item);
            }

            if (logger.isDebugEnabled()) {
                logger.debug("select, sql:{}, params:{}", sqlBuilderModel.getSql(), JSONUtil.toJSONString(paramList.toArray()));
            }

            return jdbcTemplate.update(sqlBuilderModel.getSql(), paramList.toArray());
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 不需要写SQL，单表删除
     *
     * @param tableName
     * @param conditionBuilder
     * @return
     * @throws Exception
     */
    public static int delete(JdbcTemplate jdbcTemplate, String tableName, ConditionBuilder conditionBuilder) throws Exception {
        List<Condition> conditions = conditionBuilder.build();
        if (conditions == null || conditions.size() < 1) {
            throw new Exception("For the sake of safety, please write sql for unconditional delete operations.");
        }
        try {
            StringBuffer sql = new StringBuffer();
            sql.append("delete from ");
            sql.append(tableName);
            sql.append(" where ");
            SqlBuilderModel sqlBuilderModel = SqlConversion.getSql(sql, conditions);

            if (logger.isDebugEnabled()) {
                logger.debug("select, sql:{}, params:{}", sqlBuilderModel.getSql(), JSONUtil.toJSONString(sqlBuilderModel.getParams()));
            }

            return jdbcTemplate.update(sqlBuilderModel.getSql(), sqlBuilderModel.getParams());
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 不需要写SQL，单表插入
     *
     * @param tableName
     * @param data
     * @return
     * @throws Exception
     */
    public static int insert(JdbcTemplate jdbcTemplate, String tableName, Object data) throws Exception {
        try {
            StringBuffer sql = new StringBuffer();
            sql.append("insert into ");
            sql.append(tableName);
            sql.append(" (");

            StringBuffer values = new StringBuffer();
            values.append(") values (");

            Map<String, Object> paramMap = JSONUtil.toMap(data);

            List<Object> paramList = new ArrayList<>();

            Boolean first = false;
            for (Map.Entry<String, Object> item : paramMap.entrySet()) {
                if (item.getValue() == null) {
                    continue;
                }
                if (first) {
                    sql.append(",");
                    values.append(",");
                }
                sql.append(item.getKey());
                values.append("?");
                paramList.add(item.getValue());

                first = true;
            }

            sql.append(values);
            sql.append(")");

            if (logger.isDebugEnabled()) {
                logger.debug("select, sql:{}, params:{}", sql, JSONUtil.toJSONString(paramList));
            }

            return jdbcTemplate.update(sql.toString(), paramList.toArray());
        } catch (Exception e) {
            throw e;
        }
    }
}
