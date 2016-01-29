package com.iblock.workflow.api;


import com.iblock.workflow.dtos.BaseResultDTO;
import com.iblock.workflow.dtos.ProcessResultDTO;
import com.iblock.workflow.dtos.ReceiveSignalDTO;
import com.iblock.workflow.dtos.SignResultDTO;
import com.iblock.workflow.dtos.TaskActionDTO;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: qihong
 * Date: 14-9-4
 * Time: 下午5:21
 * To change this template use File | Settings | File Templates.
 */
public interface ProcessManageService {

    ProcessResultDTO startProcess(Map<String, Object> varMap);

    BaseResultDTO operateTask(TaskActionDTO taskActionDTO);

    BaseResultDTO operateReceiveTask(ReceiveSignalDTO receiveSignalDTO);

    void updateVariable(String procId, String name, Object value);

    void autoAssignee(String taskId);
}
