package com.iblock.common.enums;

/**
 * Created by baidu on 16/2/14.
 */
public enum WorkflowType {

    PROJECT(1, "项目流程"), SUB_PROCESS(2, "子流程");

    private int code;
    private String msg;

    WorkflowType(int code, String msg) {
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
