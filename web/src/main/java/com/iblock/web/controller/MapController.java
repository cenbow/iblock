package com.iblock.web.controller;

import com.iblock.dao.po.District;
import com.iblock.dao.po.UserDetail;
import com.iblock.dao.po.UserGeo;
import com.iblock.service.map.MapService;
import com.iblock.service.user.UserGeoService;
import com.iblock.service.user.UserService;
import com.iblock.web.enums.ResponseStatus;
import com.iblock.web.request.user.MapUsersRequest;
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
 * Created by baidu on 16/2/3.
 */
@Controller
@Log4j
@RequestMapping("/map")
public class MapController extends BaseController {

    @Autowired
    private MapService mapService;
    @Autowired
    private UserGeoService userGeoService;
    @Autowired
    private UserService userService;

    @RequestMapping(value = "/district/{cityId}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResponse<List<District>> getDistricts(@PathVariable("cityId") int cityId) {
        try {
            return new CommonResponse<List<District>>(mapService.getDistrict(cityId));
        } catch (Exception e) {
            log.error("get district error", e);
        }
        return new CommonResponse<List<District>>(ResponseStatus.SYSTEM_ERROR);
    }

    @RequestMapping(value = "/getUsers", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public CommonResponse<List<UserGeo>> getUsers(@RequestBody MapUsersRequest request) {
        try {
            return new CommonResponse<List<UserGeo>>(userGeoService.getUserGeo(request.getMaxLat(), request.getMinLat
                    (), request.getMaxLon(), request.getMinLon()));
        } catch (Exception e) {
            log.error("get map users error", e);
        }
        return new CommonResponse<List<UserGeo>>(ResponseStatus.SYSTEM_ERROR);
    }

    @RequestMapping(value = "/getUserDetail/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResponse<UserDetail> getUsers(@PathVariable("id") Long userId) {
        try {
            return new CommonResponse<UserDetail>(userService.getUserDetail(userId));
        } catch (Exception e) {
            log.error("get map user detail error", e);
        }
        return new CommonResponse<UserDetail>(ResponseStatus.SYSTEM_ERROR);
    }
}
