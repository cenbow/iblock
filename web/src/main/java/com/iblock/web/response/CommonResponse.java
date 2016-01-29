package com.iblock.web.response;

import com.iblock.web.enums.ResponseStatus;
import lombok.Data;

/**
 * Created by qihong on 16/1/27.
 */
@Data
public class CommonResponse<T> {
    private T data;
    private int status;
    private String msg;

    public CommonResponse(T result) {
        this(result, ResponseStatus.SUCCESS);
    }

    public CommonResponse(ResponseStatus responseStatus) {
        this(null, responseStatus);
    }

    public CommonResponse(ResponseStatus responseStatus, String msg) {
        this(null, responseStatus.getCode(), msg);
    }

    public CommonResponse(T result, ResponseStatus responseStatus) {
        this.status = responseStatus.getCode();
        this.msg = responseStatus.getValue();
        this.data = result;
    }

    public CommonResponse(T result, int status, String msg) {
        this.status = status;
        this.msg = msg;
        this.data = result;
    }

    public CommonResponse(int status, String msg) {
        this(null, status, msg);
    }
}
