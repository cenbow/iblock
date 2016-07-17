package com.iblock.service.interest;

import com.iblock.dao.CityDao;
import com.iblock.dao.IndustryDao;
import com.iblock.dao.JobInterestDao;
import com.iblock.dao.po.City;
import com.iblock.dao.po.Industry;
import com.iblock.dao.po.JobInterest;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
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
        List<Integer> ii = new ArrayList<Integer>();
        for (String s : ids.split(",")) {
            if (StringUtils.isNotBlank(s)) {
                ii.add(Integer.parseInt(s));
            }
        }
        if (CollectionUtils.isEmpty(ii)) {
            return new ArrayList<City>();
        }
        List<City> list = cityDao.selectByIds(ii);
        return list == null ? new ArrayList<City>() : list;
    }

    public List<Industry> getIndusties(String ids) {
        List<Integer> ii = new ArrayList<Integer>();
        for (String s : ids.split(",")) {
            if (StringUtils.isNotBlank(s)) {
                ii.add(Integer.parseInt(s));
            }
        }
        if (CollectionUtils.isEmpty(ii)) {
            return new ArrayList<Industry>();
        }
        List<Industry> list = industryDao.selectByIds(ii);
        return list == null ? new ArrayList<Industry>() : list;
    }
}
