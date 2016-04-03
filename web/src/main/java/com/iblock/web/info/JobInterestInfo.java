package com.iblock.web.info;

import lombok.Data;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

/**
 * Created by baidu on 16/2/28.
 */
@Data
public class JobInterestInfo {

    private List<String> industry;
    private List<String> city;
    private Integer minPay;
    private Integer maxPay;
    private Boolean isLongTerm;

    public boolean validate() {
        return CollectionUtils.isNotEmpty(industry) && CollectionUtils.isNotEmpty(city) && minPay != null && maxPay
                != null && isLongTerm != null;
    }
}
