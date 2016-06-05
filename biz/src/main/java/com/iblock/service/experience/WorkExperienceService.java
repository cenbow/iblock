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

    public Long addOrUpdate(WorkExperience experience) {
        experience.setStatus((byte) CommonStatus.NORMAL.getCode());
        if (experience.getId() == null) {
            experience.setAddTime(new Date());
            workExperienceDao.insertSelective(experience);
            return experience.getId();
        } else {
            if (workExperienceDao.selectByPrimaryKey(experience.getId()).getUserId().equals(experience.getId())
                    || workExperienceDao.updateByPrimaryKeyWithBLOBs(experience) < 0) {
                throw new RuntimeException("数据不匹配");
            }
            return experience.getId();
        }
    }

    public boolean remove(Long id, Long userId) {
        return workExperienceDao.updateStatusByUserAndId(userId, id, (byte) CommonStatus.DELETE.getCode()) > 0;
    }
}
