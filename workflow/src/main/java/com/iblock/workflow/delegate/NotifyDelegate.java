package com.iblock.workflow.delegate;

import com.iblock.common.enums.MessageAction;
import com.iblock.service.message.MessageService;
import com.iblock.workflow.vars.ProcessVars;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by baidu on 16/2/1.
 */
@Service("notifyDelegate")
public class NotifyDelegate implements JavaDelegate {

    @Autowired
    private MessageService messageService;

    public void execute(DelegateExecution execution) throws Exception {
        messageService.systemSend(MessageAction.getByCode((Integer) execution.getVariable(ProcessVars.MESSAGE)),
                execution.getProcessInstanceId());
    }
}
