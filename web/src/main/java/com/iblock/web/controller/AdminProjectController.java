package com.iblock.web.controller;

import com.iblock.common.advice.Auth;
import com.iblock.common.bean.Page;
import com.iblock.common.bean.ProjectSearchBean;
import com.iblock.common.enums.ProjectStatus;
import com.iblock.common.enums.UserRole;
import com.iblock.dao.po.Project;
import com.iblock.dao.po.User;
import com.iblock.service.project.ProjectService;
import com.iblock.service.user.UserService;
import com.iblock.web.constant.RoleConstant;
import com.iblock.web.enums.ResponseStatus;
import com.iblock.web.request.PageRequest;
import com.iblock.web.request.admin.AddBrokerRequest;
import com.iblock.web.response.CommonResponse;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by baidu on 16/6/26.
 */

@Controller
@Log4j
@RequestMapping("/admin/project")
public class AdminProjectController extends BaseController {

    @Autowired
    private ProjectService projectService;
    @Autowired
    private UserService userService;

    @RequestMapping(value = "/allnew", method = RequestMethod.GET)
    @Auth(role = RoleConstant.ADMINISTRATOR)
    @ResponseBody
    public CommonResponse<Page<Project>> search() {
        try {
            ProjectSearchBean bean = new ProjectSearchBean();
            bean.setPageSize(9999);
            bean.setOffset(0);
            bean.setStatus(ProjectStatus.AUDIT.getCode());
            return new CommonResponse<Page<Project>>(projectService.search(bean));
        } catch (Exception e) {
            log.error("admin search project error!", e);
        }
        return new CommonResponse<Page<Project>>(ResponseStatus.SYSTEM_ERROR);
    }

    @RequestMapping(value = "/addbroker", method = RequestMethod.POST, consumes = "application/json")
    @Auth(role = RoleConstant.ADMINISTRATOR)
    @ResponseBody
    public CommonResponse<Boolean> addBroker(@RequestBody AddBrokerRequest request) {
        try {
            User user = userService.getUser(request.getBroker());
            if (user == null || user.getRole().intValue() != UserRole.AGENT.getRole()) {
                return new CommonResponse<Boolean>(ResponseStatus.PARAM_ERROR, "经纪人不存在");
            }
            Project project = projectService.get(request.getId());
            if (project == null || project.getStatus().intValue() != ProjectStatus.AUDIT.getCode()) {
                return new CommonResponse<Boolean>(ResponseStatus.PARAM_ERROR, "不存在项目或者项目状态不正确");
            }
            project.setStatus((byte) ProjectStatus.RECRUITING.getCode());
            project.setAgentId(request.getBroker());
            if (projectService.update(project)) {
                return new CommonResponse<Boolean>(ResponseStatus.SUCCESS);
            }
        } catch (Exception e) {
            log.error("admin addBroker error!", e);
        }
        return new CommonResponse<Boolean>(ResponseStatus.SYSTEM_ERROR);
    }

    @RequestMapping(value = "/allfreezed", method = RequestMethod.POST, consumes = "application/json")
    @Auth(role = RoleConstant.ADMINISTRATOR)
    @ResponseBody
    public CommonResponse<Page<Project>> allFreezed(@RequestBody PageRequest request) {
        try {
            ProjectSearchBean bean = new ProjectSearchBean();
            bean.setPageSize(request.getPageSize());
            bean.setOffset((request.getPageNo() - 1) * request.getPageSize());
            bean.setFreeze(true);
            return new CommonResponse<Page<Project>>(projectService.search(bean));
        } catch (Exception e) {
            log.error("admin allfreezed project error!", e);
        }
        return new CommonResponse<Page<Project>>(ResponseStatus.SYSTEM_ERROR);
    }

    @RequestMapping(value = "/freeze/{id}", method = RequestMethod.GET)
    @Auth(role = RoleConstant.ADMINISTRATOR)
    @ResponseBody
    public CommonResponse<Boolean> freeze(@PathVariable("id") Long id) {
        try {
            Project p = projectService.get(id);
            if (p == null) {
                return new CommonResponse<Boolean>(ResponseStatus.PARAM_ERROR, "项目不存在");
            }
            if (p.getFreeze()) {
                return new CommonResponse<Boolean>(ResponseStatus.PARAM_ERROR, "项目已冻结");
            }
            p.setFreeze(true);
            if (projectService.update(p)) {
                return new CommonResponse<Boolean>(ResponseStatus.SUCCESS);
            }
        } catch (Exception e) {
            log.error("admin freeze project error!", e);
        }
        return new CommonResponse<Boolean>(ResponseStatus.SYSTEM_ERROR);
    }

    @RequestMapping(value = "/unfreeze/{id}", method = RequestMethod.GET)
    @Auth(role = RoleConstant.ADMINISTRATOR)
    @ResponseBody
    public CommonResponse<Boolean> unfreeze(@PathVariable("id") Long id) {
        try {
            Project p = projectService.get(id);
            if (p == null) {
                return new CommonResponse<Boolean>(ResponseStatus.PARAM_ERROR, "项目不存在");
            }
            if (!p.getFreeze()) {
                return new CommonResponse<Boolean>(ResponseStatus.PARAM_ERROR, "项目未冻结");
            }
            p.setFreeze(false);
            if (projectService.update(p)) {
                return new CommonResponse<Boolean>(ResponseStatus.SUCCESS);
            }
        } catch (Exception e) {
            log.error("admin unfreeze project error!", e);
        }
        return new CommonResponse<Boolean>(ResponseStatus.SYSTEM_ERROR);
    }
}
