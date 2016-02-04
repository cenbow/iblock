package com.iblock.service.user;

import com.iblock.common.enums.FriendStatus;
import com.iblock.dao.UserRelationDao;
import com.iblock.dao.po.UserRelation;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by baidu on 16/2/1.
 */
public class UserRelationService {

    @Autowired
    private UserRelationDao userRelationDao;

    public boolean isFriend(long src, long target) {
        return userRelationDao.selectByUsers(src, target) != null;
    }

    public Long applyFriend(long src, long target, String desc) {
        UserRelation relation = new UserRelation();
        relation.setDesc(desc);
        relation.setStatus((byte) FriendStatus.APPLYING.getCode());
        relation.setUserId(src);
        relation.setFriendId(target);
        userRelationDao.insertSelective(relation);
        return relation.getId();
    }

    public boolean updateStatus(long id, long userId, boolean agree) {
        UserRelation relation = userRelationDao.selectByPrimaryKey(id);
        if (relation == null || !relation.getFriendId().equals(userId)) {
            return false;
        }
        relation.setStatus((byte) (agree ? FriendStatus.NORMAL.getCode() : FriendStatus.DELETE.getCode()));
        return userRelationDao.updateByPrimaryKeySelective(relation) > 0;
    }

}
