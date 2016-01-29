package com.iblock.workflow.dtos;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: qihong
 * Date: 14-9-10
 * Time: 下午12:39
 * To change this template use File | Settings | File Templates.
 */
@Data
public class ActionLogDTO implements Serializable {

    /**
     *  操作时间
     */
    private Date actionTime;
    /**
     *  操作人Id
     */
    private String userId;
    /**
     *  操作行为：通过/驳回/提交/撤销/。。。
     */
    private int action;
    /**
     *  信号：支付成功/支付失败/。。。
     */
    private int signal;
    /**
     *  备注
     */
    private String memo;
    /**
     *  任务描述
     */
    private String taskDescription;
}
