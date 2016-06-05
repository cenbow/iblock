package com.iblock.dao.po;

import lombok.Data;

/**
 * Created by baidu on 16/2/4.
 */
@Data
public class UserDetail {

    private Integer id;

    private String userName;

    private Byte role;

    private Boolean sex;

    private Byte status;

    private String headFigure;

    private Byte education;

    private String skills;

    private Boolean online;

    private String province;

    private Integer provinceId;

    private String city;

    private Integer cityId;

    private String district;

    private Integer districtId;

    private String address;

    private Float latitude;

    private Float longitude;

}
