package com.iblock.workflow.dtos;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: qihong
 * Date: 14-9-4
 * Time: 下午5:45
 * To change this template use File | Settings | File Templates.
 */
@Data
public class TaskDTO implements Serializable {

    /**
     *  任务Id
     */
    private String taskId;
    /**
     *  该任务对应的流程Id
     */
    private String processId;
    /**
     *  任务所有者
     */
    private String owner;
    /**
     *  当前被指派人
     */
    private String assignee;
    /**
     *  任务执行候选人
     */
    private List<String> candidateList;
    /**
     *  活动Id ------ 暂用来判断任务类型并给予指定操作种类
     */
    private String activityId;
    /**
     *  任务名称
     */
    private String taskName;
    /**
     *  备注
     */
    private String memo;
    /**
     *  任务截止日期
     */
    private Date dueDate;
    /**
     *  任务开始日期
     */
    private Date startDate;

}
