package com.iblock.web.controller;

import com.iblock.common.advice.Auth;
import com.iblock.dao.po.JobInterest;
import com.iblock.service.interest.JobInterestService;
import com.iblock.web.constant.RoleConstant;
import com.iblock.web.enums.ResponseStatus;
import com.iblock.web.info.JobInterestInfo;
import com.iblock.web.response.CommonResponse;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;

/**
 * Created by baidu on 16/2/28.
 */
@Controller
@Log4j
@RequestMapping("/user")
public class JobInterestController extends BaseController {

    @Autowired
    private JobInterestService jobInterestService;

    @RequestMapping(value = "/getWorkPrefs/{userId}", method = RequestMethod.GET)
    @Auth(role = RoleConstant.DESIGNER)
    @ResponseBody
    public CommonResponse<JobInterestInfo> getWorkPrefs(@PathVariable(value="userId") Long userId) {
        try {
            JobInterest interest = jobInterestService.get(userId);
            if (interest == null) {
                return new CommonResponse<JobInterestInfo>(null, ResponseStatus.SUCCESS);
            }
            JobInterestInfo info = new JobInterestInfo();
            if (StringUtils.isNotBlank(interest.getCityList())) {
                info.setCity(Arrays.asList(interest.getCityList()));
            }
            if (StringUtils.isNotBlank(interest.getJobTypeList())) {
                info.setIndustry(Arrays.asList(interest.getJobTypeList()));
            }
            info.setIsLongTerm(interest.getResident());
            info.setMaxPay(interest.getEndPay());
            info.setMinPay(interest.getStartPay());
            return new CommonResponse<JobInterestInfo>(info);
        } catch (Exception e) {
            log.error("getWorkPrefs error!", e);
        }
        return new CommonResponse<JobInterestInfo>(ResponseStatus.SYSTEM_ERROR);
    }

    @RequestMapping(value = "/updateWorkPrefs", method = RequestMethod.POST, consumes = "application/json")
    @Auth(role = RoleConstant.DESIGNER)
    @ResponseBody
    public CommonResponse<Boolean> updateWorkPrefs(JobInterestInfo info) {
        try {
            JobInterest interest = jobInterestService.get(getUserInfo().getUserId());
            if (interest == null) {
                interest = new JobInterest();
            }
            StringBuffer cityList = new StringBuffer();
            for (String s : info.getCity()) {
                cityList.append(s).append(",");
            }
            interest.setCityList(cityList.substring(0, cityList.length() - 1));
            StringBuffer jobType = new StringBuffer();
            for (String s : info.getIndustry()) {
                jobType.append(s).append(",");
            }
            interest.setJobTypeList(jobType.substring(0, jobType.length() - 1));
            interest.setEndPay(info.getMaxPay());
            interest.setStartPay(info.getMinPay());
            interest.setResident(info.getIsLongTerm());
            return new CommonResponse<Boolean>(jobInterestService.addOrUpdate(interest));
        } catch (Exception e) {
            log.error("updateWorkPrefs error!", e);
        }
        return new CommonResponse<Boolean>(ResponseStatus.SYSTEM_ERROR);
    }
}
