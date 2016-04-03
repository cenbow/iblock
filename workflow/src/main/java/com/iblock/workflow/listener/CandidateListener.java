package com.iblock.workflow.listener;

import com.iblock.service.bo.TaskPredicateBo;
import com.iblock.service.user.TaskPermissionService;
import lombok.extern.log4j.Log4j;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by qihong on 16/1/28.
 */
@Log4j
@Service("candidateListener")
public class CandidateListener implements TaskListener {

    @Autowired
    private TaskPermissionService taskPermissionService;

    public void notify(DelegateTask delegateTask) {
        TaskPredicateBo bo = new TaskPredicateBo();
        bo.setTaskDefinitionKey(delegateTask.getTaskDefinitionKey());
        bo.setProcessId(delegateTask.getProcessInstanceId());
        List<Long> userList = taskPermissionService.getCandidateUserList(bo);
        if (CollectionUtils.isEmpty(userList)) {
            log.error(String.format("fail to get candidate users for task, task key is [%s] and process id is [%s]",
                    delegateTask.getTaskDefinitionKey(), delegateTask.getProcessInstanceId()));
        }
        delegateTask.addCandidateUsers(buildCandidateList(userList));
    }

    private List<String> buildCandidateList(List<Long> userList) {
        List<String> list = new ArrayList<String>();
        for (long user : userList) {
            list.add(String.valueOf(user));
        }
        return list;
    }
}
