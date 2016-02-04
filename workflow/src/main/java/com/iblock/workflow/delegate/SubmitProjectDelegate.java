package com.iblock.workflow.delegate;

import com.iblock.service.project.ProjectService;
import com.iblock.service.bo.ProjectBo;
import com.iblock.workflow.vars.ProcessVars;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * Created by baidu on 16/2/1.
 */
public class SubmitProjectDelegate implements JavaDelegate {

    @Autowired
    private ProjectService projectService;

    public void execute(DelegateExecution execution) throws Exception {
        Map<String, Object> varMap = execution.getVariables();
        if(!varMap.containsKey(ProcessVars.PROJECT_KEY)){
            throw new Exception("project info missing!");
        }
        ProjectBo project = (ProjectBo) varMap.get(ProcessVars.PROJECT_KEY);
        String processId = execution.getProcessInstanceId();
        boolean result = projectService.submitProject(project, processId);
        if(!result){
            throw new Exception("Save expense error!");
        }
    }
}
