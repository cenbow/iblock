package com.iblock.web.request.project;

import lombok.Data;

/**
 * Created by baidu on 16/6/13.
 */
@Data
public class AcceptHiringRequest {
    private long hireid;
    private boolean accept;
    private long msgId;
}
