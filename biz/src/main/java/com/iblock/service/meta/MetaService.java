package com.iblock.service.meta;

import com.iblock.common.enums.CommonStatus;
import com.iblock.dao.CityDao;
import com.iblock.dao.DistrictDao;
import com.iblock.dao.IndustryDao;
import com.iblock.dao.SkillDao;
import com.iblock.dao.po.City;
import com.iblock.dao.po.District;
import com.iblock.dao.po.Industry;
import com.iblock.dao.po.Skill;
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
    @Autowired
    private IndustryDao industryDao;
    @Autowired
    private SkillDao skillDao;

    public List<City> getCity(List<Integer> list) {
        return cityDao.selectByIds(list);
    }

    public List<Industry> getIndustry(List<Integer> list) {
        return industryDao.selectByIds(list);
    }

    public List<City> getCities(String name) {
        return cityDao.selectByName(StringUtils.isBlank(name) ? null : name);
    }

    public List<District> getDistricts(Integer cityId) {
        return districtDao.selectByCity(cityId);
    }

    public boolean deleteSkill(Integer id) {
        Skill skill = skillDao.selectByPrimaryKey(id);
        if (skill == null || skill.getStatus().intValue() == CommonStatus.DELETE.getCode()) {
            return false;
        }
        skill.setStatus((byte) CommonStatus.DELETE.getCode());
        return skillDao.updateByPrimaryKeySelective(skill) > 0;
    }

    public boolean deleteIndustry(Integer id) {
        Industry industry = industryDao.selectByPrimaryKey(id);
        if (industry == null || industry.getStatus().intValue() == CommonStatus.DELETE.getCode()) {
            return false;
        }
        industry.setStatus((byte) CommonStatus.DELETE.getCode());
        return industryDao.updateByPrimaryKeySelective(industry) > 0;
    }
}
