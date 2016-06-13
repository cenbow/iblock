package com.iblock.workflow.delegate;

import com.iblock.service.project.ProjectService;
import com.iblock.workflow.vars.ProcessVars;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by baidu on 16/2/1.
 */
@Service("submitProjectDelegate")
public class SubmitProjectDelegate implements JavaDelegate {

    @Autowired
    private ProjectService projectService;

    public void execute(DelegateExecution execution) throws Exception {
        Map<String, Object> varMap = execution.getVariables();
        if(!varMap.containsKey(ProcessVars.PROJECT_KEY)){
            throw new Exception("project info missing!");
        }
        Long projectId = (Long) varMap.get(ProcessVars.PROJECT_KEY);
        Long manager = (Long) varMap.get(ProcessVars.MANAGER);
        String processId = execution.getProcessInstanceId();
    }
}
