package com.iblock.web.controller;

import com.iblock.common.advice.Auth;
import com.iblock.common.bean.Page;
import com.iblock.dao.po.Message;
import com.iblock.service.message.MessageService;
import com.iblock.web.enums.ResponseStatus;
import com.iblock.web.info.MessageInfo;
import com.iblock.web.info.PageInfo;
import com.iblock.web.request.message.MessagesRequest;
import com.iblock.web.response.CommonResponse;
import com.iblock.web.response.PageResponse;
import lombok.extern.log4j.Log4j;
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
 * Created by baidu on 16/6/13.
 */
@Controller
@Log4j
public class MessageController extends BaseController {

    @Autowired
    protected MessageService messageService;

    @RequestMapping(value = "/message/update/{id}", method = RequestMethod.GET)
    @Auth
    @ResponseBody
    public CommonResponse<Boolean> read(@PathVariable("id") Long id) {
        try {
            if (!messageService.read(id, getUserInfo().getUserId())) {
                return new CommonResponse<Boolean>(ResponseStatus.VALIDATE_ERROR);
            }
            return new CommonResponse<Boolean>(ResponseStatus.SUCCESS);
        } catch (Exception e) {
            log.error("read message error!", e);
        }
        return new CommonResponse<Boolean>(ResponseStatus.SYSTEM_ERROR);
    }

    @RequestMapping(value = "/user/messages", method = RequestMethod.POST, consumes = "application/json")
    @Auth
    @ResponseBody
    public PageResponse<MessageInfo> messages(@RequestBody MessagesRequest request) {
        try {
            Page<Message> page = messageService.getMsgs(getUserInfo().getUserId(), request.getPageNo(), request
                    .getPageSize(), !request.isUnprocessed());
            List<MessageInfo> list = new ArrayList<MessageInfo>();
            if (page.getResult() != null) {
                for (Message message : page.getResult()) {
                    list.add(MessageInfo.parse(message));
                }
            }
            return new PageResponse<MessageInfo>(new PageInfo<MessageInfo>(page.getPageNo(), page.getPageSize(), page
                    .getOrder(), page.getOrderBy(), page.getTotalCount(), list));
        } catch (Exception e) {
            log.error("messages error!", e);
        }
        return new PageResponse<MessageInfo>(ResponseStatus.SYSTEM_ERROR);
    }
}
