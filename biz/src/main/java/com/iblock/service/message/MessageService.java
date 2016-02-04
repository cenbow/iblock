package com.iblock.service.message;

import com.iblock.common.enums.MessageAction;
import com.iblock.common.enums.MessageStatus;
import com.iblock.dao.MessageDao;
import com.iblock.dao.po.Message;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * Created by baidu on 16/2/1.
 */
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

    public boolean send(long src, long target, MessageAction action, String s) {
        if (!validateMsg(null, action)) {
            return false;
        }
        Message msg = new Message();
        msg.setStatus((byte) MessageStatus.UNREAD.getCode());
        msg.setAction(action.getCode());
        msg.setDetail(s);
        msg.setSourceId(src);
        msg.setTargetId(target);
        return messageDao.insertSelective(msg) > 0;
    }

    private boolean validateMsg(Map<String, Object> map, MessageAction action) {
//        switch (action.getCode()) {
//            case MessageAction.AUDIT_FAIL
//        }
        // todo
        return true;
    }
}
