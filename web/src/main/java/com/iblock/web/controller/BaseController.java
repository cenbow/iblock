package com.iblock.web.controller;

import com.iblock.web.constant.CommonProperties;
import com.iblock.web.info.UserInfo;
import org.apache.tools.ant.taskdefs.condition.Http;
import org.springframework.beans.factory.annotation.Autowired;
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

    public UserInfo getUserInfo() {
        return (UserInfo) session.getAttribute(CommonProperties.USER_INFO);
    }
}
