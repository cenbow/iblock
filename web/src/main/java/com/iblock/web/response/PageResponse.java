package com.iblock.web.response;

import com.iblock.web.enums.ResponseStatus;
import com.iblock.web.info.PageInfo;
import lombok.Data;

/**
 * Created by qihong on 16/1/27.
 */
@Data
public class PageResponse<T> {

    protected int status;
    protected String msg;
    private PageInfo<T> data;

    public PageResponse(ResponseStatus status) {
        this.status = status.getCode();
        this.msg = status.getValue();
    }

    public PageResponse(PageInfo<T> data) {
        this.data = data;
        this.status = ResponseStatus.SUCCESS.getCode();
        this.msg = "";
    }

}
