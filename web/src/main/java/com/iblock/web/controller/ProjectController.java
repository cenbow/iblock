package com.iblock.web.controller;

import com.iblock.common.advice.Auth;
import com.iblock.common.bean.Page;
import com.iblock.common.bean.ProjectSearchBean;
import com.iblock.common.enums.ProjectStatus;
import com.iblock.dao.po.JobInterest;
import com.iblock.dao.po.Project;
import com.iblock.service.bo.ProjectAcceptBo;
import com.iblock.service.interest.JobInterestService;
import com.iblock.service.project.ProjectService;
import com.iblock.web.constant.RoleConstant;
import com.iblock.web.enums.ResponseStatus;
import com.iblock.web.info.ProjectDetailInfo;
import com.iblock.web.request.PageRequest;
import com.iblock.web.request.project.AcceptHiringRequest;
import com.iblock.web.request.project.HireRequest;
import com.iblock.web.request.project.ProjectCreateRequest;
import com.iblock.web.request.project.ProjectIdRequest;
import com.iblock.web.request.project.ProjectSearchRequest;
import com.iblock.web.request.project.ProjectUpdateRequest;
import com.iblock.web.response.CommonResponse;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by baidu on 16/4/3.
 */
@Controller
@Log4j
@RequestMapping("/project")
public class ProjectController extends BaseController {

    @Autowired
    private ProjectService projectService;
    @Autowired
    private JobInterestService jobInterestService;


    @RequestMapping(value = "/new", method = RequestMethod.POST, consumes = "application/json")
    @Auth(role = RoleConstant.MANAGER)
    @ResponseBody
    public CommonResponse<Long> save(@RequestBody ProjectCreateRequest project) {
        try {
            long id = projectService.save(project.toProject(), getUserInfo().getUserId());
            if (id > 0) {
                return new CommonResponse<Long>(id);
            }
        } catch (Exception e) {
            log.error("save project error!", e);
        }
        return new CommonResponse<Long>(ResponseStatus.SYSTEM_ERROR);
    }

    @RequestMapping(value = "/update/{projectid}", method = RequestMethod.POST, consumes = "application/json")
    @Auth(role = RoleConstant.MANAGER)
    @ResponseBody
    public CommonResponse<Long> save(@RequestBody ProjectUpdateRequest project, @PathVariable("projectid") Long projectId) {
        try {
            Project p = projectService.get(projectId);
            project.updateProject(p);
            p.setStatus((byte) ProjectStatus.AUDIT.getCode());
            long id = projectService.save(p, getUserInfo().getUserId());
            if (id > 0) {
                return new CommonResponse<Long>(id);
            }
        } catch (Exception e) {
            log.error("save project error!", e);
        }
        return new CommonResponse<Long>(ResponseStatus.SYSTEM_ERROR);
    }

    @RequestMapping(value = "/details/{projectid}", method = RequestMethod.GET, consumes = "application/json")
    @ResponseBody
    public CommonResponse<ProjectDetailInfo> detail(@PathVariable("projectid") Long projectId) {
        try {
            Project p = projectService.get(projectId);
            if (p == null) {
                return new CommonResponse<ProjectDetailInfo>(ResponseStatus.NOT_FOUND);
            }
            ProjectDetailInfo info = ProjectDetailInfo.parse(p);
            // todo
            return new CommonResponse<ProjectDetailInfo>(info);
        } catch (Exception e) {
            log.error("get project error!", e);
        }
        return new CommonResponse<ProjectDetailInfo>(ResponseStatus.SYSTEM_ERROR);
    }

    @RequestMapping(value = "/recommended", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public CommonResponse<Page<Project>> recommended(@RequestBody PageRequest request) {
        try {
            JobInterest interest = jobInterestService.get(getUserInfo().getUserId());
            ProjectSearchBean bean = new ProjectSearchBean();
            bean.setPageSize(request.getPageSize());
            bean.setPageNo(request.getPageNo());
            bean.setFreeze(false);
            bean.setStatus(ProjectStatus.RECRUITING.getCode());
            if (interest != null) {
                bean.setResident(interest.getResident());
                bean.setMinPay(interest.getStartPay());
                bean.setMaxPay(interest.getEndPay());
                if (StringUtils.isNotBlank(interest.getJobTypeList())) {
                    List<Integer> list = new ArrayList<Integer>();
                    for (String s : interest.getJobTypeList().split(",")) {
                        list.add(Integer.parseInt(s));
                    }
                    bean.setIndustry(list);
                }
                if (StringUtils.isNotBlank(interest.getCityList())) {
                    List<Integer> list = new ArrayList<Integer>();
                    for (String s : interest.getCityList().split(",")) {
                        list.add(Integer.parseInt(s));
                    }
                    bean.setCity(list);
                }
            }
            return new CommonResponse<Page<Project>>(projectService.search(bean));
        } catch (Exception e) {
            log.error("recommended project error!", e);
        }
        return new CommonResponse<Page<Project>>(ResponseStatus.SYSTEM_ERROR);
    }

    @RequestMapping(value = "/latest", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public CommonResponse<Page<Project>> latest(@RequestBody PageRequest request) {
        try {
            ProjectSearchBean bean = new ProjectSearchBean();
            bean.setPageNo(request.getPageNo());
            bean.setPageSize(request.getPageSize());
            bean.setFreeze(false);
            bean.setStatus(ProjectStatus.RECRUITING.getCode());
            return new CommonResponse<Page<Project>>(projectService.search(bean));
        } catch (Exception e) {
            log.error("latest project error!", e);
        }
        return new CommonResponse<Page<Project>>(ResponseStatus.SYSTEM_ERROR);
    }

    @RequestMapping(value = "/search", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public CommonResponse<Page<Project>> search(@RequestBody ProjectSearchRequest request) {
        try {
            return new CommonResponse<Page<Project>>(projectService.search(request.toBean()));
        } catch (Exception e) {
            log.error("search project error!", e);
        }
        return new CommonResponse<Page<Project>>(ResponseStatus.SYSTEM_ERROR);
    }

    @RequestMapping(value = "/accept", method = RequestMethod.POST, consumes = "application/json")
    @Auth(role = RoleConstant.ADMINISTRATOR)
    @ResponseBody
    public CommonResponse<Boolean> accept(@RequestBody ProjectAcceptBo acceptBo) {
        try {
            if (!projectService.accept(acceptBo)) {
                return new CommonResponse<Boolean>(ResponseStatus.VALIDATE_ERROR);
            }
            return new CommonResponse<Boolean>(ResponseStatus.SUCCESS);
        } catch (Exception e) {
            log.error("accept project error!", e);
        }
        return new CommonResponse<Boolean>(ResponseStatus.SYSTEM_ERROR);
    }

    @RequestMapping(value = "/terminate", method = RequestMethod.POST, consumes = "application/json")
    @Auth(role = RoleConstant.MANAGER)
    @ResponseBody
    public CommonResponse<Boolean> terminate(@RequestBody ProjectIdRequest request) {
        try {
            if (!projectService.terminate(request.getId(), getUserInfo().getUserId())) {
                return new CommonResponse<Boolean>(ResponseStatus.VALIDATE_ERROR);
            }
            return new CommonResponse<Boolean>(ResponseStatus.SUCCESS);
        } catch (Exception e) {
            log.error("terminate project error!", e);
        }
        return new CommonResponse<Boolean>(ResponseStatus.SYSTEM_ERROR);
    }

    @RequestMapping(value = "/completeHire", method = RequestMethod.POST, consumes = "application/json")
    @Auth(role = RoleConstant.MANAGER)
    @ResponseBody
    public CommonResponse<Boolean> completeHire(@RequestBody ProjectIdRequest request) {
        try {
            if (!projectService.completeHire(request.getId(), getUserInfo().getUserId())) {
                return new CommonResponse<Boolean>(ResponseStatus.VALIDATE_ERROR);
            }
            return new CommonResponse<Boolean>(ResponseStatus.SUCCESS);
        } catch (Exception e) {
            log.error("completeHire project error!", e);
        }
        return new CommonResponse<Boolean>(ResponseStatus.SYSTEM_ERROR);
    }

    @RequestMapping(value = "/start", method = RequestMethod.POST, consumes = "application/json")
    @Auth(role = RoleConstant.MANAGER)
    @ResponseBody
    public CommonResponse<Boolean> start(@RequestBody ProjectIdRequest request) {
        try {
            if (!projectService.start(request.getId(), getUserInfo().getUserId())) {
                return new CommonResponse<Boolean>(ResponseStatus.VALIDATE_ERROR);
            }
            return new CommonResponse<Boolean>(ResponseStatus.SUCCESS);
        } catch (Exception e) {
            log.error("start project error!", e);
        }
        return new CommonResponse<Boolean>(ResponseStatus.SYSTEM_ERROR);
    }

    @RequestMapping(value = "/end", method = RequestMethod.POST, consumes = "application/json")
    @Auth(role = RoleConstant.AGENT)
    @ResponseBody
    public CommonResponse<Boolean> end(@RequestBody ProjectIdRequest request) {
        try {
            if (!projectService.end(request.getId(), getUserInfo().getUserId())) {
                return new CommonResponse<Boolean>(ResponseStatus.VALIDATE_ERROR);
            }
            return new CommonResponse<Boolean>(ResponseStatus.SUCCESS);
        } catch (Exception e) {
            log.error("end project error!", e);
        }
        return new CommonResponse<Boolean>(ResponseStatus.SYSTEM_ERROR);
    }

    @RequestMapping(value = "/hire", method = RequestMethod.POST, consumes = "application/json")
    @Auth(role = RoleConstant.AGENT)
    @ResponseBody
    public CommonResponse<Boolean> hire(@RequestBody HireRequest request) {
        try {
            if (!projectService.hire(request.getId(), request.getUserid(), getUserInfo().getUserId())) {
                return new CommonResponse<Boolean>(ResponseStatus.VALIDATE_ERROR);
            }
            return new CommonResponse<Boolean>(ResponseStatus.SUCCESS);
        } catch (Exception e) {
            log.error("hire project error!", e);
        }
        return new CommonResponse<Boolean>(ResponseStatus.SYSTEM_ERROR);
    }

    @RequestMapping(value = "/acceptHiring", method = RequestMethod.POST, consumes = "application/json")
    @Auth(role = RoleConstant.DESIGNER)
    @ResponseBody
    public CommonResponse<Boolean> acceptHiring(@RequestBody AcceptHiringRequest request) {
        try {
            if (!projectService.acceptHiring(request.getHireid(), getUserInfo().getUserId(), request.isAccept())) {
                return new CommonResponse<Boolean>(ResponseStatus.VALIDATE_ERROR);
            }
            return new CommonResponse<Boolean>(ResponseStatus.SUCCESS);
        } catch (Exception e) {
            log.error("acceptHiring project error!", e);
        }
        return new CommonResponse<Boolean>(ResponseStatus.SYSTEM_ERROR);
    }


}
