package com.iblock.common.exception;

/**
 * Created by baidu on 16/4/3.
 */
public class InvalidRequestException extends Exception {

    public InvalidRequestException() {
        super("无效请求!");
    }

    public InvalidRequestException(String msg) {
        super(msg);
    }
}
