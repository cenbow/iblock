package com.iblock.workflow.enums;

/**
 * Created by baidu on 15/12/20.
 */
public enum TaskStatus {
    DEFAULT(0, "默认错误"),

    OVER(1, "已结束"),
    RUNNING(2, "正在进行中"),
    PENDING(3,"待运行"),
    ;

    int code;
    String message;

    TaskStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static TaskStatus getByCode(int code) {
        for (TaskStatus taskStatus : TaskStatus.values()) {
            if (taskStatus.getCode() == code) {
                return taskStatus;
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

