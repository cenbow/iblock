package com.iblock.web.request.project;

import com.iblock.common.enums.CommonStatus;
import com.iblock.common.utils.DateUtils;
import com.iblock.dao.po.Project;
import com.iblock.dao.po.ProjectSkill;
import com.iblock.web.info.KVInfo;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by baidu on 16/6/13.
 */
@Data
public class ProjectCreateRequest {

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

    public Project toProject() throws ParseException {
        Project project = new Project();
        project.setMaxPay(maxPay);
        project.setMinPay(minPay);
        project.setResident(isLongTerm);
        project.setCity(city.getId());
        project.setDesc(description);
        project.setDistrict(district);
        project.setIndustryAge(industryAge);
        project.setEndTime(DateUtils.parse(endDate, "yyyy-MM-dd"));
        project.setStartTime(DateUtils.parse(startDate, "yyyy-MM-dd"));
        project.setHeadCount(headCount);
        project.setName(title);
        project.setImage(image);
        project.setIndustry(industry.getId());
        return project;
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
