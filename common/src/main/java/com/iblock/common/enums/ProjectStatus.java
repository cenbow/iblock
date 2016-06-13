package com.iblock.common.enums;

/**
 * Created by baidu on 16/4/3.
 */
public enum ProjectStatus {
    AUDIT_DENY(0, "审核未通过"), AUDIT(1, "审核中"), RECRUITING(2, "招募中"), READY(3, "已就绪"), ONGOING(4, "进行中"), FINISH(5,
            "已完成"), TERMINATION(6, "已终止");

    private int code;
    private String msg;

    ProjectStatus(int code, String msg) {
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
