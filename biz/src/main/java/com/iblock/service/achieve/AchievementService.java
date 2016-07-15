package com.iblock.service.achieve;

import com.iblock.dao.AchievementDao;
import com.iblock.dao.po.Achievement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * Created by baidu on 16/7/15.
 */
@Component
public class AchievementService {

    @Autowired
    private AchievementDao achievementDao;

    public boolean add(Achievement achievement) {
        achievement.setAddTime(new Date());
        achievement.setStatus(true);
        return achievementDao.insertSelective(achievement) > 0;
    }

    public List<Achievement> getByUser(Long userId) {
        return achievementDao.selectByUser(userId);
    }

    public boolean delete(long achievementId, long userId) {
        Achievement achievement = achievementDao.selectByUserAndId(userId, achievementId);
        if (achievement == null) {
            return false;
        }
        achievement.setStatus(false);
        return achievementDao.updateByPrimaryKey(achievement) > 0;

    }
}
