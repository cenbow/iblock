package com.iblock.common.exception;

/**
 * Created by baidu on 16/6/13.
 */
public class InnerLogicException extends Exception {

    public InnerLogicException() {
        super("内部逻辑错误!");
    }

    public InnerLogicException(String msg) {
        super(msg);
    }
}
