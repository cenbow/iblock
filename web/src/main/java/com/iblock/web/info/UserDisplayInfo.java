package com.iblock.web.info;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by baidu on 16/6/5.
 */
@Data
public class UserDisplayInfo implements Serializable {

    private Long userId;
    private String username;
    private Integer gender;
    private Double rating;
    private KVInfo education;
    private Boolean online;
    private Integer role;
    private String contactPhone;
    private GeoInfo geo;
    private List<SkillInfo> skills;
    private String avatar;
    private String corporateName;
    private String corporateBio;

}
