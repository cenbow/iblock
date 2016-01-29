package com.iblock.web.info;

import lombok.Data;

import java.util.Collections;
import java.util.List;

/**
 * Created by qihong on 16/1/27.
 */
@Data
public class PageInfo<T> {

    public static final long FIRST_PAGE = 1;
    public static final long MAX_PAGE = 10000;

    protected int pageNo;
    protected int pageSize;
    protected String order = "DESC";
    protected String orderBy;
    int totalCount;
    private List<T> result;

    public PageInfo() {
    }

    /**
     * Description:
     */
    public PageInfo(int pageNo, int pageSize, String pageOrder, String pageOrderBy, int totalCount, List<T> result) {
        super();
        if (pageNo < FIRST_PAGE) {
            throw new IllegalArgumentException(String.format("currPage must not less than %d", FIRST_PAGE));
        }
        if (pageNo > MAX_PAGE) {
            throw new IllegalArgumentException(
                    "currPage must not greater than maxPage");
        }
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.order = pageOrder;
        this.orderBy = pageOrderBy;
        this.totalCount = totalCount;
        this.result = (result == null)
                ? Collections.<T> emptyList() : Collections.unmodifiableList(result);
    }

}
