package com.iblock.web.controller;

import com.iblock.common.advice.Auth;
import com.iblock.common.enums.Education;
import com.iblock.common.enums.UserRole;
import com.iblock.dao.po.JobInterest;
import com.iblock.dao.po.Manager;
import com.iblock.dao.po.Skill;
import com.iblock.dao.po.User;
import com.iblock.dao.po.UserGeo;
import com.iblock.dao.po.WorkExperience;
import com.iblock.service.bo.UserUpdateBo;
import com.iblock.service.experience.WorkExperienceService;
import com.iblock.service.file.FileService;
import com.iblock.service.interest.JobInterestService;
import com.iblock.service.user.UserService;
import com.iblock.web.constant.CommonProperties;
import com.iblock.web.constant.RoleConstant;
import com.iblock.web.enums.ResponseStatus;
import com.iblock.web.info.AddWorkExperienceInfo;
import com.iblock.web.info.GeoInfo;
import com.iblock.web.info.IdRequestInfo;
import com.iblock.web.info.JobInterestInfo;
import com.iblock.web.info.KVInfo;
import com.iblock.web.info.SkillInfo;
import com.iblock.web.info.UserDisplayInfo;
import com.iblock.web.info.UserInfo;
import com.iblock.web.info.UserUpdateInfo;
import com.iblock.web.info.WorkExperienceInfo;
import com.iblock.web.info.WorkExperienceResultInfo;
import com.iblock.web.request.user.LoginRequest;
import com.iblock.web.request.user.SendValidateCodeRequest;
import com.iblock.web.request.user.SignUpRequest;
import com.iblock.web.response.CommonResponse;
import com.iblock.web.session.RedisSessionFactory;
import lombok.extern.log4j.Log4j;
import nl.captcha.Captcha;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
    protected WorkExperienceService workExperienceService;

    @RequestMapping(value = "/login", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public CommonResponse<UserInfo> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        try {
            User user = userService.login(request.getUserName(), request.getPasswd());
            if (user != null) {
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
            User user = userService.getUser(getUserInfo().getUserId());
            if (!request.getPhone().equals(user.getMobile())) {
                return new CommonResponse<Boolean>(ResponseStatus.VALIDATE_ERROR);
            }
            // todo send code
            return new CommonResponse<Boolean>(true);
        } catch (Exception e) {
            log.error("sendValidateCode error!", e);
        }
        return new CommonResponse<Boolean>(ResponseStatus.SYSTEM_ERROR);
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public CommonResponse<Boolean> signUp(@RequestBody SignUpRequest request) {
        try {
            return new CommonResponse<Boolean>(userService.signUp(request.toUserBo()));
        } catch (Exception e) {
            log.error("sendValidateCode error!", e);
        }
        return new CommonResponse<Boolean>(ResponseStatus.SYSTEM_ERROR);
    }

    @RequestMapping(value = "/skills", method = RequestMethod.GET)
    @ResponseBody
    public CommonResponse<List<KVInfo>> skills() {
        try {
            List<KVInfo> result = new ArrayList<KVInfo>();
            for (Skill skill : userService.getSkills()) {
                result.add(new KVInfo(skill.getId(), skill.getName()));
            }
            return new CommonResponse<List<KVInfo>>(result);
        } catch (Exception e) {
            log.error("skills error!", e);
        }
        return new CommonResponse<List<KVInfo>>(ResponseStatus.SYSTEM_ERROR);
    }

    @RequestMapping(value = "/info/{userId}", method = RequestMethod.GET)
    @Auth(role = RoleConstant.LOGIN)
    @ResponseBody
    public CommonResponse<UserDisplayInfo> getUser(@PathVariable(value = "userId") Long userId) {
        try {
            UserDisplayInfo info = new UserDisplayInfo();
            User user = userService.getUser(userId);
            info.setUserId(userId);
            info.setUsername(user.getUserName());
            info.setAvatar(user.getHeadFigure());
            info.setRole(user.getRole().intValue());
            info.setEducation(new KVInfo(user.getEducation(), Education.getByCode(user.getEducation()).getMsg()));
            info.setRating(5);
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
            User user = userService.getUser(getUserInfo().getUserId());
            if (user == null) {
                return new CommonResponse<Boolean>(false, ResponseStatus.NOT_FOUND);
            }
            UserUpdateBo bo = new UserUpdateBo();
            if (info.getOnline() != null) {
                user.setOnline(info.getOnline());
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
                userGeo.setDistrict(info.getGeo().getDistrict().getName());
                userGeo.setDistrictId(info.getGeo().getDistrict().getId());
                userGeo.setCity(info.getGeo().getCity().getName());
                userGeo.setCityId(info.getGeo().getCity().getId());
                userGeo.setProvince(info.getGeo().getProvince().getName());
                userGeo.setProvinceId(info.getGeo().getProvince().getId());
                userGeo.setAddress(info.getGeo().getAddress());
                userGeo.setLongitude(info.getGeo().getLongitude());
                userGeo.setLatitude(info.getGeo().getLatitude());
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
            User user = userService.getUser(getUserInfo().getUserId());
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
            return new CommonResponse<JobInterestInfo>(JobInterestInfo.parse(interest));
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
            JobInterest interest = jobInterestService.get(getUserInfo().getUserId());
            if (interest == null) {
                interest = new JobInterest();
                interest.setUserId(getUserInfo().getUserId());
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
                list.add(WorkExperienceInfo.parse(experience));
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
            experience.setUserId(getUserInfo().getUserId());
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
            return new CommonResponse<Boolean>(workExperienceService.remove(info.getId(), getUserInfo().getUserId()));
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
            experience.setUserId(getUserInfo().getUserId());
            experience.setYear(info.getTime());
            experience.setDesc(info.getDesc());
            experience.setIndustry(info.getIndustry().getId());
            return new CommonResponse<Boolean>(workExperienceService.addOrUpdate(experience) > 0);
        } catch (Exception e) {
            log.error("updateWorkExperience error!", e);
        }
        return new CommonResponse<Boolean>(ResponseStatus.SYSTEM_ERROR);
    }
}
