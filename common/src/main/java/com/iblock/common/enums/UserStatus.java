package com.iblock.common.enums;

/**
 * Created by baidu on 16/6/26.
 */
public enum UserStatus {

    NORMAL(1, "正常"), DELETE(0, "删除"), FREEZE(2, "冻结");

    private int code;
    private String msg;

    UserStatus(int code, String msg) {
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
