package com.iblock.web.request.user;

import com.iblock.service.info.KVInfo;
import lombok.Data;

import java.util.List;

/**
 * Created by baidu on 16/7/24.
 */
@Data
public class SearchDesignerRequest {

    private String keyword;
    private Integer minPay;
    private Integer maxPay;
    private List<KVInfo> skill;
    private List<KVInfo> city;
    private List<KVInfo> industry;
    private Integer pageNo;
    private Integer pageSize;
}
