package com.iblock.common.enums;

/**
 * Created by baidu on 16/2/1.
 */
public enum MessageAction {

    AUDIT_SUCCESS(1, "审核成功"), AUDIT_FAIL(2, "审核失败"),
    HIRE(3, "招募"), ACCEPT_HIRE(4, "同意招募"), DENY_HIRE(5, "拒绝招募"), MANAGER_RATING(6, "为项目经理打分"), DESIGNER_RATING(7,
            "为设计师打分"), APPLY_JOB(8, "申请职位"), PROJECT_RATING(9, "为项目打分"), PROJECT_RESUBMIT(11, "重提交项目"), ASSIGN_BROKER
            (10, "分配经纪人");

    private int code;
    private String msg;

    MessageAction(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static MessageAction getByCode(int code) {
        for (MessageAction action : MessageAction.values()) {
            if (action.getCode() == code) {
                return action;
            }
        }
        return null;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
