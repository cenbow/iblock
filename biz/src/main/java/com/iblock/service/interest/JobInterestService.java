package com.iblock.service.interest;

import com.iblock.dao.JobInterestDao;
import com.iblock.dao.po.JobInterest;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by baidu on 16/2/14.
 */
@Component
public class JobInterestService {

    @Autowired
    private JobInterestDao jobInterestDao;

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
}
