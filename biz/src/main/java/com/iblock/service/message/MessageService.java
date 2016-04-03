package com.iblock.service.message;

import com.cloopen.rest.sdk.CCPRestSmsSDK;
import com.google.gson.Gson;
import com.iblock.common.enums.MessageAction;
import com.iblock.common.enums.MessageStatus;
import com.iblock.dao.MessageDao;
import com.iblock.dao.po.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by baidu on 16/2/1.
 */
@Component
public class MessageService {

    @Autowired
    private MessageDao messageDao;

    public List<Message> getUnreadMsg(long userId) {
        return messageDao.selectByUserAndStatus(userId, MessageStatus.UNREAD.getCode());
    }

    public boolean read(long msgId, long userId) {
        Message msg = messageDao.selectByPrimaryKey(msgId);
        if (msg == null || !msg.getTargetId().equals(userId)) {
            return false;
        }
        msg.setStatus((byte) MessageStatus.READ.getCode());
        return messageDao.updateByPrimaryKeySelective(msg) > 0;
    }

    public boolean systemSend(MessageAction action, String processId) {
        return true;
    }

    public boolean send(long src, long target, MessageAction action, Msg s) {
        if (!validateMsg(null, action)) {
            return false;
        }
        Message msg = new Message();
        msg.setStatus((byte) MessageStatus.UNREAD.getCode());
        msg.setAction(action.getCode());
        msg.setDetail(new Gson().toJson(s));
        msg.setSourceId(src);
        msg.setTargetId(target);
        return messageDao.insertSelective(msg) > 0;
    }

    public boolean send(Msg msg) {
        return true;
    }

    private void sendSMS(String code) {
        HashMap<String, Object> result = null;
        CCPRestSmsSDK restAPI = new CCPRestSmsSDK();
        restAPI.init("sandboxapp.cloopen.com", "8883");
        restAPI.setAccount("8a48b55153cb69470153da0f39dc13f5", "b322d8844b444babad188ccaff172f0a");
        restAPI.setAppId("aaf98f8953cadc690153da0f9ca53d56");
        result = restAPI.sendTemplateSMS("13311625852","1" ,new String[]{"code","5"});
        if("000000".equals(result.get("statusCode"))){
            //正常返回输出data包体信息（map）
            HashMap<String,Object> data = (HashMap<String, Object>) result.get("data");
            Set<String> keySet = data.keySet();
            for(String key:keySet){
                Object object = data.get(key);
                System.out.println(key +" = "+object);
            }
        }else{
            //异常返回输出错误码和错误信息
            System.out.println("错误码=" + result.get("statusCode") +" 错误信息= "+result.get("statusMsg"));
        }

    }

    private boolean validateMsg(Map<String, Object> map, MessageAction action) {
//        switch (action.getCode()) {
//            case MessageAction.AUDIT_FAIL
//        }
        // todo
        return true;
    }
}
