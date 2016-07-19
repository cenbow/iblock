package com.iblock.web.controller;

import com.iblock.common.advice.Auth;
import com.iblock.dao.po.User;
import com.iblock.service.message.SMSService;
import com.iblock.service.user.UserService;
import com.iblock.web.enums.ResponseStatus;
import com.iblock.web.request.user.ModifyPasswordRequest;
import com.iblock.web.request.user.ResetMobileRequest;
import com.iblock.web.response.CommonResponse;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by baidu on 16/7/19.
 */
@Controller
@Log4j
@RequestMapping("/account/security")
public class AccountController extends BaseController {

    @Autowired
    private UserService userService;
    @Autowired
    private SMSService smsService;

    @RequestMapping(value = "/updatePassword", method = RequestMethod.POST)
    @Auth
    @ResponseBody
    public CommonResponse<Void> modifyPassword(@RequestBody ModifyPasswordRequest request) {
        try {
            User user = userService.getUser(getUserInfo().getId());
            if (!request.getOldPassword().equals(user.getPassword())) {
                return new CommonResponse<Void>(ResponseStatus.PARAM_ERROR, "旧密码输入错误");
            }
            user.setPassword(request.getNewPassword());
            userService.update(user);
            return new CommonResponse<Void>(ResponseStatus.SUCCESS);
        } catch (Exception e) {
            log.error("modifyPassword error!", e);
        }
        return new CommonResponse<Void>(ResponseStatus.SYSTEM_ERROR);
    }

    @RequestMapping(value = "/resetMobile", method = RequestMethod.POST)
    @Auth
    @ResponseBody
    public CommonResponse<Void> resetMobile(@RequestBody ResetMobileRequest request) {
        try {
            if (!smsService.checkVerifyCode(request.getPhone(), request.getVerifyCode())) {
                return new CommonResponse<Void>(ResponseStatus.PARAM_ERROR, "验证码校验失败");
            }
            User user = userService.getUser(getUserInfo().getId());
            if (!user.getPassword().equals(request.getPassword())) {
                return new CommonResponse<Void>(ResponseStatus.PARAM_ERROR, "密码校验失败");
            }
            user.setMobile(request.getPhone());
            userService.update(user);
            return new CommonResponse<Void>(ResponseStatus.SUCCESS);
        } catch (Exception e) {
            log.error("resetMobile error!", e);
        }
        return new CommonResponse<Void>(ResponseStatus.SYSTEM_ERROR);
    }
}
