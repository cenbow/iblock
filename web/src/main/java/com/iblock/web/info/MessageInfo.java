package com.iblock.web.info;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.iblock.common.utils.DateUtils;
import com.iblock.dao.po.Message;
import com.iblock.service.message.MsgContentUtil;
import lombok.Data;

import java.util.Set;

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
        Set<String> paramSet = MsgContentUtil.getInstance().getMsg(message.getAction()).getParams();
        if (paramSet != null && paramSet.contains("msgId")) {
            JSONObject o = JSON.parseObject(message.getParams());
            o.put("msgId", message.getId());
            info.setPostparams(o.toString());
        } else {
            info.setPostparams(message.getParams());
        }
        info.setSendTime(DateUtils.format(message.getAddTime(), "yyyy-MM-dd hh:mm:ss"));
        info.setReadStatus(message.getStatus());
        info.setType(message.getType());
        return info;
    }

}
