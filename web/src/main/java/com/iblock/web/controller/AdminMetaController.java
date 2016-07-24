package com.iblock.web.controller;

import com.iblock.common.advice.Auth;
import com.iblock.common.bean.Page;
import com.iblock.common.bean.ProjectSearchBean;
import com.iblock.dao.po.Industry;
import com.iblock.dao.po.Project;
import com.iblock.dao.po.Skill;
import com.iblock.service.meta.MetaService;
import com.iblock.service.user.UserService;
import com.iblock.web.constant.RoleConstant;
import com.iblock.web.enums.ResponseStatus;
import com.iblock.web.info.KVInfo;
import com.iblock.web.request.IntIdRequest;
import com.iblock.web.request.PageRequest;
import com.iblock.web.request.admin.BroadCastRequest;
import com.iblock.web.request.admin.NameRequest;
import com.iblock.web.response.CommonResponse;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by baidu on 16/6/26.
 */

@Controller
@Log4j
@RequestMapping("/admin/meta")
public class AdminMetaController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private MetaService metaService;

    @RequestMapping(value = "/all/skill", method = RequestMethod.GET)
    @Auth(role = RoleConstant.ADMINISTRATOR)
    @ResponseBody
    public CommonResponse<List<Skill>> allSkills() {
        try {
            return new CommonResponse<List<Skill>>(userService.getSkills());
        } catch (Exception e) {
            log.error("admin search skills error!", e);
        }
        return new CommonResponse<List<Skill>>(ResponseStatus.SYSTEM_ERROR);
    }

    @RequestMapping(value = "/skill/delete", method = RequestMethod.POST, consumes = "application/json")
    @Auth(role = RoleConstant.ADMINISTRATOR)
    @ResponseBody
    public CommonResponse<Void> deleteSkill(IntIdRequest request) {
        try {
            if (metaService.deleteSkill(request.getId())) {
            return new CommonResponse<Void>(ResponseStatus.SUCCESS);
            }
        } catch (Exception e) {
            log.error("admin delete skill error!", e);
        }
        return new CommonResponse<Void>(ResponseStatus.SYSTEM_ERROR);
    }

    @RequestMapping(value = "/industry/delete", method = RequestMethod.POST, consumes = "application/json")
    @Auth(role = RoleConstant.ADMINISTRATOR)
    @ResponseBody
    public CommonResponse<Void> deleteIndustry(IntIdRequest request) {
        try {
            if (metaService.deleteIndustry(request.getId())) {
                return new CommonResponse<Void>(ResponseStatus.SUCCESS);
            }
        } catch (Exception e) {
            log.error("admin delete industry error!", e);
        }
        return new CommonResponse<Void>(ResponseStatus.SYSTEM_ERROR);
    }

    @RequestMapping(value = "/all/industry", method = RequestMethod.GET)
    @Auth(role = RoleConstant.ADMINISTRATOR)
    @ResponseBody
    public CommonResponse<List<Industry>> allIndustries() {
        try {
            return new CommonResponse<List<Industry>>(userService.getIndustries());
        } catch (Exception e) {
            log.error("admin search industries error!", e);
        }
        return new CommonResponse<List<Industry>>(ResponseStatus.SYSTEM_ERROR);
    }

    @RequestMapping(value = "/industry/add", method = RequestMethod.POST, consumes = "application/json")
    @Auth(role = RoleConstant.ADMINISTRATOR)
    @ResponseBody
    public CommonResponse<Boolean> addIndustry(@RequestBody NameRequest request) {
        try {
            if (userService.addIndustry(request.getName())) {
                return new CommonResponse<Boolean>(ResponseStatus.SUCCESS);
            }
        } catch (Exception e) {
            log.error("admin add industry error!", e);
        }
        return new CommonResponse<Boolean>(ResponseStatus.SYSTEM_ERROR);
    }

    @RequestMapping(value = "/skill/add", method = RequestMethod.POST, consumes = "application/json")
    @Auth(role = RoleConstant.ADMINISTRATOR)
    @ResponseBody
    public CommonResponse<Boolean> addSkill(@RequestBody NameRequest request) {
        try {
            if (userService.addSkill(request.getName())) {
                return new CommonResponse<Boolean>(ResponseStatus.SUCCESS);
            }
        } catch (Exception e) {
            log.error("admin add skill error!", e);
        }
        return new CommonResponse<Boolean>(ResponseStatus.SYSTEM_ERROR);
    }

    @RequestMapping(value = "/industry/delete/{id}", method = RequestMethod.GET)
    @Auth(role = RoleConstant.ADMINISTRATOR)
    @ResponseBody
    public CommonResponse<Boolean> deleteIndustry(@PathVariable("id") Integer id) {
        try {
            if (userService.deleteIndustry(id)) {
                return new CommonResponse<Boolean>(ResponseStatus.SUCCESS);
            }
        } catch (Exception e) {
            log.error("admin delete industry error!", e);
        }
        return new CommonResponse<Boolean>(ResponseStatus.SYSTEM_ERROR);
    }

    @RequestMapping(value = "/skill/delete/{id}", method = RequestMethod.GET)
    @Auth(role = RoleConstant.ADMINISTRATOR)
    @ResponseBody
    public CommonResponse<Boolean> deleteSkill(@PathVariable("id") Integer id) {
        try {
            if (userService.deleteSkill(id)) {
                return new CommonResponse<Boolean>(ResponseStatus.SUCCESS);
            }
        } catch (Exception e) {
            log.error("admin delete skill error!", e);
        }
        return new CommonResponse<Boolean>(ResponseStatus.SYSTEM_ERROR);
    }


}
