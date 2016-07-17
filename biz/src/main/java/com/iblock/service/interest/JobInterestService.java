package com.iblock.service.interest;

import com.iblock.dao.CityDao;
import com.iblock.dao.IndustryDao;
import com.iblock.dao.JobInterestDao;
import com.iblock.dao.po.City;
import com.iblock.dao.po.Industry;
import com.iblock.dao.po.JobInterest;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by baidu on 16/2/14.
 */
@Component
public class JobInterestService {

    @Autowired
    private JobInterestDao jobInterestDao;
    @Autowired
    private CityDao cityDao;
    @Autowired
    private IndustryDao industryDao;

    public boolean addOrUpdate(JobInterest interest) {
        if (interest.getId() == null) {
            interest.setAddTime(new Date());
            interest.setStatus(true);
            return jobInterestDao.insertSelective(interest) > 0;
        } else {
            return jobInterestDao.updateByUserAndId(interest) > 0;
        }
    }

    public JobInterest get(Long userId) {
        return jobInterestDao.selectByUser(userId);
    }

    public List<City> getCities(String ids) {
        List<City> list = cityDao.selectByIds(ids);
        return list == null ? new ArrayList<City>() : list;
    }

    public List<Industry> getIndusties(String ids) {
        List<Industry> list = industryDao.selectByIds(ids);
        return list == null ? new ArrayList<Industry>() : list;
    }
}
