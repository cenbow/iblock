package com.iblock.web.request.user;

import lombok.Data;

/**
 * Created by baidu on 16/2/3.
 */
@Data
public class MapUsersRequest {

    private double maxLat;
    private double minLat;
    private double maxLon;
    private double minLon;
}
