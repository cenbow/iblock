package com.iblock.web.info;

import com.iblock.common.utils.DateUtils;
import com.iblock.dao.po.Project;
import lombok.Data;

/**
 * Created by baidu on 16/7/17.
 */
@Data
public class ProjectSimpleInfo {

    private Long id;
    private String name;
    private String desc;
    private KVLongInfo manager;
    private KVLongInfo broker;
    private Integer status;
    private String startDate;
    private KVInfo city;
    private KVInfo industry;
    private String image;

    public static ProjectSimpleInfo parse(Project p) {
        ProjectSimpleInfo info = new ProjectSimpleInfo();
        info.setId(p.getId());
        info.setName(p.getName());
        info.setDesc(p.getDesc());
        info.setStartDate(DateUtils.format(p.getAddTime(), "yyyy-MM-dd"));
        info.setImage(p.getImage());
        info.setStatus(p.getStatus().intValue());
        return info;
    }
}
