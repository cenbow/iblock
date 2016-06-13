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

    private Long id;
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
        project.setEndTime(DateUtils.parse(endDate, "yyyy-MM-dd"));
        project.setStartTime(DateUtils.parse(startDate, "yyyy-MM-dd"));
        project.setHeadCount(headCount);
        project.setId(id);
        project.setImage(image);
        if (CollectionUtils.isNotEmpty(skills)) {
            StringBuffer sb = new StringBuffer();
            for (KVInfo kv : skills) {
                sb.append(kv.getId()).append(",");
            }
            project.setSkills(sb.toString());
        }
        return project;
    }
}
