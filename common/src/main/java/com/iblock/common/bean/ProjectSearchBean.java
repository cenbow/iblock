package com.iblock.common.bean;

import lombok.Data;

import java.util.List;

/**
 * Created by baidu on 16/4/3.
 */
@Data
public class ProjectSearchBean {

    private int pageNo;
    private int pageSize;
    private String order;
    private String orderBy;
    private String keyword;
    private Integer maxPay;
    private Integer minPay;
    private Integer status;
    private List<Integer> city;
    private List<Integer> industry;
    private Boolean resident;
    private Boolean freeze;
}
