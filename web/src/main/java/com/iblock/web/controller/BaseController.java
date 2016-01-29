package com.iblock.web.controller;

import com.iblock.web.constant.CommonProperties;
import com.iblock.web.info.UserInfo;

import javax.servlet.http.HttpSession;

/**
 * Created by qihong on 16/1/27.
 */
public class BaseController {

    protected UserInfo getUserInfo(HttpSession session) {
        return (UserInfo) session.getAttribute(CommonProperties.USER_INFO);
    }
}
