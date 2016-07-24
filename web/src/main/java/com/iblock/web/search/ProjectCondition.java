package com.iblock.web.search;

import lombok.Data;

import java.util.List;

/**
 * Created by baidu on 16/7/23.
 */
@Data
public class ProjectCondition {

    private int offset;
    private int pageSize;
    private String order;
    private String orderBy;
    private String keyword;
    private Integer maxPay;
    private Integer minPay;
    private Long agentId;
    private Long managerId;
    private List<Integer> status;
    private List<Integer> city;
    private List<Integer> industry;
    private List<Long> ids;
    private List<Integer> skill;
    private Boolean resident;
    private Boolean freeze;
}
