package com.iblock.web.info;

import com.iblock.dao.po.JobInterest;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by baidu on 16/2/28.
 */
@Data
public class JobInterestInfo {

    private List<KVInfo> industries;
    private List<KVInfo> cities;
    private Integer minPay;
    private Integer maxPay;
    private Boolean isLongTerm;

    public static JobInterestInfo parse(JobInterest interest) {
        JobInterestInfo info = new JobInterestInfo();
        info.setMinPay(interest.getStartPay());
        info.setMaxPay(interest.getEndPay());
        info.setIsLongTerm(interest.getResident());

        return info;

    }
}
