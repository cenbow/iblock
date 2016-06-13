package com.iblock.web.request.message;

import lombok.Data;

/**
 * Created by baidu on 16/6/13.
 */
@Data
public class MessagesRequest {

    private int pageNo;
    private int pageSize;
    private boolean unprocessed;
}
