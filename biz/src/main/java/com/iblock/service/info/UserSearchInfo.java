package com.iblock.service.info;

import lombok.Data;

import java.util.List;

/**
 * Created by baidu on 16/7/24.
 */
@Data
public class UserSearchInfo {
    private Long id;
    private String name;
    private Integer gender;
    private List<KVInfo> city;
    private List<KVInfo> industry;
    private List<KVInfo> skill;
    private String avatar;
    private Integer role;

}
