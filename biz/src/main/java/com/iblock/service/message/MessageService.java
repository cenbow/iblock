package com.iblock.service.message;

import com.cloopen.rest.sdk.CCPRestSmsSDK;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.iblock.common.bean.Page;
import com.iblock.common.enums.CommonStatus;
import com.iblock.common.enums.MessageAction;
import com.iblock.common.enums.MessageStatus;
import com.iblock.common.exception.InnerLogicException;
import com.iblock.common.utils.JsonUtils;
import com.iblock.dao.MessageDao;
import com.iblock.dao.UserDao;
import com.iblock.dao.po.Message;
import com.iblock.dao.po.Project;
import com.iblock.dao.po.User;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
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
    @Autowired
    private UserDao userDao;

    public Page<Message> getMsgs(long userId, int pageNo, int pageSize, boolean processed, Integer role) {
        syncBroadCast(userId, role);
        List<Integer> status = new ArrayList<Integer>();
        if (processed) {
            status.add(MessageStatus.FINISH.getCode());
        } else {
            status.add(MessageStatus.READ.getCode());
            status.add(MessageStatus.UNREAD.getCode());
        }
        List<Message> messages = messageDao.selectByUserAndStatus(userId, status, (pageNo - 1) * pageSize, pageSize);
        int count = messageDao.countByUserAndStatus(userId, status);
        return new Page<Message>(messages, pageNo, pageSize, count, "sendTime", "desc");
    }

    public boolean read(long msgId, long userId) {
        Message msg = messageDao.selectByPrimaryKey(msgId);
        if (msg == null || !msg.getTargetId().equals(userId)) {
            return false;
        }
        msg.setStatus((byte) MessageStatus.READ.getCode());
        if (msg.getStatus().intValue() == MessageStatus.UNREAD.getCode()) {
            msg.setStatus((byte) MessageStatus.READ.getCode());
        }
        return messageDao.updateByPrimaryKeySelective(msg) > 0;
    }

    public boolean finish(long msgId, long userId) {
        Message msg = messageDao.selectByPrimaryKey(msgId);
        if (msg == null || !msg.getTargetId().equals(userId)) {
            return false;
        }
        msg.setStatus((byte) MessageStatus.FINISH.getCode());
        return messageDao.updateByPrimaryKeySelective(msg) > 0;
    }

    public boolean send(Long sourceId, Long targetId, MessageAction action, Long managerId, Long agentId, Long
            designerId, Project project, Map<String, String> params) throws InnerLogicException, IOException {
        User manager = null;
        User agent = null;
        User designer = null;
        if (managerId != null) {
            manager = userDao.selectByPrimaryKey(managerId);
            if (manager == null || manager.getStatus().intValue() != CommonStatus.NORMAL.getCode()) {
                throw new InnerLogicException("invalid manager id");
            }
        }
        if (agentId != null) {
            agent = userDao.selectByPrimaryKey(agentId);
            if (agent == null || agent.getStatus().intValue() != CommonStatus.NORMAL.getCode()) {
                throw new InnerLogicException("invalid agent id");
            }
        }
        if (designerId != null) {
            designer = userDao.selectByPrimaryKey(designerId);
            if (designer == null || designer.getStatus().intValue() != CommonStatus.NORMAL.getCode()) {
                throw new InnerLogicException("invalid designer id");
            }
        }
        Message msg = buildMessage(sourceId, targetId, action, manager, agent, designer, project, params);
        return messageDao.insertSelective(msg) > 0;
    }

    @Transactional
    public boolean syncBroadCast(Long userId, Integer role) {
        User user = userDao.selectByPrimaryKey(userId);
        List<Message> messages = messageDao.selectUnloadBroadcastMsg(user.getLastMsgTime(), role);
        user.setLastMsgTime(new Date());
        userDao.updateByPrimaryKey(user);
        if (CollectionUtils.isNotEmpty(messages)) {
            for (Message message : messages) {
                message.setTargetId(userId);
                message.setId(null);
                messageDao.insertSelective(message);
            }
        }
        return true;
    }

    public boolean broadCast(String content, Integer role) {
        Message message = new Message();
        message.setTargetId(-1L);
        message.setSourceId(-1L);
        message.setAction(-1);
        message.setDetail(content);
        if (role != null) {
            message.setRole(role.byteValue());
        }
        message.setAddTime(new Date());
        message.setStatus((byte) MessageStatus.UNREAD.getCode());
        message.setType((byte) 0);
        return messageDao.insertSelective(message) > 0;
    }

    private Message buildMessage(Long sourceId, Long targetId, MessageAction action, User manager, User agent, User
            designer, Project project, Map<String, String> params) throws InnerLogicException, IOException {
        Message message = new Message();
        Msg msg = MsgContentUtil.getInstance().getMsg(action.getCode());
        if (msg == null) {
            throw new InnerLogicException("invalid msg action " + action.getCode());
        }
        message.setSourceId(sourceId);
        message.setTargetId(targetId);
        message.setAction(action.getCode());
        message.setStatus((byte) MessageStatus.UNREAD.getCode());
        message.setAddTime(new Date());
        message.setService(msg.getService());
        message.setType((byte) msg.getType());
        String content = msg.getContent();
        if (project != null) {
            content = content.replaceAll("\\{project\\}", "{" + project.getName() + "|project?id=" + project.getId() +
                    "}");
        }
        if (manager != null) {
            content = content.replaceAll("\\{manager\\}", "{" + manager.getUserName() + "|user?id=" + manager.getId() + "}");
        }
        if (agent != null) {
            content = content.replaceAll("\\{agent\\}", "{" + agent.getUserName() + "|user?id=" + agent.getId() + "}");
        }
        if (designer != null) {
            content = content.replaceAll("\\{designer\\}", "{" + designer.getUserName() + "|user?id=" + designer.getId() + "}");
        }
        message.setDetail(content);
        if (MapUtils.isNotEmpty(msg.getInputType())) {
            message.setInputType(JsonUtils.toStr(msg.getInputType()));
        }
        if (msg.getParams() != null) {
            JsonObject json = new JsonObject();
            for (String s : msg.getParams()) {
                if (!params.containsKey(s)) {
                    throw new InnerLogicException("cannot find param " + s);
                }
                json.addProperty(s, params.get(s));
            }
            message.setParams(json.toString());
        }
        return message;
    }


}
