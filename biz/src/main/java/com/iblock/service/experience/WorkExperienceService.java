package com.iblock.service.experience;

import com.iblock.common.enums.CommonStatus;
import com.iblock.dao.WorkExperienceDao;
import com.iblock.dao.po.WorkExperience;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by baidu on 16/2/14.
 */
@Component
public class WorkExperienceService {

    @Autowired
    private WorkExperienceDao workExperienceDao;

    public List<WorkExperience> getByUser(Long userId) {
        List<WorkExperience> list = workExperienceDao.selectByUserId(userId);
        return list == null ? new ArrayList<WorkExperience>() : list;
    }

    public boolean addOrUpdate(WorkExperience experience) {
        if (experience.getId() == null) {
            experience.setAddTime(new Date());
            experience.setStatus((byte) CommonStatus.NORMAL.getCode());
            return workExperienceDao.insertSelective(experience) > 0;
        } else {
            return workExperienceDao.updateByPrimaryKeySelective(experience) > 0;
        }
    }

    public boolean remove(Long id, Long userId) {
        return workExperienceDao.updateStatusByUserAndId(userId, id, (byte) CommonStatus.DELETE.getCode()) > 0;
    }
}
