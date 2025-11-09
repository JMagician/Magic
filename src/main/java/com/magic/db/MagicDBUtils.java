package com.magic.db;

import com.magic.db.conversion.ConditionBuilder;
import com.magic.db.model.PageModel;
import com.magic.db.model.PageParamModel;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

/**
 * 数据库操作工具类
 */
public class MagicDBUtils {

    private JdbcTemplate jdbcTemplate;

    public static MagicDBUtils get(JdbcTemplate jdbcTemplate) {
        MagicDBUtils MagicDBUtils = new MagicDBUtils();
        MagicDBUtils.jdbcTemplate = jdbcTemplate;
        return MagicDBUtils;
    }

    /* -------------------------------------- 单表不需要写SQL的操作 ------------------------------------------ */

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
    public <T> List<T> select(String tableName, ConditionBuilder conditionBuilder, Class<T> cls) {
        return SingleTableUtils.select(jdbcTemplate, tableName, conditionBuilder, cls);
    }

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
    public <T> T selectFirst(String tableName, ConditionBuilder conditionBuilder, Class<T> cls) throws Exception {
        List<T> list = SingleTableUtils.select(jdbcTemplate, tableName, conditionBuilder, cls);
        if (list == null || list.size() == 0) {
            return null;
        }

        if (list.size() > 1) {
            throw new Exception("more than one data");
        }

        return list.get(0);
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
    public int update(String tableName, Object data, ConditionBuilder conditionBuilder) throws Exception {
        return SingleTableUtils.update(jdbcTemplate, tableName, data, conditionBuilder);
    }

    /**
     * 不需要写SQL，单表删除
     *
     * @param tableName
     * @param conditionBuilder
     * @return
     * @throws Exception
     */
    public int delete(String tableName, ConditionBuilder conditionBuilder) throws Exception {
        return SingleTableUtils.delete(jdbcTemplate, tableName, conditionBuilder);
    }

    /**
     * 不需要写SQL，单表插入
     *
     * @param tableName
     * @param data
     * @return
     * @throws Exception
     */
    public int insert(String tableName, Object data) throws Exception {
        return SingleTableUtils.insert(jdbcTemplate, tableName, data);
    }

    /* -------------------------------------- 自定义SQL操作 ------------------------------------------ */

    /**
     * 查询列表
     *
     * @param sql
     * @param param
     * @param cls
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> List<T> selectList(String sql, Object param, Class<T> cls) throws Exception {
        return CustomSqlUtils.selectList(jdbcTemplate, sql, param, cls);
    }

    /**
     * 查询列表，无参数
     *
     * @param sql
     * @param cls
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> List<T> selectList(String sql, Class<T> cls) throws Exception {
        return CustomSqlUtils.selectList(jdbcTemplate, sql, cls);
    }

    /**
     * 查询一条数据
     *
     * @param sql
     * @param cls
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> T selectOne(String sql, Object param, Class<T> cls) throws Exception {
        return CustomSqlUtils.selectOne(jdbcTemplate, sql, param, cls);
    }

    /**
     * 查询一条数据，无参数
     *
     * @param sql
     * @param cls
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> T selectOne(String sql, Class<T> cls) throws Exception {
        return CustomSqlUtils.selectOne(jdbcTemplate, sql, cls);
    }

    /**
     * 增、删、改
     *
     * @param sql
     * @param param
     * @return
     */
    public int exec(String sql, Object param) throws Exception {
        return CustomSqlUtils.exec(jdbcTemplate, sql, param);
    }

    /**
     * 增、删、改，无参数
     *
     * @param sql
     * @return
     */
    public int exec(String sql) throws Exception {
        return CustomSqlUtils.exec(jdbcTemplate, sql);
    }

    /* -------------------------------------- 分页查询 ------------------------------------------ */

    /**
     * 使用默认的count sql 分页查询
     *
     * @param sql
     * @param pageParamModel
     * @param cls
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> PageModel<T> selectPage(String sql, PageParamModel pageParamModel, Class<T> cls) throws Exception {
        return CustomSqlUtils.selectPage(jdbcTemplate, sql, pageParamModel, cls);
    }

    /**
     * 使用自定义的count sql 分页查询
     *
     * @param sql
     * @param countSql
     * @param pageParamModel
     * @param cls
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> PageModel<T> selectPageCustomCountSql(String sql, String countSql, PageParamModel pageParamModel, Class<T> cls) throws Exception {
        return CustomSqlUtils.selectPageCustomCountSql(jdbcTemplate, sql, countSql, pageParamModel, cls);
    }
}
