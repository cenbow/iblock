package com.iblock.web.info;

import com.iblock.common.utils.DateUtils;
import com.iblock.dao.po.Message;
import lombok.Data;

/**
 * Created by baidu on 16/6/13.
 */
@Data
public class MessageInfo {
    private int readStatus;
    private String sendTime;
    private int type;
    private String msg;
    private String service;
    private String postparams;
    private String inputparams;

    public static MessageInfo parse(Message message) {
        MessageInfo info = new MessageInfo();
        info.setService(message.getService());
        info.setInputparams(message.getInputType());
        info.setMsg(message.getDetail());
        info.setPostparams(message.getParams());
        info.setSendTime(DateUtils.format(message.getAddTime(), "yyyy-MM-dd hh:mm:ss"));
        info.setReadStatus(message.getStatus());
        info.setType(message.getType());
        return info;
    }

}
