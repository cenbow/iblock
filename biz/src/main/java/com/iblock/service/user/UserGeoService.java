package com.iblock.service.user;

import com.iblock.dao.UserGeoDao;
import com.iblock.dao.po.UserGeo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by baidu on 16/2/3.
 */
@Component
public class UserGeoService {

    @Autowired
    private UserGeoDao userGeoDao;

    public List<UserGeo> getUserGeo(double maxLat, double minLat, double maxLon, double minLon) {
        return userGeoDao.selectByCoordinate(maxLat, minLat, maxLon, minLon);
    }
}
