package com.iblock.web.controller;

import com.iblock.common.enums.UserRole;
import com.iblock.dao.po.User;
import com.iblock.service.user.UserService;
import com.iblock.web.enums.ResponseStatus;
import com.iblock.web.info.UserInfo;
import com.iblock.web.request.user.LoginRequest;
import com.iblock.web.response.CommonResponse;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by qihong on 16/1/25.
 */

@Controller
@Log4j
@RequestMapping("/user")
public class UserController extends BaseController {

    @Autowired
    protected UserService userService;

    @RequestMapping(value = "/login.action", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public CommonResponse<Integer> login(@RequestBody LoginRequest request) {
        try {
            Integer i = userService.login(request.getUserName(), request.getPassword(), UserRole.getByCode(request
                    .getRole()));
            return new CommonResponse<Integer>(i);
        } catch (Exception e) {
            log.error("login error!", e);
        }
        return new CommonResponse<Integer>(ResponseStatus.SYSTEM_ERROR);
    }

    @RequestMapping(value = "/user.do", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public CommonResponse<UserInfo> getUser(HttpSession session) {
        try {
            return new CommonResponse<UserInfo>(new UserInfo(userService.getUser(getUserInfo(session).getUserId())));
        } catch (Exception e) {
            log.error("getUser error!", e);
        }
        return new CommonResponse<UserInfo>(ResponseStatus.SYSTEM_ERROR);
    }
}
