package com.iblock.common.enums;

/**
 * Created by baidu on 16/2/1.
 */
public enum MessageAction {

    SUBMIT_SUCCESS(11, "提交项目成功"), AUDIT_FAIL(12, "审核失败"),
    ADD_FRIEND(21, "申请添加好友"), FRIEND_AGREE(22, "同意添加好友"), FRIEND_DENY(23, "拒绝添加好友");

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
