package com.iblock.workflow.api;

import com.iblock.workflow.dtos.ActionLogDTO;
import com.iblock.workflow.dtos.TaskDTO;
import com.iblock.workflow.dtos.UserTaskInfoDTO;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: qihong
 * Date: 14-9-10
 * Time: 下午12:31
 * To change this template use File | Settings | File Templates.
 */
public interface ProcessQueryService {

    boolean processActive(String processId);

    List<TaskDTO> queryTodoTaskList(String assignee, List<String> taskDefinitionKeyList);

    public List<TaskDTO> queryDoneTaskList(String assignee, List<String> taskDefinitionKeyList, int action);

    List<TaskDTO> queryActiveTaskList(List<String> processIdList);

    List<TaskDTO> queryActiveTaskListByKeys(List<String> taskDefinitionKeyList);

    List<String> queryActiveProcessIds(List<String> taskDefinitionKeyList);

    Map<String, Date> queryFinishTime(List<String> processIds);

    List<String> queryPaySuccessIDs(String requestNo, Date beginTime, Date endTime);

    List<TaskDTO> queryByDefAndTime(String taskDefKey, Date begin, Date end);

    TaskDTO queryActiveTask(String taskId);

    List<UserTaskInfoDTO> queryAllUserTaskInfo(String processId);

    TaskDTO queryActiveTaskByProcess(String processId);

    TaskDTO queryTaskByProcess(String processId);

    List<ActionLogDTO> queryWorkflowLog(String processId);

    String queryWorkflowLauncher(String processId);

    Map<String, ActionLogDTO> querySubTaskLog(String taskId);

    public TaskDTO queryParentTask(String taskId);

    Map<String, String> queryWorkflowLaunchers(List<TaskDTO> tasks);

    Object loadVarById(String id, String name);

    TaskDTO queryLastCompleteUserTask(String processId);

}
