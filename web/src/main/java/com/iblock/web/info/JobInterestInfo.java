package com.iblock.web.info;

import com.iblock.dao.po.JobInterest;
import com.iblock.service.info.KVInfo;
import lombok.Data;

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
