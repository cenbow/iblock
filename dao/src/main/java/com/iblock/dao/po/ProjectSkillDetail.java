package com.iblock.dao.po;

import lombok.Data;

import java.util.Date;

/**
 * Created by baidu on 16/7/17.
 */
@Data
public class ProjectSkillDetail {

    private Long id;
    private Long projectId;
    private Integer skillId;
    private String skillName;
    private Byte status;
    private Date updateTime;
    private Date addTime;
}
