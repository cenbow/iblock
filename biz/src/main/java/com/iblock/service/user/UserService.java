package com.iblock.service.user;

import com.iblock.common.enums.UserRole;
import com.iblock.dao.UserDao;
import com.iblock.dao.po.User;
import com.iblock.dao.po.UserDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by qihong on 16/1/25.
 */
@Component
public class UserService {

    @Autowired
    private UserDao userDao;

    public User login(String userName, String password) {
        return userDao.selectUser(userName, password);
    }

    public User getUser(Long userId) {
        return userDao.selectByPrimaryKey(userId);
    }

    public UserDetail getUserDetail(Long userId) {
        return userDao.selectDetailById(userId);
    }

    public boolean update(User user) {
        return userDao.updateByPrimaryKeySelective(user) > 0;
    }
}
