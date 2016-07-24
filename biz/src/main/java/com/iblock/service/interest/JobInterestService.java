package com.iblock.service.interest;

import com.iblock.dao.CityDao;
import com.iblock.dao.IndustryDao;
import com.iblock.dao.JobInterestDao;
import com.iblock.dao.UserDao;
import com.iblock.dao.po.City;
import com.iblock.dao.po.Industry;
import com.iblock.dao.po.JobInterest;
import com.iblock.service.search.UserSearch;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
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
    private UserSearch userSearch;
    @Autowired
    private UserDao userDao;

    @Transactional
    public boolean addOrUpdate(JobInterest interest) throws IOException {
        boolean result = true;
        if (interest.getId() == null) {
            interest.setAddTime(new Date());
            interest.setStatus(true);
            result = jobInterestDao.insertSelective(interest) > 0;
        } else {
            result = jobInterestDao.updateByUserAndId(interest) > 0;
        }
        userSearch.update(userDao.selectByPrimaryKey(interest.getUserId()));
        return result;
    }

    public JobInterest get(Long userId) {
        return jobInterestDao.selectByUser(userId);
    }
}
