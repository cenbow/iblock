package com.iblock.workflow.listener;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

import java.util.Arrays;

/**
 * Created by qihong on 16/1/28.
 */
public class CandidateListener implements TaskListener {

    public void notify(DelegateTask delegateTask) {
        delegateTask.addCandidateUsers(Arrays.asList("1", "2"));
    }
}
