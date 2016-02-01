package com.iblock.common.enums;

/**
 * Created by baidu on 16/2/1.
 */
public enum CommonStatus {
    NORMAL(1, "正常"), DELETE(0, "删除");

    private int code;
    private String msg;

    CommonStatus(int code, String msg) {
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
