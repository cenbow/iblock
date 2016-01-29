package com.iblock.workflow.enums;

/**
 * Created by baidu on 15/12/20.
 */
public enum ProcessError {
    DEFAULT(0, "默认错误"),

    SUCCESS(1, "成功"),
    SYSTEM_ERROR(-1, "系统异常"),

    INPUT_EMPTY(1001, "输入为空"),
    TOKEN_MISSING(1002,"缺少必要的token信息"),
    TASK_MISSING(1003, "缺少任务信息"),
    USER_MISSING(1004, "缺少操作用户信息"),
    ACTION_EMPTY(1005, "操作信息为空"),
    ACTION_INVALID(1006, "非法的操作行为"),
    ACTION_MISSING(1007, "缺少操作信息"),
    USER_ID_MISSING(1008, "发起人信息缺失"),
    PROCESS_INFO_MISSING(1009, "流程信息缺失"),
    SIGNAL_INVALID(1010, "非法的信号"),
    PROCESS_NOT_RUNNING(1011, "流程未运行"),
    ACTIVE_TASK_NOT_FOUND(2001, "不存在对应的活动任务"),
    TASK_ASSIGNEE_NOT_EQUAL_ACTION_USER(2002, "任务对应的执行人与操作人不一致"),
    ACTION_USER_NOT_IN_CANDIDATE_GROUP(2003, "任务操作人不在候选执行人列表中"),


    ;

    int code;
    String message;

    ProcessError(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static ProcessError getByCode(int code) {
        for (ProcessError error : ProcessError.values()) {
            if (error.getCode() == code) {
                return error;
            }
        }
        return DEFAULT;
    }

    public int getCode() {
        return code;
    }
    public void setCode(int code) {
        this.code = code;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}
