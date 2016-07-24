package com.iblock.web.controller;

import com.iblock.common.advice.Auth;
import com.iblock.common.enums.UserStatus;
import com.iblock.dao.po.User;
import com.iblock.service.user.UserService;
import com.iblock.web.constant.RoleConstant;
import com.iblock.web.enums.ResponseStatus;
import com.iblock.web.info.AdminUserInfo;
import com.iblock.web.info.KVInfo;
import com.iblock.web.info.KVLongInfo;
import com.iblock.web.info.UserStatusInfo;
import com.iblock.web.request.admin.AddUserRequest;
import com.iblock.web.response.CommonResponse;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by baidu on 16/6/26.
 */

@Controller
@Log4j
@RequestMapping("/admin/user")
public class AdminUserController extends BaseController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/add", method = RequestMethod.POST, consumes = "application/json")
    @Auth(role = RoleConstant.ADMINISTRATOR)
    @ResponseBody
    public CommonResponse<Boolean> userAdd(@RequestBody AddUserRequest request) {
        try {
            if (userService.simpleAdd(request.toUser())) {
                return new CommonResponse<Boolean>(ResponseStatus.SUCCESS);
            }
        } catch (Exception e) {
            log.error("userAdd error!", e);
        }
        return new CommonResponse<Boolean>(ResponseStatus.SYSTEM_ERROR);
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    @Auth(role = RoleConstant.ADMINISTRATOR)
    @ResponseBody
    public CommonResponse<Boolean> userDelete(@PathVariable("id") Long id) {
        try {
            User user = userService.getUser(id);
            if (user == null) {
                return new CommonResponse<Boolean>(ResponseStatus.PARAM_ERROR, "用户不存在");
            }
            if (user.getStatus().intValue() == UserStatus.DELETE.getCode()) {
                return new CommonResponse<Boolean>(ResponseStatus.PARAM_ERROR, "用户已处删除状态");
            }
            user.setStatus((byte) UserStatus.DELETE.getCode());
            if (userService.update(user)) {
                return new CommonResponse<Boolean>(ResponseStatus.SUCCESS);
            }
        } catch (Exception e) {
            log.error("userDelete error!", e);
        }
        return new CommonResponse<Boolean>(ResponseStatus.SYSTEM_ERROR);
    }

    @RequestMapping(value = "/status/{id}", method = RequestMethod.GET)
    @Auth(role = RoleConstant.ADMINISTRATOR)
    @ResponseBody
    public CommonResponse<UserStatusInfo> userStatus(@PathVariable("id") Long id) {
        try {
            UserStatusInfo info = new UserStatusInfo();
            info.setStatus(userService.getUser(id).getStatus().intValue());
            return new CommonResponse<UserStatusInfo>(info);
        } catch (Exception e) {
            log.error("userStatus error!", e);
        }
        return new CommonResponse<UserStatusInfo>(ResponseStatus.SYSTEM_ERROR);
    }

    @RequestMapping(value = "/freeze/{id}", method = RequestMethod.GET)
    @Auth(role = RoleConstant.ADMINISTRATOR)
    @ResponseBody
    public CommonResponse<Boolean> freeze(@PathVariable("id") Long id) {
        try {
            User user = userService.getUser(id);
            if (user == null) {
                return new CommonResponse<Boolean>(ResponseStatus.PARAM_ERROR, "用户不存在");
            }
            if (user.getStatus().intValue() == UserStatus.DELETE.getCode()) {
                return new CommonResponse<Boolean>(ResponseStatus.PARAM_ERROR, "用户已处删除状态");
            }
            if (user.getStatus().intValue() == UserStatus.FREEZE.getCode()) {
                return new CommonResponse<Boolean>(ResponseStatus.PARAM_ERROR, "用户已处冻结状态");
            }
            user.setStatus((byte) UserStatus.FREEZE.getCode());
            if (userService.update(user)) {
                return new CommonResponse<Boolean>(ResponseStatus.SUCCESS);
            }
        } catch (Exception e) {
            log.error("freeze error!", e);
        }
        return new CommonResponse<Boolean>(ResponseStatus.SYSTEM_ERROR);
    }

    @RequestMapping(value = "/freezed", method = RequestMethod.GET)
    @Auth(role = RoleConstant.ADMINISTRATOR)
    @ResponseBody
    public CommonResponse<List<AdminUserInfo>> freeze() {
        try {
            List<User> users = userService.getUsersByStatus(UserStatus.FREEZE.getCode());
            List<AdminUserInfo> result = new ArrayList<AdminUserInfo>();
            if(CollectionUtils.isNotEmpty(users)) {
                for (User user : users) {
                    AdminUserInfo info = new AdminUserInfo();
                    info.setId(user.getId());
                    info.setName(user.getUserName());
                    info.setMobile(user.getMobile());
                    result.add(info);
                }
            }
            return new CommonResponse<List<AdminUserInfo>>(result);
        } catch (Exception e) {
            log.error("admin list user error!", e);
        }
        return new CommonResponse<List<AdminUserInfo>>(ResponseStatus.SYSTEM_ERROR);
    }

    @RequestMapping(value = "/unfreeze/{id}", method = RequestMethod.GET)
    @Auth(role = RoleConstant.ADMINISTRATOR)
    @ResponseBody
    public CommonResponse<Boolean> unfreeze(@PathVariable("id") Long id) {
        try {
            User user = userService.getUser(id);
            if (user == null) {
                return new CommonResponse<Boolean>(ResponseStatus.PARAM_ERROR, "用户不存在");
            }
            if (user.getStatus().intValue() != UserStatus.FREEZE.getCode()) {
                return new CommonResponse<Boolean>(ResponseStatus.PARAM_ERROR, "用户未处冻结状态");
            }
            user.setStatus((byte) UserStatus.NORMAL.getCode());
            if (userService.update(user)) {
                return new CommonResponse<Boolean>(ResponseStatus.SUCCESS);
            }
        } catch (Exception e) {
            log.error("unfreeze error!", e);
        }
        return new CommonResponse<Boolean>(ResponseStatus.SYSTEM_ERROR);
    }

    @RequestMapping(value = "/list/{role}", method = RequestMethod.GET)
    @Auth(role = RoleConstant.ADMINISTRATOR)
    @ResponseBody
    public CommonResponse<List<AdminUserInfo>> getRole(@PathVariable("role") Integer role) {
        try {
            List<User> users = userService.getUsersByRole(role);
            List<AdminUserInfo> result = new ArrayList<AdminUserInfo>();
            if(CollectionUtils.isNotEmpty(users)) {
                for (User user : users) {
                    AdminUserInfo info = new AdminUserInfo();
                    info.setId(user.getId());
                    info.setName(user.getUserName());
                    info.setMobile(user.getMobile());
                    result.add(info);
                }
            }
            return new CommonResponse<List<AdminUserInfo>>(result);
        } catch (Exception e) {
            log.error("admin list user error!", e);
        }
        return new CommonResponse<List<AdminUserInfo>>(ResponseStatus.SYSTEM_ERROR);
    }
}
