package com.iblock.web.enums;

/**
 * Created by baidu on 16/1/27.
 */

public enum ResponseStatus {

    SUCCESS(0, "成功执行"),
    NOT_FOUND(404, "未找到资源"),
    SYSTEM_ERROR(500, "由于系统繁忙，很抱歉我们暂时无法为您提供服务，请您耐心等待一会，稍后重试。"),
    EXTERNAL_INTERFACE_ERROR(501, "外部接口错误"),
    NO_AUTH(403, "没有权限"),
    PARAM_ERROR(415, "参数错误或无参数");

    private int code;
    private String value;

    ResponseStatus(int code, String value) {
        this.code = code;
        this.value = value;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
