package com.iblock.web.controller;

import com.iblock.common.advice.Auth;
import com.iblock.service.message.MessageService;
import com.iblock.web.constant.RoleConstant;
import com.iblock.web.enums.ResponseStatus;
import com.iblock.web.request.admin.BroadCastRequest;
import com.iblock.web.response.CommonResponse;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by baidu on 16/6/27.
 */
@Controller
@Log4j
@RequestMapping("/message/admin")
public class AdminMessageController extends BaseController {

    @Autowired
    private MessageService messageService;

    @RequestMapping(value = "/new", method = RequestMethod.POST, consumes = "application/json")
    @Auth(role = RoleConstant.ADMINISTRATOR)
    @ResponseBody
    public CommonResponse<Boolean> announce(@RequestBody BroadCastRequest request) {
        try {
            if (messageService.broadCast(request.getContent(), request.getRole())) {
                return new CommonResponse<Boolean>(ResponseStatus.SUCCESS);
            }
        } catch (Exception e) {
            log.error("admin announce error!", e);
        }
        return new CommonResponse<Boolean>(ResponseStatus.SYSTEM_ERROR);
    }
}
