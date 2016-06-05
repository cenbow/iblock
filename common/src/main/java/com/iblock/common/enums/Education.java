package com.iblock.common.enums;

/**
 * Created by baidu on 16/2/2.
 */
public enum Education {

    UNDERGRADUATE(1, "本科"), MASTER(2, "硕士"), PHD(3, "博士"), OTHER(4, "其他");

    private int code;
    private String msg;

    Education(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static Education getByCode(int code) {
        for (Education e : Education.values()) {
            if (e.getCode() == code) {
                return e;
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
