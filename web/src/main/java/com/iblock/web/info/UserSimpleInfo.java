package com.iblock.web.info;

import lombok.Data;

/**
 * Created by baidu on 16/6/10.
 */
@Data
public class UserSimpleInfo {
    private Long id;
    private String username;
    private String avatar;
    private double rating;
}
