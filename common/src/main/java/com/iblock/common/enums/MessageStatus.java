package com.iblock.common.enums;

/**
 * Created by baidu on 16/2/1.
 */
public enum MessageStatus {
    UNREAD(1, "未读"), READ(2, "已读"), DELETE(0, "删除");

    private int code;
    private String msg;

    MessageStatus(int code, String msg) {
        this.code = code;
        this.msg = msg;
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
