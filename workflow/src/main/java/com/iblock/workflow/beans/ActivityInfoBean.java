package com.iblock.workflow.beans;

import lombok.Data;

import java.util.List;

/**
 * Created by baidu on 15/12/21.
 */
@Data
public class ActivityInfoBean {

    private String activityName;
    private int sequence;
    private List<String> activityIdList;
    private boolean isTask;
    private boolean mandatory;

    public ActivityInfoBean() {
    }

    public ActivityInfoBean(String activityName, int sequence, List<String> activityIdList, boolean isTask, boolean mandatory) {
        this.activityName = activityName;
        this.sequence = sequence;
        this.activityIdList = activityIdList;
        this.isTask = isTask;
        this.mandatory = mandatory;
    }
}
