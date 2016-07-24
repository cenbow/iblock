package com.iblock.service.search;

import lombok.Data;

import java.util.List;

/**
 * Created by baidu on 16/7/24.
 */
@Data
public class UserCondition {

    private int offset;
    private int pageSize;
    private String order;
    private String orderBy;
    private String keyword;
    private Integer maxPay;
    private Integer minPay;
    private List<Integer> status;
    private List<Integer> city;
    private List<Integer> industry;
    private List<Long> ids;
    private List<Integer> skill;
}
