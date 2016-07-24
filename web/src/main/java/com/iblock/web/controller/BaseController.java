package com.iblock.web.controller;

import com.iblock.web.constant.CommonProperties;
import com.iblock.web.info.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.servlet.http.HttpSession;

/**
 * Created by qihong on 16/1/27.
 */
@Controller
public class BaseController {

    @Autowired
    HttpSession session;
    @Value("${mock.user}")
    boolean mockUser;
    @Value("${mock.role}")
    int mockRole;
    @Value("${mock.userId}")
    long mockUserId;

    public UserInfo getUserInfo() {
        UserInfo info = null;
        if (mockUser) {
            info = new UserInfo();
            info.setRole(mockRole);
            info.setUsername("坂田银时");
            info.setId(mockUserId);
        } else if (session != null) {
            info = (UserInfo) session.getAttribute(CommonProperties.USER_INFO);
        }
        return info;
    }


}
