package com.iblock.web.request.project;

import lombok.Data;

import java.util.List;

/**
 * Created by baidu on 16/7/17.
 */
@Data
public class MyProjectSearchRequest {

    private List<Integer> status;
    private int pageNo;
    private int pageSize;
}
