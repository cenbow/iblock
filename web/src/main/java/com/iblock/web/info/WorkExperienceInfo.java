package com.iblock.web.info;

import com.iblock.dao.po.WorkExperience;
import com.iblock.service.info.KVInfo;
import lombok.Data;

/**
 * Created by baidu on 16/6/5.
 */
@Data
public class WorkExperienceInfo {

    private Long id;
    private KVInfo industry;
    private int time;
    private String desc;

    public static WorkExperienceInfo parse(WorkExperience experience) {
        WorkExperienceInfo info = new WorkExperienceInfo();
        info.setDesc(experience.getDesc());
        info.setTime(experience.getYear());
        info.setId(experience.getId());
        return info;
    }
}
