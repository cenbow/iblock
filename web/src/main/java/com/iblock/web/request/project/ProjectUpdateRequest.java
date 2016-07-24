package com.iblock.web.request.project;

import com.iblock.common.enums.CommonStatus;
import com.iblock.common.utils.DateUtils;
import com.iblock.dao.po.Project;
import com.iblock.dao.po.ProjectSkill;
import com.iblock.web.info.GeoInfo;
import com.iblock.service.info.KVInfo;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by baidu on 16/6/10.
 */
@Data
public class ProjectUpdateRequest {

    private Long id;
    private String title;
    private GeoInfo geo;
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
        if (geo != null) {
            if (geo.getCity() != null) {
                project.setCity(geo.getCity().getId());
            }
            if (geo.getDistrict() != null) {
                project.setDistrict(geo.getDistrict());
            }
            if (geo.getAddress() != null) {
                project.setAddress(geo.getAddress());
            }
        }
        if (description != null) {
            project.setDesc(description);
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
    }

    public List<ProjectSkill> toSkills() {
        List<ProjectSkill> list = new ArrayList<ProjectSkill>();
        if (CollectionUtils.isNotEmpty(skills)) {
            for (KVInfo kv : skills) {
                ProjectSkill skill = new ProjectSkill();
                skill.setAddTime(new Date());
                skill.setStatus((byte) CommonStatus.NORMAL.getCode());
                skill.setSkillId(kv.getId());
                list.add(skill);
            }
        }
        return list;
    }
}
