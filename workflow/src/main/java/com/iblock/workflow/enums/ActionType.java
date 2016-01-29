package com.iblock.workflow.enums;

/**
 * Created by qihong on 15/12/20.
 */
public enum ActionType {
    DEFAULT(0, "默认错误"),

    PASS(1, "通过"),
    REJECT(2, "驳回"),
    RESUBMIT(3,"重新提交"),
    ROLLBACK(4, "终止"),
    SUBMIT(5, "提交"),
    PAYMENT(6, "支付"),
    END(7,"流程结束"),
    CANCEL(8, "撤销")
    ;

    int code;
    String message;

    ActionType(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static ActionType getByCode(int code) {
        for (ActionType actionType : ActionType.values()) {
            if (actionType.getCode() == code) {
                return actionType;
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
