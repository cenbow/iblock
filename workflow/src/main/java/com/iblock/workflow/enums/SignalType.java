package com.iblock.workflow.enums;

/**
 * Created by qihong on 15/12/21.
 */
public enum SignalType {
    DEFAULT(0, "默认错误"),

    PAY_SUCCESS(1, "支付成功"),
    PAY_FAILED(2, "支付失败"),
    ;

    int code;
    String message;

    SignalType(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static SignalType getByCode(int code) {
        for (SignalType signalType : SignalType.values()) {
            if (signalType.getCode() == code) {
                return signalType;
            }
        }
        return DEFAULT;
    }

    public int getCode() {
        return code;
    }
    public void setCode(int code) {
        this.code = code;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}

