package com.iblock.service.user;

import com.iblock.common.enums.UserRole;
import com.iblock.dao.UserDao;
import com.iblock.dao.po.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by qihong on 16/1/25.
 */
@Component
public class UserService {

    @Autowired
    private UserDao userDao;

    public Integer login(String userName, String password, UserRole role) {
        User user = userDao.selectUser(userName, password, role.getRole());
        if (user == null) {
            return null;
        }
        return user.getId();
    }

    public User getUser(Integer userId) {
        return userDao.selectByPrimaryKey(userId);
    }
}
