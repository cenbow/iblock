package com.iblock.web.controller;

import com.iblock.common.advice.Auth;
import com.iblock.common.bean.Page;
import com.iblock.common.bean.ProjectSearchBean;
import com.iblock.common.enums.ProjectStatus;
import com.iblock.dao.po.City;
import com.iblock.dao.po.Industry;
import com.iblock.dao.po.JobInterest;
import com.iblock.dao.po.Project;
import com.iblock.dao.po.ProjectSkill;
import com.iblock.dao.po.ProjectSkillDetail;
import com.iblock.dao.po.User;
import com.iblock.service.bo.ProjectAcceptBo;
import com.iblock.service.interest.JobInterestService;
import com.iblock.service.meta.MetaService;
import com.iblock.service.project.ProjectService;
import com.iblock.service.user.UserService;
import com.iblock.web.constant.RoleConstant;
import com.iblock.web.enums.ResponseStatus;
import com.iblock.web.info.GeoInfo;
import com.iblock.web.info.KVInfo;
import com.iblock.web.info.KVLongInfo;
import com.iblock.web.info.ProjectDetailInfo;
import com.iblock.web.info.ProjectSimpleInfo;
import com.iblock.web.info.UserSimpleInfo;
import com.iblock.web.request.PageRequest;
import com.iblock.web.request.project.AcceptHiringRequest;
import com.iblock.web.request.project.HireRequest;
import com.iblock.web.request.project.ProjectCreateRequest;
import com.iblock.web.request.project.ProjectIdRequest;
import com.iblock.web.request.project.ProjectSearchRequest;
import com.iblock.web.request.project.ProjectUpdateRequest;
import com.iblock.web.response.CommonResponse;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
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
    @Autowired
    private MetaService metaService;
    @Autowired
    private UserService userService;


    @RequestMapping(value = "/new", method = RequestMethod.POST, consumes = "application/json")
    @Auth(role = RoleConstant.MANAGER)
    @ResponseBody
    public CommonResponse<Long> save(@RequestBody ProjectCreateRequest project) {
        try {
            long id = projectService.save(project.toProject(), project.toSkills(), getUserInfo().getId());
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
            long id = projectService.save(p, project.toSkills(), getUserInfo().getId());
            if (id > 0) {
                return new CommonResponse<Long>(id);
            }
        } catch (Exception e) {
            log.error("save project error!", e);
        }
        return new CommonResponse<Long>(ResponseStatus.SYSTEM_ERROR);
    }

    @RequestMapping(value = "/details/{projectid}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResponse<ProjectDetailInfo> detail(@PathVariable("projectid") Long projectId) {
        try {
            Project p = projectService.get(projectId);
            if (p == null) {
                return new CommonResponse<ProjectDetailInfo>(ResponseStatus.NOT_FOUND);
            }
            ProjectDetailInfo info = ProjectDetailInfo.parse(p);
            List<ProjectSkillDetail> skills = projectService.getSkills(projectId);
            if (CollectionUtils.isNotEmpty(skills)) {
                List<KVInfo> list = new ArrayList<KVInfo>();
                for (ProjectSkillDetail skill : skills) {
                    list.add(new KVInfo(skill.getSkillId(), skill.getSkillName()));
                }
                info.setSkills(list);
            }

            City city = metaService.getCity(Arrays.asList(p.getCity())).get(0);
            GeoInfo geo = new GeoInfo();
            geo.setCity(new KVInfo(city.getCityId(), city.getCityName()));
            info.setGeo(geo);

            Industry industry = metaService.getIndustry(Arrays.asList(p.getIndustry())).get(0);
            info.setIndustry(new KVInfo(industry.getId(), industry.getName()));

            if (p.getManagerId() != null && !p.getManagerId().equals(0L)) {
                info.setManager(getUser(p.getManagerId()));
            }
            if (p.getAgentId() != null && !p.getAgentId().equals(0L)) {
                info.setBroker(getUser(p.getAgentId()));
            }
            List<User> designers = projectService.getDesigners(p.getId());
            if (CollectionUtils.isNotEmpty(designers)) {
                List<UserSimpleInfo> d = new ArrayList<UserSimpleInfo>();
                for (User user : designers) {
                    UserSimpleInfo u = new UserSimpleInfo();
                    u.setUsername(user.getUserName());
                    u.setAvatar(user.getHeadFigure());
                    u.setId(user.getId());
                    u.setRating(5);
                    d.add(u);
                }
                info.setDesigner(d);
            }
            return new CommonResponse<ProjectDetailInfo>(info);
        } catch (Exception e) {
            log.error("get project error!", e);
        }
        return new CommonResponse<ProjectDetailInfo>(ResponseStatus.SYSTEM_ERROR);
    }

    private UserSimpleInfo getUser(Long userId) {
        User user = userService.getUser(userId);
        UserSimpleInfo u = new UserSimpleInfo();
        if (user != null) {
            u.setUsername(user.getUserName());
            u.setAvatar(user.getHeadFigure());
            u.setId(user.getId());
            u.setRating(5);
        }
        return u;
    }

    @RequestMapping(value = "/recommended", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public CommonResponse<Page<Project>> recommended(@RequestBody PageRequest request) {
        try {
            JobInterest interest = jobInterestService.get(getUserInfo().getId());
            ProjectSearchBean bean = new ProjectSearchBean();
            bean.setPageSize(request.getPageSize());
            bean.setOffset((request.getPageNo() - 1) * request.getPageSize());
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
            bean.setOffset((request.getPageNo() - 1) * request.getPageSize());
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
    public CommonResponse<Page<ProjectSimpleInfo>> search(@RequestBody ProjectSearchRequest request) {
        try {
            Page<Project> page = projectService.search(request.toBean());
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
                    info.setManager(new KVLongInfo(p.getAgentId(), userMap.get(p.getAgentId())));
                }
                list.add(info);
            }
            Page<ProjectSimpleInfo> result = new Page<ProjectSimpleInfo>(list, page.getPageNo(), page.getPageSize(),
                    page.getTotalCount());

            return new CommonResponse<Page<ProjectSimpleInfo>>(result);
        } catch (Exception e) {
            log.error("search project error!", e);
        }
        return new CommonResponse<Page<ProjectSimpleInfo>>(ResponseStatus.SYSTEM_ERROR);
    }

    @RequestMapping(value = "/accept", method = RequestMethod.POST, consumes = "application/json")
    @Auth(role = RoleConstant.AGENT)
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
            if (!projectService.terminate(request.getId(), getUserInfo().getId())) {
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
            if (!projectService.completeHire(request.getId(), getUserInfo().getId())) {
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
            if (!projectService.start(request.getId(), getUserInfo().getId())) {
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
            if (!projectService.end(request.getId(), getUserInfo().getId())) {
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
            if (!projectService.hire(request.getId(), request.getUserid(), getUserInfo().getId())) {
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
            if (!projectService.acceptHiring(request.getHireid(), getUserInfo().getId(), request.isAccept())) {
                return new CommonResponse<Boolean>(ResponseStatus.VALIDATE_ERROR);
            }
            return new CommonResponse<Boolean>(ResponseStatus.SUCCESS);
        } catch (Exception e) {
            log.error("acceptHiring project error!", e);
        }
        return new CommonResponse<Boolean>(ResponseStatus.SYSTEM_ERROR);
    }


}
