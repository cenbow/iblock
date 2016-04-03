package com.iblock.workflow.listener;

import com.iblock.dao.po.SubProcess;
import com.iblock.service.project.SubProcessService;
import com.iblock.workflow.vars.ProcessVars;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.delegate.TaskListener;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by baidu on 16/3/1.
 */
@Service("subProcessListener")
public class SubProcessListener implements ExecutionListener {

    @Autowired
    private SubProcessService subProcessService;

    public void notify(DelegateExecution execution) throws Exception {
        String instId = execution.getProcessInstanceId();
        List<SubProcess> processList = subProcessService.findByInstId(instId);
        if (CollectionUtils.isNotEmpty(processList)) {
            execution.setVariable(ProcessVars.PROCESS_NUM, CollectionUtils.size(processList));
        } else {
            throw new Exception("no sub process to run");
        }
    }
}
