package com.iblock.workflow.dtos;

import lombok.Data;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: qihong
 * Date: 14-9-10
 * Time: 下午12:38
 * To change this template use File | Settings | File Templates.
 */
@Data
public class UserTaskInfoDTO implements Serializable {

    /**
     *  任务执行的顺序
     */
    private int sequence;
    /**
     *  任务名称
     */
    private String taskName;
    /**
     *  任务被指派人
     */
    private String assignee;
    /**
     *  任务状态 1 已结束 2 正在进行中 3 未开始
     */
    private int taskStatus;

    public UserTaskInfoDTO(){

    }

    public UserTaskInfoDTO(int sequence, String taskName, String assignee, int taskStatus){
        this.sequence = sequence;
        this.taskName = taskName;
        this.assignee = assignee;
        this.taskStatus = taskStatus;
    }

    public UserTaskInfoDTO(int sequence, String taskName, int taskStatus){
        this.sequence = sequence;
        this.taskName = taskName;
        this.taskStatus = taskStatus;
    }

}
