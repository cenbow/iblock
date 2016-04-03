package com.iblock.common.bean;

import lombok.Data;

import java.util.List;

/**
 * Created by baidu on 16/4/3.
 */
@Data
public class Page<T> {
    private int pageNo;
    private int pageSize;
    private String order;
    private String orderBy;
    private int totalCount;
    private List<T> result;

    public Page(List<T> result, int pageNo, int pageSize, int total) {
        this.result = result;
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.order = "asc";
        this.orderBy = "startTime";
        this.totalCount = total;
    }

    public Page(List<T> result, int pageNo, int pageSize, int total, String order, String orderBy) {
        this.result = result;
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.order = order;
        this.orderBy = orderBy;
        this.totalCount = total;
    }

}
