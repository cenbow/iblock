package com.iblock.common.enums;

/**
 * Created by baidu on 16/7/15.
 */
public enum VerifyType {

    SIGNUP(1, "signup"), CHANGE_PASSWORD(2, "changepasswd");

    private int code;
    private String msg;

    VerifyType(int code, String msg) {
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
