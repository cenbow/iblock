package com.iblock.common.enums;

/**
 * Created by baidu on 16/6/13.
 */
public enum HireStatus {

    HIRING(1, "招募中"), ACCEPT(2, "同意"), DENY(0, "否决");

    private int code;
    private String msg;

    HireStatus(int code, String msg) {
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
