package com.iblock.common.bean;

import lombok.Data;

import java.util.List;

/**
 * Created by qihong on 15/12/21.
 */
@Data
public class PageModel<T> {
    private int pageSize;
    private int page;
    private int recordCount;
    private List<T> records;
}
