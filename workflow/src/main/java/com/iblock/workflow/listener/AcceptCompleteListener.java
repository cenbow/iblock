package com.iblock.workflow.listener;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.stereotype.Service;

/**
 * Created by baidu on 16/4/3.
 */
@Service("acceptCompleteListener")
public class AcceptCompleteListener implements TaskListener {
    public void notify(DelegateTask delegateTask) {

    }
}
