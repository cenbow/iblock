package com.iblock.common.enums;

/**
 * Created by baidu on 16/4/3.
 */
public enum ProjectStatus {
    DRAFT(1, "草稿"), PUBLISH(2, "发布"), FINISH(3, "完成"), DELETE(4, "删除");

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
