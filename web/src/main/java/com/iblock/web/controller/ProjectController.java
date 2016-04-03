package com.iblock.web.controller;

import com.iblock.common.advice.Auth;
import com.iblock.common.bean.Page;
import com.iblock.common.bean.ProjectSearchBean;
import com.iblock.dao.po.Project;
import com.iblock.dao.po.WorkflowLog;
import com.iblock.service.bo.ProjectAcceptBo;
import com.iblock.service.bo.ProjectBo;
import com.iblock.service.project.ProjectService;
import com.iblock.web.constant.RoleConstant;
import com.iblock.web.enums.ResponseStatus;
import com.iblock.web.info.JobInterestInfo;
import com.iblock.web.response.CommonResponse;
import com.iblock.workflow.api.ProcessManageService;
import com.iblock.workflow.api.ProcessQueryService;
import com.iblock.workflow.dtos.BaseResultDTO;
import com.iblock.workflow.dtos.ProcessResultDTO;
import com.iblock.workflow.dtos.TaskActionDTO;
import com.iblock.workflow.enums.ProcessError;
import com.iblock.workflow.vars.ProcessVars;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by baidu on 16/4/3.
 */
@Controller
@Log4j
@RequestMapping("/project")
public class ProjectController extends BaseController {

    @Autowired
    private ProcessManageService processManageService;
    @Autowired
    private ProcessQueryService processQueryService;
    @Autowired
    private ProjectService projectService;


    @RequestMapping(value = "/save", method = RequestMethod.POST, consumes = "application/json")
    @Auth(role = RoleConstant.MANAGER)
    @ResponseBody
    public CommonResponse<Long> save(@RequestBody ProjectBo project) {
        try {
            long id = projectService.save(project, getUserInfo().getUserId());
            if (id > 0) {
                return new CommonResponse<Long>(id);
            }
        } catch (Exception e) {
            log.error("save project error!", e);
        }
        return new CommonResponse<Long>(ResponseStatus.SYSTEM_ERROR);
    }

    @RequestMapping(value = "/publish/{projectId}", method = RequestMethod.GET)
    @Auth(role = RoleConstant.MANAGER)
    @ResponseBody
    public CommonResponse<Boolean> publish(@PathVariable(value = "projectId") long id) {
        try {
            Map<String, Object> map = new HashMap<String, Object>();
            Map<String, Object> objectMap = new HashMap<String, Object>();
            map.put(ProcessVars.USER_ID, getUserInfo().getUserId());
            objectMap.put(ProcessVars.PROJECT_KEY, id);
            objectMap.put(ProcessVars.MANAGER, getUserInfo().getUserId());
            map.put(ProcessVars.DATA, objectMap);
            ProcessResultDTO dto = processManageService.startProcess(map);
            if (dto.getProcessId() != null) {
                return new CommonResponse<Boolean>(true);
            }
        } catch (Exception e) {
            log.error("publish project error!", e);
        }
        return new CommonResponse<Boolean>(ResponseStatus.SYSTEM_ERROR);
    }



    @RequestMapping(value = "/search", method = RequestMethod.GET)
    @Auth(role = RoleConstant.DESIGNER)
    @ResponseBody
    public CommonResponse<Page<Project>> search(@RequestParam("pageNo") int pageNo, @RequestParam("pageSize") int
            pageSize,
                                          @RequestParam(value = "order", required = false) int order, @RequestParam
                                                  (value = "orderBy", required = false) int orderBy,
                                          @RequestParam(value = "keyword", required = false) int keyword, @RequestParam
                                                      (value = "maxPay", required = false) int maxPay,
                                          @RequestParam(value = "city", required = false) int city, @RequestParam
                                                  (value = "industry", required = false) int industry) {
        try {
            ProjectSearchBean bean = new ProjectSearchBean();
            bean.setPageSize(pageSize);
            bean.setPageNo(pageNo);
            bean.setUserId(getUserInfo().getUserId());
            bean.setRole(getUserInfo().getRole());
            // todo
            return new CommonResponse<Page<Project>>(projectService.search(bean));
        } catch (Exception e) {
            log.error("search project error!", e);
        }
        return new CommonResponse<Page<Project>>(ResponseStatus.SYSTEM_ERROR);
    }

    @RequestMapping(value = "/accept", method = RequestMethod.POST, consumes = "application/json")
    @Auth(role = RoleConstant.MANAGER)
    @ResponseBody
    public CommonResponse<Boolean> accept(@RequestBody ProjectAcceptBo acceptBo) {
        try {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put(ProcessVars.ACTION, acceptBo.getAccept() ? 1 : 2);
            TaskActionDTO action = new TaskActionDTO();
            action.setUserId(getUserInfo().getUserId().toString());
            action.setActionMap(map);
            action.setTaskId(processQueryService.queryTask(getUserInfo().getUserId().toString(), projectService
                    .getWorkflowId(acceptBo.getId())).getTaskId());
            BaseResultDTO result = processManageService.operateTask(action);
            if (result.getCode() == ProcessError.SUCCESS.getCode()) {
                return new CommonResponse<Boolean>(true);
            }
        } catch (Exception e) {
            log.error("accept project error!", e);
        }
        return new CommonResponse<Boolean>(ResponseStatus.SYSTEM_ERROR);
    }


}
