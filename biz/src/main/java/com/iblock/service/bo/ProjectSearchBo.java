package com.iblock.service.bo;

import lombok.Data;

import java.util.List;

/**
 * Created by baidu on 16/4/3.
 */
@Data
public class ProjectSearchBo {

    private int pageNo;
    private int pageSize;
    private String order;
    private String orderBy;
    private String keyword;
    private int maxPay;
    private List<String> city;

}
