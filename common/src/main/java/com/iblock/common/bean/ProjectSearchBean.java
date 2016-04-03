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
    private Integer role;
    private Long userId;
    private Integer status;
    private String order;
    private String orderBy;
    private String keyword;
    private Integer maxPay;
    private List<String> city;
    private List<Integer> industry;
}
