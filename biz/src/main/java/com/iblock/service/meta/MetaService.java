package com.iblock.service.meta;

import com.iblock.dao.CityDao;
import com.iblock.dao.DistrictDao;
import com.iblock.dao.po.City;
import com.iblock.dao.po.District;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * Created by baidu on 16/7/1.
 */
@Component
public class MetaService {

    @Autowired
    private CityDao cityDao;
    @Autowired
    private DistrictDao districtDao;

    public List<City> getCities(String name) {
        return cityDao.selectByName(StringUtils.isBlank(name) ? null : name);
    }

    public List<District> getDistricts(Integer cityId) {
        return districtDao.selectByCity(cityId);
    }
}
