package com.iblock.web.controller;

import com.iblock.common.advice.Auth;
import com.iblock.dao.po.Achievement;
import com.iblock.service.achieve.AchievementService;
import com.iblock.web.constant.RoleConstant;
import com.iblock.web.enums.ResponseStatus;
import com.iblock.web.info.AchievementInfo;
import com.iblock.web.request.achieve.AchievementAddRequest;
import com.iblock.web.request.admin.BroadCastRequest;
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
import java.util.List;

/**
 * Created by baidu on 16/7/15.
 */
@Controller
@Log4j
@RequestMapping("/work")
public class AchievementController extends BaseController {

    @Autowired
    private AchievementService achievementService;

    @RequestMapping(value = "/add", method = RequestMethod.POST, consumes = "application/json")
    @Auth(role = RoleConstant.DESIGNER)
    @ResponseBody
    public CommonResponse<Boolean> add(@RequestBody AchievementAddRequest request) {
        try {
            Achievement achievement = new Achievement();
            achievement.setUserId(getUserInfo().getId());
            achievement.setDesc(request.getDesc());
            achievement.setName(request.getTitle());
            achievement.setDesc(request.getDesc());
            if (achievementService.add(achievement)) {
                return new CommonResponse<Boolean>(ResponseStatus.SUCCESS);
            }
        } catch (Exception e) {
            log.error("work add error!", e);
        }
        return new CommonResponse<Boolean>(ResponseStatus.SYSTEM_ERROR);
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    @Auth(role = RoleConstant.DESIGNER)
    @ResponseBody
    public CommonResponse<Boolean> delete(@PathVariable("id") Long id) {
        try {
            if (achievementService.delete(id, getUserInfo().getId())) {
                return new CommonResponse<Boolean>(ResponseStatus.SUCCESS);
            }
        } catch (Exception e) {
            log.error("work delete error!", e);
        }
        return new CommonResponse<Boolean>(ResponseStatus.SYSTEM_ERROR);
    }

    @RequestMapping(value = "/get/{userId}", method = RequestMethod.GET)
    @Auth
    @ResponseBody
    public CommonResponse<List<AchievementInfo>> get(@PathVariable("userId") Long userId) {
        try {
            List<AchievementInfo> result = new ArrayList<AchievementInfo>();
            List<Achievement> list = achievementService.getByUser(userId);
            if (CollectionUtils.isEmpty(list)) {
                return new CommonResponse<List<AchievementInfo>>(result);
            }
            for (Achievement achievement : list) {
                AchievementInfo info = new AchievementInfo();
                info.setId(achievement.getId());
                info.setTitle(achievement.getName());
                info.setDesc(achievement.getDesc());
                info.setImgUrl(achievement.getUrl());
                result.add(info);
            }
            return new CommonResponse<List<AchievementInfo>>(result);
        } catch (Exception e) {
            log.error("work add error!", e);
        }
        return new CommonResponse<List<AchievementInfo>>(ResponseStatus.SYSTEM_ERROR);
    }

}
