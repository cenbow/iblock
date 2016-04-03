package com.iblock.service.experience;

import com.google.gson.Gson;
import com.iblock.common.enums.CommonExperienceStatus;
import com.iblock.common.utils.DateUtils;
import com.iblock.dao.CommonExperienceDao;
import com.iblock.dao.po.CommonExperience;
import com.iblock.service.message.CommonExperienceMsg;
import com.iblock.service.message.MessageService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * Created by baidu on 16/2/14.
 */
public class CommonExperienceService {

    @Autowired
    private CommonExperienceDao commonExperienceDao;
    @Autowired
    private MessageService messageService;

    public boolean applyFriend(long src, long target, String desc) {
        CommonExperience experience = new CommonExperience();
        experience.setDesc(desc);
        experience.setStatus((byte) CommonExperienceStatus.APPLYING.getCode());
        experience.setUserId(src);
        experience.setFriendId(target);
        if (commonExperienceDao.insertSelective(experience) > 0) {
            messageService.send(buildMsg(experience));
            return true;
        }
        return false;
    }

    public boolean updateStatus(long id, long userId, boolean agree) {
        CommonExperience experience = commonExperienceDao.selectByPrimaryKey(id);
        if (experience == null || !experience.getFriendId().equals(userId)) {
            return false;
        }
        experience.setStatus((byte) (agree ? CommonExperienceStatus.NORMAL.getCode() : CommonExperienceStatus.DELETE.getCode()));
        return commonExperienceDao.updateByPrimaryKeySelective(experience) > 0;
    }

    public boolean isFriend(long src, long target) {
        return commonExperienceDao.selectByUsers(src, target) != null;
    }


    private CommonExperienceMsg buildMsg(CommonExperience experience) {
        CommonExperienceMsg msg = new CommonExperienceMsg();
        msg.setDesc(experience.getDesc());
        msg.setDestuserid(String.valueOf(experience.getFriendId()));
        msg.setSourceuserid(String.valueOf(experience.getUserId()));
        msg.setEndDate(DateUtils.format(experience.getEndDate(), "yyyy-MM-dd"));
        msg.setStartDate(DateUtils.format(experience.getStartDate(), "yyyy-MM-dd"));
        msg.setMessageid(experience.getId().toString());
        return msg;
    }
}
