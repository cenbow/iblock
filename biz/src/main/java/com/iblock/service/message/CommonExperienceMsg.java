package com.iblock.service.message;

import lombok.Data;

/**
 * Created by baidu on 16/2/14.
 */
@Data
public class CommonExperienceMsg extends Msg {

    private String messageid;
    private String desc;
    private String startDate;
    private String endDate;
    private String destuserid;
    private String sourceuserid;
}
