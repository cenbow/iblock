package com.iblock.web.controller;

import com.iblock.common.advice.Auth;
import com.iblock.dao.po.User;
import com.iblock.service.message.SMSService;
import com.iblock.service.user.UserService;
import com.iblock.service.utils.RedisUtils;
import com.iblock.web.enums.ResponseStatus;
import com.iblock.web.request.security.ResetPasswordRequest;
import com.iblock.web.request.security.ValidateVerifyCodeRequest;
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
            if (request.getPhone().equals(user.getMobile())) {
                return new CommonResponse<Void>(ResponseStatus.PARAM_ERROR, "手机号码和原号码相同");
            }
            if (userService.getByMobile(request.getPhone()) != null) {
                return new CommonResponse<Void>(ResponseStatus.PARAM_ERROR, "该手机已被注册");
            }
            user.setMobile(request.getPhone());
            userService.update(user);
            return new CommonResponse<Void>(ResponseStatus.SUCCESS);
        } catch (Exception e) {
            log.error("resetMobile error!", e);
        }
        return new CommonResponse<Void>(ResponseStatus.SYSTEM_ERROR);
    }

    @RequestMapping(value = "/validateVerifyCode", method = RequestMethod.POST)
    @Auth
    @ResponseBody
    public CommonResponse<String> validateVerifyCode(@RequestBody ValidateVerifyCodeRequest request) {
        try {
            User user = userService.getUser(getUserInfo().getId());
            if (!request.getPhone().equals(user.getMobile())) {
                return new CommonResponse<String>(ResponseStatus.PARAM_ERROR, "手机号码不正确");
            }
            if (smsService.checkVerifyCode(request.getPhone(), request.getVerifyCode())) {
                String token = smsService.getRandomString(6);
                set(request.getPhone(), token);
                return new CommonResponse<String>(token);
            }
            return new CommonResponse<String>(ResponseStatus.PARAM_ERROR, "校验码验证错误");
        } catch (Exception e) {
            log.error("validateVerifyCode error!", e);
        }
        return new CommonResponse<String>(ResponseStatus.SYSTEM_ERROR);
    }

    @RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
    @Auth
    @ResponseBody
    public CommonResponse<Void> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            User user = userService.getUser(getUserInfo().getId());
            if (!request.getPhone().equals(user.getMobile())) {
                return new CommonResponse<Void>(ResponseStatus.PARAM_ERROR, "手机号码不正确");
            }
            if (check(request.getPhone(), request.getToken())) {
                user.setPassword(request.getPassword());
                userService.update(user);
                return new CommonResponse<Void>(ResponseStatus.SUCCESS);
            }
            return new CommonResponse<Void>(ResponseStatus.PARAM_ERROR, "TOKEN校验错误");
        } catch (Exception e) {
            log.error("resetPassword error!", e);
        }
        return new CommonResponse<Void>(ResponseStatus.SYSTEM_ERROR);
    }

    @Autowired
    private RedisUtils redisUtils;

    private void set(String phone, String token) {
        redisUtils.put("resetPW_" + phone, token, 300);
    }

    private boolean check(String phone, String token) {
        if (token.equals(redisUtils.fetch("resetPW_" + phone))) {
            redisUtils.rm("resetPW_" + phone);
            return true;
        }
        return false;
    }
}
