package com.magician.tools.db.model;

import com.magician.tools.util.JSONUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 分页查询参数
 */
public class PageParamModel {

    /**
     * 查询参数
     */
    private Map<String,Object> param;

    /**
     * 查询第几页
     */
    private int currentPage;

    /**
     * 每页大小
     */
    private int pageSize;

    /**
     * 创建并获取一个PageParamModel对象
     * @param page
     * @param pageSize
     * @return
     */
    public static PageParamModel getPageParamModel(Integer page,Integer pageSize){
        PageParamModel pageParamModel = new PageParamModel();
        pageParamModel.setCurrentPage(page);
        pageParamModel.setPageSize(pageSize);
        return pageParamModel;
    }

    public Map<String,Object> getParam() {
        if(param == null){
            param = new HashMap<>();
        }
        return param;
    }

    public PageParamModel setParam(Object param) {
        if(param == null){
            return this;
        }
        this.param = JSONUtil.toMap(param);
        return this;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public PageParamModel setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
        return this;
    }

    public int getPageSize() {
        return pageSize;
    }

    public PageParamModel setPageSize(int pageSize) {
        this.pageSize = pageSize;
        return this;
    }
}
