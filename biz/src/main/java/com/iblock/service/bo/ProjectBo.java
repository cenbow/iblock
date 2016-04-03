package com.iblock.service.bo;

import com.iblock.dao.po.Project;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by baidu on 16/2/1.
 */
@Data
public class ProjectBo implements Serializable {

    private Long id;
    private String name;
    private String address;
    private String city;
    private Boolean isLongTerm;
    private Boolean achieveVisible;
    private String desc;

    public Project toProject() {
        Project p = new Project();
        p.setName(name);
        p.setCity(city);
        p.setResident(isLongTerm);
        // todo industry
        p.setAchievementVisable(achieveVisible);
        p.setDesc(desc);
        p.setAddress(address);
        return p;
    }

    public void updateProject(Project p) {
        p.setName(name);
        p.setCity(city);
        p.setResident(isLongTerm);
        // todo industry
        p.setAchievementVisable(achieveVisible);
        p.setDesc(desc);
        p.setAddress(address);
    }

}
