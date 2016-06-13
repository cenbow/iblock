package com.iblock.web.info;

import com.iblock.dao.po.UserGeo;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by baidu on 16/2/8.
 */
@Data
@AllArgsConstructor
public class GeoInfo {
    private KVInfo city;
    private String district;
    private String address;
    private Float longitude;
    private Float latitude;

    public GeoInfo() {}

    public static GeoInfo parse(UserGeo geo) {
        GeoInfo info = new GeoInfo();
        info.setAddress(geo.getAddress());
        info.setCity(new KVInfo(geo.getCityId(), geo.getCity()));
        info.setDistrict(geo.getDistrict());
        info.setLatitude(geo.getLatitude());
        info.setLongitude(geo.getLongitude());
        return info;
    }

}
