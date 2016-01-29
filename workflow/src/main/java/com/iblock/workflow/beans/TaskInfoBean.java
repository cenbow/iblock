package com.iblock.workflow.beans;

import com.iblock.workflow.enums.ProcessError;
import lombok.Data;
import org.activiti.engine.task.Task;

/**
 * Created by baidu on 15/12/21.
 */
@Data
public class TaskInfoBean {
    private Task task;
    private ProcessError processError;

    public TaskInfoBean(){}

    public TaskInfoBean(Task task, ProcessError processError){
        this.task = task;
        this.processError = processError;
    }

    public TaskInfoBean(ProcessError processError){
        this.processError = processError;
    }
}
