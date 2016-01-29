package com.iblock.workflow.biz;

import com.iblock.common.bean.PageModel;
import com.iblock.workflow.api.ProcessQueryService;
import com.iblock.workflow.beans.ActivityInfoBean;
import com.iblock.workflow.dtos.ActionLogDTO;
import com.iblock.workflow.dtos.TaskDTO;
import com.iblock.workflow.dtos.UserTaskInfoDTO;
import com.iblock.workflow.enums.ActionType;
import com.iblock.workflow.enums.TaskStatus;
import com.iblock.workflow.vars.ProcessVars;
import com.iblock.workflow.vars.WorkflowXmlVars;
import lombok.extern.log4j.Log4j;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricIdentityLink;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.persistence.entity.HistoricVariableInstanceEntity;
import org.activiti.engine.impl.pvm.ReadOnlyProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: 遐
 * Date: 14-9-10
 * Time: 下午1:39
 * To change this template use File | Settings | File Templates.
 */
@Log4j
public class ProcessQueryServiceObject implements ProcessQueryService {

    private final Comparator<ActionLogDTO> actionLogDTOComparator = new Comparator<ActionLogDTO>() {
        public int compare(ActionLogDTO o1, ActionLogDTO o2) {
            return o1.getActionTime().compareTo(o2.getActionTime());
        }
    };

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    private List<TaskDTO> queryTodoTaskList(String assignee) {
        List<TaskDTO> taskDTOList = new ArrayList<TaskDTO>();
        List<Task> myTodoTaskList = taskService.createTaskQuery().taskAssignee(assignee).active().list();
        if (myTodoTaskList != null) {
            for (Task task : myTodoTaskList) {
                taskDTOList.add(buildTaskDTO(task, Arrays.asList(assignee)));
            }
        }
        List<Task> myCandidateTaskList = taskService.createTaskQuery().taskCandidateUser(assignee).active().list();
        if (myCandidateTaskList != null) {
            for (Task task : myCandidateTaskList) {
                taskDTOList.add(buildTaskDTO(task, Arrays.asList(assignee)));
            }
        }
        return taskDTOList;
    }

    public boolean processActive(String processId) {
        try {
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processId).singleResult();
            return processInstance != null;
        } catch (Exception e) {
            log.error(String.format("severity=[1], ProcessQueryService.processActive error!. processId=[%s]", processId));
            return false;
        }
    }

    public List<TaskDTO> queryTodoTaskList(String assignee, List<String> taskDefinitionKeyList) {
        if (CollectionUtils.isEmpty(taskDefinitionKeyList)) {
            return queryTodoTaskList(assignee);
        }
        List<TaskDTO> taskDTOList = new ArrayList<TaskDTO>();
        List<Task> myTodoTaskList = taskService.createTaskQuery().taskAssignee(assignee).active().list();
        if (myTodoTaskList != null) {
            for (Task task : myTodoTaskList) {
                if (taskDefinitionKeyList.contains(task.getTaskDefinitionKey()) || (taskDefinitionKeyList.contains(ProcessVars.SIGN) && !StringUtils.isBlank(task.getParentTaskId()))) {
                    taskDTOList.add(buildTaskDTO(task, Arrays.asList(assignee)));
                }
            }
        }
        List<Task> myCandidateTaskList = taskService.createTaskQuery().taskCandidateUser(assignee).active().list();
        if (myCandidateTaskList != null) {
            for (Task task : myCandidateTaskList) {
                if (taskDefinitionKeyList.contains(task.getTaskDefinitionKey()) || (taskDefinitionKeyList.contains(ProcessVars.SIGN) && !StringUtils.isBlank(task.getParentTaskId()))) {
                    taskDTOList.add(buildTaskDTO(task, Arrays.asList(assignee)));
                }
            }
        }
        return taskDTOList;
    }

    private List<TaskDTO> queryDoneTaskList(String assignee) {
        List<TaskDTO> taskDTOList = new ArrayList<TaskDTO>();
        List<HistoricTaskInstance> historicTaskInstanceList = historyService.createHistoricTaskInstanceQuery().taskAssignee(assignee).list();
        if (historicTaskInstanceList != null) {
            for (HistoricTaskInstance historicTaskInstance : historicTaskInstanceList) {
                if (!(historicTaskInstance.getEndTime() == null || historicTaskInstance.getDurationInMillis() == null || historicTaskInstance.getDurationInMillis() == 0)) {
                    taskDTOList.add(buildTaskDTO(historicTaskInstance, Arrays.asList(assignee)));
                }
            }
        }
        return taskDTOList;
    }

    private List<TaskDTO> queryDoneTaskList(String assignee, List<String> taskDefinitionKeyList) {
        if (CollectionUtils.isEmpty(taskDefinitionKeyList)) {
            return queryDoneTaskList(assignee);
        }

        List<HistoricTaskInstance> historicTaskInstanceList = historyService.createHistoricTaskInstanceQuery().taskAssignee(assignee).list();
        if (CollectionUtils.isEmpty(historicTaskInstanceList)) {
            return new ArrayList<TaskDTO>();
        }
        List<TaskDTO> taskDTOList = new ArrayList<TaskDTO>();
        for (HistoricTaskInstance historicTaskInstance : historicTaskInstanceList) {
            if (!(historicTaskInstance.getEndTime() == null || historicTaskInstance.getDurationInMillis() == null || historicTaskInstance.getDurationInMillis() == 0)) {
                if (taskDefinitionKeyList.contains(historicTaskInstance.getTaskDefinitionKey()) || (taskDefinitionKeyList.contains(ProcessVars.SIGN) && !StringUtils.isBlank(historicTaskInstance.getParentTaskId()))) {
                    taskDTOList.add(buildTaskDTO(historicTaskInstance, Arrays.asList(assignee)));
                }
            }
        }
        return taskDTOList;
    }

    public List<TaskDTO> queryDoneTaskList(String assignee, List<String> taskDefinitionKeyList, int action) {
        HistoricTaskInstanceQuery query = historyService.createHistoricTaskInstanceQuery().taskAssignee(assignee);
        if (action != 0) {
            query = query.taskVariableValueEquals("action", action);
        }
        query.taskVariableValueNotEquals("action", ActionType.CANCEL.getCode());
        List<HistoricTaskInstance> historicTaskInstanceList = query.list();
        if (CollectionUtils.isEmpty(historicTaskInstanceList)) {
            return new ArrayList<TaskDTO>();
        }
        List<TaskDTO> taskDTOList = new ArrayList<TaskDTO>();
        for (HistoricTaskInstance historicTaskInstance : historicTaskInstanceList) {
            if (!(historicTaskInstance.getEndTime() == null || historicTaskInstance.getDurationInMillis() == null || historicTaskInstance.getDurationInMillis() == 0)) {
                if (taskDefinitionKeyList.contains(historicTaskInstance.getTaskDefinitionKey()) || (taskDefinitionKeyList.contains(ProcessVars.SIGN) && !StringUtils.isBlank(historicTaskInstance.getParentTaskId()))) {
                    taskDTOList.add(buildTaskDTO(historicTaskInstance));
                }
            }
        }
        return taskDTOList;
    }

    private TaskDTO buildTaskDTO(HistoricTaskInstance task) {
        return buildTaskDTO(task, StringUtils.isBlank(task.getAssignee()) ? getTaskCandidates(task.getId()) : new ArrayList<String>());
    }

    private TaskDTO buildTaskDTO(HistoricTaskInstance task, List<String> candidateList) {
        TaskDTO taskDTO = new TaskDTO();
        if (!StringUtils.isBlank(task.getParentTaskId())) {
            HistoricTaskInstance parentTask = historyService.createHistoricTaskInstanceQuery().taskId(task.getParentTaskId()).singleResult();
            if (parentTask != null) {
                taskDTO.setProcessId(parentTask.getProcessInstanceId());
            }
            taskDTO.setActivityId(ProcessVars.SIGN);
        } else {
            taskDTO.setProcessId(task.getProcessInstanceId());
            taskDTO.setActivityId(task.getTaskDefinitionKey());
        }
        taskDTO.setTaskId(task.getId());
        taskDTO.setTaskName(task.getName());
        taskDTO.setAssignee(task.getAssignee());
        taskDTO.setStartDate(task.getStartTime());
        taskDTO.setDueDate(task.getDueDate());
        if (StringUtils.isNotEmpty(task.getTaskDefinitionKey()) && !task.getTaskDefinitionKey().equalsIgnoreCase(ProcessVars.WAIT_FOR_RESUBMIT)) {
            taskDTO.setMemo("");
        } else {
            HistoricVariableInstance memoHis = historyService.createHistoricVariableInstanceQuery().taskId(task.getId()).variableName(ProcessVars.MEMO).singleResult();
            if (memoHis != null) {
                taskDTO.setMemo((String) memoHis.getValue());
            }
        }
        taskDTO.setOwner(task.getOwner());
        taskDTO.setCandidateList(candidateList);
        return taskDTO;
    }

    private TaskDTO buildTaskDTO(Task task) {
        return buildTaskDTO(task, StringUtils.isBlank(task.getAssignee()) ? getTaskCandidates(task.getId()) : new ArrayList<String>());
    }

    private TaskDTO buildTaskDTO(Task task, List<String> candidateList) {
        TaskDTO taskDTO = new TaskDTO();
        if (!StringUtils.isBlank(task.getParentTaskId())) {
            Task parentTask = taskService.createTaskQuery().taskId(task.getParentTaskId()).singleResult();
            if (parentTask != null) {
                taskDTO.setProcessId(parentTask.getProcessInstanceId());
            }
            taskDTO.setActivityId(ProcessVars.SIGN);
        } else {
            taskDTO.setProcessId(task.getProcessInstanceId());
            taskDTO.setActivityId(task.getTaskDefinitionKey());
        }
        taskDTO.setTaskId(task.getId());
        taskDTO.setAssignee(task.getAssignee());
        taskDTO.setStartDate(task.getCreateTime());
        taskDTO.setDueDate(task.getDueDate());
        if (StringUtils.isNotEmpty(task.getTaskDefinitionKey()) && !task.getTaskDefinitionKey().equalsIgnoreCase(ProcessVars.WAIT_FOR_RESUBMIT)) {
            taskDTO.setMemo("");
        } else {
            HistoricVariableInstance memoHis = historyService.createHistoricVariableInstanceQuery().taskId(task.getId()).variableName(ProcessVars.MEMO).singleResult();
            if (memoHis != null) {
                taskDTO.setMemo((String) memoHis.getValue());
            }
        }
        taskDTO.setOwner(task.getOwner());
        taskDTO.setTaskName(task.getName());
        taskDTO.setCandidateList(candidateList);
        return taskDTO;
    }

    private List<String> getTaskCandidates(String taskId) {
        List<String> candidateList = new ArrayList<String>();
        List<IdentityLink> identityLinkList = taskService.getIdentityLinksForTask(taskId);
        if (CollectionUtils.isEmpty(identityLinkList)) {
            return new ArrayList<String>();
        }
        for (IdentityLink identityLink : identityLinkList) {
            candidateList.add(identityLink.getUserId());
        }
        return candidateList;
    }

    private PageModel createPageModel(List<TaskDTO> taskDTOList, int page, int pageSize, long recordCount) {
        PageModel pageModel = new PageModel();
        pageModel.setPageSize(pageSize);
        pageModel.setPage(page);
        pageModel.setRecords(taskDTOList);
        pageModel.setRecordCount((int) recordCount);
        return pageModel;
    }

    private PageModel createEmptyPageModel(int page, int pageSize, long recordCount) {
        List<TaskDTO> taskDTOList = new ArrayList<TaskDTO>();
        return createPageModel(taskDTOList, page, pageSize, recordCount);
    }

    public List<TaskDTO> queryActiveTaskList(List<String> processIdList) {
        List<TaskDTO> taskDTOList = new ArrayList<TaskDTO>();
        for (String processId : processIdList) {
            Task task = taskService.createTaskQuery().processInstanceId(processId).active().singleResult();
            if (task != null) {
                taskDTOList.add(buildTaskDTO(task));
            } else {
                List<HistoricActivityInstance> historicActivityInstanceList = historyService.createHistoricActivityInstanceQuery().processInstanceId(processId).activityType(ProcessVars.RECEIVE_TASK).orderByActivityId().desc().list();
                if (CollectionUtils.isEmpty(historicActivityInstanceList)) {
                    continue;
                }
                HistoricActivityInstance activityInstance = historicActivityInstanceList.get(0);
                if (activityInstance.getEndTime() == null || activityInstance.getDurationInMillis() == null || activityInstance.getDurationInMillis() == 0) {
                    taskDTOList.add(buildTaskDTO(activityInstance));
                }
            }
        }
        return taskDTOList;
    }

    public List<TaskDTO> queryActiveTaskListByKeys(List<String> taskDefinitionKeyList) {
        List<Task> tasks = new ArrayList<Task>();
        List<TaskDTO> result = new ArrayList<TaskDTO>();
        TaskQuery query = taskService.createTaskQuery();
        for (String s : taskDefinitionKeyList) {
            List<Task> taskInstances = query.taskDefinitionKey(s).list();
            if (CollectionUtils.isNotEmpty(taskInstances)) {
                tasks.addAll(taskInstances);
            }
        }
        for (Task task : tasks) {
            if (StringUtils.isNotEmpty(task.getAssignee())) {
                result.add(buildTaskDTO(task));
            } else {
                result.add(buildTaskDTO(task, getTaskCandidates(task.getId())));
            }
        }
        return result;
    }

    public List<String> queryActiveProcessIds(List<String> taskDefinitionKeyList) {
        List<Task> tasks = new ArrayList<Task>();
        List<HistoricActivityInstance> activityInstances = new ArrayList<HistoricActivityInstance>();
        Map<String, String> map = new HashMap<String, String>();
        List<String> result = new ArrayList<String>();
        findActivityAndTaskByKey(taskDefinitionKeyList, activityInstances, tasks);
        for (Task task : tasks) {
            if (!map.containsKey(task.getProcessInstanceId())) {
                map.put(task.getProcessInstanceId(), task.getProcessInstanceId());
            }
        }
        for (HistoricActivityInstance instance : activityInstances) {
            map.put(instance.getProcessInstanceId(), instance.getProcessInstanceId());
        }
        result.addAll(map.keySet());
        return result;
    }

    public Map<String, Date> queryFinishTime(List<String> processIds) {
        HistoricProcessInstanceQuery query = historyService.createHistoricProcessInstanceQuery();
        List<HistoricProcessInstance> list = query.finished().processInstanceIds(new HashSet<String>(processIds)).list();
        Map<String, Date> result = new HashMap<String, Date>();
        if (CollectionUtils.isEmpty(list)) {
            return result;
        }
        for (HistoricProcessInstance instance : list) {
            result.put(instance.getId(), instance.getEndTime());
        }
        return result;
    }

    private void findActivityAndTaskByKey(List<String> taskDefinitionKeyList, List<HistoricActivityInstance> activityInstances, List<Task> tasks) {
        TaskQuery query = taskService.createTaskQuery();
        for (String s : taskDefinitionKeyList) {
            List<Task> taskInstances = query.taskDefinitionKey(s).list();
            if (CollectionUtils.isNotEmpty(taskInstances)) {
                tasks.addAll(taskInstances);
            } else {
                List<HistoricActivityInstance> historicActivityInstanceList = historyService.createHistoricActivityInstanceQuery().activityId(s).activityType(ProcessVars.RECEIVE_TASK).orderByActivityId().desc().list();
                if (CollectionUtils.isEmpty(historicActivityInstanceList)) {
                    continue;
                }
                for (HistoricActivityInstance activityInstance : historicActivityInstanceList) {
                    if (activityInstance.getEndTime() == null || activityInstance.getDurationInMillis() == null || activityInstance.getDurationInMillis() == 0) {
                        activityInstances.add(activityInstance);
                    }
                }
            }
        }
    }

    public List<String> queryPaySuccessIDs(String requestNo, Date beginTime, Date endTime) {
        List<String> result = new ArrayList<String>();
        Map<String, String> tmp = new HashMap<String, String>();
        HistoricProcessInstanceQuery query = historyService.createHistoricProcessInstanceQuery();
        if (StringUtils.isNotEmpty(requestNo)) {
            query = query.startedBy(requestNo);
        }
        if (beginTime != null) {
            query = query.startedAfter(beginTime);
        }
        if (endTime != null) {
            query = query.startedBefore(endTime);
        }
        List<HistoricProcessInstance> list = query.finished().list();
        List<HistoricVariableInstance> excludes = historyService.createHistoricVariableInstanceQuery().excludeTaskVariables().variableValueEquals(ProcessVars.ACTION, ActionType.ROLLBACK.getCode()).list();
        for (HistoricVariableInstance exclude : excludes) {
            tmp.put(exclude.getProcessInstanceId(), exclude.getProcessInstanceId());
        }
        if (CollectionUtils.isNotEmpty(list)) {
            for (HistoricProcessInstance instance : list) {
                if (!tmp.containsKey(instance.getId())) {
                    result.add(instance.getId());
                }
            }
        }
        return result;
    }

    public List<TaskDTO> queryByDefAndTime(String taskDefKey, Date begin, Date end) {
        List<Task> tasks = taskService.createTaskQuery().taskDefinitionKey(taskDefKey).taskCreatedBefore(end).taskCreatedAfter(begin).list();
        List<TaskDTO> taskDTOList = new ArrayList<TaskDTO>();
        if (tasks != null) {
            for (Task task : tasks) {
                taskDTOList.add(buildTaskDTO(task));
            }
        }
        return taskDTOList;
    }

    private TaskDTO buildTaskDTO(HistoricActivityInstance activityInstance) {
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setTaskId(activityInstance.getTaskId());
        taskDTO.setActivityId(activityInstance.getActivityId());
        taskDTO.setDueDate(null);
        taskDTO.setOwner(null);
        taskDTO.setProcessId(activityInstance.getProcessInstanceId());
        taskDTO.setTaskName(activityInstance.getActivityName());
        return taskDTO;
    }

    public TaskDTO queryActiveTask(String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).active().singleResult();
        return task == null ? new TaskDTO() : buildTaskDTO(task);
    }

    public List<UserTaskInfoDTO> queryAllUserTaskInfo(String processId) {
        try {
            String processDefinitionKey = getProcessDefinitionKey(processId);
            HistoricVariableInstanceEntity launcher = (HistoricVariableInstanceEntity) historyService.createHistoricVariableInstanceQuery().processInstanceId(processId).variableName("launcher").singleResult();
            if (processDefinitionKey == null) {
                return new ArrayList<UserTaskInfoDTO>();
            }
            List<ActivityInfoBean> activityInfoBeanList = buildActivityInfoBeanList(processDefinitionKey);
            if (activityInfoBeanList.size() == 0) {
                return new ArrayList<UserTaskInfoDTO>();
            }
            //get activity
            List<HistoricActivityInstance> activityInstanceList = historyService.createHistoricActivityInstanceQuery().processInstanceId(processId).orderByHistoricActivityInstanceEndTime().desc().list();
            return CollectionUtils.isEmpty(activityInstanceList) ? new ArrayList<UserTaskInfoDTO>() : buildUserTaskInfoDTOList(activityInfoBeanList, getLatestHistoricActivityInstanceList(activityInstanceList), launcher.getTextValue());
        }catch (Exception e){
            log.error(String.format("severity=[1], ProcessQueryService.queryAllUserTaskInfo fail! processId=[%s]", processId), e);
            return new ArrayList<UserTaskInfoDTO>();
        }
    }

    public TaskDTO queryActiveTaskByProcess(String processId) {
        Task task = taskService.createTaskQuery().processInstanceId(processId).active().singleResult();
        return task == null ? null : buildTaskDTO(task);
    }

    public TaskDTO queryTaskByProcess(String processId) {
        Task task = taskService.createTaskQuery().processInstanceId(processId).singleResult();
        return task == null ? null : buildTaskDTO(task);
    }

    protected Element getWorkflowElement(String processDefinitionKey) {
        try {
            Builder builder = new Builder();
            Document doc = builder.build(this.getClass().getClassLoader().getResourceAsStream("config/spring/local/workflow/Expense.workflow.xml"));
            Elements elements = doc.getRootElement().getChildElements();
            for (int i = 0; i < elements.size(); i++) {
                String id = elements.get(i).getAttribute(WorkflowXmlVars.ID).getValue();
                if (id.equalsIgnoreCase(processDefinitionKey)) {
                    return elements.get(i);
                }
            }
            return null;
        } catch (Exception e) {
            log.error("severity=[1], failed to parse workflow document. path=[config/spring/local/workflow/Expense.workflow.xml], processDefinitionKey is " + processDefinitionKey, e);
            return null;
        }
    }

    private String getProcessDefinitionKey(String processId) {
        String processDefinitionId = null;
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processId).singleResult();
        if (processInstance != null) {
            processDefinitionId = processInstance.getProcessDefinitionId();
        } else {
            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processId).singleResult();
            if (historicProcessInstance != null) {
                processDefinitionId = historicProcessInstance.getProcessDefinitionId();
            }
        }
        if (processDefinitionId == null) {
            return null;
        }
        ReadOnlyProcessDefinition processDefinition = ((RepositoryServiceImpl) repositoryService).getDeployedProcessDefinition(processDefinitionId);
        return processDefinition == null ? null : processDefinition.getKey();
    }

    private Map<String, List<HistoricActivityInstance>> buildHistoricActivityInstanceMap(List<HistoricActivityInstance> activityInstanceList) {
        Map<String, List<HistoricActivityInstance>> historicActivityInstanceMap = new HashMap<String, List<HistoricActivityInstance>>();
        for (HistoricActivityInstance activityInstance : activityInstanceList) {
            if (validTask(activityInstance.getActivityType()) || validEvent(activityInstance.getActivityType()) || activityInstance.getActivityId().equalsIgnoreCase(ProcessVars.PAY_SUCCESS_ID)) {
                if (historicActivityInstanceMap.containsKey(activityInstance.getActivityId())) {
                    historicActivityInstanceMap.get(activityInstance.getActivityId()).add(activityInstance);
                } else {
                    List<HistoricActivityInstance> tmpInstanceList = new ArrayList<HistoricActivityInstance>();
                    tmpInstanceList.add(activityInstance);
                    historicActivityInstanceMap.put(activityInstance.getActivityId(), tmpInstanceList);
                }
            }
        }
        return historicActivityInstanceMap;
    }

    private List<ActivityInfoBean> buildActivityInfoBeanList(String processDefinitionKey) {
        Element workflowElement = getWorkflowElement(processDefinitionKey);
        if (workflowElement == null) {
            return new ArrayList<ActivityInfoBean>();
        }
        List<ActivityInfoBean> activityInfoBeanList = new ArrayList<ActivityInfoBean>();
        Elements activityElements = workflowElement.getChildElements();
        for (int i = 0; i < activityElements.size(); i++) {
            Element e = activityElements.get(i);
            ActivityInfoBean activityInfoBean = new ActivityInfoBean(e.getValue(), Integer.parseInt(e.getAttributeValue
                    (WorkflowXmlVars.SEQ)),
                    Arrays.asList(StringUtils.split(e.getAttributeValue(WorkflowXmlVars.ID), ",")),
                    e.getAttributeValue(WorkflowXmlVars.TASK).equals(WorkflowXmlVars.TRUE),
                    e.getAttributeValue(WorkflowXmlVars.MANDATORY).equals(WorkflowXmlVars.TRUE));
            activityInfoBeanList.add(activityInfoBean);
        }
        return activityInfoBeanList;
    }

    private List<HistoricActivityInstance> getLatestHistoricActivityInstanceList(List<HistoricActivityInstance> activityInstanceList) {
        Map<String, List<HistoricActivityInstance>> historicActivityInstanceMap = buildHistoricActivityInstanceMap(activityInstanceList);
        Iterator<Map.Entry<String, List<HistoricActivityInstance>>> iterator = historicActivityInstanceMap.entrySet().iterator();
        List<HistoricActivityInstance> historicActivityInstanceList = new ArrayList<HistoricActivityInstance>();
        while (iterator.hasNext()) {
            Map.Entry<String, List<HistoricActivityInstance>> entry = iterator.next();
            historicActivityInstanceList.add(getLatestActivityInstance(entry.getValue()));
        }
        return historicActivityInstanceList;
    }

    private List<UserTaskInfoDTO> buildUserTaskInfoDTOList(List<ActivityInfoBean> activityInfoBeanList, List<HistoricActivityInstance> historicActivityInstanceList, String launcher) {
        List<UserTaskInfoDTO> userTaskInfoDTOList = new ArrayList<UserTaskInfoDTO>();
        int rank = 1;
        for (ActivityInfoBean infoBean : activityInfoBeanList) {
            HistoricActivityInstance instance = getMatchHistoryActivityInstance(historicActivityInstanceList, infoBean);
            if (instance != null) {
                TaskStatus taskStatus = getTaskStatus(instance);
                String assignee = (StringUtils.isNotEmpty(instance.getAssignee()) && launcher.equals(instance.getAssignee())) ? "" : instance.getAssignee();
                if (instance.getActivityType().equalsIgnoreCase(ProcessVars.USER_TASK) && taskStatus == TaskStatus.RUNNING) {
                    List<String> candidates = getTaskCandidates(instance.getTaskId());
                    if (!CollectionUtils.isEmpty(candidates)) {
                        assignee = candidates.get(0);
                    }
                }
                userTaskInfoDTOList.add(new UserTaskInfoDTO(rank++, infoBean.getActivityName(), assignee, taskStatus.getCode()));
            } else if (infoBean.isMandatory()) {
                userTaskInfoDTOList.add(new UserTaskInfoDTO(rank++, infoBean.getActivityName(), TaskStatus.PENDING.getCode()));
            }
        }
        return userTaskInfoDTOList;
    }

    private HistoricActivityInstance getMatchHistoryActivityInstance(List<HistoricActivityInstance> historicActivityInstanceList, ActivityInfoBean activityInfoBean) {
        for (HistoricActivityInstance instance : historicActivityInstanceList) {
            if (activityInfoBean.getActivityIdList().contains(instance.getActivityId())) {
                return instance;
            }
        }
        return null;
    }

    private HistoricActivityInstance getLatestActivityInstance(List<HistoricActivityInstance> activityInstanceList) {
        for (HistoricActivityInstance activityInstance : activityInstanceList) {
            TaskStatus taskStatus = getTaskStatus(activityInstance);
            if (taskStatus == TaskStatus.RUNNING) {
                return activityInstance;
            }
        }
        return activityInstanceList.get(0);
    }

    private TaskStatus getTaskStatus(HistoricActivityInstance historicActivityInstance) {
        if (historicActivityInstance.getEndTime() == null || historicActivityInstance.getDurationInMillis() == null || historicActivityInstance.getDurationInMillis() == 0) {
            return TaskStatus.RUNNING;
        } else {
            return TaskStatus.OVER;
        }
    }

    private boolean validTask(String activityType) {
        return activityType.equalsIgnoreCase(ProcessVars.USER_TASK) || activityType.equalsIgnoreCase(ProcessVars.RECEIVE_TASK);
    }

    private boolean validEvent(String activityType){
        return  activityType.equalsIgnoreCase(ProcessVars.START_EVENT) || activityType.equalsIgnoreCase(ProcessVars.END_EVENT);
    }

    public List<ActionLogDTO> queryWorkflowLog(String processId) {
        try {
            List<HistoricActivityInstance> activityInstanceList = historyService.createHistoricActivityInstanceQuery().processInstanceId(processId).list();
            if (CollectionUtils.isEmpty(activityInstanceList)) {
                return new ArrayList<ActionLogDTO>();
            }
            List<ActionLogDTO> actionLogDTOList = new ArrayList<ActionLogDTO>();
            for (HistoricActivityInstance instance : activityInstanceList) {
                if ((validTask(instance.getActivityType()) && getTaskStatus(instance) == TaskStatus.OVER) || validEvent(instance.getActivityType())) {
                    ActionLogDTO actionLogDTO = buildActionLogDTO(instance);
                    actionLogDTOList.add(actionLogDTO);
                }
            }
            List<HistoricTaskInstance> taskInstanceList = historyService.createHistoricTaskInstanceQuery().processInstanceId(processId).list();
            Map<String, HistoricTaskInstance> historicTaskInstanceMap = buildHistoricTaskMap(taskInstanceList);
            List<Comment> comments = taskService.getProcessInstanceComments(processId);
            if (!CollectionUtils.isEmpty(comments)) {
                for (Comment comment : comments) {
                    actionLogDTOList.add(buildActionLogDTO(comment, historicTaskInstanceMap));
                }
            }
            for (HistoricTaskInstance hisTask : taskInstanceList) {
                List<HistoricTaskInstance> tmpList = historyService.createHistoricTaskInstanceQuery().taskParentTaskId(hisTask.getId()).list();
                if (CollectionUtils.isEmpty(tmpList)) {
                    continue;
                }
                for (HistoricTaskInstance subTask : tmpList) {
                    if (subTask.getEndTime() != null && subTask.getDurationInMillis() != null) {
                        ActionLogDTO dto = buildActionLogDTO(subTask);
                        if (dto != null) {
                            actionLogDTOList.add(dto);
                        }
                    }
                }
            }
            Collections.sort(actionLogDTOList, actionLogDTOComparator);
            return actionLogDTOList;
        } catch (Exception e) {
            log.error(String.format("severity=[1], ProcessQueryService.queryWorkflowLog fail! processId=[%s]", processId), e);
            return new ArrayList<ActionLogDTO>();
        }
    }

    private ActionLogDTO buildActionLogDTO(HistoricActivityInstance instance) {
        ActionLogDTO actionLogDTO = new ActionLogDTO();
        actionLogDTO.setActionTime(instance.getEndTime());
        actionLogDTO.setUserId(instance.getAssignee());
        if (instance.getActivityType().equalsIgnoreCase(ProcessVars.USER_TASK)) {
            actionLogDTO.setTaskDescription(instance.getActivityName());
            setUserTaskActionAndMemo(actionLogDTO, instance.getTaskId());
        } else if (instance.getActivityType().equalsIgnoreCase(ProcessVars.RECEIVE_TASK)) {
            setReceiveTaskActionAndMemo(actionLogDTO, instance.getProcessInstanceId());
        } else {
            setEventActionAndMemo(actionLogDTO, instance.getActivityType());
        }
        return actionLogDTO;
    }

    private Map<String, HistoricTaskInstance> buildHistoricTaskMap(List<HistoricTaskInstance> taskInstanceList) {
        Map<String, HistoricTaskInstance> taskMap = new HashMap<String, HistoricTaskInstance>();
        for (HistoricTaskInstance instance : taskInstanceList) {
            taskMap.put(instance.getId(), instance);
        }
        return taskMap;
    }

    private ActionLogDTO buildActionLogDTO(Comment comment, Map<String, HistoricTaskInstance> historicTaskInstanceMap) {
        ActionLogDTO actionLogDTO = new ActionLogDTO();
        actionLogDTO.setAction(Integer.valueOf(comment.getType()));
        actionLogDTO.setTaskDescription("");
        actionLogDTO.setUserId(historicTaskInstanceMap.get(comment.getTaskId()).getAssignee());
        actionLogDTO.setActionTime(comment.getTime());
        actionLogDTO.setMemo(comment.getFullMessage());
        return actionLogDTO;
    }

    public String queryWorkflowLauncher(String processId) {
        List<HistoricIdentityLink> identityLinkList = historyService.getHistoricIdentityLinksForProcessInstance(processId);
        if (CollectionUtils.isEmpty(identityLinkList)) {
            return null;
        }
        for (HistoricIdentityLink link : identityLinkList) {
            if (link.getType().equalsIgnoreCase(ProcessVars.STARTER_TYPE)) {
                return link.getUserId();
            }
        }
        return null;
    }

    public Map<String, String> queryWorkflowLaunchers(List<TaskDTO> tasks) {
        Map<String, String> result = new HashMap<String, String>();
        for(TaskDTO task : tasks) {
            result.put(task.getTaskId(), queryWorkflowLauncher(task.getProcessId()));
        }
        return result;
    }

    public Object loadVarById(String id, String name) {
        return runtimeService.getVariable(id, name);
    }

    public TaskDTO queryLastCompleteUserTask(String processId) {
        List<HistoricTaskInstance> taskInstanceList = historyService.createHistoricTaskInstanceQuery().processInstanceId(processId).finished().orderByTaskId().desc().list();
        return CollectionUtils.isEmpty(taskInstanceList) ? null : buildTaskDTO(taskInstanceList.get(0));
    }

    public Map<String, ActionLogDTO> querySubTaskLog(String taskId) {
        if (StringUtils.isBlank(taskId)) {
            return new HashMap<String, ActionLogDTO>();
        }
        List<HistoricTaskInstance> taskInstanceList = historyService.createHistoricTaskInstanceQuery().taskParentTaskId(taskId).list();
        if (CollectionUtils.isEmpty(taskInstanceList)) {
            return new HashMap<String, ActionLogDTO>();
        }
        Map<String, ActionLogDTO> actionLogDTOMap = new HashMap<String, ActionLogDTO>();
        for (HistoricTaskInstance hisTask : taskInstanceList) {
            ActionLogDTO dto = new ActionLogDTO();
            if (hisTask.getEndTime() != null && hisTask.getDurationInMillis() != null) {
                dto = buildActionLogDTO(hisTask);
            }
            if (dto != null) {
                actionLogDTOMap.put(hisTask.getAssignee(), dto);
            }
        }
        return actionLogDTOMap;
    }

    public TaskDTO queryParentTask(String taskId) {
        HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
        if (historicTaskInstance == null || StringUtils.isBlank(historicTaskInstance.getParentTaskId())) {
            return null;
        }
        Task task = taskService.createTaskQuery().taskId(historicTaskInstance.getParentTaskId()).singleResult();
        if (task == null) {
            log.error(String.format("severity=[2], the parent task is no longer active. subTaskId=[%s]&parentTaskId=[%s]", taskId, historicTaskInstance.getParentTaskId()));
            return null;
        }
        return buildTaskDTO(task);
    }

    private ActionLogDTO buildActionLogDTO(HistoricTaskInstance hisTask) {
        HistoricVariableInstance vInstance = historyService.createHistoricVariableInstanceQuery().taskId(hisTask.getId()).variableName(ProcessVars.ACTION).singleResult();
        if (vInstance == null) {
            return null;
        }
        ActionLogDTO dto = new ActionLogDTO();
        dto.setUserId(hisTask.getAssignee());
        dto.setActionTime(hisTask.getEndTime());
        dto.setAction((Integer) vInstance.getValue());
        vInstance = historyService.createHistoricVariableInstanceQuery().taskId(hisTask.getId()).variableName(ProcessVars.MEMO).singleResult();
        if (vInstance != null) {
            dto.setMemo((String) vInstance.getValue());
        }
        dto.setTaskDescription(hisTask.getName());
        return dto;
    }

    private void setReceiveTaskActionAndMemo(ActionLogDTO actionLogDTO, String processId) {
        HistoricVariableInstance historicVariableInstance = historyService.createHistoricVariableInstanceQuery().processInstanceId(processId).variableName(ProcessVars.SIGNAL).singleResult();
        if (historicVariableInstance != null) {
            actionLogDTO.setSignal((Integer) historicVariableInstance.getValue());
        }
    }

    private void setUserTaskActionAndMemo(ActionLogDTO actionLogDTO, String taskId) {
        if (StringUtils.isBlank(taskId)) {
            actionLogDTO.setAction(ActionType.PASS.getCode());
            return;
        }
        List<HistoricVariableInstance> historicVariableInstanceList = historyService.createHistoricVariableInstanceQuery().taskId(taskId).list();
        for (HistoricVariableInstance varInstance : historicVariableInstanceList) {
            if (varInstance.getVariableName().equalsIgnoreCase(ProcessVars.ACTION)) {
                actionLogDTO.setAction((Integer) varInstance.getValue());
            } else if (varInstance.getVariableName().equalsIgnoreCase(ProcessVars.MEMO)) {
                actionLogDTO.setMemo((String) varInstance.getValue());
            }
        }
    }

    private void setEventActionAndMemo(ActionLogDTO actionLogDTO, String activityType) {
        if (activityType.equalsIgnoreCase(ProcessVars.START_EVENT)) {
            actionLogDTO.setAction(ActionType.SUBMIT.getCode());
            actionLogDTO.setMemo(ActionType.SUBMIT.getMessage());
        } else if (activityType.equalsIgnoreCase(ProcessVars.END_EVENT)) {
            actionLogDTO.setAction(ActionType.END.getCode());
            actionLogDTO.setMemo(ActionType.END.getMessage());
        }
    }

}
