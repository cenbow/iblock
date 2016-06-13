package com.iblock.web.request.project;

import com.iblock.common.utils.DateUtils;
import com.iblock.dao.po.Project;
import com.iblock.web.info.KVInfo;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;

import java.text.ParseException;
import java.util.List;

/**
 * Created by baidu on 16/6/10.
 */
@Data
public class ProjectUpdateRequest {

    private String title;
    private KVInfo city;
    private String district;
    private String description;
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

    public void updateProject(Project project) throws ParseException {
        if (maxPay != null) {
            project.setMaxPay(maxPay);
        }
        if (minPay != null) {
            project.setMinPay(minPay);
        }
        if (isLongTerm != null) {
            project.setResident(isLongTerm);
        }
        if (city != null) {
            project.setCity(city.getId());
        }
        if (description != null) {
            project.setDesc(description);
        }
        if (district != null) {
            project.setDistrict(district);
        }
        if (industryAge != null) {
            project.setIndustryAge(industryAge);
        }
        if (endDate != null) {
            project.setEndTime(DateUtils.parse(endDate, "yyyy-MM-dd"));
        }
        if (startDate != null) {
            project.setStartTime(DateUtils.parse(startDate, "yyyy-MM-dd"));
        }
        if (headCount != null) {
            project.setHeadCount(headCount);
        }
        if (title != null) {
            project.setName(title);
        }
        if (image != null) {
            project.setImage(image);
        }
        if (industry != null) {
            project.setIndustry(industry.getId());
        }
        if (skills != null) {
            if (CollectionUtils.isNotEmpty(skills)) {
                StringBuffer sb = new StringBuffer();
                for (KVInfo kv : skills) {
                    sb.append(kv.getId()).append(",");
                }
                project.setSkills(sb.toString());
            }
        }
    }
}
