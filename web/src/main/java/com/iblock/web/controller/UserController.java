package com.iblock.web.controller;

import com.iblock.common.advice.Auth;
import com.iblock.common.bean.Page;
import com.iblock.common.enums.Education;
import com.iblock.common.enums.ProjectStatus;
import com.iblock.common.enums.UserRole;
import com.iblock.common.enums.UserStatus;
import com.iblock.dao.po.City;
import com.iblock.dao.po.Industry;
import com.iblock.dao.po.JobInterest;
import com.iblock.dao.po.Manager;
import com.iblock.dao.po.ProjectSkill;
import com.iblock.dao.po.Skill;
import com.iblock.dao.po.User;
import com.iblock.dao.po.UserGeo;
import com.iblock.dao.po.WorkExperience;
import com.iblock.service.bo.UserUpdateBo;
import com.iblock.service.experience.WorkExperienceService;
import com.iblock.service.file.FileService;
import com.iblock.service.info.ProjectSimpleInfo;
import com.iblock.service.info.UserSearchInfo;
import com.iblock.service.interest.JobInterestService;
import com.iblock.service.message.MessageService;
import com.iblock.service.message.SMSService;
import com.iblock.service.meta.MetaService;
import com.iblock.service.project.ProjectService;
import com.iblock.service.search.ProjectCondition;
import com.iblock.service.search.UserCondition;
import com.iblock.service.user.UserService;
import com.iblock.web.constant.CommonProperties;
import com.iblock.web.constant.RoleConstant;
import com.iblock.web.enums.ResponseStatus;
import com.iblock.web.info.AddWorkExperienceInfo;
import com.iblock.web.info.AdminUserInfo;
import com.iblock.web.info.GeoInfo;
import com.iblock.web.info.IdRequestInfo;
import com.iblock.web.info.JobInterestInfo;
import com.iblock.service.info.KVInfo;
import com.iblock.web.info.SkillInfo;
import com.iblock.web.info.UserDisplayInfo;
import com.iblock.web.info.UserInfo;
import com.iblock.web.info.UserUpdateInfo;
import com.iblock.web.info.WorkExperienceInfo;
import com.iblock.web.info.WorkExperienceResultInfo;
import com.iblock.web.request.PageRequest;
import com.iblock.web.request.user.LoginRequest;
import com.iblock.web.request.user.SearchDesignerRequest;
import com.iblock.web.request.user.SendValidateCodeRequest;
import com.iblock.web.request.user.SignUpDesignerRequest;
import com.iblock.web.request.user.SignUpManagerRequest;
import com.iblock.web.request.user.UserRatingRequest;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by qihong on 16/1/25.
 */

@Controller
@Log4j
@RequestMapping("/user")
public class UserController extends BaseController {

    @Autowired
    protected UserService userService;

    @Autowired
    protected FileService fileService;

    @Autowired
    protected JobInterestService jobInterestService;

    @Autowired
    protected MessageService messageService;

    @Autowired
    protected WorkExperienceService workExperienceService;

    @Autowired
    protected SMSService smsService;
    @Autowired
    protected MetaService metaService;
    @Autowired
    protected ProjectService projectService;

    @RequestMapping(value = "/profilecomplete", method = RequestMethod.GET)
    @Auth
    @ResponseBody
    public CommonResponse<Boolean> profileComplete() {
        try {
            return new CommonResponse<Boolean>(userService.profileComplete(getUserInfo().getId()));
        } catch (Exception e) {
            log.error("profileComplete user error!", e);
        }
        return new CommonResponse<Boolean>(ResponseStatus.SYSTEM_ERROR);
    }

    @RequestMapping(value = "/recommended", method = RequestMethod.POST, consumes = "application/json")
    @Auth(role = RoleConstant.MANAGER)
    @ResponseBody
    public CommonResponse<Page<UserSearchInfo>> recommended(@RequestBody PageRequest request) {
        try {
            ProjectCondition condition = new ProjectCondition();
            condition.setManagerId(getUserInfo().getId());
            condition.setStatus(Arrays.asList(ProjectStatus.RECRUITING.getCode()));
            condition.setOffset(0);
            condition.setPageSize(100);
            List<ProjectSimpleInfo> list = projectService.search(condition).getResult();
            UserCondition uc = new UserCondition();
            uc.setPageSize(request.getPageSize());
            uc.setOffset((request.getPageNo() - 1) * request.getPageSize());
            uc.setStatus(Arrays.asList(UserStatus.NORMAL.getCode()));
            if (CollectionUtils.isNotEmpty(list)) {
                List<Integer> industries = new ArrayList<Integer>();
                List<Integer> cities = new ArrayList<Integer>();
                List<Long> ids = new ArrayList<Long>();
                for (ProjectSimpleInfo info : list) {
                    cities.add(info.getCity().getId());
                    industries.add(info.getIndustry().getId());
                    ids.add(info.getId());
                }
                uc.setCity(cities);
                uc.setIndustry(industries);
                List<ProjectSkill> skills = projectService.getSkills(ids);
                if (CollectionUtils.isNotEmpty(skills)) {
                    List<Integer> tmp = new ArrayList<Integer>();
                    for (ProjectSkill s : skills) {
                        tmp.add(s.getSkillId());
                    }
                    uc.setSkill(tmp);
                }
            }

            return new CommonResponse<Page<UserSearchInfo>>(userService.search(uc));
        } catch (Exception e) {
            log.error("user recommended error!", e);
        }
        return new CommonResponse<Page<UserSearchInfo>>(ResponseStatus.SYSTEM_ERROR);
    }

    @RequestMapping(value = "/search/designer", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public CommonResponse<Page<UserSearchInfo>> searchDesigner(@RequestBody SearchDesignerRequest request) {
        try {
            UserCondition condition = new UserCondition();
            condition.setMaxPay(request.getMaxPay());
            condition.setMinPay(request.getMinPay());
            condition.setPageSize(request.getPageSize());
            condition.setStatus(Arrays.asList(UserStatus.NORMAL.getCode()));
            condition.setOffset((request.getPageNo() - 1) * request.getPageSize());
            if (CollectionUtils.isNotEmpty(request.getCity())) {
                List<Integer> tmp = new ArrayList<Integer>();
                for (KVInfo info : request.getCity()) {
                    tmp.add(info.getId());
                }
                condition.setCity(tmp);
            }
            if (CollectionUtils.isNotEmpty(request.getSkill())) {
                List<Integer> tmp = new ArrayList<Integer>();
                for (KVInfo info : request.getSkill()) {
                    tmp.add(info.getId());
                }
                condition.setSkill(tmp);
            }
            if (CollectionUtils.isNotEmpty(request.getIndustry())) {
                List<Integer> tmp = new ArrayList<Integer>();
                for (KVInfo info : request.getIndustry()) {
                    tmp.add(info.getId());
                }
                condition.setIndustry(tmp);
            }
            if (StringUtils.isNotBlank(request.getKeyword())) {
                condition.setKeyword(request.getKeyword());
            }
            return new CommonResponse<Page<UserSearchInfo>>(userService.search(condition));
        } catch (Exception e) {
            log.error("user search error!", e);
        }
        return new CommonResponse<Page<UserSearchInfo>>(ResponseStatus.SYSTEM_ERROR);
    }

    @RequestMapping(value = "/rate", method = RequestMethod.POST, consumes = "application/json")
    @Auth
    @ResponseBody
    public CommonResponse<Void> rate(@RequestBody UserRatingRequest request) {
        try {
            if (userService.rate(request.getId(), request.getRating(), getUserInfo().getId(), request.getMsgId())) {
                return new CommonResponse<Void>(ResponseStatus.SUCCESS);
            } else {
                return new CommonResponse<Void>(ResponseStatus.PARAM_ERROR, "评分失败，项目未完结");
            }
        } catch (Exception e) {
            log.error("rate user error!", e);
        }
        return new CommonResponse<Void>(ResponseStatus.SYSTEM_ERROR);
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public CommonResponse<UserInfo> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        try {
            User user = userService.login(request.getMobile(), request.getPassword());
            if (user != null && user.getRole().intValue() != UserRole.ADMINISTRATOR.getRole()) {
                UserInfo info = new UserInfo(user);
                httpRequest.getSession().setAttribute(CommonProperties.USER_INFO, info);
                return new CommonResponse<UserInfo>(info);
            }
            return new CommonResponse<UserInfo>(ResponseStatus.NO_AUTH);
        } catch (Exception e) {
            log.error("login error!", e);
        }
        return new CommonResponse<UserInfo>(ResponseStatus.SYSTEM_ERROR);
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    @ResponseBody
    public CommonResponse<Boolean> logout() {
        try {
            session.setAttribute(CommonProperties.USER_INFO, null);
            return new CommonResponse<Boolean>(true);
        } catch (Exception e) {
            log.error("logout error!", e);
        }
        return new CommonResponse<Boolean>(ResponseStatus.SYSTEM_ERROR);
    }

    @RequestMapping(value = "/sendValidateCode", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public CommonResponse<Boolean> sendValidateCode(@RequestBody SendValidateCodeRequest request) {
        try {
            if (smsService.sendVerifyCode(request.getPhone())) {
                return new CommonResponse<Boolean>(true);
            }
        } catch (Exception e) {
            log.error("sendValidateCode error!", e);
        }
        return new CommonResponse<Boolean>(ResponseStatus.SYSTEM_ERROR);
    }

    @RequestMapping(value = "/signup/manager", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public CommonResponse<Boolean> signUpManager(@RequestBody SignUpManagerRequest request) {
        try {
            if (!smsService.checkVerifyCode(request.getMobile(), request.getVerifyCode())) {
                return new CommonResponse<Boolean>(ResponseStatus.PARAM_ERROR, "验证码校验失败");
            }
            if (userService.getByMobile(request.getMobile()) != null) {
                return new CommonResponse<Boolean>(ResponseStatus.PARAM_ERROR, "该手机已被注册");
            }
            return new CommonResponse<Boolean>(userService.signUp(request.toUserBo()));
        } catch (Exception e) {
            log.error("sendValidateCode error!", e);
        }
        return new CommonResponse<Boolean>(ResponseStatus.SYSTEM_ERROR);
    }

    @RequestMapping(value = "/signup/designer", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public CommonResponse<Boolean> signUpDesigner(@RequestBody SignUpDesignerRequest request) {
        try {
            if (!smsService.checkVerifyCode(request.getMobile(), request.getVerifyCode())) {
                return new CommonResponse<Boolean>(ResponseStatus.PARAM_ERROR, "验证码校验失败");
            }
            if (userService.getByMobile(request.getMobile()) != null) {
                return new CommonResponse<Boolean>(ResponseStatus.PARAM_ERROR, "该手机已被注册");
            }
            return new CommonResponse<Boolean>(userService.signUp(request.toUserBo()));
        } catch (Exception e) {
            log.error("sendValidateCode error!", e);
        }
        return new CommonResponse<Boolean>(ResponseStatus.SYSTEM_ERROR);
    }

    @RequestMapping(value = "/info/{userId}", method = RequestMethod.GET)
    @Auth(role = RoleConstant.LOGIN)
    @ResponseBody
    public CommonResponse<UserDisplayInfo> getUser(@PathVariable(value = "userId") Long userId) {
        try {
            UserDisplayInfo info = new UserDisplayInfo();
            User user = userService.getUser(userId);
            info.setUserId(userId);
            if (user.getSex() != null) {
                info.setGender(user.getSex() ? 1 : 2);
            }
            info.setUsername(user.getUserName());
            info.setAvatar(user.getHeadFigure());
            info.setRole(user.getRole().intValue());
            if (user.getEducation() != null) {
                info.setEducation(new KVInfo(user.getEducation(), Education.getByCode(user.getEducation()).getMsg()));
            }
            info.setRating(userService.getRating(userId));
            info.setContactPhone(user.getMobile());
            info.setOnline(user.getOnline());
            if (StringUtils.isNotBlank(user.getSkills())) {
                List<SkillInfo> skills = new ArrayList<SkillInfo>();
                for (Skill skill : userService.getSkillByIds(user.getSkills())) {
                    skills.add(new SkillInfo(skill.getId(), skill.getName()));
                }
                info.setSkills(skills);
            }
            UserGeo geo = userService.getUserGeo(userId);
            if (geo != null) {
                info.setGeo(GeoInfo.parse(userService.getUserGeo(userId)));
            }
            if (user.getRole().intValue() == UserRole.MANAGER.getRole()) {
                Manager manager = userService.getManager(userId);
                info.setCorporateBio(manager.getDesc());
                info.setCorporateName(manager.getCompanyName());
            }
            return new CommonResponse<UserDisplayInfo>(info);
        } catch (Exception e) {
            log.error("getUser error!", e);
        }
        return new CommonResponse<UserDisplayInfo>(ResponseStatus.SYSTEM_ERROR);
    }

    @RequestMapping(value = "/getUserFavicon/{userId}", method = RequestMethod.GET)
    @Auth
    public void getUserFavicon(@PathVariable(value = "userId") Long userId, HttpServletResponse response) {
        FileInputStream fis = null;
        try {
            User user = userService.getUser(userId);
            if (user == null && StringUtils.isBlank(user.getHeadFigure())) {
                return;
            }
            OutputStream out = response.getOutputStream();
            File file = new File(user.getHeadFigure());
            fis = new FileInputStream(file);
            byte[] b = new byte[fis.available()];
            fis.read(b);
            out.write(b);
            out.flush();
        } catch (Exception e) {
            log.error("getUserFavicon error!", e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (Exception e) {
                    log.error("close fis error", e);
                }
            }
        }
    }

    @RequestMapping(value = "/updateUserInfo", method = RequestMethod.POST)
    @Auth
    @ResponseBody
    public CommonResponse<Boolean> updateUserInfo(@RequestBody UserUpdateInfo info) {

        try {
            User user = userService.getUser(getUserInfo().getId());
            if (user == null) {
                return new CommonResponse<Boolean>(false, ResponseStatus.NOT_FOUND);
            }
            UserUpdateBo bo = new UserUpdateBo();
            if (info.getOnline() != null) {
                user.setOnline(info.getOnline());
            }
            if (info.getGender() != null) {
                if (info.getGender() == 1) {
                    user.setSex(true);
                }
                if (info.getGender() == 2) {
                    user.setSex(false);
                }
            }
            if (info.getEducation() != null) {
                user.setEducation((byte) info.getEducation().getId());
            }
            if (info.getSkills() != null) {
                StringBuffer skills = new StringBuffer();
                if (CollectionUtils.isNotEmpty(info.getSkills())) {
                    for (KVInfo kv : info.getSkills()) {
                        skills.append(kv.getId()).append(",");
                    }
                }
                user.setSkills(skills.toString());
            }
            bo.setUser(user);
            if (info.getGeo() != null) {
                UserGeo userGeo = userService.getUserGeo(user.getId());
                if (userGeo == null) {
                    userGeo = new UserGeo();
                    userGeo.setUserId(user.getId());
                }
                userGeo.setDistrict(info.getGeo().getDistrict());
                userGeo.setCity(info.getGeo().getCity().getName());
                userGeo.setCityId(info.getGeo().getCity().getId());
                userGeo.setAddress(info.getGeo().getAddress());
                userGeo.setLongitude(info.getGeo().getLng());
                userGeo.setLatitude(info.getGeo().getLat());
                bo.setUserGeo(userGeo);
            }

            if (info.getCorporateBio() != null || info.getCorporateName() != null) {
                Manager manager = userService.getManager(user.getId());
                if (manager == null) {
                    return new CommonResponse<Boolean>(false, ResponseStatus.NOT_FOUND);
                }
                if (info.getCorporateName() != null) {
                    manager.setCompanyName(info.getCorporateName());
                }
                if (info.getCorporateBio() != null) {
                    manager.setDesc(info.getCorporateBio());
                }
                bo.setManager(manager);
            }
            return new CommonResponse<Boolean>(userService.update(bo));
        } catch (Exception e) {
            log.error("updateUserInfo error!", e);
        }
        return new CommonResponse<Boolean>(ResponseStatus.SYSTEM_ERROR);
    }

    @RequestMapping(value = "/updateUserFavicon", method = RequestMethod.POST)
    @Auth
    @ResponseBody
    public CommonResponse<Boolean> updateUserFavicon(@RequestParam(value = "file") CommonsMultipartFile file) {

        try {
            User user = userService.getUser(getUserInfo().getId());
            if (user == null) {
                return new CommonResponse<Boolean>(false, ResponseStatus.NOT_FOUND);
            }
            String name = fileService.uploadFile(file);
            user.setHeadFigure(name);
            return new CommonResponse<Boolean>(userService.update(user));
        } catch (Exception e) {
            log.error("getUserFavicon error!", e);
        }
        return new CommonResponse<Boolean>(ResponseStatus.SYSTEM_ERROR);
    }

    @RequestMapping(value = "/getWorkPrefs/{userId}", method = RequestMethod.GET)
    @Auth
    @ResponseBody
    public CommonResponse<JobInterestInfo> getWorkPrefs(@PathVariable(value = "userId") Long userId) {

        try {
            JobInterest interest = jobInterestService.get(userId);
            if (interest == null) {
                return new CommonResponse<JobInterestInfo>(null, ResponseStatus.SUCCESS);
            }
            JobInterestInfo info = JobInterestInfo.parse(interest);
            List<KVInfo> cities = new ArrayList<KVInfo>();
            List<KVInfo> industries = new ArrayList<KVInfo>();
            if (StringUtils.isNotBlank(interest.getCityList())) {
                List<Integer> list = new ArrayList<Integer>();
                for (String s : interest.getCityList().split(",")) {
                    if (StringUtils.isNotBlank(s)) {
                        list.add(Integer.parseInt(s));
                    }
                }
                for (City city : metaService.getCity(list)) {
                    cities.add(new KVInfo(city.getCityId(), city.getCityName()));
                }

            }

            if (StringUtils.isNotBlank(interest.getJobTypeList())) {
                List<Integer> list = new ArrayList<Integer>();
                for (String s : interest.getJobTypeList().split(",")) {
                    if (StringUtils.isNotBlank(s)) {
                        list.add(Integer.parseInt(s));
                    }
                }
                for (Industry industry : metaService.getIndustry(list)) {
                    industries.add(new KVInfo(industry.getId(), industry.getName()));
                }

            }
            info.setCities(cities);
            info.setIndustries(industries);
            return new CommonResponse<JobInterestInfo>(info);
        } catch (Exception e) {
            log.error("getWorkPrefs error!", e);
        }
        return new CommonResponse<JobInterestInfo>(ResponseStatus.SYSTEM_ERROR);
    }

    @RequestMapping(value = "/updateWorkPrefs", method = RequestMethod.POST)
    @Auth(role = RoleConstant.DESIGNER)
    @ResponseBody
    public CommonResponse<Boolean> updateWorkPrefs(@RequestBody JobInterestInfo info) {

        try {
            JobInterest interest = jobInterestService.get(getUserInfo().getId());
            if (interest == null) {
                interest = new JobInterest();
                interest.setUserId(getUserInfo().getId());
            }
            if (info.getIsLongTerm() != null) {
                interest.setResident(info.getIsLongTerm());
            }
            if (info.getMaxPay() != null) {
                interest.setEndPay(info.getMaxPay());
            }
            if (info.getMinPay() != null) {
                interest.setStartPay(info.getMinPay());
            }
            if (info.getCities() != null) {
                StringBuffer sb = new StringBuffer();
                for (KVInfo kv : info.getCities()) {
                    sb.append(kv.getId()).append(",");
                }
                interest.setCityList(sb.toString());
            }
            if (info.getIndustries() != null) {
                StringBuffer sb = new StringBuffer();
                for (KVInfo kv : info.getIndustries()) {
                    sb.append(kv.getId()).append(",");
                }
                interest.setJobTypeList(sb.toString());
            }

            return new CommonResponse<Boolean>(jobInterestService.addOrUpdate(interest));
        } catch (Exception e) {
            log.error("updateWorkPrefs error!", e);
        }
        return new CommonResponse<Boolean>(ResponseStatus.SYSTEM_ERROR);
    }

    @RequestMapping(value = "/getWorkExperience/{userId}", method = RequestMethod.GET)
    @Auth
    @ResponseBody
    public CommonResponse<WorkExperienceResultInfo> getWorkExperience(@PathVariable(value = "userId") Long userId) {

        try {
            WorkExperienceResultInfo info = new WorkExperienceResultInfo();
            List<WorkExperience> experiences = workExperienceService.getByUser(userId);
            if (experiences == null) {
                info.setExperiences(new ArrayList<WorkExperienceInfo>());
                return new CommonResponse<WorkExperienceResultInfo>(info, ResponseStatus.SUCCESS);
            }
            List<WorkExperienceInfo> list = new ArrayList<WorkExperienceInfo>();
            for (WorkExperience experience : experiences) {
                WorkExperienceInfo i = WorkExperienceInfo.parse(experience);
                Industry industry = userService.getIndustryByIds(experience.getIndustry().toString()).get(0);
                i.setIndustry(new KVInfo(industry.getId(), industry.getName()));
                list.add(i);
            }
            info.setExperiences(list);
            return new CommonResponse<WorkExperienceResultInfo>(info);
        } catch (Exception e) {
            log.error("getWorkExperience error!", e);
        }
        return new CommonResponse<WorkExperienceResultInfo>(ResponseStatus.SYSTEM_ERROR);
    }

    @RequestMapping(value = "/addWorkExperience", method = RequestMethod.POST)
    @Auth(role = RoleConstant.DESIGNER)
    @ResponseBody
    public CommonResponse<Long> addWorkExperience(@RequestBody AddWorkExperienceInfo info) {

        try {
            WorkExperience experience = new WorkExperience();
            experience.setYear(info.getTime());
            experience.setDesc(info.getDesc());
            experience.setUserId(getUserInfo().getId());
            experience.setIndustry(info.getIndustry().getId());
            return new CommonResponse<Long>(workExperienceService.addOrUpdate(experience));
        } catch (Exception e) {
            log.error("addWorkExperience error!", e);
        }
        return new CommonResponse<Long>(ResponseStatus.SYSTEM_ERROR);
    }

    @RequestMapping(value = "/deleteWorkExperience", method = RequestMethod.POST)
    @Auth(role = RoleConstant.DESIGNER)
    @ResponseBody
    public CommonResponse<Boolean> deleteWorkExperience(@RequestBody IdRequestInfo info) {

        try {
            return new CommonResponse<Boolean>(workExperienceService.remove(info.getId(), getUserInfo().getId()));
        } catch (Exception e) {
            log.error("deleteWorkExperience error!", e);
        }
        return new CommonResponse<Boolean>(ResponseStatus.SYSTEM_ERROR);
    }

    @RequestMapping(value = "/updateWorkExperience", method = RequestMethod.POST)
    @Auth(role = RoleConstant.DESIGNER)
    @ResponseBody
    public CommonResponse<Boolean> updateWorkExperience(@RequestBody WorkExperienceInfo info) {
        try {
            WorkExperience experience = new WorkExperience();
            experience.setId(info.getId());
            experience.setUserId(getUserInfo().getId());
            experience.setYear(info.getTime());
            experience.setDesc(info.getDesc());
            experience.setIndustry(info.getIndustry().getId());
            return new CommonResponse<Boolean>(workExperienceService.addOrUpdate(experience) > 0);
        } catch (Exception e) {
            log.error("updateWorkExperience error!", e);
        }
        return new CommonResponse<Boolean>(ResponseStatus.SYSTEM_ERROR);
    }

    @RequestMapping(value = "/list/{role}", method = RequestMethod.GET)
    @Auth(role = RoleConstant.ADMINISTRATOR)
    @ResponseBody
    public CommonResponse<List<AdminUserInfo>> getRole(@PathVariable("role") Integer role) {
        try {
            List<User> users = userService.getUsersByRole(role);
            List<AdminUserInfo> result = new ArrayList<AdminUserInfo>();
            if (CollectionUtils.isNotEmpty(users)) {
                for (User user : users) {
                    AdminUserInfo info = new AdminUserInfo();
                    info.setId(user.getId());
                    info.setRole(user.getRole().byteValue());
                    info.setName(user.getUserName());
                    info.setMobile(user.getMobile());
                    result.add(info);
                }
            }
            return new CommonResponse<List<AdminUserInfo>>(result);
        } catch (Exception e) {
            log.error("admin list user error!", e);
        }
        return new CommonResponse<List<AdminUserInfo>>(ResponseStatus.SYSTEM_ERROR);
    }
}
