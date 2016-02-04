package com.iblock.workflow.biz;

import com.iblock.workflow.api.ProcessManageService;
import com.iblock.workflow.api.ProcessQueryService;
import com.iblock.workflow.beans.TaskInfoBean;
import com.iblock.workflow.dtos.BaseResultDTO;
import com.iblock.workflow.dtos.ProcessResultDTO;
import com.iblock.workflow.dtos.ReceiveSignalDTO;
import com.iblock.workflow.dtos.TaskActionDTO;
import com.iblock.workflow.enums.ActionType;
import com.iblock.workflow.enums.ProcessError;
import com.iblock.workflow.enums.SignalType;
import com.iblock.workflow.vars.ProcessVars;
import lombok.extern.log4j.Log4j;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.persistence.entity.HistoricVariableInstanceEntity;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: qihong
 * Date: 14-9-4
 * Time: 下午5:26
 * To change this template use File | Settings | File Templates.
 */
@Log4j
public class ProcessManageServiceObject implements ProcessManageService {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private IdentityService identityService;

    @Autowired
    private ProcessQueryService processQueryService;

    @Autowired
    private HistoryService historyService;


    public ProcessResultDTO startProcess(Map<String, Object> varMap) {
        ProcessError processError = validateStartProcessInput(varMap);
        if (processError != ProcessError.SUCCESS) {
            return new ProcessResultDTO(processError);
        }
        Map<String, Object> dataMap = new HashMap<String, Object>();
        if (varMap.containsKey(ProcessVars.DATA)) {
            dataMap = (Map<String, Object>) varMap.get(ProcessVars.DATA);
        }
        try {
            identityService.setAuthenticatedUserId((String) varMap.get(ProcessVars.USER_ID));
            ProcessInstance instance = runtimeService.startProcessInstanceByKey("iblock", dataMap);
            return new ProcessResultDTO(ProcessError.SUCCESS, instance.getId());
        } catch (Exception e) {
            log.error(String.format("severity=[1], failed to start process. varMap=[%s]", varMap.toString()), e);
            return new ProcessResultDTO(ProcessError.SYSTEM_ERROR);
        }
    }

    public BaseResultDTO operateTask(TaskActionDTO taskActionDTO) {
        ProcessError processError = validateOperateTaskInput(taskActionDTO);
        if (processError != ProcessError.SUCCESS) {
            log.error(String.format("severity=[2], operateTask input error. taskActionDTO=[%s]", ToStringBuilder.reflectionToString(taskActionDTO)));
            return new BaseResultDTO(processError);
        }
        TaskInfoBean taskInfoBean = claimTask(taskActionDTO);
        if (taskInfoBean.getProcessError() != ProcessError.SUCCESS) {
            log.error(String.format("severity=[1], failed to claim task. taskActionDTO=[%s]", ToStringBuilder.reflectionToString(taskActionDTO)));
            return new BaseResultDTO(taskInfoBean.getProcessError());
        }
        try {
            String executionId = getExecutionId(taskInfoBean);
            Map<String, Object> dataMap = buildDataMap(taskActionDTO, executionId);
            boolean success = handleTask(taskInfoBean.getTask(), taskActionDTO.getUserId(), dataMap, buildTaskLocalVarMap(taskActionDTO));
            if (success && !StringUtils.isBlank(taskInfoBean.getTask().getParentTaskId()) && (Integer) taskActionDTO.getActionMap().get(ProcessVars.ACTION) == ActionType.PASS.getCode()) {
                runtimeService.setVariable(executionId, ProcessVars.PASS_ID, dataMap.get(ProcessVars.PASS_ID));
            }
            if(success){
                return new BaseResultDTO(ProcessError.SUCCESS);
            } else {
                log.error(String.format("severity=[2], failed to operate task. taskActionDTO=[%s]", ToStringBuilder.reflectionToString(taskActionDTO)));
                return new BaseResultDTO(ProcessError.SYSTEM_ERROR);
            }
        } catch (Exception e) {
            log.error(String.format("severity=[1], failed to operate task. taskActionDTO=[%s]", taskActionDTO.toString()), e);
            return new ProcessResultDTO(ProcessError.SYSTEM_ERROR);
        }
    }

    private String getExecutionId(TaskInfoBean taskInfoBean) {
        if (taskInfoBean == null || taskInfoBean.getTask() == null) {
            log.error(String.format("severity=[3], failed to get executionId. taskInfoBean is invalid. taskInfoBean=%s", ToStringBuilder.reflectionToString(taskInfoBean)));
            return null;
        }
        if (!StringUtils.isBlank(taskInfoBean.getTask().getExecutionId())) {
            return taskInfoBean.getTask().getExecutionId();
        }
        if (!StringUtils.isBlank(taskInfoBean.getTask().getParentTaskId())) {
            Task parentTask = taskService.createTaskQuery().taskId(taskInfoBean.getTask().getParentTaskId()).singleResult();
            if (parentTask != null) {
                return parentTask.getExecutionId();
            }
        }
        return null;
    }

    public BaseResultDTO operateReceiveTask(ReceiveSignalDTO receiveSignalDTO) {
        ProcessError processError = validateReceiveSignalDTO(receiveSignalDTO);
        if (processError != ProcessError.SUCCESS) {
            log.error(String.format("severity=[2], operateReceiveTask input error. receiveSignalDTO=[%s]", ToStringBuilder.reflectionToString(receiveSignalDTO)));
            return new BaseResultDTO(processError);
        }
        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put(ProcessVars.SIGNAL, receiveSignalDTO.getSignal());
        if (!StringUtils.isBlank(receiveSignalDTO.getMemo())) {
            dataMap.put(ProcessVars.MEMO, receiveSignalDTO.getMemo());
        }
        try {
            Execution execution = runtimeService.createExecutionQuery().processInstanceId(receiveSignalDTO.getProcessId()).singleResult();
            if (execution == null) {
                log.error(String.format("severity=[2], process not running when operating receive task.  receiveSignalDTO=[%s]", ToStringBuilder.reflectionToString(receiveSignalDTO)));
                return new BaseResultDTO(ProcessError.PROCESS_NOT_RUNNING);
            }
            runtimeService.signal(execution.getId(), dataMap);
            return new BaseResultDTO(ProcessError.SUCCESS);
        } catch (Exception e) {
            log.error(String.format("severity=[1], failed to operate receive task. receiveSignalDTO=[%s]", receiveSignalDTO.toString()), e);
            return new ProcessResultDTO(ProcessError.SYSTEM_ERROR);
        }
    }

    public void updateVariable(String procId, String name, Object value) {
        runtimeService.setVariable(procId, name, value);
    }

    public void autoAssignee(String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).active().singleResult();
        if(null == task) {
            return;
        }
        task.setAssignee("-1");
        taskService.saveTask(task);
    }

    private TaskInfoBean claimTask(TaskActionDTO taskActionDTO) {
        try {
            Task task = taskService.createTaskQuery().taskId(taskActionDTO.getTaskId()).active().singleResult();
            if (task == null) {
                log.error(String.format("severity=[2], the specified task is no longer active. taskActionDTO=[%s]", ToStringBuilder.reflectionToString(taskActionDTO)));
                return new TaskInfoBean(ProcessError.ACTIVE_TASK_NOT_FOUND);
            }
            if (StringUtils.isBlank(task.getAssignee())) {
                Task candidateTask = getCandidateTask(task, taskActionDTO.getUserId());
                if (candidateTask == null && (Integer)taskActionDTO.getActionMap().get("action") != ActionType.CANCEL.getCode()) {
                    log.error(String.format("severity=[2], action user not in candidate group. taskActionDTO=[%s]", ToStringBuilder.reflectionToString(taskActionDTO)));
                    return new TaskInfoBean(ProcessError.ACTION_USER_NOT_IN_CANDIDATE_GROUP);
                } else if((Integer)taskActionDTO.getActionMap().get("action") == ActionType.CANCEL.getCode()) {
                    List<HistoricTaskInstance> taskInstanceList = historyService.createHistoricTaskInstanceQuery().processInstanceId(task.getProcessInstanceId()).finished().orderByHistoricTaskInstanceStartTime().desc().list();
                    HistoricVariableInstanceEntity launcherEntity = (HistoricVariableInstanceEntity) historyService.createHistoricVariableInstanceQuery().processInstanceId(task.getProcessInstanceId()).variableName("launcher").singleResult();
                    if(CollectionUtils.isNotEmpty(taskInstanceList) && !taskInstanceList.get(0).getTaskDefinitionKey().equals(ProcessVars.WAIT_FOR_RESUBMIT) || !launcherEntity.getTextValue().equals(taskActionDTO.getUserId())) {
                        log.error(String.format("severity=[2], action user has not authority to cancel task. taskActionDTO=[%s]", ToStringBuilder.reflectionToString(taskActionDTO)));
                        return new TaskInfoBean(ProcessError.ACTION_INVALID);
                    }
                }
                taskService.claim(task.getId(), taskActionDTO.getUserId());
            } else if (!task.getAssignee().equals(taskActionDTO.getUserId())) {
                log.error(String.format("severity=[2], action user not equal task assignee. taskActionDTO=[%s]&taskAssignee=[%s]", ToStringBuilder.reflectionToString(taskActionDTO), task.getAssignee()));
                return new TaskInfoBean(ProcessError.TASK_ASSIGNEE_NOT_EQUAL_ACTION_USER);
            }
            return new TaskInfoBean(task, ProcessError.SUCCESS);
        } catch (Exception e) {
            log.error(String.format("severity=[1], failed to claim task. taskActionDTO=[%s]", taskActionDTO.toString()), e);
            return new TaskInfoBean(ProcessError.SYSTEM_ERROR);
        }
    }

    private Task getCandidateTask(Task task, String candidateUserId) {
        List<Task> candidateTaskList = taskService.createTaskQuery().taskCandidateUser(candidateUserId).processInstanceId(task.getProcessInstanceId()).active().list();
        for (Task candidateTask : candidateTaskList) {
            if (candidateTask.getId().equals(task.getId())) {
                return candidateTask;
            }
        }
        return null;
    }

    private boolean handleTask(Task task, String actionUserId, Map<String, Object> varMap, Map<String, Object> taskLocalVarMap) {
        try {
            taskService.setVariablesLocal(task.getId(), taskLocalVarMap);
            if (task.getOwner() == null || task.getOwner().equals(actionUserId)) {
                taskService.complete(task.getId(), varMap);
            } else {
                taskService.resolveTask(task.getId(), varMap);
            }
            return true;
        } catch (Exception e) {
            log.error(String.format("severity=[1], handle task error! task id is [%s], actionUserId is [%s], varMap is [%s], taskLocalVarMap is [%s]", task.getId(), actionUserId, varMap.toString(), taskLocalVarMap.toString()), e);
            return false;
        }
    }

    private ProcessError validateOperateTaskInput(TaskActionDTO taskActionDTO) {
        return validateTaskActionDTO(taskActionDTO);
    }

    private ProcessError validateTaskActionDTO(TaskActionDTO taskActionDTO) {
        if (taskActionDTO == null) {
            return ProcessError.INPUT_EMPTY;
        }
        if (StringUtils.isBlank(taskActionDTO.getTaskId())) {
            return ProcessError.TASK_MISSING;
        }
        if (StringUtils.isBlank(taskActionDTO.getUserId())) {
            return ProcessError.USER_MISSING;
        }
        Map<String, Object> actionMap = taskActionDTO.getActionMap();
        if (actionMap == null || actionMap.isEmpty()) {
            return ProcessError.ACTION_EMPTY;
        }
        if (!actionMap.containsKey(ProcessVars.ACTION)) {
            return ProcessError.ACTION_MISSING;
        }
        ActionType actionType = ActionType.getByCode((Integer) actionMap.get(ProcessVars.ACTION));
        if (actionType == ActionType.DEFAULT) {
            return ProcessError.ACTION_INVALID;
        }
        return ProcessError.SUCCESS;
    }

    private Map<String, Object> buildTaskLocalVarMap(TaskActionDTO taskActionDTO) {
        Map<String, Object> actionMap = taskActionDTO.getActionMap();
        Map<String, Object> dataMap = new HashMap<String, Object>();
        int action = (Integer) actionMap.get(ProcessVars.ACTION);
        dataMap.put(ProcessVars.MEMO, ActionType.getByCode(action).getMessage());
        String memo = null;
        if (actionMap.containsKey(ProcessVars.MEMO)) {
            memo = (String) actionMap.get(ProcessVars.MEMO);
        }
        if (!StringUtils.isBlank(memo)) {
            dataMap.put(ProcessVars.MEMO, memo);
        }
        dataMap.put(ProcessVars.ACTION, actionMap.get(ProcessVars.ACTION));
        return dataMap;
    }

    private Map<String, Object> buildDataMap(TaskActionDTO taskActionDTO, String executionId) {
        Map<String, Object> map = buildDataMap(taskActionDTO);
        if (!StringUtils.isBlank(executionId)) {
            int action = (Integer) taskActionDTO.getActionMap().get(ProcessVars.ACTION);
            if(action == ActionType.PASS.getCode()){
                Object passId = runtimeService.getVariable(executionId, ProcessVars.PASS_ID);
                if (passId == null) {
                    map.put(ProcessVars.PASS_ID, taskActionDTO.getUserId());
                } else {
                    String passIdStr = passId +  "," + taskActionDTO.getUserId();
                    map.put(ProcessVars.PASS_ID, passIdStr);
                }
            } else if (action == ActionType.REJECT.getCode()){
                map.put(ProcessVars.PASS_ID, "");
            }
        }
        return map;
    }
    private Map<String, Object> buildDataMap(TaskActionDTO taskActionDTO) {
        Map<String, Object> dataMap = buildTaskLocalVarMap(taskActionDTO);
        Map<String, Object> actionMap = taskActionDTO.getActionMap();
        if (actionMap.containsKey(ProcessVars.DATA)) {
            Map<String, Object> tmpDataMap = (Map<String, Object>) actionMap.get(ProcessVars.DATA);
            for (String key : tmpDataMap.keySet()) {
                if (!dataMap.containsKey(key)) {
                    dataMap.put(key, tmpDataMap.get(key));
                }
            }
        }
        return dataMap;
    }

    private ProcessError validateReceiveSignalDTO(ReceiveSignalDTO receiveSignalDTO) {
        if (receiveSignalDTO == null) {
            return ProcessError.INPUT_EMPTY;
        }
        if (StringUtils.isBlank(receiveSignalDTO.getProcessId())) {
            return ProcessError.PROCESS_INFO_MISSING;
        }
        SignalType signalType = SignalType.getByCode(receiveSignalDTO.getSignal());
        if (signalType == SignalType.DEFAULT) {
            return ProcessError.SIGNAL_INVALID;
        }
        return ProcessError.SUCCESS;
    }

    private ProcessError validateStartProcessInput(Map<String, Object> varMap) {
        if (varMap == null || varMap.isEmpty()) {
            return ProcessError.INPUT_EMPTY;
        }
        if (!varMap.containsKey(ProcessVars.USER_ID)) {
            return ProcessError.USER_ID_MISSING;
        }
        return ProcessError.SUCCESS;
    }


}
