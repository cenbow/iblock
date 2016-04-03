package com.iblock.web.controller;

import com.iblock.common.advice.Auth;
import com.iblock.common.enums.UserRole;
import com.iblock.dao.po.User;
import com.iblock.service.file.FileService;
import com.iblock.service.user.UserService;
import com.iblock.web.constant.CommonProperties;
import com.iblock.web.constant.RoleConstant;
import com.iblock.web.enums.ResponseStatus;
import com.iblock.web.info.UserInfo;
import com.iblock.web.request.user.LoginRequest;
import com.iblock.web.request.user.SendValidateCodeRequest;
import com.iblock.web.request.user.SignUpRequest;
import com.iblock.web.response.CommonResponse;
import lombok.extern.log4j.Log4j;
import nl.captcha.Captcha;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

/**
 * Created by qihong on 16/1/25.
 */

@Controller
@Log4j
@RequestMapping("/user")
public class UserController extends BaseController {

    @Autowired
    protected UserService userService;

    @Autowired
    protected FileService fileService;

    @RequestMapping(value = "/login", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public CommonResponse<UserInfo> login(@RequestBody LoginRequest request, HttpSession session) {
        try {
            User user = userService.login(request.getUserName(), request.getPasswd());
            if (user != null) {
                UserInfo info = new UserInfo(user);
                session.setAttribute(CommonProperties.USER_INFO, info);
                return new CommonResponse<UserInfo>(info);
            }
            return new CommonResponse<UserInfo>(ResponseStatus.NO_AUTH);
        } catch (Exception e) {
            log.error("login error!", e);
        }
        return new CommonResponse<UserInfo>(ResponseStatus.SYSTEM_ERROR);
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    @ResponseBody
    public CommonResponse<Boolean> logout() {
        try {
            session.setAttribute(CommonProperties.USER_INFO, null);
            return new CommonResponse<Boolean>(true);
        } catch (Exception e) {
            log.error("logout error!", e);
        }
        return new CommonResponse<Boolean>(ResponseStatus.SYSTEM_ERROR);
    }

    @RequestMapping(value = "/sendValidateCode", method = RequestMethod.POST, consumes = "application/json")
    @Auth(role = RoleConstant.DESIGNER)
    @ResponseBody
    public CommonResponse<Boolean> sendValidateCode(@RequestBody SendValidateCodeRequest request) {
        try {
            User user = userService.getUser(getUserInfo().getUserId());
            if (!request.getPhone().equals(user.getMobile())) {
                return new CommonResponse<Boolean>(ResponseStatus.VALIDATE_ERROR);
            }
            // todo send code
            return new CommonResponse<Boolean>(true);
        } catch (Exception e) {
            log.error("sendValidateCode error!", e);
        }
        return new CommonResponse<Boolean>(ResponseStatus.SYSTEM_ERROR);
    }

    @RequestMapping(value = "/getUserInfo/{userId}", method = RequestMethod.GET, consumes = "application/json")
    @Auth(role = RoleConstant.DESIGNER)
    @ResponseBody
    public CommonResponse<UserInfo> getUser(@PathVariable(value="userId") Long userId) {
        try {
            return new CommonResponse<UserInfo>(new UserInfo(userService.getUser(userId)));
        } catch (Exception e) {
            log.error("getUser error!", e);
        }
        return new CommonResponse<UserInfo>(ResponseStatus.SYSTEM_ERROR);
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public CommonResponse<Boolean> signUp(SignUpRequest request) {
        try {

//            return new CommonResponse<Boolean>(new UserInfo(userService.getUser(userId)));
        } catch (Exception e) {
            log.error("getUser error!", e);
        }
        return new CommonResponse<Boolean>(ResponseStatus.SYSTEM_ERROR);
    }

    @RequestMapping(value = "/getUserFavicon/{userId}", method = RequestMethod.GET)
    @Auth
    public void getUserFavicon(@PathVariable(value="userId") Long userId, HttpServletResponse response) {
        FileInputStream fis = null;
        try {
            User user = userService.getUser(userId);
            if (user == null && StringUtils.isBlank(user.getHeadFigure())) {
                return;
            }
            OutputStream out = response.getOutputStream();
            File file = new File(user.getHeadFigure());
            fis = new FileInputStream(file);
            byte[] b = new byte[fis.available()];
            fis.read(b);
            out.write(b);
            out.flush();
        } catch (Exception e) {
            log.error("getUserFavicon error!", e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (Exception e) {
                    log.error("close fis error", e);
                }
            }
        }
    }

    @RequestMapping(value = "/updateUserFavicon", method = RequestMethod.POST)
    @Auth
    @ResponseBody
    public CommonResponse<Boolean> updateUserFavicon(@RequestParam(value = "file") CommonsMultipartFile file) {

        try {
            User user = userService.getUser(getUserInfo().getUserId());
            if (user == null) {
                return new CommonResponse<Boolean>(false, ResponseStatus.NOT_FOUND);
            }
            String name = fileService.uploadFile(file);
            user.setHeadFigure(name);
            return new CommonResponse<Boolean>(userService.update(user));
        } catch (Exception e) {
            log.error("getUserFavicon error!", e);
        }
        return new CommonResponse<Boolean>(ResponseStatus.SYSTEM_ERROR);
    }
}
