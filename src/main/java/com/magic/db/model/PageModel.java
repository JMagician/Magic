package com.magic.db.model;

import java.io.Serializable;
import java.util.List;

/**
 * 分页查询的返回对象
 * @param <T>
 */
public class PageModel<T> implements Serializable {

    /**
     * 当前页
     */
    private Integer currentPage;

    /**
     * 每页大小
     */
    private Integer pageSize;

    /**
     * 总数据条数
     */
    private Long total;

    /**
     * 总页数
     */
    private Long pageTotal;

    /**
     * 分页标识（业务流转中可以携带附加参数）
     */
    private String pageTag;

    /**
     * 当前页的数据
     */
    private List<T> dataList;

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getPageTotal() {
        return pageTotal;
    }

    public void setPageTotal(Long pageTotal) {
        this.pageTotal = pageTotal;
    }

    public List<T> getDataList() {
        return dataList;
    }

    public void setDataList(List<T> dataList) {
        this.dataList = dataList;
    }

    public String getPageTag() {
        return pageTag;
    }

    public void setPageTag(String pageTag) {
        this.pageTag = pageTag;
    }
}
