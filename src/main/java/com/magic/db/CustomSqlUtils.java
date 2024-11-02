package com.magic.db;

import com.magic.db.conversion.SqlConversion;
import com.magic.db.model.PageModel;
import com.magic.db.model.PageParamModel;
import com.magic.db.model.SqlBuilderModel;
import com.magic.util.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 用自定义SQL 操作数据库的工具类
 */
public class CustomSqlUtils {

    private static Logger logger = LoggerFactory.getLogger(CustomSqlUtils.class);

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
    public static <T> List<T> selectList(JdbcTemplate jdbcTemplate, String sql, Object param, Class<T> cls) throws Exception {
        try {
            SqlBuilderModel sqlBuilderModel = SqlConversion.builderSql(sql, param);

            if(logger.isDebugEnabled()){
                logger.debug("selectList, sql:{}, params:{}", sqlBuilderModel.getSql(), sqlBuilderModel.getParams());
            }

            List<Map<String, Object>> result = jdbcTemplate.queryForList(sqlBuilderModel.getSql(), sqlBuilderModel.getParams());

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
     * 查询列表，无参数
     *
     * @param sql
     * @param cls
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> List<T> selectList(JdbcTemplate jdbcTemplate, String sql, Class<T> cls) throws Exception {
        return selectList(jdbcTemplate, sql, new Object[0], cls);
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
    public static <T> T selectOne(JdbcTemplate jdbcTemplate, String sql, Object param, Class<T> cls) throws Exception {
        List<T> resultList = selectList(jdbcTemplate, sql, param, cls);
        if (resultList != null && resultList.size() > 1) {
            throw new Exception("more than one data");
        }
        if (resultList != null && resultList.size() < 1) {
            return null;
        }
        return resultList.get(0);
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
    public static <T> T selectOne(JdbcTemplate jdbcTemplate, String sql, Class<T> cls) throws Exception {
        return selectOne(jdbcTemplate, sql, new Object[0], cls);
    }

    /**
     * 增、删、改
     *
     * @param sql
     * @param param
     * @return
     */
    public static int exec(JdbcTemplate jdbcTemplate, String sql, Object param) throws Exception {
        try {
            SqlBuilderModel sqlBuilderModel = SqlConversion.builderSql(sql, param);

            if(logger.isDebugEnabled()){
                logger.debug("exec, sql:{}, params:{}", sqlBuilderModel.getSql(), JSONUtil.toJSONString(sqlBuilderModel.getParams()));
            }

            return jdbcTemplate.update(sqlBuilderModel.getSql(), sqlBuilderModel.getParams());
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 增、删、改，无参数
     *
     * @param sql
     * @return
     */
    public static int exec(JdbcTemplate jdbcTemplate, String sql) throws Exception {
        return exec(jdbcTemplate, sql, new Object[0]);
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
    public static <T> PageModel<T> selectPage(JdbcTemplate jdbcTemplate, String sql, PageParamModel pageParamModel, Class<T> cls) throws Exception {
        String countSql = "select count(0) total from(" + sql + ") tbl";
        return selectPageCustomCountSql(jdbcTemplate, sql, countSql, pageParamModel, cls);
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
    public static <T> PageModel<T> selectPageCustomCountSql(JdbcTemplate jdbcTemplate, String sql, String countSql, PageParamModel pageParamModel, Class<T> cls) throws Exception {

        Map result = selectOne(jdbcTemplate, countSql, pageParamModel.getParam(), Map.class);
        Object totalObj = result.get("total");
        if (totalObj == null || "".equals(totalObj)) {
            totalObj = 0;
        }

        pageParamModel.getParam().put("pageStart", (pageParamModel.getCurrentPage() - 1) * pageParamModel.getPageSize());
        pageParamModel.getParam().put("pageSize", pageParamModel.getPageSize());

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(sql);
        stringBuffer.append(" limit {pageStart},{pageSize}");

        List<T> resultList = selectList(jdbcTemplate, stringBuffer.toString(), pageParamModel.getParam(), cls);

        PageModel<T> pageModel = new PageModel<>();
        pageModel.setTotal(Integer.parseInt(totalObj.toString()));
        pageModel.setCurrentPage(pageParamModel.getCurrentPage());
        pageModel.setPageSize(pageParamModel.getPageSize());
        pageModel.setPageTotal(getPageTotal(pageModel));
        pageModel.setDataList(resultList);
        return pageModel;
    }

    /**
     * 计算总页数
     *
     * @param pageModel
     * @return
     */
    private static int getPageTotal(PageModel pageModel) {
        if (pageModel.getTotal() % pageModel.getPageSize() == 0) {
            return pageModel.getTotal() / pageModel.getPageSize();
        } else {
            return pageModel.getTotal() / pageModel.getPageSize() + 1;
        }
    }
}
