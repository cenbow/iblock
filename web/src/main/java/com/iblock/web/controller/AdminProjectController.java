package com.iblock.web.controller;

import com.iblock.common.advice.Auth;
import com.iblock.common.bean.Page;
import com.iblock.common.bean.ProjectSearchBean;
import com.iblock.common.enums.ProjectStatus;
import com.iblock.common.enums.UserRole;
import com.iblock.dao.po.City;
import com.iblock.dao.po.Industry;
import com.iblock.dao.po.Project;
import com.iblock.dao.po.User;
import com.iblock.service.meta.MetaService;
import com.iblock.service.project.ProjectService;
import com.iblock.service.user.UserService;
import com.iblock.web.constant.RoleConstant;
import com.iblock.web.enums.ResponseStatus;
import com.iblock.web.info.KVInfo;
import com.iblock.web.info.KVLongInfo;
import com.iblock.web.info.ProjectSimpleInfo;
import com.iblock.web.request.PageRequest;
import com.iblock.web.request.admin.AddBrokerRequest;
import com.iblock.web.response.CommonResponse;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    @Autowired
    private MetaService metaService;

    @RequestMapping(value = "/allnew", method = RequestMethod.GET)
    @Auth(role = RoleConstant.ADMINISTRATOR)
    @ResponseBody
    public CommonResponse<Page<ProjectSimpleInfo>> search() {
        try {
            ProjectSearchBean bean = new ProjectSearchBean();
            bean.setPageSize(9999);
            bean.setOffset(0);
            bean.setStatus(Arrays.asList(ProjectStatus.AUDIT.getCode()));
            Page<Project> page = projectService.search(bean);

            List<Project> tmp = page.getResult();
            Map<Integer, String> cityMap = new HashMap<Integer, String>();
            Map<Integer, String> industryMap = new HashMap<Integer, String>();
            Map<Long, String> userMap = new HashMap<Long, String>();
            Set<Long> userIds = new HashSet<Long>();
            Set<Integer> cityIds = new HashSet<Integer>();
            Set<Integer> industryIds = new HashSet<Integer>();
            if (CollectionUtils.isNotEmpty(tmp)) {
                for (Project p : tmp) {
                    cityIds.add(p.getCity());
                    industryIds.add(p.getIndustry());
                    if (p.getManagerId() != null && !p.getManagerId().equals(0L)) {
                        userIds.add(p.getManagerId());
                    }
                    if (p.getAgentId() != null && !p.getAgentId().equals(0L)) {
                        userIds.add(p.getAgentId());
                    }
                }
                for (City city : metaService.getCity(new ArrayList<Integer>(cityIds))) {
                    cityMap.put(city.getCityId(), city.getCityName());
                }
                for (Industry industry : metaService.getIndustry(new ArrayList<Integer>(industryIds))) {
                    industryMap.put(industry.getId(), industry.getName());
                }

                for (User user : userService.batchGet(new ArrayList<Long>(userIds))) {
                    userMap.put(user.getId(), user.getUserName());
                }
            }
            List<ProjectSimpleInfo> list = new ArrayList<ProjectSimpleInfo>();
            for (Project p : tmp) {
                ProjectSimpleInfo info = ProjectSimpleInfo.parse(p);
                info.setCity(new KVInfo(p.getCity(), cityMap.get(p.getCity())));
                info.setIndustry(new KVInfo(p.getIndustry(), industryMap.get(p.getIndustry())));
                if (p.getManagerId() != null && !p.getManagerId().equals(0L)) {
                    info.setManager(new KVLongInfo(p.getManagerId(), userMap.get(p.getManagerId())));
                }
                if (p.getAgentId() != null && !p.getAgentId().equals(0L)) {
                    info.setBroker(new KVLongInfo(p.getAgentId(), userMap.get(p.getAgentId())));
                }
                list.add(info);
            }
            Page<ProjectSimpleInfo> result = new Page<ProjectSimpleInfo>(list, page.getPageNo(), page.getPageSize(),
                    page.getTotalCount());
            return new CommonResponse<Page<ProjectSimpleInfo>>(result);
        } catch (Exception e) {
            log.error("admin search project error!", e);
        }
        return new CommonResponse<Page<ProjectSimpleInfo>>(ResponseStatus.SYSTEM_ERROR);
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
