package com.iblock.web.info;

import lombok.Data;

import java.util.List;

/**
 * Created by baidu on 16/6/5.
 */
@Data
public class UserUpdateInfo {

    private GeoInfo geo;
    private Boolean online;
    private KVInfo education;
    private String corporateName;
    private String corporateBio;
    private List<KVInfo> skills;


}
