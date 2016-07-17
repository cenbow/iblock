package com.iblock.web.info;

import com.iblock.common.utils.DateUtils;
import com.iblock.dao.po.Project;
import lombok.Data;

import java.util.List;

/**
 * Created by baidu on 16/6/10.
 */
@Data
public class ProjectDetailInfo {

    private Long id;
    private String title;
    private String desc;
    private Integer headCount;
    private String image;
    private String startDate;
    private String endDate;
    private Integer minPay;
    private Integer maxPay;
    private Boolean isLongTerm;
    private KVInfo industry;
    private Integer industryAge;
    private List<KVInfo> skills;
    private Integer status;
    private String creationDate;
    private GeoInfo geo;
    private List<UserSimpleInfo> designer;
    private UserSimpleInfo manager;
    private UserSimpleInfo broker;

    public static ProjectDetailInfo parse(Project p) {
        ProjectDetailInfo info = new ProjectDetailInfo();
        info.setId(p.getId());
        info.setTitle(p.getName());
        info.setHeadCount(p.getHeadCount());
        info.setDesc(p.getDesc());
        info.setImage(p.getImage());
        info.setStartDate(DateUtils.format(p.getStartTime(), "yyyy-MM-dd"));
        info.setEndDate(DateUtils.format(p.getEndTime(), "yyyy-MM-dd"));
        info.setMinPay(p.getMinPay());
        info.setMaxPay(p.getMaxPay());
        info.setIndustryAge(p.getIndustryAge());
        info.setStatus(p.getStatus().intValue());
        info.setIsLongTerm(p.getResident());
        info.setCreationDate(DateUtils.format(p.getAddTime(), "yyyy-MM-dd"));
        return info;
    }

}
