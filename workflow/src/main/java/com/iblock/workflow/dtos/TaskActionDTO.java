package com.iblock.workflow.dtos;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: qihong
 * Date: 14-9-4
 * Time: 下午5:32
 * To change this template use File | Settings | File Templates.
 */
@Data
public class TaskActionDTO implements Serializable {

    /**
     *  操作用户Id
     */
    private String userId;
    /**
     *  任务Id
     */
    private String taskId;
    /**
     *  操作内容
     */
    private Map<String, Object> actionMap;

    @Override
    public String toString() {
        return "{" +
                "userId=" + userId +
                ",taskId=" + taskId +
                ",actionMap=" + actionMap.toString() +
                "}";
    }
}
