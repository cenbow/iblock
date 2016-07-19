package com.iblock.common.bean;

import lombok.Data;

import java.util.List;

/**
 * Created by baidu on 16/7/19.
 */
@Data
public class UserSearchBean {

    private String keyword;
    private List<Integer> cities;
    private List<Integer> skills;
    private List<Integer> industries;
    private Integer minPay;
    private Integer maxPay;
}
