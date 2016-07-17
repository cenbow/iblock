package com.iblock.web.controller;

import com.iblock.common.advice.Auth;
import com.iblock.dao.po.City;
import com.iblock.dao.po.District;
import com.iblock.dao.po.Industry;
import com.iblock.dao.po.Skill;
import com.iblock.service.meta.MetaService;
import com.iblock.service.user.UserService;
import com.iblock.web.constant.RoleConstant;
import com.iblock.web.enums.ResponseStatus;
import com.iblock.web.info.KVInfo;
import com.iblock.web.response.CommonResponse;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by baidu on 16/7/1.
 */
@Controller
@Log4j
@RequestMapping("/meta")
public class MetaController extends BaseController {

    @Autowired
    private MetaService metaService;

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/city/search", method = RequestMethod.GET)
    @ResponseBody
    public CommonResponse<List<KVInfo>> search(@RequestParam("q") String keyword) {
        try {
            List<City> list = metaService.getCities(new String(keyword.getBytes("ISO-8859-1"), "UTF-8"));
            List<KVInfo> result = new ArrayList<KVInfo>();
            if (CollectionUtils.isNotEmpty(list)) {
                for (City city : list) {
                    result.add(new KVInfo(city.getCityId(), city.getCityName()));
                }
            }
            return new CommonResponse<List<KVInfo>>(result);
        } catch (Exception e) {
            log.error("search city error!", e);
        }
        return new CommonResponse<List<KVInfo>>(ResponseStatus.SYSTEM_ERROR);
    }

    @RequestMapping(value = "/district/{cityId}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResponse<List<String>> getDistrict(@PathVariable("cityId") Integer cityId) {
        try {
            List<District> list = metaService.getDistricts(cityId);
            List<String> result = new ArrayList<String>();
            if (CollectionUtils.isNotEmpty(list)) {
                for (District district : list) {
                    result.add(district.getName());
                }
            }
            return new CommonResponse<List<String>>(result);
        } catch (Exception e) {
            log.error("search district error!", e);
        }
        return new CommonResponse<List<String>>(ResponseStatus.SYSTEM_ERROR);
    }

    @RequestMapping(value = "/skills/all", method = RequestMethod.GET)
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

    @RequestMapping(value = "/industry/all", method = RequestMethod.GET)
    @ResponseBody
    public CommonResponse<List<Industry>> allIndustries() {
        try {
            return new CommonResponse<List<Industry>>(userService.getIndustries());
        } catch (Exception e) {
            log.error("get industries error!", e);
        }
        return new CommonResponse<List<Industry>>(ResponseStatus.SYSTEM_ERROR);
    }
}


